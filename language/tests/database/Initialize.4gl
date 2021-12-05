-- CREATE TABLE test (
--     testId INTEGER UNIQUE PRIMARY KEY,
--     testVarchar NVARCHAR(50) DEFAULT ('default text') NOT NULL,
--     testInteger INTEGER DEFAULT(12) NOT NULL
-- );


DATABASE test

MAIN
    DEFINE testInteger LIKE test.testInteger
    DEFINE testVarchar LIKE test.testVarchar
    DEFINE testRecord RECORD LIKE test.*

    INITIALIZE testInteger TO NULL
    IF testInteger IS NULL THEN
        DISPLAY "testInteger is null"
    END IF
    DISPLAY "|", testInteger, "|"

    INITIALIZE testVarchar TO NULL
    IF testVarchar IS NULL THEN
        DISPLAY "testVarchar is null"
    END IF
    DISPLAY "|", testVarchar, "|"

    INITIALIZE testVarchar LIKE test.testVarchar
    IF testVarchar IS NULL THEN
        DISPLAY "testVarchar is null"
    END IF
    DISPLAY "|", testVarchar, "|"

    INITIALIZE testRecord.* TO NULL
    IF testRecord.testVarchar IS NULL THEN
        DISPLAY "testRecord.testVarchar is null"
    END IF
    DISPLAY "|", testRecord.testVarchar, "|"
    IF testRecord.testInteger IS NULL THEN
        DISPLAY "testRecord.testInteger is null"
    END IF
    DISPLAY "|", testRecord.testInteger, "|"

    INITIALIZE testRecord.* LIKE test.*
    IF testRecord.testVarchar IS NULL THEN
        DISPLAY "testRecord.testVarchar is null"
    END IF
    DISPLAY "|", testRecord.testVarchar, "|"
    IF testRecord.testInteger IS NULL THEN
        DISPLAY "testRecord.testInteger is null"
    END IF
    DISPLAY "|", testRecord.testInteger, "|"

END MAIN
