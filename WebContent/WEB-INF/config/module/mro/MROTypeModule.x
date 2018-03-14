########################################
#  Title:病案项目类型module
# 
#  Description:病案项目类型module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.4.29
#  version 4.0
########################################
Module.item=selectdata;insertdata;updatedata;deletedata

//查询数据
selectdata.Type=TSQL
selectdata.SQL=SELECT  TYPE_CODE, TYPE_DESC, PY1, &
		PY2, DESCRIPTION, OPT_USER, &
		OPT_DATE, OPT_TERM &
		FROM MRO_TYPE &
		WHERE TYPE_CODE LIKE <TYPE_CODE> &
		AND TYPE_DESC LIKE <TYPE_DESC>
selectdata.Debug=N

//插入数据
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_TYPE (TYPE_CODE, TYPE_DESC, PY1, &
                      PY2, DESCRIPTION, OPT_USER, &
                      OPT_DATE, OPT_TERM &
                     ) &
              VALUES (<TYPE_CODE>, <TYPE_DESC>, <PY1>, & 
                      <PY2>, <DESCRIPTION>, <OPT_USER>, &
                      SYSDATE, <OPT_TERM> &
                     )
insertdata.Debug=N

//更新数据
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_TYPE SET TYPE_DESC=<TYPE_DESC>,&
				   PY1=<PY1>,&
				   PY2=<PY2>,&
				   DESCRIPTION=<DESCRIPTION>,&
				   OPT_USER=<OPT_USER>,&
				   OPT_DATE=SYSDATE,&
				   OPT_TERM=<OPT_TERM> &
			   WHERE   TYPE_CODE = <TYPE_CODE>
updatedata.Debug=N

//删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_TYPE WHERE TYPE_CODE=<TYPE_CODE>
deletedata.Debug=N