# 
#  Title:调价库存损益表
# 
#  Description:调价库存损益表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert

//新增批次参数程序
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_INDRPPANDL( &
		READJUST_DATE, ORG_CODE, ORDER_CODE, BATCH_SEQ, UNIT_CODE, &
		STOCK_QTY, OWN_PRICE_OLD, OWN_PRICE_NEW, OPT_USER, OPT_DATE, &
		OPT_TERM) &
           VALUES( &
           	<READJUST_DATE>, <ORG_CODE>, <ORDER_CODE>, <BATCH_SEQ>, <UNIT_CODE>, &
		<STOCK_QTY>, <OWN_PRICE_OLD>, <OWN_PRICE_NEW>, <OPT_USER>, <OPT_DATE>, &
		<OPT_TERM>)
insert.Debug=N

