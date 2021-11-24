DATABASE test

MAIN
    DEFINE theTotal BIGINT
    DEFINE id INT
    DEFINE lastName VARCHAR(20)
    SELECT COUNT(*) INTO theTotal
        FROM customers
    DISPLAY theTotal
    SELECT CustomerId,LastName INTO id,lastName
        FROM customers LIMIT 1
    DISPLAY "|",id,"|",lastName,"|"
END MAIN
