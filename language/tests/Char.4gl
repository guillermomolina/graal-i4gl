MAIN
    DEFINE var1 NCHAR(8)
    LET var1 = "H"
    DISPLAY "|", var1, "|"
    LET var1[4] = "X"
    DISPLAY "|", var1, "|"
    LET var1[8] = "Z"
    DISPLAY "|", var1, "|"
END MAIN
