-- Pitt Tours Database
-- Jonathan Hanobik and Seth Stayer
-- CS1555, Fall 2016

--Drop Tables Before Beginning
DROP TABLE Airline CASCADE CONSTRAINTS;
DROP TABLE Flight CASCADE CONSTRAINTS;
DROP TABLE Plane CASCADE CONSTRAINTS;
DROP TABLE Price CASCADE CONSTRAINTS;
DROP TABLE Customer CASCADE CONSTRAINTS;
DROP TABLE Reservation CASCADE CONSTRAINTS;
DROP TABLE Reservation_Detail CASCADE CONSTRAINTS;
DROP TABLE Our_Date CASCADE CONSTRAINTS;

-- Airline
-- ASSUMPTIONS:
--		1) 
CREATE TABLE Airline (
	airline_id VARCHAR(5),
	airline_name VARCHAR(50),
	airline_abbreviation VARCHAR(20),
	year_founded int,
	CONSTRAINT PK_Airline PRIMARY KEY(airline_id),
);

-- Flight Schedule
-- ASSUMPTIONS:
-- 		1) There are no delays or early departures for any flights
--		2) Every flight is at least offered on some day of the week
CREATE TABLE Flight (
	flight_number VARCHAR(3),
	plane_type CHAR(4),
	departure_city VARCHAR(3),
	arrival_city VARCHAR(3),
	departure_time VARCHAR(4),
	arrival_time VARCHAR(4),
	weekly_schedule VARCHAR(7),
	CONSTRAINT PK_Flight PRIMARY KEY(flight_number),
	CONSTRAINT FK1_Flight FOREIGN KEY(plane_type) REFERENCES Plane(plane_type) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT FK2_Flight FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT cityCheckFlight CHECK (departure_city <> arrival_city),
	CONSTRAINT scheduleCheck CHECK (weekly_schedule <> '-------'),
	CONSTRAINT timeCheck CHECK((departure_time BETWEEN '00:00' AND '23:59') AND (arrival_time BETWEEN '00:00' AND '23:59')	
);

-- Plane
-- ASSUMPTIONS:
-- 1) Each plane has a capacity of at least 0
-- 2) Each plane has been serviced after its birth year		
CREATE TABLE Plane (
	plane_type CHAR(4),
	manufacture VARCHAR(10),
	plane_capacity INT,
	last_service DATE,
	year INT,
	owner_id VARCHAR(5),
	CONSTRAINT PK_Plane PRIMARY KEY(plane_type, owner_id),
	CONSTRAINT FK_Plane FOREIGN KEY(owner_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT capacityCheck CHECK (plane_capacity > 0),
	CONSTRAINT yearCheck CHECK (CAST(to_char(last_service, YYYY) AS INT) >= year)
);

-- Flight Pricing
-- ASSUMPTIONS:
--		
CREATE TABLE Price (
	departure_city varchar(3),
	arrival_city varchar(3),
	airline_id varchar(5),
	high_price int,
	low_price int,
	CONSTRAINT Pk_Price PRIMARY KEY(departure_city, arrival_city, airline_id),
	CONSTRAINT Fk_Price FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT cityCheckPrice CHECK (departure_city <> arrival_city),
	CONSTRAINT priceCheck CHECK (high_price > low_price)
);

-- Customer
-- ASSUMPTIONS:
--		1) Two people can have the same name, but will have different cids
--		2) A customer's credit card's expiration date must be later than the current year
CREATE TABLE Customer (
	cid varchar(9),
	salutation varchar(3),
	first_name varchar(30),
	last_name varchar(30),
	frequent_miles varchar(5),
	credit_card_num varchar(16),
	credit_card_expire date,
	street varchar(30),
	city varchar(30),
	state varchar(2),
	phone varchar(10),
	email varchar(30),
	CONSTRAINT Pk_Customer PRIMARY KEY(cid),
	CONSTRAINT validCC CHECK CAST(to_char(credit_card_expire, YYYY) AS INT) >= 2016
);

-- Reservation Information
-- ASSUMPTIONS:
--		1) Each customer uses their preferred credit card, the one provided in Customer
--		2) ReservationDate must be after the current date
CREATE TABLE Reservation (
	reservation_number varchar(5),
	cid varchar(9),
	start_city char(3),
	end_city char(3),
	cost int,
	credit_card_num varchar(16),
	reservation_date date,
	ticketed varchar(1),
	CONSTRAINT Pk_Reservation PRIMARY KEY(reservation_number),
	CONSTRAINT Fk_Reservation FOREIGN KEY(cid) REFERENCES Customer(cid) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT Fk_Reservation FOREIGN KEY(credit_card_num) REFERENCES Customer(credit_card_num) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT customerCCCheck CHECK (credit_card_num = (SELECT credit_card_num FROM Customer C WHERE C.cid = cid)),
	CONSTRAINT reserveDateCheck CHECK (reservation_date >= sysdate)
);

-- Reservation Detail
-- ASSUMPTIONS:
--		1) Each Reservation Has At Least One Leg
CREATE TABLE Reservation_Detail (
	reservation_number varchar(5),
	flight_number varchar(3),
	flight_date date,
	leg int,
	CONSTRAINT Pk_Reservation_Detail PRIMARY KEY(reservation_number, leg),
	CONSTRAINT Fk1_Reservation_Detail FOREIGN KEY(reservation_number) REFERENCES Reservation(reservation_number) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT Fk2_Reservation_Detail FOREIGN KEY(flight_number) REFERENCES Flight(flight_number) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT legCheck CHECK (leg > 0)
);

-- Our Time
-- ASSUMPTIONS:
-- 		1) Time is store as 4 digits without a colon, just like in Flight
CREATE TABLE Our_Date (
	c_date date,
	CONSTRAINT Pk_Date PRIMARY KEY(c_date)
);

--Trigger 1
--Changed Requirements Left Us Confused On How To Implement This Trigger With Regards to Airline ID and Roundtrips
CREATE OR REPLACE TRIGGER adjustTicket 
AFTER UPDATE ON Price
REFERENCING NEW AS NEW_PRICE
FOR EACH ROW
	WHEN NEW_PRICE.price <> old.price
BEGIN
	UPDATE Reservation R
	SET R.cost = NEW_PRICE.cost
	WHERE (NEW_PRICE.departure_city = R.start_city) AND (NEW_PRICE.arrival_city = R.end_city) AND (R.Ticketed = 'N');
END;
/

--Trigger 2
CREATE OR REPLACE TRIGGER planeUpgrade
AFTER INSERT OR UPDATE ON Reservation_Detail
DECLARE curr_capacity INT;
DECLARE max_capacity INT;
DECLARE new_type CHAR(4);
BEGIN
	SELECT COUNT(*) INTO curr_capacity
	FROM Reservation_Detail
	GROUP BY flight_number
	WHERE :new.flight_number = flight_number;
	
	SELECT capacity INTO max_capacity
	FROM (Flight NATURAL JOIN Plane) W
	WHERE :new.flight_number = W.flight_number;
	
	IF curr_capacity > max_capacity
	THEN {
		SELECT plane_type INTO new_type
		FROM Plane P
		WHERE (P.capacity > curr_capacity) AND (P.owner_id = (SELECT airline_id 
																FROM Flight NATURAL JOIN Reservation_Detail 
																WHERE :new.flight_number = Flight.flight_number));
		
		UPDATE Flight	
		SET plane_type = new_type
		WHERE :new.flight_number = flight_number;
	}
	END IF;
END;
/

--Trigger 3
CREATE OR REPLACE TRIGGER cancelReservation
AFTER UPDATE ON Our_Date
WHEN(to_char((SELECT * FROM Our_Date) + INTERVAL '12' HH24, HH24MI) IN (SELECT departure_time FROM Flight))
DECLARE cancel_time CHAR(4);
DECLARE curr_capacity INT;
DECLARE curr_flightNum VARCHAR(3);
DECLARE low_capacity INT;
DECLARE new_type CHAR(4);
BEGIN
	cancel_time := to_char((SELECT * FROM Our_Date) + INTERVAL '12' HH24, HH24MI);
	DELETE FROM Reservation
	WHERE Ticketed == 'N' AND reservation_number == (SELECT reservation_number FROM Reservation_Detail WHERE leg == 0 AND cancel_time == departure_time);
	-- Fit Into Smaller Plane
	SELECT COUNT(*) INTO curr_capacity, flight_number INTO curr_flightNum
	FROM Reservation_Detail
	GROUP BY flight_number
	WHERE cancel_time == (SELECT departure_time FROM Flight F);
	
	SELECT capacity INTO low_capacity
	FROM Plane P
	WHERE curr_capacity > P.capacity;
	
	SELECT plane_type INTO new_type
	FROM Plane P
	WHERE (low_capacity = capacity) AND (P.owner_id = (SELECT airline_id 
																FROM Flight NATURAL JOIN Reservation_Detail 
																WHERE curr_flightNum = Flight.flight_number));
	
	UPDATE Flight
	SET plane_type = new_type
	WHERE flight_number == curr_flightNum;
	
END;
/