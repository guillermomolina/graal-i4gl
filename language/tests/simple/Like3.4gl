DATABASE test

MAIN
    DEFINE customer RECORD LIKE customers.*
    DECLARE aCursor CURSOR FOR 
        SELECT *
        FROM customers
        LIMIT 10
    FOREACH aCursor INTO customer.*
        DISPLAY customer.*
    END FOREACH
END MAIN
