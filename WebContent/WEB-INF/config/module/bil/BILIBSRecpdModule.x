# 
#  Title:住院收据明细档module
# 
#  Description:住院收据明细档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectAllData;updataData;insertData;selDateForReturn

//查询所有数据
selectAllData.Type=TSQL
selectAllData.SQL=SELECT RECEIPT_NO,BILL_NO,REXP_CODE,WRT_OFF_AMT,OPT_USER,&
			 OPT_DATE,OPT_TERM,RESET_RECEIPT_NO,REFUND_FLG,REFUND_CODE,&
			 REFUND_DATE &
		    FROM BIL_IBS_RECPD
selectAllData.item=RECEIPT_NO;BILL_NO;REXP_CODE
selectAllData.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selectAllData.BILL_NO=BILL_NO=<BILL_NO>
selectAllData.REXP_CODE=REXP_CODE=<REXP_CODE>
selectAllData.Debug=N


//插入收据主档
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_IBS_RECPD &
		 	   (RECEIPT_NO,BILL_NO,REXP_CODE,WRT_OFF_AMT,OPT_USER,&
		 	   OPT_DATE,OPT_TERM) &
		    VALUES (<RECEIPT_NO>,<BILL_NO>,<REXP_CODE>,<WRT_OFF_AMT>,<OPT_USER>, &
		    	   SYSDATE,<OPT_TERM>)
insertData.Debug=N

//查询召回主表数据
selDateForReturn.Type=TSQL
selDateForReturn.SQL=SELECT RECEIPT_NO, BILL_NO, REXP_CODE, WRT_OFF_AMT &
                            FROM BIL_IBS_RECPD &
                      WHERE REFUND_CODE IS NULL 
selDateForReturn.item=RECEIPT_NO
selDateForReturn.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selDateForReturn.Debug=N

//更新数据
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_IBS_RECPD &
		  SET REFUND_FLG = <REFUND_FLG>,RESET_RECEIPT_NO=<RESET_RECEIPT_NO>,REFUND_CODE=<REFUND_CODE>,REFUND_DATE = SYSDATE &
		WHERE RECEIPT_NO = <RECEIPT_NO> 

updataData.Debug=N