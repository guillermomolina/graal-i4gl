DATABASE test

MAIN
    DEFINE theTotal BIGINT
    DEFINE id INT
    DEFINE lastName VARCHAR(20)
    SQL
        SELECT COUNT(*) INTO $theTotal
            FROM customers
    END SQL
    DISPLAY theTotal
    SQL
        SELECT CustomerId,LastName INTO $id,$lastName
            FROM customers LIMIT 1
    END SQL
    DISPLAY "|",id,"|",lastName,"|"
END MAIN
