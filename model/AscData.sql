-- phpMyAdmin SQL Dump
-- version 2.11.9.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Apr 06, 2009 at 01:50 PM
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
  `year` varchar(16) default NULL,
  `env_data_type` varchar(255) default NULL,
  `env_data_subtype` varchar(255) default NULL,
  `metadata` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=37 ;

--
-- Dumping data for table `AscData`
--
INSERT INTO ascdata (id, data_type, file_name, south_boundary, west_boundary, north_boundary, east_boundary, width, height, min_value, max_value, description, units, variable_type, year, env_data_type, env_data_subtype, metadata) 
VALUES
(1, 'climate', 'etptotal_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 621.0372, 1655.935, 'Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates', 'mm', 'Continuous', '2000', 'Evapotranspiration', 'Total rate/12 months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(2, 'climate', 'wbyear_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, -1054.511, 1294.463, 'Annual water balance', 'mm', 'Continuous', 'Future', 'Water balance', 'Annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(3, 'climate', 'wbyear_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, -926.3455, 2127.329, 'Annual water balance', 'mm', 'Continuous', '2000', 'Water balance', 'Annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(4, 'climate', 'wbpos_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 12, 'The number of months with a positive water balance', '0-12', 'Continuous', '1950', 'Water balance', 'Number of months positive', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(5, 'climate', 'wbpos_2x.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 11, 'The number of months with a positive water balance', '0-12', 'Continuous', 'Future', 'Water balance', 'Number of months positive', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(6, 'climate', 'wbpos_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 12, 'The number of months with a positive water balance', '0-12', 'Continuous', '2000', 'Water balance', 'Number of months positive', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(7, 'climate', 'realmat_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 109, 274, 'Mean annual temperature (mean of monthly temperatures)', 'Degrees C', 'Continuous', '1950', 'Temperature', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(8, 'climate', 'realmat_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 123, 289, 'Mean annual temperature (mean of monthly temperatures)', 'Degrees C', 'Continuous', 'Future', 'Temperature', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(9, 'climate', 'realmat_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 110, 276, 'Mean annual temperature (mean of monthly temperatures)', 'Degrees C', 'Continuous', '2000', 'Temperature', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(10, 'climate', 'realmar_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 327, 3224, 'Mean annual precipitation (sum of mean monthly rainfall)', 'mm', 'Continuous', '1950', 'Precipitation', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(11, 'climate', 'realmar_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 312, 2626, 'Mean annual precipitation (sum of mean monthly rainfall)', 'mm', 'Continuous', 'Future', 'Precipitation', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(12, 'climate', 'realmar_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 331, 3373, 'Mean annual precipitation (sum of mean monthly rainfall)', 'mm', 'Continuous', '2000', 'Precipitation', 'Mean annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(13, 'forest', 'pfc2000.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 100, 'Precent Forest Cover', 'Percent', 'Continuous', '2000', 'Forest Cover', 'Percentage', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(14, 'forest', 'pfc1990.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 100, 'Precent Forest Cover', 'Percent', 'Continuous', '1990', 'Forest Cover', 'Percentage', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(15, 'forest', 'pfc1970.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 100, 'Precent Forest Cover', 'Percent', 'Continuous', '1970', 'Forest Cover', 'Percentage', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(16, 'forest', 'pfc1950.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 100, 'Precent Forest Cover', 'Percent', 'Continuous', '1950', 'Forest Cover', 'Percentage', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(17, 'climate', 'mintemp_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 18, 212, 'Mean temperature of the coldest month', 'Degrees C', 'Continuous', '1950', 'Temperature', 'Mean of coldest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(18, 'climate', 'mintemp_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 28, 223, 'Mean temperature of the coldest month', 'Degrees C', 'Continuous', 'Future', 'Temperature', 'Mean of coldest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(19, 'climate', 'mintemp_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 18, 212, 'Mean temperature of the coldest month', 'Degrees C', 'Continuous', '2000', 'Temperature', 'Mean of coldest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(20, 'climate', 'minprec_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 108, 'Mean precipitation of the driest month', 'mm', 'Continuous', '1950', 'Precipitation', 'Mean of driest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(21, 'climate', 'minprec_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 88, 'Mean precipitation of the driest month', 'mm', 'Continuous', 'Future', 'Precipitation', 'Mean of driest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(22, 'climate', 'minprec_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 0, 108, 'Mean precipitation of the driest month', 'mm', 'Continuous', '2000', 'Precipitation', 'Mean of driest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(23, 'climate', 'maxtemp_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 189, 371, 'Mean temperature of the hottest month', 'Degrees C', 'Continuous', '1950', 'Temperature', 'Mean of hottest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(24, 'climate', 'maxtemp_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 207, 396, 'Mean temperature of the hottest month', 'Degrees C', 'Continuous', 'Future', 'Temperature', 'Mean of hottest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(25, 'climate', 'maxtemp_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 119, 325, 'Mean temperature of the hottest month', 'Degrees C', 'Continuous', '2000', 'Temperature', 'Mean of hottest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(26, 'climate', 'maxprec_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 77, 520, 'Mean precipitation of the wettest month', 'mm', 'Continuous', '1950', 'Precipitation', 'Mean of wettest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(27, 'climate', 'maxprec_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 74, 490, 'Mean precipitation of the wettest month', 'mm', 'Continuous', 'Future', 'Precipitation', 'Mean of wettest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(28, 'climate', 'maxprec_00.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 75, 516, 'Mean precipitation of the wettest month', 'mm', 'Continuous', '2000', 'Precipitation', 'Mean of wettest months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(30, 'climate', 'etptotal_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 619.6593, 1636.501, 'Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates', 'mm', 'Continuous', '1950', 'Evapotranspiration', 'Total rate/12 months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(31, 'climate', 'etptotal_2X.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 650.3458, 1816.503, 'Annual total evapotranspiration calculated by summing 12 monthly evapotranspiration rates', 'mm', 'Continuous', 'Future', 'Evapotranspiration', 'Total rate/12 months', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(32, 'climate', 'sapm_oct08.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 883, 1633, 0, 5, 'Parks', 'Park', 'Discrete', '2008', 'Parks', 'Madagascar', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(36, 'env_variable', 'wbyear_50.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, -921.4669, 1996.459, 'Annual water balance', 'mm', 'Continuous', '1950', 'Water balance', 'Annual', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(33, 'GeolStrech', 'geolstrech.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 879, 1648, 1, 11, 'Geology Strech', 'GeolStrech', 'Discrete', '2000', 'GeolStrech', 'Madagascar', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(34, 'env_variable', 'topex.asc', -25.64166487474, 43.183344973251, -11.9083308251576, 50.5083453552795, 5640, 4800, -7083, 5578, 'DEM Elevation', 'm', 'Continuous', '2000', 'DEM elevatiom', 'WIOMER', 'http://code.google.com/p/rebioma/wiki/EnvironmentalLayerMetadata'),
(37,'Oceanography','bathy.asc',-28.7,40.6,-10.3,53.3,1524,2208,-5600,531,'Bathymetrie','m','Continuous','-','Bathymetry','Madagascar','http://seawifs.gsfc.nasa.gov/cgi/reefs.pl'),
(38,'Oceanography','chloro1.asc',-28.7,40.6,-10.3,53.3,1524,2208,0.08,29.43,'Taux de Chlorophylle','mg/m3','Continuous','1997 - 2006','Chlorophyla-Monthly mean','Madagascar','http://oceancolor.gsfc.nasa.gov/cgi/level3'),
(39,'Oceanography','cv1.asc',-28.7,40.6,-10.3,53.3,1524,2208,4.9,7.9,'Vitesse du courant de surface','m/s','Continuous','1992 - 2007','Current velocity-Monthly mean','Madagascar','http://www.oscar.noaa.gov/datadisplay/datadownload-nj.htm'),
(40,'Oceanography','par1.asc',-28.7,40.6,-10.3,53.3,1524,2208,35.62,51.84,'Photosynthetic Active Radiation','Einstein/m2/day','Continuous','1997 - 2006','Photosynthetic Active Radiation-Monthly mean','Madagascar','http://oceancolor.gsfc.nasa.gov/cgi/level3'),
(41,'Oceanography','reef.asc',-28.7,40.6,-10.3,53.3,1524,2208,0,1,'Reef percentage','Percent','Continuous','2006','Reef percentage-Reef','Madagascar','Atlas of Western and Central Indian Ocean Coral Reefs (Andrefouet et Al. 2009)'),
(42,'Oceanography','sali1.asc',-28.7,40.6,-10.3,53.3,1524,2208,34.81,35.3,'Salinity','Percent','Continuous','1997 - 2006','Salinity-Monthly mean','Madagascar','http://oceancolor.gsfc.nasa.gov/cgi/l3'),
(43,'Oceanography','sst.asc',-28.7,40.6,-10.3,53.3,1524,2208,23.38,27.85,'Sea surface temperature','Degrees C','Continuous','1985 - 2006','Sea surface temperature-Monthly mean','Madagascar','http://data.nodc.noaa.gov/pathfinder/Version5.0/Monthly/'),
(44,'Oceanography','uv1.asc',-28.7,40.6,-10.3,53.3,1524,2208,203.25,288.4,'Ultra Violet','Milliwatts/m2/nm','Continuous','1996 - 2005','Ultra Violet-Daily mean','Madagascar','ftp://jwocky.gsfc.nasa.gov/pub/eptoms/data');
