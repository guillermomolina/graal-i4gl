MAIN
    DEFINE aVarchar VARCHAR(50)
    DEFINE aChar CHAR(50)
    DEFINE aText TEXT
    DEFINE anInteger INTEGER
    LET aVarchar = "Hello how are you"
    LET aChar = "Hello how are you"
    LET aText = "Hello how are you"
    LET anInteger = 65537
    DISPLAY "|", aVarchar CLIPPED, "|"
    DISPLAY "|", aChar CLIPPED, "|"
    DISPLAY "|", aText CLIPPED, "|"
    #DISPLAY "|", anInteger CLIPPED, "|"
END MAIN