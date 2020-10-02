MAIN
  DEFINE myrecord RECORD
    anInteger INTEGER,
    aFloat REAL,
    aRecord RECORD
        aString TEXT,
        anArray ARRAY[10] OF INTEGER,
        aChar CHAR(10)
    END RECORD
  END RECORD 
  LET myrecord.anInteger = 100
  DISPLAY myrecord.anInteger
  LET myrecord.aFloat = 121.2
  DISPLAY myrecord.aFloat
  LET myrecord.aRecord.aString = "Hola como estas"
  DISPLAY myrecord.aRecord.aString
  LET myrecord.aRecord.anArray[3] = 123
  DISPLAY myrecord.aRecord.anArray[3]
  LET myrecord.aRecord.aChar[3] = "X"
  DISPLAY myrecord.aRecord.aChar[3]
END MAIN
