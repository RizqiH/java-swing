-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: laundry_system
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` varchar(20) NOT NULL,
  `customer_name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `laundry_type` varchar(50) NOT NULL,
  `service` varchar(50) NOT NULL,
  `status` varchar(20) DEFAULT 'Pending',
  `weight` decimal(5,2) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `pickup_time` datetime DEFAULT NULL,
  `order_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `customer_id` int DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('ORD001','raz','09998','raz','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 19:50:29',0),('ORD002','raz','09998','raz','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 19:54:32',0),('ORD003','raz','09998','raza','Cuci Kering','Express','Completed',2.00,10000.00,NULL,'2025-06-10 20:04:17',0),('ORD004','raz','09998','raza','Cuci Kering','Express','Completed',1.00,5000.00,NULL,'2025-06-10 20:05:04',0),('ORD005','raz','09998','raza','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 20:06:15',0),('ORD006','raz','09998','raza','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 20:09:47',0),('ORD007','raz','09998','raza','Cuci Kering','Express','Completed',1.00,5000.00,NULL,'2025-06-10 20:09:54',0),('ORD008','raz','09998','raza','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 20:16:48',0),('ORD009','raz','09998','raza','Cuci Setrika','Regular','Completed',1.00,5000.00,NULL,'2025-06-10 20:44:15',0);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `role` enum('ADMIN','MEMBER') NOT NULL,
  `points` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin','admin','Administrator','081234567890','Admin Office','ADMIN',0,'2025-06-10 19:50:09'),('john','123','John Doe','081234567891','Jl. Merdeka No. 1','MEMBER',0,'2025-06-10 19:50:09'),('raz','raza','raza','09998','raza','MEMBER',45,'2025-06-10 19:50:23');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-11  3:50:29
