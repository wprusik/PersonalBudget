-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2019 at 08:08 PM
-- Server version: 10.1.37-MariaDB
-- PHP Version: 7.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `personal_budget`
--

-- --------------------------------------------------------

--
-- Table structure for table `accounts`
--

CREATE TABLE `accounts` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `account_number` varchar(26) NOT NULL,
  `bank` varchar(40) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `balance` float NOT NULL,
  `currency` varchar(3) NOT NULL,
  `account_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `accounts`
--

INSERT INTO `accounts` (`username`, `account_number`, `bank`, `balance`, `currency`, `account_name`) VALUES
('test', '86578643065942356403625734', 'Bank Billenium', 3697, 'NZD', 'Konto walutowe'),
('test', '97390856749836278538754387', 'Česká spořitelna', 38505, 'CZK', 'Konto zagraniczne'),
('test', '38295687436574397695479674', 'Millenium', 22, 'USD', 'Oszczędnościowe');

-- --------------------------------------------------------

--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `authority` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `authorities`
--

INSERT INTO `authorities` (`username`, `authority`) VALUES
('test', 'ROLE_USER'),
('testNonActivated', 'ROLE_USER');

-- --------------------------------------------------------

--
-- Table structure for table `budgets`
--

CREATE TABLE `budgets` (
  `budget_id` int(255) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `budget_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `purpose` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `amount` float DEFAULT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `budgets`
--

INSERT INTO `budgets` (`budget_id`, `username`, `budget_name`, `purpose`, `amount`, `currency`) VALUES
(9, 'test', 'Budżet wakacyjny', 'Wyprawa do Peru', 16000, 'DKK'),
(11, 'test', 'Przeprowadzka', 'Wydatki związane z przeprowadzką', 4000, 'PLN');

-- --------------------------------------------------------

--
-- Table structure for table `currency`
--

CREATE TABLE `currency` (
  `currency` varchar(3) COLLATE utf8_polish_ci NOT NULL,
  `name` varchar(60) COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;

--
-- Dumping data for table `currency`
--

INSERT INTO `currency` (`currency`, `name`) VALUES
('AUD', 'Australian Dollar'),
('BGN', 'Bulgarian Lev'),
('BRL', 'Brazilian Real'),
('CAD', 'Canadian Dollar'),
('CHF', 'Swiss Franc'),
('CNY', 'Yuan (Chinese) Renminbi'),
('CZK', 'Czech Republic Koruna'),
('DKK', 'Danish Krone'),
('EUR', 'Euro'),
('GBP', 'British Pound'),
('HKD', 'Hong Kong Dollar'),
('HRK', 'Croatian kuna'),
('HUF', 'Hungarian Forint'),
('IDR', 'Indonesian Rupiah'),
('ILS', 'Israeli Shekel'),
('INR', 'Indian Rupee'),
('ISK', 'Icelandic Krona'),
('JPY', 'Japanese Yen'),
('KRW', '(South) Korean Won'),
('MXN', 'Mexican peso'),
('MYR', 'Malaysian Ringgit'),
('NOK', 'Norwegian Kroner'),
('NZD', 'New Zealand Dollar'),
('PHP', 'Philippine Peso'),
('PLN', 'Polish Zloty'),
('RON', 'Romanian Leu'),
('RUB', 'Russian Ruble'),
('SEK', 'Swedish Krona'),
('SGD', 'Singapore Dollar'),
('THB', 'Thai Baht'),
('TRY', 'Turkish Lira'),
('USD', 'US Dollar'),
('ZAR', 'South African rand');

-- --------------------------------------------------------

--
-- Table structure for table `debts`
--

CREATE TABLE `debts` (
  `debt_id` int(11) NOT NULL,
  `debt_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `creditor` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `type` varchar(20) NOT NULL,
  `amount` float NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `debts`
--

INSERT INTO `debts` (`debt_id`, `debt_name`, `creditor`, `type`, `amount`, `username`, `currency`) VALUES
(14, 'Kredyt mieszkaniowy', 'Bank Millenium SA', 'debt', 247000, 'test', 'PLN'),
(15, 'Pożyczka na samochód', 'Mama', 'debt', 4830, 'test', 'PLN'),
(16, 'Dług Daniela', 'Daniel Heczko', 'claim', 3054, 'test', 'CZK'),
(17, 'Pożyczka na naprawę samochodu', 'Krzysiek Kowalski', 'claim', 683, 'test', 'PLN');

-- --------------------------------------------------------

--
-- Table structure for table `expenditures`
--

CREATE TABLE `expenditures` (
  `id` int(255) NOT NULL,
  `budget_id` int(255) DEFAULT NULL,
  `amount` float NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `expenditures`
--

INSERT INTO `expenditures` (`id`, `budget_id`, `amount`, `description`) VALUES
(5, 9, 1500, 'Bilety lotnicze'),
(6, 9, 1700, 'Prowiant'),
(4, 9, 5000, 'Inne wydatki'),
(3, 9, 7000, 'Nocleg'),
(9, 11, 200, 'Paliwo'),
(10, 11, 500, 'Żywność'),
(8, 11, 900, 'Wyposażenie domu'),
(7, 11, 1800, 'Kaucja za mieszkanie');

-- --------------------------------------------------------

--
-- Table structure for table `expenditure_categories`
--

CREATE TABLE `expenditure_categories` (
  `expenditure_type` varchar(30) NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_polish_ci DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `expenditure_categories`
--

INSERT INTO `expenditure_categories` (`expenditure_type`, `description`, `username`) VALUES
('Rozrywka', 'Kino, wycieczki, kupno gadżetów', 'test'),
('Sport', 'Siłownia, rower, wędkarstwo, itp.', 'test'),
('Wydatki związane z samochodem', 'Paliwo, naprawy, ubezpieczenie, itd.', 'test'),
('Żywność', 'Wydatki związane z zakupem żywności', 'test');

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `transaction_id` int(11) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `type` varchar(30) NOT NULL,
  `account_number_from` varchar(26) DEFAULT NULL,
  `account_number_to` varchar(26) DEFAULT NULL,
  `amount` float NOT NULL,
  `datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(60) DEFAULT NULL,
  `expenditure_type` varchar(30) DEFAULT NULL,
  `currency` varchar(3) NOT NULL,
  `debt_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`transaction_id`, `username`, `type`, `account_number_from`, `account_number_to`, `amount`, `datetime`, `description`, `expenditure_type`, `currency`, `debt_id`) VALUES
(83, 'test', 'outgoing', '86578643065942356403625734', '21573647895643985783496587', 550, '2019-07-04 18:45:40', 'Zamówienie nr 01764322', 'Żywność', 'USD', 0),
(84, 'test', 'incoming', '73489654907658940769587654', '97390856749836278538754387', 450, '2019-07-04 18:46:26', 'Przelew środków', NULL, 'USD', 0),
(85, 'test', 'outgoing', '86578643065942356403625734', '63487560436705482357576543', 200, '2019-07-04 18:47:12', 'Opłata za rezerwację biletów', 'Rozrywka', 'USD', 0),
(86, 'test', 'outgoing', '97390856749836278538754387', '21573647895643985783496587', 780, '2019-07-04 18:48:08', 'Opłata za fakturę nr 983', 'Wydatki związane z samochodem', 'CZK', 0),
(92, 'test', 'deposit', NULL, '09809576843879547835476325', 300, '2019-07-04 18:52:09', 'Oszczędności', NULL, 'PLN', 0),
(96, 'test', 'debt', '09809576843879547835476325', NULL, 1500, '2019-08-03 20:00:00', 'V rata kredytu', NULL, 'PLN', 14),
(104, 'test', 'outgoing', '97390856749836278538754387', '93847598634985697438658476', 1200, '2019-06-27 22:00:00', 'Przelew środków', 'Wydatki związane z samochodem', 'CZK', 0),
(105, 'test', 'outgoing', '97390856749836278538754387', '36456354732432546547657658', 600, '2019-03-13 20:24:31', 'Przelew środków', 'Rozrywka', 'CZK', 0),
(110, 'test', 'outgoing', '97390856749836278538754387', '35435439854389568743658764', 300, '2017-02-15 23:00:00', 'Przelew środków', 'Sport', 'CZK', 0),
(111, 'test', 'outgoing', '86578643065942356403625734', '32543654675476738247983748', 40, '2017-03-17 23:00:00', 'Przelew środków', 'Sport', 'USD', 0),
(112, 'test', 'debt', '86578643065942356403625734', NULL, 50, '2019-07-10 07:50:24', 'fdgfdghfd', NULL, 'NZD', 15),
(113, 'test', 'debt', '97390856749836278538754387', NULL, 20, '2019-07-26 09:27:01', 'Archeologia', NULL, 'CZK', 16),
(114, 'test', 'outgoing', '86578643065942356403625734', '15346547658768769876978698', 14, '2019-08-07 20:15:50', 'Archeologia', 'Wydatki związane z samochodem', 'NZD', 0),
(115, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 615, '2019-08-16 20:00:00', '5325346456', NULL, 'CZK', 0),
(116, 'test', 'withdraw', '97390856749836278538754387', NULL, 16, '2019-08-28 20:00:00', 'fdhgfhgfnb', NULL, 'CZK', 0),
(117, 'test', 'withdraw', '86578643065942356403625734', NULL, 15, '2019-09-11 20:00:00', 'asdasd', NULL, 'NZD', 0),
(119, 'test', 'withdraw', '97390856749836278538754387', NULL, 15, '2019-10-17 20:00:00', 'asdasd', NULL, 'CZK', 0),
(122, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 5, '2019-08-08 08:33:19', 'asdasd', 'Sport', 'CZK', 0),
(123, 'test', 'between', '86578643065942356403625734', '38295687436574397695479674', 15, '2019-08-08 08:33:29', '12343534tgfdbfdgb12343534tgfdbfdgb12343534tgfdbfdgb12343534t', NULL, 'NZD', 0),
(124, 'test', 'deposit', NULL, '97390856749836278538754387', 7, '2019-08-08 08:33:36', 'Archeologia', NULL, 'CZK', 0),
(125, 'test', 'withdraw', '86578643065942356403625734', NULL, 4, '2019-08-08 08:33:44', 'asdasd', NULL, 'NZD', 0),
(126, 'test', 'debt', '86578643065942356403625734', NULL, 10, '2019-08-08 08:33:55', 'asdasd', NULL, 'NZD', 16),
(127, 'test', 'debt', '86578643065942356403625734', NULL, 7, '2019-08-08 08:34:06', 'Archeologia', NULL, 'NZD', 17),
(128, 'test', 'between', '97390856749836278538754387', '86578643065942356403625734', 15, '2019-08-30 20:00:00', 'asdasd', NULL, 'CZK', 0),
(129, 'test', 'debt', '86578643065942356403625734', NULL, 15, '2019-09-25 20:00:00', 'Archeologia', NULL, 'NZD', 15),
(130, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 10, '2019-09-19 20:00:00', 'asdasd', 'Rozrywka', 'CZK', 0),
(131, 'test', 'debt', '38295687436574397695479674', NULL, 27, '2019-08-08 08:36:06', 'Archeologia', NULL, 'USD', 16),
(132, 'test', 'outgoing', '86578643065942356403625734', '21573647895643985783496587', 15, '2020-12-22 22:00:00', 'Test transaction', 'Sport', 'NZD', 0),
(133, 'test', 'between', '86578643065942356403625734', '97390856749836278538754387', 5, '2021-03-29 20:00:00', 'Przelew środków', NULL, 'NZD', 0),
(134, 'test', 'withdraw', '38295687436574397695479674', NULL, 10, '2020-05-14 20:00:00', '-', NULL, 'USD', 0),
(135, 'test', 'debt', '97390856749836278538754387', NULL, 50, '2020-12-13 22:00:00', 'Spłata długu', NULL, 'CZK', 15),
(136, 'test', 'outgoing', '97390856749836278538754387', '21573647895643985783496587', 270, '2019-11-26 19:02:36', 'Przykładowy przelew', 'Rozrywka', 'CZK', 0),
(137, 'test', 'incoming', '23543543654765756765765765', '97390856749836278538754387', 1700, '2019-11-26 19:03:08', 'Wypłata', NULL, 'BRL', 0),
(138, 'test', 'debt', '97390856749836278538754387', NULL, 160, '2020-02-08 22:00:00', 'Spłata długu', NULL, 'CZK', 16),
(139, 'test', 'withdraw', '86578643065942356403625734', NULL, 10, '2020-02-10 22:00:00', 'Wypłata gotówki', NULL, 'NZD', 0);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `password` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `email` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`username`, `password`, `enabled`, `email`) VALUES
('test', '$2a$10$4yNtm9Az2ZNnbHb5bhgoK.0n0kJR61z/qZ7tkTOXp/StYKJ3AEqJG', 1, 'test@test.pl'),
('testNonActivated', '$2a$10$VAqxCoze6DiPtfTPdn3qoOlZpRhPAmhcHYGAjDVX7rY45DA9iNepy', 0, 'testNonActivated@test.pl');

-- --------------------------------------------------------

--
-- Table structure for table `users_activation`
--

CREATE TABLE `users_activation` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `activation_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `expiration` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users_activation`
--

INSERT INTO `users_activation` (`username`, `activation_code`, `expiration`) VALUES
('testNonActivated', 'jiodhgifdjopghfdhjoigfhgilfhnloghmdflgmldf', '3020-06-25 00:00:00');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `accounts`
--
ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_number`) USING BTREE,
  ADD KEY `username` (`username`,`bank`,`balance`,`currency`,`account_name`) USING BTREE;

--
-- Indexes for table `authorities`
--
ALTER TABLE `authorities`
  ADD UNIQUE KEY `ix_auth_username` (`username`,`authority`);

--
-- Indexes for table `budgets`
--
ALTER TABLE `budgets`
  ADD PRIMARY KEY (`budget_id`),
  ADD KEY `username` (`username`,`budget_name`,`purpose`),
  ADD KEY `amount` (`amount`),
  ADD KEY `currency` (`currency`);

--
-- Indexes for table `currency`
--
ALTER TABLE `currency`
  ADD PRIMARY KEY (`currency`);

--
-- Indexes for table `debts`
--
ALTER TABLE `debts`
  ADD PRIMARY KEY (`debt_id`),
  ADD KEY `debt_name` (`creditor`,`type`,`amount`) USING BTREE,
  ADD KEY `username` (`username`),
  ADD KEY `debt_name_2` (`debt_name`);

--
-- Indexes for table `expenditures`
--
ALTER TABLE `expenditures`
  ADD PRIMARY KEY (`id`),
  ADD KEY `budget_id` (`budget_id`,`amount`,`description`);

--
-- Indexes for table `expenditure_categories`
--
ALTER TABLE `expenditure_categories`
  ADD PRIMARY KEY (`expenditure_type`),
  ADD KEY `description` (`description`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `type` (`amount`,`username`,`datetime`,`description`),
  ADD KEY `username` (`username`),
  ADD KEY `expenditure_type` (`expenditure_type`),
  ADD KEY `account_number` (`account_number_from`),
  ADD KEY `currency` (`currency`),
  ADD KEY `type_2` (`type`),
  ADD KEY `debt_id` (`debt_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);

--
-- Indexes for table `users_activation`
--
ALTER TABLE `users_activation`
  ADD UNIQUE KEY `username_2` (`username`),
  ADD KEY `username` (`username`,`activation_code`,`expiration`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `budgets`
--
ALTER TABLE `budgets`
  MODIFY `budget_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `debts`
--
ALTER TABLE `debts`
  MODIFY `debt_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `expenditures`
--
ALTER TABLE `expenditures`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=140;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `accounts`
--
ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

--
-- Constraints for table `authorities`
--
ALTER TABLE `authorities`
  ADD CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

--
-- Constraints for table `budgets`
--
ALTER TABLE `budgets`
  ADD CONSTRAINT `budgets_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

--
-- Constraints for table `debts`
--
ALTER TABLE `debts`
  ADD CONSTRAINT `debts_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

--
-- Constraints for table `expenditures`
--
ALTER TABLE `expenditures`
  ADD CONSTRAINT `expenditures_ibfk_1` FOREIGN KEY (`budget_id`) REFERENCES `budgets` (`budget_id`);

--
-- Constraints for table `expenditure_categories`
--
ALTER TABLE `expenditure_categories`
  ADD CONSTRAINT `expenditure_categories_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`),
  ADD CONSTRAINT `transactions_ibfk_3` FOREIGN KEY (`expenditure_type`) REFERENCES `expenditure_categories` (`expenditure_type`);

--
-- Constraints for table `users_activation`
--
ALTER TABLE `users_activation`
  ADD CONSTRAINT `users_activation_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);

DELIMITER $$
--
-- Events
--
CREATE DEFINER=`root`@`localhost` EVENT `Delete expired users_activation, users and authorities` ON SCHEDULE EVERY 5 MINUTE STARTS '2019-11-11 20:00:36' ON COMPLETION NOT PRESERVE ENABLE DO BEGIN

delete from `users_activation` where `users_activation`.`expiration` < NOW();

DELETE FROM `authorities` 
 WHERE `authorities`.`username` IN (SELECT username FROM `users` WHERE `users`.`enabled`=0) AND NOT EXISTS(SELECT NULL FROM `users_activation` WHERE `users_activation`.`username` = username);

DELETE FROM `users` 
 WHERE `users`.`enabled`=0 AND NOT EXISTS(SELECT NULL
                    FROM `users_activation`
                   WHERE `users_activation`.`username` = username);

END$$

DELIMITER ;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
