DATABASE test

MAIN
    DEFINE customer RECORD LIKE customers.*
    DECLARE aCursor CURSOR FOR 
        SELECT CustomerId,Company
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customer.CustomerId,customer.Company
        DISPLAY "|",customer.CustomerId,"|",customer.Company,"|"
    END FOREACH
END MAIN
