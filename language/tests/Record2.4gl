MAIN
  DEFINE myrecord RECORD
    anInteger INTEGER,
    aFloat FLOAT,
    aRecord RECORD
        aString TEXT,
        anArray ARRAY[10] OF INTEGER
    END RECORD
  END RECORD 
  LET myrecord.anInteger = 100
  LET myrecord.aFloat = 121.2
  LET myrecord.aRecord.aString = "Hola como estas"
  LET myrecord.aRecord.anArray[3] = 123
  DISPLAY myrecord.anInteger, myrecord.aFloat
  DISPLAY myrecord.aRecord.aString, myrecord.aRecord.anArray[3]
END MAIN
