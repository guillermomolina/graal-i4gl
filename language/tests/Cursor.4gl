
MAIN
    DEFINE customerNumber INT
    DEFINE customerName VARCHAR(50)
    DATABASE test
    DECLARE aCursor CURSOR FOR 
        SELECT customerNumber,customerName
        FROM customers
    FOREACH aCursor INTO customerNumber,customerName
        DISPLAY "|",customerNumber,"|",customerName,"|"
    END FOREACH
END MAIN
