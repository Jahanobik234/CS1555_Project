-- Pitt Tours Database
-- Jonathan Hanobik and Seth Stayer
-- CS1555, Fall 2016

-- Airline
-- ASSUMPTIONS:
DROP TABLE Airline CASCADE CONSTRAINTS;
CREATE TABLE Airline (
	airline_id VARCHAR(5),
	airline_name VARCHAR(50),
	airline_abbreviation VARCHAR(20),
	year_founded int,
	CONSTRAINT PK_Airline PRIMARY KEY(airline_id)
);

-- Plane
-- ASSUMPTIONS:
-- 1) Each plane has a capacity of at least 0
-- 2) Each plane has been serviced after its birth year	
DROP TABLE Plane CASCADE CONSTRAINTS;
CREATE TABLE Plane (
	plane_type CHAR(4),
	manufacture VARCHAR(10),
	plane_capacity INT,
	last_service DATE,
	year INT,
	owner_id VARCHAR(5),
	CONSTRAINT PK_Plane PRIMARY KEY(plane_type, owner_id),
	CONSTRAINT FK_Plane FOREIGN KEY(owner_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT capacityCheck CHECK (plane_capacity > 0),
	CONSTRAINT yearCheck CHECK (CAST(to_char(last_service, 'YYYY') AS INT) >= year)
);

-- Flight Schedule
-- ASSUMPTIONS:
-- 		1) There are no delays or early departures for any flights
--		2) Every flight is at least offered on some day of the week
DROP TABLE Flight CASCADE CONSTRAINTS;
CREATE TABLE Flight (
	flight_number VARCHAR(3),
	airline_id VARCHAR(5),
	plane_type CHAR(4),
	departure_city VARCHAR(3),
	arrival_city VARCHAR(3),
	departure_time VARCHAR(4),
	arrival_time VARCHAR(4),
	weekly_schedule VARCHAR(7),
	CONSTRAINT PK_Flight PRIMARY KEY(flight_number),
	CONSTRAINT FK1_Flight FOREIGN KEY(plane_type, airline_id) REFERENCES Plane(plane_type, owner_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT FK2_Flight FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT cityCheckFlight CHECK (departure_city <> arrival_city),
	CONSTRAINT scheduleCheck CHECK (weekly_schedule <> '-------'),
	CONSTRAINT timeCheck CHECK((departure_time BETWEEN '0000' AND '2359') AND (arrival_time BETWEEN '0000' AND '2359'))	
);

-- Flight Pricing
-- ASSUMPTIONS:
--		
DROP TABLE Price CASCADE CONSTRAINTS;
CREATE TABLE Price (
	departure_city varchar(3),
	arrival_city varchar(3),
	airline_id varchar(5),
	high_price int,
	low_price int,
	CONSTRAINT Pk_Price PRIMARY KEY(departure_city, arrival_city, airline_id),
	CONSTRAINT Fk_Price FOREIGN KEY(airline_id) REFERENCES Airline(airline_id) INITIALLY DEFERRED DEFERRABLE,
	CONSTRAINT cityCheckPrice CHECK (departure_city <> arrival_city),
	CONSTRAINT priceCheck CHECK (high_price > low_price)
);

-- Customer
-- ASSUMPTIONS:
--		1) Two people can have the same name, but will have different cids
--		2) A customer's credit card's expiration date must be later than the current year
--		3)
DROP TABLE Customer CASCADE CONSTRAINTS;
CREATE TABLE Customer (
	cid varchar(9),
	salutation varchar(3),
	first_name varchar(30),
	last_name varchar(30),
	credit_card_num varchar(16),
	credit_card_expire date,
	street varchar(30),
	city varchar(30),
	state varchar(2),
	phone varchar(10),
	email varchar(30),
	frequent_miles varchar(5),
	CONSTRAINT Pk_Customer PRIMARY KEY(cid),
	CONSTRAINT validCC CHECK (CAST(to_char(credit_card_expire, 'YYYY') AS INT) >= 2016)
);

-- Reservation Information
-- ASSUMPTIONS:
--		1) Each customer uses their preferred credit card, the one provided in Customer
--		2) ReservationDate must be after the current date
--DROP TABLE Reservation CASCADE CONSTRAINTS;
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
	--CONSTRAINT Fk_Reservation FOREIGN KEY(credit_card_num) REFERENCES Customer(credit_card_num) INITIALLY DEFERRED DEFERRABLE,
	--CONSTRAINT customerCCCheck CHECK (credit_card_num == (SELECT credit_card_num FROM Customer C WHERE C.cid = cid)),
	--CONSTRAINT reserveDateCheck CHECK (reservation_date >= sysdate)
);

-- Reservation Detail
-- ASSUMPTIONS:
--		1) Each Reservation Has At Least One Leg
--DROP TABLE Reservation_Detail CASCADE CONSTRAINTS;
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
DROP TABLE Our_Date CASCADE CONSTRAINTS;
CREATE TABLE Our_Date (
	c_date date,
	CONSTRAINT Pk_Date PRIMARY KEY(c_date)
);
commit;

-- Data For Database
INSERT INTO CUSTOMER VALUES(00000001, 'Mr', 'Will', 'Johnson', '3509210469923528', to_date('03/23', 'MM/YY'), '958 Bigelow Boulevard', 'Tallahassee', 'FL', '1200192583', '1@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000002, 'Mr', 'Joshua', 'Stayer', '5183818328028387', to_date('06/20', 'MM/YY'), '406 McKee Street', 'Billings', 'MT', '0523382044', '2@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000003, 'Mrs', 'Day', 'Stevenson', '4618950241582653', to_date('08/25', 'MM/YY'), '694 West Avenue', 'Albany', 'NY', '4364870991', '3@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000004, 'Mrs', 'Katie', 'Getz', '8940856511400581', to_date('12/25', 'MM/YY'), '059 Cherry Street', 'Boston', 'MA', '3163344374', '4@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000005, 'Mrs', 'Martha', 'Ross', '4695416608579068', to_date('08/24', 'MM/YY'), '334 Farm Lane', 'Honolulu', 'HI', '2374976958', '5@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000006, 'Mrs', 'Kathy', 'Carroll', '8165678802082387', to_date('09/25', 'MM/YY'), '469 Friendship Lane', 'Pittsburgh', 'PA', '7610910610', '6@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000007, 'Mrs', 'Staci', 'Hanobik', '4439137482904566', to_date('05/24', 'MM/YY'), '205 Orchard Circle', 'Philadelphia', 'PA', '9319585296', '7@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000008, 'Mrs', 'Audrey', 'Roosevelt', '1877459825416352', to_date('07/18', 'MM/YY'), '160 Cathedral Road', 'Columbus', 'OH', '0055020662', '8@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000009, 'Mr', 'Will', 'Miller', '8449039710882388', to_date('01/17', 'MM/YY'), '098 Main Street', 'Tallahassee', 'FL', '7874247615', '9@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000010, 'Mr', 'Tim', 'McKinney', '0982454960167452', to_date('10/19', 'MM/YY'), '713 Forbes Avenue', 'Indianapolis', 'IA', '4200274696', '10@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000011, 'Mrs', 'Breanna', 'Johnson', '9631010112481858', to_date('03/25', 'MM/YY'), '370 Bigelow Boulevard', 'Anchorage', 'AK', '5619224302', '11@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000012, 'Mr', 'Adam', 'Carroll', '7540858498047522', to_date('09/22', 'MM/YY'), '397 Friendship Lane', 'Honolulu', 'HI', '3086671193', '12@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000013, 'Mr', 'Caleb', 'Miller', '3575905750798514', to_date('01/17', 'MM/YY'), '720 Main Street', 'Los Angeles', 'CA', '5228583563', '13@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000014, 'Mr', 'Dan', 'Frey', '1718109962795822', to_date('04/21', 'MM/YY'), '810 Kenyon Trail', 'Houston', 'TX', '6378272032', '14@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000015, 'Mr', 'Jesse', 'Misuraca', '1528929352041193', to_date('05/17', 'MM/YY'), '777 Yuletide Road', 'New Orleans', 'LA', '3180270448', '15@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000016, 'Mrs', 'Lisa', 'Ross', '4566357016445796', to_date('08/24', 'MM/YY'), '939 Farm Lane', 'Los Angeles', 'CA', '1348331706', '16@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000017, 'Mr', 'Tim', 'Smith', '9320382336177751', to_date('02/25', 'MM/YY'), '858 Walnut Street', 'Indianapolis', 'IA', '2698395432', '17@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000018, 'Mrs', 'Fay', 'Miller', '6724984859862411', to_date('01/18', 'MM/YY'), '848 Main Street', 'Harrisburg', 'PA', '0714969202', '18@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000019, 'Mr', 'Seth', 'Butcher', '2851633427121462', to_date('03/17', 'MM/YY'), '596 Memory Lane', 'Pittsburgh', 'PA', '0407160996', '19@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000020, 'Mr', 'Paul', 'Misuraca', '5643345357492844', to_date('05/18', 'MM/YY'), '798 Yuletide Road', 'Columbus', 'OH', '1980113058', '20@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000021, 'Mrs', 'Fay', 'Misuraca', '4946803852932601', to_date('05/23', 'MM/YY'), '474 Yuletide Road', 'Harrisburg', 'PA', '1151885826', '21@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000022, 'Mr', 'Adam', 'Miller', '7296528752365057', to_date('01/21', 'MM/YY'), '660 Main Street', 'Honolulu', 'HI', '8501495157', '22@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000023, 'Mr', 'John', 'Sinatra', '2637881711500501', to_date('01/20', 'MM/YY'), '702 New School Lane', 'Albany', 'NY', '3941809857', '23@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000024, 'Mrs', 'Kay', 'Richardson', '5736761917996732', to_date('07/17', 'MM/YY'), '724 Pine Road', 'Hershey', 'PA', '5138741550', '24@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000025, 'Mr', 'Adam', 'Brown', '1485941585929089', to_date('04/22', 'MM/YY'), '798 Oakland Avenue', 'Honolulu', 'HI', '0960476003', '25@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000026, 'Mrs', 'Rebecca', 'Carroll', '4939720896231476', to_date('09/24', 'MM/YY'), '119 Friendship Lane', 'Chicago', 'IL', '9640332333', '26@pitt.edu', '80001');
INSERT INTO CUSTOMER VALUES(00000027, 'Ms', 'Day', 'Gosa', '8450134252361069', to_date('11/19', 'MM/YY'), '106 Fifth Avenue', 'Albany', 'NY', '1154123533', '27@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000028, 'Mr', 'John', 'Misuraca', '2139306304630889', to_date('05/21', 'MM/YY'), '120 Yuletide Road', 'Albany', 'NY', '4323084714', '28@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000029, 'Mrs', 'Monica', 'Ross', '5460466635486598', to_date('08/22', 'MM/YY'), '413 Farm Lane', 'Indianapolis', 'IA', '2520638344', '29@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000030, 'Mr', 'Jesse', 'Ross', '2526508990253940', to_date('08/16', 'MM/YY'), '983 Farm Lane', 'New Orleans', 'LA', '1645552536', '30@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000031, 'Mr', 'Sam', 'Richardson', '1930198002028675', to_date('07/22', 'MM/YY'), '347 Pine Road', 'Boston', 'MA', '4219463696', '31@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000032, 'Ms', 'Demeara', 'Miller', '6696510677012455', to_date('01/24', 'MM/YY'), '363 Main Street', 'Tallahassee', 'FL', '2422042551', '32@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000033, 'Mr', 'Tim', 'Frey', '5109478517595569', to_date('04/21', 'MM/YY'), '078 Kenyon Trail', 'Indianapolis', 'IA', '2231378059', '33@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000034, 'Ms', 'Gay', 'Gosa', '9727544016348432', to_date('11/18', 'MM/YY'), '036 Fifth Avenue', 'York', 'PA', '0954513934', '34@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000035, 'Mrs', 'Demeara', 'Tuft', '3851862666259639', to_date('06/20', 'MM/YY'), '822 Swanson Street', 'Tallahassee', 'FL', '6697839051', '35@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000036, 'Mrs', 'Kathy', 'Brown', '0160875713407283', to_date('04/18', 'MM/YY'), '944 Oakland Avenue', 'Pittsburgh', 'PA', '2498978504', '36@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000037, 'Mrs', 'Audrey', 'Miller', '4954587240899349', to_date('01/24', 'MM/YY'), '820 Main Street', 'Columbus', 'OH', '4349975811', '37@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000038, 'Mrs', 'Monica', 'Getz', '0443927771496815', to_date('12/17', 'MM/YY'), '122 Cherry Street', 'Indianapolis', 'IA', '1159344274', '38@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000039, 'Mrs', 'Kim', 'Brown', '0329959621882047', to_date('04/20', 'MM/YY'), '298 Oakland Avenue', 'New Orleans', 'LA', '4851700842', '39@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000040, 'Ms', 'Day', 'Johnson', '6224401101423939', to_date('03/20', 'MM/YY'), '267 Bigelow Boulevard', 'Albany', 'NY', '1174774358', '40@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000041, 'Mr', 'Spencer', 'Gosa', '3504148436158590', to_date('11/17', 'MM/YY'), '063 Fifth Avenue', 'Chicago', 'IL', '2121937915', '41@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000042, 'Mrs', 'Annie', 'Stayer', '3719835273903841', to_date('06/23', 'MM/YY'), '974 McKee Street', 'Providence', 'RI', '9248491393', '42@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000043, 'Ms', 'Kim', 'Stayer', '7100478503562931', to_date('06/17', 'MM/YY'), '237 McKee Street', 'New Orleans', 'LA', '5620941601', '43@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000044, 'Mr', 'Will', 'Gosa', '2040671908910502', to_date('11/23', 'MM/YY'), '929 Fifth Avenue', 'Tallahassee', 'FL', '2810538356', '44@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000045, 'Mr', 'Caleb', 'Tuft', '2926006389305926', to_date('06/24', 'MM/YY'), '864 Swanson Street', 'Los Angeles', 'CA', '2374979427', '45@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000046, 'Mr', 'John', 'Sinatra', '6801389985486120', to_date('01/18', 'MM/YY'), '967 New School Lane', 'Albany', 'NY', '6785553729', '46@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000047, 'Ms', 'Rebecca', 'Smith', '0434969628486258', to_date('02/18', 'MM/YY'), '854 Walnut Street', 'Chicago', 'IL', '9561405587', '47@pitt.edu', '20001');
INSERT INTO CUSTOMER VALUES(00000048, 'Mrs', 'Emily', 'Brown', '5042681098477439', to_date('04/24', 'MM/YY'), '145 Oakland Avenue', 'Des Moines', 'IA', '6215960781', '48@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000049, 'Mrs', 'Monica', 'Gosa', '4855911971562064', to_date('11/17', 'MM/YY'), '554 Fifth Avenue', 'Indianapolis', 'IA', '2309649082', '49@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000050, 'Ms', 'Penelope', 'Johnson', '8036222254748686', to_date('03/25', 'MM/YY'), '688 Bigelow Boulevard', 'Billings', 'MT', '6069057088', '50@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000051, 'Mr', 'John', 'Johnson', '1480818139710937', to_date('03/19', 'MM/YY'), '479 Bigelow Boulevard', 'Albany', 'NY', '8594374375', '51@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000052, 'Ms', 'Staci', 'Brown', '8409779245586390', to_date('04/22', 'MM/YY'), '419 Oakland Avenue', 'Philadelphia', 'PA', '8839125238', '52@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000053, 'Mr', 'Adam', 'Brown', '2341507851224478', to_date('04/22', 'MM/YY'), '011 Oakland Avenue', 'Honolulu', 'HI', '1102688052', '53@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000054, 'Ms', 'Sara', 'Tuft', '4401414574284730', to_date('06/22', 'MM/YY'), '045 Swanson Street', 'Houston', 'TX', '3304971065', '54@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000055, 'Ms', 'Rebecca', 'Frey', '6738302317390465', to_date('04/17', 'MM/YY'), '242 Kenyon Trail', 'Chicago', 'IL', '4513423480', '55@pitt.edu', '90001');
INSERT INTO CUSTOMER VALUES(00000056, 'Mr', 'Hunter', 'Frey', '0444096568051483', to_date('04/23', 'MM/YY'), '079 Kenyon Trail', 'Anchorage', 'AK', '5943469831', '56@pitt.edu', '30001');
INSERT INTO CUSTOMER VALUES(00000057, 'Mr', 'Michael', 'Gosa', '8167992698658577', to_date('11/25', 'MM/YY'), '710 Fifth Avenue', 'Providence', 'RI', '5132086944', '57@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000058, 'Mr', 'Caleb', 'Misuraca', '5149998125775560', to_date('05/23', 'MM/YY'), '058 Yuletide Road', 'Los Angeles', 'CA', '7857466550', '58@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000059, 'Mr', 'Dan', 'Miller', '8084689669940150', to_date('01/22', 'MM/YY'), '329 Main Street', 'Houston', 'TX', '0135826281', '59@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000060, 'Mrs', 'Monica', 'McKinney', '7085158892112588', to_date('10/22', 'MM/YY'), '924 Forbes Avenue', 'Indianapolis', 'IA', '3961834356', '60@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000061, 'Mr', 'Sam', 'Carroll', '3936272347202651', to_date('09/25', 'MM/YY'), '119 Friendship Lane', 'Boston', 'MA', '7601137618', '61@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000062, 'Mr', 'Will', 'Roosevelt', '5111350216352289', to_date('07/16', 'MM/YY'), '207 Cathedral Road', 'Tallahassee', 'FL', '5070124698', '62@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000063, 'Mr', 'Tim', 'Johnson', '5081575328635955', to_date('03/23', 'MM/YY'), '674 Bigelow Boulevard', 'Indianapolis', 'IA', '1403623538', '63@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000064, 'Mr', 'Wyatt', 'Roosevelt', '4598708160853369', to_date('07/16', 'MM/YY'), '630 Cathedral Road', 'Atlanta', 'GA', '0721941260', '64@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000065, 'Mr', 'Joshua', 'Brown', '1167313331416291', to_date('04/18', 'MM/YY'), '292 Oakland Avenue', 'Billings', 'MT', '0590560745', '65@pitt.edu', '70001');
INSERT INTO CUSTOMER VALUES(00000066, 'Mr', 'Will', 'Stevenson', '0961896372251614', to_date('08/23', 'MM/YY'), '927 West Avenue', 'Tallahassee', 'FL', '8367783075', '66@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000067, 'Mrs', 'Katie', 'Tuft', '3721706305097034', to_date('06/17', 'MM/YY'), '553 Swanson Street', 'Boston', 'MA', '2420151324', '67@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000068, 'Ms', 'Emily', 'Tuft', '6396032344802302', to_date('06/24', 'MM/YY'), '239 Swanson Street', 'Des Moines', 'IA', '9732606476', '68@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000069, 'Mr', 'Jonathan', 'McKinney', '2321720975389942', to_date('10/24', 'MM/YY'), '557 Forbes Avenue', 'Philadelphia', 'PA', '8046231053', '69@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000070, 'Mr', 'John', 'Brown', '6092529472117771', to_date('04/17', 'MM/YY'), '447 Oakland Avenue', 'Albany', 'NY', '5909215506', '70@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000071, 'Ms', 'Rebecca', 'Brown', '7030586022578211', to_date('04/18', 'MM/YY'), '154 Oakland Avenue', 'Chicago', 'IL', '2317259532', '71@pitt.edu', '30001');
INSERT INTO CUSTOMER VALUES(00000072, 'Mrs', 'Kay', 'Stayer', '3553187226177857', to_date('06/21', 'MM/YY'), '205 McKee Street', 'Hershey', 'PA', '7507794491', '72@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000073, 'Mr', 'Tim', 'Richardson', '3385072481728921', to_date('07/20', 'MM/YY'), '750 Pine Road', 'Indianapolis', 'IA', '5910682103', '73@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000074, 'Mr', 'John', 'Richardson', '3300818647800763', to_date('07/25', 'MM/YY'), '564 Pine Road', 'Albany', 'NY', '8733085224', '74@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000075, 'Mr', 'Jonathan', 'Stayer', '3102715931171510', to_date('06/16', 'MM/YY'), '176 McKee Street', 'Philadelphia', 'PA', '4116796542', '75@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000076, 'Mr', 'Wyatt', 'Hanobik', '2119613837554732', to_date('05/21', 'MM/YY'), '498 Orchard Circle', 'Atlanta', 'GA', '3841414028', '76@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000077, 'Ms', 'Rebecca', 'Jonas', '8352940609842567', to_date('02/25', 'MM/YY'), '279 Distillery Road', 'Chicago', 'IL', '0905804623', '77@pitt.edu', '30001');
INSERT INTO CUSTOMER VALUES(00000078, 'Mr', 'Adam', 'Richardson', '7379746691920097', to_date('07/20', 'MM/YY'), '528 Pine Road', 'Honolulu', 'HI', '3022678381', '78@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000079, 'Ms', 'Monica', 'Tuft', '8217346511345258', to_date('06/17', 'MM/YY'), '375 Swanson Street', 'Indianapolis', 'IA', '6617630979', '79@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000080, 'Ms', 'Fay', 'Miller', '5447192009972154', to_date('01/21', 'MM/YY'), '466 Main Street', 'Harrisburg', 'PA', '6861094924', '80@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000081, 'Mr', 'Seth', 'McKinney', '4740867488206327', to_date('10/23', 'MM/YY'), '574 Forbes Avenue', 'Pittsburgh', 'PA', '2401876395', '81@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000082, 'Mr', 'Dan', 'Roosevelt', '5071966867945893', to_date('07/23', 'MM/YY'), '774 Cathedral Road', 'Houston', 'TX', '2090068474', '82@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000083, 'Ms', 'Fay', 'Gosa', '1573264227608143', to_date('11/18', 'MM/YY'), '027 Fifth Avenue', 'Harrisburg', 'PA', '6380075169', '83@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000084, 'Mrs', 'Rebecca', 'Sinatra', '6898673609096861', to_date('01/17', 'MM/YY'), '490 New School Lane', 'Chicago', 'IL', '5651915109', '84@pitt.edu', '10001');
INSERT INTO CUSTOMER VALUES(00000085, 'Mr', 'Jonathan', 'Misuraca', '9749720517829037', to_date('05/21', 'MM/YY'), '073 Yuletide Road', 'Philadelphia', 'PA', '7695377313', '85@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000086, 'Mr', 'Joshua', 'Stayer', '1577410975288073', to_date('06/24', 'MM/YY'), '909 McKee Street', 'Billings', 'MT', '3662119009', '86@pitt.edu', '80001');
INSERT INTO CUSTOMER VALUES(00000087, 'Ms', 'Day', 'Tuft', '7781041754497283', to_date('06/24', 'MM/YY'), '425 Swanson Street', 'Albany', 'NY', '3275277820', '87@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000088, 'Ms', 'Audrey', 'Miller', '8577401533009166', to_date('01/24', 'MM/YY'), '112 Main Street', 'Columbus', 'OH', '5031790462', '88@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000089, 'Mr', 'Matt', 'Sinatra', '1715460299429148', to_date('01/19', 'MM/YY'), '194 New School Lane', 'Des Moines', 'IA', '1354777384', '89@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000090, 'Ms', 'Rebecca', 'Smith', '6582061701969403', to_date('02/21', 'MM/YY'), '123 Walnut Street', 'Chicago', 'IL', '5069779134', '90@pitt.edu', '50001');
INSERT INTO CUSTOMER VALUES(00000091, 'Mr', 'Seth', 'Johnson', '8169058205344944', to_date('03/20', 'MM/YY'), '355 Bigelow Boulevard', 'Pittsburgh', 'PA', '5007606366', '91@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000092, 'Mr', 'Seth', 'Getz', '9803263591575527', to_date('12/24', 'MM/YY'), '018 Cherry Street', 'Pittsburgh', 'PA', '3466593503', '92@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000093, 'Mr', 'Will', 'Sinatra', '2998284892946533', to_date('01/17', 'MM/YY'), '365 New School Lane', 'Tallahassee', 'FL', '3957569525', '93@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000094, 'Ms', 'Gay', 'Frey', '7771749396401296', to_date('04/21', 'MM/YY'), '722 Kenyon Trail', 'York', 'PA', '6366040155', '94@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000095, 'Mr', 'Caleb', 'Richardson', '7249825416190863', to_date('07/25', 'MM/YY'), '153 Pine Road', 'Los Angeles', 'CA', '2508568994', '95@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000096, 'Mr', 'Tim', 'Misuraca', '1031515031499483', to_date('05/21', 'MM/YY'), '245 Yuletide Road', 'Indianapolis', 'IA', '5629809341', '96@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000097, 'Mr', 'Hunter', 'Ross', '5062047248845301', to_date('08/24', 'MM/YY'), '122 Farm Lane', 'Anchorage', 'AK', '0029534375', '97@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000098, 'Mr', 'Seth', 'Johnson', '4642089492146367', to_date('03/16', 'MM/YY'), '468 Bigelow Boulevard', 'Pittsburgh', 'PA', '0204966634', '98@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000099, 'Mr', 'Hunter', 'Carroll', '6496561604423304', to_date('09/24', 'MM/YY'), '745 Friendship Lane', 'Anchorage', 'AK', '6794310713', '99@pitt.edu', '50001');
INSERT INTO CUSTOMER VALUES(00000100, 'Mr', 'Spencer', 'McKinney', '0954607427700860', to_date('10/24', 'MM/YY'), '469 Forbes Avenue', 'Chicago', 'IL', '1224203419', '100@pitt.edu', '50001');
INSERT INTO CUSTOMER VALUES(00000101, 'Ms', 'Kim', 'Gosa', '5415659997647814', to_date('11/20', 'MM/YY'), '497 Fifth Avenue', 'New Orleans', 'LA', '4088965612', '101@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000102, 'Ms', 'Sara', 'Roosevelt', '9392765437308988', to_date('07/17', 'MM/YY'), '310 Cathedral Road', 'Houston', 'TX', '8297714410', '102@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000103, 'Mr', 'Caleb', 'Miller', '9791647944832003', to_date('01/20', 'MM/YY'), '902 Main Street', 'Los Angeles', 'CA', '6937511400', '103@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000104, 'Mr', 'Will', 'Getz', '1902388885829047', to_date('12/23', 'MM/YY'), '916 Cherry Street', 'Tallahassee', 'FL', '7368803738', '104@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000105, 'Mrs', 'Audrey', 'Misuraca', '1790122791906894', to_date('05/23', 'MM/YY'), '542 Yuletide Road', 'Columbus', 'OH', '4577772589', '105@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000106, 'Mr', 'Paul', 'Brown', '7175889425486242', to_date('04/22', 'MM/YY'), '559 Oakland Avenue', 'Columbus', 'OH', '1379383139', '106@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000107, 'Mrs', 'Audrey', 'Misuraca', '3386195228995261', to_date('05/20', 'MM/YY'), '746 Yuletide Road', 'Columbus', 'OH', '2075571645', '107@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000108, 'Mrs', 'Rebecca', 'Tuft', '9978980484198440', to_date('06/19', 'MM/YY'), '118 Swanson Street', 'Chicago', 'IL', '9749874189', '108@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000109, 'Ms', 'Fay', 'McKinney', '8506032208206702', to_date('10/21', 'MM/YY'), '145 Forbes Avenue', 'Harrisburg', 'PA', '0953272143', '109@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000110, 'Mr', 'Hunter', 'Miller', '7536388004299987', to_date('01/17', 'MM/YY'), '677 Main Street', 'Anchorage', 'AK', '3945503073', '110@pitt.edu', '10001');
INSERT INTO CUSTOMER VALUES(00000111, 'Ms', 'Lisa', 'Richardson', '9270000873503957', to_date('07/20', 'MM/YY'), '627 Pine Road', 'Los Angeles', 'CA', '8543588960', '111@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000112, 'Mr', 'Spencer', 'Johnson', '4452669461972828', to_date('03/25', 'MM/YY'), '057 Bigelow Boulevard', 'Chicago', 'IL', '1055646026', '112@pitt.edu', '80001');
INSERT INTO CUSTOMER VALUES(00000113, 'Ms', 'Annie', 'Stayer', '4407119418888801', to_date('06/21', 'MM/YY'), '613 McKee Street', 'Providence', 'RI', '1850136745', '113@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000114, 'Mr', 'Joshua', 'Richardson', '5327060662740491', to_date('07/18', 'MM/YY'), '949 Pine Road', 'Billings', 'MT', '8285186229', '114@pitt.edu', '60001');
INSERT INTO CUSTOMER VALUES(00000115, 'Ms', 'Gay', 'Misuraca', '8670311608612532', to_date('05/18', 'MM/YY'), '361 Yuletide Road', 'York', 'PA', '4245441126', '115@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000116, 'Ms', 'Annie', 'Miller', '0947108245765652', to_date('01/20', 'MM/YY'), '246 Main Street', 'Providence', 'RI', '2408942022', '116@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000117, 'Mr', 'Dan', 'Hanobik', '8631383162837944', to_date('05/21', 'MM/YY'), '960 Orchard Circle', 'Houston', 'TX', '6517230215', '117@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000118, 'Mr', 'Wyatt', 'Frey', '4172762908514121', to_date('04/20', 'MM/YY'), '514 Kenyon Trail', 'Atlanta', 'GA', '6989722086', '118@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000119, 'Ms', 'Emily', 'Hanobik', '1415696706865186', to_date('05/25', 'MM/YY'), '880 Orchard Circle', 'Des Moines', 'IA', '3928452215', '119@pitt.edu', '20001');
INSERT INTO CUSTOMER VALUES(00000120, 'Mrs', 'Sara', 'Sinatra', '1697180956208149', to_date('01/18', 'MM/YY'), '158 New School Lane', 'Houston', 'TX', '7806080222', '120@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000121, 'Mr', 'Sam', 'Frey', '0021365605291411', to_date('04/16', 'MM/YY'), '321 Kenyon Trail', 'Boston', 'MA', '8172196825', '121@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000122, 'Mr', 'Dan', 'McKinney', '2155864787856023', to_date('10/19', 'MM/YY'), '276 Forbes Avenue', 'Houston', 'TX', '2044028852', '122@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000123, 'Ms', 'Fay', 'Misuraca', '7543079548339971', to_date('05/25', 'MM/YY'), '231 Yuletide Road', 'Harrisburg', 'PA', '8918252691', '123@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000124, 'Mr', 'Hunter', 'Roosevelt', '5091448202333672', to_date('07/21', 'MM/YY'), '354 Cathedral Road', 'Anchorage', 'AK', '9511829946', '124@pitt.edu', '60001');
INSERT INTO CUSTOMER VALUES(00000125, 'Mrs', 'Penelope', 'Richardson', '7060738457799091', to_date('07/21', 'MM/YY'), '892 Pine Road', 'Billings', 'MT', '3487694843', '125@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000126, 'Ms', 'Rebecca', 'Butcher', '8162047525651610', to_date('03/25', 'MM/YY'), '515 Memory Lane', 'Chicago', 'IL', '0431374550', '126@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000127, 'Mr', 'Jesse', 'Tuft', '4033806723201579', to_date('06/16', 'MM/YY'), '961 Swanson Street', 'New Orleans', 'LA', '7821254843', '127@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000128, 'Ms', 'Monica', 'Sinatra', '2285064809421226', to_date('01/17', 'MM/YY'), '496 New School Lane', 'Indianapolis', 'IA', '2401239602', '128@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000129, 'Mr', 'Dan', 'Misuraca', '5347008258536308', to_date('05/16', 'MM/YY'), '760 Yuletide Road', 'Houston', 'TX', '2865492421', '129@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000130, 'Mrs', 'Katie', 'Jonas', '0618438461040987', to_date('02/17', 'MM/YY'), '509 Distillery Road', 'Boston', 'MA', '5916948295', '130@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000131, 'Ms', 'Breanna', 'Misuraca', '7496983499520473', to_date('05/19', 'MM/YY'), '480 Yuletide Road', 'Anchorage', 'AK', '0880109973', '131@pitt.edu', '90001');
INSERT INTO CUSTOMER VALUES(00000132, 'Mrs', 'Fay', 'Ross', '4632566301396338', to_date('08/23', 'MM/YY'), '294 Farm Lane', 'Harrisburg', 'PA', '1413789279', '132@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000133, 'Mr', 'Will', 'Sinatra', '9740100773390663', to_date('01/24', 'MM/YY'), '208 New School Lane', 'Tallahassee', 'FL', '5323444513', '133@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000134, 'Mr', 'Will', 'Getz', '0960335663548419', to_date('12/23', 'MM/YY'), '696 Cherry Street', 'Tallahassee', 'FL', '9814015734', '134@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000135, 'Mrs', 'Rebecca', 'McKinney', '0681615094207231', to_date('10/21', 'MM/YY'), '641 Forbes Avenue', 'Chicago', 'IL', '7173329217', '135@pitt.edu', '70001');
INSERT INTO CUSTOMER VALUES(00000136, 'Mr', 'Michael', 'Richardson', '9200402230717757', to_date('07/16', 'MM/YY'), '762 Pine Road', 'Providence', 'RI', '7080188324', '136@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000137, 'Mr', 'Will', 'Jonas', '3568255817964530', to_date('02/17', 'MM/YY'), '380 Distillery Road', 'Tallahassee', 'FL', '5496271083', '137@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000138, 'Mr', 'Hunter', 'Richardson', '1460978079515914', to_date('07/17', 'MM/YY'), '577 Pine Road', 'Anchorage', 'AK', '0968231922', '138@pitt.edu', '10010');
INSERT INTO CUSTOMER VALUES(00000139, 'Mr', 'Tim', 'Richardson', '4013614703166748', to_date('07/20', 'MM/YY'), '217 Pine Road', 'Indianapolis', 'IA', '9079364042', '139@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000140, 'Mrs', 'Martha', 'Jonas', '0470816750761864', to_date('02/19', 'MM/YY'), '857 Distillery Road', 'Honolulu', 'HI', '0889957455', '140@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000141, 'Mrs', 'Fay', 'Tuft', '8354456281011335', to_date('06/25', 'MM/YY'), '486 Swanson Street', 'Harrisburg', 'PA', '2658840384', '141@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000142, 'Mrs', 'Emily', 'Smith', '4829391338828163', to_date('02/21', 'MM/YY'), '678 Walnut Street', 'Des Moines', 'IA', '1417269889', '142@pitt.edu', '50001');
INSERT INTO CUSTOMER VALUES(00000143, 'Mr', 'Jesse', 'Richardson', '1659020176047015', to_date('07/16', 'MM/YY'), '627 Pine Road', 'New Orleans', 'LA', '1143889865', '143@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000144, 'Ms', 'Staci', 'Ross', '0971617304797914', to_date('08/18', 'MM/YY'), '683 Farm Lane', 'Philadelphia', 'PA', '1312180528', '144@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000145, 'Mr', 'Hunter', 'Stayer', '3022946793639597', to_date('06/19', 'MM/YY'), '688 McKee Street', 'Anchorage', 'AK', '2865107775', '145@pitt.edu', '70001');
INSERT INTO CUSTOMER VALUES(00000146, 'Ms', 'Day', 'Gosa', '5099412201360748', to_date('11/22', 'MM/YY'), '293 Fifth Avenue', 'Albany', 'NY', '5073073469', '146@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000147, 'Mr', 'Hunter', 'McKinney', '1975775613294742', to_date('10/20', 'MM/YY'), '387 Forbes Avenue', 'Anchorage', 'AK', '7231496059', '147@pitt.edu', '70001');
INSERT INTO CUSTOMER VALUES(00000148, 'Mr', 'Jonathan', 'McKinney', '7069971875337311', to_date('10/25', 'MM/YY'), '765 Forbes Avenue', 'Philadelphia', 'PA', '6374767996', '148@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000149, 'Mr', 'Adam', 'Gosa', '1423874250589501', to_date('11/24', 'MM/YY'), '855 Fifth Avenue', 'Honolulu', 'HI', '0256093990', '149@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000150, 'Ms', 'Martha', 'Misuraca', '7589094685239671', to_date('05/17', 'MM/YY'), '689 Yuletide Road', 'Honolulu', 'HI', '7017958635', '150@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000151, 'Mr', 'Hunter', 'Frey', '1011588177074101', to_date('04/24', 'MM/YY'), '908 Kenyon Trail', 'Anchorage', 'AK', '9447930617', '151@pitt.edu', '30001');
INSERT INTO CUSTOMER VALUES(00000152, 'Ms', 'Jessica', 'Stayer', '4200834896601973', to_date('06/17', 'MM/YY'), '331 McKee Street', 'Atlanta', 'GA', '3649264246', '152@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000153, 'Mr', 'Sam', 'Johnson', '4707942277818822', to_date('03/21', 'MM/YY'), '279 Bigelow Boulevard', 'Boston', 'MA', '8823846642', '153@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000154, 'Mrs', 'Kay', 'Misuraca', '7728610314231816', to_date('05/22', 'MM/YY'), '868 Yuletide Road', 'Hershey', 'PA', '5864836146', '154@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000155, 'Mrs', 'Emily', 'Stevenson', '7402339701607970', to_date('08/21', 'MM/YY'), '191 West Avenue', 'Des Moines', 'IA', '8846061832', '155@pitt.edu', '80001');
INSERT INTO CUSTOMER VALUES(00000156, 'Mrs', 'Gay', 'Brown', '4504692868365684', to_date('04/25', 'MM/YY'), '428 Oakland Avenue', 'York', 'PA', '9603483688', '156@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000157, 'Mr', 'Hunter', 'Gosa', '4315458379633217', to_date('11/22', 'MM/YY'), '494 Fifth Avenue', 'Anchorage', 'AK', '8042289436', '157@pitt.edu', '90001');
INSERT INTO CUSTOMER VALUES(00000158, 'Ms', 'Penelope', 'Jonas', '2957634565726032', to_date('02/20', 'MM/YY'), '546 Distillery Road', 'Billings', 'MT', '4138214076', '158@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000159, 'Mr', 'Spencer', 'Carroll', '2505738668745668', to_date('09/18', 'MM/YY'), '659 Friendship Lane', 'Chicago', 'IL', '1750553648', '159@pitt.edu', '30001');
INSERT INTO CUSTOMER VALUES(00000160, 'Ms', 'Audrey', 'Richardson', '7248286405634639', to_date('07/18', 'MM/YY'), '332 Pine Road', 'Columbus', 'OH', '2739790629', '160@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000161, 'Mr', 'Michael', 'Tuft', '5542692985163071', to_date('06/19', 'MM/YY'), '659 Swanson Street', 'Providence', 'RI', '4709057580', '161@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000162, 'Mr', 'Adam', 'Roosevelt', '9824948680523329', to_date('07/20', 'MM/YY'), '640 Cathedral Road', 'Honolulu', 'HI', '1790508291', '162@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000163, 'Mrs', 'Monica', 'Jonas', '7380496582562872', to_date('02/22', 'MM/YY'), '846 Distillery Road', 'Indianapolis', 'IA', '3179673994', '163@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000164, 'Mr', 'Paul', 'Tuft', '0483283696235563', to_date('06/22', 'MM/YY'), '706 Swanson Street', 'Columbus', 'OH', '1707579380', '164@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000165, 'Mrs', 'Monica', 'Jonas', '4734160185908841', to_date('02/18', 'MM/YY'), '989 Distillery Road', 'Indianapolis', 'IA', '5771383272', '165@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000166, 'Mrs', 'Kay', 'Carroll', '4344068484423623', to_date('09/24', 'MM/YY'), '470 Friendship Lane', 'Hershey', 'PA', '2719030248', '166@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000167, 'Mr', 'Michael', 'Misuraca', '9917726363498158', to_date('05/16', 'MM/YY'), '410 Yuletide Road', 'Providence', 'RI', '8389635915', '167@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000168, 'Mr', 'Hunter', 'Butcher', '5897997276973341', to_date('03/21', 'MM/YY'), '036 Memory Lane', 'Anchorage', 'AK', '4882991032', '168@pitt.edu', '50001');
INSERT INTO CUSTOMER VALUES(00000169, 'Ms', 'Day', 'Jonas', '8973353296889250', to_date('02/17', 'MM/YY'), '068 Distillery Road', 'Albany', 'NY', '6045802069', '169@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000170, 'Mr', 'Mark', 'Butcher', '7359973028163416', to_date('03/23', 'MM/YY'), '983 Memory Lane', 'Harrisburg', 'PA', '4203567281', '170@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000171, 'Ms', 'Penelope', 'Carroll', '2865216281964375', to_date('09/23', 'MM/YY'), '259 Friendship Lane', 'Billings', 'MT', '2501796324', '171@pitt.edu', '40001');
INSERT INTO CUSTOMER VALUES(00000172, 'Mrs', 'Kay', 'Brown', '9798715508235178', to_date('04/17', 'MM/YY'), '903 Oakland Avenue', 'Hershey', 'PA', '0221296660', '172@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000173, 'Ms', 'Martha', 'Carroll', '0400388534613297', to_date('09/18', 'MM/YY'), '507 Friendship Lane', 'Honolulu', 'HI', '5677716458', '173@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000174, 'Mr', 'Michael', 'Richardson', '6581900915693037', to_date('07/23', 'MM/YY'), '797 Pine Road', 'Providence', 'RI', '2820461349', '174@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000175, 'Mr', 'Paul', 'Misuraca', '4331067701430437', to_date('05/24', 'MM/YY'), '617 Yuletide Road', 'Columbus', 'OH', '9029406135', '175@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000176, 'Mr', 'Sam', 'Stevenson', '5478753380011187', to_date('08/19', 'MM/YY'), '610 West Avenue', 'Boston', 'MA', '6374469771', '176@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000177, 'Mr', 'Wyatt', 'McKinney', '5141390310499287', to_date('10/18', 'MM/YY'), '396 Forbes Avenue', 'Atlanta', 'GA', '4547473010', '177@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000178, 'Ms', 'Day', 'Richardson', '9913663382935361', to_date('07/16', 'MM/YY'), '470 Pine Road', 'Albany', 'NY', '9088578584', '178@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000179, 'Ms', 'Kay', 'McKinney', '8970425234510609', to_date('10/23', 'MM/YY'), '085 Forbes Avenue', 'Hershey', 'PA', '0546319452', '179@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000180, 'Ms', 'Monica', 'Getz', '6565100511828102', to_date('12/20', 'MM/YY'), '466 Cherry Street', 'Indianapolis', 'IA', '5310603593', '180@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000181, 'Mr', 'Jesse', 'Carroll', '2364583858140291', to_date('09/16', 'MM/YY'), '056 Friendship Lane', 'New Orleans', 'LA', '4114216819', '181@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000182, 'Ms', 'Breanna', 'Smith', '1263266297041290', to_date('02/17', 'MM/YY'), '172 Walnut Street', 'Anchorage', 'AK', '8855613620', '182@pitt.edu', '90001');
INSERT INTO CUSTOMER VALUES(00000183, 'Mr', 'Joshua', 'Roosevelt', '9151604545640914', to_date('07/19', 'MM/YY'), '277 Cathedral Road', 'Billings', 'MT', '1699209663', '183@pitt.edu', '90001');
INSERT INTO CUSTOMER VALUES(00000184, 'Mr', 'Jesse', 'Johnson', '2419417984371109', to_date('03/24', 'MM/YY'), '590 Bigelow Boulevard', 'New Orleans', 'LA', '0501636337', '184@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000185, 'Mr', 'Steve', 'Richardson', '7453257140635244', to_date('07/21', 'MM/YY'), '434 Pine Road', 'Hershey', 'PA', '7487302248', '185@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000186, 'Mr', 'Jesse', 'Roosevelt', '8769446460093840', to_date('07/19', 'MM/YY'), '470 Cathedral Road', 'New Orleans', 'LA', '3633811427', '186@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000187, 'Mr', 'Will', 'Stayer', '9520868151587346', to_date('06/22', 'MM/YY'), '442 McKee Street', 'Tallahassee', 'FL', '0959528259', '187@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000188, 'Ms', 'Penelope', 'Misuraca', '1732660689409408', to_date('05/25', 'MM/YY'), '224 Yuletide Road', 'Billings', 'MT', '8650661552', '188@pitt.edu', '80001');
INSERT INTO CUSTOMER VALUES(00000189, 'Ms', 'Sara', 'Gosa', '0973297881319311', to_date('11/17', 'MM/YY'), '418 Fifth Avenue', 'Houston', 'TX', '5850742474', '189@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000190, 'Mr', 'Steve', 'Butcher', '6186155566982386', to_date('03/21', 'MM/YY'), '394 Memory Lane', 'Hershey', 'PA', '2698806292', '190@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000191, 'Mrs', 'Monica', 'Roosevelt', '5601274400403984', to_date('07/17', 'MM/YY'), '029 Cathedral Road', 'Indianapolis', 'IA', '2172599009', '191@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000192, 'Mr', 'Michael', 'Jonas', '7905149067611479', to_date('02/24', 'MM/YY'), '863 Distillery Road', 'Providence', 'RI', '4900780374', '192@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000193, 'Mr', 'Mark', 'Jonas', '8530594830596851', to_date('02/25', 'MM/YY'), '861 Distillery Road', 'Harrisburg', 'PA', '0102285067', '193@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000194, 'Ms', 'Katie', 'Stevenson', '8419109954315446', to_date('08/21', 'MM/YY'), '251 West Avenue', 'Boston', 'MA', '6694913466', '194@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000195, 'Mr', 'Seth', 'Misuraca', '4233230488091064', to_date('05/16', 'MM/YY'), '196 Yuletide Road', 'Pittsburgh', 'PA', '7344388658', '195@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000196, 'Mrs', 'Jessica', 'Richardson', '6532467464894053', to_date('07/24', 'MM/YY'), '509 Pine Road', 'Atlanta', 'GA', '2430709999', '196@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000197, 'Mr', 'Matt', 'Jonas', '5550394771771901', to_date('02/18', 'MM/YY'), '955 Distillery Road', 'Des Moines', 'IA', '6701118216', '197@pitt.edu', '60001');
INSERT INTO CUSTOMER VALUES(00000198, 'Ms', 'Rebecca', 'Misuraca', '9283275573638918', to_date('05/21', 'MM/YY'), '357 Yuletide Road', 'Chicago', 'IL', '1645580139', '198@pitt.edu', '10001');
INSERT INTO CUSTOMER VALUES(00000199, 'Mrs', 'Sara', 'Tuft', '8277529445604492', to_date('06/22', 'MM/YY'), '137 Swanson Street', 'Houston', 'TX', '8179698930', '199@pitt.edu', NULL);
INSERT INTO CUSTOMER VALUES(00000200, 'Mr', 'Adam', 'Richardson', '9170836930009156', to_date('07/20', 'MM/YY'), '071 Pine Road', 'Honolulu', 'HI', '5500125365', '200@pitt.edu', NULL);

INSERT INTO RESERVATION_DETAIL VALUES('00001', '020', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00001', '043', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00002', '041', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00002', '046', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00002', '026', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00002', '038', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00003', '028', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00003', '052', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00003', '019', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00003', '037', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00004', '047', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00005', '024', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00005', '018', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00005', '032', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00006', '049', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00007', '004', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00007', '031', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00008', '015', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00008', '002', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00008', '021', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00009', '029', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00009', '001', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00009', '012', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00010', '033', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00010', '042', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00010', '053', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00011', '008', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00011', '001', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00011', '014', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00011', '051', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00012', '014', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00012', '051', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00012', '010', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00012', '022', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00013', '052', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00013', '020', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00014', '021', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00014', '052', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00015', '043', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00015', '003', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00016', '027', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00016', '044', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00016', '012', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00016', '042', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00017', '016', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00018', '055', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00019', '009', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00019', '018', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00019', '032', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00020', '010', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00021', '011', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00022', '039', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00023', '056', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00023', '049', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00023', '054', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00024', '036', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00024', '001', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00024', '008', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00025', '027', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00025', '044', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00025', '012', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00025', '041', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00026', '040', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00026', '029', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00026', '006', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00027', '018', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00027', '031', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00027', '016', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00027', '014', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00028', '017', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00029', '047', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00029', '035', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00029', '054', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00030', '050', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00030', '005', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00031', '104', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00031', '094', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00031', '072', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00031', '068', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00032', '068', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00032', '093', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00033', '081', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00033', '090', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00034', '097', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00034', '101', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00035', '069', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00035', '100', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00035', '068', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00035', '093', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00036', '097', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00037', '058', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00037', '076', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00037', '104', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00038', '085', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00038', '063', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00038', '108', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00038', '073', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00039', '106', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00039', '062', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00039', '105', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00040', '071', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00040', '062', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00041', '102', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00042', '066', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00042', '078', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00042', '061', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00043', '090', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00043', '100', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00043', '066', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00043', '079', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00044', '081', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00044', '086', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00045', '100', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00046', '063', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00047', '078', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00047', '062', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00048', '092', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00049', '083', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00050', '110', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00050', '089', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00050', '097', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00050', '100', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00051', '072', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00051', '069', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00052', '061', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00053', '092', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00053', '058', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00054', '110', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00055', '066', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00055', '079', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00055', '068', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00056', '081', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00056', '087', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00057', '087', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00057', '073', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00058', '086', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00059', '069', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00059', '101', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00060', '100', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00060', '070', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00061', '129', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00061', '134', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00061', '114', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00062', '114', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00062', '133', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00062', '168', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00063', '143', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00063', '132', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00064', '131', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00064', '148', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00064', '116', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00064', '143', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00065', '138', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00065', '149', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00066', '141', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00066', '118', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00067', '144', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00067', '134', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00067', '114', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00068', '151', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00069', '139', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00069', '161', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00069', '168', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00069', '159', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00070', '140', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00070', '162', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00071', '147', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00071', '164', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00072', '126', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00072', '165', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00072', '135', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00072', '123', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00073', '128', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00073', '120', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00074', '133', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00074', '163', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00074', '123', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00074', '145', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00075', '147', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00075', '164', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00075', '133', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00075', '162', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00076', '131', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00076', '154', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00076', '165', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00076', '135', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00077', '115', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00077', '137', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00077', '147', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00077', '168', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00078', '118', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00078', '159', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00078', '147', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00078', '162', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00079', '114', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00079', '133', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00079', '162', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00080', '138', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00081', '136', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00081', '132', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00081', '156', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00082', '119', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00082', '163', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00083', '129', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00083', '140', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00084', '161', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00084', '165', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00084', '138', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00084', '154', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00085', '140', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00085', '163', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00085', '122', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00085', '134', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00086', '157', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00087', '114', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00088', '160', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00088', '153', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00088', '160', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00089', '149', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00089', '124', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00089', '152', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00089', '146', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00090', '133', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00090', '163', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00090', '123', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00091', '179', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00091', '198', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00092', '195', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00092', '211', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00092', '173', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00092', '206', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00093', '179', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00093', '203', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00094', '197', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00094', '175', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00094', '218', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00094', '174', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00095', '187', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00095', '207', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00095', '196', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00095', '224', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00096', '193', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00097', '195', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00097', '211', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00097', '174', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00097', '211', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00098', '180', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00098', '205', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00098', '182', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00099', '176', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00099', '175', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00100', '185', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00101', '216', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00102', '188', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00102', '214', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00103', '169', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00103', '181', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00104', '174', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00104', '217', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00105', '222', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00105', '201', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00106', '207', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00106', '191', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00106', '176', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00107', '208', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00107', '199', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00107', '183', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00108', '194', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00108', '209', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00109', '185', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00109', '191', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00109', '182', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00109', '222', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00110', '224', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00111', '175', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00112', '195', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00112', '213', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00113', '224', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00113', '217', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00113', '224', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00114', '203', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00114', '220', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00114', '183', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00115', '189', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00116', '169', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00116', '176', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00116', '172', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00116', '203', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00117', '202', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00117', '214', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00117', '193', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00118', '170', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00118', '189', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00119', '190', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00119', '174', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00120', '199', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00120', '185', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00120', '195', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00120', '214', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00121', '266', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00121', '278', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00122', '229', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00122', '260', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00122', '226', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00122', '243', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00123', '269', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00124', '256', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00124', '246', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00124', '227', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00124', '251', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00125', '250', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00125', '261', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00125', '236', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00125', '266', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00126', '250', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00126', '266', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00127', '256', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00127', '252', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00127', '280', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00127', '273', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00128', '258', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00128', '268', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00129', '226', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00129', '245', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00130', '257', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00130', '265', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00131', '273', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00132', '259', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00132', '274', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00133', '264', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00134', '256', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00134', '248', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00134', '241', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00135', '248', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00136', '266', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00136', '277', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00136', '249', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00136', '254', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00137', '229', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00137', '265', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00138', '278', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00139', '234', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00139', '252', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00139', '279', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00140', '276', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00140', '242', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00140', '258', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00140', '271', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00141', '241', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00142', '238', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00143', '241', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00143', '251', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00143', '269', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00143', '239', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00144', '245', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00144', '274', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00144', '230', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00144', '269', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00145', '266', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00146', '275', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00146', '236', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00147', '275', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00147', '233', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00148', '260', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00148', '226', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00149', '255', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00149', '245', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00149', '280', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00149', '270', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00150', '242', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00151', '287', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00151', '331', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00151', '290', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00151', '306', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00152', '285', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00153', '314', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00153', '328', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00154', '317', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00154', '292', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00155', '281', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00155', '291', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00155', '315', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00156', '326', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00156', '302', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00156', '284', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00157', '322', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00157', '330', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00157', '282', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00158', '334', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00159', '324', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00159', '292', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00160', '327', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00160', '312', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00161', '304', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00161', '297', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00161', '302', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00162', '314', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00162', '326', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00162', '303', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00162', '290', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00163', '301', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00164', '307', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00164', '328', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00164', '317', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00165', '310', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00166', '324', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00166', '289', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00167', '335', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00167', '319', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00168', '321', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00168', '328', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00168', '317', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00169', '290', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00169', '303', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00169', '293', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00170', '330', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00170', '284', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00171', '302', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00172', '315', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00172', '332', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00172', '297', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00172', '307', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00173', '323', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00174', '285', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00174', '319', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00174', '308', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00175', '282', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00176', '300', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00176', '326', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00177', '329', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00177', '336', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00177', '324', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00178', '292', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00178', '316', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00178', '282', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00179', '306', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00179', '320', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00179', '309', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00179', '281', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00180', '334', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00181', '355', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00181', '374', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00181', '351', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00181', '343', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00182', '355', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00182', '375', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00182', '361', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00183', '362', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00183', '373', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00183', '349', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00183', '383', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00184', '347', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00184', '365', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00185', '371', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00185', '387', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00185', '345', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00185', '356', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00186', '369', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00186', '375', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00186', '363', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00187', '363', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00188', '341', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00188', '377', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00189', '392', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00189', '384', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00190', '365', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00191', '378', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00192', '357', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00192', '392', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00192', '382', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00193', '381', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00193', '356', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00193', '380', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00193', '347', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00194', '356', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00194', '379', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00195', '360', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00196', '374', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00197', '360', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00197', '357', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00198', '382', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00198', '360', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00198', '354', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00198', '365', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00199', '387', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00199', '345', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00200', '369', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00201', '354', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00201', '367', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00201', '353', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00201', '359', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00202', '380', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00202', '346', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00203', '383', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00203', '371', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00204', '355', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00204', '377', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00204', '383', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00205', '354', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00205', '368', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00206', '361', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00206', '369', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00206', '378', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00206', '392', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00207', '355', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00207', '377', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00207', '381', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00208', '384', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00208', '374', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00209', '339', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00210', '370', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00210', '385', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00211', '417', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00211', '425', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00211', '433', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00211', '438', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00212', '414', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00213', '436', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00213', '403', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00213', '423', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00214', '414', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00214', '399', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00214', '445', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00215', '405', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00215', '439', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00215', '423', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00215', '412', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00216', '405', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00216', '435', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00216', '397', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00217', '426', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00217', '441', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00217', '448', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00218', '398', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00218', '439', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00218', '421', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00219', '431', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00219', '418', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00219', '429', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00220', '430', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00220', '411', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00221', '397', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00221', '429', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00222', '413', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00222', '446', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00222', '424', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00222', '419', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00223', '439', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00223', '421', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00223', '393', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00223', '403', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00224', '402', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00224', '418', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00224', '429', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00224', '403', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00225', '441', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00225', '448', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00226', '444', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00226', '410', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00226', '422', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00227', '403', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00228', '415', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00228', '401', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00228', '407', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00229', '415', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00229', '402', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00229', '418', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00229', '432', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00230', '424', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00230', '414', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00231', '402', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00231', '417', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00231', '426', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00232', '448', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00232', '435', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00232', '393', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00233', '432', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00233', '427', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00234', '415', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00234', '405', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00235', '428', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00235', '398', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00235', '437', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00236', '408', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00236', '402', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00236', '420', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00237', '396', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00237', '427', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00238', '430', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00238', '410', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00238', '425', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00238', '434', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00239', '427', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00239', '444', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00239', '413', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00240', '432', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00240', '427', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00241', '470', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00241', '452', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00241', '480', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00242', '498', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00243', '484', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00244', '480', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00245', '469', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00245', '504', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00245', '497', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00246', '495', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00246', '480', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00246', '475', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00246', '494', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00247', '499', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00247', '460', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00247', '489', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00248', '464', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00248', '456', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00248', '451', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00249', '461', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00249', '495', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00249', '483', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00249', '499', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00250', '484', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00250', '454', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00250', '497', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00251', '501', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00251', '472', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00251', '469', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00251', '502', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00252', '498', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00252', '449', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00252', '457', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00253', '491', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00254', '458', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00255', '500', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00255', '463', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00255', '450', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00255', '463', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00256', '502', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00256', '482', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00256', '493', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00257', '500', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00257', '468', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00257', '494', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00258', '459', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00259', '476', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00260', '493', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00261', '487', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00261', '473', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00261', '477', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00262', '500', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00263', '486', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00264', '449', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00264', '462', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00264', '503', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00265', '493', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00265', '468', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00266', '503', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00266', '486', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00267', '471', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00267', '462', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00267', '503', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00268', '462', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00268', '500', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00269', '463', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00269', '452', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00269', '478', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00270', '473', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00270', '479', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00271', '511', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00272', '533', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00272', '511', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00273', '528', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00273', '523', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00273', '541', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00274', '547', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00275', '510', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00275', '553', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00276', '511', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00276', '557', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00277', '544', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00278', '534', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00278', '512', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00278', '505', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00279', '538', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00280', '559', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00280', '544', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00280', '538', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00280', '548', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00281', '555', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00281', '518', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00281', '556', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00282', '524', to_date('12-09-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00283', '543', to_date('12-07-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00284', '518', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00285', '513', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00285', '523', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00285', '546', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00285', '560', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00286', '535', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00286', '523', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00286', '541', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00286', '517', to_date('12-07-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00287', '524', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00287', '552', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00287', '541', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00288', '555', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00289', '521', to_date('12-06-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00290', '540', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00290', '510', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00291', '513', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00292', '533', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00292', '509', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00293', '556', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00293', '524', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00293', '553', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00293', '554', to_date('12-08-2016', 'MM-DD-YYYY'), 3);
INSERT INTO RESERVATION_DETAIL VALUES('00294', '530', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00294', '541', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00294', '518', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00295', '520', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00295', '515', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00295', '534', to_date('12-07-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00296', '537', to_date('12-08-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00297', '535', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00297', '520', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00297', '515', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00298', '553', to_date('12-10-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00299', '512', to_date('12-05-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00299', '508', to_date('12-06-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00299', '539', to_date('12-06-2016', 'MM-DD-YYYY'), 2);
INSERT INTO RESERVATION_DETAIL VALUES('00300', '553', to_date('12-04-2016', 'MM-DD-YYYY'), 0);
INSERT INTO RESERVATION_DETAIL VALUES('00300', '555', to_date('12-05-2016', 'MM-DD-YYYY'), 1);
INSERT INTO RESERVATION_DETAIL VALUES('00300', '516', to_date('12-07-2016', 'MM-DD-YYYY'), 2);

commit;

--Trigger 1
--Changed Requirements Left Us Confused On How To Implement This Trigger With Regards to Airline ID and Roundtrips
CREATE OR REPLACE TRIGGER adjustTicket 
AFTER UPDATE ON Price
REFERENCING NEW AS NEW_PRICE
FOR EACH ROW
	WHEN (NEW_PRICE.high_price <> old.high_price OR NEW_PRICE.low_price <> old.low_price)
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
FOR EACH ROW
	WHEN(to_char((SELECT * FROM Our_Date) + INTERVAL '12' hour, HH24MI) IN (SELECT departure_time FROM Flight))
DECLARE cancel_time CHAR(4);
DECLARE curr_capacity INT;
DECLARE curr_flightNum VARCHAR(3);
DECLARE low_capacity INT;
DECLARE new_type CHAR(4);
BEGIN
	cancel_time := to_char((SELECT * FROM Our_Date) + INTERVAL '12' hour, HH24MI);
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