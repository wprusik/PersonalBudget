SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;



CREATE DATABASE `personal_budget`;
USE `personal_budget`;



CREATE TABLE `accounts` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `account_number` varchar(26) NOT NULL,
  `bank` varchar(40) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `balance` float NOT NULL,
  `currency` varchar(3) NOT NULL,
  `account_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `accounts` (`username`, `account_number`, `bank`, `balance`, `currency`, `account_name`) VALUES
('test', '86578643065942356403625734', 'Bank Millenium', 3716, 'USD', 'Konto walutowe'),
('test', '97390856749836278538754387', 'Česká spořitelna', 30059, 'CZK', 'Konto zagraniczne'),
('test', '12345678901234567890123456', 'PKO BP', 380, 'PLN', 'Konto osobiste'),
('test', '09809576843879547835476325', 'Raiffeisen Polbank SA', 17120, 'PLN', 'Konto oszczędnościowe');



CREATE TABLE `authorities` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `authority` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `authorities` (`username`, `authority`) VALUES
('test', 'ROLE_USER');



CREATE TABLE `budgets` (
  `budget_id` int(255) NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `budget_name` varchar(30) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `purpose` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `amount` float DEFAULT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `budgets` (`budget_id`, `username`, `budget_name`, `purpose`, `amount`, `currency`) VALUES
(9, 'test', 'Budżet wakacyjny', 'Wyprawa do Peru', 16000, 'PLN'),
(11, 'test', 'Przeprowadzka', 'Wydatki związane z przeprowadzką', 4000, 'PLN');



CREATE TABLE `currency` (
  `currency` varchar(3) COLLATE utf8_polish_ci NOT NULL,
  `name` varchar(60) COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;



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



CREATE TABLE `debts` (
  `debt_id` int(11) NOT NULL,
  `debt_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `creditor` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `type` varchar(20) NOT NULL,
  `amount` float NOT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `debts` (`debt_id`, `debt_name`, `creditor`, `type`, `amount`, `username`, `currency`) VALUES
(14, 'Kredyt mieszkaniowy', 'Bank Millenium SA', 'debt', 247000, 'test', 'PLN'),
(15, 'Pożyczka na samochód', 'Mama', 'debt', 5000, 'test', 'PLN'),
(16, 'Dług Daniela', 'Daniel Heczko', 'claim', 4000, 'test', 'CZK'),
(17, 'Pożyczka na naprawę samochodu', 'Krzysiek Kowalski', 'claim', 700, 'test', 'PLN');



CREATE TABLE `expenditures` (
  `id` int(255) NOT NULL,
  `budget_id` int(255) DEFAULT NULL,
  `amount` float NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `expenditures` (`id`, `budget_id`, `amount`, `description`) VALUES
(40, 9, 1000, 'Zakupy na podróż'),
(37, 9, 1500, 'Bilety lotnicze'),
(39, 9, 2000, 'Organizacja wycieczki krajoznawczej'),
(38, 9, 8000, 'Hotel');



CREATE TABLE `expenditure_categories` (
  `expenditure_type` varchar(30) NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_polish_ci DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `expenditure_categories` (`expenditure_type`, `description`, `username`) VALUES
('Rozrywka', 'Kino, wycieczki, kupno gadżetów', 'test'),
('Sport', 'Siłownia, rower, wędkarstwo, itp.', 'test'),
('Wydatki związane z samochodem', 'Paliwo, naprawy, ubezpieczenie, itd.', 'test'),
('Żywność', 'Wydatki związane z zakupem żywności', 'test');



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



INSERT INTO `transactions` (`transaction_id`, `username`, `type`, `account_number_from`, `account_number_to`, `amount`, `datetime`, `description`, `expenditure_type`, `currency`, `debt_id`) VALUES
(82, 'test', 'between', '12345678901234567890123456', '86578643065942356403625734', 400, '2019-07-04 18:22:12', 'Przelew środków', NULL, 'PLN', 0),
(83, 'test', 'outgoing', '86578643065942356403625734', '21573647895643985783496587', 550, '2019-07-04 18:45:40', 'Zamówienie nr 01764322', 'Żywność', 'USD', 0),
(84, 'test', 'incoming', '73489654907658940769587654', '97390856749836278538754387', 450, '2019-07-04 18:46:26', 'Przelew środków', NULL, 'USD', 0),
(85, 'test', 'outgoing', '86578643065942356403625734', '63487560436705482357576543', 200, '2019-07-04 18:47:12', 'Opłata za rezerwację biletów', 'Rozrywka', 'USD', 0),
(86, 'test', 'outgoing', '97390856749836278538754387', '21573647895643985783496587', 780, '2019-07-04 18:48:08', 'Opłata za fakturę nr 983', 'Wydatki związane z samochodem', 'CZK', 0),
(87, 'test', 'outgoing', '09809576843879547835476325', '63487560436705482357576524', 80, '2019-07-04 18:48:52', 'Karnet na siłownię nr 157', 'Sport', 'PLN', 0),
(88, 'test', 'outgoing', '12345678901234567890123456', '86348756436595475934687354', 990, '2019-07-04 18:49:49', 'Przelew za telefon OLX', 'Rozrywka', 'PLN', 0),
(89, 'test', 'outgoing', '12345678901234567890123456', '64736758463985674396795436', 80, '2019-07-04 18:50:28', 'Przelew OLX', 'Rozrywka', 'PLN', 0),
(90, 'test', 'outgoing', '12345678901234567890123456', '86986877567235464532637242', 900, '2019-07-04 18:51:21', 'Rower - Allegro', 'Sport', 'PLN', 0),
(91, 'test', 'outgoing', '86578643065942356403625734', '21543543645623437657868769', 140, '2019-07-04 18:51:50', 'Paliwo', 'Wydatki związane z samochodem', 'USD', 0),
(92, 'test', 'deposit', NULL, '09809576843879547835476325', 300, '2019-07-04 18:52:09', 'Oszczędności', NULL, 'PLN', 0),
(93, 'test', 'withdraw', '12345678901234567890123456', NULL, 50, '2019-07-04 18:52:23', 'Wypłata z bankomatu', NULL, 'PLN', 0),
(94, 'test', 'debt', '12345678901234567890123456', NULL, 1500, '2019-07-04 18:56:26', 'IV rata kredytu', NULL, 'PLN', 14),
(95, 'test', 'between', '12345678901234567890123456', '09809576843879547835476325', 500, '2019-07-10 20:00:00', 'Przelew środków', NULL, 'PLN', 0),
(96, 'test', 'debt', '09809576843879547835476325', NULL, 1500, '2019-08-03 20:00:00', 'V rata kredytu', NULL, 'PLN', 14),
(97, 'test', 'outgoing', '12345678901234567890123456', '63487560436705482357576525', 200, '2019-07-16 20:00:00', 'Przelew środków', NULL, 'PLN', 0),
(98, 'test', 'outgoing', '97390856749836278538754387', '21573647895643985783496587', 300, '2019-07-10 20:00:00', 'Przelew środków', NULL, 'CZK', 0),
(99, 'test', 'outgoing', '09809576843879547835476325', '74357947589476949837685787', 100, '2019-07-04 19:07:17', 'Przelew za zakupy', 'Żywność', 'PLN', 0),
(101, 'test', 'outgoing', '09809576843879547835476325', '34u89674589674587604579867', 70, '2019-07-04 21:29:19', 'Zestaw do sushi', 'Żywność', 'PLN', 0),
(102, 'test', 'outgoing', '12345678901234567890123456', '78326487365874376543757343', 55, '2019-04-12 22:00:00', 'Przelew środków', 'Wydatki związane z samochodem', 'PLN', 0),
(103, 'test', 'outgoing', '09809576843879547835476325', '78326487365874376543757343', 300, '2019-05-08 22:00:00', 'Przelew środków', 'Wydatki związane z samochodem', 'PLN', 0),
(104, 'test', 'outgoing', '97390856749836278538754387', '93847598634985697438658476', 1200, '2019-06-27 22:00:00', 'Przelew środków', 'Wydatki związane z samochodem', 'CZK', 0),
(105, 'test', 'outgoing', '97390856749836278538754387', '36456354732432546547657658', 600, '2019-03-13 20:24:31', 'Przelew środków', 'Rozrywka', 'CZK', 0),
(106, 'test', 'outgoing', '12345678901234567890123456', '43534543243t57436856843509', 140, '2018-12-19 20:24:31', 'Przelew środków', 'Rozrywka', 'PLN', 0),
(107, 'test', 'outgoing', '12345678901234567890123456', '93275894368956438756348764', 50, '2018-08-26 19:24:31', 'Przelew środków', 'Rozrywka', 'PLN', 0),
(108, 'test', 'outgoing', '09809576843879547835476325', '35436587634856876983254432', 30, '2018-09-03 22:00:00', 'Przelew środków', 'Sport', 'PLN', 0),
(109, 'test', 'outgoing', '12345678901234567890123456', '43248634785634876543788932', 80, '2016-12-11 23:00:00', 'Przelew środków', 'Sport', 'PLN', 0),
(110, 'test', 'outgoing', '97390856749836278538754387', '35435439854389568743658764', 300, '2017-02-15 23:00:00', 'Przelew środków', 'Sport', 'CZK', 0),
(111, 'test', 'outgoing', '86578643065942356403625734', '32543654675476738247983748', 40, '2017-03-17 23:00:00', 'Przelew środków', 'Sport', 'USD', 0);



CREATE TABLE `users` (
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_polish_ci NOT NULL,
  `password` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `users` (`username`, `password`, `enabled`) VALUES
('test', '$2a$10$4yNtm9Az2ZNnbHb5bhgoK.0n0kJR61z/qZ7tkTOXp/StYKJ3AEqJG', 1);


ALTER TABLE `accounts`
  ADD PRIMARY KEY (`account_number`) USING BTREE,
  ADD KEY `username` (`username`,`bank`,`balance`,`currency`,`account_name`) USING BTREE;


ALTER TABLE `authorities`
  ADD UNIQUE KEY `ix_auth_username` (`username`,`authority`);


ALTER TABLE `budgets`
  ADD PRIMARY KEY (`budget_id`),
  ADD KEY `username` (`username`,`budget_name`,`purpose`),
  ADD KEY `amount` (`amount`),
  ADD KEY `currency` (`currency`);


ALTER TABLE `currency`
  ADD PRIMARY KEY (`currency`);


ALTER TABLE `debts`
  ADD PRIMARY KEY (`debt_id`),
  ADD KEY `debt_name` (`creditor`,`type`,`amount`) USING BTREE,
  ADD KEY `username` (`username`),
  ADD KEY `debt_name_2` (`debt_name`);


ALTER TABLE `expenditures`
  ADD PRIMARY KEY (`id`),
  ADD KEY `budget_id` (`budget_id`,`amount`,`description`);


ALTER TABLE `expenditure_categories`
  ADD PRIMARY KEY (`expenditure_type`),
  ADD KEY `description` (`description`),
  ADD KEY `username` (`username`);


ALTER TABLE `transactions`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `type` (`amount`,`username`,`datetime`,`description`),
  ADD KEY `username` (`username`),
  ADD KEY `expenditure_type` (`expenditure_type`),
  ADD KEY `account_number` (`account_number_from`),
  ADD KEY `currency` (`currency`),
  ADD KEY `type_2` (`type`),
  ADD KEY `debt_id` (`debt_id`);


ALTER TABLE `users`
  ADD PRIMARY KEY (`username`);


ALTER TABLE `budgets`
  MODIFY `budget_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;


ALTER TABLE `debts`
  MODIFY `debt_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;


ALTER TABLE `expenditures`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;


ALTER TABLE `transactions`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;


ALTER TABLE `accounts`
  ADD CONSTRAINT `accounts_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


ALTER TABLE `authorities`
  ADD CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


ALTER TABLE `budgets`
  ADD CONSTRAINT `budgets_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


ALTER TABLE `debts`
  ADD CONSTRAINT `debts_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


ALTER TABLE `expenditures`
  ADD CONSTRAINT `expenditures_ibfk_1` FOREIGN KEY (`budget_id`) REFERENCES `budgets` (`budget_id`);


ALTER TABLE `expenditure_categories`
  ADD CONSTRAINT `expenditure_categories_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`);


ALTER TABLE `transactions`
  ADD CONSTRAINT `transactions_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`),
  ADD CONSTRAINT `transactions_ibfk_3` FOREIGN KEY (`expenditure_type`) REFERENCES `expenditure_categories` (`expenditure_type`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

