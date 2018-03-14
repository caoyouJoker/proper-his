   #
   # Title:订购明细
   #
   # Description:订购明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=queryPurOrderD;updatePlanDVer;queryPurOrderQTY;createPurorderD

//查询订购明细
queryPurOrderD.Type=TSQL
queryPurOrderD.SQL=SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, &
			  BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, &
			  OPT_USER, OPT_DATE, OPT_TERM &
		     FROM IND_PURORDERD
queryPurOrderD.ITEM=PURORDER_NO;SEQ_NO;ORDER_CODE
queryPurOrderD.PURORDER_NO=PURORDER_NO=<PURORDER_NO>
queryPurOrderD.SEQ_NO=SEQ_NO=<SEQ_NO>
queryPurOrderD.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
queryPurOrderD.Debug=N


//累计验收数、累计品质扣款更新
updatePlanDVer.Type=TSQL
updatePlanDVer.SQL=UPDATE IND_PURORDERD &
		     SET UPDATE_FLG=<UPDATE_FLG>, &
			 ACTUAL_QTY=ACTUAL_QTY+<VERIFYIN_QTY>, &
			 QUALITY_DEDUCT_AMT=QUALITY_DEDUCT_AMT+<QUALITY_DEDUCT_AMT> &
		     WHERE PURORDER_NO=<PURORDER_NO> &
		       AND SEQ_NO=<SEQ_NO>
updatePlanDVer.Debug=N


//查询累计生成量
queryPurOrderQTY.Type=TSQL
queryPurOrderQTY.SQL=SELECT SUM(A.PURORDER_QTY) AS PURORDER_QTY &
		       FROM IND_PURORDERD A , IND_PURORDERM B &
		      WHERE A.PURORDER_NO = B.PURORDER_NO &
		        AND A.ORDER_CODE = <ORDER_CODE> &
		        AND B.PLAN_NO = <PLAN_NO>
queryPurOrderQTY.Debug=N

//新增订购细项
createPurorderD.Type=TSQL
createPurorderD.SQL=INSERT INTO IND_PURORDERD( &
		      PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, &
		      BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, &
		      OPT_USER, OPT_DATE, OPT_TERM) &
		    VALUES( &
		      <PURORDER_NO>, <SEQ_NO>, <ORDER_CODE>, <PURORDER_QTY>, <GIFT_QTY>, &
		      <BILL_UNIT>, <PURORDER_PRICE>, <ACTUAL_QTY>, <QUALITY_DEDUCT_AMT>, <UPDATE_FLG>, &
		      <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
createPurorderD.Debug=N



