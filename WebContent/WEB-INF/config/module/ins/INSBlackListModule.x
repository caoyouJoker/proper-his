# 
#  Title:诚信审核信息下载module
# 
#  Description:诚信审核信息下载module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata

//查询
selectdata.Type=TSQL
selectdata.SQL=SELECT SEQ_NO,DR_NAME,BLIST_NO,VO_BDATE,VO_SDATE,ST_BDATE,ST_SDATE,ST_DESC,VO_DESC,BLIST_TYPE,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM INS_BLACK_LIST &
		WHERE VO_BDATE  BETWEEN TO_DATE(<START_DATE>,'YYYY/MM/DD') AND TO_DATE(<END_DATE>,'YYYY/MM/DD') AND DR_NAME like <DR_NAME>&
	     ORDER BY SEQ_NO
selectdata.Debug=N

//根据时间区间删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM INS_BLACK_LIST &
		     WHERE VO_BDATE  BETWEEN TO_DATE(<START_DATE>,'YYYY/MM/DD') AND TO_DATE(<END_DATE>,'YYYY/MM/DD')  AND DR_NAME=<DR_NAME>

		     
deletedata.Debug=N

//新增
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO INS_BLACK_LIST &
			   (SEQ_NO,DR_NAME,BLIST_NO,VO_BDATE,VO_SDATE,ST_BDATE,ST_SDATE,ST_DESC,VO_DESC,BLIST_TYPE,&
			   OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<SEQ_NO>,<DR_NAME>,<BLIST_NO>,TO_DATE(<VO_BDATE>,'YYYY-MM-DD'),TO_DATE(<VO_SDATE>,'YYYY-MM-DD'),TO_DATE(<ST_BDATE>,'YYYY-MM-DD'),TO_DATE(<ST_SDATE>,'YYYY-MM-DD'),<ST_DESC>,&
		          <VO_DESC>,<BLIST_TYPE>, <OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N


