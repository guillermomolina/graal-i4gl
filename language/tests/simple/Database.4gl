DATABASE test

MAIN
    DEFINE theTotal BIGINT
    DEFINE customerNumber INT
    DEFINE customerName VARCHAR(50)
    SELECT COUNT(*) INTO theTotal
        FROM customers
    DISPLAY theTotal
    # mysql syntax for 1 row limit
    SELECT customerNumber,customerName INTO customerNumber,customerName
        FROM customers LIMIT 1
    DISPLAY "|",customerNumber,"|",customerName,"|"
END MAIN
