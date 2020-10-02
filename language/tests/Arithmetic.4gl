MAIN  
  DEFINE myvar INTEGER
  LET myvar = 1
  DISPLAY "|", myvar + 1, "|"
  DISPLAY "|", myvar - 2, "|"
  DISPLAY "|", myvar + 2147483648, "|"
  DISPLAY "|", myvar + 1.1, "|"
  DISPLAY "|", myvar + "Hola", "|"
END MAIN 

# Expected output
# "|          2|"
# "|         -1|"
# "|          2147483649|"
# "|          2,10|"
# "|1Hola|"
