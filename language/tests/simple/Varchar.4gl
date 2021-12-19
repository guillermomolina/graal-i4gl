MAIN
    DEFINE var1 VARCHAR(9)
    LET var1 = 12345678901234567890123456789
    DISPLAY "|", var1, "|"
    LET var1 = 1
    DISPLAY "|", var1, "|"
    LET var1 = 1.9
    DISPLAY "|", var1, "|"
    LET var1 = "H"
    DISPLAY "|", var1, "|"
    LET var1[4] = "X"
    DISPLAY "|", var1, "|"
    LET var1[9] = "Z"
    DISPLAY "|", var1, "|"
    LET var1 = "H"
    DISPLAY "|", var1, "|"
END MAIN
