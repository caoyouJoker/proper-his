# 
#  Title:�Һ��վ�module
# 
#  Description:�Һ��վ�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author 
#  version 1.0
#
Module.item=insertBill;selCaseCountForREG;updateRecpForUnReg;insertDataForUnReg;selDataForUnReg;updateAccount;selDateForAccount;selPayTypeFee;&
	    selForRePrint;upRecpForRePrint;getRegResetCount

//����Һ��վݵ�
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

//��ѯ�Ƿ�����Һ��վ�
selCaseCountForREG.Type=TSQL
selCaseCountForREG.SQL=SELECT COUNT(CASE_NO) &
			 FROM BIL_REG_RECP &
			WHERE CASE_NO = <CASE_NO> 
selCaseCountForREG.Debug=N

//�����˹��վ�
updateRecpForUnReg.Type=TSQL
updateRecpForUnReg.SQL=UPDATE BIL_REG_RECP &
			  SET RESET_RECEIPT_NO=<RESET_RECEIPT_NO>  &
			WHERE CASE_NO=<CASE_NO> &
			  AND RECEIPT_NO = <RECEIPT_NO>
updateRecpForUnReg.Debug=N

//�˹�д��һ�ʸ�������(FOR REG)
insertDataForUnReg.Type=TSQL
insertDataForUnReg.SQL=INSERT INTO BIL_REG_RECP &
				   (CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
				   RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,&
				   REG_FEE,REG_FEE_REAL,CLINIC_FEE,CLINIC_FEE_REAL,SPC_FEE,&
				   OTHER_FEE1,OTHER_FEE2,OTHER_FEE3,AR_AMT,PAY_CASH,&
				   PAY_BANK_CARD,PAY_CHECK,PAY_MEDICAL_CARD,PAY_INS_CARD,PAY_DEBIT,&
				   PAY_INS,REMARK,CASH_CODE,ACCOUNT_FLG,ACCOUNT_SEQ,&
				   ACCOUNT_USER,ACCOUNT_DATE,BANK_SEQ,BANK_DATE,BANK_USER,&
				   OPT_USER,OPT_DATE,OPT_TERM,ALIPAY,QE_PAY_TYPE) &
			    VALUES (<CASE_NO>,<RECEIPT_NO>,<ADM_TYPE>,<REGION_CODE>,<MR_NO>,&
			    	   <RESET_RECEIPT_NO>,<PRINT_NO>,<BILL_DATE>,<CHARGE_DATE>,<PRINT_DATE>,&
			    	   <REG_FEE>,<REG_FEE_REAL>,<CLINIC_FEE>,<CLINIC_FEE_REAL>,<SPC_FEE>,&
			    	   <OTHER_FEE1>,<OTHER_FEE2>,<OTHER_FEE3>,<AR_AMT>,<PAY_CASH>,&
			    	   <PAY_BANK_CARD>,<PAY_CHECK>,<PAY_MEDICAL_CARD>,<PAY_INS_CARD>,<PAY_DEBIT>,&
			    	   <PAY_INS>,<REMARK>,<CASH_CODE>,<ACCOUNT_FLG>,<ACCOUNT_SEQ>,&
			    	   <ACCOUNT_USER>,<ACCOUNT_DATE>,<BANK_SEQ>,<BANK_DATE>,<BANK_USER>,&
			    	   <OPT_USER>,SYSDATE,<OPT_TERM>,<ALIPAY>,<QE_PAY_TYPE>)
insertDataForUnReg.Debug=N

//�˹Ҳ�ѯ�վ�����
selDataForUnReg.Type=TSQL
selDataForUnReg.SQL=SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
			   RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,&
			   REG_FEE,REG_FEE_REAL,CLINIC_FEE,CLINIC_FEE_REAL,SPC_FEE,&
			   OTHER_FEE1,OTHER_FEE2,OTHER_FEE3,AR_AMT,PAY_CASH,&
			   PAY_BANK_CARD,PAY_CHECK,PAY_MEDICAL_CARD,PAY_INS_CARD,PAY_DEBIT,&
			   PAY_INS,REMARK,CASH_CODE,ACCOUNT_FLG,ACCOUNT_SEQ,&
			   ACCOUNT_USER,ACCOUNT_DATE,BANK_SEQ,BANK_DATE,BANK_USER,&
			   OPT_USER,OPT_DATE,OPT_TERM,ALIPAY,QE_PAY_TYPE &
		      FROM BIL_REG_RECP &
		     WHERE CASE_NO = <CASE_NO>
selDataForUnReg.Debug=N

//�����ս���,�ս��,�ս���Ա,�ս�����     AND PRINT_NO IS NOT NULL add by huangtt 20160516 δ��Ʊ�������ս�
updateAccount.Type=TSQL
updateAccount.SQL=UPDATE BIL_REG_RECP &
		     SET ACCOUNT_FLG = 'Y',ACCOUNT_SEQ = <ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_DATE=<ACCOUNT_DATE> &
		   WHERE CASH_CODE = <CASH_CODE> &
		   AND ADM_TYPE = <ADM_TYPE> &
		     AND ACCOUNT_FLG IS NULL &
		     AND PRINT_NO IS NOT NULL &
		     AND BILL_DATE < TO_DATE (<BILL_DATE>, 'YYYYMMDDHH24MISS')
updateAccount.Debug=N

//��ѯ���ս�����
selDateForAccount.Type=TSQL
selDateForAccount.SQL=SELECT CASH_CODE, SUM (REG_FEE) AS REG_FEE, SUM (CLINIC_FEE) AS CLINIC_FEE,SUM (AR_AMT) AS AR_AMT &
			FROM BIL_REG_RECP &
		       WHERE ACCOUNT_FLG IS NULL &
		         AND CASH_CODE = <CASH_CODE> &
		         AND BILL_DATE < TO_DATE(<BILL_DATE>,'yyyyMMddHH24miss') &
		         AND ADM_TYPE = <ADM_TYPE> &
		         AND PRINT_NO IS NOT NULL &
		    GROUP BY CASH_CODE
//===========pangben modify 20110407 start
selDateForAccount.item=REGION_CODE
selDateForAccount.REGION_CODE=REGION_CODE=<REGION_CODE>
//===========pangben modify 20110407 stop
selDateForAccount.Debug=N


//��ѯ��֧ͬ����ʽ������(�ս�)
selPayTypeFee.Type=TSQL
selPayTypeFee.SQL=SELECT SUM (PAY_CASH) PAY_CASH, SUM (PAY_BANK_CARD) PAY_BANK_CARD,&
			 SUM (PAY_CHECK) PAY_CHECK, SUM (PAY_MEDICAL_CARD) PAY_MEDICAL_CARD,&
		         SUM (PAY_INS_CARD) PAY_INS_CARD, SUM (PAY_DEBIT) PAY_DEBIT,&
		         SUM (PAY_INS) PAY_INS &
		    FROM BIL_REG_RECP &
		   WHERE ACCOUNT_SEQ IN (<ACCOUNT_SEQ>) &
		     AND RESET_RECEIPT_NO = RECEIPT_NO
selPayTypeFee.Debug=N

//��ӡ��ѯȫ�ֶ�
selForRePrint.Type=TSQL
selForRePrint.SQL=SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
			 RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,&
			 REG_FEE,REG_FEE_REAL,CLINIC_FEE,CLINIC_FEE_REAL,SPC_FEE,&
			 OTHER_FEE1,OTHER_FEE2,OTHER_FEE3,AR_AMT,PAY_CASH,&
			 PAY_BANK_CARD,PAY_CHECK,PAY_MEDICAL_CARD,PAY_INS_CARD,PAY_DEBIT,&
			 PAY_INS,REMARK,CASH_CODE,ACCOUNT_FLG,ACCOUNT_SEQ,&
			 ACCOUNT_USER,ACCOUNT_DATE,BANK_SEQ,BANK_DATE,BANK_USER,&
			 OPT_USER,OPT_DATE,OPT_TERM &
		    FROM BIL_REG_RECP &
		   WHERE CASE_NO = <CASE_NO> &
		     AND RESET_RECEIPT_NO IS NULL &
		     AND AR_AMT >= 0
selForRePrint.Debug=N

//���²�ӡ�վ�(FOR REG)
upRecpForRePrint.Type=TSQL
upRecpForRePrint.SQL=UPDATE BIL_REG_RECP &
			SET PRINT_NO=<PRINT_NO>,PRINT_DATE=SYSDATE,&
			    OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		      WHERE CASE_NO=<CASE_NO> &
			AND RECEIPT_NO = <RECEIPT_NO>
upRecpForRePrint.Debug=N

//�õ��ս���Ա��������
getRegResetCount.Type=TSQL
getRegResetCount.SQL=SELECT COUNT(PRINT_NO) AS COUNT FROM BIL_REG_RECP &
		     WHERE CASH_CODE=<CASH_CODE> &
                           AND ADM_TYPE = <ADM_TYPE> &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                           AND AR_AMT < 0
getRegResetCount.Debug=N