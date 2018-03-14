# 
#  Title:账务参数档module
# 
#  Description:账务参数档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.06.23
#  version 1.0
#
Module.item=getDayCycle

//查询结账时间
getDayCycle.Type=TSQL
getDayCycle.SQL=SELECT MONTH_CYCLE,DAY_CYCLE &
		     FROM BIL_SYSPARM 
getDayCycle.item=ADM_TYPE
getDayCycle.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
getDayCycle.Debug=N
