# 
#  Title:检伤等级module
# 
#  Description:检伤等级module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=initErdLevel

//检伤等级共用combo
initErdLevel.Type=TSQL
initErdLevel.SQL=SELECT LEVEL_CODE AS ID,LEVEL_DESC AS NAME,ENNAME,PY1,PY2 &
		   FROM REG_ERD_LEVEL &
	       ORDER BY LEVEL_CODE,SEQ
initErdLevel.item=TIME_LIMIT
initErdLevel.TIME_LIMIT=TIME_LIMIT=<TIME_LIMIT>
initErdLevel.Debug=N

