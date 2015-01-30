-- -----------------------------------------------------
-- Table `flywaytest`.`PERSON`
-- -----------------------------------------------------
CREATE TABLE `flywaytest`.`PERSON` (
  `ID` INT(11) NOT NULL,
  `NAME` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `flywaytest`.`SUPPORT_CASE`
-- -----------------------------------------------------
CREATE TABLE `flywaytest`.`SUPPORT_CASE` (
  `ID` INT(11) NOT NULL,
  `NAME` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ID`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `flywaytest`.`SUPPORT_CASE_TO_PERSON`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `flywaytest`.`SUPPORT_CASE_TO_PERSON` (
  `SUPPORT_CASE_ID` INT(11) NOT NULL,
  `PERSON_ID` INT(11) NOT NULL,
  PRIMARY KEY (`SUPPORT_CASE_ID`, `PERSON_ID`),
  INDEX `fk_SUPPORT_CASE_has_PERSON_PERSON1_idx` (`PERSON_ID` ASC),
  INDEX `fk_SUPPORT_CASE_has_PERSON_SUPPORT_CASE_idx` (`SUPPORT_CASE_ID` ASC),
  CONSTRAINT `fk_SUPPORT_CASE_has_PERSON_SUPPORT_CASE`
    FOREIGN KEY (`SUPPORT_CASE_ID`)
    REFERENCES `flywaytest`.`SUPPORT_CASE` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_SUPPORT_CASE_has_PERSON_PERSON1`
    FOREIGN KEY (`PERSON_ID`)
    REFERENCES `flywaytest`.`PERSON` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
