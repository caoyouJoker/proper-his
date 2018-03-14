   #
   # Title:采购计划主档
   #
   # Description:采购计划主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/28

Module.item=queryPlanM;createNewPlanM;updatePlanM;deletePlanM;queryExcerptByOrder;queryBuyQty;querySellQty;queryMiddQty;updateCreateFlg;queryMainQty

//查询采购计划主档
queryPlanM.Type=TSQL
queryPlanM.SQL=SELECT ORG_CODE , PLAN_NO , PLAN_DESC , PLAN_MONTH , PLAN_DATE , &
		      PLAN_USER , PUR_USER , PUR_DATE , CHECK_USER, CHECK_DATE , &
		      PLANEND_USER , PLANEND_DATE , CREATE_FLG , DESCRIPTION , OPT_USER , OPT_DATE , &
		      OPT_TERM  &
		 FROM IND_PURPLANM  &
		 ORDER BY ORG_CODE
queryPlanM.ITEM=ORG_CODE;PLAN_NO;PLAN_DESC;PLAN_MONTH;DESCRIPTION;PLAN_DATE;PUR_DATE;CHECK_DATE;PLANEND_DATE;CREATE_FLG;PLANEND_USER;REGION_CODE
queryPlanM.ORG_CODE=ORG_CODE=<ORG_CODE>
queryPlanM.PLAN_NO=PLAN_NO LIKE <PLAN_NO>
queryPlanM.PLAN_DESC=PLAN_DESC LIKE <PLAN_DESC>
queryPlanM.PLAN_MONTH=PLAN_MONTH=<PLAN_MONTH>
queryPlanM.DESCRIPTION=DESCRIPTION LIKE <DESCRIPTION>
queryPlanM.PLAN_DATE=PLAN_DATE=<PLAN_DATE>
queryPlanM.PUR_DATE=PUR_DATE=<PUR_DATE>
queryPlanM.CHECK_DATE=CHECK_DATE=<CHECK_DATE>
queryPlanM.PLANEND_DATE=PLANEND_DATE=<PLANEND_DATE>
queryPlanM.PLANEND_USER=PLANEND_USER IS NOT NULL
queryPlanM.CREATE_FLG=CREATE_FLG=<CREATE_FLG>
queryPlanM.REGION_CODE=REGION_CODE=<REGION_CODE>
queryPlanM.Debug=N


//新建采购计划主档
createNewPlanM.Type=TSQL
createNewPlanM.SQL=INSERT INTO IND_PURPLANM( &
			ORG_CODE , PLAN_NO , PLAN_DESC , PLAN_MONTH , PLAN_DATE ,&
			PLAN_USER , PUR_USER , PUR_DATE , CHECK_USER , CHECK_DATE ,&
			PLANEND_USER , PLANEND_DATE , DESCRIPTION , OPT_USER , OPT_DATE ,&
		  	OPT_TERM , CREATE_FLG, REGION_CODE ) &
	    	   VALUES( &
	    	   	<ORG_CODE> ,  <PLAN_NO> , <PLAN_DESC> , <PLAN_MONTH> , <PLAN_DATE> , &
	    	   	<PLAN_USER> ,  <PUR_USER> , <PUR_DATE> , <CHECK_USER> , <CHECK_DATE> , &
	    	   	<PLANEND_USER> ,  <PLANEND_DATE> , <DESCRIPTION> , <OPT_USER> , <OPT_DATE> , &
	          	<OPT_TERM> , <CREATE_FLG> , <REGION_CODE>)
createNewPlanM.Debug=N


//更新采购计划主档
updatePlanM.Type=TSQL
updatePlanM.SQL=UPDATE IND_PURPLANM &
   		    SET PLAN_DESC = <PLAN_DESC>, &
       			PLAN_MONTH = <PLAN_MONTH>, &
       			PLAN_DATE = <PLAN_DATE>, &
       			PLAN_USER = <PLAN_USER>, &
       			PUR_USER = <PUR_USER>, &
       			PUR_DATE = <PUR_DATE>, &
       			CHECK_USER = <CHECK_USER>, &
       			CHECK_DATE = <CHECK_DATE>, &
       			PLANEND_USER = <PLANEND_USER>, &
       			PLANEND_DATE = <PLANEND_DATE>, &
       			DESCRIPTION = <DESCRIPTION>, &
       			OPT_USER = <OPT_USER>, &
       			OPT_DATE = <OPT_DATE>, &
       			OPT_TERM = <OPT_TERM> &
 		  WHERE ORG_CODE = <ORG_CODE> &
 		    AND PLAN_NO = <PLAN_NO>
updatePlanM.Debug=N


//删除采购计划主档
deletePlanM.Type=TSQL
deletePlanM.SQL=DELETE FROM IND_PURPLANM WHERE ORG_CODE=<ORG_CODE> AND PLAN_NO=<PLAN_NO>
deletePlanM.Debug=N


//采购计划引用查询(按照药品查询)
queryExcerptByOrder.Type=TSQL
queryExcerptByOrder.SQL=SELECT   'N' AS SELECT_FLG, A.ORDER_CODE, C.PURCH_UNIT, &
				 0 AS BUY_QTY, &
				 0 AS SELL_QTY, &
				 0 AS MAIN_QTY, &
				 0 AS MIDD_QTY, &
				 SUM (B.STOCK_QTY) / D.DOSAGE_QTY / D.STOCK_QTY AS STOCK_QTY, &
				 A.ECONOMICBUY_QTY / D.DOSAGE_QTY / D.STOCK_QTY AS E_QTY, &
				 E.CONTRACT_PRICE, &
				   A.ECONOMICBUY_QTY &
				 / D.DOSAGE_QTY &
				 / D.STOCK_QTY &
				 * E.CONTRACT_PRICE AS TOT_MONEY, &
				 E.SUP_CODE, A.SAFE_QTY / D.DOSAGE_QTY / D.STOCK_QTY AS SAFE_QTY, &
				 A.MAX_QTY / D.DOSAGE_QTY / D.STOCK_QTY AS MAX_QTY, &
				 A.BUY_UNRECEIVE_QTY / D.DOSAGE_QTY &
				 / D.STOCK_QTY AS BUY_UNRECEIVE_QTY, &
				 CASE WHEN F.GOODS_DESC IS NULL THEN C.ORDER_DESC ELSE C.ORDER_DESC || '(' || F.GOODS_DESC || ')' END AS ORDER_DESC, &
				 F.SPECIFICATION, F.MAN_CODE &
			    FROM IND_STOCKM A, IND_STOCK B, PHA_BASE C, PHA_TRANSUNIT D, IND_AGENT E, SYS_FEE F &
			   WHERE A.ORG_CODE = B.ORG_CODE &
			     AND A.ORDER_CODE = B.ORDER_CODE &
			     AND A.ACTIVE_FLG = 'N' &
			     AND B.ACTIVE_FLG = 'Y' &
			     AND A.ORDER_CODE = C.ORDER_CODE &
			     AND A.ORDER_CODE = D.ORDER_CODE &
			     AND C.SUP_CODE = E.SUP_CODE &
			     AND A.ORDER_CODE = E.ORDER_CODE &
			     AND A.ORDER_CODE = F.ORDER_CODE &
			     GROUP BY A.ORDER_CODE, &
				 C.PURCH_UNIT, &
				 D.DOSAGE_QTY, &
				 D.STOCK_QTY, &
				 A.ECONOMICBUY_QTY, &
				 E.CONTRACT_PRICE, &
				 E.SUP_CODE, &
				 A.SAFE_QTY, &
				 A.MAX_QTY, &
				 A.BUY_UNRECEIVE_QTY, &
				 C.ORDER_DESC, &
				 F.SPECIFICATION, &
				 F.MAN_CODE, &
				 F.GOODS_DESC
queryExcerptByOrder.ITEM=ORG_CODE;ORDER_CODE;TYPE_CODE;MATERIAL_LOC_CODE;SUP_CODE
queryExcerptByOrder.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryExcerptByOrder.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
queryExcerptByOrder.TYPE_CODE=C.TYPE_CODE=<TYPE_CODE>
queryExcerptByOrder.MATERIAL_LOC_CODE=A.MATERIAL_LOC_CODE=<MATERIAL_LOC_CODE>
queryExcerptByOrder.SUP_CODE=C.SUP_CODE=<SUP_CODE>
queryExcerptByOrder.Debug=N


//查询上期采购量
queryBuyQty.Type=TSQL
queryBuyQty.SQL=SELECT SUM (B.VERIFYIN_QTY- B.ACTUAL_QTY) AS BUY_QTY &
  		  FROM IND_VERIFYINM A, IND_VERIFYIND B &
 		  WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
 	  	    AND A.CHECK_DATE IS NOT NULL &
          	    AND A.VERIFYIN_DATE BETWEEN <START_DATE> AND <END_DATE> &
 	  	    AND A.ORG_CODE = <ORG_CODE> &
 	 	    AND B.ORDER_CODE = <ORDER_CODE>
queryBuyQty.Debug=N


//查询上期销售量
querySellQty.Type=TSQL
querySellQty.SQL=SELECT SUM (A.DOSAGE_QTY - A.REGRESSDRUG_QTY) / B.DOSAGE_QTY / B.STOCK_QTY AS SELL_QTY &
   		   FROM IND_DDSTOCK A, PHA_TRANSUNIT B &
   		  WHERE A.ORDER_CODE = B.ORDER_CODE &
     		    AND TO_DATE(A.TRANDATE, 'YYYYMMDD') BETWEEN <START_DATE> AND <END_DATE> &
    		    AND A.ORDER_CODE = <ORDER_CODE> &
		  GROUP BY B.DOSAGE_QTY, B.STOCK_QTY
querySellQty.Debug=N


//查询主库库存
queryMainQty.Type=TSQL
queryMainQty.SQL=SELECT SUM (A.STOCK_QTY) / C.DOSAGE_QTY / C.STOCK_QTY AS MAIN_QTY &
		   FROM IND_STOCK A, IND_ORG B,PHA_TRANSUNIT C &
		   WHERE B.ORG_TYPE = 'A' &
		     AND B.ORG_CODE = A.ORG_CODE &
     		     AND A.ORDER_CODE = C.ORDER_CODE &
     		     AND A.ORDER_CODE = <ORDER_CODE> &
     		   GROUP BY A.ORDER_CODE ,C.DOSAGE_QTY, C.STOCK_QTY
queryMainQty.Debug=N


//查询中库库存
queryMiddQty.Type=TSQL
queryMiddQty.SQL=SELECT SUM (A.STOCK_QTY) / C.DOSAGE_QTY / C.STOCK_QTY AS MIDD_QTY &
		   FROM IND_STOCK A, IND_ORG B,PHA_TRANSUNIT C &
		   WHERE B.ORG_TYPE = 'B' &
		     AND B.ORG_CODE = A.ORG_CODE &
     		     AND A.ORDER_CODE = C.ORDER_CODE &
     		     AND A.ORDER_CODE = <ORDER_CODE> &
     		   GROUP BY A.ORDER_CODE ,C.DOSAGE_QTY, C.STOCK_QTY
queryMiddQty.Debug=N


//更新采购计划单生成订购单状态
updateCreateFlg.Type=TSQL
updateCreateFlg.SQL=UPDATE IND_PURPLANM &
   		    SET CREATE_FLG = <CREATE_FLG>, &
       			OPT_USER = <OPT_USER>, &
       			OPT_DATE = <OPT_DATE>, &
       			OPT_TERM = <OPT_TERM> &
 		  WHERE ORG_CODE = <ORG_CODE> &
 		    AND PLAN_NO = <PLAN_NO>
updateCreateFlg.Debug=N

