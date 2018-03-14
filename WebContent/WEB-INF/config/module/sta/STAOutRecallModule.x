# 
#  Title:出院召回查询表module
# 
#  Description:出院召回查询表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wukai 2009.04.24
#  version 1.0
#

Module.item=selectData;insertData

//查询相关数据
selectData.Type=TSQL
selectData.SQL=SELECT &
		MR_NO,PAT_NAME,LAST_DS_DATE,DS_DEPT_CODE,DS_STATION_CODE,REFUND_DATE,REFUND_CODE  &
		FROM STA_OUT_RECALL  &
		WHERE 1=1    ORDER BY DS_DEPT_CODE,REFUND_DATE DESC
selectData.item=REFUND_DATE1;REFUND_DATE2
selectData.REFUND_DATE1= REFUND_DATE >= <START_DATE>
selectData.REFUND_DATE2= REFUND_DATE <= <END_DATE>
selectData.Debug=N

//插入一条数据
insertData.Type=TSQL
insertData.SQL=INSERT INTO STA_OUT_RECALL (MR_NO,CASE_NO,PAT_NAME,LAST_DS_DATE,DS_DEPT_CODE,DS_STATION_CODE,REFUND_DATE,REFUND_CODE,REGION_CODE,RECALL_TYPE) &
		VALUES (<MR_NO>,<CASE_NO>,<PAT_NAME>,<LAST_DS_DATE>,<DS_DEPT_CODE>,<DS_STATION_CODE>, &
		<REFUND_DATE>,<REFUND_CODE>,<REGION_CODE>,<RECALL_TYPE>)
insertData.Debug=N	    