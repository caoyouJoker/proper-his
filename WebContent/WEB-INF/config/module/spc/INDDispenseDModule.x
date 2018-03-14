   #
   # Title:出入库单细项
   #
   # Description:出入库单细项
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/25
   
Module.item=createNewDispenseD;updateDispenseD;queryDispenseD;updateDispenseDToxic;updateDDrug;queryDDrug;&
            createNewDispenseDAcnt;queryDispenseDAcnt;updateDispenseDAcnt


//新增出入库单细项
//luhai 2012-01-12 add  batch_seq verifyin_price
createNewDispenseD.Type=TSQL
createNewDispenseD.SQL=INSERT INTO IND_DISPENSED( &
			DISPENSE_NO, SEQ_NO, REQUEST_SEQ, ORDER_CODE, BATCH_SEQ, &
			BATCH_NO, VALID_DATE, QTY, UNIT_CODE, RETAIL_PRICE, &
			STOCK_PRICE, ACTUAL_QTY, PHA_TYPE, OPT_USER, OPT_DATE, &
			OPT_TERM,VERIFYIN_PRICE,BOX_ESL_ID,BOXED_USER,BOXED_DATE, &
                        IS_BOXED,SUP_CODE,INVENT_PRICE,SUP_ORDER_CODE) &
	    	   VALUES( &
	    	   	<DISPENSE_NO>, <SEQ_NO>, <REQUEST_SEQ>, <ORDER_CODE>, <BATCH_SEQ>, &
			<BATCH_NO>, <VALID_DATE>, <QTY>, <UNIT_CODE>, <RETAIL_PRICE>, &
			<STOCK_PRICE>, <ACTUAL_QTY>, <PHA_TYPE>, <OPT_USER>, <OPT_DATE>, &
			<OPT_TERM>,<VERIFYIN_PRICE>,<BOX_ESL_ID>,<BOXED_USER>,SYSDATE,&
                        <IS_BOXED>,<SUP_CODE>,<INVENT_PRICE>,<SUP_ORDER_CODE>)
createNewDispenseD.Debug=N

//新增出入库单细项 wanglong add 20150202
createNewDispenseDAcnt.Type=TSQL
createNewDispenseDAcnt.SQL=INSERT INTO SPC_DISPENSED( &
            DISPENSE_NO, SEQ_NO, REQUEST_SEQ, ORDER_CODE, BATCH_SEQ, &
            BATCH_NO, VALID_DATE, QTY, UNIT_CODE, RETAIL_PRICE, &
            STOCK_PRICE, ACTUAL_QTY, PHA_TYPE, OPT_USER, OPT_DATE, &  
            OPT_TERM,VERIFYIN_PRICE,BOX_ESL_ID,BOXED_USER,BOXED_DATE, &
                        IS_BOXED,SUP_CODE,INVENT_PRICE,SUP_ORDER_CODE,RETAILBOX_PRICE) &
               VALUES( &
                <DISPENSE_NO>, <SEQ_NO>, <REQUEST_SEQ>, <ORDER_CODE>, <BATCH_SEQ>, &
            <BATCH_NO>, <VALID_DATE>, <QTY>, <UNIT_CODE>, <RETAIL_PRICE>, &  
            <STOCK_PRICE>, <ACTUAL_QTY>, <PHA_TYPE>, <OPT_USER>, <OPT_DATE>, &
            <OPT_TERM>,<VERIFYIN_PRICE>,<BOX_ESL_ID>,<BOXED_USER>,SYSDATE,&
                        <IS_BOXED>,<SUP_CODE>,<INVENT_PRICE>,<SUP_ORDER_CODE>,<RETAILBOX_PRICE>)
createNewDispenseDAcnt.Debug=N

//更新细项
updateDispenseD.Type=TSQL
updateDispenseD.SQL=UPDATE IND_DISPENSED SET &
			   STOCK_PRICE=<STOCK_PRICE>, &
			   ACTUAL_QTY=<ACTUAL_QTY>, &
			   PUTAWAY_USER=<PUTAWAY_USER>, &
			   IS_PUTAWAY=<IS_PUTAWAY>, &
			   PUTAWAY_DATE=SYSDATE, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO> &
		       AND SEQ_NO=<SEQ_NO>
updateDispenseD.Debug=N

//更新细项 wanglong add 20150202
updateDispenseDAcnt.Type=TSQL
updateDispenseDAcnt.SQL=UPDATE SPC_DISPENSED SET &
               STOCK_PRICE=<STOCK_PRICE>, &
               ACTUAL_QTY=<ACTUAL_QTY>, &
               PUTAWAY_USER=<PUTAWAY_USER>, &
               IS_PUTAWAY=<IS_PUTAWAY>, &
               PUTAWAY_DATE=SYSDATE, &
               OPT_USER=<OPT_USER>, &
               OPT_DATE=<OPT_DATE>, &
               OPT_TERM=<OPT_TERM> &
             WHERE DISPENSE_NO=<DISPENSE_NO> &
               AND SEQ_NO=<SEQ_NO>
updateDispenseDAcnt.Debug=N

//查询是否全部上架
queryDispenseD.Type=TSQL
queryDispenseD.SQL=SELECT DISPENSE_NO,IS_PUTAWAY  &
		   FROM  IND_DISPENSED   &
		   WHERE DISPENSE_NO=<DISPENSE_NO> &
		         AND IS_PUTAWAY=<IS_PUTAWAY>
queryDispenseD.Debug=N

//查询是否全部上架 wanglong add 20150202
queryDispenseDAcnt.Type=TSQL
queryDispenseDAcnt.SQL=SELECT DISPENSE_NO,IS_PUTAWAY  &
           FROM  SPC_DISPENSED   &
           WHERE DISPENSE_NO=<DISPENSE_NO> &
                 AND IS_PUTAWAY=<IS_PUTAWAY>
queryDispenseDAcnt.Debug=N

updateDispenseDToxic.Type=TSQL

updateDispenseDToxic.SQL=UPDATE IND_DISPENSED SET &
			   STOCK_PRICE=<STOCK_PRICE>, &
		           IS_PUTAWAY=<IS_PUTAWAY>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO> &
		       AND SEQ_NO=<SEQ_NO>

updateDispenseDToxic.Debug=N


queryDDrug.Type=TSQL
queryDDrug.SQL=SELECT DISPENSE_NO,SEQ_NO  &
		   FROM  IND_DISPENSED   &
		   WHERE DISPENSE_NO=<DISPENSE_NO> &
		         AND SEQ_NO=<SEQ_NO>
queryDDrug.Debug=N

updateDDrug.Type=TSQL
updateDDrug.SQL=UPDATE IND_DISPENSED SET &
			   ACTUAL_QTY=ACTUAL_QTY+<ACTUAL_QTY>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO> &
		       AND SEQ_NO=<SEQ_NO>
updateDDrug.Debug=N


