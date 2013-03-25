-- phpMyAdmin SQL Dump
-- version 2.11.9.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 06, 2009 at 01:49 PM
-- Server version: 5.0.67
-- PHP Version: 5.2.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `rebioma_prod`
--

-- --------------------------------------------------------

--
-- Table structure for table `AscData`
--

CREATE TABLE IF NOT EXISTS `AscData` (
  `id` int(11) NOT NULL auto_increment,
  `data_type` varchar(16) default NULL,
  `file_name` varchar(128) NOT NULL,
  `south_boundary` double NOT NULL,
  `west_boundary` double NOT NULL,
  `north_boundary` double NOT NULL,
  `east_boundary` double NOT NULL,
  `width` int(11) default NULL,
  `height` int(11) default NULL,
  `min_value` double NOT NULL,
  `max_value` double NOT NULL,
  `description` varchar(128) default NULL,
  `units` varchar(16) default NULL,
  `variable_type` varchar(16) default NULL,
  `year` varchar(8) default NULL,
  `env_data_type` varchar(255) default NULL,
  `env_data_subtype` varchar(255) default NULL,
  `metadata` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=37 ;

-- --------------------------------------------------------

--
-- Table structure for table `collaborators`
--

CREATE TABLE IF NOT EXISTS `collaborators` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `userId` int(10) unsigned NOT NULL,
  `friendId` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `userId` USING BTREE (`userId`),
  KEY `friendId` USING BTREE (`friendId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=39 ;

-- --------------------------------------------------------

--
-- Table structure for table `Occurrence`
--

CREATE TABLE IF NOT EXISTS `Occurrence` (
  `ID` int(11) NOT NULL auto_increment,
  `Owner` int(11) NOT NULL,
  `Public` tinyint(1) NOT NULL,
  `Vettable` tinyint(1) NOT NULL,
  `Validated` tinyint(1) NOT NULL,
  `Vetted` tinyint(1) NOT NULL,
  `TapirAccessible` tinyint(1) NOT NULL,
  `email` varchar(128) default NULL,
  `VettingError` text,
  `ValidationError` text,
  `BasisOfRecord` text,
  `YearCollected` int(11) default NULL,
  `Genus` text,
  `SpecificEpithet` text,
  `DecimalLatitude` double(10,7) default NULL,
  `DecimalLongitude` double(10,7) default NULL,
  `GeodeticDatum` text,
  `CoordinateUncertaintyInMeters` double default NULL,
  `DateLastModified` text,
  `InstitutionCode` text,
  `CollectionCode` text,
  `CatalogNumber` text,
  `ScientificName` text,
  `GlobalUniqueIdentifier` text,
  `InformationWithheld` text,
  `Remarks` text,
  `HigherTaxon` text,
  `Kingdom` text,
  `Phylum` text,
  `Class` text,
  `Order` text,
  `Family` text,
  `InfraspecificRank` text,
  `InfraspecificEpithet` text,
  `AuthorYearOfScientificName` text,
  `NomenclaturalCode` text,
  `IdentificationQualifer` text,
  `HigherGeography` text,
  `Continent` text,
  `WaterBody` text,
  `IslandGroup` text,
  `Island` text,
  `Country` text,
  `StateProvince` text,
  `County` text,
  `Locality` text,
  `MinimumElevationInMeters` double default NULL,
  `MaximumElevationInMeters` double default NULL,
  `MinimumDepthInMeters` double default NULL,
  `MaximumDepthInMeters` double default NULL,
  `CollectingMethod` text,
  `ValidDistributionFlag` tinyint(1) default NULL,
  `EarliestDateCollected` text,
  `LatestDateCollected` text,
  `DayOfYear` int(11) default NULL,
  `MonthCollected` int(11) default NULL,
  `DayCollected` int(11) default NULL,
  `Collector` text,
  `Sex` text,
  `LifeStage` text,
  `Attributes` text,
  `ImageURL` text,
  `RelatedInformation` text,
  `CatalogNumberNumeric` double default NULL,
  `IdentifiedBy` text,
  `DateIdentified` text,
  `CollectorNumber` text,
  `FieldNumber` text,
  `FieldNotes` text,
  `VerbatimCollectingDate` text,
  `VerbatimElevation` text,
  `VerbatimDepth` text,
  `Preparations` text,
  `TypeStatus` text,
  `GenBankNumber` text,
  `OtherCatalogNumbers` text,
  `RelatedCatalogedItems` text,
  `Disposition` text,
  `IndividualCount` int(11) default NULL,
  `PointRadiusSpatialFit` double default NULL,
  `VerbatimCoordinates` text,
  `VerbatimLatitude` text,
  `VerbatimLongitude` text,
  `VerbatimCoordinateSystem` text,
  `GeoreferenceProtocol` text,
  `GeoreferenceSources` text,
  `GeoreferenceVerificationStatus` text,
  `GeoreferenceRemarks` text,
  `FootprintWKT` text,
  `FootprintSpatialFit` double default NULL,
  `VerbatimSpecies` text,
  `AcceptedSpecies` text,
  `AcceptedNomenclaturalCode` text,
  `AcceptedKingdom` text,
  `AcceptedPhylum` text,
  `AcceptedClass` text,
  `AcceptedOrder` text,
  `AcceptedSuborder` text,
  `AcceptedFamily` text,
  `AcceptedSubfamily` text,
  `AcceptedGenus` text,
  `AcceptedSubgenus` text,
  `AcceptedSpecificEpithet` text,
  `DecLatInWGS84` double(10,7) default NULL,
  `DecLongInWGS84` double(10,7) default NULL,
  `AdjustedCoordinateUncertaintyInMeters` int(11) default NULL,
  `DEMElevation` double default NULL,
  `EtpTotal2000` double default NULL,
  `EtpTotalfuture` double default NULL,
  `EtpTotal1950` double default NULL,
  `GeolStrech` double default NULL,
  `MaxPerc2000` double default NULL,
  `MaxPercfuture` double default NULL,
  `MaxPerc1950` double default NULL,
  `MaxTemp2000` double default NULL,
  `MaxTempfuture` double default NULL,
  `Maxtemp1950` double default NULL,
  `MinPerc2000` double default NULL,
  `MinPercfuture` double default NULL,
  `MinPerc1950` double default NULL,
  `MinTemp2000` double default NULL,
  `MinTempfuture` double default NULL,
  `MinTemp1950` double default NULL,
  `PFC1950` double default NULL,
  `PFC1970` double default NULL,
  `PFC1990` double default NULL,
  `PFC2000` double default NULL,
  `RealMar2000` double default NULL,
  `RealMarfuture` double default NULL,
  `RealMar1950` double default NULL,
  `RealMat2000` double default NULL,
  `RealMatfuture` double default NULL,
  `RealMat1950` double default NULL,
  `WBPos2000` double default NULL,
  `WBPosfuture` double default NULL,
  `WBPos1950` double default NULL,
  `WBYear2000` double default NULL,
  `WBYearfuture` double default NULL,
  `WBYear1950` double default NULL,
  `LastUpdated` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `TimeCreated` timestamp NOT NULL default '0000-00-00 00:00:00',
  `Obfuscated` tinyint(1) default '0',
  `SharedUsers` text,
  `EmailVisible` tinyint(1) NOT NULL default '1',
  `reviewed` tinyint(1) default NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 19456 kB' AUTO_INCREMENT=1790463 ;

-- --------------------------------------------------------

--
-- Table structure for table `OccurrenceComments`
--

CREATE TABLE IF NOT EXISTS `OccurrenceComments` (
  `id` int(10) unsigned zerofill NOT NULL auto_increment,
  `oid` int(10) unsigned NOT NULL,
  `uid` int(10) unsigned NOT NULL,
  `userComment` text NOT NULL,
  `dateCommented` timestamp NOT NULL default CURRENT_TIMESTAMP,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `OccurrenceUpdates`
--

CREATE TABLE IF NOT EXISTS `OccurrenceUpdates` (
  `lastupdate` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `id` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

CREATE TABLE IF NOT EXISTS `User` (
  `id` int(11) NOT NULL auto_increment,
  `first_name` varchar(128) character set utf8 default NULL,
  `last_name` varchar(128) character set utf8 default NULL,
  `open_id` varchar(32) character set utf8 default NULL,
  `email` varchar(128) character set utf8 default NULL,
  `approved` tinyint(1) default '0',
  `vetter` int(1) default '0',
  `data_provider` int(1) default '0',
  `institution` varchar(128) character set utf8 default NULL,
  `password_hash` varchar(256) collate utf8_unicode_ci default NULL,
  `session_id` varchar(256) collate utf8_unicode_ci default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=100 ;


CREATE TABLE IF NOT EXISTS `Role` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name_en` VARCHAR(255) NOT NULL,
  `name_fr` VARCHAR(255) NOT NULL,
  `description_en` TEXT NOT NULL,
  `description_fr` TEXT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `UserRoles` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `userId` INTEGER UNSIGNED NOT NULL,
  `roleId` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `taxonomic_reviewer` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `userId` INTEGER UNSIGNED NOT NULL,
  `taxonomicField` VARCHAR(255) NOT NULL,
  `taxonomicValue` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `record_review` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `userId` INTEGER UNSIGNED NOT NULL,
  `occurrenceId` INTEGER UNSIGNED NOT NULL,
  `reviewed` BOOLEAN DEFAULT NULL,
  `reviewed_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `asc_model` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `accepted_species` VARCHAR(256) NOT NULL,
  `model_location` VARCHAR(256) NOT NULL,
  `index_file` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1;