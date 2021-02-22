CREATE DATABASE  IF NOT EXISTS `jchat` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `jchat`;
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: jchat
-- ------------------------------------------------------
-- Server version	5.6.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mittente` varchar(45) NOT NULL,
  `destinatario` varchar(45) NOT NULL,
  `messaggio` varchar(256) NOT NULL,
  `orario` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=149 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat`
--

LOCK TABLES `chat` WRITE;
/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
INSERT INTO `chat` VALUES (1,'arianna@unipi.it','prova','messaggio senza button','2021-01-29 11:45:00'),(2,'prova','arianna@unipi.it','Ciao Arianna!','2020-12-26 23:44:07'),(5,'prova','arianna@unipi.it','CIao Arianna questo è un messaggio più lungo dei soliti','2021-02-01 11:44:49'),(18,'prova1@gmail.com','prova@gmail.com','Ciao prova','2020-12-28 15:42:17'),(19,'prova@gmail.com','prova1@gmail.com','Ciao prova1!','2020-12-28 15:41:44'),(20,'prova','arianna@unipi.it','Ciao Arianna','2021-01-29 17:21:44'),(27,'prova','alberto@puntradio.it','Ciao Alberto! Come va?','2021-01-30 11:50:51'),(28,'prova','alberto@puntradio.it','Io tutto bene','2021-01-30 11:51:21'),(29,'prova','alberto@puntradio.it','Ciaooo','2021-01-30 11:52:50'),(30,'prova','prova1','Ciao prova1!','2021-01-30 12:15:12'),(32,'arianna@unipi.it','prova','provaaaaa','2021-01-30 12:15:28'),(35,'prova','prova1','Ciaooo','2021-02-01 10:27:56'),(37,'prova','prova1','c','2021-02-01 10:28:07'),(41,'prova','arianna@unipi.it','aaaaaaaaaaaaaaaaaaaaaa','2021-02-01 11:31:11'),(43,'prova','arianna@unipi.it','Ciao Arianna questo è un messaggio molto più lungo dei soliti che ti mando','2021-02-01 11:46:34'),(55,'prova','paolo@gmail.com','Ciao Paolo','2021-02-03 14:49:31'),(103,'prova1','prova','Buongiorno Prova','2021-02-06 09:40:30'),(104,'prova','prova1','Buongiorno!','2021-02-06 09:40:52'),(113,'utente_prova@gmail.com','paolo@gmail.com','Ciao Paolo, come stai?','2021-02-07 11:34:38'),(114,'paolo@gmail.com','utente_prova@gmail.com','Ciao, io sto bene, tu?','2021-02-07 11:35:15'),(122,'prova','francesca@yahoo.it','Ciao','2021-02-08 16:15:52'),(137,'prova','marco@unipi.it','Ciao ','2021-02-08 16:27:01'),(139,'prova','arianna@unipi.it','Ciao','2021-02-08 16:27:15'),(142,'prova','paolo@gmail.com','Ciao Paolo, come stai?','2021-02-08 16:28:18'),(143,'prova','paolo@gmail.com','PPP','2021-02-08 16:28:31'),(144,'prova','francesca@yahoo.it','Ciao Francesca','2021-02-08 16:28:50');
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary table structure for view `nnuovimessaggi`
--

DROP TABLE IF EXISTS `nnuovimessaggi`;
/*!50001 DROP VIEW IF EXISTS `nnuovimessaggi`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE TABLE `nnuovimessaggi` (
  `mittente` tinyint NOT NULL,
  `nNuoviMessaggi` tinyint NOT NULL
) ENGINE=MyISAM */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `utenti_connessi`
--

DROP TABLE IF EXISTS `utenti_connessi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `utenti_connessi` (
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utenti_connessi`
--

LOCK TABLES `utenti_connessi` WRITE;
/*!40000 ALTER TABLE `utenti_connessi` DISABLE KEYS */;
INSERT INTO `utenti_connessi` VALUES ('alberto@puntradio.it'),('arianna@unipi.it'),('francesca@yahoo.it'),('giulia@unipi.it'),('luca@yahoo.it'),('marco@unipi.it'),('paolo@gmail.com'),('sara@yahoo.it'),('valerio@gmail.com');
/*!40000 ALTER TABLE `utenti_connessi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `nnuovimessaggi`
--

/*!50001 DROP TABLE IF EXISTS `nnuovimessaggi`*/;
/*!50001 DROP VIEW IF EXISTS `nnuovimessaggi`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `nnuovimessaggi` AS select `chat`.`mittente` AS `mittente`,count(0) AS `nNuoviMessaggi` from `chat` where ((`chat`.`destinatario` = 'prova') and (`chat`.`orario` > '2021-02-09 16:27:50.708')) group by `chat`.`mittente` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-09 16:35:22
