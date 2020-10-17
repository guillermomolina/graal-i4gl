
MAIN
    DEFINE theTotal BIGINT
    DEFINE customerNumber INT
    DEFINE customerName VARCHAR(50)
    DATABASE test
    SELECT COUNT(*) INTO theTotal
        FROM customers
    DISPLAY theTotal
    SELECT customerNumber,customerName INTO customerNumber,customerName
        FROM customers
    DISPLAY "|",customerNumber,"|",customerName,"|"
END MAIN
