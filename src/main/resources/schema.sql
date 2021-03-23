
create table if not exists `account`(
	id int auto_increment primary key,
    `name` varchar(255) not null,
    dob date not null,
    `address` varchar(255) not null,
    idcard varchar(255) not null,
    sex int not null,
    username varchar(255) not null,
    `password` varchar(255) not null,
    `role` int not null
);

create table if not exists saving(
	id int primary key auto_increment,
    accountid int not null,
    `type` int not null,
    balance float not null, 
    interest float not null,
    foreign key saving(accountid) references `account`(id)
);

create table if not exists `transaction`(
	id int primary key auto_increment,
    `type` int not null,
    savingid int not null,
    amount float not null
);