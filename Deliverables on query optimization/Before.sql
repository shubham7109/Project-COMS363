-- author group 105 
-- Brayden Ruch
-- Shubham Sharma
use group105;
DROP PROCEDURE if exists q3;
DELIMITER &&

CREATE PROCEDURE q3(IN k INT, IN `year` year)
BEGIN
	SELECT count(distinct state) as statenum,
		GROUP_CONCAT(distinct state
		order by state asc
        SEPARATOR ',') as states,
        hastags.name as hashtag
	FROM users
	join posts on posts.screen_name=users.screen_name 
    join tweets on tweets.tid = posts.tid
	join hastags on tweets.tid = hastags.tid
    where year(tweets.createdTime) = `year` and state!='na'
    group by  hastags.name
    order by statenum desc
    LIMIT k;
END &&

DELIMITER ;



DROP PROCEDURE if exists q7;
DELIMITER &&

CREATE PROCEDURE q7(IN hashtag varchar(280), IN state_name varchar(2), IN k int, IN month int, IN `year` year)
BEGIN
	SELECT count(distinct tweets.tid) as tweet_count, screen_name, category 
    from tweets
    join users on users.screen_name = tweets.posted_user
    join hastags on tweets.tid = hastags.tid
    where users.state like state_name 
		and hastags.`name` like hashtag
        and year(createdTime) = `year`
        and month(createdTime) = month
    group by screen_name
    order by tweet_count desc
    limit k
    ;
END &&
DELIMITER ;




DROP PROCEDURE if exists q9;
DELIMITER &&

CREATE PROCEDURE q9(IN category varchar(50), IN k int)
BEGIN
	SELECT screen_name, subcategory, numFollowers
    FROM users
    where users.subcategory like category
    order by numFollowers desc
    limit k
    ;
END &&
DELIMITER ;



DROP PROCEDURE if exists q16;
 DELIMITER &&

CREATE PROCEDURE q16(IN k int, IN month int, IN `year` year)
BEGIN
	SELECT screen_name as user_name, category, `text` as texts, retweet_count as retweetCt, address
    FROM tweets
    join hasurls on hasurls.tid = tweets.tid
    join users on users.screen_name = tweets.posted_user
    where month(createdTime) = month and year(createdTime) = `year`
    order by retweetCt desc
    limit k
    ;
 END &&
DELIMITER ;



DROP PROCEDURE if exists q18;
DELIMITER &&

CREATE PROCEDURE q18(IN k int, IN sub_category varchar(50), IN month int, IN `year` year)
BEGIN
	SELECT mentions.screen_name as mentionedUser, u2.state as mentionedUserState, 
		GROUP_CONCAT(distinct posted_user
		order by posted_user asc
        SEPARATOR ',') as postingUsers
	FROM tweets
    join users on tweets.posted_user = users.screen_name
    join mentions on mentions.tid = tweets.tid
    join users as u2 on u2.screen_name = mentions.screen_name
    -- join users on tweets.posted_user = users.screen_name
    where users.`subcategory` = `sub_category`
		and year(createdTime) = `year`
        and month(createdTime) = month
	group by mentionedUser
    order by count(tweets.tid) desc
    limit k;
END &&
DELIMITER ;




DROP PROCEDURE if exists q23;
DELIMITER &&

CREATE PROCEDURE q23(IN k int, IN sub_category varchar(50), IN month varchar(28), IN `year` year)
BEGIN
	SELECT hastags.name, count(hastags.name) as num_uses
    from hastags
    join tweets on tweets.tid = hastags.tid
    join users on users.screen_name = tweets.posted_user
    where users.subcategory = sub_category
		and year(createdTime) = `year`
        and find_in_set(month(createdTime), month)
    group by `name`
    order by num_uses desc
    limit k
    ;
END &&
DELIMITER ;

CALL q3(5, 2016);
call q7('GOPdebate', 'NC',5, 2, 2016);
call q9('DEMOCRAT',5);
call q16(5,2,2016);
call q18(5, 'GOP', 1, 2016);
call q23(5,'GOP','1,2,3',2016);
