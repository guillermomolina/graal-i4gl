MAIN  
  DEFINE anArray ARRAY[2] OF RECORD
    anInt INT,
    aSmallFloat SMALLFLOAT
  END RECORD
  LET anArray[1].anInt = 123
  LET anArray[1].aSmallFloat = 21.3
  LET anArray[1].anInt = 123
  LET anArray[1].aSmallFloat = 232.4
  DISPLAY anArray[1].anInt, anArray[1].aSmallFloat
  DISPLAY anArray[2].anInt, anArray[2].aSmallFloat
END MAIN 
