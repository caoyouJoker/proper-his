# 
#  Title:住院费用结算module
# 
#  Description:住院费用结算module
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2012-2-1
#  version 1.0
#

Module.item=insertINSIbsOrder;deleteINSIbsOrder;queryInsIbsDUnion;queryCheckSumIbsOrder;queryOldSplit


//添加数据 
insertINSIbsOrder.Type=TSQL
insertINSIbsOrder.SQL=INSERT INTO INS_IBS_ORDER (&
		      YEAR_MON, CASE_NO, SEQ_NO, &
		      REGION_CODE, ADM_SEQ, &
		      INSBRANCH_CODE, BILL_DATE, HOSP_NHI_NO, &
		      ORDER_CODE, NHI_ORDER_CODE, ORDER_DESC, &
		      OWN_RATE, DOSE_CODE, DOSE_DESC, &
		      STANDARD, PRICE, QTY, &
		      TOTAL_AMT, TOTAL_NHI_AMT, OWN_AMT, &
		      ADDPAY_AMT, OP_FLG, ADDPAY_FLG, &
		      NHI_ORD_CLASS_CODE, PHAADD_FLG, CARRY_FLG, &
		      OPT_USER, OPT_DATE, OPT_TERM, &
		      HYGIENE_TRADE_CODE,DOSAGE_UNIT,EXE_DEPT_CODE) VALUES(&
		      <YEAR_MON>, <CASE_NO>, <SEQ_NO>, &
		      <REGION_CODE>, <ADM_SEQ>, &
		      <INSBRANCH_CODE>, TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS'), <HOSP_NHI_NO>, &
		      <ORDER_CODE>, <NHI_ORDER_CODE>, <ORDER_DESC>, &
		      <OWN_RATE>, <DOSE_CODE>, <DOSE_DESC>, &
		      <STANDARD>, <PRICE>, <QTY>, &
		      <TOTAL_AMT>, <TOTAL_NHI_AMT>, <OWN_AMT>, &
		      <ADDPAY_AMT>, <OP_FLG>, <ADDPAY_FLG>, &
		      <NHI_ORD_CLASS_CODE>, <PHAADD_FLG>, <CARRY_FLG>, &
		      <OPT_USER>, SYSDATE, <OPT_TERM>, &
		      <HYGIENE_TRADE_CODE>,<DOSAGE_UNIT>,<EXE_DEPT_CODE>)
insertINSIbsOrder.Debug=N

//修改数据

//删除操作
deleteINSIbsOrder.Type=TSQL
deleteINSIbsOrder.SQL=DELETE FROM INS_IBS_ORDER WHERE YEAR_MON=<YEAR_MON> AND CASE_NO=<CASE_NO>
deleteINSIbsOrder.Debug=N

//查询上传数据 转申报操作
queryInsIbsDUnion.Type=TSQL
queryInsIbsDUnion.SQL= SELECT B.* FROM ( SELECT A.REGION_CODE,A.YEAR_MON,A.ADM_SEQ,A.HOSP_NHI_NO,A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,&
                       A.PRICE,SUM(A.QTY) AS QTY,SUM(A.TOTAL_AMT) AS TOTAL_AMT,SUM(A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,SUM(A.OWN_AMT) AS OWN_AMT,SUM(A.ADDPAY_AMT) AS ADDPAY_AMT,&
                       A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG,A.CARRY_FLG,A.NHI_ORDER_CODE, '0',&
                       C.NHI_CODE_I,C.OWN_PRICE ,A.OWN_RATE,A.DOSE_CODE , MAX(A.BILL_DATE) AS CHARGE_DATE, A.HYGIENE_TRADE_CODE,&
                       A.OPT_USER,MAX(A.OPT_DATE) AS OPT_DATE,A.OPT_TERM &
                       FROM INS_IBS_ORDER A ,SYS_FEE C &
           	       WHERE CASE_NO=<CASE_NO> &
           	       AND YEAR_MON = <YEAR_MON> &
		       AND A.TOTAL_AMT <> 0 &
		       AND A.ORDER_CODE=C.ORDER_CODE &
//		       AND C.ACTIVE_FLG='Y' &
		       GROUP BY A.REGION_CODE,A.YEAR_MON,A.ADM_SEQ,A.HOSP_NHI_NO,A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,&
		       A.PRICE,A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG,A.CARRY_FLG, &
		       A.NHI_ORDER_CODE,A.OPT_TERM,A.OPT_USER, &
		       C.NHI_CODE_I,C.OWN_PRICE ,A.OWN_RATE,A.DOSE_CODE,A.HYGIENE_TRADE_CODE ) B &
                       WHERE B.TOTAL_AMT!=0
queryInsIbsDUnion.Debug=N

//校验费用分割后金额是否相同
queryCheckSumIbsOrder.Type=TSQL
queryCheckSumIbsOrder.SQL=SELECT SUM(B.TOT_AMT) AS TOTAL_AMT FROM IBS_ORDM A,IBS_ORDD B,IBS_BILLM D &
			    WHERE A.CASE_NO=B.CASE_NO  AND A.BILL_NO=D.BILL_NO &
                                 AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND D.REFUND_FLG ='N' & 
                                 AND B.TOT_AMT<>0 AND D.REFUND_BILL_NO IS NULL &
                                 AND B.CASE_NO=<CASE_NO>
queryCheckSumIbsOrder.Debug=N

//费用分割 查询分割前数据

queryOldSplit.Type=TSQL
queryOldSplit.SQL=SELECT A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,A.PHAADD_FLG,&
                   A.CARRY_FLG,A.PRICE,SUM(A.QTY) AS QTY,SUM(A.TOTAL_AMT) AS TOTAL_AMT,SUM(A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,SUM(A.OWN_AMT) AS OWN_AMT,SUM(A.ADDPAY_AMT) AS ADDPAY_AMT, &
                   A.NHI_ORD_CLASS_CODE, &
                   C.NHI_CODE_I,C.OWN_PRICE , MAX(TO_CHAR(A.BILL_DATE,'YYYYMMDD')) BILL_DATE &
                   FROM INS_IBS_ORDER A ,SYS_FEE C  &
                   WHERE A.TOTAL_AMT <> 0  &
// 		   AND C.ACTIVE_FLG='Y' &
       		   AND A.ORDER_CODE=C.ORDER_CODE &
		   AND A.CASE_NO=<CASE_NO> &
         	   AND A.YEAR_MON = <YEAR_MON> &
                   GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,A.PHAADD_FLG,&
      	           A.CARRY_FLG,A.PRICE,A.NHI_ORD_CLASS_CODE,C.NHI_CODE_I,C.OWN_PRICE ORDER BY A.ORDER_CODE
queryOldSplit.item=NHI_ORD_CLASS_CODE
queryOldSplit.NHI_ORD_CLASS_CODE=A.NHI_ORD_CLASS_CODE=<NHI_ORD_CLASS_CODE>      	       
queryOldSplit.Debug=N      	      