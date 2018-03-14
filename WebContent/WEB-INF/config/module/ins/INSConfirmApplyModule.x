# 
#  Title:资格确认书下载/开立module
# 
#  Description:资格确认书下载/开立module
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-11-28
#  version 1.0
#

Module.item=queryConfirmApply;insertConfirmApply

//查询资格确认书是否存在
queryConfirmApply=TSQL
queryConfirmApply.SQL=SELECT 
			   CONFIRM_NO AS CONFIRM_NO2 , REGION_CODE , HOSP_CLASS_CODE,&
			   INS_ADM_CATEGORY, PERSONAL_NO, IDNO AS IDNO1, &
			   PAT_NAME, SEX_CODE, BIRTH_DATE, &
			   AGE, CTZ1_CODE, UNIT_NO, &
			   INS_UNIT, UNIT_CODE, UNIT_DESC, &
			   DEPT_DESC, DIAG_DESC, IN_DATE, &
			   EMG_FLG1, INP_TIME, INS_FLG, &
			   TRANHOSP_NO, TRANHOSP_DESC, ADM_SEQ, &
			   TRAN_CLASS, TRAN_NUM, HOMEBED_TYPE, &
			   HOMEDIAG_DESC, HOMEBED_TIME, HOMEBED_DAYS, &
			   ADDINS_AMT, INSBRANCH_CODE, ADDOWN_AMT, &
			   ADDPAY_AMT, ADDNUM_AMT, INSBASE_LIMIT_BALANCE,& 
			   INS_LIMIT_BALANCE, START_STANDARD_AMT, RESTART_STANDARD_AMT,&
			   OWN_RATE, DECREASE_RATE, REALOWN_RATE, &
			   INSOWN_RATE, STATION_DESC, BED_NO, &
			   TRANHOSP_RESTANDARD_AMT, DS_DATE, DSDIAG_CODE, &
			   DSDIAG_DESC, DSDIAG_DESC1, DEPT_CODE, &
			   TRANHOSP_DAYS, INLIMIT_DATE, ADM_PRJ, &
			   SPEDRS_CODE, INSOCC_CODE, MR_NO, &
			   OVERINP_FLG, INSCASE_NO, OPEN_FLG, &
			   SOURCE_CODE, CANCEL_FLG, CONFIRM_SRC, &
			   OPT_USER, OPT_DATE, OPT_TERM, &
			   INS_STATUS, UP_DATE, DOWN_DATE, &
			   AUD_DATE, DOWND_DATE, CASE_NO, &
			   BEARING_OPERATIONS_TYPE, INJURY_CONFIRM_NO, ENTERPRISES_TYPE, &
			   SPECIAL_PAT, PAT_CLASS, TREATMENT_CLASS, &
			   ZFBL2, SDISEASE_CODE &
			   FROM INS_ADM_CONFIRM 
queryConfirmApply.item=CONFIRM_NO;INSCASE_NO;CANCEL_FLG
queryConfirmApply.CONFIRM_NO=CONFIRM_NO=<CONFIRM_NO>
queryConfirmApply.INSCASE_NO=INSCASE_NO=<INSCASE_NO>
queryConfirmApply.CANCEL_FLG=CANCEL_FLG=<CANCEL_FLG>
queryConfirmApply.Debug=N


insertConfirmApply=TSQL
insertConfirmApply.SQL=INSERT INTO ADM_CONFIRM(&
                      CONFIRM_NO,REGION_CODE,HOSP_CLASS_CODE,&
                      ADM_CATEGORY,PERSONAL_NO,IDNO,&
                      PAT_NAME,SEX_CODE,BIRTH_DATE,&
                      AGE,CTZ1_CODE,UNIT_NO,&
                      INS_UNIT,UNIT_CODE,UNIT_DESC,&
                      DEPT_DESC,DIAG_DESC,IN_DATE,&
                      EMG_FLG,INP_TIME,INS_FLG,&
                      TRANHOSP_NO,TRANHOSP_DESC,ADM_SEQ,&
                      TRAN_CLASS,TRAN_NUM,HOMEBED_TYPE,&
                      HOMEDIAG_DESC,HOMEBED_TIME,HOMEBED_DAYS,&
                      ADDINS_AMT,INSBRANCH_CODE,ADDOWN_AMT,&
                      ADDPAY_AMT,ADDNUM_AMT,INSBASE_LIMIT_BALANCE,&
                      INS_LIMIT_BALANCE,START_STANDARD_AMT,RESTART_STANDARD_AMT,&
                      OWN_RATE,DECREASE_RATE,REALOWN_RATE,&
                      INSOWN_RATE,STATION_DESC,BED_NO,&
                      TRANHOSP_RESTANDARD_AMT,DS_DATE,DSDIAG_CODE,&
                      DSDIAG_DESC,DSDIAG_DESC1,DEPT_CODE,&
                      TRANHOSP_DAYS,INLIMIT_DATE,ADM_PRJ,&
                      SPEDRS_CODE,INSOCC_CODE,MR_NO,&
                      OVERINP_FLG,INSCASE_NO,OPEN_FLG,&
                      SOURCE_CODE,CONFIRM_SRC,CANCEL_FLG,&
                      OPT_USER,OPT_DATE,OPT_TERM,INS_STATUS,UP_DATE,CASE_NO,ENTERPRISES_TYPE, &
                      SPECIAL_PAT,PAT_CLASS,INJURY_CONFIRM_NO,ZFBL2)&
                      VALUES(<CONFIRM_NO>,<REGION_CODE>,<HOSP_CLASS_CODE>,&
                      <ADM_CATEGORY>,<PERSONAL_NO>,<IDNO>,&
                      <PAT_NAME>,<SEX_CODE>,<BIRTH_DATE>,&
                      <AGE>,<CTZ1_CODE>,<UNIT_NO>,&
                      <INS_UNIT>,<UNIT_CODE>,<UNIT_DESC>,&
                      <DEPT_DESC>,<DIAG_DESC>,<IN_DATE>,&
                      <EMG_FLG>,<INP_TIME>,<INS_FLG>,&
                      <TRANHOSP_NO>,<TRANHOSP_DESC>,<ADM_SEQ>,&
                      <TRAN_CLASS>,<TRAN_NUM>,<HOMEBED_TYPE>,&
                      <HOMEDIAG_DESC>,<HOMEBED_TIME>,<HOMEBED_DAYS>,&
                      <ADDINS_AMT>,<INSBRANCH_CODE>,<ADDOWN_AMT>,&
                      <ADDPAY_AMT>,<ADDNUM_AMT>,<INSBASE_LIMIT_BALANCE>,&
                      <INS_LIMIT_BALANCE>,<START_STANDARD_AMT>,<RESTART_STANDARD_AMT>,&
                      <OWN_RATE>,<DECREASE_RATE>,<REALOWN_RATE>,&
                      <INSOWN_RATE>,<STATION_DESC>,<BED_NO>,&
                      <TRANHOSP_RESTANDARD_AMT>,<DS_DATE>,<DSDIAG_CODE>,&
                      <DSDIAG_DESC>,<DSDIAG_DESC1>,<DEPT_CODE>,&
                      <TRANHOSP_DAYS>,<INLIMIT_DATE>,<ADM_PRJ>,&
                      <SPEDRS_CODE>,<INSOCC_CODE>,<MR_NO>,&
                      <OVERINP_FLG>,<INSCASE_NO>,<OPEN_FLG>,&
                      <SOURCE_CODE>,<CONFIRM_SRC>,<CANCEL_FLG>,&
                      <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<INS_STATUS>,<UP_DATE>,<CASE_NO>,<ENTERPRISES_TYPE>, &
                      <SPECIAL_PAT>,<PAT_CLASS>,<INJURY_CONFIRM_NO>,<ZFBL2>)
insertConfirmApply.Debug=N