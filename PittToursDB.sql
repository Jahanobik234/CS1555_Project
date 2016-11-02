--Pitt Tours Database
--Jonathan Hanobik and Seth Stayer
--CS1555, Fall 2016

--Airline
CREATE OR REPLACE TABLE Airline (
	airline_id VARCHAR(5),
	airline_name VARCHAR(50),
	airline_abbreviation VARCHAR(20),
	year_founded int,
	CONSTRAINT PK_Airline PRIMARY KEY(airline_id)
);

--Flight Schedule
CREATE OR REPLACE TABLE Flight (
	flight_number VARCHAR(3),
	plane_type CHAR(4),
	departure_city VARCHAR(3),
	arrival_city VARCHAR(3),
	departure_time VARCHAR(4),
	arrival_time VARCHAR(4),
	weekly_schedule VARCHAR(7),
	CONSTRAINT PK_Flight PRIMARY KEY(flight_number),
	CONSTRAINT FK1_Flight FOREIGN KEY(plane_type) REFERENCES Plane(plane_type),
	CONSTRAINT FK2_Flight FOREIGN KEY(airline_id) REFERENCES Airline(airline_id)
);

--Plane
CREATE OR REPLACE TABLE Plane (
	plane_type CHAR(4),
	manufacture VARCHAR(10),
	plane_capacity INT,
	last_service DATE,
	year INT,
	owner_id VARCHAR(5),
	CONSTRAINT PK_Plane PRIMARY KEY(plane_type),
	CONSTRAINT FK_Plane FOREIGN KEY(owner_id) REFERENCES Airline(airline_id)
);