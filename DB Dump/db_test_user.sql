-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: db_test
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `UserId` int unsigned NOT NULL AUTO_INCREMENT,
  `Username` varchar(45) NOT NULL,
  `Password` varchar(60) NOT NULL,
  `Email` varchar(45) NOT NULL,
  PRIMARY KEY (`UserId`),
  UNIQUE KEY `Username_UNIQUE` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (27,'jerry','$2a$10$yNdmptODOqDXjqqu7TDIceZR/8BhW8LBgaZUDQ/7gvx2MgRo1z5cu','jerry@mail.com'),(30,'admin','$2a$10$HJlA72iphwmT8AeKK9pD5.0PjkKz64YFqGlvlz2hKEtabHc3KgbnW','admin@mail.com'),(31,'user','$2a$10$7Jc7wpC4RQBlaxtwVsdij.HCCNS415vz5M767XOMjzvEWo27bzHUm','user@mail.polimi.com'),(32,'bufo','$2a$10$.oLrvlaROuz8fSQcG98HpuieRNlhwjsKtYD7QKMj3hI1xE/udI8eK','bufo@coglione.com'),(33,'acd','$2a$10$OXDmGMgA5QOYKaXbNvKfIeEHoo3ga38J8ECiCgnRNSH9hTdHRFRum','acd@lk.vom'),(34,'folder','$2a$10$tuq84aZVSa3XeFu47adzKezgX1smRrIm3/0ZUkk08lTXUDxdewpQW','folder@mail.com'),(37,'wasd','$2a$10$1U/gf/SNYQKyNRGfZdqd.umcaxIZnrHsvlI2f2cMNuiS.C93ZkF.S','wasd@mail.com'),(38,'lol','$2a$10$vwV64g558HX5E9pVzCTcN.m.dQCFmIdrcGtZ0G6/L5L4Z6C9fbTUK','lol@mail.com'),(39,'password12345','$2a$10$7D/upYpQ.43tOZ4EkaxGQ.SOZZPGXk/tf4NSVbKcWo.btkdLGPP.W','password@pass.com'),(40,'caca','$2a$10$J3JGERQB5Cwf5rL3vZ6bju2QXfvqrYFeouX7AJEAfSOZvkjyZwujm','pupu@mail.com'),(41,'testingJS','$2a$10$u0wfiovqToQA3TF0Jl5R2e9ysuC.Mb3A8l/6DEbYVjMvpuHfKvI/6','testing@mail.com'),(42,'JS','$2a$10$AMy5vaTncj4xYfGRblUQxeR3R4ErftvkeIGtE8v8o.B1lfdY2YrdO','js@mail.com'),(43,'folderbug','$2a$10$H2jVVtunlFAtCeMntIfY0O0xAzUiukBGgNUE4XjOaPOsEE4vwqGMS','folderbug@mail.com'),(44,'shori','$2a$10$JYixZjxZHVpuIRWfAntmvuyGmlj9f0lLLE1dqrf5iwAqlO5XEmyFe','shori@mail.com'),(46,'grazzani','$2a$10$d/aPN0A7vh.JdOAwWOSvnulhF3o7mHUDU2mSGRVCkoF4Dcb5jUDzy','gr@pornhub.com'),(47,'Paoli','$2a$10$w43m0.PMrJG.8.z64fDqh.KpBJ6NrdIkz22D4DfnyOZR3l7ZNERr6','matteo@mail.com'),(48,'lore','$2a$10$p.nVC7UAW.zAZpkUYwkSXuzx5lVMI1d9B/Ai4iriF6bheG8j5.YlG','lore@mail.com'),(49,'user123','$2a$10$ibXYC.28847PV4CrFDQzvOVIcdB4Ystm.eKpZQledzVxOzex9e0EK','user123@mail.mail.mail.com'),(50,'booozzzz','$2a$10$Ut51oAGtD/dvs7UXghQt3.Hv8bmeMrLeEARrxieUaIm8.W9teWin.','booz@mail.com'),(51,'booz','$2a$10$goOV5tAKYeW3.oC4Ygl6uOmyVvcqxjWOJhExrP6sAPsAE/mRVZNxe','adsuiasuiashu@mail.com');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-12 13:30:40
