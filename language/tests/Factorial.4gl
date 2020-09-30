MAIN
    DEFINE n INTEGER
    FOR n = 0 TO 11
        DISPLAY "El factorial de ", n, " es ", factorial(n)
    END FOR
END MAIN

FUNCTION factorial(n)
    DEFINE n INTEGER
    IF n <= 1 THEN
        RETURN 1
    END IF
    RETURN n * factorial(n -1)
END FUNCTION

