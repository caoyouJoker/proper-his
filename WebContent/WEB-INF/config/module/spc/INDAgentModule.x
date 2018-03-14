  #
   # Title:药库供应厂商
   #
   # Description:药库供应厂商
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/27
Module.item=query;insert;update;delete;queryUnit;updateContractPrice

//根据条件查询供应厂商的供应药品
query.Type=TSQL
query.SQL=SELECT A.SUP_CODE, A.ORDER_CODE, B.ORDER_DESC, A.MAIN_FLG, A.CONTRACT_NO, &
       		 A.CONTRACT_PRICE, A.LAST_ORDER_DATE, A.LAST_ORDER_QTY, A.LAST_ORDER_PRICE , A.LAST_ORDER_NO, &
       		 A.LAST_VERIFY_DATE, A.LAST_VERIFY_PRICE, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, &
       		 C.UNIT_CHN_DESC &
       	  FROM IND_AGENT A, PHA_BASE B, SYS_UNIT C &
       	  WHERE A.ORDER_CODE = B.ORDER_CODE &
       	    AND B.PURCH_UNIT = C.UNIT_CODE 
query.ITEM=SUP_CODE;ORDER_CODE
query.SUP_CODE=A.SUP_CODE=<SUP_CODE>
query.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
query.Debug=N

//添加供应厂商的供应药品
insert.Type=TSQL
insert.SQL = INSERT INTO IND_AGENT( &
		SUP_CODE,ORDER_CODE,MAIN_FLG,CONTRACT_NO,CONTRACT_PRICE,LAST_VERIFY_PRICE, &
	     	OPT_USER,OPT_DATE,OPT_TERM) &
	     VALUES(<SUP_CODE>,<ORDER_CODE>,<MAIN_FLG>,<CONTRACT_NO>,<CONTRACT_PRICE>,<LAST_VERIFY_PRICE>,&
	     	<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insert.Debug=N

//更新供应厂商的供应药品
update.Type=TSQL
update.SQL = UPDATE IND_AGENT SET &
		SUP_CODE=<SUP_CODE> , ORDER_CODE=<ORDER_CODE> , MAIN_FLG=<MAIN_FLG> , CONTRACT_NO=<CONTRACT_NO> , CONTRACT_PRICE=<CONTRACT_PRICE> ,LAST_VERIFY_PRICE=<LAST_VERIFY_PRICE>, & 
		OPT_USER=<OPT_USER> , OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	     WHERE SUP_CODE=<SUP_CODE> AND ORDER_CODE=<ORDER_CODE>
update.Debug=N

//删除供应厂商的供应药品
delete.Type=TSQL
delete.SQL=DELETE FROM IND_AGENT WHERE SUP_CODE=<SUP_CODE> AND ORDER_CODE=<ORDER_CODE>
delete.Debug=N

//根据药品代码查询其购入单位
queryUnit.Type=TSQL
queryUnit.SQL=SELECT A.UNIT_CHN_DESC &
	      FROM SYS_UNIT A , PHA_BASE B &
	      WHERE A.UNIT_CODE=B.PURCH_UNIT &
  		AND B.ORDER_CODE=<ORDER_CODE>
queryUnit.Debug=N

//更新合约单价
updateContractPrice.Type=TSQL
updateContractPrice.SQL=UPDATE IND_AGENT SET &
				CONTRACT_PRICE=<CONTRACT_PRICE> &
	     		  WHERE SUP_CODE=<SUP_CODE> AND ORDER_CODE=<ORDER_CODE>
updateContractPrice.Debug=N

