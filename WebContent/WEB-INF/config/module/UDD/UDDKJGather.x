#
   # Title: 使用抗菌药物汇总
   #
   # Description: 使用抗菌药物汇总
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author yanj 2013.3.21

Module.item=getPatientDetail;selectInformation2
//使用抗菌药物
selectInformation2.Type=TSQL
selectInformation2.SQL=SELECT F.REGION_CHN_ABN &
AS REGION_CHN_DESC,A.ORDER_CODE,B.ORDER_DESC AS ORDER_DESC, &
       B.SPECIFICATION AS SPECIFICATION,C.UNIT_CHN_DESC AS UNIT_DESC, &
       A.OWN_PRICE AS OWN_PRICE,SUM(A.DOSAGE_QTY) AS SUM_QTY, &
       SUM (A.DOSAGE_QTY * A.OWN_PRICE) AS SUM_AMT  &
FROM IBS_ORDD A,PHA_BASE B,SYS_UNIT C,SYS_REGION F &
WHERE A.ORDER_CODE = B.ORDER_CODE(+) &
 AND A.DOSAGE_UNIT = C.UNIT_CODE(+) &
AND B.ANTIBIOTIC_CODE IS NOT NULL AND A.CAT1_TYPE = 'PHA' &
AND A.BILL_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy/MM/dd HH24 :mi:ss') & 
AND TO_DATE(<END_DATE>, 'yyyy/MM/dd HH24:mi:ss') &
GROUP BY A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,F.REGION_CHN_ABN, &
C.UNIT_CHN_DESC,A.OWN_PRICE  & 
ORDER BY A.ORDER_CODE
selectInformation2.item=ORDER_CODE;A.BILL_DATE
selectInformation2.ORDER_CODE = A.ORDER_CODE = <ORDER_CODE>
selectInformation2.Debug=N

