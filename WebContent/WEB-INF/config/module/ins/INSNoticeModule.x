# 
#  Title:中心通知下载module
# 
#  Description:中心通知下载module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata

//查询
selectdata.Type=TSQL
selectdata.SQL=SELECT NOTICE_NO REP_NO,NOTICE_DATE REP_DATE,NOTICE_TITLE REP_CODE,NOTICE_DESC REP_DESC,PUBLISH_FLG,OPT_USER,OPT_DATE,OPT_TERM ,'Y' SAVE_FLG &
		 FROM INS_NOTICE &
		WHERE TO_DATE(SUBSTR(NOTICE_DATE,1,10),'YYYY/MM/DD')  BETWEEN TO_DATE(SUBSTR(<START_DATE>,1,10),'YYYY/MM/DD') AND TO_DATE(SUBSTR(<END_DATE>,1,10),'YYYY/MM/DD') &
	     ORDER BY NOTICE_NO
selectdata.Debug=N

//根据时间区间删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM INS_NOTICE &
		     WHERE NOTICE_NO=<REP_NO>

		     
		     
deletedata.Debug=N

//新增
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO INS_NOTICE &
			   (NOTICE_NO,NOTICE_DATE,NOTICE_TITLE,NOTICE_DESC,PUBLISH_FLG,&
			   OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<REP_NO>,<REP_DATE>,<REP_CODE>,<REP_DESC>,<PUBLISH_FLG>,&
		           <OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N


