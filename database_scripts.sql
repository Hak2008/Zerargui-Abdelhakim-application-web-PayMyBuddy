
| paymybuddy | CREATE DATABASE `paymybuddy`


    | user  | CREATE TABLE `user` (
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `email` varchar(255) NOT NULL,
                                      `password` varchar(255) NOT NULL,
                                      `firstName` varchar(255) NOT NULL,
                                      `lastName` varchar(255) NOT NULL,
                                      `dateOfBirth` date DEFAULT NULL,
                                      `address` varchar(255) DEFAULT NULL,
                                      `phoneNumber` varchar(20) DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `email` (`email`)
              )

    | bankaccount | CREATE TABLE `bankaccount` (
                                                   `accountNumber` varchar(20) NOT NULL,
                                                   `balance` decimal(10,4) NOT NULL,
                                                   `user_id` int DEFAULT NULL,
                                                   PRIMARY KEY (`accountNumber`),
                                                   KEY `user_id` (`user_id`),
                                                   CONSTRAINT `bankaccount_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
                    )

    | friendship | CREATE TABLE `friendship` (
                                                 `id` int NOT NULL AUTO_INCREMENT,
                                                 `user_id` int DEFAULT NULL,
                                                 `friend_id` int DEFAULT NULL,
                                                 PRIMARY KEY (`id`),
                                                 KEY `user_id` (`user_id`),
                                                 KEY `friend_id` (`friend_id`),
                                                 CONSTRAINT `friendship_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                                 CONSTRAINT `friendship_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `user` (`id`)
                   )

    | transaction | CREATE TABLE `transaction` (
                                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                                   `sender_accountNumber` varchar(20) DEFAULT NULL,
                                                   `receiver_accountNumber` varchar(20) DEFAULT NULL,
                                                   `amount` decimal(38,2) NOT NULL,
                                                   `fee` double NOT NULL,
                                                   `totalAmount` decimal(10,4) NOT NULL,
                                                   `date` timestamp NOT NULL,
                                                   `status` varchar(255) NOT NULL,
                                                   `paymentReason` varchar(255) DEFAULT NULL,
                                                   PRIMARY KEY (`id`),
                                                   KEY `sender_accountNumber` (`sender_accountNumber`),
                                                   KEY `receiver_accountNumber` (`receiver_accountNumber`),
                                                   CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`sender_accountNumber`) REFERENCES `bankaccount` (`accountNumber`),
                                                   CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`receiver_accountNumber`) REFERENCES `bankaccount` (`accountNumber`)
                    )
