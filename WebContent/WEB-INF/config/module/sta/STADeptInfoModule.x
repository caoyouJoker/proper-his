# 
#  Title:对照科室信息module
# 
#  Description:对照科室信息module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2010.07.26
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata

//查询
selectdata.Type=TSQL
selectdata.SQL=SELECT STA_DEPT_CODE,BED_NUM,BED_ACTIVE_NUM,DR_NUM,PROF_DR_NUM,&
		ATTEND_DR_NUM,VS_DR_NUM,INDUCATION_DR_NUM,NS_NUM,VS_NURSE_NUM,&
		OPT_USER,OPT_DATE,OPT_TERM &
		FROM STA_OEI_DEPT_INFO
selectdata.item=STA_DEPT_CODE;REGION_CODE
selectdata.STA_DEPT_CODE=STA_DEPT_CODE=<STA_DEPT_CODE>
//============pangben modify 20110523 
selectdata.REGION_CODE=REGION_CODE=<REGION_CODE>
selectdata.Debug=N

//删除
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM STA_OEI_DEPT_INFO &
		WHERE STA_DEPT_CODE = <STA_DEPT_CODE>
deletedata.Debug=N

//新增
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO STA_OEI_DEPT_INFO (STA_DEPT_CODE,BED_NUM,BED_ACTIVE_NUM,DR_NUM,PROF_DR_NUM,ATTEND_DR_NUM,VS_DR_NUM,INDUCATION_DR_NUM,NS_NUM,VS_NURSE_NUM,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE) &
		VALUES(<STA_DEPT_CODE>,<BED_NUM>,<BED_ACTIVE_NUM>,<DR_NUM>,<PROF_DR_NUM>,<ATTEND_DR_NUM>,<VS_DR_NUM>,<INDUCATION_DR_NUM>,<NS_NUM>,<VS_NURSE_NUM>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE>)
insertdata.Debug=N

//更新
updatedata.Type=TSQL
updatedata.SQL=UPDATE STA_OEI_DEPT_INFO SET BED_NUM=<BED_NUM>, &
		BED_ACTIVE_NUM=<BED_ACTIVE_NUM>, &
		DR_NUM=<DR_NUM>, &
		PROF_DR_NUM=<PROF_DR_NUM>, &
		ATTEND_DR_NUM=<ATTEND_DR_NUM>, &
		VS_DR_NUM=<VS_DR_NUM>, &
		INDUCATION_DR_NUM=<INDUCATION_DR_NUM>, &
		NS_NUM=<NS_NUM>, &
		VS_NURSE_NUM=<VS_NURSE_NUM>, &
		OPT_USER=<OPT_USER>, &
		OPT_DATE=SYSDATE, &
		OPT_TERM=<OPT_TERM> &
		WHERE STA_DEPT_CODE=<STA_DEPT_CODE>
updatedata.Debug=N