   #
   # Title:药库库存明细档
   #
   # Description:药库库存明细档
   #
   # Copyright: JavaHis (c) 2009
   #  
   # @author zhangy 2009/04/29

Module.item=queryStockQTY;updateStockQtyOut;updateStockQtyCancelOut;updateStockQtyVer;createNewStockD;&
	    updateStockQtyReg;queryStockQTYByBatch;queryStockQTYByBatchAll;updateStockQtyDisOut;updateStockQtyDisIn;queryQtyCheck;&
	    updateStockQtyRegOut;updateStockQtyIn;updateStockQtyRegIn;updateQtyCheck;updateUnLockQtyCheck;&
	    getDDStock;updateOutQtyToZero;updateStockQtyDisOutReq;updateStockQtyDisOutGif;updateStockQtyDisOutRet;&
	    updateStockQtyDisOutWas;updateStockQtyDisOutTho;updateStockQtyDisOutCos;updateStockQtyDisInReq;updateStockQtyDisInGif;&
	    updateStockQtyDisInReq;updateStockQtyDisInThi;updateProfitLossAmt;getOrgStockQuery;getOrgStockQueryNotBatch;&
	    updateStockQtyDisInRet;getDrugMianStockQuery;onQueryLockQTY;updateIndStockLockQty;getOrgStockDrugQuery;&
	    querySPCStockQTY;updateSPCStockQtyReg;querySPCStockQTYByBatch;updateStockQtyDisOutReqAcnt;updateStockQtyDisOutGifAcnt;&
	    updateStockQtyDisOutRetAcnt;updateStockQtyDisOutWasAcnt;updateStockQtyDisOutThoAcnt;updateStockQtyDisOutCosAcnt;&
	    updateStockQtyVerAcnt;createNewStockDAcnt;updateSPCStockQtyDisInReq;updateStockQtyDisInGifAcnt;updateStockQtyDisInRetAcnt;updateStockQtyDisInThiAcnt;updateStockQtyDisOutSrd
//根据药库编号及药品代码查询药库库存量
queryStockQTY.Type=TSQL
queryStockQTY.SQL=SELECT SUM(STOCK_QTY) AS QTY &
			FROM IND_STOCK &
			WHERE 1=1
			  //ACTIVE_FLG='Y' &    
			  //AND SYSDATE < VALID_DATE
queryStockQTY.ITEM=ORG_CODE;ORDER_CODE
queryStockQTY.ORG_CODE=ORG_CODE=<ORG_CODE>
queryStockQTY.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
queryStockQTY.Debug=Y

//根据药库编号及药品代码查询药库库存量 wanglong add 20150202
querySPCStockQTY.Type=TSQL
querySPCStockQTY.SQL=SELECT SUM(STOCK_QTY) AS QTY &
            FROM SPC_STOCK &
            WHERE 1=1
              //ACTIVE_FLG='Y' &    
              //AND SYSDATE < VALID_DATE
querySPCStockQTY.ITEM=ORG_CODE;ORDER_CODE
querySPCStockQTY.ORG_CODE=ORG_CODE=<ORG_CODE>
querySPCStockQTY.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
querySPCStockQTY.Debug=N


//更新库存量(取消出库,退药)
updateStockQtyCancelOut.Type=TSQL
updateStockQtyCancelOut.SQL=UPDATE IND_STOCK &
				SET OUT_QTY=OUT_QTY+<OUT_QTY>, OUT_AMT=OUT_AMT+<OUT_AMT>, STOCK_QTY=STOCK_QTY-<OUT_QTY> &
				WHERE ORG_CODE=<ORG_CODE> &
			    	  AND ORDER_CODE=<ORDER_CODE> &
			  	  AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyCancelOut.Debug=N


//更新库存量(验收入库)
updateStockQtyVer.Type=TSQL
updateStockQtyVer.SQL=UPDATE IND_STOCK &
			SET IN_QTY=IN_QTY+<IN_QTY>, &
			    IN_AMT=IN_AMT+<IN_AMT>, &
			    STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			    VERIFYIN_QTY=VERIFYIN_QTY+<VERIFYIN_QTY>, &
			    VERIFYIN_AMT=VERIFYIN_AMT+<VERIFYIN_AMT>, &
			    VERIFYIN_PRICE=<VERIFYIN_PRICE>, &
			    FAVOR_QTY=FAVOR_QTY+<FAVOR_QTY>, &
			    PROFIT_LOSS_AMT=PROFIT_LOSS_AMT+<PROFIT_LOSS_AMT>, &
			    RETAIL_PRICE=<RETAIL_PRICE> &
				,INVENT_PRICE=<INVENT_PRICE> &
		        WHERE ORG_CODE=<ORG_CODE> &
			  AND ORDER_CODE=<ORDER_CODE> &
			  AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyVer.Debug=N

//更新库存量(验收入库) wanglong add 20150202
updateStockQtyVerAcnt.Type=TSQL
updateStockQtyVerAcnt.SQL=UPDATE SPC_STOCK &
            SET IN_QTY=IN_QTY+<IN_QTY>, &
                IN_AMT=IN_AMT+<IN_AMT>, &
                STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
                VERIFYIN_QTY=VERIFYIN_QTY+<VERIFYIN_QTY>, &
                VERIFYIN_AMT=VERIFYIN_AMT+<VERIFYIN_AMT>, &
                VERIFYIN_PRICE=<VERIFYIN_PRICE>, &
                FAVOR_QTY=FAVOR_QTY+<FAVOR_QTY>, &
                PROFIT_LOSS_AMT=PROFIT_LOSS_AMT+<PROFIT_LOSS_AMT>, &
                RETAIL_PRICE=<RETAIL_PRICE> &
                WHERE ORG_CODE=<ORG_CODE> &
              AND ORDER_CODE=<ORDER_CODE> &
              AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyVerAcnt.Debug=N

//新增药库库存明细   modify yxm 新增一个字段SUP_CODE
createNewStockD.Type=TSQL
createNewStockD.SQL=INSERT INTO IND_STOCK( &
			ORG_CODE, ORDER_CODE, BATCH_SEQ, BATCH_NO, VALID_DATE, &
			REGION_CODE, MATERIAL_LOC_CODE, ACTIVE_FLG, STOCK_FLG, READJUSTP_FLG, &
			STOCK_QTY, LAST_TOTSTOCK_QTY, LAST_TOTSTOCK_AMT, IN_QTY, IN_AMT, &
			OUT_QTY, OUT_AMT, CHECKMODI_QTY, CHECKMODI_AMT, VERIFYIN_QTY, &
			VERIFYIN_AMT, FAVOR_QTY, REGRESSGOODS_QTY, REGRESSGOODS_AMT, DOSEAGE_QTY, &
			DOSAGE_AMT, REGRESSDRUG_QTY, REGRESSDRUG_AMT, FREEZE_TOT, PROFIT_LOSS_AMT, &
			VERIFYIN_PRICE, STOCKIN_QTY, STOCKIN_AMT, STOCKOUT_QTY, STOCKOUT_AMT, &
			REQUEST_IN_QTY,REQUEST_IN_AMT,REQUEST_OUT_QTY,REQUEST_OUT_AMT,GIF_IN_QTY, &
			GIF_IN_AMT,GIF_OUT_QTY,GIF_OUT_AMT,RET_IN_QTY,RET_IN_AMT, &
			RET_OUT_QTY,RET_OUT_AMT,WAS_OUT_QTY,WAS_OUT_AMT,THO_OUT_QTY, &
			THO_OUT_AMT,THI_IN_QTY,THI_IN_AMT,COS_OUT_QTY,COS_OUT_AMT, &
			OPT_USER, OPT_DATE, OPT_TERM, RETAIL_PRICE,SUP_CODE,INVENT_PRICE,SUP_ORDER_CODE) &
	    	    VALUES( &
	    	   	<ORG_CODE>, <ORDER_CODE>, <BATCH_SEQ>, <BATCH_NO>, <VALID_DATE>, &
			<REGION_CODE>, <MATERIAL_LOC_CODE>, <ACTIVE_FLG>, <STOCK_FLG>, <READJUSTP_FLG>, &
			<STOCK_QTY>, <LAST_TOTSTOCK_QTY>, <LAST_TOTSTOCK_AMT>, <IN_QTY>, <IN_AMT>, &
			<OUT_QTY>, <OUT_AMT>, <CHECKMODI_QTY>, <CHECKMODI_AMT>, <VERIFYIN_QTY>, &
			<VERIFYIN_AMT>, <FAVOR_QTY>, <REGRESSGOODS_QTY>, <REGRESSGOODS_AMT>, <DOSEAGE_QTY>, &
			<DOSAGE_AMT>, <REGRESSDRUG_QTY>, <REGRESSDRUG_AMT>, <FREEZE_TOT>, <PROFIT_LOSS_AMT>, &
			<VERIFYIN_PRICE>, <STOCKIN_QTY>, <STOCKIN_AMT>, <STOCKOUT_QTY>, <STOCKOUT_AMT>, &
			<REQUEST_IN_QTY>,<REQUEST_IN_AMT>,<REQUEST_OUT_QTY>,<REQUEST_OUT_AMT>,<GIF_IN_QTY>, &
			<GIF_IN_AMT>,<GIF_OUT_QTY>,<GIF_OUT_AMT>,<RET_IN_QTY>,<RET_IN_AMT>, &
			<RET_OUT_QTY>,<RET_OUT_AMT>,<WAS_OUT_QTY>,<WAS_OUT_AMT>,<THO_OUT_QTY>, &
			<THO_OUT_AMT>,<THI_IN_QTY>,<THI_IN_AMT>,<COS_OUT_QTY>,<COS_OUT_AMT>, &
			<OPT_USER>, SYSDATE, <OPT_TERM>, <RETAIL_PRICE>,<SUP_CODE>,<INVENT_PRICE>,<SUP_ORDER_CODE>)
createNewStockD.Debug=N

//新增药库库存明细   modify yxm 新增一个字段SUP_CODE wanglong add 20150202
createNewStockDAcnt.Type=TSQL
createNewStockDAcnt.SQL=INSERT INTO SPC_STOCK( &
			ORG_CODE, ORDER_CODE, BATCH_SEQ, BATCH_NO, VALID_DATE, &
			REGION_CODE, MATERIAL_LOC_CODE, ACTIVE_FLG, STOCK_FLG, READJUSTP_FLG, &
			STOCK_QTY, LAST_TOTSTOCK_QTY, LAST_TOTSTOCK_AMT, IN_QTY, IN_AMT, &
			OUT_QTY, OUT_AMT, CHECKMODI_QTY, CHECKMODI_AMT, VERIFYIN_QTY, &
			VERIFYIN_AMT, FAVOR_QTY, REGRESSGOODS_QTY, REGRESSGOODS_AMT, DOSEAGE_QTY, &
			DOSAGE_AMT, REGRESSDRUG_QTY, REGRESSDRUG_AMT, FREEZE_TOT, PROFIT_LOSS_AMT, &
			VERIFYIN_PRICE, STOCKIN_QTY, STOCKIN_AMT, STOCKOUT_QTY, STOCKOUT_AMT, &
			REQUEST_IN_QTY,REQUEST_IN_AMT,REQUEST_OUT_QTY,REQUEST_OUT_AMT,GIF_IN_QTY, &
			GIF_IN_AMT,GIF_OUT_QTY,GIF_OUT_AMT,RET_IN_QTY,RET_IN_AMT, &
			RET_OUT_QTY,RET_OUT_AMT,WAS_OUT_QTY,WAS_OUT_AMT,THO_OUT_QTY, &
			THO_OUT_AMT,THI_IN_QTY,THI_IN_AMT,COS_OUT_QTY,COS_OUT_AMT, &
			OPT_USER, OPT_DATE, OPT_TERM, RETAIL_PRICE,SUP_CODE,INVENT_PRICE,SUP_ORDER_CODE) &
	    	    VALUES( &
	    	   	<ORG_CODE>, <ORDER_CODE>, <BATCH_SEQ>, <BATCH_NO>, <VALID_DATE>, &
			<REGION_CODE>, <MATERIAL_LOC_CODE>, <ACTIVE_FLG>, <STOCK_FLG>, <READJUSTP_FLG>, &
			<STOCK_QTY>, <LAST_TOTSTOCK_QTY>, <LAST_TOTSTOCK_AMT>, <IN_QTY>, <IN_AMT>, &
			<OUT_QTY>, <OUT_AMT>, <CHECKMODI_QTY>, <CHECKMODI_AMT>, <VERIFYIN_QTY>, &
			<VERIFYIN_AMT>, <FAVOR_QTY>, <REGRESSGOODS_QTY>, <REGRESSGOODS_AMT>, <DOSEAGE_QTY>, &
			<DOSAGE_AMT>, <REGRESSDRUG_QTY>, <REGRESSDRUG_AMT>, <FREEZE_TOT>, <PROFIT_LOSS_AMT>, &
			<VERIFYIN_PRICE>, <STOCKIN_QTY>, <STOCKIN_AMT>, <STOCKOUT_QTY>, <STOCKOUT_AMT>, &
			<REQUEST_IN_QTY>,<REQUEST_IN_AMT>,<REQUEST_OUT_QTY>,<REQUEST_OUT_AMT>,<GIF_IN_QTY>, &
			<GIF_IN_AMT>,<GIF_OUT_QTY>,<GIF_OUT_AMT>,<RET_IN_QTY>,<RET_IN_AMT>, &
			<RET_OUT_QTY>,<RET_OUT_AMT>,<WAS_OUT_QTY>,<WAS_OUT_AMT>,<THO_OUT_QTY>, &
			<THO_OUT_AMT>,<THI_IN_QTY>,<THI_IN_AMT>,<COS_OUT_QTY>,<COS_OUT_AMT>, &
			<OPT_USER>, SYSDATE, <OPT_TERM>, <RETAIL_PRICE>,<SUP_CODE>,<INVENT_PRICE>,<SUP_ORDER_CODE>)
createNewStockDAcnt.Debug=N

//更新库存量(退货出库)
updateStockQtyReg.Type=TSQL
updateStockQtyReg.SQL=UPDATE IND_STOCK &
			SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    STOCK_QTY=STOCK_QTY-<OUT_QTY>, &
			    REGRESSGOODS_QTY=REGRESSGOODS_QTY+<OUT_QTY>, &
			    REGRESSGOODS_AMT=REGRESSGOODS_AMT+<OUT_AMT>, &
			    OPT_USER=<OPT_USER>, &
			    OPT_DATE=<OPT_DATE>, &
			    OPT_TERM=<OPT_TERM> &
		        WHERE ORG_CODE=<ORG_CODE> &
			  AND ORDER_CODE=<ORDER_CODE> &
			  AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyReg.Debug=N

//更新库存量(退货出库) wanglong add 20150202
updateSPCStockQtyReg.Type=TSQL
updateSPCStockQtyReg.SQL=UPDATE SPC_STOCK &
            SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                OUT_AMT=OUT_AMT+<OUT_AMT>, &
                STOCK_QTY=STOCK_QTY-<OUT_QTY>, &
                REGRESSGOODS_QTY=REGRESSGOODS_QTY+<OUT_QTY>, &
                REGRESSGOODS_AMT=REGRESSGOODS_AMT+<OUT_AMT>, &
                OPT_USER=<OPT_USER>, &
                OPT_DATE=<OPT_DATE>, &
                OPT_TERM=<OPT_TERM> &
                WHERE ORG_CODE=<ORG_CODE> &
              AND ORDER_CODE=<ORDER_CODE> &
              AND BATCH_SEQ=<BATCH_SEQ>
updateSPCStockQtyReg.Debug=N

//查询指定批次药品出库部门的库存
queryStockQTYByBatch.Type=TSQL
queryStockQTYByBatch.SQL=SELECT STOCK_QTY   AS QTY &
			   FROM IND_STOCK &
			  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ> 
			    //AND ACTIVE_FLG='Y'
queryStockQTYByBatch.Debug=N

//查询指定批次药品出库部门的库存 wanglong add 20150202
querySPCStockQTYByBatch.Type=TSQL
querySPCStockQTYByBatch.SQL=SELECT STOCK_QTY   AS QTY &
               FROM SPC_STOCK &
              WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ> 
                //AND ACTIVE_FLG='Y'
querySPCStockQTYByBatch.Debug=N

//查询指定批次药品出库部门的库存
queryStockQTYByBatchAll.Type=TSQL
queryStockQTYByBatchAll.SQL=SELECT STOCK_QTY   AS QTY &
			   FROM IND_STOCK &
			  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>  
queryStockQTYByBatchAll.Debug=N


//更新库存量(出库作业)
updateStockQtyDisOut.Type=TSQL
updateStockQtyDisOut.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOut.Debug=N


//更新库存量(出库作业--DEP,TEC,EXM)
updateStockQtyDisOutReq.Type=TSQL
updateStockQtyDisOutReq.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	REQUEST_OUT_QTY=REQUEST_OUT_QTY+<STOCKOUT_QTY>,&
			    	REQUEST_OUT_AMT=REQUEST_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutReq.Debug=N

//更新库存量(出库作业--DEP,TEC,EXM) wanglong add 20150202
updateStockQtyDisOutReqAcnt.Type=TSQL
updateStockQtyDisOutReqAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    REQUEST_OUT_QTY=REQUEST_OUT_QTY+<STOCKOUT_QTY>,&
                    REQUEST_OUT_AMT=REQUEST_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutReqAcnt.Debug=N

//更新库存量(出库作业--GIF)
updateStockQtyDisOutGif.Type=TSQL
updateStockQtyDisOutGif.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	GIF_OUT_QTY=GIF_OUT_QTY+<STOCKOUT_QTY>,&
			    	GIF_OUT_AMT=GIF_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutGif.Debug=N

//更新库存量(出库作业--GIF) wanglong add 20150202
updateStockQtyDisOutGifAcnt.Type=TSQL
updateStockQtyDisOutGifAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    GIF_OUT_QTY=GIF_OUT_QTY+<STOCKOUT_QTY>,&
                    GIF_OUT_AMT=GIF_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutGifAcnt.Debug=N

//更新库存量(出库作业--RET)
updateStockQtyDisOutRet.Type=TSQL
updateStockQtyDisOutRet.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	RET_OUT_QTY=RET_OUT_QTY+<STOCKOUT_QTY>,&
			    	RET_OUT_AMT=RET_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutRet.Debug=N

//更新库存量(出库作业--RET) wanglong add 20150202
updateStockQtyDisOutRetAcnt.Type=TSQL
updateStockQtyDisOutRetAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    RET_OUT_QTY=RET_OUT_QTY+<STOCKOUT_QTY>,&
                    RET_OUT_AMT=RET_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutRetAcnt.Debug=N

//更新库存量(出库作业--WAS)
updateStockQtyDisOutWas.Type=TSQL
updateStockQtyDisOutWas.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	WAS_OUT_QTY=WAS_OUT_QTY+<STOCKOUT_QTY>,&
			    	WAS_OUT_AMT=WAS_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutWas.Debug=N

//更新库存量(出库作业--WAS) wanglong add 20150202
updateStockQtyDisOutWasAcnt.Type=TSQL
updateStockQtyDisOutWasAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    WAS_OUT_QTY=WAS_OUT_QTY+<STOCKOUT_QTY>,&
                    WAS_OUT_AMT=WAS_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutWasAcnt.Debug=N

//更新库存量(出库作业--THO)
updateStockQtyDisOutTho.Type=TSQL
updateStockQtyDisOutTho.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	THO_OUT_QTY=THO_OUT_QTY+<STOCKOUT_QTY>,&
			    	THO_OUT_AMT=THO_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutTho.Debug=N

//更新库存量(出库作业--THO) wanglong add 20150202
updateStockQtyDisOutThoAcnt.Type=TSQL
updateStockQtyDisOutThoAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    THO_OUT_QTY=THO_OUT_QTY+<STOCKOUT_QTY>,&
                    THO_OUT_AMT=THO_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutThoAcnt.Debug=N

//更新库存量(出库作业--COS)
updateStockQtyDisOutCos.Type=TSQL
updateStockQtyDisOutCos.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	COS_OUT_QTY=COS_OUT_QTY+<STOCKOUT_QTY>,&
			    	COS_OUT_AMT=COS_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &
			    	OPT_DATE=<OPT_DATE>, &
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutCos.Debug=N


//更新库存量(出库作业--SDR)
updateStockQtyDisOutSrd.Type=TSQL
updateStockQtyDisOutSrd.SQL=UPDATE IND_STOCK &
			    SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			    	OUT_AMT=OUT_AMT+<OUT_AMT>, &
			    	STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			    	STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
			    	STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
			    	COS_OUT_QTY=COS_OUT_QTY+<STOCKOUT_QTY>,&
			    	COS_OUT_AMT=COS_OUT_AMT+<STOCKOUT_AMT>,&
			    	OPT_USER=<OPT_USER>, &                         
			    	OPT_DATE=<OPT_DATE>, &                
			    	OPT_TERM=<OPT_TERM> &
		       	  WHERE ORG_CODE=<ORG_CODE> &
			    AND ORDER_CODE=<ORDER_CODE> &           
			    AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutSrd.Debug=N

//更新库存量(出库作业--COS) wanglong add 20150202
updateStockQtyDisOutCosAcnt.Type=TSQL
updateStockQtyDisOutCosAcnt.SQL=UPDATE SPC_STOCK &
                SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
                    OUT_AMT=OUT_AMT+<OUT_AMT>, &
                    STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
                    STOCKOUT_QTY=STOCKOUT_QTY+<STOCKOUT_QTY>, &
                    STOCKOUT_AMT=STOCKOUT_AMT+<STOCKOUT_AMT>, &
                    COS_OUT_QTY=COS_OUT_QTY+<STOCKOUT_QTY>,&
                    COS_OUT_AMT=COS_OUT_AMT+<STOCKOUT_AMT>,&
                    OPT_USER=<OPT_USER>, &
                    OPT_DATE=<OPT_DATE>, &
                    OPT_TERM=<OPT_TERM> &
                  WHERE ORG_CODE=<ORG_CODE> &
                AND ORDER_CODE=<ORDER_CODE> &
                AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisOutCosAcnt.Debug=N

//更新库存量(入库作业)
updateStockQtyDisIn.Type=TSQL
updateStockQtyDisIn.SQL=UPDATE IND_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisIn.Debug=N


//更新库存量(入库作业--DEP,TEC)
updateStockQtyDisInReq.Type=TSQL
updateStockQtyDisInReq.SQL=UPDATE IND_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       REQUEST_IN_QTY=REQUEST_IN_QTY+<STOCKIN_QTY>, &
			       REQUEST_IN_AMT=REQUEST_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInReq.Debug=N

//更新库存量(入库作业--DEP,TEC) wanglong add 20150202
updateSPCStockQtyDisInReq.Type=TSQL
updateSPCStockQtyDisInReq.SQL=UPDATE SPC_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       REQUEST_IN_QTY=REQUEST_IN_QTY+<STOCKIN_QTY>, &
			       REQUEST_IN_AMT=REQUEST_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateSPCStockQtyDisInReq.Debug=N

//更新库存量(入库作业--GIF)
updateStockQtyDisInGif.Type=TSQL
updateStockQtyDisInGif.SQL=UPDATE IND_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       GIF_IN_QTY=GIF_IN_QTY+<STOCKIN_QTY>, &
			       GIF_IN_AMT=GIF_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInGif.Debug=N

//更新库存量(入库作业--GIF) wanglong add 20150202
updateStockQtyDisInGifAcnt.Type=TSQL
updateStockQtyDisInGifAcnt.SQL=UPDATE SPC_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       GIF_IN_QTY=GIF_IN_QTY+<STOCKIN_QTY>, &
			       GIF_IN_AMT=GIF_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInGifAcnt.Debug=N

//更新库存量(入库作业--RET)
updateStockQtyDisInRet.Type=TSQL
updateStockQtyDisInRet.SQL=UPDATE IND_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       RET_IN_QTY=RET_IN_QTY+<STOCKIN_QTY>, &
			       RET_IN_AMT=RET_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInRet.Debug=N

//更新库存量(入库作业--RET) wanglong add 20150202
updateStockQtyDisInRetAcnt.Type=TSQL
updateStockQtyDisInRetAcnt.SQL=UPDATE SPC_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       RET_IN_QTY=RET_IN_QTY+<STOCKIN_QTY>, &
			       RET_IN_AMT=RET_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInRetAcnt.Debug=N

//更新库存量(入库作业--THI)
updateStockQtyDisInThi.Type=TSQL
updateStockQtyDisInThi.SQL=UPDATE IND_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       THI_IN_QTY=THI_IN_QTY+<STOCKIN_QTY>, &
			       THI_IN_AMT=THI_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInThi.Debug=N

//更新库存量(入库作业--THI) wanglong add 20150202
updateStockQtyDisInThiAcnt.Type=TSQL
updateStockQtyDisInThiAcnt.SQL=UPDATE SPC_STOCK &
			   SET IN_QTY=IN_QTY+<IN_QTY>, &
			       IN_AMT =IN_AMT+<IN_AMT>, &
			       STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			       STOCKIN_QTY=STOCKIN_QTY+<STOCKIN_QTY>, &
			       STOCKIN_AMT=STOCKIN_AMT+<STOCKIN_AMT>, &
			       THI_IN_QTY=THI_IN_QTY+<STOCKIN_QTY>, &
			       THI_IN_AMT=THI_IN_AMT+<STOCKIN_AMT>, &
			       OPT_USER=<OPT_USER>, &
			       OPT_DATE=<OPT_DATE>, &
			       OPT_TERM=<OPT_TERM> &
		         WHERE ORG_CODE=<ORG_CODE> &
			   AND ORDER_CODE=<ORDER_CODE> &
			   AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyDisInThiAcnt.Debug=N

//盘点查询
queryQtyCheck.Type=TSQL
queryQtyCheck.SQL=SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, &
       			 A.BATCH_SEQ,&
       		 	 A.STOCK_QTY AS STOCK_QTY, &
       		 	 B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE &
       	  	   FROM IND_STOCK A, PHA_BASE B &
       	  	  WHERE A.ORDER_CODE = B.ORDER_CODE
queryQtyCheck.ITEM=ORG_CODE
queryQtyCheck.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryQtyCheck.Debug=N


//<药房>更新库存量(扣库)
updateStockQtyOut.Type=TSQL
updateStockQtyOut.SQL=UPDATE IND_STOCK &
			 SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			     OUT_AMT=OUT_AMT+<OUT_AMT>, &
			     STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			     DOSEAGE_QTY=DOSEAGE_QTY+<DOSEAGE_QTY>, &
			     DOSAGE_AMT=DOSAGE_AMT+<DOSAGE_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyOut.Debug=N


//<药房>更新库存量(取消配药)
updateStockQtyIn.Type=TSQL
updateStockQtyIn.SQL=UPDATE IND_STOCK &
			 SET IN_QTY=IN_QTY+<IN_QTY>, &
			     IN_AMT=IN_AMT+<IN_AMT>, &
			     STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			     DOSEAGE_QTY=DOSEAGE_QTY-<DOSEAGE_QTY>, &
			     DOSAGE_AMT=DOSAGE_AMT-<DOSAGE_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyIn.Debug=N


//<药房>更新库存量(退药入库)
updateStockQtyRegIn.Type=TSQL
updateStockQtyRegIn.SQL=UPDATE IND_STOCK &
			 SET IN_QTY=IN_QTY+<IN_QTY>, &
			     IN_AMT=IN_AMT+<IN_AMT>, &
			     STOCK_QTY=STOCK_QTY+<STOCK_QTY>, &
			     REGRESSDRUG_QTY=REGRESSDRUG_QTY+<REGRESSDRUG_QTY>, &
			     REGRESSDRUG_AMT=REGRESSDRUG_AMT+<REGRESSDRUG_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyRegIn.Debug=N


//<药房>更新库存量(取消退药扣库)
updateStockQtyRegOut.Type=TSQL
updateStockQtyRegOut.SQL=UPDATE IND_STOCK &
			 SET OUT_QTY=OUT_QTY+<OUT_QTY>, &
			     OUT_AMT=OUT_AMT+<OUT_AMT>, &
			     STOCK_QTY=STOCK_QTY-<STOCK_QTY>, &
			     REGRESSDRUG_QTY=REGRESSDRUG_QTY-<REGRESSDRUG_QTY>, &
			     REGRESSDRUG_AMT=REGRESSDRUG_AMT-<REGRESSDRUG_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateStockQtyRegOut.Debug=N


//盘点更新
updateQtyCheck.Type=TSQL
updateQtyCheck.SQL=UPDATE IND_STOCK &
			 SET STOCK_FLG=<STOCK_FLG>, &
			     FREEZE_TOT=<FREEZE_TOT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateQtyCheck.Debug=N

/*begin update by guoyi 20120525 for 当天内进行多次盘点，最后一次会将之前盘点的数量（CHECKMODI_QTY）回冲*/
//解除冻结更新数据
/*
updateUnLockQtyCheck.Type=TSQL
updateUnLockQtyCheck.SQL=UPDATE IND_STOCK &
			 SET STOCK_FLG=<STOCK_FLG>, &
			     STOCK_QTY=STOCK_QTY+<CHECKMODI_QTY>, &
			     CHECKMODI_QTY=<CHECKMODI_QTY>, &
			     CHECKMODI_AMT=<CHECKMODI_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateUnLockQtyCheck.Debug=N
*/
updateUnLockQtyCheck.Type=TSQL
updateUnLockQtyCheck.SQL=UPDATE IND_STOCK &
			 SET STOCK_FLG=<STOCK_FLG>, &
			     STOCK_QTY=STOCK_QTY+<CHECKMODI_QTY>, &
			     CHECKMODI_QTY=CHECKMODI_QTY+<CHECKMODI_QTY>, &
			     CHECKMODI_AMT=CHECKMODI_AMT+<CHECKMODI_AMT>, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateUnLockQtyCheck.Debug=N
/*end update by guoyi 20120525 for 当天内进行多次盘点，最后一次会将之前盘点的数量（CHECKMODI_QTY）回冲*/

//日库存交易档
getDDStock.Type=TSQL
getDDStock.SQL=SELECT A.ORG_CODE, A.ORDER_CODE, A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, &
         	      A.REGION_CODE, A.STOCK_QTY, A.STOCK_QTY * B.STOCK_PRICE AS STOCK_AMT, &
		      A.LAST_TOTSTOCK_QTY, A.LAST_TOTSTOCK_AMT, A.IN_QTY, A.IN_AMT, &
		      A.OUT_QTY, A.OUT_AMT, A.CHECKMODI_QTY, A.CHECKMODI_AMT, &
		      A.VERIFYIN_QTY, A.VERIFYIN_AMT, A.FAVOR_QTY, A.REGRESSGOODS_QTY, &
		      A.REGRESSGOODS_AMT, A.DOSEAGE_QTY, A.DOSAGE_AMT, A.REGRESSDRUG_QTY, &
		      A.REGRESSDRUG_AMT, A.PROFIT_LOSS_AMT, A.VERIFYIN_PRICE, &
		      B.STOCK_PRICE, A.RETAIL_PRICE, B.TRADE_PRICE, A.STOCKIN_QTY, &
              A.STOCKIN_AMT, A.STOCKOUT_QTY, A.STOCKOUT_AMT, A.REQUEST_IN_QTY, A.REQUEST_IN_AMT, &
              A.REQUEST_OUT_QTY, A.REQUEST_OUT_AMT, A.GIF_IN_QTY, A.GIF_IN_AMT, A.GIF_OUT_QTY, &
              A.GIF_OUT_AMT, A.RET_IN_QTY, A.RET_IN_AMT, A.RET_OUT_QTY, A.RET_OUT_AMT, &
              A.WAS_OUT_QTY, A.WAS_OUT_AMT, A.THO_OUT_QTY, A.THO_OUT_AMT, A.THI_IN_QTY, &
              A.THI_IN_AMT, A.COS_OUT_QTY, A.COS_OUT_AMT,A.SUP_CODE,A.INVENT_PRICE,A.SUP_ORDER_CODE &
    		 FROM IND_STOCK A, PHA_BASE B &
   	        WHERE A.ORDER_CODE = B.ORDER_CODE &
     		  AND A.ORG_CODE = <ORG_CODE> &
		  ORDER BY A.ORG_CODE, A.ORDER_CODE
getDDStock.Debug=N


//各项出库纪录归零
updateOutQtyToZero.Type=TSQL
updateOutQtyToZero.SQL=UPDATE IND_STOCK &
			 SET STOCK_QTY=<STOCK_QTY>, &
			     LAST_TOTSTOCK_QTY=<LAST_TOTSTOCK_QTY>, &
			     LAST_TOTSTOCK_AMT=<LAST_TOTSTOCK_AMT>, &
			     IN_QTY=0, &
			     IN_AMT=0, &
			     OUT_QTY=0, &
			     OUT_AMT=0, &
			     CHECKMODI_QTY=0, &
			     CHECKMODI_AMT=0, &
			     VERIFYIN_QTY=0, &
			     VERIFYIN_AMT=0, &
			     FAVOR_QTY=0, &
			     REGRESSGOODS_QTY=0, &
			     REGRESSGOODS_AMT=0, &
			     DOSEAGE_QTY=0, &
			     DOSAGE_AMT=0, &
			     REGRESSDRUG_QTY=0, &
			     REGRESSDRUG_AMT=0, &
			     FREEZE_TOT=0, &
			     PROFIT_LOSS_AMT=0, &
			     STOCKIN_QTY=0, &
			     STOCKIN_AMT=0, &
			     STOCKOUT_QTY=0, &
			     STOCKOUT_AMT=0, &
			     REQUEST_IN_QTY=0, &
			     REQUEST_IN_AMT=0, &
			     REQUEST_OUT_QTY=0, &
			     REQUEST_OUT_AMT=0, &
			     GIF_IN_QTY=0, &
			     GIF_IN_AMT=0, &
			     GIF_OUT_QTY=0, &
			     GIF_OUT_AMT=0, &
			     RET_IN_QTY=0, &
			     RET_IN_AMT=0, &
			     RET_OUT_QTY=0, &
			     RET_OUT_AMT=0, &
			     WAS_OUT_QTY=0, &
			     WAS_OUT_AMT=0, &
			     THO_OUT_QTY=0, &
			     THO_OUT_AMT=0, &
			     THI_IN_QTY=0, &
			     THI_IN_AMT=0, &
			     COS_OUT_QTY=0, &
			     COS_OUT_AMT=0, &
			     OPT_USER=<OPT_USER>, &
			     OPT_DATE=<OPT_DATE>, &
			     OPT_TERM=<OPT_TERM> &
		       WHERE ORG_CODE=<ORG_CODE> &
			 AND ORDER_CODE=<ORDER_CODE> &
			 AND BATCH_SEQ=<BATCH_SEQ>
updateOutQtyToZero.Debug=N


//更新IND_STOCK中的调价损益
updateProfitLossAmt.Type=TSQL
updateProfitLossAmt.SQL=UPDATE IND_STOCK &
				SET PROFIT_LOSS_AMT=(<OWN_PRICE> - RETAIL_PRICE) * STOCK_QTY, &
				    RETAIL_PRICE=<OWN_PRICE>, &
				    OPT_USER=<OPT_USER>, &
				    OPT_DATE=<OPT_DATE>, &
			            OPT_TERM=<OPT_TERM> &
				WHERE ORG_CODE=<ORG_CODE> &
			    	  AND ORDER_CODE=<ORDER_CODE> &
			  	  AND BATCH_SEQ=<BATCH_SEQ>
updateProfitLossAmt.Debug=N

   
//部门库存查询(显示批号和效期) luhai 2012-2-14 加入根据效期倒叙排序
getOrgStockQuery.Type=TSQL
getOrgStockQuery.SQL=SELECT   a.order_code, c.order_desc, c.SPECIFICATION, h.chn_desc, a.stock_qty, &
         g.unit_chn_desc, &
            FLOOR (a.stock_qty / f.dosage_qty) &
         || e.unit_chn_desc &
         || MOD (a.stock_qty, f.dosage_qty) &
         || g.unit_chn_desc AS qty, &
            a.retail_price * f.dosage_qty &
         || '/' &
         || e.unit_chn_desc &
         || ';' &
         || a.retail_price &
         || '/' &
         || g.unit_chn_desc AS price, &
         a.verifyin_price AS stock_price, &
         a.stock_qty * a.verifyin_price AS stock_amt, &
         a.stock_qty * c.own_price AS own_amt, &
         (a.stock_qty * a.retail_price - a.stock_qty * a.verifyin_price &
         ) AS diff_amt, &
         a.batch_no, a.valid_date, a.stock_flg, b.safe_qty, d.pha_type, &
         b.material_loc_code, a.retail_price AS own_price, a.active_flg ,f.dosage_qty, k.org_chn_desc &
    FROM ind_stock a, &
         ind_stockm b, &
         sys_fee c, &
         pha_base d, &
         sys_unit e, &
         pha_transunit f, &
         sys_unit g, &
         sys_dictionary h, &
		 ind_org k &
   WHERE a.org_code = b.org_code &
     AND a.order_code = b.order_code &
     AND a.order_code = c.order_code &
     AND a.order_code = d.order_code  &
     AND d.stock_unit = e.unit_code &
     AND a.order_code = f.order_code &
     AND d.dosage_unit = g.unit_code  &
     AND h.GROUP_ID = 'SYS_PHATYPE'  &
     AND d.type_code = h.ID &
	 AND a.org_code = k.org_code &
ORDER BY a.org_code,b.material_loc_code,a.order_code,a.valid_date 
getOrgStockQuery.ITEM=ORG_CODE;ORDER_CODE;BATCH_NO;MATERIAL_LOC_CODE;TYPE_CODE;SAFE_QTY;STOCK_QTY;SUP_CODE
getOrgStockQuery.ORG_CODE=A.ORG_CODE=<ORG_CODE>
getOrgStockQuery.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getOrgStockQuery.BATCH_NO=A.BATCH_NO=<BATCH_NO>
getOrgStockQuery.MATERIAL_LOC_CODE=A.MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>
getOrgStockQuery.TYPE_CODE=D.TYPE_CODE=<TYPE_CODE>
getOrgStockQuery.SAFE_QTY=B.SAFE_QTY>A.STOCK_QTY
getOrgStockQuery.STOCK_QTY=A.STOCK_QTY!=0
getOrgStockQuery.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getOrgStockQuery.Debug=Y



//部门库存查询(不显示批号和效期)
getOrgStockQueryNotBatch.Type=TSQL
getOrgStockQueryNotBatch.SQL=SELECT   A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, H.CHN_DESC, &
				 SUM (A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC,  &
				    FLOOR (SUM (A.STOCK_QTY) / F.DOSAGE_QTY)  &
				 || E.UNIT_CHN_DESC  &
				 || MOD (SUM (A.STOCK_QTY), F.DOSAGE_QTY)  &
				 || G.UNIT_CHN_DESC AS QTY,  &
				    A.RETAIL_PRICE * F.DOSAGE_QTY  &
				 || '/'  &
				 || E.UNIT_CHN_DESC  &
				 || ';'  &
				 || A.RETAIL_PRICE  &
				 || '/'  &
				 || G.UNIT_CHN_DESC AS PRICE,  &
				 A.VERIFYIN_PRICE AS STOCK_PRICE, SUM (A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT,  &
				 SUM (A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT,  &
				 (SUM (A.STOCK_QTY) * A.RETAIL_PRICE - SUM (A.STOCK_QTY) * A.VERIFYIN_PRICE & 
				 ) AS DIFF_AMT,  &
				 '' AS BATCH_NO, '' AS VALID_DATE, A.STOCK_FLG, B.SAFE_QTY,  &
				 D.PHA_TYPE, I.MATERIAL_CHN_DESC, A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG &
				FROM IND_STOCK A,  &
				 IND_STOCKM B,  &
				 SYS_FEE C,  &
				 PHA_BASE D,  &
				 SYS_UNIT E,  &
				 PHA_TRANSUNIT F,  &
				 SYS_UNIT G, &
				 SYS_DICTIONARY H, &
				 IND_MATERIALLOC I &
			       WHERE A.ORG_CODE = B.ORG_CODE  &
				 AND A.ORDER_CODE = B.ORDER_CODE  &
				 AND A.ORDER_CODE = C.ORDER_CODE  &
				 AND B.ORDER_CODE = C.ORDER_CODE  &
				 AND A.ORDER_CODE = D.ORDER_CODE  &
				 AND B.ORDER_CODE = D.ORDER_CODE  &
				 AND C.ORDER_CODE = D.ORDER_CODE  &
				 AND D.STOCK_UNIT = E.UNIT_CODE  &
				 AND A.ORDER_CODE = F.ORDER_CODE & 
				 AND B.ORDER_CODE = F.ORDER_CODE  &
				 AND C.ORDER_CODE = F.ORDER_CODE  &
				 AND D.ORDER_CODE = F.ORDER_CODE  &
				 AND D.DOSAGE_UNIT = G.UNIT_CODE  &
				 AND H.GROUP_ID = 'SYS_PHATYPE' &
				 AND D.TYPE_CODE = H.ID &
				 AND A.ORG_CODE = I.ORG_CODE(+)  &
				 AND A.MATERIAL_LOC_CODE = I.MATERIAL_LOC_CODE(+)  &
			       GROUP BY A.ORDER_CODE,  &
				 C.ORDER_DESC,  &
				 C.SPECIFICATION,  &
				 H.CHN_DESC,  &
				 E.UNIT_CHN_DESC,  &
				 F.DOSAGE_QTY,  &
				 G.UNIT_CHN_DESC,  &
				 A.RETAIL_PRICE,  &
				 A.VERIFYIN_PRICE,  &
				 A.STOCK_FLG,  &
				 B.SAFE_QTY,  &
				 D.PHA_TYPE, &
				 I.MATERIAL_CHN_DESC, &
				 A.ACTIVE_FLG &
		               ORDER BY A.ORDER_CODE
getOrgStockQueryNotBatch.ITEM=ORG_CODE;ORDER_CODE;BATCH_NO;MATERIAL_LOC_CODE;TYPE_CODE;SAFE_QTY;STOCK_QTY
getOrgStockQueryNotBatch.ORG_CODE=A.ORG_CODE=<ORG_CODE>
getOrgStockQueryNotBatch.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getOrgStockQueryNotBatch.BATCH_NO=A.BATCH_NO=<BATCH_NO>
getOrgStockQueryNotBatch.MATERIAL_LOC_CODE=A.MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>
getOrgStockQueryNotBatch.TYPE_CODE=D.TYPE_CODE=<TYPE_CODE>
getOrgStockQueryNotBatch.SAFE_QTY=B.SAFE_QTY>A.STOCK_QTY
getOrgStockQueryNotBatch.STOCK_QTY=A.STOCK_QTY!=0
getOrgStockQueryNotBatch.Debug=N

//养护药品库存查询
getDrugMianStockQuery.Type=TSQL
getDrugMianStockQuery.SQL=SELECT A.ORDER_CODE,A.ORG_CODE,A.BATCH_SEQ,B.ORDER_DESC,B.ORDER_CAT1_CODE,B.SPECIFICATION,  &
        D.UNIT_CHN_DESC UNIT,D.UNIT_CODE,C.MAN_CHN_DESC MAN,B.MAN_CODE,A.BATCH_NO,A.VALID_DATE,  &
         I.WAS_QTY ,I.WAS_REASON,FLOOR(SUM(A.STOCK_QTY)/ F.DOSAGE_QTY) AS QTY &
        FROM IND_STOCK A,SYS_FEE B,SYS_MANUFACTURER C,SYS_UNIT D,IND_ORG E,PHA_TRANSUNIT F ,PHA_BASE H ,IND_CONSERVATION_MED I &
        WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND B.MAN_CODE=C.MAN_CODE(+) AND A.ORDER_CODE=H.ORDER_CODE(+) AND H.STOCK_UNIT=D.UNIT_CODE(+) &
         AND  A.ORG_CODE=E.ORG_CODE(+) AND A.ORDER_CODE=F.ORDER_CODE(+) AND E.ORG_TYPE='A' AND A.ORG_CODE=I.ORG_CODE(+) AND A.ORDER_CODE=I.ORDER_CODE(+) &
         AND A.BATCH_SEQ=I.BATCH_SEQ(+) &
        GROUP BY A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, D.UNIT_CHN_DESC,C.MAN_CHN_DESC,  &
        A.BATCH_NO,A.VALID_DATE,F.DOSAGE_QTY,B.ORDER_CAT1_CODE,A.ORG_CODE , &
        I.WAS_QTY,I.WAS_REASON,A.BATCH_SEQ,D.UNIT_CODE,B.MAN_CODE &
        ORDER BY  A.ORDER_CODE
getDrugMianStockQuery.ITEM=ORG_CODE;ORDER_CAT1_CODE;ORDER_CODE;REGION_CODE
getDrugMianStockQuery.ORG_CODE=A.ORG_CODE=<ORG_CODE>
getDrugMianStockQuery.ORDER_CAT1_CODE=B.ORDER_CAT1_CODE=<ORDER_CAT1_CODE>
getDrugMianStockQuery.REGION_CODE=A.REGION_CODE=<REGION_CODE>
getDrugMianStockQuery.Debug=N


//部门库存查询(显示批号和效期,仅查麻精) luhai 2012-2-14 加入根据效期倒叙排序
getOrgStockDrugQuery.Type=TSQL
getOrgStockDrugQuery.SQL=SELECT A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY, &
			       G.UNIT_CHN_DESC,  &
			      FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)  &
			       || E.UNIT_CHN_DESC  &
			       || MOD (A.STOCK_QTY, F.DOSAGE_QTY)  &
			       || G.UNIT_CHN_DESC AS QTY,  &
			      A.RETAIL_PRICE * F.DOSAGE_QTY  &
			       || '/'  &
			       || E.UNIT_CHN_DESC  &
			       || ';'  &
			       || A.RETAIL_PRICE  &
			       || '/'  &
			       || G.UNIT_CHN_DESC AS PRICE,  &
			       A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY * A.VERIFYIN_PRICE AS STOCK_AMT,  &
			       A.STOCK_QTY * C.OWN_PRICE AS OWN_AMT,  &
			       (A.STOCK_QTY * A.RETAIL_PRICE - A.STOCK_QTY * A.VERIFYIN_PRICE  &
			       ) AS DIFF_AMT, A.BATCH_NO, A.VALID_DATE, A.STOCK_FLG, B.SAFE_QTY,  &
			       D.PHA_TYPE , A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,J.CTRL_FLG &
			  FROM IND_STOCK A,  &
			       IND_STOCKM B,  &
			       SYS_FEE C,  &
			       PHA_BASE D,  &
			       SYS_UNIT E,  &
			       PHA_TRANSUNIT F,  &
			       SYS_UNIT G, &
			       SYS_DICTIONARY H,  &
			       SYS_CTRLDRUGCLASS J &
			 WHERE A.ORG_CODE = B.ORG_CODE  &
			   AND A.ORDER_CODE = B.ORDER_CODE  &
			   AND A.ORDER_CODE = C.ORDER_CODE  &
			   AND B.ORDER_CODE = C.ORDER_CODE  &
			   AND A.ORDER_CODE = D.ORDER_CODE  &
			   AND B.ORDER_CODE = D.ORDER_CODE  &
			   AND C.ORDER_CODE = D.ORDER_CODE  &
			   AND D.STOCK_UNIT = E.UNIT_CODE  &
			   AND A.ORDER_CODE = F.ORDER_CODE  &
			   AND B.ORDER_CODE = F.ORDER_CODE  &
			   AND C.ORDER_CODE = F.ORDER_CODE  &
			   AND D.ORDER_CODE = F.ORDER_CODE  &
			   AND D.DOSAGE_UNIT = G.UNIT_CODE &
			   AND H.GROUP_ID = 'SYS_PHATYPE'  &
			   AND D.TYPE_CODE = H.ID  &
			   AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y' &  
			 ORDER BY A.ORDER_CODE,A.VALID_DATE 
getOrgStockDrugQuery.ITEM=ORG_CODE;ORDER_CODE;BATCH_NO;MATERIAL_LOC_CODE;TYPE_CODE;SAFE_QTY;STOCK_QTY
getOrgStockDrugQuery.ORG_CODE=A.ORG_CODE=<ORG_CODE>
getOrgStockDrugQuery.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getOrgStockDrugQuery.BATCH_NO=A.BATCH_NO=<BATCH_NO>
getOrgStockDrugQuery.MATERIAL_LOC_CODE=A.MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>
getOrgStockDrugQuery.TYPE_CODE=D.TYPE_CODE=<TYPE_CODE>
getOrgStockDrugQuery.SAFE_QTY=B.SAFE_QTY>A.STOCK_QTY
getOrgStockDrugQuery.STOCK_QTY=A.STOCK_QTY>0
getOrgStockDrugQuery.Debug=N


//根据药库编号及药品代码查询药库锁库存量
//====pangben 2013-11-4 爱育华版本
onQueryLockQTY.Type=TSQL
onQueryLockQTY.SQL=SELECT SUM(A.STOCK_QTY)-(B.LOCK_QTY+<DOSAGE_QTY>) AS QTY &
			FROM IND_STOCK A ,IND_STOCKM B &
			WHERE A.ORG_CODE=B.ORG_CODE AND A.STOCK_QTY > 0  &
			AND A.ORDER_CODE=B.ORDER_CODE AND   SYSDATE < A.VALID_DATE  &
			GROUP BY A.ORDER_CODE,B.LOCK_QTY 
onQueryLockQTY.ITEM=ORG_CODE;ORDER_CODE;ACTIVE_FLG
onQueryLockQTY.ORG_CODE=A.ORG_CODE=<ORG_CODE>
onQueryLockQTY.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
onQueryLockQTY.ACTIVE_FLG=A.ACTIVE_FLG=<ACTIVE_FLG>
onQueryLockQTY.Debug=N

//修改锁库存字段库存量操作
//pangben 2013-11-6
updateIndStockLockQty.Type=TSQL
updateIndStockLockQty.SQL=UPDATE IND_STOCKM SET LOCK_QTY=LOCK_QTY+<LOCK_QTY>  WHERE ORG_CODE=<ORG_CODE> AND ORDER_CODE=<ORDER_CODE>
updateIndStockLockQty.Debug=N




