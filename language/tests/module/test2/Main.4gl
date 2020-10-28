DEFINE aBigInt BIGINT
DEFINE aSmallInt SMALLINT
DEFINE aFloat FLOAT
DEFINE aText TEXT
DEFINE aChar CHAR(10)
DEFINE aVarchar VARCHAR(10)
DEFINE anArray ARRAY[2] OF FLOAT
DEFINE aRecord RECORD
    anInt INT,
    aSmallFloat SMALLFLOAT
END RECORD

MAIN
  DISPLAY "Start main"
  LET aBigInt = 123456789012
  LET aSmallInt = -1223
  LET aFloat = 1.0/3.0
  LET aText = "Texto muy largo"
  LET aChar = "Un texto de 10"
  LET aVarchar = "otro texto"
  LET anArray[1] = 101.5
  LET anArray[2] = 102.74
  LET aRecord.anInt = 100
  LET aRecord.aSmallFloat = 121.2
  call module1()
  DISPLAY aBigInt
  DISPLAY aSmallInt
  DISPLAY aFloat
  DISPLAY aText
  DISPLAY aChar
  DISPLAY aVarchar
  DISPLAY anArray[1], anArray[2]
  DISPLAY aRecord.anInt, aRecord.aSmallFloat
  DISPLAY "End main"
END MAIN