# 
#  Title:批次程序
# 
#  Description:批次程序
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update;delete

//新增批次程序
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_PATCH( &
		PATCH_CODE, PATCH_DESC, PATCH_SRC,PATCH_TYPE, PATCH_DATE, &
		PATCH_REOMIT_COUNT,PATCH_REOMIT_INTERVAL, PATCH_REOMIT_POINT, STATUS, END_DATE, &
		OPT_USER, OPT_DATE, OPT_TERM ) &
           VALUES( &
           	<PATCH_CODE>, <PATCH_DESC>, <PATCH_SRC>,<PATCH_TYPE>, <PATCH_DATE>, &
	   	<PATCH_REOMIT_COUNT>,<PATCH_REOMIT_INTERVAL>, <PATCH_REOMIT_POINT>, <STATUS>, <END_DATE>, &
		<OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insert.Debug=N

//更新批次程序
update.Type=TSQL
update.SQL=UPDATE SYS_PATCH SET &
		PATCH_DESC = <PATCH_DESC> , &
		PATCH_SRC = <PATCH_SRC> , &
		PATCH_TYPE = <PATCH_TYPE> , &
		PATCH_DATE = <PATCH_DATE> , &
		PATCH_REOMIT_COUNT = <PATCH_REOMIT_COUNT> , &
		PATCH_REOMIT_INTERVAL = <PATCH_REOMIT_INTERVAL> , &
		PATCH_REOMIT_POINT = <PATCH_REOMIT_POINT> , &
		STATUS = <STATUS> , &
		END_DATE = <END_DATE> , &
		OPT_USER = <OPT_USER> , &
		OPT_DATE = <OPT_DATE> , &
		OPT_TERM = <OPT_TERM> &
	    WHERE PATCH_CODE = <PATCH_CODE>
update.Debug=N

//删除批次程序
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_PATCH WHERE PATCH_CODE = <PATCH_CODE>
delete.Debug=N