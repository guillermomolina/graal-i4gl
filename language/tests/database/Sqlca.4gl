# https://www.ibm.com/docs/en/informix-servers/14.10?topic=structure-fields-sqlca

DATABASE test

MAIN
    DEFINE theTotal BIGINT
    DEFINE id INT
    DEFINE lastName VARCHAR(20)
    CALL print_sqlca()
    SELECT COUNT(*) INTO theTotal
        FROM customers
    CALL print_sqlca()
    DISPLAY theTotal
    SELECT CustomerId,LastName INTO id,lastName
        FROM customers LIMIT 1
    CALL print_sqlca()
    DISPLAY "|",id,"|",lastName,"|"
END MAIN

FUNCTION print_sqlca()
    DEFINE i INT
    DISPLAY "sqlcode: ", sqlca.sqlcode
    DISPLAY "sqlerrm: |", sqlca.sqlerrm, "|"
    DISPLAY "sqlerrp: |", sqlca.sqlerrp, "|"
    DISPLAY "sqlawarn: |", sqlca.sqlawarn, "|"
    FOR i = 1 TO 6
        DISPLAY "sqlerrd[", i USING "<&", "]: ", sqlca.sqlerrd[i]
    END FOR
END FUNCTION

