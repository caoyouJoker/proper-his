# 
#  Title:物资退货
# 
#  Description:物资退货
# 
#  Copyright: Copyright (c) ProperSoft
# 
#  author caowl 20130521
#  version 1.0
#
Module.item=insertData;insertDataForQtyCheck;updInvStockD;updInvStockM;updInvStockDD;insertDataForSingleUse;CheckUpdInvStockD

//插入退货数据
insertData.Type=TSQL
insertData.SQL=INSERT INTO INV_RETURNHIGH( &
			RETURN_NO, &
			SEQ, &
			INV_CODE, &
			RFID, &  
			RETURN_USER, &
			RETURN_DATE, &
			RETURN_DEPT, &
			SUP_CODE, &
			REASON, &
			OPT_USER, &
			OPT_DATE, &
			OPT_TERM, &
			QTY,ORGIN_CODE,BATCH_NO) &
		VALUES(<RETURN_NO>, &
			<SEQ>, &
			<INV_CODE>, &
			<RFID>, &
			<RETURN_USER>, &
			SYSDATE, &
			<RETURN_DEPT>, &
			<SUP_CODE>, &
			<REASON>, &
			<OPT_USER>, &
			SYSDATE, &
			<OPT_TERM>, &
			<QTY>,<ORGIN_CODE>,<BATCH_NO>)
insertData.Debug=N


//插入领用数据
insertDataForSingleUse.Type=TSQL
insertDataForSingleUse.SQL=INSERT INTO INV_USE( &
			USE_NO, &
			SEQ, &
			INV_CODE, &
			RFID, &
			USE_USER, &
			USE_DATE, &
			USE_DEPT, &
			SUP_CODE, &
			REASON, &
			OPT_USER, &
			OPT_DATE, &
			OPT_TERM, &
			QTY) &
		VALUES(<USE_NO>, &
			<SEQ>, &
			<INV_CODE>, &
			<RFID>, &
			<USE_USER>, &
			SYSDATE, &
			<USE_DEPT>, &
			<SUP_CODE>, &
			<REASON>, &
			<OPT_USER>, &
			SYSDATE, &
			<OPT_TERM>, &
			<QTY>)
insertDataForSingleUse.Debug=N


//插入盘点数据
insertDataForQtyCheck.Type=TSQL
insertDataForQtyCheck.SQL=INSERT INTO INV_QTYCHECK( &
			CHECK_NO, &
			SEQ, &
			INV_CODE, &
			ORG_CODE, &   
			CHECK_USER, &
			CHECK_DATE, &
			AGCHECK_USER, &
			AGCHECK_DATE, &
			STOCK_QTY, &
			MODI_QTY, &
			BASE_QTY, &
			OPT_USER, &
			OPT_DATE, &
			OPT_TERM, &
			LW) &
		VALUES(<CHECK_NO>, &
			<SEQ>, &
			<INV_CODE>, &
			<ORG_CODE>, &
			<CHECK_USER>, &
			<CHECK_DATE>, &
			<AGCHECK_USER>, &
			<AGCHECK_DATE>, &
			<STOCK_QTY>, &
			<MODI_QTY>, &
			<BASE_QTY>, &
			<OPT_USER>, &
			<OPT_DATE>, &
			<OPT_TERM>, &
			<LW>)
insertDataForQtyCheck.Debug=N


//扣库 更新INVStockD表
updInvStockD.Type=TSQL
updInvStockD.SQL=UPDATE INV_STOCKD SET &
			STOCK_QTY=STOCK_QTY-<QTY> , &
			DAYOUT_QTY=DAYOUT_QTY+<QTY>, &
			DAY_REQUESTOUT_QTY=DAY_REQUESTOUT_QTY+<QTY>, &
			DAY_CHANGEOUT_QTY=DAY_CHANGEOUT_QTY+<QTY>, &
			DAY_TRANSMITOUT_QTY=DAY_TRANSMITOUT_QTY+<QTY>, &
			DAY_WASTE_QTY=DAY_WASTE_QTY+<QTY>, &
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ>
updInvStockD.Debug=N

//盘点 更新INVStockD表
CheckUpdInvStockD.Type=TSQL
CheckUpdInvStockD.SQL=UPDATE INV_STOCKD SET &
			DAY_CHECKMODI_QTY=STOCK_QTY-<QTY> , &
			STOCK_QTY=<QTY>, &      
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ>
CheckUpdInvStockD.Debug=N

//扣库 更新INVStockM表
updInvStockM.Type=TSQL
updInvStockM.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updInvStockM.Debug=N

//扣库 更新INVStockDD表
updInvStockDD.Type=TSQL  
updInvStockDD.SQL=DELETE INV_STOCKDD WHERE RFID = <RFID>
updInvStockDD.Debug=N