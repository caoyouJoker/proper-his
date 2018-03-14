# 
#  Title:传染病类别module
# 
#  Description:传染病类别module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.06.15
#  version 1.0
#
Module.item=queryDiseaseTpye;initdiseasetypecode

//查询传染病类别是否存在
queryDiseaseTpye.Type=TSQL
queryDiseaseTpye.SQL=SELECT DISEASETYPE_CODE, DISEASETYPE_DESC, PY1, PY2, SEQ, DESCRIPTION, &
			    DEADLINE, DEADLINE_DESC, LEGALINFECT_FLG, OPT_USER, OPT_DATE, OPT_TERM &
	               FROM SYS_DISEASETYPE &
	               WHERE DISEASETYPE_CODE=<DISEASETYPE_CODE>
queryDiseaseTpye.Debug=N

//初始化报告类别
initdiseasetypecode.Type=TSQL
initdiseasetypecode.SQL=SELECT DISEASETYPE_CODE AS ID, DISEASETYPE_DESC AS NAME,ENG_DESC AS ENNAME FROM SYS_DISEASETYPE  ORDER BY SEQ
initdiseasetypecode.Debug=N


