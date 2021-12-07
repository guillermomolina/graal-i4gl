MAIN  
  DEFINE myvar INT
  DEFINE myarray1 ARRAY[10] OF INT
  DEFINE myarray2 ARRAY[10] OF SMALLFLOAT
  DEFINE myarray3 ARRAY[10] OF CHAR
  FOR myvar = 1 TO 10
      LET myarray1[myvar] = myvar * 2
      LET myarray2[myvar] = myvar * 2.3
      LET myarray3[myvar] = ASCII myvar + 65
  END FOR
  FOR myvar = 1 TO 10
      DISPLAY "|", myarray1[myvar], "|", myarray2[myvar], "|", myarray3[myvar], "|"
  END FOR
END MAIN 
