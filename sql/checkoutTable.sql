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
