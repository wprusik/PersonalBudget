INSERT INTO `accounts` (`username`, `account_number`, `bank`, `balance`, `currency`, `account_name`) VALUES
('test', '86578643065942356403625734', 'Bank Billenium', 3727, 'NZD', 'Konto walutowe'),
('test', '97390856749836278538754387', 'Česká spořitelna', 29670, 'CZK', 'Konto zagraniczne'),
('test', 'foo', 'Česká spořitelna', 29670, 'CZK', 'Konto zagraniczne'),
('test', '38295687436574397695479674', 'Milleniuąćżsm', 32, 'USD', 'Oszczędnościowe');


INSERT INTO `authorities` (`username`, `authority`) VALUES
('test', 'ROLE_USER'),
('testNonActivated', 'ROLE_USER');

INSERT INTO `users_activation` (`username`, `activation_code`, `expiration`) VALUES
('testNonActivated', 'nuigfdbuigbidkfg', '3010-10-10 10:10:00');


INSERT INTO `budgets` (`budget_id`, `username`, `budget_name`, `purpose`, `amount`, `currency`) VALUES
(default, 'test', 'Budżet wakacyjny', 'Wyprawa do Peru', 16000, 'DKK'),
(default, 'test', 'Przeprowadzka', 'Wydatki związane z przeprowadzką', 4000, 'PLN'),
(default, 'test', '', 'Budżet z błędem', 16000, 'DKK');

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

INSERT INTO `debts` (`debt_id`, `debt_name`, `creditor`, `type`, `amount`, `username`, `currency`) VALUES
(default, 'Kredyt mieszkaniowy', 'Bank Millenium SA', 'debt', 247000, 'test', 'PLN'),
(default, 'Pożyczka na samochód', 'Mama', 'debt', 4838, 'test', 'PLN'),
(default, 'Dług Daniela', 'Daniel Heczko', 'claim', 3214, 'test', 'CZK'),
(default, 'Test debt', 'Daniel Reszko', 'debt', 3214, 'test', 'CZK'),
(default, 'Pożyczka na naprawę samochodu', 'Krzysiek Kowalski', 'claim', 683, 'test', 'PLN');

INSERT INTO `expenditures` (`id`, `budget_id`, `amount`, `description`) VALUES
(default, 1, 102, 'Archeologia'),
(default, 1, 99, 'Paleontologia'),
(default, 2, 11, 'Karcelologia');

INSERT INTO `expenditure_categories` (`expenditure_type`, `description`, `username`) VALUES
('Rozrywka', 'Kino, wycieczki, kupno gadżetów', 'test'),
('Sport', 'Siłownia, rower, wędkarstwo, itp.', 'test'),
('Wydatki związane z samochodem', 'Paliwo, naprawy, ubezpieczenie, itd.', 'test'),
('Żywność', 'Wydatki związane z zakupem żywności', 'test'),
('Testowanie', 'Wydatki związane z przeprowadzaniem testów integracyjnych', 'test');

INSERT INTO `transactions` (`transaction_id`, `username`, `type`, `account_number_from`, `account_number_to`, `amount`, `datetime`, `description`, `expenditure_type`, `currency`, `debt_id`) VALUES
(default, 'test', 'outgoing', '86578643065942356403625734', '21573647895643985783496587', 550, '2019-07-04 18:45:40', 'Zamówienie nr 01764322', 'Żywność', 'USD', 0),
(default, 'test', 'incoming', '73489654907658940769587654', '97390856749836278538754387', 450, '2019-07-04 18:46:26', 'Przelew środków', NULL, 'USD', 0),
(default, 'test', 'outgoing', '86578643065942356403625734', '63487560436705482357576543', 200, '2019-07-04 18:47:12', 'Opłata za rezerwację biletów', 'Rozrywka', 'USD', 0),
(default, 'test', 'outgoing', '97390856749836278538754387', '21573647895643985783496587', 780, '2019-07-04 18:48:08', 'Opłata za fakturę nr 983', 'Wydatki związane z samochodem', 'CZK', 0),
(default, 'test', 'deposit', NULL, '09809576843879547835476325', 300, '2019-07-04 18:52:09', 'Oszczędności', NULL, 'PLN', 0),
(default, 'test', 'debt', '09809576843879547835476325', NULL, 1500, '2019-08-03 20:00:00', 'V rata kredytu', NULL, 'PLN', 14),
(default, 'test', 'outgoing', '97390856749836278538754387', '93847598634985697438658476', 1200, '2019-06-27 22:00:00', 'Przelew środków', 'Wydatki związane z samochodem', 'CZK', 0),
(default, 'test', 'outgoing', '97390856749836278538754387', '36456354732432546547657658', 600, '2019-03-13 20:24:31', 'Przelew środków', 'Rozrywka', 'CZK', 0),
(default, 'test', 'outgoing', '97390856749836278538754387', '35435439854389568743658764', 300, '2017-02-15 23:00:00', 'Przelew środków', 'Sport', 'CZK', 0),
(default, 'test', 'outgoing', '86578643065942356403625734', '32543654675476738247983748', 40, '2017-03-17 23:00:00', 'Przelew środków', 'Sport', 'USD', 0),
(default, 'test', 'debt', '86578643065942356403625734', NULL, 50, '2019-07-10 07:50:24', 'fdgfdghfd', NULL, 'NZD', 15),
(default, 'test', 'debt', '97390856749836278538754387', NULL, 20, '2019-07-26 09:27:01', 'Archeologia', NULL, 'CZK', 16),
(default, 'test', 'outgoing', '86578643065942356403625734', '15346547658768769876978698', 14, '2019-08-07 20:15:50', 'Archeologia', 'Wydatki związane z samochodem', 'NZD', 0),
(default, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 615, '2019-08-16 20:00:00', '5325346456', NULL, 'CZK', 0),
(default, 'test', 'withdraw', '97390856749836278538754387', NULL, 16, '2019-08-28 20:00:00', 'fdhgfhgfnb', NULL, 'CZK', 0),
(default, 'test', 'withdraw', '86578643065942356403625734', NULL, 15, '2019-09-11 20:00:00', 'asdasd', NULL, 'NZD', 0),
(default, 'test', 'withdraw', '97390856749836278538754387', NULL, 15, '2019-10-17 20:00:00', 'asdasd', NULL, 'CZK', 0),
(default, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 5, '2019-08-08 08:33:19', 'asdasd', 'Sport', 'CZK', 0),
(default, 'test', 'between', '86578643065942356403625734', '38295687436574397695479674', 15, '2019-08-08 08:33:29', '12343534tgfdbfdgb12343534tgfdbfdgb12343534tgfdbfdgb12343534t', NULL, 'NZD', 0),
(default, 'test', 'deposit', NULL, '97390856749836278538754387', 7, '2019-08-08 08:33:36', 'Archeologia', NULL, 'CZK', 0),
(default, 'test', 'withdraw', '86578643065942356403625734', NULL, 4, '2019-08-08 08:33:44', 'asdasd', NULL, 'NZD', 0),
(default, 'test', 'debt', '86578643065942356403625734', NULL, 10, '2019-08-08 08:33:55', 'asdasd', NULL, 'NZD', 16),
(default, 'test', 'debt', '86578643065942356403625734', NULL, 7, '2119-08-08 08:34:06', 'Archeologia', NULL, 'NZD', 17),
(default, 'test', 'between', '97390856749836278538754387', '86578643065942356403625734', 15, '2019-08-30 20:00:00', 'asdasd', NULL, 'CZK', 0),
(default, 'test', 'debt', '86578643065942356403625734', NULL, 15, '2119-09-25 20:00:00', 'Archeologia', NULL, 'NZD', 15),
(default, 'test', 'outgoing', '97390856749836278538754387', '215435436456', 10, '2019-09-19 20:00:00', 'asdasd', 'Rozrywka', 'CZK', 0),
(default, 'test', 'debt', '38295687436574397695479674', NULL, 27, '2119-08-08 08:36:06', 'Archeologia', NULL, 'USD', 16),
(28, 'test', 'unknown', '38295687436574397695479674', NULL, 27, '2019-08-08 08:36:06', 'Archeologia', NULL, 'USD', 16);

INSERT INTO `users` (`username`, `password`, `enabled`, `email`) VALUES
('test', '$2a$10$4yNtm9Az2ZNnbHb5bhgoK.0n0kJR61z/qZ7tkTOXp/StYKJ3AEqJG', 1, 'test@test.pl'),
('testNonActivated', '$2a$10$4yNtm9Az2ZNnbHb5bhgoK.0n0kJR61z/qZ7tkTOXp/StYKJ3AEqJG', 0, 'testNonActivated@test.pl');



ALTER TABLE `authorities`
  ADD UNIQUE KEY `ix_auth_username` (`username`,`authority`);

ALTER TABLE `accounts`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `authorities`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `budgets`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `debts`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `expenditures`
  ADD FOREIGN KEY (`budget_id`) REFERENCES `budgets` (`budget_id`);

ALTER TABLE `expenditure_categories`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `transactions`
  ADD FOREIGN KEY (`username`) REFERENCES `users` (`username`);

ALTER TABLE `transactions`
  ADD FOREIGN KEY (`expenditure_type`) REFERENCES `expenditure_categories` (`expenditure_type`) ;

CREATE ALIAS IF NOT EXISTS DATE FOR "com.personalbudget.demo.H2functions.Date";