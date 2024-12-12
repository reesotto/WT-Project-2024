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
-- Table structure for table `folder`
--

DROP TABLE IF EXISTS `folder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `folder` (
  `FolderId` int unsigned NOT NULL AUTO_INCREMENT,
  `Owner` int unsigned NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Date` date NOT NULL,
  `ParentFolder` int unsigned DEFAULT NULL,
  PRIMARY KEY (`FolderId`),
  UNIQUE KEY `Unique_keys` (`Owner`,`Name`,`ParentFolder`),
  KEY `FK3_idx` (`Owner`),
  KEY `FK4_idx` (`ParentFolder`),
  CONSTRAINT `FK3` FOREIGN KEY (`Owner`) REFERENCES `user` (`UserId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK4` FOREIGN KEY (`ParentFolder`) REFERENCES `folder` (`FolderId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=207 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `folder`
--

LOCK TABLES `folder` WRITE;
/*!40000 ALTER TABLE `folder` DISABLE KEYS */;
INSERT INTO `folder` VALUES (34,27,'root','2024-09-16',NULL),(40,27,'1','2024-09-16',34),(41,27,'2','2024-09-16',34),(42,27,'3','2024-09-16',34),(43,27,'11','2024-09-16',40),(44,27,'21','2024-09-16',41),(45,27,'12','2024-09-16',40),(46,27,'31','2024-09-16',42),(48,30,'root','2024-09-16',NULL),(49,30,'adminFolder','2024-09-16',48),(50,30,'projectFolder','2024-09-16',48),(51,31,'root','2024-09-16',NULL),(52,31,'userFolder','2024-09-16',51),(53,31,'1','2024-09-16',52),(54,31,'2','2024-09-16',52),(55,31,'3','2024-09-16',52),(56,31,'11','2024-09-16',53),(57,31,'12','2024-09-16',53),(58,31,'21','2024-09-16',54),(59,31,'23','2024-09-16',54),(60,31,'22','2024-09-16',54),(61,32,'root','2024-09-17',NULL),(62,33,'root','2024-09-17',NULL),(63,32,'1','2024-09-17',61),(64,32,'2','2024-09-17',61),(65,32,'3','2024-09-17',61),(66,32,'11','2024-09-17',63),(67,34,'root','2024-09-19',NULL),(68,34,'Folder 1','2024-09-19',67),(69,34,'Folder 11','2024-09-19',68),(70,34,'1','2024-09-19',67),(71,34,'2','2024-09-19',67),(72,34,'Folder 2','2024-09-19',67),(73,34,'Folder 12','2024-09-19',68),(74,34,'Folder 13','2024-09-19',68),(75,34,'Folder 22','2024-09-19',72),(76,34,'Folder 21','2024-09-19',72),(77,34,'Folder 23 test wow','2024-09-19',67),(78,34,'Folder 21 12312 12312312 12312','2024-09-19',69),(81,37,'root','2024-09-23',NULL),(82,38,'root','2024-09-23',NULL),(83,39,'root','2024-09-24',NULL),(84,40,'root','2024-09-24',NULL),(85,40,'root2','2024-09-24',84),(86,41,'root','2024-09-24',NULL),(87,42,'root','2024-09-25',NULL),(89,34,'11','2024-10-01',70),(90,34,'111','2024-10-01',89),(91,34,'1111','2024-10-01',90),(93,34,'112','2024-10-01',91),(94,34,'parentfolder is 11','2024-10-01',70),(95,34,'parentfolder is 1','2024-10-01',70),(96,34,'parent folder is 111','2024-10-01',90),(97,34,'parentfolder is 1111','2024-10-01',91),(98,34,'abc','2024-10-02',67),(99,34,'def','2024-10-02',67),(100,34,'def1','2024-10-02',67),(101,34,'def11','2024-10-02',67),(102,27,'121','2024-10-02',43),(104,27,'122','2024-10-02',43),(105,27,'13','2024-10-02',40),(106,43,'root','2024-10-02',NULL),(112,43,'1','2024-10-02',106),(113,43,'11','2024-10-02',106),(114,43,'111','2024-10-02',113),(115,43,'11','2024-10-02',113),(116,43,'abc','2024-10-02',112),(117,43,'def','2024-10-02',112),(118,43,'ghi','2024-10-02',117),(119,43,'llll','2024-10-02',117),(120,43,'kmkmkmk','2024-10-02',117),(121,43,'lmao','2024-10-02',117),(122,43,'bruh','2024-10-02',117),(123,43,'sasaas','2024-10-02',117),(124,43,'qwasas','2024-10-02',116),(125,43,'n kknj','2024-10-02',116),(126,43,'jjasas','2024-10-02',125),(127,43,'jansjask','2024-10-02',125),(128,43,'asdasas','2024-10-02',125),(129,27,'1211','2024-10-03',102),(131,27,'123','2024-10-03',104),(133,44,'root','2024-10-03',NULL),(138,34,'Folder 12','2024-10-03',69),(139,34,'Folder 12345 12345','2024-10-03',138),(141,27,'12345asdf','2024-10-03',40),(145,27,'parent folder is 1','2024-10-04',40),(146,27,'parentfolder is 13','2024-10-04',105),(147,27,'parent folder is 13 again','2024-10-04',105),(148,27,'wawawawa','2024-10-04',141),(150,34,'hello gello','2024-10-04',97),(151,34,'hello gello 2','2024-10-04',97),(152,34,'foldertest123456','2024-10-04',67),(154,44,'Documents','2024-10-06',133),(155,44,'Images','2024-10-06',133),(156,44,'JS','2024-10-06',154),(157,44,'C','2024-10-06',154),(158,44,'Polimi','2024-10-06',154),(159,44,'JAVA','2024-10-06',154),(160,44,'Music','2024-10-06',133),(179,46,'root','2024-10-08',NULL),(181,46,'zeb89 videos','2024-10-08',179),(182,46,'dbd','2024-10-08',181),(184,47,'root','2024-10-20',NULL),(185,47,'documents','2024-10-20',184),(187,47,'uni','2024-10-20',184),(188,47,'wawawa','2024-10-20',184),(191,48,'root','2024-10-21',NULL),(193,48,'2','2024-10-21',191),(199,49,'root','2024-12-02',NULL),(201,49,'subfolder1','2024-12-02',199),(202,27,'foldertest','2024-12-03',34),(203,50,'root','2024-12-03',NULL),(204,51,'root','2024-12-03',NULL),(205,27,'testingroot','2024-12-11',34),(206,27,'new folder','2024-12-11',43);
/*!40000 ALTER TABLE `folder` ENABLE KEYS */;
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
