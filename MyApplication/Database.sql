/*	name: Lexie Haveman
	date: Nov 26 2023
    title: Assignment-4
    purpose: To create a database based on an er diagram that has real life applications.
			 This database is for an insurance company that is scoring drivers based on their driving patterns
*/

-- Create/use database
create database insurance;
use insurance;

-- policy table to hold details of a policy
create table policy (
policynum char(10) not null,
start date not null,
expiry date not null,
streetnum char(10) not null, streetname char(50) not null, city char(50) not null, province char(2) not null, postalcode char(6) not null,
primary key (policynum),
constraint unique_policynum unique policy(policynum));

-- car table to hold details of a car
create table car (
vin char(17) not null,
policynum char(10) not null,
model char(30) not null,
make char(15) not null,
year int not null,
primary key (vin),
foreign key (policynum) references policy(policynum),
constraint unique_vin unique car(vin));

-- person table to hold the details of a customer
create table person (
cid int not null auto_increment,
fname char(35) not null,
lname char(50) not null,
dob date,
primary key (cid),
constraint unique_cid unique person(cid));

-- drive table to hold the details of a drive
create table drive (
did int not null auto_increment,
vin char(17) not null,
cid int not null,
dod date not null,
duration float not null,
score int not null,
primary key (did),
foreign key (vin) references car(vin),
foreign key (cid) references person(cid),
constraint unique_did unique drive(did));

-- associative entity table
create table custaccount (
cid int not null,
policynum char(10) not null,
primary key (cid, policynum),
foreign key (cid) references person(cid),
foreign key (policynum) references policy(policynum),
constraint unique_combo unique custaccount(cid, policynum));

create table authenticate (
username char(30) not null,
password char(50) not null,
cid int not null,
primary key (username),
foreign key cid references person(cid),
constraint unique_user unique authenticate(username));

-- ************************************** INSERT POLICY ************************************************
-- *****************************************************************************************************
-- *****************************************************************************************************
INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('XC7TQ48Q52', '2020-08-20', '2021-08-20', '747', 'Maple St', 'Toronto', 'ON', 'M5G2C3');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('AIV3DZDU6Q', '2021-06-30', '2022-06-30', '8590', 'Oak Rd', 'Vancouver', 'BC', 'V6B1H4');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('EPMWCPRJWR', '2020-06-01', '2021-06-01', '3472', 'Pine Ave', 'Montreal', 'QC', 'H2X2T1');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('5E7FQ0PFRM', '2022-12-12', '2023-12-12', '747', 'Maple St', 'Toronto', 'ON', 'M5G2C3');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('KY927JP6DC', '2021-12-19', '2022-12-19', '6746', 'Birch Rd', 'Ottawa', 'ON', 'K1A0B1');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('POC78GISJU', '2020-09-08', '2021-09-08', '1475', 'Cedar Ave', 'Edmonton', 'AB', 'T5J2L8');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('OHNLJ6BDIT', '2020-07-31', '2021-07-31', '7889', 'Spruce Dr', 'Halifax', 'NS', 'B3H4R2');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('NJ75NKY1GW', '2022-10-10', '2023-10-10', '4006', 'Fir St', 'Winnipeg', 'MB', 'R3C0T1');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('YGASTXKIG7', '2021-05-15', '2022-05-15', '8683', 'Aspen Blvd', 'Victoria', 'BC', 'V8W1W1');

INSERT INTO policy (policynum, start, expiry, streetnum, streetname, city, province, postalcode)
VALUES ('QLHVNZG8WO', '2023-06-08', '2024-06-07', '285', 'Maple St', 'Quebec City', 'QC', 'G1R5P4');


-- ************************************** INSERT POLICY END************************************************
-- ********************************************************************************************************
-- ********************************************************************************************************

-- ************************************** INSERT CUSTACCOUNT ************************************************
-- **********************************************************************************************************
-- **********************************************************************************************************
INSERT INTO person (fname, lname, dob)
VALUES ('Daniel', 'Arnold', '1986-12-26');

INSERT INTO person (fname, lname, dob)
VALUES ('Nancy', 'Bolton', '1972-03-14');

INSERT INTO person (fname, lname, dob)
VALUES ('David', 'Cook', '1975-09-02');

INSERT INTO person (fname, lname, dob)
VALUES ('Jennifer', 'Glover', '1981-06-15');

INSERT INTO person (fname, lname, dob)
VALUES ('Christopher', 'Glover', '2007-09-11');

INSERT INTO person (fname, lname, dob)
VALUES ('John', 'Glover', '2002-06-21');

INSERT INTO person (fname, lname, dob)
VALUES ('James', 'Lloyd', '1976-02-22');

INSERT INTO person (fname, lname, dob)
VALUES ('Paul', 'Lloyd', '2005-10-20');

INSERT INTO person (fname, lname, dob)
VALUES ('Jaime', 'Lloyd', '2002-06-30');

INSERT INTO person (fname, lname, dob)
VALUES ('Seth', 'Miller', '1960-09-07');

INSERT INTO person (fname, lname, dob)
VALUES ('Briana', 'Miller', '2004-09-09');

INSERT INTO person (fname, lname, dob)
VALUES ('John', 'Nguyen', '1943-03-11');

INSERT INTO person (fname, lname, dob)
VALUES ('Aaron', 'Snyder', '1961-07-29');

INSERT INTO person (fname, lname, dob)
VALUES ('Linda', 'Thomas', '1975-11-09');

INSERT INTO person (fname, lname, dob)
VALUES ('James', 'Bradley', '1955-02-21');

-- ************************************** INSERT PERSON END************************************************
-- ********************************************************************************************************
-- ********************************************************************************************************

-- ************************************** INSERT CAR ************************************************
-- **************************************************************************************************
-- **************************************************************************************************
-- Glover Family cars
INSERT INTO car (vin, policynum, model, make, year) VALUES ('BZTQTXKL3OUJ6B8OQ',
'AIV3DZDU6Q', 'Mustang', 'Toyota', 2018);
INSERT INTO car (vin, policynum, model, make, year) VALUES ('FT0RPTA8CSUYFHZ5T',
'AIV3DZDU6Q', 'Corolla', 'Ford', 2000);

-- Lloyd Family cars
INSERT INTO car (vin, policynum, model, make, year) VALUES ('FAMIL1PKJH5Q1FL1D',
'EPMWCPRJWR', 'Camry', 'Honda', 2012);
INSERT INTO car (vin, policynum, model, make, year) VALUES ('OTKKHKR0NYYFO2BJ6',
'EPMWCPRJWR', 'Accord', 'Honda', 2014);

-- Miller
INSERT INTO car (vin, policynum, model, make, year) VALUES ('Z097Q80MFLI1LM2FN',
'XC7TQ48Q52', 'Mustang', 'Nissan', 2010);

-- Miiler
INSERT INTO car (vin, policynum, model, make, year) VALUES ('MCIWPGLXXW7ONOB53',
'5E7FQ0PFRM', 'Accord', 'Nissan', 2022);

-- Nguyen
INSERT INTO car (vin, policynum, model, make, year) VALUES ('1MMRIXZJSVWXUXWHL',
'KY927JP6DC', 'Accord', 'Ford', 2017);

-- Snyder
INSERT INTO car (vin, policynum, model, make, year) VALUES ('MM2RCGYPQVNQUHNCC',
'POC78GISJU', 'Mustang', 'Tesla', 2021);

-- Thomas
INSERT INTO car (vin, policynum, model, make, year) VALUES ('K09K9FZVG4P8HFIEP',
'OHNLJ6BDIT', 'Focus', 'Honda', 2001);

-- Bradley
INSERT INTO car (vin, policynum, model, make, year) VALUES ('BG9D9XADLTNXZ0TEE',
'NJ75NKY1GW', 'Altima', 'Nissan', 2011);

-- Arnold
INSERT INTO car (vin, policynum, model, make, year) VALUES ('04QX004WSSSFGMGFE',
'YGASTXKIG7', 'Civic', 'Tesla', 2015);

-- Bolton/Cook
INSERT INTO car (vin, policynum, model, make, year) VALUES ('KJ2MI8LYJN6PHGBMK',
'QLHVNZG8WO', 'Corolla', 'Ford', 2008);

-- ************************************** INSERT CAR END************************************************
-- *****************************************************************************************************
-- *****************************************************************************************************

-- ************************************** INSERT CUSTACCOUNT ************************************************
-- **********************************************************************************************************
-- **********************************************************************************************************
INSERT INTO custaccount (cid, policynum) VALUES (4, 'AIV3DZDU6Q'); -- Jennifer Glover
INSERT INTO custaccount (cid, policynum) VALUES (5, 'AIV3DZDU6Q'); -- Christopher Glover
INSERT INTO custaccount (cid, policynum) VALUES (6, 'AIV3DZDU6Q'); -- John Glover

INSERT INTO custaccount (cid, policynum) VALUES (7, 'EPMWCPRJWR'); -- James Lloyd
INSERT INTO custaccount (cid, policynum) VALUES (8, 'EPMWCPRJWR'); -- Paul Lloyd
INSERT INTO custaccount (cid, policynum) VALUES (9, 'EPMWCPRJWR'); -- Jaime Lloyd

INSERT INTO custaccount (cid, policynum) VALUES (10, 'XC7TQ48Q52'); -- Seth Miller
INSERT INTO custaccount (cid, policynum) VALUES (11, 'XC7TQ48Q52'); -- Briana Miller
INSERT INTO custaccount (cid, policynum) VALUES (11, '5E7FQ0PFRM'); -- Briana Miller (child's policy)

INSERT INTO custaccount (cid, policynum) VALUES (12, 'KY927JP6DC'); -- John Nguyen

INSERT INTO custaccount (cid, policynum) VALUES (13, 'POC78GISJU'); -- Aaron Snyder

INSERT INTO custaccount (cid, policynum) VALUES (14, 'OHNLJ6BDIT'); -- Linda Thomas

INSERT INTO custaccount (cid, policynum) VALUES (15, 'NJ75NKY1GW'); -- James Bradley

INSERT INTO custaccount (cid, policynum) VALUES (1, 'YGASTXKIG7'); -- Daniel Arnold

INSERT INTO custaccount (cid, policynum) VALUES (2, 'QLHVNZG8WO'); -- Nancy Bolton
INSERT INTO custaccount (cid, policynum) VALUES (3, 'QLHVNZG8WO'); -- David Cook

-- ************************************** INSERT CUSTACCOUNT END ************************************************
-- **************************************************************************************************************
-- **************************************************************************************************************

-- ************************************** INSERT DRIVE ************************************************
-- ****************************************************************************************************
-- ****************************************************************************************************
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-24', 2.49, 934);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-23', 35.64, 726);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-23', 21.59, 761);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 4, '2024-11-23', 21.55, 837);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-27', 5.89, 762);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 4, '2024-11-25', 27.97, 994);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-23', 21.27, 975);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 4, '2024-11-25', 80.28, 898);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 4, '2024-11-26', 4.03, 984);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 5, '2024-11-25', 4.58, 927);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 5, '2024-11-26', 8.32, 681);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 5, '2024-11-25', 19.98, 673);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 5, '2024-11-27', 69.26, 780);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 5, '2024-11-24', 18.71, 977);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 5, '2024-11-25', 19.87, 971);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 5, '2024-11-27', 4.76, 770);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 6, '2024-11-21', 24.69, 616);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 6, '2024-11-21', 12.82, 683);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FT0RPTA8CSUYFHZ5T', 6, '2024-11-21', 6.66, 746);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 6, '2024-11-22', 25.37, 684);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BZTQTXKL3OUJ6B8OQ', 6, '2024-11-27', 20.22, 988);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 7, '2024-11-26', 28.36, 861);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 7, '2024-11-25', 17.52, 860);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 7, '2024-11-23', 20.09, 941);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 7, '2024-11-24', 72.06, 925);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 7, '2024-11-24', 12.85, 686);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 7, '2024-11-25', 20.55, 714);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 7, '2024-11-21', 12.04, 999);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 7, '2024-11-23', 21.12, 641);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 7, '2024-11-25', 25.4, 614);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-23', 20.96, 891);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-25', 5.19, 629);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-24', 70.01, 701);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-27', 13.69, 796);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-25', 34.31, 660);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 8, '2024-11-21', 2.63, 963);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-27', 13.14, 749);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 8, '2024-11-21', 28.85, 621);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 9, '2024-11-27', 20.67, 684);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 9, '2024-11-26', 17.46, 893);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-26', 24.15, 885);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-27', 26.1, 687);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-21', 43.5, 862);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-27', 9.54, 980);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 9, '2024-11-23', 15.08, 617);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-23', 24.06, 881);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('OTKKHKR0NYYFO2BJ6', 9, '2024-11-22', 16.17, 931);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('FAMIL1PKJH5Q1FL1D', 9, '2024-11-21', 6.86, 645);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-26', 19.39, 815);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-24', 17.74, 894);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-21', 14.47, 886);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-27', 23.07, 738);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-23', 16.42, 735);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-21', 12.1, 689);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-22', 19.71, 924);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-25', 22.12, 803);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-22', 9.47, 663);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('Z097Q80MFLI1LM2FN', 10, '2024-11-24', 7.45, 676);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-22', 5.56, 652);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-27', 8.92, 782);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-26', 13.26, 750);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-24', 13.35, 758);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-26', 16.82, 812);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-24', 28.78, 917);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-23', 11.52, 666);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-21', 15.1, 882);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-25', 22.83, 969);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MCIWPGLXXW7ONOB53', 11, '2024-11-23', 58.95, 999);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-27', 41.41, 645);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-27', 7.38, 948);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-24', 28.53, 895);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-22', 10.18, 725);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-27', 5.68, 934);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-26', 18.99, 622);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-26', 16.88, 926);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('1MMRIXZJSVWXUXWHL', 12, '2024-11-24', 6.77, 655);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MM2RCGYPQVNQUHNCC', 13, '2024-11-27', 5.13, 830);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MM2RCGYPQVNQUHNCC', 13, '2024-11-24', 28.16, 976);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MM2RCGYPQVNQUHNCC', 13, '2024-11-24', 4.99, 811);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MM2RCGYPQVNQUHNCC', 13, '2024-11-27', 24.54, 799);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('MM2RCGYPQVNQUHNCC', 13, '2024-11-26', 3.43, 960);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-21', 29.87, 627);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-24', 14.01, 645);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-22', 7.84, 783);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-21', 50.56, 879);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-23', 14.75, 926);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-24', 68.86, 856);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-21', 26.57, 600);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('K09K9FZVG4P8HFIEP', 14, '2024-11-27', 26.37, 693);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-27', 10.03, 896);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-23', 22.1, 783);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-23', 28.16, 944);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-22', 16.71, 680);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-21', 28.81, 847);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('BG9D9XADLTNXZ0TEE', 15, '2024-11-27', 22.83, 968);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-21', 2.07, 934);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-21', 89.44, 911);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-26', 2.45, 938);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-26', 23.54, 797);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-21', 14.91, 748);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-23', 17.25, 699);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-25', 18.77, 793);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-27', 20.13, 737);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('04QX004WSSSFGMGFE', 1, '2024-11-26', 14.94, 771);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-26', 8.15, 716);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-22', 16.07, 835);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-27', 18.15, 660);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-26', 20.47, 726);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-25', 10.64, 604);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 2, '2024-11-25', 22.11, 781);

INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-27', 24.45, 681);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-27', 11.88, 704);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-26', 21.08, 813);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-27', 8.4, 700);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-25', 27.21, 880);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-22', 21.93, 966);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-24', 89.17, 698);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-25', 23.07, 955);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-26', 14.87, 867);
INSERT INTO drive (vin, cid, dod, duration, score) VALUES ('KJ2MI8LYJN6PHGBMK', 3, '2024-11-23', 20.23, 885);

INSERT INTO authenticate (username, password, cid)
VALUES ('admin', '1234', 1);
-- kill the database for repeated testing/debugging
-- drop database insurance;