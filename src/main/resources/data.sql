
delete from `transaction`;
delete from `saving`;
delete from `account`;

insert into `account`(`name`, `dob`, `address`, `idcard`, `username`, `password`, `role`, `sex`) values('KienPT', '1999-07-20', 'ABC', '0123456789', 'admin', '123', 1, 1);