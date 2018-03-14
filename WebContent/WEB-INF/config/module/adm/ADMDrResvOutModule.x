###############################################
# <p>Title:出院通知 </p>
#
# <p>Description:出院通知 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2010-2-25
# @version 1.0
###############################################
Module.item=selectPrintInfo;selectDiag

//查询打印信息（出院通知书）
selectPrintInfo.Type=TSQL
selectPrintInfo.SQL=SELECT C.CTZ_DESC,B.PAT_NAME,D.CHN_DESC,B.BIRTH_DATE,E.DEPT_CHN_DESC, &
			A.IN_DATE &
			FROM ADM_INP A,SYS_PATINFO B,SYS_CTZ C,SYS_DICTIONARY D,SYS_DEPT E &
			WHERE A.MR_NO=B.MR_NO &
			AND A.CTZ1_CODE=C.CTZ_CODE &
			AND B.SEX_CODE = D.ID &
			AND D.GROUP_ID='SYS_SEX' &
			AND A.DEPT_CODE=E.DEPT_CODE &
			AND A.CASE_NO=<CASE_NO>
selectPrintInfo.Debug=N

//查询病患的诊断
selectDiag.Type=TSQL
selectDiag.SQL=SELECT A.CASE_NO, A.IO_TYPE, A.ICD_CODE, &
		   A.MAINDIAG_FLG, A.ICD_TYPE, A.SEQ_NO, &
		   A.MR_NO, A.IPD_NO, A.DESCRIPTION, &
		   A.OPT_USER, A.OPT_DATE, A.OPT_TERM, B.ICD_CHN_DESC &
		FROM ADM_INPDIAG A,SYS_DIAGNOSIS B &
		WHERE A.ICD_CODE=B.ICD_CODE &
		ORDER BY A.SEQ_NO
selectDiag.item=IO_TYPE;MAINDIAG_FLG;CASE_NO;ICD_TYPE
selectDiag.IO_TYPE=A.IO_TYPE=<IO_TYPE>
selectDiag.MAINDIAG_FLG=A.MAINDIAG_FLG=<MAINDIAG_FLG>
selectDiag.CASE_NO=A.CASE_NO=<CASE_NO>
selectDiag.ICD_TYPE=A.ICD_TYPE=<ICD_TYPE>
selectDiag.Debug=N