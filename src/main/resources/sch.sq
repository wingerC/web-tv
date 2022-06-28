DROP TABLE IF EXISTS description, movies CASCADE;


CREATE TABLE description (
  id SERIAL PRIMARY KEY NOT NULL,
  title varchar(255) UNIQUE DEFAULT NULL,
  genre varchar(100) DEFAULT NULL,
  director varchar(100) DEFAULT NULL,
  casting varchar(255) DEFAULT NULL,
  info text,
  time varchar(30) DEFAULT NULL,
  src varchar(255)
);



CREATE TABLE movies (
  id varchar(10) PRIMARY KEY NOT NULL,
  date varchar(20) DEFAULT NULL,
  year varchar(10) DEFAULT NULL,
  title varchar(200) DEFAULT NULL,
  ru_title varchar(100) DEFAULT NULL,
  en_title varchar(100) DEFAULT NULL,
  size varchar(15) DEFAULT NULL,
  peers int DEFAULT NULL,
  torrent_link varchar(250) DEFAULT NULL,
  url_page varchar(250) DEFAULT NULL,
  description_id int DEFAULT NULL, 
  CONSTRAINT FK_des FOREIGN KEY (description_id) REFERENCES description (id)
);
