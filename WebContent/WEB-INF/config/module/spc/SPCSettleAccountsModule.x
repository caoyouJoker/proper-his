  #
   # Title: 结算功能模块
   #
   # Description:结算功能模块
   #
   # Copyright: BlueCore (c) 2013
   #
   # @author robo
Module.item=onSaveIndOdd;onSaveIndAccount;deleteIndOdd;deleteIndAccount

#
#保存暂估
#
#@author robo
#
onSaveIndOdd.Type=TSQL
onSaveIndOdd.SQL=INSERT INTO IND_ODD( &
			CLOSE_DATE, ORG_CODE, ORDER_CODE,LAST_ODD_QTY,OUT_QTY, &
			ODD_QTY,OPT_USER, OPT_DATE, OPT_TERM,SUP_ORDER_CODE,VERIFYIN_PRICE) &
	    	    VALUES( &
	    	   	<CLOSE_DATE>, <ORG_CODE>, <ORDER_CODE>, <LAST_ODD_QTY>, <OUT_QTY>, &
			<ODD_QTY>, <OPT_USER>, SYSDATE,  <OPT_TERM>,<SUP_ORDER_CODE>,<VERIFYIN_PRICE> )  
onSaveIndOdd.Debug=N

#
#保存结算
#
#@author robo
#
onSaveIndAccount.Type=TSQL
onSaveIndAccount.SQL=INSERT INTO IND_ACCOUNT( &
			CLOSE_DATE, ORG_CODE, ORDER_CODE,LAST_ODD_QTY,OUT_QTY, &
			TOTAL_OUT_QTY,TOTAL_UNIT_CODE,VERIFYIN_PRICE,VERIFYIN_AMT ,ACCOUNT_QTY,&
		        ACCOUNT_UNIT_CODE,ODD_QTY, ODD_AMT,OPT_USER, OPT_DATE, &
			OPT_TERM,REGION_CODE,CONTRACT_PRICE ,SUP_CODE,SUP_ORDER_CODE) &
	    	    VALUES( &
	    	   	<CLOSE_DATE>, <ORG_CODE>, <ORDER_CODE>,<LAST_ODD_QTY>,<OUT_QTY>, &
			<TOTAL_OUT_QTY>,<TOTAL_UNIT_CODE>,<VERIFYIN_PRICE>,<VERIFYIN_AMT> ,<ACCOUNT_QTY>,&
		        <ACCOUNT_UNIT_CODE>,<ODD_QTY>, <ODD_AMT>,<OPT_USER>, SYSDATE,   &
			<OPT_TERM>,<REGION_CODE>,<CONTRACT_PRICE>,<SUP_CODE>,<SUP_ORDER_CODE> )
 
onSaveIndAccount.Debug=N

//删除暂估
deleteIndOdd.Type=TSQL
deleteIndOdd.SQL=DELETE FROM IND_ODD WHERE CLOSE_DATE=<CLOSE_DATE>
deleteIndOdd.Debug=N

//删除结算
deleteIndAccount.Type=TSQL
deleteIndAccount.SQL=DELETE FROM IND_ACCOUNT WHERE CLOSE_DATE=<CLOSE_DATE>
deleteIndAccount.Debug=N




