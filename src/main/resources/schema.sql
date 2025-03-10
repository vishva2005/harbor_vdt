create sequence USERS_TBL_SEQ;

create table users_tbl(
    id      int not null primary key auto_increment,
    name    varchar(255) not null,
    email   varchar(255) not null,
    unique(email)
);

create sequence schedule_tbl_seq;

create table schedule_tbl(
    id      int not null primary key auto_increment,
    user_id int not null,
    name    varchar(255) not null,
    timezone   varchar(255) not null,
    description text,
    foreign key (user_id) references users_tbl(id),
    unique(user_id, name)
);

create sequence availability_tbl_seq;

create table availability_tbl(
    id                          int not null primary key auto_increment,
    schedule_id                 int not null,
    weekday                     varchar(50),
    start_time_in_sec           int,
    duration_in_sec             int,
    start_date_time_in_epoch    int,
    end_date_time_in_epoch      int,
    is_available                boolean not null default true,
    foreign key (schedule_id) references schedule_tbl(id)
);

create sequence booking_slot_tbl_seq;
create table booking_slot_tbl(
    id                          int not null primary key auto_increment,
    user_id                     int not null,
    guest_email                 varchar(255) not null,
    guest_name                  varchar(255) not null,
    description                 text not null,
    start_date_time_in_epoch    int not null,
    end_date_time_in_epoch      int not null,
    foreign key (user_id) references users_tbl(id)
);