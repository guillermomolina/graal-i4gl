MAIN
    CALL case1(1)
    CALL case1(2)
    CALL case1(3)
    CALL case2(1)
    CALL case2(2)
    CALL case2(3)
END MAIN 

FUNCTION case1(var) 
    DEFINE var INTEGER
    CASE var
        WHEN 1
            DISPLAY "one"
        WHEN 2
            DISPLAY "two"
        OTHERWISE
            DISPLAY "dont know"
    END CASE
END FUNCTION

FUNCTION case2(var) 
    DEFINE var INTEGER
    CASE
        WHEN var = 1
            DISPLAY "one"
        WHEN var = 2
            DISPLAY "two"
        OTHERWISE
            DISPLAY "dont know"
    END CASE
END FUNCTION
