MAIN
  DEFINE myrecord RECORD
    anInteger INT,
    anArray ARRAY[10] OF INT
  END RECORD
  CALL f1() RETURNING myrecord.anInteger, myrecord.anArray[2]
  DISPLAY myrecord.anInteger, myrecord.anArray[2]
END MAIN 

FUNCTION f1()
  RETURN 102, 202
END FUNCTION
