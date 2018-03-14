######################################################
# <p>Title:门急诊工作量统计Module </p>
#
# <p>Description:门急诊工作量统计Module </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author zhangk 2010-2-4
# @version 4.0
#
######################################################
Module.item=selectData

//查询信息
//============pangben modify 20110418 添加区域显示
selectData.Type=TSQL
selectData.SQL=SELECT I.REGION_CHN_DESC,G.USER_NAME,C.PAT_NAME,E.CHN_DESC,C.BIRTH_DATE,A.ADM_DATE,&
		C.ADDRESS,D.ICD_CHN_DESC,TO_CHAR(F.ILLNESS_DATE,'yyyy-mm-dd') AS ILLNESS_DATE,A.VISIT_CODE,F.PAD_DATE, &
		H.DEPT_CHN_DESC &
		FROM REG_PATADM A,OPD_DIAGREC B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,MRO_INFECT_RECORD F,SYS_OPERATOR G,SYS_DEPT H,SYS_REGION I &
		WHERE A.CASE_NO=B.CASE_NO &
		AND B.MAIN_DIAG_FLG='Y' &
		AND A.REGION_CODE=I.REGION_CODE &
		AND A.MR_NO=C.MR_NO &
		AND B.ICD_CODE=D.ICD_CODE &
		AND E.GROUP_ID='SYS_SEX' &
		AND C.SEX_CODE=E.ID &
		AND A.CASE_NO=F.CASE_NO(+) &
		AND A.REALDR_CODE=G.USER_ID &
		AND A.REALDEPT_CODE = H.DEPT_CODE &
		ORDER BY I.REGION_CHN_DESC
		//=================pangben modify 20110406 start 
selectData.item=ADM_DATE;ADM_TYPE;SESSION_CODE;DEPT_CODE;DR_CODE;REGION_CODE
selectData.ADM_DATE=TO_CHAR(A.ADM_DATE,'yyyymmdd')=<ADM_DATE>
selectData.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selectData.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
selectData.DEPT_CODE=A.REALDEPT_CODE=<DEPT_CODE>
selectData.DR_CODE=A.REALDR_CODE=<DR_CODE>
//=================pangben modify 20110406 start 
selectData.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//=================pangben modify 20110406 stop
selectData.Debug=N