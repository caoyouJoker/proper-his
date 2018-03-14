   #
   # Title:验收细档
   #
   # Description:验收细档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=createVerifyinD;queryVerifyinD;updateVerifyinD;deleteVerifyinD;deleteVerifyinDAll


//查询验收单细项
queryVerifyinD.Type=TSQL
queryVerifyinD.SQL=SELECT 'Y' AS SELECT_FLG, B.INV_CHN_DESC, B.DESCRIPTION, A.QTY AS VERIFIN_QTY,A.GIFT_QTY, &
                          A.BILL_UNIT, A.UNIT_PRICE * C.STOCK_QTY * C.DISPENSE_QTY AS UNIT_PRICE, &
                          A.IN_QTY * A.UNIT_PRICE AS VERIFYIN_AMT, A.BATCH_NO, &
                          A.VALID_DATE, A.REN_CODE, A.QUALITY_DEDUCT_AMT, A.IN_QTY, A.STOCK_UNIT, &
                          B.MAN_CODE, B.EXPENSIVE_FLG, B.VALIDATE_FLG, A.PURORDER_NO, A.SEQ_NO, &
                          A.STESEQ_NO, A.INV_CODE, C.STOCK_QTY, C.DISPENSE_QTY, B.SEQMAN_FLG &
                     FROM INV_VERIFYIND A, INV_BASE B, INV_TRANSUNIT C &
                     WHERE A.INV_CODE = B.INV_CODE &
                       AND A.INV_CODE = C.INV_CODE &
                       AND B.INV_CODE = C.INV_CODE
queryVerifyinD.ITEM=VERIFYIN_NO
queryVerifyinD.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
queryVerifyinD.Debug=N


//新建验收单细项
createVerifyinD.Type=TSQL
createVerifyinD.SQL=INSERT INTO INV_VERIFYIND( &
			VERIFYIN_NO , SEQ_NO , INV_CODE, QTY , GIFT_QTY ,&
			BILL_UNIT , IN_QTY , STOCK_UNIT, UNIT_PRICE , BATCH_NO ,&
			VALID_DATE , PURORDER_NO , STESEQ_NO, REN_CODE , QUALITY_DEDUCT_AMT ,&
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<VERIFYIN_NO> ,  <SEQ_NO> , <INV_CODE> , <QTY> , <GIFT_QTY> , &
	    	   	<BILL_UNIT> ,  <IN_QTY> , <STOCK_UNIT> , <UNIT_PRICE> , <BATCH_NO> , &
	    	   	<VALID_DATE> ,  <PURORDER_NO> , <STESEQ_NO> , <REN_CODE> , <QUALITY_DEDUCT_AMT> , &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> )
createVerifyinD.Debug=N


//更新验收单细项
updateVerifyinD.Type=TSQL
updateVerifyinD.SQL=UPDATE INV_VERIFYIND SET &
			   INV_CODE=<INV_CODE>, QTY=<QTY>, GIFT_QTY=<GIFT_QTY>, &
			   BILL_UNIT=<BILL_UNIT>, IN_QTY=<IN_QTY>, STOCK_UNIT=<STOCK_UNIT>, &
			   UNIT_PRICE=<UNIT_PRICE>, BATCH_NO=<BATCH_NO>, VALID_DATE=<VALID_DATE>, &
			   PURORDER_NO=<PURORDER_NO>, STESEQ_NO=<STESEQ_NO>, REN_CODE=<REN_CODE>, &
			   QUALITY_DEDUCT_AMT=<QUALITY_DEDUCT_AMT>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		     WHERE VERIFYIN_NO=<VERIFYIN_NO> AND SEQ_NO=<SEQ_NO>
updateVerifyinD.Debug=N


//删除验收细项
deleteVerifyinD.Type=TSQL
deleteVerifyinD.SQL=DELETE FROM INV_VERIFYIND WHERE VERIFYIN_NO=<VERIFYIN_NO> AND SEQ_NO=<SEQ_NO>
deleteVerifyinD.Debug=N


//删除验收细项(全部)
deleteVerifyinDAll.Type=TSQL
deleteVerifyinDAll.SQL=DELETE FROM INV_VERIFYIND WHERE VERIFYIN_NO=<VERIFYIN_NO>
deleteVerifyinDAll.Debug=N












