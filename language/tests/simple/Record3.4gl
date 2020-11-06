MAIN
  DEFINE myrecord RECORD
    anInteger INTEGER,
    aFloat FLOAT
  END RECORD
  DEFINE aChar CHAR(1)
  CALL f() RETURNING myrecord.*, aChar
  DISPLAY myrecord.*, aChar
END MAIN

FUNCTION f()
  DEFINE myrecord RECORD
    anInteger INTEGER,
    aFloat FLOAT
  END RECORD
  DEFINE aString VARCHAR(50)
  LET myrecord.anInteger = 1000
  LET myrecord.aFloat = 121.2343
  DISPLAY myrecord.*, ASCII 65
  LET aString = myrecord.*, ASCII 65
  DISPLAY aString
  RETURN myrecord.*, ASCII 65
END FUNCTION
