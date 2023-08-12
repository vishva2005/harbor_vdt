create sequence USERS_TBL_SEQ;

create table users_tbl(
    id      int not null primary key auto_increment,
    name    varchar(255) not null,
    email   varchar(255) not null,
    unique(email)
);