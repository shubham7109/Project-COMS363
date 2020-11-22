-- author group 105 
-- Brayden Ruch
-- Shubham Sharma


DROP SCHEMA IF EXISTS `group105`;
CREATE SCHEMA `group105`;
USE `group105`;

CREATE TABLE `tweets` (
	`tid` bigint not null ,
    `retweet_count` int(11),
	`text` text,
	`createdTime` datetime DEFAULT NULL,
    `posted_user` varchar(15) not null,
    primary key(`tid`)
);

CREATE TABLE `hashtags` (
	`name` varchar(280) not null,
    primary key(`name`)
);

CREATE TABLE `urls` (
	`address` varchar(500) not null,
    primary key(`address`)
);

CREATE TABLE `users` (
	`name` varchar(50),
    `screen_name` varchar(16) not null,
    `numFollowers` int not null,
    `numFollowing` int not null,
    `category` varchar(50),
    `subcategory` varchar(50),
    `state` varchar(21),
    primary key(`screen_name`)
);

CREATE TABLE `mentions` (
	`tid` bigint not null,
    `screen_name` varchar(15) not null,
    primary key(`tid`,`screen_name`),
    foreign key(`screen_name`) references `users`(`screen_name`) ON DELETE restrict,
    foreign key(`tid`) references `tweets`(`tid`) ON DELETE restrict
);

CREATE TABLE `hasUrls` (
	`tid` bigint not null,
	`address` varchar(500) not null,
    primary key(`tid`, `address`),
    foreign key(`tid`) references `tweets`(`tid`) ON DELETE restrict,
    foreign key(`address`) references `urls`(`address`) ON DELETE restrict
);

CREATE TABLE `posts` (
	`tid` bigint not null,
    `screen_name` varchar(16) not null,
    primary key(`tid`,`screen_name`),
    foreign key(`screen_name`) references `users`(`screen_name`) ON DELETE restrict,
    foreign key(`tid`) references `tweets`(`tid`) ON DELETE restrict
);

CREATE TABLE `hasTags` (
	`tid` bigint not null,
	`name` varchar(500) not null,
    primary key(`name`,`tid`),
    foreign key (`tid`) references `tweets`(`tid`) ON DELETE restrict,
    foreign key(`name`) references `hashtags`(`name`) ON DELETE RESTRICT
);





