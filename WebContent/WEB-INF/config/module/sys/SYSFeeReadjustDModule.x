# 
#  Title:调价计划细项
# 
#  Description:调价计划细项
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update;delete;query;deleteAll

//新增调价计划细项
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_FEE_READJUSTD( &
		RPP_CODE, SEQ_NO, ORDER_CODE, START_DATE, END_DATE, &
		OWN_PRICE, OWN_PRICE2, OWN_PRICE3, &
		OPT_USER, OPT_DATE, OPT_TERM ) &
           VALUES( &
           	<RPP_CODE>, <SEQ_NO>, <ORDER_CODE>,<START_DATE>, <END_DATE>, &
	   	<OWN_PRICE>, <OWN_PRICE2>, <OWN_PRICE3>, &
		<OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insert.Debug=N


//删除调价计划细项
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_FEE_READJUSTD WHERE RPP_CODE = <RPP_CODE> AND SEQ_NO = <SEQ_NO>
delete.Debug=N

//删除调价计划全部细项
deleteAll.Type=TSQL
deleteAll.SQL=DELETE FROM SYS_FEE_READJUSTD WHERE RPP_CODE = <RPP_CODE>
deleteAll.Debug=N


//查询调价计划细项
query.Type=TSQL
query.SQL=SELECT A.ORDER_CODE, B.ORDER_DESC, A.START_DATE, A.END_DATE, &
	         A.OWN_PRICE AS OWN_PRICE_NEW, A.OWN_PRICE2 AS OWN_PRICE2_NEW, &
	         A.OWN_PRICE3 AS OWN_PRICE3_NEW, B.OWN_PRICE AS OWN_PRICE_OLD, &
	         B.OWN_PRICE2 AS OWN_PRICE2_OLD, B.OWN_PRICE3 AS OWN_PRICE3_OLD, A.SEQ_NO &
          FROM SYS_FEE_READJUSTD A, SYS_FEE B &
          WHERE A.ORDER_CODE = B.ORDER_CODE &
                AND A.RPP_CODE = <RPP_CODE>
query.Debug=N


//更新调价计划细项
update.Type=TSQL
update.SQL=UPDATE SYS_FEE_READJUSTD SET &
		START_DATE = <START_DATE> &
		WHERE RPP_CODE = <RPP_CODE>
update.Debug=N