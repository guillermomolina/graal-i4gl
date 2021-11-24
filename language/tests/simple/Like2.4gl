DATABASE test

MAIN
    DEFINE customer RECORD LIKE customers.*
    DECLARE aCursor CURSOR FOR 
        SELECT customerId,company
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customer.customerId,customer.company
        DISPLAY "|",customer.customerId,"|",customer.company,"|"
    END FOREACH
END MAIN
