# 
#  Title:住院费用结算module
# 
#  Description:住院费用结算module
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2012-2-1
#  version 1.0
#

Module.item=insertINSIpdHistroy;selectINSIpdHistory


//添加数据 
insertINSIpdHistroy.Type=TSQL
insertINSIpdHistroy.SQL=INSERT INTO INS_IPD_HISTORY(ORDER_CODE,START_DAY,END_DAY,INS_CODE) VALUES(<ORDER_CODE>,<START_DAY>,<END_DAY>,<INS_CODE>)
insertINSIpdHistroy.Debug=N

//查询数据
selectINSIpdHistory.Type=TSQL
selectINSIpdHistory.SQL=SELECT ORDER_CODE,START_DAY,END_DAY,INS_CODE,TJDM,ZFBL1,PZWH FROM INS_IPD_HISTORY WHERE ORDER_CODE=<ORDER_CODE> ORDER BY START_DAY DESC
selectINSIpdHistory.Debug=N