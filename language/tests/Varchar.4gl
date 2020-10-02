MAIN
    DEFINE var1 VARCHAR(9)
    LET var1 = "H"
    DISPLAY "|", var1, "|"
    LET var1[4] = "X"
    DISPLAY "|", var1, "|"
    LET var1[9] = "Z"
    DISPLAY "|", var1, "|"
    LET var1 = "H"
    DISPLAY "|", var1, "|"
END MAIN

# Expected output
# "|H|"
# "|H  X|"
# "|H  X    Z|"
# "|H|"
