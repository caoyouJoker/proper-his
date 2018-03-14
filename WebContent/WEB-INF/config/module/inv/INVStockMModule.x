   #
   # Title: 库存主档
   #
   # Description: 库存主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=updateStockQty;updateStockQtyOut;createStockM;updateStockQtyByPack;updateStockQtyByReq;updateStockM;getStockQty;updateStockQtyGYS


//更新库存主档的库存量
updateStockQty.Type=TSQL
updateStockQty.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY+<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQty.Debug=N


//出库作业更新库存量
updateStockQtyOut.Type=TSQL
updateStockQtyOut.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyOut.Debug=N


//新增库存主档
createStockM.Type=TSQL
createStockM.SQL=INSERT INTO INV_STOCKM( &
			ORG_CODE, INV_CODE, REGION_CODE, DISPENSE_FLG, DISPENSE_ORG_CODE, &
			STOCK_FLG, MATERIAL_LOC_CODE, SAFE_QTY, MIN_QTY, MAX_QTY, &
			ECONOMICBUY_QTY, STOCK_QTY, MM_USE_QTY, AVERAGE_DAYUSE_QTY, STOCK_UNIT, &
			OPT_USER, OPT_DATE, OPT_TERM,BASE_QTY) &
			VALUES( &
			<ORG_CODE>, <INV_CODE>, <REGION_CODE>, <DISPENSE_FLG>, <DISPENSE_ORG_CODE>, &
			<STOCK_FLG>, <MATERIAL_LOC_CODE>, <SAFE_QTY>, <MIN_QTY>, <MAX_QTY>, &
			<ECONOMICBUY_QTY>, <STOCK_QTY>, <MM_USE_QTY>, <AVERAGE_DAYUSE_QTY>, <STOCK_UNIT>, &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>,<BASE_QTY>)
createStockM.Debug=N

//更新库存主档
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET &
			REGION_CODE=<REGION_CODE> , DISPENSE_FLG=<DISPENSE_FLG>, DISPENSE_ORG_CODE=<DISPENSE_ORG_CODE>, &
			MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>, SAFE_QTY=<SAFE_QTY>, MIN_QTY=<MIN_QTY>, &
			MAX_QTY=<MAX_QTY>, ECONOMICBUY_QTY=<ECONOMICBUY_QTY>, MM_USE_QTY=<MM_USE_QTY>,BASE_QTY=<BASE_QTY> , &
			AVERAGE_DAYUSE_QTY=<AVERAGE_DAYUSE_QTY>, OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockM.Debug=N

//手术包打包更新库存主档的库存量GYSUsed
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyByPack.Debug=N


//供应室出库作业更新库存量(请领作业)
updateStockQtyByReq.Type=TSQL
updateStockQtyByReq.SQL=UPDATE INV_STOCKM SET &
			       STOCK_QTY=STOCK_QTY-<QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	         WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updateStockQtyByReq.Debug=N


//查找全院物资库存量GYSUsed
getStockQty.Type=TSQL
getStockQty.SQL=SELECT SUM(STOCK_QTY) FROM INV_STOCKM 
getStockQty.item=INV_CODE;ORG_CODE;
getStockQty.INV_CODE=INV_CODE=<INV_CODE>
getStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockQty.Debug=N


//更新库存总量GYSUsed
updateStockQtyGYS.Type=TSQL
updateStockQtyGYS.SQL=UPDATE INV_STOCKM SET STOCK_QTY=STOCK_QTY+<STOCK_QTY>
updateStockQtyGYS.item=INV_CODE;ORG_CODE
updateStockQtyGYS.INV_CODE=INV_CODE=<INV_CODE>
updateStockQtyGYS.ORG_CODE=ORG_CODE=<ORG_CODE>
updateStockQtyGYS.Debug=N










