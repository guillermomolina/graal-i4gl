MAIN
  DISPLAY "This is MAIN"
  CALL f1()
  DISPLAY "This is MAIN again"
END MAIN 

FUNCTION f2()
  DISPLAY "This is f2"
END FUNCTION

FUNCTION f1()
  DISPLAY "This is f1"
  CALL f2()
  DISPLAY "This is f1 again"
END FUNCTION
