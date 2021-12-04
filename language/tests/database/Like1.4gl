DATABASE test

MAIN
    DEFINE customerId LIKE customers.CustomerId
    DEFINE company LIKE customers.Company
    DECLARE aCursor CURSOR FOR 
        SELECT customerId,company
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customerId,company
        DISPLAY "|",customerId,"|",company,"|"
    END FOREACH
END MAIN
