# 
#  Title:评估方案module
# 
#  Description:评估方案module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.10.26
#  version 1.0
#
Module.item=initCombo

//根据科室，发生日期查询对应的差错与事故
initCombo.Type=TSQL
initCombo.SQL=SELECT PLAN_CODE AS ID,PLAN_DESC AS NAME,ENNAME,PY1,PY2 &
		FROM DSS_EVAL_PLANM &
	       ORDER BY SEQ,PLAN_CODE 		  
initCombo.Debug=N

