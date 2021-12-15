MAIN
  DEFINE aSmallInt1, aSmallInt2, aSmallInt3 ARRAY[1] OF SMALLINT
  DEFINE anInteger1, anInteger2, anInteger3 ARRAY[1] OF INTEGER
  DEFINE aBigInt1, aBigInt2, aBigInt3 ARRAY[1] OF BIGINT
  DEFINE aSmallFloat1, aSmallFloat2, aSmallFloat3 ARRAY[1] OF SMALLFLOAT
  DEFINE aFloat1, aFloat2, aFloat3 ARRAY[1] OF FLOAT
  LET aSmallInt1[1] = 1231
  LET anInteger1[1] = 321432
  LET aBigInt1[1] = 234215253455
  LET aSmallFloat1[1] = 2423.34535
  LET aFloat1[1] = 32423.2432432
  DISPLAY aSmallInt1[1], anInteger1[1], aBigInt1[1], aSmallFloat1[1], aFloat1[1]
  LET aSmallInt2[1] = 3345
  LET anInteger2[1] = 2342345
  LET aBigInt2[1] = 97845522565
  LET aSmallFloat2[1] = 3424.5892
  LET aFloat2[1] = 934528.81689
  DISPLAY aSmallInt2[1], anInteger2[1], aBigInt2[1], aSmallFloat2[1], aFloat2[1]
  LET aSmallInt3[1] = aSmallInt1[1] + aSmallInt2[1]
  LET anInteger3[1] = anInteger1[1] + anInteger2[1]
  LET aBigInt3[1] = aBigInt1[1] + aBigInt2[1]
  LET aSmallFloat3[1] = aSmallFloat1[1] + aSmallFloat2[1]
  LET aFloat3[1] = aFloat1[1] + aFloat2[1]
  DISPLAY aSmallInt3[1], anInteger3[1], aBigInt3[1], aSmallFloat3[1], aFloat3[1]
END MAIN
