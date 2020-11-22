-- author group 105 
-- Brayden Ruch
-- Shubham Sharma
use group105;
LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tweets.csv' REPLACE INTO TABLE `tweets`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tid,`text`,retweet_count,@col4,createdTime,posted_user);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/user.csv' REPLACE INTO TABLE `users`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(screen_name,`name`,subcategory,category,state,numFollowers,numFollowing);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/mentioned.csv' REPLACE INTO TABLE `mentions`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tid,screen_name);



LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tweets.csv' REPLACE INTO TABLE `posts`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tid,@col2,@col3,@col4,@col5,screen_name);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tagged.csv' REPLACE INTO TABLE `hashtags`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@col1,`name`);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/tagged.csv' REPLACE INTO TABLE `hastags`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tid,`name`);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/urlused.csv' REPLACE INTO TABLE `urls`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@col1,address);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/urlused.csv' REPLACE INTO TABLE `hasurls`
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'
IGNORE 1 LINES
(tid,address);
