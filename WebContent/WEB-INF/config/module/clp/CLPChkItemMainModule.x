# 
#  Title:关键诊疗项目执行module
# 
#  Description:临床
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author zhangjg 2011.04.21
#  version 1.0
#
Module.item=queryByClncPathCode;queryByDurationCode;queryPatientInfo;queryManagerDBySchdCode;insertManagerD;updateManagerD;deleteManagerD

#根据CLNCPATH_CODE查询时程
queryByClncPathCode.Type=TSQL
queryByClncPathCode.SQL=SELECT * &
		FROM CLP_THRPYSCHDM CT, CLP_DURATION CD &
		WHERE CT.SCHD_CODE = CD.DURATION_CODE &
		AND CT.CLNCPATH_CODE = <CLNCPATH_CODE>
queryByClncPathCode.Debug=N

#根据DURATION_CODE和PARENT_CODE查询时程
queryByDurationCode.Type=TSQL
queryByDurationCode.SQL= SELECT DURATION_CODE, REGION_CODE, DURATION_CHN_DESC, &
			   DURATION_ENG_DESC, PY1, PY2, &
			   SEQ, PARENT_CODE, LEAF_FLG, &
			   DESCRIPTION &
			FROM  CLP_DURATION
queryByDurationCode.item=DURATION_CODE;PARENT_CODE
queryByDurationCode.DURATION_CODE= DURATION_CODE = <DURATION_CODE>
queryByDurationCode.PARENT_CODE= PARENT_CODE = <PARENT_CODE>
queryByDurationCode.Debug=N

#病人信息
queryPatientInfo.Type=TSQL
queryPatientInfo.SQL=SELECT &
         CM.CASE_NO, &
         CM.CLNCPATH_CODE, &
	 CM.REGION_CODE, &
	 CM.VERSION, &
	 CM.MR_NO, &
	 CM.IN_DATE, &
	 CM.START_DTTM, &
	 CM.DELETE_DTTM, &
	 CM.END_DTTM, &
	 CM.EVL_CODE, &
	 CM.STATUS, &
	 CM.DESCRIPTION, &
	 CM.OUTISSUE_CODE, &
	 CM.CHANGE_REASON, &
	 AI.DEPT_CODE, &
         AI.STATION_CODE, &
         AI.BED_NO, &
         AI.IPD_NO, &
         AI.VS_DR_CODE, &
         AI.DS_DATE, &
         SP.PAT_NAME, &
         SP.SEX_CODE, &
         CB.STAYHOSP_DAYS, &
         CB.AVERAGECOST &
  FROM CLP_MANAGEM CM, ADM_INP AI, SYS_PATINFO SP, CLP_BSCINFO CB &
  WHERE CM.CASE_NO = AI.CASE_NO &
  AND CM.MR_NO = SP.MR_NO &
  AND CM.CLNCPATH_CODE = CB.CLNCPATH_CODE &
  AND CM.CASE_NO = <CASE_NO> &
  ORDER BY AI.CASE_NO
queryPatientInfo.Debug=N

#CLP_MANAGED
queryManagerDBySchdCode.Type=TSQL
queryManagerDBySchdCode.SQL=SELECT CMD.* &
		FROM CLP_MANAGED CMD &
	WHERE CMD.CASE_NO = CMM.CASE_NO
	AND CMD.CASE_NO = <CASE_NO> &
	AND CMD.CLNCPATH_CODE = <CLNCPATH_CODE> &
	AND CMD.SCHD_CODE = <SCHD_CODE> &
	AND CMD.ORDER_FLG = <ORDER_FLG> &
queryManagerDBySchdCode.Debug=N

#插入CLP_MANAGED
insertManagerD.Type=TSQL
insertManagerD.SQL=INSERT INTO CLP_MANAGED &
  (CASE_NO, &
   CLNCPATH_CODE, &
   SCHD_CODE, &
   ORDER_NO, &
   ORDER_SEQ, &
   REGION_CODE, &
   ORDER_CODE, &
   CHKTYPE_CODE, &
   START_DAY, &
   STANDING_DTTM, &
   CHKUSER_CODE, &
   EXEC_FLG, &
   TOT, &
   DISPENSE_UNIT, &
   STANDARD, &
   ORDER_FLG, &
   SCHD_DESC, &
   CHANGE_FLG, &
   STANDARD_FLG, &
   MAINORD_CODE, &
   MAINTOT, &
   MAINDISPENSE_UNIT, &
   CFM_DTTM, &
   CFM_USER, &
   PROGRESS_CODE, &
   MEDICAL_MONCAT, &
   MEDICAL_VARIANCE, &
   MEDICAL_NOTE, &
   MANAGE_MONCAT, &
   MANAGE_VARIANCE, &
   MANAGE_NOTE, &
   MANAGE_DTTM, &
   MANAGE_USER, &
   R_DEPT_CODE, &
   R_USER, &
   TOT_AMT, &
   MAIN_AMT, &
   MAINCFM_USER, &
   ORDTYPE_CODE, &
   DEPT_CODE, &
   EXE_DEPT_CODE, &
   OPT_USER, &
   OPT_DATE, &
   OPT_TERM) &
VALUES &
  (<CASE_NO>, &
   <CLNCPATH_CODE>, &
   <SCHD_CODE>, &
   <ORDER_NO>, &
   <ORDER_SEQ>, &
   <REGION_CODE>, &
   <ORDER_CODE>, &
   <CHKTYPE_CODE>, &
   <START_DAY>, &
   TO_DATE(<STANDING_DTTM>, 'YYYY/MM/DD HH24:MI:SS'), &
   <CHKUSER_CODE>, &
   <EXEC_FLG>, &
   <TOT>, &
   <DISPENSE_UNIT>, &
   <STANDARD>, &
   <ORDER_FLG>, &
   <SCHD_DESC>, &
   <CHANGE_FLG>, &
   <STANDARD_FLG>, &
   <MAINORD_CODE>, &
   <MAINTOT>, &
   <MAINDISPENSE_UNIT>, &
   TO_DATE(<CFM_DTTM>, 'YYYY/MM/DD HH24:MI:SS'), &
   <CFM_USER>, &
   <PROGRESS_CODE>, &
   <MEDICAL_MONCAT>, &
   <MEDICAL_VARIANCE>, &
   <MEDICAL_NOTE>, &
   <MANAGE_MONCAT>, &
   <MANAGE_VARIANCE>, &
   <MANAGE_NOTE>, &
   TO_DATE(<MANAGE_DTTM>, 'YYYY/MM/DD HH24:MI:SS'), &
   <MANAGE_USER>, &
   <R_DEPT_CODE>, &
   <R_USER>, &
   <TOT_AMT>, &
   <MAIN_AMT>, &
   <MAINCFM_USER>, &
   <ORDTYPE_CODE>, &
   <DEPT_CODE>, &
   <EXE_DEPT_CODE>, &
   <OPT_USER>, &
   TO_DATE(<OPT_DATE>, 'YYYY/MM/DD HH24:MI:SS'), &
   <OPT_TERM>)
insertManagerD.Debug=N

#更新CLP_MANAGED
updateManagerD.Type=TSQL
updateManagerD.SQL=UPDATE CLP_MANAGED SET &
   PROGRESS_CODE = <PROGRESS_CODE>, &
   MANAGE_NOTE = <MANAGE_NOTE>, &
   OPT_USER = <OPT_USER>, &
   OPT_DATE = TO_DATE(<OPT_DATE>, 'YYYY/MM/DD HH24:MI:SS'), &
   OPT_TERM = <OPT_TERM> &
WHERE CASE_NO = <CASE_NO> &
   AND CLNCPATH_CODE = <CLNCPATH_CODE> &
   AND SCHD_CODE = <SCHD_CODE> &
   AND ORDER_NO = <ORDER_NO> &
   AND ORDER_SEQ = <ORDER_SEQ>
updateManagerD.Debug=N

#删除CLP_MANAGED
deleteManagerD.Type=TSQL
deleteManagerD.SQL=DELETE FROM CLP_MANAGED WHERE &
		CASE_NO = <CASE_NO> &
		AND CLNCPATH_CODE = <CLNCPATH_CODE> &
		AND SCHD_CODE = <SCHD_CODE> &
		AND ORDER_NO = <ORDER_NO> &
		AND ORDER_SEQ = <ORDER_SEQ>
deleteManagerD.Debug=N