//门急诊药房(PHA)统计报表查询SQL

Module.item=queryApothecaryLoadCheck;queryApothecaryLoadDispense;queryApothecaryLoadSend;queryApothecaryLoadReturn

//工作量查询--审核
//================pangben modify 20110416  添加区域显示
queryApothecaryLoadCheck.Type=TSQL

queryApothecaryLoadCheck.SQL=SELECT B.REGION_CHN_DESC, TO_CHAR(A.PHA_CHECK_DATE,'yyyy/mm/dd') AS QDATE , A.EXEC_DEPT_CODE AS DEPT , A.PHA_CHECK_CODE AS PERSON , COUNT (DISTINCT A.RX_NO) AS COUNT,SUM (A.AR_AMT) AS CHARGE, &
                                     COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.RX_NO END) DCT_RXNUM,&
                                     COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.CASE_NO END) DCT_CASENUM &
FROM OPD_ORDER A ,SYS_REGION B &
WHERE A.REGION_CODE=B.REGION_CODE &
GROUP BY  TO_CHAR(A.PHA_CHECK_DATE,'yyyy/mm/dd'), A.EXEC_DEPT_CODE, A.PHA_CHECK_CODE,B.REGION_CHN_DESC ORDER BY B.REGION_CHN_DESC
   
queryApothecaryLoadCheck.item=REGION_CODE;EXEC_DEPT_CODE;ADM_TYPE;PHA_CHECK_DATE
//==============pangben modify 20110406 start
queryApothecaryLoadCheck.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==============pangben modify 20110406 stop
queryApothecaryLoadCheck.EXEC_DEPT_CODE=A.EXEC_DEPT_CODE=<EXEC_DEPT_CODE>
queryApothecaryLoadCheck.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryApothecaryLoadCheck.PHA_CHECK_DATE=A.PHA_CHECK_DATE BETWEEN <START_DATE> AND <END_DATE>
 
queryApothecaryLoadCheck.Debug=N


//工作量查询--配药
//================pangben modify 20110416  添加区域显示
queryApothecaryLoadDispense.Type=TSQL

queryApothecaryLoadDispense.SQL=SELECT B.REGION_CHN_DESC, TO_CHAR(A.PHA_DOSAGE_DATE,'yyyy/mm/dd') AS QDATE , A.EXEC_DEPT_CODE AS DEPT , A.PHA_DOSAGE_CODE AS PERSON , COUNT (DISTINCT A.RX_NO) AS COUNT,SUM (A.AR_AMT) AS CHARGE, &
                                        COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.RX_NO END) DCT_RXNUM,&
                                        COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.CASE_NO END) DCT_CASENUM &
FROM OPD_ORDER A,SYS_REGION B &
WHERE A.REGION_CODE=B.REGION_CODE &
GROUP BY TO_CHAR(A.PHA_DOSAGE_DATE,'yyyy/mm/dd'), A.EXEC_DEPT_CODE, A.PHA_DOSAGE_CODE,B.REGION_CHN_DESC ORDER BY B.REGION_CHN_DESC
   
queryApothecaryLoadDispense.item=REGION_CODE;EXEC_DEPT_CODE;ADM_TYPE;PHA_DOSAGE_DATE
//==============pangben modify 20110406 start
queryApothecaryLoadDispense.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==============pangben modify 20110406 stop
queryApothecaryLoadDispense.EXEC_DEPT_CODE=A.EXEC_DEPT_CODE=<EXEC_DEPT_CODE>
queryApothecaryLoadDispense.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryApothecaryLoadDispense.PHA_DOSAGE_DATE=A.PHA_DOSAGE_DATE BETWEEN <START_DATE> AND <END_DATE>
queryApothecaryLoadDispense.Debug=N


//工作量查询--发药
//================pangben modify 20110416  添加区域显示
queryApothecaryLoadSend.Type=TSQL

queryApothecaryLoadSend.SQL=SELECT B.REGION_CHN_DESC,TO_CHAR(A.PHA_DISPENSE_DATE,'yyyy/mm/dd') AS QDATE , A.EXEC_DEPT_CODE AS DEPT , A.PHA_DISPENSE_CODE AS PERSON , COUNT (DISTINCT A.RX_NO) AS COUNT,SUM (A.AR_AMT) AS CHARGE, &
                                    COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.RX_NO END) DCT_RXNUM,&
                                    COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.CASE_NO END) DCT_CASENUM &
FROM OPD_ORDER A,SYS_REGION B &
WHERE A.REGION_CODE=B.REGION_CODE &
GROUP BY TO_CHAR(A.PHA_DISPENSE_DATE,'yyyy/mm/dd'), A.EXEC_DEPT_CODE, A.PHA_DISPENSE_CODE,B.REGION_CHN_DESC ORDER BY B.REGION_CHN_DESC
   
queryApothecaryLoadSend.item=REGION_CODE;EXEC_DEPT_CODE;ADM_TYPE;PHA_DISPENSE_DATE
//==============pangben modify 20110406 start
queryApothecaryLoadSend.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==============pangben modify 20110406 stop
queryApothecaryLoadSend.EXEC_DEPT_CODE=A.EXEC_DEPT_CODE=<EXEC_DEPT_CODE>
queryApothecaryLoadSend.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryApothecaryLoadSend.PHA_DISPENSE_DATE=A.PHA_DISPENSE_DATE BETWEEN <START_DATE> AND <END_DATE>

 
queryApothecaryLoadSend.Debug=N


//工作量查询--退药
//================pangben modify 20110416  添加区域显示
queryApothecaryLoadReturn.Type=TSQL

queryApothecaryLoadReturn.SQL=SELECT B.REGION_CHN_DESC,TO_CHAR(A.PHA_RETN_DATE,'yyyy/mm/dd') AS QDATE , A.EXEC_DEPT_CODE AS DEPT , A.PHA_RETN_CODE AS PERSON , COUNT (DISTINCT A.RX_NO) AS COUNT,SUM (A.AR_AMT) AS CHARGE, &
                                      COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.RX_NO END) DCT_RXNUM,&
                                      COUNT (DISTINCT CASE WHEN A.DECOCT_DATE IS NOT NULL THEN A.CASE_NO END) DCT_CASENUM &
FROM OPD_ORDER A,SYS_REGION B &
WHERE A.REGION_CODE=B.REGION_CODE &
GROUP BY TO_CHAR(A.PHA_RETN_DATE,'yyyy/mm/dd'), A.EXEC_DEPT_CODE, A.PHA_RETN_CODE,B.REGION_CHN_DESC ORDER BY B.REGION_CHN_DESC
   
queryApothecaryLoadReturn.item=REGION_CODE;EXEC_DEPT_CODE;ADM_TYPE;PHA_RETN_DATE
//==============pangben modify 20110406 start
queryApothecaryLoadReturn.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==============pangben modify 20110406 stop
queryApothecaryLoadReturn.EXEC_DEPT_CODE=A.EXEC_DEPT_CODE=<EXEC_DEPT_CODE>
queryApothecaryLoadReturn.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryApothecaryLoadReturn.PHA_RETN_DATE=A.PHA_RETN_DATE BETWEEN <START_DATE> AND <END_DATE>
 
queryApothecaryLoadReturn.Debug=N






