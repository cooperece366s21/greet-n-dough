create table if not exists users
(
    id           varchar(36) primary key not null,
    name         varchar(256)            not null,
);

create table if not exists user_wallet
(
    user_id varchar(36) not null,
    wallet int,
    primary key (user_id, wallet),
    foreign key (user_id) references users (id)
);