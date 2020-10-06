MAIN
    DEFINE n INTEGER
    FOR n = 0 TO 11
        DISPLAY "Fibonacci de ", n, " es ", fibonacci(n)
    END FOR
END MAIN

FUNCTION fibonacci(n)
    DEFINE n INTEGER
    IF n < 2 THEN
        RETURN n
    END IF
    RETURN fibonacci(n - 1) + fibonacci(n - 2)
END FUNCTION
