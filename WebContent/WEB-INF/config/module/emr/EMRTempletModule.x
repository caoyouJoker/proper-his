# 
#  Title:结构化病例申请单module
# 
#  Description:结构化病例申请单module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.06.05
#  version 1.0
#
Module.item=initCombo

//结构化病例申请单共用combo
initCombo.Type=TSQL
initCombo.SQL=SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME ,ENNAME AS ENNAME &
		FROM EMR_TEMPLET &
	       ORDER BY SUBCLASS_CODE
initCombo.Debug=N
