# 
#  Title: 医疗统计系统 中间对照科室 combo
# 
#  Description:  医疗统计系统 中间对照科室 combo
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.18
#  version 1.0
#
Module.item=initData

//初始化
initData.Type=TSQL
initData.SQL=SELECT DEPT_CODE AS ID ,DEPT_DESC AS NAME ,ENNAME,OE_DEPT_CODE AS PY1,IPD_DEPT_CODE AS PY2 &
		FROM STA_OEI_DEPT_LIST &
		ORDER BY DEPT_CODE
initData.item=DEPT_LEVEL
initData.DEPT_LEVEL=DEPT_LEVEL=<DEPT_LEVEL>
initData.Debug=N