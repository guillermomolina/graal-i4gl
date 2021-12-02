MAIN
  DEFINE result TEXT

  LET result = "This is MAIN"
  CALL f1("f1_var", "f2_var") RETURNING result
  DISPLAY result
END MAIN 

FUNCTION f1(f1_var , f2_var)
  DEFINE f1_var TEXT
  DEFINE f2_var TEXT

  DISPLAY f2_var
  LET f2_var = "This is f2"

  DISPLAY f1_var
  LET f1_var = "This is f1"
  RETURN f1_var
END FUNCTION
