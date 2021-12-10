MAIN  
  DEFINE aBigInt BIGINT
  DEFINE aSmallInt SMALLINT
  DEFINE aFloat FLOAT
  DEFINE aChar CHAR(10)
  DEFINE aVarchar VARCHAR(10)
  DEFINE aDecimal DECIMAL(10,2)
  DEFINE anArray ARRAY[2] OF FLOAT
  DEFINE aRecord RECORD
    anInt INT,
    aSmallFloat SMALLFLOAT
  END RECORD
  LET aBigInt = NULL
  LET aSmallInt = NULL
  LET aFloat = NULL
  LET aChar = NULL
  LET aVarchar = NULL
  LET aDecimal = NULL
  LET anArray[1] = NULL
  LET anArray[2] = NULL
  LET aRecord.anInt = NULL
  LET aRecord.aSmallFloat = NULL
  IF aBigInt IS NULL THEN 
    DISPLAY aBigInt
    DISPLAY aBigInt, "|"
  END IF
  IF aSmallInt IS NULL THEN 
    DISPLAY aSmallInt
    DISPLAY aSmallInt, "|"
  END IF
  IF aFloat IS NULL THEN 
    DISPLAY aFloat
    DISPLAY aFloat, "|"
  END IF
  IF aChar IS NULL THEN 
    DISPLAY aChar
    DISPLAY aChar, "|"
  END IF
  IF aVarchar IS NULL THEN 
    DISPLAY aVarchar
    DISPLAY aVarchar, "|"
  END IF
  IF aDecimal IS NULL THEN 
    DISPLAY aDecimal
    DISPLAY aDecimal, "|"
  END IF
  IF anArray[1] IS NULL THEN 
    DISPLAY anArray[1]
    DISPLAY anArray[1], "|"
  END IF
  IF anArray[2] IS NULL THEN 
    DISPLAY anArray[2]
    DISPLAY anArray[2], "|"
  END IF
  IF aRecord.anInt IS NULL THEN 
    DISPLAY aRecord.anInt
    DISPLAY aRecord.anInt, "|"
  END IF
  IF aRecord.aSmallFloat IS NULL THEN 
    DISPLAY aRecord.aSmallFloat
    DISPLAY aRecord.aSmallFloat, "|"
  END IF
END MAIN 

