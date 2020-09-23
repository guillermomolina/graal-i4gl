MAIN  
  DEFINE myvar INTEGER
  DEFINE myarray ARRAY[10] OF INTEGER
  FOR myvar = 1 TO 10
      LET myarray[myvar] = myvar * 2
  END FOR
  FOR myvar = 1 TO 10
      DISPLAY myarray[myvar]
  END FOR
END MAIN 
