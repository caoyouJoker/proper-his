   #
   # Title:物资系统申请汇总表
   #
   # Description:物资系统申请汇总表
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangh 2013/05/08

Module.item=queryGoodsApplyReport

//查询报表
queryGoodsApplyReport.Type=TSQL
queryGoodsApplyReport.SQL=SELECT D.DEPT_CHN_DESC, &
				 E.REN_DESC, &
				 A.REQUEST_NO, &
				 A.INV_CODE, &
				 A.INVSEQ_NO, &
				 C.INV_CHN_DESC, &
				 C.DESCRIPTION, &
				 A.QTY, &
				 A.BATCH_NO &
			   FROM INV_REQUESTD A,INV_REQUESTM B,INV_BASE C,SYS_DEPT D,INV_REASON E &
			   WHERE A.REQUEST_NO = B.REQUEST_NO &
				 AND A.INV_CODE = C.INV_CODE &
				 AND B.REN_CODE = E.REN_CODE(+) &
				 AND B.TO_ORG_CODE = D.DEPT_CODE &
			   ORDER BY A.REQUEST_NO
queryGoodsApplyReport.ITEM=REN_CODE;START_TIME;END_TIME;INV_CODE;TO_ORG_CODE
queryGoodsApplyReport.REN_CODE=B.REN_CODE=<REN_CODE>
queryGoodsApplyReport.START_TIME=A.OPT_DATE>=TO_DATE(<START_TIME>,'YYYYMMDDHH24MISS')
queryGoodsApplyReport.END_TIME=A.OPT_DATE<=TO_DATE(<END_TIME>,'YYYYMMDDHH24MISS')
queryGoodsApplyReport.INV_CODE=C.INV_CODE=<INV_CODE>
queryGoodsApplyReport.TO_ORG_CODE=B.TO_ORG_CODE=<TO_ORG_CODE>
queryGoodsApplyReport.Debug=N