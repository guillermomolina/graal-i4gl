MAIN  
  DEFINE aRecord RECORD
    anInteger INTEGER,
    aFloat FLOAT
  END RECORD
  DEFINE aBigInteger BIGINT
  DEFINE aDouble SMALLFLOAT
  DEFINE aText TEXT
  DEFINE aChar CHAR(10)
  DEFINE aVarchar VARCHAR(10)
  LET aRecord.anInteger = 100
  LET aRecord.aFloat = 121.2
  LET aBigInteger = 123456789012
  LET aDouble = 1/3
  LET aText = "Texto muy largo"
  LET aChar = "Un texto de 10"
  LET aVarchar = "otro texto"
  DISPLAY aRecord.anInteger
  DISPLAY aRecord.aFloat
  DISPLAY aBigInteger
  DISPLAY aDouble
  DISPLAY aText
  DISPLAY aChar
  DISPLAY aVarchar
END MAIN 
