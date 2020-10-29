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

FUNCTION module1()
  DISPLAY "Start module1"
  LET aBigInt = 210987654321
  LET aSmallInt = -3441
  LET aFloat = 2.0/3.0
  LET aText = "Otro texto muy largo"
  LET aChar = "Otro texto de 10"
  LET aVarchar = "este texto"
  LET anArray[1] = 202.7
  LET anArray[2] = 355.7623
  LET aRecord.anInt = 3483
  LET aRecord.aSmallFloat = 243.3234
  DISPLAY aBigInt
  DISPLAY aSmallInt
  DISPLAY aFloat
  DISPLAY aText
  DISPLAY aChar
  DISPLAY aVarchar
  DISPLAY anArray[1], anArray[2]
  DISPLAY aRecord.anInt, aRecord.aSmallFloat
  DISPLAY "End module1"
END FUNCTION