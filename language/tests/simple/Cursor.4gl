
MAIN
    DEFINE customerNumber INT
    DEFINE customerName VARCHAR(50)
    DATABASE test
    DECLARE aCursor CURSOR FOR 
        SELECT customerNumber AS number,customerName AS name
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customerNumber,customerName
        DISPLAY "|",customerNumber,"|",customerName,"|"
    END FOREACH
END MAIN
