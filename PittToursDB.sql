-- Pitt Tours Database
-- Jonathan Hanobik and Seth Stayer
-- CS1555, Fall 2016

-- Airline
CREATE OR REPLACE TABLE Airline (
	airline_id VARCHAR(5),
	airline_name VARCHAR(50),
	airline_abbreviation VARCHAR(20),
	year_founded int,
	CONSTRAINT PK_Airline PRIMARY KEY(airline_id) IMMEDIATE,
);

-- Flight Schedule
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
CREATE OR REPLACE TABLE Plane (
	plane_type CHAR(4),
	manufacture VARCHAR(10),
	plane_capacity INT,
	last_service DATE,
	year INT,
	owner_id VARCHAR(5),
	CONSTRAINT PK_Plane PRIMARY KEY(plane_type) IMMEDIATE,
	CONSTRAINT FK_Plane FOREIGN KEY(owner_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT capacityCheck CHECK (plane_capacity > 0) IMMEDIATE,
	CONSTRAINT yearCheck CHECK (CAST(to_char(last_service, YYYY) AS INT) >= year) IMMEDIATE
);

-- Flight Pricing
CREATE OR REPLACE TABLE Price (
	departure_city varchar(3),
	arrival_city varchar(3),
	airline_id varchar(5),
	high_price int,
	low_price int,
	CONSTRAINT Pk_Price PRIMARY KEY(departure_city, arrival_city) IMMEDIATE,
	CONSTRAINT Fk_Price FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE
	CONSTRAINT cityCheckPrice CHECK departure_city <> arrival_city IMMEDIATE,
	CONSTRAINT priceCheck CHECK high_price > low_price IMMEDIATE
);

-- Customer
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
CREATE OR REPLACE TABLE Reservation (
	reservation_number varchar(5),
	cid varchar(9),
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
CREATE OR REPLACE TABLE Our_Date (
	c_date date,
	CONSTRAINT Pk_Date PRIMARY KEY(c_date) IMMEDIATE
);

--Trigger 1
CREATE OR REPLACE TRIGGER adjustTicket 
AFTER UPDATE ON Reservation_Detail
REFERENCING NEW AS NEW_LEG
FOR EACH ROW
	WHEN NEW_LEG.price <> old.price
BEGIN
	DECLARE reservationCost INT;
	SELECT cost INTO reservationCost
	FROM Reservation R
	WHERE NEW_LEG.reservation_number = R.reservation_number;
	reservationCost = reservationCost - :old.price;
	reservationCost = reservationCost + :NEW_LEG.price;
	UPDATE Reservation R
	SET cost = reservationCost
	WHERE :NEW_LEG.reservation_number = R.reservation_number;
END;
/

--Trigger 2
CREATE OR REPLACE planeUpgrade
AFTER INSERT OR UPDATE ON Reservation_Detail
BEGIN
	DECLARE curr_capacity INT;
	SELECT COUNT(*)
	FROM (Reservation_Detail R JOIN Flight F ON R.flight_number = F.flight_number) AS T JOIN Plane P ON T.plane_type = P.plane_type
	GROUP BY P.plane_type
	HAVING plane_type = 
END;
/