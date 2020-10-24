MAIN
  DEFINE anInt1,anInt2 INTEGER
  DEFINE aFloat1,aFloat2 FLOAT
  DEFINE aChar1,aChar2 CHAR(10)
  DEFINE aVarchar1,aVarchar2 VARCHAR(10)
  DEFINE aText1 TEXT

  IF anInt1 IS NULL THEN
    DISPLAY "anInt1 is null"
  END IF
  DISPLAY "|", anInt1, "|"

  LET anInt2=anInt1

  IF anInt2 IS NULL THEN
    DISPLAY "anInt2 is null"
  END IF
  DISPLAY "|", anInt2, "|"

  IF aFloat1 IS NULL THEN
    DISPLAY "aFloat1 is null"
  END IF
  DISPLAY "|", aFloat1, "|"

  LET aFloat2=aFloat1

  IF aFloat2 IS NULL THEN
    DISPLAY "aFloat2 is null"
  END IF
  DISPLAY "|", aFloat2, "|"

  IF aChar1 IS NULL THEN
    DISPLAY "aChar1 is null"
  END IF
  DISPLAY "|", aChar1, "|"

  LET aChar2=aChar1

  IF aChar2 IS NULL THEN
    DISPLAY "aChar2 is null"
  END IF
  DISPLAY "|", aChar2, "|"

  IF aVarchar1 IS NULL THEN
    DISPLAY "aVarchar1 is null"
  END IF
  DISPLAY "|", aVarchar1, "|"

  LET aVarchar2=aVarchar1

  IF aVarchar2 IS NULL THEN
    DISPLAY "aVarchar2 is null"
  END IF
  DISPLAY "|", aVarchar2, "|"

  IF aText1 IS NULL THEN
    DISPLAY "aText1 is null"
  END IF
  DISPLAY "|", aText1, "|"
END MAIN
