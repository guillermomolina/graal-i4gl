MAIN
    DEFINE n INT
    FOR n = 0 TO 11
        DISPLAY "Fibonacci de ", n, " es ", fibonacci(n)
    END FOR
END MAIN

FUNCTION fibonacci(n)
    DEFINE n, n1, n2, i, n3 INT
    IF n < 1 THEN
        RETURN 0
    END IF
    LET n1 = 0
    LET n2 = 1
    LET i = 1
    WHILE i < n
        LET n3 = n2 + n1
        LET n1 = n2
        LET n2 = n3
        LET i = i + 1
    END WHILE
    RETURN n2
END FUNCTION
