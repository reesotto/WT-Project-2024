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
-- Table structure for table `document`
--

DROP TABLE IF EXISTS `document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `document` (
  `DocumentId` int unsigned NOT NULL AUTO_INCREMENT,
  `Owner` int unsigned NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Date` date NOT NULL,
  `Type` varchar(45) NOT NULL,
  `FolderLocation` int unsigned NOT NULL,
  `Summary` varchar(255) NOT NULL,
  PRIMARY KEY (`DocumentId`),
  KEY `FK1_idx` (`FolderLocation`),
  KEY `FK2_idx` (`Owner`),
  CONSTRAINT `FK1` FOREIGN KEY (`FolderLocation`) REFERENCES `folder` (`FolderId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK2` FOREIGN KEY (`Owner`) REFERENCES `user` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document`
--

LOCK TABLES `document` WRITE;
/*!40000 ALTER TABLE `document` DISABLE KEYS */;
INSERT INTO `document` VALUES (20,27,'document','2024-09-16','txt',46,'testing testing'),(22,27,'doc','2024-09-16','txt',43,'document testing for moves'),(23,27,'doc','2024-09-16','txt',41,'testing testing'),(24,30,'doc','2024-09-16','txt',49,'Admin\'s txt'),(25,30,'doc','2024-09-16','txt',50,'Project\'s doc'),(26,30,'test','2024-09-16','doc',48,'Test document made for testing'),(27,32,'grazzaniStupido','2024-09-17','doc',66,'grazzani \\u00C3\\u00A8 stupido'),(28,34,'document 1','2024-09-19','txt',67,'test test test\\r\\n'),(29,34,'document test wow 234','2024-09-19','txt',68,'wowowow'),(30,34,'testwoasoas 2231','2024-09-19','txt',77,'wooowwowowowoow'),(31,40,'primo','2024-09-24','doc',85,'primo doc'),(32,34,'odio JAVASCRIPT LINGUAGGIO MALEDETTO','2024-09-25','js',90,'wawawawawaw odio JS!!!'),(33,27,'i hate js','2024-10-03','txt',40,'i hate jsjsjsjsjs!!!'),(34,27,'testing doc','2024-10-03','doc',40,'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum sed ante sed dui elementum aliquam non in elit. Aenean euismod, eros quis molestie scelerisque, velit leo cursus lectus, eu ornare nisl ex eget massa. Fusce dictum magna non auctor accums'),(36,27,'odio i maranza','2024-10-03','mp3',44,'ooddddioooo'),(37,27,'122','2024-10-03','log',104,'log for folder 122'),(38,27,'file','2024-10-03','doc',102,'hello world!'),(39,27,'document test','2024-10-03','log',40,'1231231212'),(41,34,'hello gello','2024-10-04','log',150,'yoyoyoy'),(42,34,'hello gello 2','2024-10-04','log',151,'wawawawawa'),(43,27,'testing','2024-10-05','doc',104,'1234'),(44,27,'tester123','2024-10-05','log',40,'sadasadsasasdasdadasdasdsadasdsadjasndjsandjasdnjasndjasndajsndajsdnajsdnasjdnasjdnasjdansdjasndjoasnajsd'),(48,44,'API Project','2024-10-06','c',154,'printf(\\\"Hello World\\\");'),(49,44,'Software engineering game project','2024-10-06','java',159,'System.out.println(\\\"Hello World\\\");'),(50,44,'Code','2024-10-06','txt',158,'Normal code for stuff'),(60,44,'q','2024-10-07','k',155,'aa'),(63,46,'ventilatore zephyr','2024-10-08','mp4',181,'video di zeb che monta la ventola al contrario'),(64,31,'I HATE JS','2024-10-08','JS',56,'I HATE JS!'),(65,31,'I HATE MYSELF','2024-10-08','TXT',56,'I HATE MYSELF!\\r\\n'),(67,47,'appunti','2024-10-20','doc',185,'appunti wawa blabla'),(71,27,'biosssss','2024-11-01','bios',34,'abiosbaoios'),(74,27,'something','2024-12-02','txt',34,'something something'),(75,27,'1234','2024-12-02','txt',34,'13122'),(76,27,'rootfile','2024-12-11','txt',34,'assasaasd'),(77,27,'new folder doc','2024-12-11','txt',206,'sdasdasasdas');
/*!40000 ALTER TABLE `document` ENABLE KEYS */;
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
