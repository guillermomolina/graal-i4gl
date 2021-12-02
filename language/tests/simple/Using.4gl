MAIN  
  DEFINE aSmallInt SMALLINT
  LET aSmallInt = 0
  DISPLAY "|", aSmallInt USING "#####", "|"
  DISPLAY "|", aSmallInt USING "&&&&&", "|"
  DISPLAY "|", aSmallInt USING "$$$$$", "|"
  DISPLAY "|", aSmallInt USING "*****", "|"
  DISPLAY "|", aSmallInt USING "<<<<<", "|"

  DISPLAY "|", 12345 USING "<<<,<<<", "|"
  DISPLAY "|", 1234 USING "<<<,<<<", "|"
  DISPLAY "|", 123 USING "<<<,<<<", "|"
  DISPLAY "|", 12 USING "<<<,<<<", "|"
END MAIN 
