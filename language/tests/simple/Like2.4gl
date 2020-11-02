DATABASE test

MAIN
    DEFINE customer RECORD LIKE customers.*
    DECLARE aCursor CURSOR FOR 
        SELECT customerNumber,customerName
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customer.customerNumber,customer.customerName
        DISPLAY "|",customer.customerNumber,"|",customer.customerName,"|"
    END FOREACH
END MAIN
