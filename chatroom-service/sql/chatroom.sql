create database if not exists chatroom;
use chatroom;

drop table if exists `t_user`;
drop table if exists `t_room`;
drop table if exists `t_room_statistic`;
drop table if exists `t_room_subscription`;
drop table if exists `t_user_subscription_statistic`;

create table `t_user`(
    `id` bigint auto_increment not null,
    `name` varchar(20) not null,
    `nick_name` varchar (50) default '',
    `pwd` varchar(100) default '123456' not null,
    `tel` varchar(20),
    `update_time` TIMESTAMP default current_timestamp on update current_timestamp,
    `create_time` TIMESTAMP default current_timestamp,
    primary key (`id`),
    unique key (`name`)
) engine=innodb default character set utf8mb4;

create table `t_room`(
    `id` bigint auto_increment not null,
    `uid` bigint not null,
    `secret_key` varchar(150) not null,
    `title` varchar(100) default '',
    `status` int not null default 0,
    `description` varchar(100) default '',
    `delete_flag` tinyint default 0,
    `status` tinyint default 0,
    `update_time` TIMESTAMP default current_timestamp on update current_timestamp,
    `create_time` TIMESTAMP default current_timestamp,
    primary key (`id`),
    unique key (`uid`)
)engine=innodb default character set utf8mb4;

-- 房间订阅表
create table `t_room_subscription`(
    `id` bigint unsigned auto_increment not null,
    `uid` bigint not null,
    `level` int unsigned default 0,
    `delete_flag` tinyint not null,
    `create_time` TIMESTAMP default current_timestamp,
    `update_time` TIMESTAMP default current_timestamp on update current_timestamp,
    primary key (`id`)
);

-- 用户在某个房间的统计数据
create table `t_user_subscription_statistic`(
    `id` bigint unsigned auto_increment not null,
    `uid` bigint not null,
    `room_id` bigint not null,
    `message_count` bigint default 0 comment '在进入房间和离开房间之间发送的消息条数',
    `last_entry_time` bigint not null,
    `last_exit_time` bigint,
    `create_time` TIMESTAMP default current_timestamp,
    `update_time` TIMESTAMP default current_timestamp on update current_timestamp,
    primary key (`id`)
);

alter table `t_room` change column `status``status` int not null default 0;
