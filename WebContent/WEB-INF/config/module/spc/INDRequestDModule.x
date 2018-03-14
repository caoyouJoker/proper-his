   #
   # Title:���뵥��ϸ
   #
   # Description:���뵥��ϸ
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/24

Module.item=updateActualQty;createNewRequestD;updateRequestFlg;updateActualQtyCancel;&
            updateActualQtyAcnt;updateRequestFlgAcnt;updateActualQtyCancelAcnt

//������������
updateActualQty.Type=TSQL
updateActualQty.SQL=UPDATE IND_REQUESTD SET &
			   ACTUAL_QTY=ACTUAL_QTY+<ACTUAL_QTY>, &
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE REQUEST_NO=<REQUEST_NO> &
		       AND SEQ_NO=<SEQ_NO>
updateActualQty.Debug=N

//������������ wanglong add 20150202
updateActualQtyAcnt.Type=TSQL
updateActualQtyAcnt.SQL=UPDATE SPC_REQUESTD SET &
               ACTUAL_QTY=ACTUAL_QTY+<ACTUAL_QTY>, &
               UPDATE_FLG=<UPDATE_FLG>, &
               OPT_USER=<OPT_USER>, &
               OPT_DATE=<OPT_DATE>, &
               OPT_TERM=<OPT_TERM> &
             WHERE REQUEST_NO=<REQUEST_NO> &
               AND SEQ_NO=<SEQ_NO>
updateActualQtyAcnt.Debug=N

createNewRequestD.Type=TSQL
createNewRequestD.SQL=INSERT INTO IND_REQUESTD( &
			REQUEST_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, &
			QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, &
			UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	    	   VALUES( &
	    	   	<REQUEST_NO>, <SEQ_NO>, <ORDER_CODE>, <BATCH_NO>, <VALID_DATE>, &
			<QTY>, <UNIT_CODE>, <RETAIL_PRICE>, <STOCK_PRICE>, <ACTUAL_QTY>, &
			<UPDATE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
createNewRequestD.Debug=N


//�������뵥״̬
updateRequestFlg.Type=TSQL
updateRequestFlg.SQL=UPDATE IND_REQUESTD SET &
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE REQUEST_NO=<REQUEST_NO> 
updateRequestFlg.Debug=N

//�������뵥״̬ wanglong add 20150202
updateRequestFlgAcnt.Type=TSQL
updateRequestFlgAcnt.SQL=UPDATE SPC_REQUESTD SET &
               UPDATE_FLG=<UPDATE_FLG>, &
               OPT_USER=<OPT_USER>, &
               OPT_DATE=<OPT_DATE>, &
               OPT_TERM=<OPT_TERM> &
             WHERE REQUEST_NO=<REQUEST_NO> 
updateRequestFlgAcnt.Debug=N

//ȡ������-��������D��
updateActualQtyCancel.Type=TSQL
updateActualQtyCancel.SQL=UPDATE IND_REQUESTD SET &
			   ACTUAL_QTY=0, &
			   UPDATE_FLG=0, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE REQUEST_NO=<REQUEST_NO>
updateActualQtyCancel.Debug=Y

//ȡ������-��������D�� wanglong add 20150202
updateActualQtyCancelAcnt.Type=TSQL
updateActualQtyCancelAcnt.SQL=UPDATE SPC_REQUESTD SET &
               ACTUAL_QTY=0, &
               UPDATE_FLG=0, &
               OPT_USER=<OPT_USER>, &
               OPT_DATE=<OPT_DATE>, &
               OPT_TERM=<OPT_TERM> &
             WHERE REQUEST_NO=<REQUEST_NO>
updateActualQtyCancelAcnt.Debug=Y