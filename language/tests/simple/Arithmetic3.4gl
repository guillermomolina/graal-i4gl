MAIN
  DEFINE record1, record2, record3 RECORD
    aSmallInt SMALLINT,
    anInteger INTEGER,
    aBigInt BIGINT,
    aSmallFloat SMALLFLOAT,
    aFloat FLOAT
  END RECORD
  LET record1.aSmallInt = 123
  LET record1.anInteger = 321432
  LET record1.aBigInt = 234215253455
  LET record1.aSmallFloat = 2423.34535
  LET record1.aFloat = 32423.2432432
  DISPLAY record1.*
  LET record2.aSmallInt = 3345
  LET record2.anInteger = 2342345
  LET record2.aBigInt = 97845522565
  LET record2.aSmallFloat = 3424.5892
  LET record2.aFloat = 934528.81689
  DISPLAY record2.*
  LET record3.aSmallInt = record1.aSmallInt + record2.aSmallInt
  LET record3.anInteger = record1.anInteger + record2.anInteger
  LET record3.aBigInt = record1.aBigInt + record2.aBigInt
  LET record3.aSmallFloat = record1.aSmallFloat + record2.aSmallFloat
  LET record3.aFloat = record1.aFloat + record2.aFloat
  DISPLAY record3.*
END MAIN
