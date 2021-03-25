
delete from `transaction`;
delete from `saving`;
delete from `account`;

insert into `account`(id, `name`, `dob`, `address`, `idcard`, `username`, `password`, `role`, `sex`) values(1, 'KienPT', '1999-07-20', 'ABC', '0123456789', 'admin', '123', 1, 1);
insert into `saving`(balance, `status`, interest, `time`, createTime, accountid) values(1000000, 1, 1.5, 6, '2020-02-02', 1);