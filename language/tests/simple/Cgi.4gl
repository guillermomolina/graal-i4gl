MAIN
    CALL icgi_mimetype("text/html")
    IF (icgi_start() != 0) THEN
        DISPLAY "Error"
    ELSE
        DISPLAY icgi_getvalue("param")
        CALL icgi_free()
    END IF
END MAIN
