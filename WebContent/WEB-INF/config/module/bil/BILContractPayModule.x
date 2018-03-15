# 
#  Title:合同单位预交金module
# 
#  Description:合同单位预交金module
# 
#  Copyright: Copyright (c) ProperSoft
# 
#  author caowl 20130114
#  version 1.0
#
Module.item=insertData;selectByReceipt;selectByContractCode;refundData;updOffRecpNo

//根据receipt_no查询
selectByReceipt.Type=TSQL
selectByReceipt.SQL=SELECT RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
		REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO &
	      FROM BIL_CONTRACT_PAY &
	      WHERE RECEIPT_NO = <RECEIPT_NO>
selectByReceipt.Debug=N

//根据合同单位查询
selectByContractCode.Type=TSQL
selectByContractCode.SQL=SELECT RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
		REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO &
	      FROM BIL_CONTRACT_PAY &
	      WHERE CONTRACT_CODE = <CONTRACT_CODE>
selectByContractCode.Debug=N

//删除合同单位数据
deleteData.Type=TSQL
deleteData.SQL=	
deleteData.Debug=N

//查询合同单位数据是否存在
checkDataExist.Type=TSQL
checkDataExist.SQL=
checkDataExist.Debug=N

//合同单位预交金退费
refundData.Type=TSQL
refundData.SQL=UPDATE BIL_CONTRACT_PAY &
		  SET REFUND_FLG = 'Y',REFUND_CODE = <REFUND_CODE> ,REFUND_DATE=<REFUND_DATE>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE RECEIPT_NO = <RECEIPT_NO> 
refundData.Debug=N


//插入合同单位数据
insertData.Type=TSQL
insertData.SQL= INSERT INTO BIL_CONTRACT_PAY (RECEIPT_NO,CONTRACT_CODE,BUSINESS_TYPE,CASHIER_CODE,CHARGE_DATE, &
         		PRE_AMT,REFUND_FLG,REFUND_CODE,REFUND_DATE,OFF_RECP_NO, &
        		 REMARK,OPT_USER,OPT_DATE,OPT_TERM,PAY_TYPE,PRINT_NO)  &
	VALUES(<RECEIPT_NO>,<CONTRACT_CODE>,<BUSINESS_TYPE>,<CASHIER_CODE>,<CHARGE_DATE>, &
        		<PRE_PAY>,<REFUND_FLG>,<REFUND_CODE>,<REFUND_DATE>,<OFF_RECP_NO>, &
        		<REMARK>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<PAY_TYPE>,<PRINT_NO>)
insertData.Debug=N

//合同预交金冲销
updOffRecpNo.Type=TSQL
updOffRecpNo.SQL=UPDATE BIL_CONTRACT_PAY &
		  SET OFF_RECP_NO = <OFF_RECP_NO>, &
		      OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE RECEIPT_NO = <RECEIPT_NO> 
updOffRecpNo.Debug=N

