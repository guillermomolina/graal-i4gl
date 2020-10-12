MAIN  
  DEFINE myrecord RECORD
    anInteger INT,
    aFloat SMALLFLOAT
  END RECORD
  LET myrecord.anInteger = 100
  LET myrecord.aFloat = 121.2
  DISPLAY myrecord.anInteger, myrecord.aFloat
END MAIN 
