#
# Title:临床路径-费用分析
#
# Description:临床路径-费用分析
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/05/04
Module.item=selectPatientData;selectBillData;selectChargeHeader;getaverageCharge;selectDurationSchdDay

selectPatientData.Type=TSQL
selectPatientData.SQL=SELECT A.CASE_NO,C.DEPT_CODE,C.BED_NO,C.MR_NO,P.PAT_NAME,C.CLNCPATH_CODE,A.SCHD_CODE,A.START_DAY,B.AVERAGECOST &
											FROM ADM_INP C  &
											LEFT JOIN CLP_MANAGED A ON C.CASE_NO=A.CASE_NO &
											LEFT JOIN SYS_PATINFO P ON P.MR_NO=C.MR_NO &
											LEFT JOIN CLP_BSCINFO B ON B.CLNCPATH_CODE=A.CLNCPATH_CODE
											WHERE C.CASE_NO=<CASE_NO> 
selectPatientData.Debug=N



selectBillData.Type=TSQL
selectBillData.SQL=SELECT '' AS SCHD_TYPE_DESC , C.CASE_NO, &
                   C.CLNCPATH_CODE, C.SCHD_TYPE, C.SCHD_CODE,A.DURATION_CHN_DESC, C.START_DAY, C.REGION_CODE, &
                   C.STANGDING_DTTM, C.TOT, C.REXP_01,C.REXP_02, C.REXP_03, C.REXP_04,C.REXP_05, C.REXP_06, C.REXP_07, &
                   C.REXP_08, C.REXP_09, C.REXP_10,C.REXP_11, C.REXP_12, C.REXP_13,C.REXP_14, C.REXP_15, C.REXP_16, &
                   C.REXP_17, C.REXP_18, C.REXP_19,C.REXP_20, C.REXP_21, C.REXP_22,C.REXP_23, C.REXP_24, C.REXP_25, &
                   C.REXP_26, C.REXP_27, C.REXP_28,C.REXP_29, C.REXP_30 &
		   FROM CLP_BILL C ,CLP_THRPYSCHDM_REAL B,CLP_DURATION A &
		   WHERE  C.CASE_NO=<CASE_NO> AND C.REGION_CODE = <REGION_CODE> AND C.CLNCPATH_CODE=B.CLNCPATH_CODE(+) &
		   AND C.CASE_NO=B.CASE_NO(+) AND C.SCHD_CODE=B.SCHD_CODE(+) AND C.SCHD_CODE=A.DURATION_CODE(+) &
		   ORDER BY C.SCHD_CODE 
selectBillData.item=CLNCPATH_CODE;SCHD_CODE;SCHD_DAY
selectBillData.CLNCPATH_CODE= C.CLNCPATH_CODE=<CLNCPATH_CODE>
selectBillData.SCHD_CODE= C.SCHD_CODE LIKE <SCHD_CODE>
selectBillData.SCHD_DAY= B.SCHD_DAY=<SCHD_DAY>
selectBillData.Debug=N

selectChargeHeader.Type=TSQL
selectChargeHeader.SQL=SELECT CHN_DESC,ID AS ID FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CHARGE' ORDER BY GROUP_ID 
selectChargeHeader.Debug=N

getaverageCharge.Type=TSQL
getaverageCharge.SQL=SELECT AVERAGECOST FROM CLP_BSCINFO WHERE CLNCPATH_CODE=<CLNCPATH_CODE>
getaverageCharge.Debug=N


selectDurationSchdDay.Type=TSQL
selectDurationSchdDay.SQL=SELECT SCHD_DAY FROM  CLP_THRPYSCHDM_REAL WHERE CLNCPATH_CODE=<CLNCPATH_CODE> &
													AND CASE_NO=<CASE_NO>
selectDurationSchdDay.item=SCHD_CODE
selectDurationSchdDay.SCHD_CODE=SCHD_CODE LIKE <SCHD_CODE>												
selectDurationSchdDay.Debug=N