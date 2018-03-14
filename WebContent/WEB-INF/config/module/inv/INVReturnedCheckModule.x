   #
   # Title:退货核对
   #
   # Description:退货核对
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/11/22

Module.item=insertReturnM;insertReturnD;insertReturnDD;queryReturnM;queryReturnD;queryReturnDD;updateReturnD;delReturnM;delReturnD;delReturnDD;updateConfirmStatus;updateReturnDSec;updateSpcRecordCheck;updateSpcRecordUnCheck;updateStockMQTY;updateStockDQTY



//插入退货单主表
insertReturnM.Type=TSQL
insertReturnM.SQL=INSERT INTO INV_SUP_RETURNM ( &
			RETURNED_NO,FROM_ORG_CODE,TO_ORG_CODE,CONFIRM_FLG, & 
			OPT_USER,OPT_DATE,OPT_TERM,CHECK_USER,CHECK_DATE ) &
	    	      VALUES ( &
	    	   	<RETURNED_NO>, <FROM_ORG_CODE>, <TO_ORG_CODE>, <CONFIRM_FLG>, & 
			<OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <CHECK_USER>, TO_DATE(<CHECK_DATE>,'yyyy/mm/dd hh24:mi:ss') )
insertReturnM.Debug=N

//插入退货单细表
insertReturnD.Type=TSQL
insertReturnD.SQL=INSERT INTO INV_SUP_RETURND ( &
			RETURNED_NO,SEQ,INV_CODE,QTY,ACTUAL_QTY, & 
			OPT_USER,OPT_DATE,OPT_TERM,UNIT_CODE ) &
			VALUES ( &
			<RETURNED_NO>, <SEQ>, <INV_CODE>, <QTY>, <ACTUAL_QTY>, &
			<OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <UNIT_CODE> )
insertReturnD.Debug=N


//插入退货单细细表
insertReturnDD.Type=TSQL
insertReturnDD.SQL=INSERT INTO INV_SUP_RETURNDD ( &
			RETURNED_NO,SEQ,INV_CODE,QTY, & 
			UNIT_CODE,PACK_CODE,PACK_GROUP_NO,MR_NO,PAT_NAME, &
			OPT_USER,OPT_DATE,OPT_TERM,BUSINESS_NO,BUSINESS_SEQ ) &
			VALUES ( &
			<RETURNED_NO>, <SEQ>, <INV_CODE>, <QTY>,  &
			<UNIT_CODE>, <PACK_CODE>, <PACK_GROUP_NO>, <MR_NO>, <PAT_NAME>, &
			<OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <BUSINESS_NO>, <BUSINESS_SEQ> )
insertReturnDD.Debug=N



//根据条件查询退货单主表
queryReturnM.Type=TSQL
queryReturnM.SQL=SELECT M.RETURNED_NO, M.FROM_ORG_CODE, M.TO_ORG_CODE, M.CONFIRM_FLG, M.OPT_USER, M.OPT_DATE, &
			M.OPT_TERM, M.CONFIRM_USER, M.CONFIRM_DATE &
  	      FROM INV_SUP_RETURNM M &
	      ORDER BY M.RETURNED_NO DESC
queryReturnM.ITEM=RETURNED_NO;FROM_ORG_CODE;TO_ORG_CODE;START_DATE;END_DATE;CONFIRM_FLG
queryReturnM.RETURNED_NO=RETURNED_NO=<RETURNED_NO>
queryReturnM.FROM_ORG_CODE=FROM_ORG_CODE=<FROM_ORG_CODE>
queryReturnM.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryReturnM.START_DATE=OPT_DATE>=TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss')
queryReturnM.END_DATE=OPT_DATE<=TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss')
queryReturnM.CONFIRM_FLG=CONFIRM_FLG=<CONFIRM_FLG>
queryReturnM.Debug=N



//根据条件查询退货单细表
queryReturnD.Type=TSQL
queryReturnD.SQL=SELECT D.RETURNED_NO, D.SEQ, D.INV_CODE, D.UNIT_CODE, B.INV_CHN_DESC, D.QTY, D.ACTUAL_QTY, D.OPT_USER, D.OPT_DATE, &
			D.OPT_TERM &
  	      FROM INV_SUP_RETURND D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE &
	      ORDER BY D.SEQ ASC
queryReturnD.ITEM=RETURNED_NO
queryReturnD.RETURNED_NO=RETURNED_NO=<RETURNED_NO>
queryReturnD.Debug=N 



//根据条件查询退货单细细表
queryReturnDD.Type=TSQL
queryReturnDD.SQL=SELECT D.RETURNED_NO, D.SEQ, D.INV_CODE, D.UNIT_CODE, B.INV_CHN_DESC, D.QTY, P.PACK_DESC, D.MR_NO, D.PAT_NAME &
  	      FROM INV_SUP_RETURNDD D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE LEFT JOIN INV_PACKM P ON SUBSTR(D.PACK_CODE, 1, 6) = P.PACK_CODE &
	      ORDER BY D.SEQ ASC
queryReturnDD.ITEM=RETURNED_NO
queryReturnDD.RETURNED_NO=RETURNED_NO=<RETURNED_NO>
queryReturnDD.Debug=N 


//根据条件更新退货单细表
updateReturnD.Type=TSQL
updateReturnD.SQL=UPDATE INV_SUP_RETURND SET ACTUAL_QTY = <ACTUAL_QTY> WHERE RETURNED_NO = <RETURNED_NO> AND SEQ = <SEQ> 
updateReturnD.Debug=N



//删除退货单主表信息
delReturnM.Type=TSQL
delReturnM.SQL=DELETE INV_SUP_RETURNM WHERE RETURNED_NO = <RETURNED_NO>
delReturnM.Debug=N


//删除退货单细表信息
delReturnD.Type=TSQL
delReturnD.SQL=DELETE INV_SUP_RETURND WHERE RETURNED_NO = <RETURNED_NO>
delReturnD.Debug=N


//删除退货单细细表信息
delReturnDD.Type=TSQL
delReturnDD.SQL=DELETE INV_SUP_RETURNDD WHERE RETURNED_NO = <RETURNED_NO>
delReturnDD.Debug=N


//修改退货单确认状态
updateConfirmStatus.Type=TSQL
updateConfirmStatus.SQL=UPDATE INV_SUP_RETURNM SET CONFIRM_FLG = 'Y', CONFIRM_USER = <CONFIRM_USER>, CONFIRM_DATE = TO_DATE(<CONFIRM_DATE>,'yyyy/mm/dd hh24:mi:ss') WHERE RETURNED_NO = <RETURNED_NO> 
updateConfirmStatus.Debug=N


//根据条件更新退货单细表(包括确认人员和时间)
updateReturnDSec.Type=TSQL
updateReturnDSec.SQL=UPDATE INV_SUP_RETURND SET ACTUAL_QTY = <ACTUAL_QTY>, CONFIRM_USER = <CONFIRM_USER>, CONFIRM_DATE = TO_DATE(<CONFIRM_DATE>,'yyyy/mm/dd hh24:mi:ss') WHERE RETURNED_NO = <RETURNED_NO> AND SEQ = <SEQ>
updateReturnDSec.Debug=N


//更新耗用记录表记录（从未核对---->已核对）
updateSpcRecordCheck.Type=TSQL
updateSpcRecordCheck.SQL=UPDATE SPC_INV_RECORD SET CHECK_FLG = 'Y', CHECK_NO = <RETURNED_NO> & 
				WHERE EXE_DEPT_CODE = <FROM_ORG_CODE> AND CHECK_FLG = 'N' AND CHECK_NO IS NULL AND PACK_BARCODE IS NOT NULL AND SUBSTR(PACK_BARCODE, 7, 6) = '000000'
updateSpcRecordCheck.Debug=N

//更新耗用记录表记录（从已核对---->未核对）
updateSpcRecordUnCheck.Type=TSQL
updateSpcRecordUnCheck.SQL=UPDATE SPC_INV_RECORD SET CHECK_FLG = 'N', CHECK_NO = '' &
				WHERE CHECK_NO = <RETURNED_NO> 
updateSpcRecordUnCheck.Debug=N


//加回stockm库存
updateStockMQTY.Type=TSQL
updateStockMQTY.SQL=UPDATE INV_STOCKM SET STOCK_QTY = STOCK_QTY + <ACTUAL_QTY> WHERE INV_CODE = <INV_CODE> AND ORG_CODE = <TO_ORG_CODE>
updateStockMQTY.Debug=N


//加回stockd库存
updateStockDQTY.Type=TSQL
updateStockDQTY.SQL=UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY + <ACTUAL_QTY> WHERE INV_CODE = <INV_CODE> AND ORG_CODE = <TO_ORG_CODE> AND BATCH_SEQ = ( SELECT MAX(BATCH_SEQ) FROM INV_STOCKD WHERE INV_CODE = <INV_CODE> AND ORG_CODE = <TO_ORG_CODE> )  
updateStockDQTY.Debug=N
