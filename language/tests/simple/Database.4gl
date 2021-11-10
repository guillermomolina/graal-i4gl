DATABASE test

MAIN
    DEFINE theTotal BIGINT
    DEFINE CustomerId INT
    DEFINE LastName VARCHAR(20)
    SELECT COUNT(*) INTO theTotal
        FROM customers
    DISPLAY theTotal
    # mysql syntax for 1 row limit
    SELECT CustomerId,LastName INTO CustomerId,LastName
        FROM customers LIMIT 1
    DISPLAY "|",CustomerId,"|",LastName,"|"
END MAIN
