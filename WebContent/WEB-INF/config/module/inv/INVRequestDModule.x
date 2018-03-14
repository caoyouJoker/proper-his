   #
   # Title:申请明细
   #
   # Description:申请明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=createRequestD;queryRequestD;deleteRequestDAll;queryRequestDOut;updateActualQtyAndType


//新建申请明细
createRequestD.Type=TSQL
createRequestD.SQL=INSERT INTO INV_REQUESTD( &
			REQUEST_NO , SEQ_NO , INV_CODE, INVSEQ_NO , QTY ,&
			ACTUAL_QTY , FINA_TYPE, &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<REQUEST_NO> ,  <SEQ_NO> , <INV_CODE> , <INVSEQ_NO> , <QTY> , &
	    	   	<ACTUAL_QTY> , <FINA_TYPE>, &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> )
createRequestD.Debug=N


//查询申请明细
queryRequestD.Type=TSQL
queryRequestD.SQL=SELECT B.INV_CHN_DESC, A.INVSEQ_NO, B.DESCRIPTION, A.QTY, B.DISPENSE_UNIT, &
		         C.CONTRACT_PRICE, A.QTY * C.CONTRACT_PRICE AS SUM_AMT, &
		         A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,A.ACTUAL_QTY,A.FINA_TYPE, &
		         A.INV_CODE, A.SEQ_NO, B.SEQMAN_FLG, B.VALIDATE_FLG, B.INVKIND_CODE &
		    FROM INV_REQUESTD A, INV_BASE B,INV_AGENT C  &
		   WHERE A.INV_CODE = B.INV_CODE &
		    AND  A.INV_CODE = C.INV_CODE
queryRequestD.ITEM=REQUEST_NO
queryRequestD.REQUEST_NO=A.REQUEST_NO=<REQUEST_NO>
queryRequestD.Debug=N


//删除申请明细(全部)
deleteRequestDAll.Type=TSQL
deleteRequestDAll.SQL=DELETE FROM INV_REQUESTD WHERE REQUEST_NO=<REQUEST_NO>
deleteRequestDAll.Debug=N


//查询需要出库的申请细项
queryRequestDOut.Type=TSQL
queryRequestDOut.SQL=SELECT 'Y' AS SELECT_FLG, B.INV_CHN_DESC, A.INVSEQ_NO, B.DESCRIPTION, &
			       A.QTY - A.ACTUAL_QTY AS QTY, A.QTY AS REQUEST_QTY, A.ACTUAL_QTY, &
			       B.DISPENSE_UNIT, &
			       D.CONTRACT_PRICE , (A.QTY - A.ACTUAL_QTY) &
			       * D.CONTRACT_PRICE  AS SUM_AMT, &
			       A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, B.MAN_CODE, 'N' AS DISPOSAL_FLG, &
			       A.INV_CODE, A.SEQ_NO AS REQUEST_SEQ, B.SEQMAN_FLG, C.DISPENSE_QTY, C.STOCK_QTY &
			  FROM INV_REQUESTD A, INV_BASE B, INV_TRANSUNIT C,INV_AGENT D &
			 WHERE A.INV_CODE = B.INV_CODE &
			   AND A.INV_CODE = C.INV_CODE &
			   AND B.INV_CODE = C.INV_CODE &
			   AND  A.INV_CODE = D.INV_CODE &
			   AND A.FINA_TYPE IN ('0', '1')
queryRequestDOut.ITEM=REQUEST_NO
queryRequestDOut.REQUEST_NO=A.REQUEST_NO=<REQUEST_NO>
queryRequestDOut.Debug=N


//申请单细项状态(更新申请单细项出入库累积量和完成状态)
updateActualQtyAndType.Type=TSQL
updateActualQtyAndType.SQL=UPDATE INV_REQUESTD SET &
				  ACTUAL_QTY=<ACTUAL_QTY>+ACTUAL_QTY, FINA_TYPE=<FINA_TYPE>, &
				  OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	            WHERE REQUEST_NO =<REQUEST_NO> AND SEQ_NO=<SEQ_NO>
updateActualQtyAndType.Debug=N









