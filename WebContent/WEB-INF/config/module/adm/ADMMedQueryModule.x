###############################################
# <p>Title:在院病患医技进度查询 </p>
#
# <p>Description:在院病患医技进度查询 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2010-11-11
# @version 1.0
###############################################
Module.item=selectData

//查询
selectData.Type=TSQL
selectData.SQL=SELECT B.ORDER_CODE,B.ORDER_DESC,B.OPTITEM_CHN_DESC,B.ORDER_DATE,B.STATUS, &
		B.ORDER_DR_CODE,B.RESERVED_DATE,B.REGISTER_DATE,B.INSPECT_DATE,B.EXAMINE_DATE, &
		B.EXEC_DR_CODE,B.REPORT_DR,B.EXAMINE_DR,C.MR_NO,C.PAT_NAME &
		FROM ADM_INP A,MED_APPLY B,SYS_PATINFO C &
		WHERE A.CASE_NO=B.CASE_NO &
		AND A.MR_NO=C.MR_NO &
		AND A.DS_DATE IS NULL &
		AND (A.CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL)
selectData.item=MR_NO;DATE_S;DATE_E;ORDERDATE_S;ORDERDATE_E;SUB_SYSTEM_CODE;RPTTYPE_CODE;DEPT_CODE;STATION_CODE;REGION_CODE
selectData.MR_NO=A.MR_NO=<MR_NO>
selectData.DATE_S=B.RESERVED_DATE>=<DATE_S>
selectData.DATE_E=B.RESERVED_DATE<=<DATE_E>
selectData.ORDERDATE_S=B.ORDER_DATE>=<ORDERDATE_S>
selectData.ORDERDATE_E=B.ORDER_DATE<=<ORDERDATE_E>
selectData.SUB_SYSTEM_CODE=B.RPTTYPE_CODE LIKE <SUB_SYSTEM_CODE>
selectData.RPTTYPE_CODE=B.RPTTYPE_CODE=<RPTTYPE_CODE>
selectData.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectData.STATION_CODE=A.STATION_CODE=<STATION_CODE>
//==========pangben modify 20110511 start
selectData.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectData.Debug=N