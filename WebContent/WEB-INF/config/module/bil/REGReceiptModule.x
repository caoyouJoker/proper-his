# 
#  Title:挂号收据module
# 
#  Description:挂号收据module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author 
#  version 1.0
#
Module.item=insertBill;selCaseCountForREG;updateRecpForUnReg;insertDataForUnReg

//结账插入Account
insertBill.Type=TSQL
insertBill.SQL=INSERT INTO BIL_REG_RECP &
			   (CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
			   RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,&
			   REG_FEE,REG_FEE_REAL,CLINIC_FEE,CLINIC_FEE_REAL,SPC_FEE,&
			   OTHER_FEE1,OTHER_FEE2,OTHER_FEE3,AR_AMT,PAY_CASH,&
			   PAY_BANK_CARD,PAY_CHECK,PAY_MEDICAL_CARD,PAY_INS_CARD,PAY_DEBIT,&
			   PAY_INS,REMARK,CASH_CODE,ACCOUNT_FLG,ACCOUNT_SEQ,&
			   ACCOUNT_USER,ACCOUNT_DATE,BANK_SEQ,BANK_DATE,BANK_USER,&
			   OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<CASE_NO>,<RECEIPT_NO>,<ADM_TYPE>,<REGION_CODE>,<MR_NO>,&
		    	   <RESET_RECEIPT_NO>,<PRINT_NO>,<BILL_DATE>,<CHARGE_DATE>,<PRINT_DATE>,&
		    	   <REG_FEE>,<REG_FEE_REAL>,<CLINIC_FEE>,<CLINIC_FEE_REAL>,<SPC_FEE>,&
		    	   <OTHER_FEE1>,<OTHER_FEE2>,<OTHER_FEE3>,<AR_AMT>,<PAY_CASH>,&
		    	   <PAY_BANK_CARD>,<PAY_CHECK>,<PAY_MEDICAL_CARD>,<PAY_INS_CARD>,<PAY_DEBIT>,&
		    	   <PAY_INS>,<REMARK>,<CASH_CODE>,<ACCOUNT_FLG>,<ACCOUNT_SEQ>,&
		    	   <ACCOUNT_USER>,<ACCOUNT_DATE>,<BANK_SEQ>,<BANK_DATE>,<BANK_USER>,&
		    	   <OPT_USER>,SYSDATE,<OPT_TERM>) 
insertBill.Debug=N

//查询是否产生挂号收据(FOR REG)
selCaseCountForREG.Type=TSQL
selCaseCountForREG.SQL=SELECT COUNT(CASE_NO) &
			 FROM BIL_REG_RECP &
			WHERE CASE_NO = <CASE_NO> 
selCaseCountForREG.Debug=N

//更新退挂收据(FOR REG)
updateRecpForUnReg.Type=TSQL
updateRecpForUnReg.SQL=UPDATE BIL_REG_RECP &
			SET RESET_RECEIPT_NO=<RESET_RECEIPT_NO>  &
		      WHERE CASE_NO=<CASE_NO>
updateRecpForUnReg.Debug=N

//退挂写入一笔负数资料(FOR REG)
insertDataForUnReg.Type=TSQL
insertDataForUnReg.SQL=INSERT INTO BIL_REG_RECP () &
	       VALUES ()
insertDataForUnReg.Debug=N


