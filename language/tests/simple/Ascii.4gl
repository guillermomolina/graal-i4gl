MAIN
  DEFINE aText VARCHAR(10)
  DEFINE l INT
  DISPLAY "|", ascii 65, "|"
  LET aText = "|", ascii 65, "|"
  DISPLAY aText
  LET aText = "|", ascii(65), "|"
  DISPLAY aText
  LET l = 65
  LET aText = "|", ascii(l), "|"
  DISPLAY aText
  LET aText = "|", ascii l, "|"
  DISPLAY aText
  FOR l = 65 TO 90
    DISPLAY "|", ascii l, "|"
  END FOR
END MAIN
