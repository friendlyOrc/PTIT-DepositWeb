
create table if not exists `account`(
	id int auto_increment primary key,
    `name` varchar(255) not null,
    dob date not null,
    `address` varchar(255) not null,
    idcard varchar(255) not null,
    sex int not null,
    username varchar(255),
    `password` varchar(255),
    email varchar(255) not null
);

create table if not exists saving(
	id int primary key auto_increment,
    accountid int not null,
    staffid int not null,
    `type` int not null,
    `status` int not null,
    balance float not null, 
    interest float not null,
    `time` int not null,
    createtime date not null,
    foreign key (accountid) references `account`(id),
    foreign key (staffid) references `account`(id)

);

create table if not exists `transaction`(
	id int primary key auto_increment,
    `type` int not null,
    savingid int not null,
    amount float not null
);