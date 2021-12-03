MAIN  
  DEFINE l INT
  DEFINE aText TEXT
  DEFINE aChar CHAR(10)
  DEFINE aVarchar VARCHAR(10)
  DEFINE anArray ARRAY[2] OF FLOAT

  LET l = length("Texto muy largo")
  DISPLAY l

  LET l = length(aChar)
  DISPLAY l
  LET aChar = NULL
  LET l = length(aChar)
  DISPLAY l
  LET aChar = "Un texto"
  LET l = length(aChar)
  DISPLAY l

  LET l = length(aVarchar)
  DISPLAY l
  LET aVarchar = NULL
  LET l = length(aVarchar)
  DISPLAY l
  LET aVarchar = "otro texto"
  LET l = length(aVarchar)
  DISPLAY l

  -- LET l = length(anArray)
  -- DISPLAY l
  -- LET anArray = NULL
  -- LET l = length(anArray)
  -- DISPLAY l
  -- LET anArray[1] = 101.5
  -- LET anArray[2] = 102.74
  -- LET l = length(anArray)
  -- DISPLAY l

END MAIN 
