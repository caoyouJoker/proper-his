# 
#  Title:批次参数程序
# 
#  Description:批次参数程序
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update;delete;queryPatchCode

//新增批次参数程序
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_PATCH_PARM( &
		PATCH_CODE, PATCH_PARM_NAME, PATCH_PARM_VALUE, OPT_USER, OPT_DATE, OPT_TERM ) &
           VALUES( &
           	<PATCH_CODE>, <PATCH_PARM_NAME>, <PATCH_PARM_VALUE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insert.Debug=N

//更新批次参数程序
update.Type=TSQL
update.SQL=UPDATE SYS_PATCH_PARM SET &
		PATCH_PARM_VALUE = <PATCH_PARM_VALUE> , &
		OPT_USER = <OPT_USER> , &
		OPT_DATE = <OPT_DATE> , &
		OPT_TERM = <OPT_TERM> &
	    WHERE PATCH_CODE = <PATCH_CODE> &
	      AND PATCH_PARM_NAME = <PATCH_PARM_NAME>
update.Debug=N

//删除批次参数程序
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_PATCH_PARM WHERE PATCH_CODE = <PATCH_CODE> AND PATCH_PARM_NAME = <PATCH_PARM_NAME>
delete.Debug=N

//查询批次程序号
queryPatchCode.Type=TSQL
queryPatchCode.SQL=SELECT PATCH_CODE FROM SYS_PATCH_PARM WHERE PATCH_PARM_NAME = <PATCH_PARM_NAME> AND PATCH_PARM_VALUE = <PATCH_PARM_VALUE>
queryPatchCode.Debug=N