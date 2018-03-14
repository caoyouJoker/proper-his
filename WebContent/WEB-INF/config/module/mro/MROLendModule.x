###############################################
#  Title:病案借阅原因字典module
# 
#  Description:病案借阅原因字典module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.5.6
#  version 4.0
###############################################
Module.item=selectdata;insertdata;updatedata;deletedata

//查询数据
selectdata.Type=TSQL
selectdata.SQL=SELECT &
		   LEND_CODE, LEND_DESC, PY1, &
		   PY2, DESCRIPTION, LEND_DAY, &
		   VALID_DAY, OPT_USER, OPT_DATE, &
		   OPT_TERM &
		FROM MRO_LEND
selectdata.item=LEND_CODE;LEND_DESC
selectdata.LEND_CODE=LEND_CODE LIKE <LEND_CODE>
selectdata.LEND_DESC=LEND_DESC LIKE <LEND_DESC>
selectdata.Debug=N

//插入数据
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_LEND ( &
                    LEND_CODE,LEND_DESC,PY1,PY2,DESCRIPTION, &
                    LEND_DAY,VALID_DAY,OPT_USER,OPT_DATE,OPT_TERM &
             ) &
             VALUES ( &
                    <LEND_CODE>,<LEND_DESC>,<PY1>,<PY2>,<DESCRIPTION>, &
                    <LEND_DAY>,<VALID_DAY>,<OPT_USER>,SYSDATE,<OPT_TERM> &
             )
insertdata.Debug=N

//更新数据
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_LEND SET LEND_DESC=<LEND_DESC>, &
				   PY1=<PY1>, &
				   PY2=<PY2>, &
				   DESCRIPTION=<DESCRIPTION>, &
				   LEND_DAY=<LEND_DAY>, &
				   VALID_DAY=<VALID_DAY>, &
				   OPT_USER=<OPT_USER>, &
				   OPT_DATE=SYSDATE, &
				   OPT_TERM=<OPT_TERM> &
			   WHERE   LEND_CODE = <LEND_CODE>
updatedata.Debug=N

//删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_LEND WHERE LEND_CODE=<LEND_CODE>
deletedata.Debug=N