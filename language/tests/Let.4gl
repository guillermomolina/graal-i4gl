MAIN  
  DEFINE aBigInteger BIGINT
  DEFINE aDouble FLOAT
  DEFINE aText TEXT
  DEFINE aChar CHAR(10)
  DEFINE aVarchar VARCHAR(10)
  DEFINE aRecord RECORD
    anInteger INTEGER,
    aFloat SMALLFLOAT
  END RECORD
  DEFINE anArray ARRAY[2] OF FLOAT
  LET aBigInteger = 123456789012
  LET aDouble = 1.0/3.0
  LET aText = "Texto muy largo"
  LET aChar = "Un texto de 10"
  LET aVarchar = "otro texto"
  LET anArray[1] = 101.5
  LET anArray[2] = 102.74
  LET aRecord.anInteger = 100
  LET aRecord.aFloat = 121.2
  DISPLAY aBigInteger
  DISPLAY aDouble
  DISPLAY aText
  DISPLAY aChar
  DISPLAY aVarchar
  DISPLAY anArray[1], anArray[2]
  DISPLAY aRecord.anInteger, aRecord.aFloat
END MAIN 
