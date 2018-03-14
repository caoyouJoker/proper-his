# 
#  Title:KPI指标module
# 
#  Description:KPI指标module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.26
#  version 1.0
#
Module.item=initCombo

//根据科室，发生日期查询对应的差错与事故
initCombo.Type=TSQL
initCombo.SQL=SELECT KPI_CODE AS ID,KPI_DESC AS NAME,ENNAME,PY1,PY2 &
		FROM DSS_KPI &
	       ORDER BY SEQ,KPI_CODE 		  
initCombo.Debug=N

