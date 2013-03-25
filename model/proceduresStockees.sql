 drop function if EXISTS getInfosKingdom;
DELIMITER |
CREATE  FUNCTION getInfosKingdom( king TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct KingdomSource FROM taxonomy WHERE Kingdom=king and KingdomSource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;


drop function if EXISTS getInfosPhylum;
DELIMITER |
CREATE  FUNCTION getInfosPhylum( critere TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct phylumSource FROM taxonomy WHERE phylum=critere and phylumSource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;

drop function if EXISTS getInfosClass;
DELIMITER |
CREATE  FUNCTION getInfosClass( critere TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct classSource FROM taxonomy WHERE class=critere and classSource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;

drop function if EXISTS getInfosGenus;
DELIMITER |
CREATE  FUNCTION getInfosGenus( critere TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct genusSource FROM taxonomy WHERE genus=critere and genusSource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;

drop function if EXISTS getInfosFamily;
DELIMITER |
CREATE  FUNCTION getInfosFamily( critere TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct familySource FROM taxonomy WHERE family=critere and familySource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;

drop function if EXISTS getInfosAcceptedSpecies;
DELIMITER |
CREATE  FUNCTION getInfosAcceptedSpecies( critere TEXT ) RETURNS text
BEGIN  
	DECLARE done INT DEFAULT 0;
	DECLARE ret VARCHAR(128) DEFAULT "";
	DECLARE source VARCHAR(128);
  DECLARE cur CURSOR FOR SELECT  distinct SpecificEpithetSource FROM taxonomy WHERE AcceptedSpecies=critere and SpecificEpithetSource is not NULL LIMIT 7;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET done = 1;

  OPEN cur;
  REPEAT
    FETCH cur INTO source;
		IF NOT done THEN	
			SET ret = CONCAT(ret," ; ",source);
		END IF;
  UNTIL done END REPEAT;

  CLOSE cur;
RETURN ret;
END|
DELIMITER ;

