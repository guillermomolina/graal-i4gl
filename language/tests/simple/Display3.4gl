MAIN
    DEFINE aSmallInt SMALLINT
    DEFINE anInt INT
    DEFINE aBigInt BIGINT
    DEFINE aSmallFloat SMALLFLOAT
    DEFINE aFloat FLOAT
    DEFINE aDecimal DECIMAL
    DEFINE aText TEXT
    DEFINE aChar CHAR(20)
    DEFINE aVarchar VARCHAR(20)

    DISPLAY "0        1         2         "
    DISPLAY "12345678901234567890123456789"
    DISPLAY 1.9, "|"
    LET aSmallInt=1.9
    DISPLAY aSmallInt
    DISPLAY aSmallInt, "|"
    LET anInt=1.9
    DISPLAY anInt
    DISPLAY anInt, "|"
    LET aBigInt=1.9
    DISPLAY aBigInt
    DISPLAY aBigInt, "|"
    LET aSmallFloat=1.9
    DISPLAY aSmallFloat
    DISPLAY aSmallFloat, "|"
    LET aFloat=1.9
    DISPLAY aFloat
    DISPLAY aFloat, "|"
    LET aDecimal=1.9
    DISPLAY aDecimal
    DISPLAY aDecimal, "|"
    LET aText=1.9
    DISPLAY aText
    DISPLAY aText, "|"
    LET aChar=1.9
    DISPLAY aChar
    DISPLAY aChar, "|"
    LET aVarchar=1.9
    DISPLAY aVarchar
    DISPLAY aVarchar, "|"
END MAIN
