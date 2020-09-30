MAIN
  DEFINE an_int INTEGER
  DEFINE a_char_10 CHAR(10)

  IF an_int IS NULL THEN
    DISPLAY "an_int is null"
  END IF  
  DISPLAY "|", an_int, "|"
  IF an_int IS NULL THEN
    DISPLAY "an_int is null"
  END IF
  IF a_char_10 IS NULL THEN
    DISPLAY "a_char_10 is null"
  END IF
  DISPLAY "|", a_char_10, "|"
  IF a_char_10 IS NULL THEN
    DISPLAY "a_char_10 is null"
  END IF
END MAIN
