 #
   # Title: 近效期及库存量提示
   #
   # Description:近效期及库存量提示
   #
   # Copyright: JavaHis (c) 2010
   #
   # @author zhangh 2013.7.12

Module.item=queryValid;queryQty

//查询近效期 
queryValid.Type=TSQL
queryValid.SQL=SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, &
		      A.RFID, A.VALID_DATE, D.SUP_CHN_DESC &
	       FROM INV_STOCKDD A, INV_BASE B, SYS_SUPPLIER D &
	       WHERE A.INV_CODE = B.INV_CODE(+) &
	       AND A.INV_CODE LIKE '08%' &
	       AND B.UP_SUP_CODE = D.SUP_CODE(+) &
	       AND A.STOCK_QTY > 0 &
	       ORDER BY A.VALID_DATE
queryValid.item=INV_CODE;VALID_DATE
queryValid.INV_CODE=A.INV_CODE=<INV_CODE>
queryValid.VALID_DATE=A.VALID_DATE <= TO_DATE(<VALID_DATE>,'YYYYMMDD')
queryValid.Debug=N


//查询库存量
queryQty.Type=TSQL
queryQty.SQL=SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, &
         	    A.STOCK_QTY, C.UNIT_CHN_DESC, A.MAX_QTY, &
         	    A.MIN_QTY, A.SAFE_QTY &
    	       FROM INV_STOCKM A, INV_BASE B, SYS_UNIT C &
   	      WHERE A.INV_CODE = B.INV_CODE &
     		AND A.STOCK_UNIT = C.UNIT_CODE &
		AND A.STOCK_QTY > 0 &
		AND A.INV_CODE LIKE '08%' &
	      ORDER BY A.INV_CODE
queryQty.item=INV_CODE;STOCK_QTY_A;STOCK_QTY_B;STOCK_QTY_C
queryQty.INV_CODE=A.INV_CODE=<INV_CODE>
queryQty.STOCK_QTY_A=A.STOCK_QTY > A.MAX_QTY
queryQty.STOCK_QTY_B=A.STOCK_QTY < A.MIN_QTY
queryQty.STOCK_QTY_C=A.STOCK_QTY < A.SAFE_QTY
queryQty.Debug=N