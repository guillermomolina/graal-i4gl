MAIN  
  DEFINE myvar INTEGER
  DEFINE myarray1 ARRAY[10] OF INTEGER
  DEFINE myarray2 ARRAY[10] OF SMALLFLOAT
  FOR myvar = 1 TO 10
      LET myarray1[myvar] = myvar * 2
      LET myarray2[myvar] = myvar * 2.3
  END FOR
  FOR myvar = 1 TO 10
      DISPLAY myarray1[myvar], myarray2[myvar] 
  END FOR
END MAIN 
