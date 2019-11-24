DROP TABLE IF EXISTS `accounts`,  `authorities`, `budgets`, `currency`, `debts`, `expenditures`, `expenditure_categories`, `transactions`, `users`, `users_activation`;


CREATE TABLE `accounts` (
  `username` varchar(50) NOT NULL,
  `account_number` varchar(26) PRIMARY KEY,
  `bank` varchar(40) NOT NULL,
  `balance` float NOT NULL,
  `currency` varchar(3) NOT NULL,
  `account_name` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `budgets` (
  `budget_id` int(255) AUTO_INCREMENT PRIMARY KEY,
  `username` varchar(50) NOT NULL,
  `budget_name` varchar(30) NOT NULL,
  `purpose` varchar(50) NOT NULL,
  `amount` float DEFAULT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `currency` (
  `currency` varchar(3) PRIMARY KEY,
  `name` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `debts` (
  `debt_id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `debt_name` varchar(50) NOT NULL,
  `creditor` varchar(50) NOT NULL,
  `type` varchar(20) NOT NULL,
  `amount` float NOT NULL,
  `username` varchar(50) NOT NULL,
  `currency` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `expenditures` (
  `id` int(255) AUTO_INCREMENT PRIMARY KEY,
  `budget_id` int(255) DEFAULT NULL,
  `amount` float NOT NULL,
  `description` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `expenditure_categories` (
  `expenditure_type` varchar(30) PRIMARY KEY,
  `description` varchar(255) DEFAULT NULL,
  `username` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `transactions` (
  `transaction_id` int(11) AUTO_INCREMENT PRIMARY KEY,
  `username` varchar(50) NOT NULL,
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

CREATE TABLE `users` (
  `username` varchar(50) PRIMARY KEY,
  `password` varchar(60) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `email` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `users_activation` (
  `username` varchar(50) PRIMARY KEY,
  `activation_code` varchar(50) NOT NULL,
  `expiration` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;