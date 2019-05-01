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
