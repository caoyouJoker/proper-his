# 
#  Title:病历主索引module
# 
#  Description:病历主索引module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.06.15
#  version 1.0
#
Module.item=existdata;insertOpdEmg;insertIpd;updateIpd

//查询病历主索引是否存在
existdata.Type=TSQL
existdata.SQL=SELECT COUNT(CASE_NO) AS COUNT FROM SYS_EMR_INDEX WHERE CASE_NO=<CASE_NO>
existdata.Debug=N


//新增门诊、急诊挂号写入病历主索引
insertOpdEmg.Type=TSQL
insertOpdEmg.SQL=INSERT INTO SYS_EMR_INDEX( &
			CASE_NO, ADM_TYPE, REGION_CODE, MR_NO, ADM_DATE, &
			DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
		 VALUES(<CASE_NO>, <ADM_TYPE>, <REGION_CODE>, <MR_NO>, <ADM_DATE>, &
			<DEPT_CODE>, <DR_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertOpdEmg.Debug=N


//新增住院登记写入病历主索引
insertIpd.Type=TSQL
insertIpd.SQL=INSERT INTO SYS_EMR_INDEX( &
		     CASE_NO, ADM_TYPE, REGION_CODE, MR_NO, ADM_DATE, &
		     DEPT_CODE, DR_CODE, OPT_USER, OPT_DATE, OPT_TERM, &
		     IPD_NO) &
	      VALUES(<CASE_NO>, <ADM_TYPE>, <REGION_CODE>, <MR_NO>, <ADM_DATE>, &
		     <DEPT_CODE>, <DR_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>, &
		     <IPD_NO>)
insertIpd.Debug=N


//出院写入病历主索引
updateIpd.Type=TSQL
updateIpd.SQL=UPDATE SYS_EMR_INDEX SET &
		     DR_DATE=<DR_DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
	       WHERE CASE_NO=<CASE_NO>
updateIpd.Debug=N


