   #
   # Title:物资系统验收入库统计表
   #
   # Description:物资系统验收入库统计表
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangh 2013/05/08

Module.item=queryVerifyInReport

//查询报表
queryVerifyInReport.Type=TSQL
queryVerifyInReport.SQL=SELECT G.ORG_DESC, &
         		       F.SUP_ABS_DESC, &
         		       A.VERIFYIN_DATE, &
         		       B.INV_CODE, &
         		       B.SEQ_NO, &
         		       C.INV_CHN_DESC, &
         		       C.DESCRIPTION, &
         		       B.QTY, &
         		       B.GIFT_QTY, &
         		       E.UNIT_CHN_DESC, &
         		       B.UNIT_PRICE, &
         		       (B.QTY * B.UNIT_PRICE) AS IN_PRICE, &
         		       B.VALID_DATE &
         		 FROM INV_VERIFYINM A,INV_VERIFYIND B,INV_BASE C,SYS_UNIT E,SYS_SUPPLIER F,INV_ORG G &
   			 WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
         		       AND B.BILL_UNIT = E.UNIT_CODE &
         		       AND A.SUP_CODE = F.SUP_CODE &
         		       AND B.INV_CODE = C.INV_CODE &
         		       AND A.VERIFYIN_DEPT =G.ORG_CODE &
         		 ORDER BY A.VERIFYIN_DATE
queryVerifyInReport.ITEM=SUP_CODE;START_TIME;END_TIME;INV_CODE;VERIFYIN_DEPT
queryVerifyInReport.SUP_CODE=A.SUP_CODE=<SUP_CODE>
queryVerifyInReport.START_TIME=A.VERIFYIN_DATE>=TO_DATE(<START_TIME>,'YYYYMMDDHH24MISS')
queryVerifyInReport.END_TIME=A.VERIFYIN_DATE<=TO_DATE(<END_TIME>,'YYYYMMDDHH24MISS')
queryVerifyInReport.INV_CODE=B.INV_CODE=<INV_CODE>
queryVerifyInReport.VERIFYIN_DEPT=A.VERIFYIN_DEPT=<VERIFYIN_DEPT>
queryVerifyInReport.Debug=N