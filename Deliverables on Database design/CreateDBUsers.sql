-- author group 105 
-- Brayden Ruch
-- Shubham Sharma
CREATE USER if not exists 'cs363'@'%' IDENTIFIED WITH mysql_native_password BY '363F2020';

GRANT SELECT ON group105.* TO 'cs363'@'%';
GRANT DROP ON group105.* TO 'cs363'@'%';
GRANT CREATE ON group105.* TO 'cs363'@'%';
GRANT INSERT ON group105.* TO 'cs363'@'%';
GRANT DELETE ON group105.* TO 'cs363'@'%';

-- view drop create insert and delete data
