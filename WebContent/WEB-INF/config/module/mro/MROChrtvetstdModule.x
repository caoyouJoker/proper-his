###############################################
#  Title:病案审核标准module
# 
#  Description:病案审核标准module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.4.29
#  version 1.0
###############################################
Module.item=selectdata;insertdata;updatedata;deletedata

//查询数据
selectdata.Type=TSQL
selectdata.SQL=SELECT &
		   EXAMINE_CODE, TYPE_CODE, EXAMINE_DESC, PY1, PY2, &
		   DESCRIPTION, SCORE, URG_FLG, SPCFY_DEPT,  &
		   CHECK_FLG, CHECK_RANGE, METHOD_CODE, METHOD_PARM, CHECK_SQL, &
		   OPT_USER, OPT_DATE, OPT_TERM,CHECK_RANGE1 &
		FROM MRO_CHRTVETSTD ORDER BY EXAMINE_CODE
selectdata.ITEM=EXAMINE_CODE;METHOD_CODE;CHECK_RANGE
selectdata.EXAMINE_CODE=EXAMINE_CODE LIKE <EXAMINE_CODE>
selectdata.METHOD_CODE=METHOD_CODE=<METHOD_CODE>
selectdata.CHECK_RANGE=CHECK_RANGE=<CHECK_RANGE>
selectdata.Debug=N

//插入数据
//==============pangben modify 20110726 添加门急诊/住院单选 
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_CHRTVETSTD( &
                       EXAMINE_CODE, TYPE_CODE, EXAMINE_DESC,  &
                       PY1, PY2, DESCRIPTION, &
                       SCORE, URG_FLG, SPCFY_DEPT, &
                       CHECK_FLG, CHECK_RANGE, METHOD_CODE, METHOD_PARM, CHECK_SQL, &
                       OPT_USER, OPT_DATE, OPT_TERM,CHECK_RANGE1 &
                       ) &
                VALUES ( &
                        <EXAMINE_CODE>, <TYPE_CODE>, <EXAMINE_DESC>, &
                        <PY1>, <PY2>, <DESCRIPTION>, &
                        <SCORE>, <URG_FLG>, <SPCFY_DEPT>, &
                        <CHECK_FLG>, <CHECK_RANGE>, <METHOD_CODE>, <METHOD_PARM>, <CHECK_SQL>, &
                        <OPT_USER>, SYSDATE, <OPT_TERM>,<CHECK_RANGE1> &
                        )
insertdata.Debug=N

//更新数据
//==============pangben modify 20110726 添加门急诊/住院单选 
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_CHRTVETSTD SET TYPE_CODE=<TYPE_CODE>, &
				   EXAMINE_DESC=<EXAMINE_DESC>, &
				   PY1=<PY1>, &
				   PY2=<PY2>, &
				   DESCRIPTION=<DESCRIPTION>, &
				   SCORE=<SCORE>, &
				   URG_FLG=<URG_FLG>, &
				   SPCFY_DEPT=<SPCFY_DEPT>, &
				   CHECK_FLG=<CHECK_FLG>, &
				   CHECK_RANGE=<CHECK_RANGE>, &
				   CHECK_RANGE1=<CHECK_RANGE1>, &
				   METHOD_CODE=<METHOD_CODE>, &
				   METHOD_PARM=<METHOD_PARM>, &
				   CHECK_SQL=<CHECK_SQL>, &
				   OPT_USER=<OPT_USER>, &
				   OPT_DATE=SYSDATE, &
				   OPT_TERM=<OPT_TERM> &
			   WHERE   EXAMINE_CODE = <EXAMINE_CODE>
updatedata.Debug=N

//删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_CHRTVETSTD WHERE EXAMINE_CODE=<EXAMINE_CODE>
deletedata.Debug=N