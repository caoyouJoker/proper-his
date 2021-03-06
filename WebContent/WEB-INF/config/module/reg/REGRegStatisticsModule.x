##############################################
# <p>Title:挂号信息统计报表 </p>
#
# <p>Description:挂号信息统计报表 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-03-18
# @version 1.0
##############################################
Module.item=selectNumForDept3;selectNumForDept2;selectOEDpet;selectDept2;selectNumForDept3_M;selectNumForDept2_M

//查询三级科室的挂号人数信息
selectNumForDept3.Type=TSQL
selectNumForDept3.SQL=SELECT D.REGION_CHN_ABN,B.DEPT_CODE,B.DEPT_CHN_DESC,C.CLINICTYPE_CODE,C.CLINICTYPE_DESC,COUNT(A.CASE_NO) AS NUM,A.REGION_CODE &
			FROM REG_PATADM A,SYS_DEPT B,REG_CLINICTYPE C ,SYS_REGION D ,BIL_REG_RECP E &
			WHERE A.DEPT_CODE =B.DEPT_CODE &
			AND A.CASE_NO = E.CASE_NO &
			AND A.REGCAN_DATE IS NULL &
			AND B.DEPT_GRADE='3' &
			AND A.CLINICTYPE_CODE=C.CLINICTYPE_CODE &
			AND A.REGION_CODE=D.REGION_CODE &
			AND E.BILL_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			GROUP BY B.DEPT_CODE,C.CLINICTYPE_CODE,B.DEPT_CHN_DESC,C.CLINICTYPE_DESC,D.REGION_CHN_ABN,A.REGION_CODE ORDER BY D.REGION_CHN_ABN
selectNumForDept3.item=DEPT_CODE;DR_CODE;REGION_CODE
selectNumForDept3.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
selectNumForDept3.DR_CODE=A.DR_CODE=<DR_CODE>
//=========pangben modify 20110410 
selectNumForDept3.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectNumForDept3.Debug=N

//查询二级科室的挂号人数信息
selectNumForDept2.Type=TSQL
selectNumForDept2.SQL=SELECT D.REGION_CHN_ABN,B.CATEGORY_CODE AS DEPT_CODE,B.CATEGORY_CHN_DESC AS DEPT_CHN_DESC,C.CLINICTYPE_CODE,C.CLINICTYPE_DESC,COUNT(A.CASE_NO) AS NUM &
			FROM REG_PATADM A,SYS_CATEGORY B,REG_CLINICTYPE C,SYS_REGION D &
			WHERE SUBSTR(A.DEPT_CODE,0,8) =B.CATEGORY_CODE &
			AND A.REGCAN_DATE IS NULL &
			AND B.RULE_TYPE='SYS_DEPT' &
			AND A.CLINICTYPE_CODE=C.CLINICTYPE_CODE &
			AND A.REGION_CODE=D.REGION_CODE &
			AND A.ADM_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			GROUP BY B.CATEGORY_CODE,C.CLINICTYPE_CODE,B.CATEGORY_CHN_DESC,C.CLINICTYPE_DESC,D.REGION_CHN_ABN ORDER BY D.REGION_CHN_ABN
			//========pangben modify 20110410
selectNumForDept2.item=REGION_CODE
selectNumForDept2.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectNumForDept2.Debug=N

//查询三级科室的挂号收费信息
selectNumForDept3_M.Type=TSQL
selectNumForDept3_M.SQL=SELECT E.REGION_CHN_ABN,B.DEPT_CODE,B.DEPT_CHN_DESC,C.CLINICTYPE_CODE,C.CLINICTYPE_DESC, &
			SUM(NVL(PAY_CASH,0)+NVL(PAY_BANK_CARD,0)+NVL(PAY_CHECK,0)+NVL(PAY_MEDICAL_CARD,0)+NVL(PAY_INS_CARD,0)+NVL(PAY_DEBIT,0) + NVL(PAY_INS,0)) AS NUM ,A.REGION_CODE &
			FROM REG_PATADM A,SYS_DEPT B,REG_CLINICTYPE C,BIL_REG_RECP D ,SYS_REGION E &
			WHERE A.DEPT_CODE =B.DEPT_CODE &
			AND A.REGCAN_DATE IS NULL &
			AND B.DEPT_GRADE='3' &
			AND A.CLINICTYPE_CODE=C.CLINICTYPE_CODE &
			AND A.REGION_CODE=E.REGION_CODE &
			AND D.BILL_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			AND A.CASE_NO=D.CASE_NO &
			GROUP BY B.DEPT_CODE,C.CLINICTYPE_CODE,B.DEPT_CHN_DESC,C.CLINICTYPE_DESC,E.REGION_CHN_ABN,A.REGION_CODE ORDER BY E.REGION_CHN_ABN
selectNumForDept3_M.item=DEPT_CODE;DR_CODE;REGION_CODE
selectNumForDept3_M.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
selectNumForDept3_M.DR_CODE=A.DR_CODE=<DR_CODE>
//========pangben modify 20110410 
selectNumForDept3_M.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectNumForDept3_M.Debug=N

//查询二级科室的挂号人数信息
selectNumForDept2_M.Type=TSQL
selectNumForDept2_M.SQL=SELECT E.REGION_CHN_ABN,B.CATEGORY_CODE AS DEPT_CODE,B.CATEGORY_CHN_DESC AS DEPT_CHN_DESC,C.CLINICTYPE_CODE,C.CLINICTYPE_DESC, &
			SUM(NVL(PAY_CASH,0)+NVL(PAY_BANK_CARD,0)+NVL(PAY_CHECK,0)+NVL(PAY_MEDICAL_CARD,0)+NVL(PAY_INS_CARD,0)+NVL(PAY_DEBIT,0) + NVL(PAY_INS,0)) AS NUM &
			FROM REG_PATADM A,SYS_CATEGORY B,REG_CLINICTYPE C,BIL_REG_RECP D ,SYS_REGION E &
			WHERE SUBSTR(A.DEPT_CODE,0,8) =B.CATEGORY_CODE &
			AND A.REGCAN_DATE IS NULL &
			AND B.RULE_TYPE='SYS_DEPT' &
			AND A.CLINICTYPE_CODE=C.CLINICTYPE_CODE &
			AND A.REGION_CODE=E.REGION_CODE &
			AND A.ADM_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			AND A.CASE_NO=D.CASE_NO &
			GROUP BY B.CATEGORY_CODE,C.CLINICTYPE_CODE,B.CATEGORY_CHN_DESC,C.CLINICTYPE_DESC,E.REGION_CHN_ABN ORDER BY E.REGION_CHN_ABN
			//=======pangben modify 20110410
selectNumForDept2_M.item=REGION_CODE
selectNumForDept2_M.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectNumForDept2_M.Debug=N

//查询门急诊科室(三级科室)
//=========pangben modify 20110413
selectOEDpet.Type=TSQL
selectOEDpet.SQL=SELECT A.DEPT_CODE AS ID,A.DEPT_CHN_DESC AS NAME,B.REGION_CHN_ABN FROM SYS_DEPT A,SYS_REGION B WHERE A.REGION_CODE=B.REGION_CODE AND (OPD_FIT_FLG='Y' OR EMG_FIT_FLG='Y') AND DEPT_GRADE='3'
selectOEDpet.item=REGION_CODE
selectOEDpet.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectOEDpet.Debug=N
//=========pangben modify 20110413

//查询二级科室
selectDept2.Type=TSQL
selectDept2.SQL=SELECT CATEGORY_CODE AS ID,CATEGORY_CHN_DESC AS NAME FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_DEPT' AND DETAIL_FLG='Y'
selectDept2.Debug=N