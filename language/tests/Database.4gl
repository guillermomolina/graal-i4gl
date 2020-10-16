DATABASE test

MAIN
    DEFINE theTotal INTEGER
    SELECT COUNT(*)
        FROM customers
    DISPLAY theTotal
END MAIN
