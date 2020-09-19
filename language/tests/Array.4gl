MAIN  
  DEFINE myvar INTEGER
  DEFINE myarray ARRAY[10] OF INTEGER
  FOR myvar = 0 TO 9
      LET myarray[myvar] = myvar * 2
  END FOR
  FOR myvar = 0 TO 9
      DISPLAY myarray[myvar]
  END FOR
END MAIN 
