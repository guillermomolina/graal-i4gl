MAIN
    DEFINE customerId INT
    DEFINE company VARCHAR(50)
    DATABASE test
    DECLARE aCursor CURSOR FOR 
        SELECT customerId AS number,company AS name
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customerId,company
        DISPLAY "|",customerId,"|",company,"|"
    END FOREACH
END MAIN
