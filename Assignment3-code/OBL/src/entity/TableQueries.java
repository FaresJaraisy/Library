package entity;

/**
 * a class that stores create statements for all the tables in the database so when the system boots it creates a new schema with all the tables needed dynamically.
 */
public class TableQueries {
	private static final String usersQuery = "CREATE TABLE `users` (\r\n" + "  `userID` varchar(10) NOT NULL,\r\n"
			+ "  `firstName` varchar(45) NOT NULL,\r\n" + "  `lastName` varchar(45) NOT NULL,\r\n"
			+ "  `phoneNumber` varchar(10) NOT NULL,\r\n" + "  `membershipNumber` varchar(15) NOT NULL,\r\n"
			+ "  `password` varchar(45) NOT NULL,\r\n" + "  `strikes` int(11) DEFAULT '0',\r\n"
			+ "  `status` enum('Active','Locked','Frozen') CHARACTER SET ascii COLLATE ascii_general_ci DEFAULT 'Active',\r\n"
			+ "  `email` varchar(45) DEFAULT NULL,\r\n" + "  `isLoggedIn` bit(1) DEFAULT NULL,\r\n"
			+ "  PRIMARY KEY (`userID`),\r\n" + "  UNIQUE KEY `librariansID_UNIQUE` (`userID`),\r\n"
			+ "  UNIQUE KEY `membershipNumber_UNIQUE` (`membershipNumber`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String librariansQuery = "CREATE TABLE `librarians` (\r\n"
			+ "  `librarianID` varchar(10) NOT NULL,\r\n" + "  `employeeNumber` varchar(15) NOT NULL,\r\n"
			+ "  `role` varchar(45) DEFAULT NULL,\r\n" + "  `departmentName` varchar(45) DEFAULT NULL,\r\n"
			+ "  PRIMARY KEY (`librarianID`),\r\n" + "  UNIQUE KEY `employeeNumber_UNIQUE` (`employeeNumber`),\r\n"
			+ "  CONSTRAINT `LibrarianID` FOREIGN KEY (`librarianID`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String managersQuery = "CREATE TABLE `managers` (\r\n"
			+ "  `managerID` varchar(10) NOT NULL,\r\n" + "  PRIMARY KEY (`managerID`),\r\n"
			+ "  CONSTRAINT `managerID` FOREIGN KEY (`managerID`) REFERENCES `librarians` (`librarianid`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String booksQuery = "CREATE TABLE `books` (\r\n" + "  `CatalogNumber` varchar(15) NOT NULL,\r\n"
			+ "  `Title` varchar(45) NOT NULL,\r\n" + "  `AuthorName` varchar(45) NOT NULL,\r\n"
			+ "  `publication` varchar(45) NOT NULL,\r\n" + "  `numberOfCopies` int(11) NOT NULL,\r\n"
			+ "  `purchaseDate` date NOT NULL,\r\n" + "  `locationOnShelf` varchar(15) NOT NULL,\r\n"
			+ "  `tableOfContents` longblob,\r\n" + "  `Description` varchar(1000) DEFAULT NULL,\r\n"
			+ "  `type` enum('Wanted','Regular') NOT NULL,\r\n" + "  PRIMARY KEY (`CatalogNumber`),\r\n"
			+ "  UNIQUE KEY `CatalogNumber_UNIQUE` (`CatalogNumber`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String bookcopiesQuery = "CREATE TABLE `bookcopies` (\r\n"
			+ "  `barcode` varchar(10) NOT NULL,\r\n" + "  `catalogNumber` varchar(15) NOT NULL,\r\n"
			+ "  `purchaseDate` date DEFAULT NULL,\r\n"
			+ "  `status` enum('available','reserved','borrowed') DEFAULT 'available',\r\n"
			+ "  PRIMARY KEY (`barcode`,`catalogNumber`),\r\n" + "  KEY `CatalogNumber_idx` (`catalogNumber`),\r\n"
			+ "  CONSTRAINT `copiesCN` FOREIGN KEY (`catalogNumber`) REFERENCES `books` (`catalognumber`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String categoriesQuery = "CREATE TABLE `categories` (\r\n"
			+ "  `categoryName` varchar(45) NOT NULL,\r\n" + "  `catalogNumber` varchar(15) NOT NULL,\r\n"
			+ "  PRIMARY KEY (`categoryName`,`catalogNumber`),\r\n" + "  KEY `CatalogNumber_idx` (`catalogNumber`),\r\n"
			+ "  CONSTRAINT `categoriesCN` FOREIGN KEY (`catalogNumber`) REFERENCES `books` (`catalognumber`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String borrowsQuery = "CREATE TABLE `borrows` (\r\n" + "  `userID` varchar(10) NOT NULL,\r\n"
			+ "  `barcode` varchar(10) NOT NULL,\r\n" + "  `librarianID` varchar(10) NOT NULL,\r\n"
			+ "  `borrowDate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\r\n"
			+ "  `returnDate` datetime DEFAULT NULL,\r\n" + "  `actualReturnDate` datetime DEFAULT NULL,\r\n"
			+ "  `status` enum('Active','LateNotReturned','LateReturned','Returned','Lost') DEFAULT NULL,\r\n"
			+ "  PRIMARY KEY (`userID`,`barcode`,`borrowDate`),\r\n" + "  KEY `borrowedBarcode_idx` (`barcode`),\r\n"
			+ "  KEY `librarianID_idx` (`librarianID`),\r\n"
			+ "  CONSTRAINT `borrowedBarcode` FOREIGN KEY (`barcode`) REFERENCES `bookcopies` (`barcode`) ON DELETE CASCADE ON UPDATE CASCADE,\r\n"
			+ "  CONSTRAINT `borrowedByID` FOREIGN KEY (`librarianID`) REFERENCES `librarians` (`librarianid`) ON DELETE CASCADE ON UPDATE CASCADE,\r\n"
			+ "  CONSTRAINT `borrowerID` FOREIGN KEY (`userID`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String reservationsQuery = "CREATE TABLE `reservations` (\r\n"
			+ "  `userID` varchar(10) NOT NULL,\r\n" + "  `barcode` varchar(10) NOT NULL,\r\n"
			+ "  `reserveDate` datetime NOT NULL,\r\n"
			+ "  `reserveStatus` enum('Pending','Canceled','Closed') DEFAULT NULL,\r\n"
			+ "  PRIMARY KEY (`userID`,`barcode`,`reserveDate`),\r\n"
			+ "  KEY `reservationBarcode_idx` (`barcode`),\r\n"
			+ "  CONSTRAINT `reservationBarcode` FOREIGN KEY (`barcode`) REFERENCES `bookcopies` (`barcode`) ON DELETE CASCADE ON UPDATE CASCADE,\r\n"
			+ "  CONSTRAINT `reserverID` FOREIGN KEY (`userID`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String faultHistoryQuery = "CREATE TABLE `faultshistory` (\r\n"
			+ "  `userID` varchar(10) NOT NULL,\r\n" + "  `faultDesc` enum('LateReturn','Lost') NOT NULL,\r\n"
			+ "  `Date` date NOT NULL,\r\n" + "  PRIMARY KEY (`userID`,`faultDesc`,`Date`),\r\n"
			+ "  CONSTRAINT `fid` FOREIGN KEY (`userID`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String manualDelaysQuery = "CREATE TABLE `manualdelays` (\r\n"
			+ "  `LibrarianID` varchar(10) NOT NULL,\r\n" + "  `date` date NOT NULL,\r\n"
			+ "  `userID` varchar(10) NOT NULL,\r\n" + "  `barcode` varchar(10) NOT NULL,\r\n"
			+ "  `borrowdate` datetime NOT NULL,\r\n"
			+ "  PRIMARY KEY (`LibrarianID`,`date`,`userID`,`barcode`,`borrowdate`),\r\n"
			+ "  KEY `borrowTuple_idx` (`userID`,`barcode`,`borrowdate`),\r\n"
			+ "  CONSTRAINT `borrowTuple` FOREIGN KEY (`userID`, `barcode`, `borrowdate`) REFERENCES `borrows` (`userid`, `barcode`, `borrowdate`) ON DELETE CASCADE ON UPDATE CASCADE,\r\n"
			+ "  CONSTRAINT `delayerID` FOREIGN KEY (`LibrarianID`) REFERENCES `librarians` (`librarianid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String messagesQuery = "CREATE TABLE `messages` (\r\n"
			+ "  `messsageType` enum('view','lock') NOT NULL,\r\n" + "  `title` varchar(45) DEFAULT NULL,\r\n"
			+ "  `msg` varchar(1000) DEFAULT NULL,\r\n" + "  `belong` varchar(10) NOT NULL,\r\n"
			+ "  `messageDate` datetime NOT NULL,\r\n" + "  `user` varchar(10) NOT NULL,\r\n"
			+ "  PRIMARY KEY (`belong`,`messageDate`,`user`,`messsageType`),\r\n" + "  KEY `fk2_idx` (`user`),\r\n"
			+ "  CONSTRAINT `fk1` FOREIGN KEY (`belong`) REFERENCES `librarians` (`librarianid`) ON DELETE CASCADE ON UPDATE CASCADE,\r\n"
			+ "  CONSTRAINT `fk2` FOREIGN KEY (`user`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String periodicalReportsQuery = "CREATE TABLE `periodicalreports` (\r\n"
			+ "  `fileName` blob NOT NULL,\r\n" + "  `startDate` date NOT NULL,\r\n" + "  `endDate` date NOT NULL,\r\n"
			+ "  PRIMARY KEY (`startDate`,`endDate`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	private static final String permissionsQuery = "CREATE TABLE `permissions` (\r\n"
			+ "  `userID` varchar(10) NOT NULL,\r\n" + "  `permission` enum('CanReserve','CanBorrow') NOT NULL,\r\n"
			+ "  PRIMARY KEY (`userID`,`permission`),\r\n"
			+ "  CONSTRAINT `userPermissionID` FOREIGN KEY (`userID`) REFERENCES `users` (`userid`) ON DELETE CASCADE ON UPDATE CASCADE\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\r\n";
	public static final String[] orderOfBuild = { usersQuery, librariansQuery, managersQuery, booksQuery,
			bookcopiesQuery, categoriesQuery, borrowsQuery, reservationsQuery, faultHistoryQuery, manualDelaysQuery,
			messagesQuery, periodicalReportsQuery, permissionsQuery };
}
