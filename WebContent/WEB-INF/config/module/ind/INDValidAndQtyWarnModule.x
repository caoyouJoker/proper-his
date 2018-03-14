 #
   # Title: 近效期及库存量提示
   #
   # Description:近效期及库存量提示
   #
   # Copyright: JavaHis (c) 2010
   #
   # @author zhangy 2010.10.29

Module.item=queryValid;queryQty

//查询近效期
queryValid.Type=TSQL
queryValid.SQL=SELECT A.ORDER_CODE, A.ORDER_DESC, A.SPECIFICATION, C.DOSE_CHN_DESC, &
       		      B.STOCK_QTY, D.UNIT_CHN_DESC, B.BATCH_NO, B.VALID_DATE, E.SUP_CHN_DESC &
  		 FROM IND_STOCK B, PHA_BASE A, PHA_DOSE C, SYS_UNIT D, SYS_SUPPLIER E &
 	         WHERE B.ORDER_CODE = A.ORDER_CODE &
   		   AND A.DOSE_CODE = C.DOSE_CODE(+) &
   		   AND A.DOSAGE_UNIT = D.UNIT_CODE(+) &
   		   AND A.SUP_CODE = E.SUP_CODE(+) &
   		   AND B.ACTIVE_FLG = 'Y' &
   		   AND B.STOCK_QTY > 0 &
   		 ORDER BY A.ORDER_CODE
queryValid.item=ORG_CODE;VALID_DATE;ORDER_CODE
queryValid.ORG_CODE=B.ORG_CODE=<ORG_CODE>
queryValid.VALID_DATE=B.VALID_DATE <= TO_DATE(<VALID_DATE>,'YYYYMMDD')
queryValid.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
queryValid.Debug=N


//查询库存量
queryQty.Type=TSQL
queryQty.SQL=SELECT A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, &
         	    SUM (A.STOCK_QTY) AS STOCK_QTY, D.UNIT_CHN_DESC, B.MAX_QTY, &
         	    B.MIN_QTY, B.SAFE_QTY &
    	       FROM IND_STOCK A, IND_STOCKM B, SYS_FEE C, SYS_UNIT D &
   	      WHERE A.ORG_CODE = B.ORG_CODE &
     	        AND A.ORDER_CODE = B.ORDER_CODE &
     		AND A.ORDER_CODE = C.ORDER_CODE &
     		AND B.ORDER_CODE = C.ORDER_CODE &
     		AND C.UNIT_CODE = D.UNIT_CODE &
     		AND A.ACTIVE_FLG = 'Y' &
     		AND B.ACTIVE_FLG = 'N' &
     		AND A.VALID_DATE >= SYSDATE &
	      GROUP BY A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, D.UNIT_CHN_DESC, B.MAX_QTY, &
         	       B.MIN_QTY, B.SAFE_QTY &
	      ORDER BY A.ORDER_CODE
queryQty.item=ORG_CODE;ORDER_CODE;STOCK_QTY_A;STOCK_QTY_B;STOCK_QTY_C
queryQty.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryQty.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
queryQty.STOCK_QTY_A=A.STOCK_QTY > 0 HAVING SUM (A.STOCK_QTY) > B.MAX_QTY
queryQty.STOCK_QTY_B=A.STOCK_QTY > 0 HAVING SUM (A.STOCK_QTY) < B.MIN_QTY
queryQty.STOCK_QTY_C=A.STOCK_QTY > 0 HAVING SUM (A.STOCK_QTY) < B.SAFE_QTY
queryQty.Debug=N

