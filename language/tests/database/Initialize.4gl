DATABASE test

MAIN
    DEFINE customerId LIKE customers.CustomerId
    DEFINE company LIKE customers.Company
    INITIALIZE customerId TO NULL
    INITIALIZE company TO NULL
END MAIN
