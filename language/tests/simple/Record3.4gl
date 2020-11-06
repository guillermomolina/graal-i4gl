MAIN
  DEFINE anInteger INTEGER
  DEFINE aFloat FLOAT
  CALL f() RETURNING anInteger, aFloat
  DISPLAY anInteger, aFloat
END MAIN

FUNCTION f()
  DEFINE myrecord RECORD
    anInteger INTEGER,
    aFloat FLOAT
  END RECORD
  DEFINE aString VARCHAR(50)
  LET myrecord.anInteger = 1000
  LET myrecord.aFloat = 121.2343
  LET aString = myrecord.*, ASCII 65
  DISPLAY aString
  RETURN myrecord.*
END FUNCTION
