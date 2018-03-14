   #
   # Title:出入库单细项
   #
   # Description:出入库单细项
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/25
   
Module.item=createNewDispenseD;updateDispenseD


//新增出入库单细项
//luhai 2012-01-12 add  batch_seq verifyin_price
createNewDispenseD.Type=TSQL
createNewDispenseD.SQL=INSERT INTO IND_DISPENSED( &
			DISPENSE_NO, SEQ_NO, REQUEST_SEQ, ORDER_CODE, BATCH_SEQ, &
			BATCH_NO, VALID_DATE, QTY, UNIT_CODE, RETAIL_PRICE, &
			STOCK_PRICE, ACTUAL_QTY, PHA_TYPE, OPT_USER, OPT_DATE, &
			OPT_TERM,VERIFYIN_PRICE) &
	    	   VALUES( &
	    	   	<DISPENSE_NO>, <SEQ_NO>, <REQUEST_SEQ>, <ORDER_CODE>, <BATCH_SEQ>, &
			<BATCH_NO>, <VALID_DATE>, <QTY>, <UNIT_CODE>, <RETAIL_PRICE>, &
			<STOCK_PRICE>, <ACTUAL_QTY>, <PHA_TYPE>, <OPT_USER>, <OPT_DATE>, &
			<OPT_TERM>,<VERIFYIN_PRICE>)
createNewDispenseD.Debug=N


//更新细项
updateDispenseD.Type=TSQL
updateDispenseD.SQL=UPDATE IND_DISPENSED SET &
			   STOCK_PRICE=<STOCK_PRICE>, &
			   ACTUAL_QTY=<ACTUAL_QTY>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO> &
		       AND SEQ_NO=<SEQ_NO>
updateDispenseD.Debug=N

