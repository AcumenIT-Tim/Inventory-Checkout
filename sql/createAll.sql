CREATE DATABASE `inventory` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
CREATE TABLE `checkout` (
  `E_ID` int(11) DEFAULT NULL,
  `D_ID` int(11) NOT NULL,
  `opTime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`D_ID`),
  UNIQUE KEY `D_ID_UNIQUE` (`D_ID`),
  KEY `checkout_ibfk_1` (`E_ID`),
  CONSTRAINT `checkout_ibfk_1` FOREIGN KEY (`E_ID`) REFERENCES `employee` (`E_ID`),
  CONSTRAINT `checkout_ibfk_2` FOREIGN KEY (`D_ID`) REFERENCES `device` (`D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `device` (
  `D_ID` int(11) NOT NULL AUTO_INCREMENT,
  `DType` varchar(255) DEFAULT NULL,
  `Make` varchar(255) DEFAULT NULL,
  `Model` varchar(255) DEFAULT NULL,
  `SN` varchar(255) DEFAULT NULL,
  `Stag` varchar(255) DEFAULT NULL,
  `Mac` varchar(255) DEFAULT NULL,
  `Checkout` varchar(3) DEFAULT 'No',
  `Notes` text,
  PRIMARY KEY (`D_ID`),
  UNIQUE KEY `SN_UNIQUE` (`SN`),
  UNIQUE KEY `Stag_UNIQUE` (`Stag`),
  UNIQUE KEY `Mac_UNIQUE` (`Mac`)
) ENGINE=InnoDB AUTO_INCREMENT=1092 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
CREATE TABLE `employee` (
  `LName` varchar(255) NOT NULL,
  `FName` varchar(255) NOT NULL,
  `Dept` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `E_ID` int(11) NOT NULL,
  PRIMARY KEY (`E_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
