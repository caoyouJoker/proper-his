##############################################
# <p>Title:��������ͳ�Ʊ� </p>
#
# <p>Description:��������ͳ�Ʊ� </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-4-8
# @version 1.0
##############################################
Module.item=selectData;selectDetial

//��ѯ�������ҵ�ͳ����Ϣ
//=============pangben modify 20110414 
selectData.Type=TSQL
selectData.SQL=SELECT E.REGION_CHN_DESC,A.DEPT_CODE,A.DR_CODE,SUM(A.AR_AMT) AS AR_AMT,A.REXP_CODE,B.DEPT_CHN_DESC,C.USER_NAME &
		FROM OPD_ORDER A,SYS_DEPT B,SYS_OPERATOR C,BIL_OPB_RECP D,SYS_REGION E &
		WHERE A.DEPT_CODE = B.DEPT_CODE(+) AND A.REGION_CODE=E.REGION_CODE &
		AND A.DR_CODE = C.USER_ID(+) & 
		AND A.BILL_FLG='Y' &
		AND A.CASE_NO = D.CASE_NO(+) &
		AND A.RECEIPT_NO = D.RECEIPT_NO(+) &
		AND D.BILL_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
		GROUP BY A.DEPT_CODE,A.DR_CODE,B.DEPT_CHN_DESC,C.USER_NAME,A.REXP_CODE,E.REGION_CHN_DESC &
		ORDER BY E.REGION_CHN_DESC,A.DEPT_CODE,A.DR_CODE 
selectData.item=DEPT_CODE;DR_CODE;REGION_CODE;ADM_TYPE
selectData.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectData.DR_CODE=A.DR_CODE=<DR_CODE>
selectData.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectData.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selectData.Debug=N

//��ϸ��ѯ
selectDetial.Type=TSQL
selectDetial.SQL=SELECT A.DEPT_CODE,A.DR_CODE,B.SESSION_CODE,B.QUE_NO,B.MR_NO, &
			C.PAT_NAME,A.CTZ1_CODE,A.ORDER_DESC,A.OWN_PRICE,A.DOSAGE_QTY,A.OWN_AMT &
			FROM OPD_ORDER A,REG_PATADM B,SYS_PATINFO C,BIL_OPB_RECP D &
			WHERE A.CASE_NO=B.CASE_NO &
			AND B.MR_NO=C.MR_NO &
			AND A.BILL_FLG='Y' &
			AND A.CASE_NO = D.CASE_NO(+) &
			AND A.RECEIPT_NO = D.RECEIPT_NO(+) &
			AND D.BILL_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			ORDER BY A.DEPT_CODE,A.DR_CODE,C.PAT_NAME
selectDetial.item=DEPT_CODE;DR_CODE
selectDetial.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectDetial.DR_CODE=A.DR_CODE=<DR_CODE>
selectDetial.Debug=N