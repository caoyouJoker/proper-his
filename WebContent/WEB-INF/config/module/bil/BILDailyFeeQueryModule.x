###############################################
# <p>Title:每日费用清单 </p>
#
# <p>Description:每日费用清单 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2010-9-28
# @version 1.0
###############################################
Module.item=selectdata;selectInpInfo

selectdata.Type=TSQL
selectdata.SQL=SELECT 'N' AS FLG,A.STATION_CODE,B.PAT_NAME,B.SEX_CODE,A.MR_NO,A.BED_NO, &
		A.DEPT_CODE,A.IPD_NO,A.CASE_NO,A.CTZ1_CODE,A.IN_DATE,&
		A.DS_DATE,A.MEDDISCH_DATE &
		FROM ADM_INP A,SYS_PATINFO B &
		WHERE A.MR_NO=B.MR_NO &
		AND A.CANCEL_FLG <> 'Y'
selectdata.item=DEPT_CODE;STATION_CODE;BED_NO;MR_NO;BILL_DATE_S;BILL_DATE_E;DS_DATE_S;DS_DATE_E;IN;OUT;IPD_NO;RED_SIGN;YELLOW_SIGN;REGION_CODE
selectdata.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdata.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectdata.BED_NO=A.BED_NO=<BED_NO>
selectdata.MR_NO=A.MR_NO=<MR_NO>
selectdata.IPD_NO=A.IPD_NO=<IPD_NO>
selectdata.BILL_DATE_S=A.IN_DATE >= <BILL_DATE_S>
selectdata.BILL_DATE_E=A.IN_DATE <= <BILL_DATE_E>
selectdata.DS_DATE_S=A.DS_DATE >= <DS_DATE_S>
selectdata.DS_DATE_E=A.DS_DATE <= <DS_DATE_E>
selectdata.IN=A.DS_DATE IS NULL
selectdata.OUT=A.DS_DATE IS NOT NULL
selectdata.RED_SIGN=A.CUR_AMT<A.RED_SIGN
selectdata.YELLOW_SIGN=A.CUR_AMT<A.YELLOW_SIGN
selectdata.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectdata.Debug=N

selectInpInfo.Type=TSQL
selectInpInfo.SQL=SELECT B.DEPT_CHN_DESC,C.STATION_DESC,D.BED_NO_DESC,E.CTZ_DESC,A.IN_DATE &
			FROM ADM_INP A,SYS_DEPT B,SYS_STATION C,SYS_BED D,SYS_CTZ E &
			WHERE A.DEPT_CODE=B.DEPT_CODE &
			AND A.STATION_CODE=C.STATION_CODE &
			AND A.BED_NO=D.BED_NO &
			AND A.CTZ1_CODE=E.CTZ_CODE
selectInpInfo.item=CASE_NO
selectInpInfo.CASE_NO=A.CASE_NO=<CASE_NO>
selectInpInfo.Debug=N