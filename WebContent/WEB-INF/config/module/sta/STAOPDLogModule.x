# 
#  Title: 门急诊日志
# 
#  Description: 门急诊日志
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.07.07
#  version 1.0
#
Module.item=selectData;selectPrintData;update_STA_OPD_DAILY

selectData.Type=TSQL
selectData.SQL=SELECT &
		   STA_DATE, DEPT_CODE, OUTP_NUM, &
		   ERD_NUM, HRM_NUM, OTHER_NUM, &
		   GET_TIMES, PROF_DR, COMM_DR, &
		   DR_HOURS, SUCCESS_TIMES, OBS_NUM, &
		   ERD_DIED_NUM, OBS_DIED_NUM, OPE_NUM, &
		   FIRST_NUM, FURTHER_NUM, OPT_USER, &
		   OPT_DATE, OPT_TERM, CONFIRM_FLG, &
		   CONFIRM_USER, CONFIRM_DATE &
		FROM STA_OPD_DAILY &
		WHERE STA_DATE = <STA_DATE>
selectData.REGION_CODE=REGION_CODE=<REGION_CODE>
selectData.Debug=N

//获取打印数据
//===========pangben modify 20110519 添加区域条件查询
selectPrintData.Type=TSQL
selectPrintData.SQL=SELECT DISTINCT A.DEPT_CODE, B.DEPT_DESC, A.OUTP_NUM, A.ERD_NUM, A.HRM_NUM, A.OTHER_NUM, &
			       A.GET_TIMES, A.PROF_DR, A.COMM_DR, A.DR_HOURS, A.SUCCESS_TIMES,&
			       A.OBS_NUM, A.ERD_DIED_NUM, A.OBS_DIED_NUM, A.OPE_NUM, A.FIRST_NUM,&
			       A.FURTHER_NUM,A.STA_DATE &
			  FROM STA_OPD_DAILY A, STA_OEI_DEPT_LIST B &
			 WHERE A.DEPT_CODE = B.DEPT_CODE &
			   AND A.STA_DATE = <STA_DATE> &
		ORDER BY A.DEPT_CODE
//===========pangben modify 20110519
selectPrintData.item=REGION_CODE
selectPrintData.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectPrintData.Debug=N


//修改 门诊日志表信息 
update_STA_OPD_DAILY.Type=TSQL
update_STA_OPD_DAILY.SQL=UPDATE STA_OPD_DAILY SET  &
				    OUTP_NUM=<OUTP_NUM>,&
				    ERD_NUM=<ERD_NUM>,&
				    HRM_NUM=<HRM_NUM>,&
				    OTHER_NUM=<OTHER_NUM>,&
				    GET_TIMES=<GET_TIMES>,&
				    PROF_DR=<PROF_DR>,&
				    COMM_DR=<COMM_DR>,&
				    DR_HOURS=<DR_HOURS>,&
				    SUCCESS_TIMES=<SUCCESS_TIMES>,&
				    OBS_NUM=<OBS_NUM>,&
				    ERD_DIED_NUM=<ERD_DIED_NUM>,&
				    OBS_DIED_NUM=<OBS_DIED_NUM>,&
				    OPE_NUM=<OPE_NUM>,&
				    FIRST_NUM=<FIRST_NUM>,&
				    FURTHER_NUM=<FURTHER_NUM>,&
		                    APPT_NUM=<APPT_NUM>,&
				    ZR_DR_NUM=<ZR_DR_NUM>,&
				    ZZ_DR_NUM=<ZZ_DR_NUM>,&
				    ZY_DR_NUM=<ZY_DR_NUM>,&
				    ZX_DR_NUM=<ZX_DR_NUM>,&
		                    CONFIRM_FLG=<CONFIRM_FLG>,&
				    CONFIRM_USER=<CONFIRM_USER>,&
		                    CONFIRM_DATE=<CONFIRM_DATE>,&
				    OPT_USER=<OPT_USER>,&
				    OPT_TERM=<OPT_TERM>,&
				    OPT_DATE=SYSDATE &
				WHERE STA_DATE=<STA_DATE> &
				AND DEPT_CODE=<DEPT_CODE>
update_STA_OPD_DAILY.Debug=N