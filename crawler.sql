-- create database if not exists crawler character set utf8;
-- use crawler;

create table if not exists cer_bill(
  id int primary key auto_increment,
  tid varchar(10) not null,
  timestamp timestamp,
  job_url varchar(80),
  job_name varchar(100),
  company_url varchar(80),
  company_name varchar(100)
);

create table if not exists cer_job(
  id int primary key auto_increment,
  uid int not null,
  timestamp timestamp,
  name varchar(100),
  date datetime,
  education varchar(9),
  language varchar(9),
  welfare varchar(50),
  about text,
  address varchar(80)
);

create table if not exists cer_company(
  id int primary key auto_increment,
  uid int not null,
  timestamp timestamp,
  name varchar(100),
  nature varchar(30),
  scale varchar(20),
  welfare varchar(50),
  about text,
  address varchar(80),
  website varchar(80)
);