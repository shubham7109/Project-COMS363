-- author group 105 
-- Brayden Ruch
-- Shubham Sharma

SET GLOBAL innodb_buffer_pool_size=2147483648; -- 2GB q23 and q3

CREATE index tid on hastags (tid); -- for q7

CREATE index subcat on users(subcategory);

ALTER TABLE tweets ADD column DateMonth int;
update tweets set DateMonth = month(createdTime);
create index months on tweets(DateMonth);

ALTER TABLE tweets ADD column DateYear int;
update tweets set DateYear = year(createdTime);
create index years on tweets(DateYear);
