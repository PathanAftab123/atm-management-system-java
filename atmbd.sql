create database atmdb;

use atmdb;

create table accounts(
id int primary key auto_increment,
name varchar(50),
balance double,
pin int
);

create table transactions(
id int primary key auto_increment,
account_id int,
type varchar(30),
amount double,
date timestamp default current_timestamp
);
select * from accounts;
select * from transactions;