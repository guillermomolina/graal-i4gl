DATABASE test

MAIN
    DEFINE customerNumber LIKE customers.customerNumber
    DEFINE customerName LIKE customers.customerName
    DECLARE aCursor CURSOR FOR 
        SELECT customerNumber,customerName
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customerNumber,customerName
        DISPLAY "|",customerNumber,"|",customerName,"|"
    END FOREACH
END MAIN
