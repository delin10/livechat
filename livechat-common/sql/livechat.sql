create database if not exists livechat;

use livechat;

drop table if exists `t_user`;
drop table if exists `t_room`;
drop table if exists `t_room_statistic`;
drop table if exists `t_room_subscription`;
drop table if exists `t_user_subscription_statistic`;
drop table if exists `t_user_log`;
drop table if exists `t_room_tag`;
drop table if exists `t_tag`;

create table `t_user`(
    `id` bigint unsigned auto_increment not null comment 'id',
    `username` varchar(20) not null comment '用户名',
    `nickname` varchar (50) default '' comment '昵称',
    `pwd` varchar(100) default '' not null comment '密码',
    `tel` varchar(20) default '' not null comment '电话号码',
    `actual_name` varchar(50) default '' not null comment '真实姓名',
    `identifier_id` varchar(30) default '' not null comment '身份证号码',
    `head_img` varchar(512) default '' not null comment '头像地址',
    `update_time` TIMESTAMP default current_timestamp on update current_timestamp comment '更新时间',
    `create_time` TIMESTAMP default current_timestamp comment '创建时间',
    primary key (`id`),
    unique key `name_unique_index`(`username`)
)engine=innodb default character set utf8mb4 comment '用户表';

create table `t_tag`(
    `id` bigint unsigned auto_increment not null comment '自增id',
    `word` varchar(32) default '' not null comment '标签名',
    `delete_flag` tinyint default 0 not null comment '删除标识',
    `update_time` TIMESTAMP default current_timestamp not null on update current_timestamp comment '更新时间',
    `create_time` TIMESTAMP default current_timestamp not null comment '创建时间',
    primary key (`id`)
)engine=innodb default character set utf8mb4 comment '标签';

create table `t_room`(
    `id` bigint unsigned auto_increment not null comment '自增id',
    `uid` bigint unsigned not null comment '用户id',
    `secret_key` varchar(150) not null comment '推流密钥',
    `title` varchar(100) default '' not null comment '房间标题',
    `description` varchar(100) default '' not null  comment '房间描述',
    `delete_flag` tinyint default 0 not null comment '删除标帜：0-未删除；1-已删除',
    `status` tinyint default 0 not null comment '房间状态：0-离线 1-在线',
    `cover` varchar(512) not null default '' comment '封面',
    `update_time` TIMESTAMP default current_timestamp not null on update current_timestamp comment '更新时间',
    `create_time` TIMESTAMP default current_timestamp not null comment '创建时间',
    primary key (`id`),
    unique key `uid_unique_index`(`uid`)
)engine=innodb default character set utf8mb4 comment '房间表';

-- 房间订阅表
create table `t_room_subscription`(
    `id` bigint unsigned auto_increment not null comment '自增id',
    `room_id` bigint unsigned not null default 0 comment '房间id',
    `uid` bigint unsigned default 0 not null comment '用户id',
    `level` int unsigned default 0 not null comment '订阅的房间号等级',
    `watch_time` bigint unsigned default 0 comment '观看总时长，单位ms',
    `delete_flag` tinyint default 0 comment '删除标识：0-正常 1-已删除',
    `create_time` TIMESTAMP default current_timestamp not null comment '创建时间',
    `update_time` TIMESTAMP default current_timestamp not null on update current_timestamp comment '更新时间',
    primary key (`id`),
    unique key `room_id_uid_unique_index`(`room_id`, `uid`),
    key `room_id_mul_index`(`room_id`),
    key `uid_mul_index`(`uid`)
)engine=innodb default character set utf8mb4 comment '房间订阅';

-- 用户行为日志
create table `t_user_log`(
    `id` bigint unsigned auto_increment not null comment '自增id',
    `uid` bigint unsigned not null default 0 comment '用户id',
    `event_code` tinyint not null comment '用户的事件code, 0-进入房间 1-离开房间 2-订阅 3-取消订阅 4-认证 5-登录 6-注册 7-开播 8-下播 -1 - 其它',
    `ip` varchar (30) not null default '' comment '操作的ip地址',
    `remark` varchar(1024) not null default '' comment '备注',
    `create_time` TIMESTAMP default current_timestamp not null comment '创建时间',
    primary key (`id`),
    key uid_mul_key(`uid`)
)engine=innodb default character set utf8mb4 comment '用户行为日志';

-- 房间标签映射表
create table `t_room_tag`(
    `id` bigint unsigned auto_increment not null comment '自增id',
    `room_id` bigint unsigned not null default 0 comment '房间id',
    `tag_id` bigint unsigned not null default 0 comment '标签id',
    `create_time` TIMESTAMP default current_timestamp not null comment '创建时间',
    primary key (`id`),
    unique key `room_id_tag_id_unique_index`(`room_id`, `tag_id`),
    key `tag_id_mul_index`(`tag_id`),
    key `room_id_mul_index`(`room_id`)
)engine=innodb default character set utf8mb4 comment '房间标签映射';

insert into `t_tag`(`word`) values ('游戏');
insert into `t_tag`(`word`) values ('户外');
insert into `t_tag`(`word`) values ('美女');
insert into `t_tag`(`word`) values ('才艺');
insert into `t_tag`(`word`) values ('教学');