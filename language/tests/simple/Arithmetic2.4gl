MAIN
  DEFINE aSmallInt1, aSmallInt2, aSmallInt3 SMALLINT
  DEFINE anInteger1, anInteger2, anInteger3 INTEGER
  DEFINE aBigInt1, aBigInt2, aBigInt3 BIGINT
  DEFINE aSmallFloat1, aSmallFloat2, aSmallFloat3 SMALLFLOAT
  DEFINE aFloat1, aFloat2, aFloat3 FLOAT
  LET aSmallInt1 = 1231
  LET anInteger1 = 321432
  LET aBigInt1 = 234215253455
  LET aSmallFloat1 = 2423.34535
  LET aFloat1 = 32423.2432432
  DISPLAY aSmallInt1, anInteger1, aBigInt1, aSmallFloat1, aFloat1
  LET aSmallInt2 = 3345
  LET anInteger2 = 2342345
  LET aBigInt2 = 97845522565
  LET aSmallFloat2 = 3424.5892
  LET aFloat2 = 934528.81689
  DISPLAY aSmallInt2, anInteger2, aBigInt2, aSmallFloat2, aFloat2
  LET aSmallInt3 = aSmallInt1 + aSmallInt2
  LET anInteger3 = anInteger1 + anInteger2
  LET aBigInt3 = aBigInt1 + aBigInt2
  LET aSmallFloat3 = aSmallFloat1 + aSmallFloat2
  LET aFloat3 = aFloat1 + aFloat2
  DISPLAY aSmallInt3, anInteger3, aBigInt3, aSmallFloat3, aFloat3
END MAIN
