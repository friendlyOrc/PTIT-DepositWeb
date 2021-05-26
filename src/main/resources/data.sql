
delete from `transaction`;
delete from `saving`;
delete from `account`;

insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(1, 'Phạm Trung Kiên', '1999-07-20', 'ABC', '0123456789', 'kienpt', '123', "kienpt@gmail.com", 0);
insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(2, 'LamVM', '1999-07-20', 'ABC', '0123456789', 'lamvm', '123', "lamvm@gmail.com", 1);
insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(3, 'LamNT', '1999-07-20', 'ABC', '0123456789', 'lamnt', '123', "lamnt@gmail.com", 0);
insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, email, `sex`) values(4, 'Phạm Văn Kiên', '1999-07-20', 'ABC', '0123456789', 'kienpv', '123', "kienpv@gmail.com", 0);
insert into `account`(id, `name`, `dob`, `address`, `idcard`, email, `sex`) values(5, 'Khách hàng', '1999-07-20', 'ABC', '0123456789',  "kh1@gmail.com", 1);
insert into `saving`(id, `type`, balance, `status`, interest, `time`, createTime, accountid, staffid) 
values(1, 1, 1000000, 1, 4, 6, '2020-02-02', 5, 2),
(2, 1, 1000000, 1, 4, 6, '2020-02-02', 2, 1),
(3, 1, 1000000, 0, 4, 6, '2020-02-02', 2, 1);