/*  SQLyog Ultimate v8.55  MySQL - restaurant_db  */

/*!40101 SET NAMES utf8 */;
CREATE DATABASE IF NOT EXISTS `restaurant_db`
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE restaurant_db;
DROP DATABASE restaurant_db;

/* =======================================================
   1. TABLES — harus buat yang tidak punya dependency dulu
   =======================================================*/
/* ---- USER ---- */
DROP TABLE IF EXISTS `user`;
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin','user') NOT NULL DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

/* ---- TABLES ---- */
DROP TABLE IF EXISTS `tables`;
CREATE TABLE `tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_number` int(11) NOT NULL,
  `capacity` int(11) NOT NULL,
  `status` enum('Tersedia','Dipesan','Terisi') NOT NULL DEFAULT 'Tersedia',
  PRIMARY KEY (`id`),
  UNIQUE KEY `table_number` (`table_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---- MENU ITEMS ---- */
DROP TABLE IF EXISTS `menu_items`;
CREATE TABLE `menu_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `price` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---- ORDERS ---- */
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `table_id` (`table_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`table_id`) 
    REFERENCES `tables` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---- RESERVATIONS ---- */
DROP TABLE IF EXISTS `reservations`;
CREATE TABLE `reservations` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `customer_name` varchar(100) NOT NULL,
  `guests` int(11) NOT NULL,
  `time` varchar(100) NOT NULL,
  `table_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `table_id` (`table_id`),
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`table_id`) 
    REFERENCES `tables` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---- ORDER ITEMS ---- */
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `menu_item_id` int(11) NOT NULL,
  `qty` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `menu_item_id` (`menu_item_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) 
      REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`menu_item_id`) 
      REFERENCES `menu_items` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* =======================================================
   2. INSERT DATA
   =======================================================*/

/* TABLES */
INSERT INTO `tables` (`id`,`table_number`,`capacity`,`status`) VALUES
(1,1,2,'Terisi'),
(2,2,4,'Tersedia'),
(3,3,6,'Tersedia');

/* MENU ITEMS */
INSERT INTO `menu_items` (`id`,`name`,`price`) VALUES
(1,'Nasi Goreng',20000),
(2,'Ayam Bakar',30000),
(3,'Es Teh',5000);

/* ORDERS */
INSERT INTO `orders` (`id`,`table_id`,`created_at`) VALUES
(1,1,'2025-11-16 13:04:40');

/* RESERVATIONS */
INSERT INTO `reservations` (`id`,`customer_name`,`guests`,`time`,`table_id`,`created_at`) VALUES
(1,'Mikhael',2,'10.00',1,'2025-11-16 13:04:03');

/* ORDER ITEMS */
INSERT INTO `order_items` (`id`,`order_id`,`menu_item_id`,`qty`) VALUES
(1,1,2,2),
(2,1,3,2);

/* Admin */
INSERT INTO user (username, password, role) VALUES ('admin', 'admin123', 'admin');

select * from reservations;
select * from user;
select * from menu_items;
select * from tables;