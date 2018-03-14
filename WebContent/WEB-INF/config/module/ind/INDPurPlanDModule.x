   #
   # Title:采购计划明细
   #
   # Description:采购计划明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/28

Module.item=queryPlanD;createNewPlanD;updatePlanD;deletePlanD;queryCreatePurPlanD

//查询采购计划明细
queryPlanD.Type=TSQL
queryPlanD.SQL=SELECT ORG_CODE , PLAN_NO , SEQ , ORDER_CODE , PLAN_QTY , &
		      PUR_QTY , ACTUAL_QTY , CHECK_QTY , PURCH_UNIT , LASTPUR_QTY , &
		      LASTCON_QTY , STOCK_QTY , STOCK_PRICE , SAFE_QTY , MAX_QTY , &
		      BUY_UNRECEIVE_QTY , SUP_CODE , START_DATE , END_DATE , OPT_USER , &
		      OPT_DATE , OPT_TERM &
		 FROM IND_PURPLAND &
		 ORDER BY SEQ
queryPlanD.ITEM=SUP_CODE;ORDER_CODE;ORG_CODE;PLAN_NO
queryPlanD.SUP_CODE=SUP_CODE=<SUP_CODE>
queryPlanD.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
queryPlanD.ORG_CODE=ORG_CODE=<ORG_CODE>
queryPlanD.PLAN_NO=PLAN_NO=<PLAN_NO>
queryPlanD.Debug=N


//新建采购计划明细
createNewPlanD.Type=TSQL
createNewPlanD.SQL=INSERT INTO IND_PURPLAND( &
			ORG_CODE , PLAN_NO , SEQ , ORDER_CODE , PLAN_QTY , &
			PUR_QTY , ACTUAL_QTY , CHECK_QTY , PURCH_UNIT , LASTPUR_QTY , &
			LASTCON_QTY , STOCK_QTY , STOCK_PRICE , SAFE_QTY , MAX_QTY , &
			BUY_UNRECEIVE_QTY , SUP_CODE , START_DATE , END_DATE , OPT_USER , &
		        OPT_DATE , OPT_TERM) &
	    	   VALUES( &
	    	   	<ORG_CODE> , <PLAN_NO> , <SEQ> , <ORDER_CODE> , <PLAN_QTY> , &
			<PUR_QTY> , <ACTUAL_QTY> , <CHECK_QTY> , <PURCH_UNIT> , <LASTPUR_QTY> , &
			<LASTCON_QTY> , <STOCK_QTY> , <STOCK_PRICE> , <SAFE_QTY> , <MAX_QTY> , &
			<BUY_UNRECEIVE_QTY> , <SUP_CODE> , <START_DATE> , <END_DATE> , <OPT_USER> , &
		        <OPT_DATE> , <OPT_TERM>)
createNewPlanD.Debug=N


//更新采购计划明细
updatePlanD.Type=TSQL
updatePlanD.SQL=UPDATE IND_PURPLAND &
   		    SET ORDER_CODE = <ORDER_CODE>, &
   		    	PLAN_QTY = <PLAN_QTY>, &
       			PUR_QTY = <PUR_QTY>, &
       			ACTUAL_QTY = <ACTUAL_QTY>, &
       			CHECK_QTY = <CHECK_QTY>, &
       			PURCH_UNIT = <PURCH_UNIT>, &
       			LASTPUR_QTY = <LASTPUR_QTY>, &
       			LASTCON_QTY = <LASTCON_QTY>, &
       			STOCK_QTY = <STOCK_QTY>, &
       			STOCK_PRICE = <STOCK_PRICE>, &
       			SAFE_QTY = <SAFE_QTY>, &
       			MAX_QTY = <MAX_QTY>, &
       			BUY_UNRECEIVE_QTY = <BUY_UNRECEIVE_QTY>, &
       			SUP_CODE = <SUP_CODE>, &
       			START_DATE = <START_DATE>, &
       			END_DATE = <END_DATE>, &
       			OPT_USER = <OPT_USER>, &
       			OPT_DATE = <OPT_DATE>, &
       			OPT_TERM = <OPT_TERM> &
 		  WHERE ORG_CODE = <ORG_CODE> &
 		    AND PLAN_NO = <PLAN_NO> &
 		    AND SEQ = <SEQ>
updatePlanD.Debug=N


//删除采购计划明细
deletePlanD.Type=TSQL
deletePlanD.SQL=DELETE FROM IND_PURPLAND WHERE ORG_CODE=<ORG_CODE> AND PLAN_NO=<PLAN_NO> AND SEQ=<SEQ>
deletePlanD.Debug=N


//查询采购计划生成订购单明细
queryCreatePurPlanD.Type=TSQL
queryCreatePurPlanD.SQL=SELECT 'N' AS SELECT_FLG, A.SUP_CODE, A.ORDER_CODE, B.ORDER_DESC, &
		      		A.PURCH_UNIT, C.CONTRACT_PRICE, A.CHECK_QTY AS PLAN_QTY &
		      	  FROM IND_PURPLAND A, PHA_BASE B, IND_AGENT C &
		      	  WHERE A.ORDER_CODE = B.ORDER_CODE &
		      	    AND A.ORDER_CODE = C.ORDER_CODE &
		      	    AND A.SUP_CODE = C.SUP_CODE &
		      	    AND B.ORDER_CODE = C.ORDER_CODE &
		      	    AND A.PLAN_NO = <PLAN_NO>  &
		      	    AND A.ORG_CODE = <ORG_CODE> &
		      	    ORDER BY SUP_CODE
queryCreatePurPlanD.Debug=N



