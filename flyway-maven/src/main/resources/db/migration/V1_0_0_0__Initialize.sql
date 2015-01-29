CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE `PERSON` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) DEFAULT CHARSET=utf8;
