CREATE TABLE `employee` (
  `LName` varchar(255) NOT NULL,
  `FName` varchar(255) NOT NULL,
  `Dept` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `E_ID` int(11) NOT NULL,
  PRIMARY KEY (`E_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
