# https://www.ibm.com/docs/en/informix-servers/14.10?topic=structure-fields-sqlca

DATABASE oartdb
MAIN
    DEFINE rol RECORD LIKE rol.*
    CALL print_sqlca() 
    DECLARE aCursor CURSOR FOR 
        SELECT * FROM rol LIMIT 10
    CALL print_sqlca()
    FOREACH aCursor INTO rol.*
        DISPLAY rol
        CALL print_sqlca()
    END FOREACH
    CALL print_sqlca()
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

