-- Pitt Tours Database
-- Jonathan Hanobik and Seth Stayer
-- CS1555, Fall 2016

-- Airline
-- ASSUMPTIONS:
--		1) 
CREATE OR REPLACE TABLE Airline (
	airline_id VARCHAR(5),
	airline_name VARCHAR(50),
	airline_abbreviation VARCHAR(20),
	year_founded int,
	CONSTRAINT PK_Airline PRIMARY KEY(airline_id) IMMEDIATE,
);

-- Flight Schedule
-- ASSUMPTIONS:
-- 		1) There are no delays or early departures for any flights
--		2) Every flight is at least offered on some day of the week
CREATE OR REPLACE TABLE Flight (
	flight_number VARCHAR(3),
	plane_type CHAR(4),
	departure_city VARCHAR(3),
	arrival_city VARCHAR(3),
	departure_time VARCHAR(4),
	arrival_time VARCHAR(4),
	weekly_schedule VARCHAR(7),
	CONSTRAINT PK_Flight PRIMARY KEY(flight_number) IMMEDIATE,
	CONSTRAINT FK1_Flight FOREIGN KEY(plane_type) REFERENCES Plane(plane_type) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT FK2_Flight FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT cityCheckFlight CHECK departure_city <> arrival_city IMMEDIATE,
	CONSTRAINT scheduleCheck CHECK weekly_schedule <> '-------' IMMEDIATE,
	CONSTRAINT timeCheck CHECK((departure_time BETWEEN '00:00' AND '23:59') AND (arrival_time BETWEEN '00:00' AND '23:59') IMMEDIATE	
);

-- Plane
-- ASSUMPTIONS:
-- 1) Each plane has a capacity of at least 0
-- 2) Each plane has been serviced after its birth year		
CREATE OR REPLACE TABLE Plane (
	plane_type CHAR(4),
	manufacture VARCHAR(10),
	plane_capacity INT,
	last_service DATE,
	year INT,
	owner_id VARCHAR(5),
	CONSTRAINT PK_Plane PRIMARY KEY(plane_type, owner_id) IMMEDIATE,
	CONSTRAINT FK_Plane FOREIGN KEY(owner_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT capacityCheck CHECK (plane_capacity > 0) IMMEDIATE,
	CONSTRAINT yearCheck CHECK (CAST(to_char(last_service, YYYY) AS INT) >= year) IMMEDIATE
);

-- Flight Pricing
-- ASSUMPTIONS:
--		
CREATE OR REPLACE TABLE Price (
	departure_city varchar(3),
	arrival_city varchar(3),
	airline_id varchar(5),
	high_price int,
	low_price int,
	CONSTRAINT Pk_Price PRIMARY KEY(departure_city, arrival_city, airline_id) IMMEDIATE,
	CONSTRAINT Fk_Price FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT cityCheckPrice CHECK departure_city <> arrival_city IMMEDIATE,
	CONSTRAINT priceCheck CHECK high_price > low_price IMMEDIATE
);

-- Customer
-- ASSUMPTIONS:
--		1) Two people can have the same name, but will have different cids
--		2) A customer's credit card's expiration date must be later than the current year
CREATE OR REPLACE TABLE Customer (
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
	CONSTRAINT Pk_Customer PRIMARY KEY(cid) IMMEDIATE,
	CONSTRAINT validCC CHECK CAST(to_char(credit_card_expire, YYYY) AS INT) > 2016 IMMEDIATE
);

-- Reservation Information
-- ASSUMPTIONS:
--		1) Each customer uses their preferred credit card, the one provided in Customer
--		2) ReservationDate must be after the current date
CREATE OR REPLACE TABLE Reservation (
	reservation_number varchar(5),
	cid varchar(9),
	start_city char(3),
	end_city char(3),
	cost int,
	credit_card_num varchar(16),
	reservation_date date,
	ticketed varchar(1),
	CONSTRAINT Pk_Reservation PRIMARY KEY(reservation_number) IMMEDIATE,
	CONSTRAINT Fk_Reservation FOREIGN KEY(cid) REFERENCES Customer(cid) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT Fk_Reservation FOREIGN KEY(credit_card_num) REFERENCES Customer(credit_card_num) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT customerCCCheck CHECK credit_card_num = (SELECT credit_card_num FROM Customer C WHERE C.cid = cid) IMMEDIATE,
	CONSTRAINT reserveDateCheck CHECK reservation_date >= sysdate IMMEDIATE
);

-- Reservation Detail
-- ASSUMPTIONS:
--		1) Each Reservation Has At Least One Leg
CREATE OR REPLACE TABLE Reservation_Detail (
	reservation_number varchar(5),
	flight_number varchar(3),
	flight_date date,
	leg int,
	CONSTRAINT Pk_Reservation_Detail PRIMARY KEY(reservation_number, leg) IMMEDIATE,
	CONSTRAINT Fk1_Reservation_Detail FOREIGN KEY(reservation_number) REFERENCES Reservation(reservation_number) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT Fk2_Reservation_Detail FOREIGN KEY(flight_number) REFERENCES Flight(flight_number) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT legCheck CHECK leg > 0 IMMEDIATE
);

-- Our Time
-- ASSUMPTIONS:
-- 		1) Time is store as 4 digits without a colon, just like in Flight
CREATE OR REPLACE TABLE Our_Date (
	c_date date,
	CONSTRAINT Pk_Date PRIMARY KEY(c_date) IMMEDIATE
);

--Trigger 1
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
CREATE OR REPLACE planeUpgrade
AFTER INSERT OR UPDATE ON Reservation_Detail
BEGIN
	DECLARE curr_capacity INT;
	DECLARE max_capacity INT;
	DECLARE new_type CHAR(4);
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
CREATE OR REPLACE cancelReservation
AFTER UPDATE ON Our_Date
WHEN(to_char((SELECT * FROM Our_Date) + INTERVAL '12' HH24, HH24MI) IN (SELECT departure_time FROM Flight))
BEGIN
	DECLARE cancel_time CHAR(4);
	DECLARE curr_capacity INT;
	DECLARE curr_flightNum VARCHAR(3);
	DECLARE low_capacity INT;
	DECLARE new_type CHAR(4);
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