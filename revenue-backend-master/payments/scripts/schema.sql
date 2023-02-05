CREATE SCHEMA IF NOT EXISTS `payments` ;


CREATE TABLE  IF NOT EXISTS `merchant_transaction` (
  `transaction_id` varchar(255) NOT NULL,
  `merchant_transaction_reference_id` varchar(255) DEFAULT NULL,
  `merchant_request` json NOT NULL,
  `merchant_response` json NOT NULL,
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE  IF NOT EXISTS `payment_error` (
  `transaction_id` varchar(255) NOT NULL,
  `error_code` varchar(255) NOT NULL,
  `error_description` varchar(255) NOT NULL,
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `payment_renewal` (
  `transaction_id` varchar(255) NOT NULL,
  `created_timestamp` datetime(6) DEFAULT NULL,
  `renewal_day` date NOT NULL,
  `renewal_hour` time NOT NULL,
  `merchant_transaction_event` varchar(255) DEFAULT NULL,
  `updated_timestamp` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `payment_renewal_day_time_index` (`renewal_day`,`renewal_hour`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `transaction` (
  `transaction_id` varchar(255) NOT NULL,
  `paid_amount` double DEFAULT NULL,
  `user_consent_timestamp` datetime(6) DEFAULT NULL,
  `coupon_id` varchar(255) DEFAULT NULL,
  `discount_amount` double DEFAULT NULL,
  `exit_timestamp` datetime(6) DEFAULT NULL,
  `init_timestamp` datetime(6) DEFAULT NULL,
  `item_id` varchar(255) DEFAULT NULL,
  `msisdn` varchar(255) DEFAULT NULL,
  `payment_channel` varchar(255) DEFAULT NULL,
  `plan_id` int(11) DEFAULT NULL,
  `transaction_status` varchar(255) DEFAULT NULL,
  `transaction_type` varchar(255) DEFAULT NULL,
  `uid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



