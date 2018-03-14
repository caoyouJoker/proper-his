   #
   # Title:物资系统退货统计表
   #
   # Description:物资系统退货统计表
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangh 2013/05/08

Module.item=queryRegressGoodsTotal;queryRegressGoodsDetail

//查询报表
queryRegressGoodsTotal.Type=TSQL
queryRegressGoodsTotal.SQL=SELECT A.INV_CODE, &
       				  B.INV_CHN_DESC, &
       				  SUM(QTY) AS QTY, &
       				  SUP_CHN_DESC &
 			   FROM INV_RETURNHIGH A &
 			   LEFT JOIN INV_BASE B ON A.INV_CODE = B.INV_CODE &
 			   LEFT JOIN SYS_SUPPLIER C ON A.SUP_CODE = C.SUP_CODE &
 			   GROUP BY A.INV_CODE,B.INV_CHN_DESC,C.SUP_CHN_DESC
queryRegressGoodsTotal.ITEM=SUP_CODE;START_TIME;END_TIME
queryRegressGoodsTotal.SUP_CODE=A.SUP_CODE=<SUP_CODE>
queryRegressGoodsTotal.START_TIME=TRUNC(A.RETURN_DATE)>=TO_DATE(<START_TIME>,'YYYYMMDDHH24MISS')
queryRegressGoodsTotal.END_TIME=TRUNC(A.RETURN_DATE)<=TO_DATE(<END_TIME>,'YYYYMMDDHH24MISS')
queryRegressGoodsTotal.Debug=N

//查询报表明细
queryRegressGoodsDetail.Type=TSQL
queryRegressGoodsDetail.SQL=SELECT A.RETURN_NO, &
       				  A.INV_CODE, &
       				  B.INV_CHN_DESC, &
       				  B.DESCRIPTION, &
       				  A.RFID, &
       				  A.QTY, &
       				  C.SUP_CHN_DESC &
 			   FROM INV_RETURNHIGH A &
 			   LEFT JOIN INV_BASE B ON A.INV_CODE = B.INV_CODE &
 			   LEFT JOIN SYS_SUPPLIER C ON A.SUP_CODE = C.SUP_CODE
queryRegressGoodsDetail.ITEM=INV_CODE
queryRegressGoodsDetail.INV_CODE=A.INV_CODE=<INV_CODE>
queryRegressGoodsDetail.Debug=N