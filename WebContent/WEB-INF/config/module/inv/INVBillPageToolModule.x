  #
   # Title: 物资帐页
   #
   # Description:物资帐页
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author duzhw
Module.item=selectSysFeeMed;selectDispenseIN;selectDispenseOUT;selectVerifyin;selectRegress;selectStockQty
  #
   #物资查询
   #
   #@author 
   #A.REGION_CODE = B.REGION_CODE 
   #
selectSysFeeMed.Type=TSQL
selectSysFeeMed.SQL=SELECT DISTINCT(A.INV_CODE) AS INV_CODE, A.INV_CHN_DESC, A.DESCRIPTION, A.PY1,D.UNIT_CHN_DESC  &
                                      FROM INV_BASE A, INV_STOCKD B, SYS_UNIT D  &
                                      WHERE &
                                      A.INV_CODE = B.INV_CODE  &
                                      AND B.REGION_CODE= <REGION_CODE>  &
                                      AND B.ORG_CODE = <ORG_CODE>    &                                 
                                      AND D.UNIT_CODE=A.STOCK_UNIT   &
                                      ORDER BY A.PY1, A.INV_CODE 
selectSysFeeMed.item=INV_CODE
selectSysFeeMed.INV_CODE=A.INV_CODE=<INV_CODE>
selectSysFeeMed.Debug=N

  #
   #物资库入库查询
   #
   #@author 
   #A.REGION_CODE = B.REGION_CODE 
   #
selectDispenseIN.Type=TSQL
selectDispenseIN.SQL=SELECT DISPENSE_DATE AS IN_DATE,M.DISPENSE_NO AS  IND_NO,REQTYPE_CODE AS  TYPE_CODE, &
									 ROUND(D.VERIFYIN_PRICE,2) AS VERIFYIN_PRICE,D.QTY AS QTY,ROUND(D.VERIFYIN_PRICE*D.QTY,2) AS VERIFYIN_AMT &
									 FROM IND_DISPENSEM M ,IND_DISPENSED D &
									 WHERE M.DISPENSE_NO=D.DISPENSE_NO &
									 AND DISPENSE_DATE >=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS') &
									 AND DISPENSE_DATE <=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
									 AND TO_ORG_CODE=<ORG_CODE> &
									 AND M.REGION_CODE=<REGION_CODE> AND D.ORDER_CODE=<ORDER_CODE>
selectDispenseIN.Debug=N

  #
   #物资库出库查询
   #
   #@author 
   #A.REGION_CODE = B.REGION_CODE 
   #
selectDispenseOUT.Type=TSQL
selectDispenseOUT.SQL=SELECT DISPENSE_DATE AS IN_DATE,M.DISPENSE_NO AS  IND_NO,REQTYPE_CODE AS  TYPE_CODE, &
									 ROUND(D.VERIFYIN_PRICE,2) AS VERIFYIN_PRICE,D.QTY AS QTY,ROUND(D.VERIFYIN_PRICE*D.QTY,2) AS VERIFYIN_AMT &
									 FROM IND_DISPENSEM M ,IND_DISPENSED D &
									 WHERE M.DISPENSE_NO=D.DISPENSE_NO &
									 AND DISPENSE_DATE >=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS') &
									 AND DISPENSE_DATE <=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
									 AND M.APP_ORG_CODE=<ORG_CODE> &
									 AND M.REGION_CODE=<REGION_CODE> AND D.ORDER_CODE=<ORDER_CODE>
selectDispenseOUT.Debug=N

  #
   #物资库验收查询
   #
   #@author 
   #A.REGION_CODE = B.REGION_CODE 
   #
selectVerifyin.Type=TSQL
selectVerifyin.SQL=SELECT VERIFYIN_DATE AS IN_DATE,M.VERIFYIN_NO AS  IND_NO,'VERIFY' AS  TYPE_CODE, &
									 ROUND(D.VERIFYIN_PRICE,2) AS VERIFYIN_PRICE,D.VERIFYIN_QTY AS QTY,ROUND(D.VERIFYIN_PRICE*D.VERIFYIN_QTY,2) AS VERIFYIN_AMT &
									 FROM IND_VERIFYINM M ,IND_VERIFYIND D &
									 WHERE M.VERIFYIN_NO=D.VERIFYIN_NO &
									 AND VERIFYIN_DATE >=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS') &
									 AND VERIFYIN_DATE <=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
									 AND M.ORG_CODE=<ORG_CODE> &
									 AND M.REGION_CODE=<REGION_CODE> AND D.ORDER_CODE=<ORDER_CODE>
selectVerifyin.Debug=N

  #
   #物资库退货查询
   #
   #@author 
   #
   #
selectRegress.Type=TSQL
selectRegress.SQL=SELECT REGRESSGOODS_DATE AS IN_DATE,M.REGRESSGOODS_NO AS  IND_NO,'REGRESS' AS  TYPE_CODE, &
									 ROUND(D.VERIFYIN_PRICE,2) AS VERIFYIN_PRICE,D.QTY AS QTY,ROUND(D.VERIFYIN_PRICE*D.QTY,2) AS VERIFYIN_AMT &
									 FROM IND_REGRESSGOODSM M ,IND_REGRESSGOODSD D &
									 WHERE M.REGRESSGOODS_NO=D.REGRESSGOODS_NO &
									 AND REGRESSGOODS_DATE >=TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS') &
									 AND REGRESSGOODS_DATE <=TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
									 AND M.ORG_CODE=<ORG_CODE> &
									 AND M.REGION_CODE=<REGION_CODE> AND D.ORDER_CODE=<ORDER_CODE>
selectRegress.Debug=N


  #
   #指定日期查询库存
   #
   #@author 
   #
   #
selectStockQty.Type=TSQL
selectStockQty.SQL=SELECT SUM(A.STOCK_QTY) AS STOCK_QTY ,SUM(A.VERIFYIN_PRICE*A.STOCK_QTY) AS STOCK_AMT  FROM IND_DDSTOCK A &
									 WHERE A.ORDER_CODE=<ORDER_CODE>  AND A.ORG_CODE=<ORG_CODE> AND TO_DATE(A.TRANDATE,'YYYYMMDD')=TO_DATE(<TRANDATE>,'YYYY/MM/DD') 

selectStockQty.Debug=N

