MAIN
  DEFINE an_int,an_int2 INT
  DEFINE a_char_10,another_char_10 CHAR(10)

  IF an_int IS NULL THEN
    DISPLAY "an_int is null"
  END IF
  DISPLAY "|", an_int, "|"

  LET an_int2=an_int
  IF an_int2 IS NULL THEN
    DISPLAY "an_int2 is null"
  END IF
  DISPLAY "|", an_int2, "|"

  IF a_char_10 IS NULL THEN
    DISPLAY "a_char_10 is null"
  END IF
  DISPLAY "|", a_char_10, "|"
 
  LET another_char_10=a_char_10
  IF another_char_10 IS NULL THEN
    DISPLAY "another_char_10 is null"
  END IF
  DISPLAY "|", another_char_10, "|"
END MAIN

# Expected output
# "|          0|"
# "|          0|"
# "a_char_10 is null"
# "|          |"
# "another_char_10 is null"
# "|          |"
