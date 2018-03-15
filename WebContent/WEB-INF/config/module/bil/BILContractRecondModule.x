# 
#  Title:���˵�λ����module
# 
#  Description:���˵�λ����module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author pangben 20110817
#  version 1.0
#
Module.item=insertRecode;selRecode;updateRecode;selRegRecode

//������˵�λ
insertRecode.Type=TSQL
insertRecode.SQL=INSERT INTO BIL_CONTRACT_RECODE &
			   (RECEIPT_NO,CONTRACT_CODE,ADM_TYPE,REGION_CODE,CASHIER_CODE,&
			   CHARGE_DATE,RECEIPT_TYPE,DATA_TYPE,CASE_NO,MR_NO,&
			   AR_AMT,BIL_STATUS,OPT_USER,OPT_DATE,OPT_TERM,RECEIPT_FLG) &
		    VALUES (<RECEIPT_NO>,<CONTRACT_CODE>,<ADM_TYPE>,<REGION_CODE>,<CASHIER_CODE>,&
		    	   SYSDATE,<RECEIPT_TYPE>,<DATA_TYPE>,<CASE_NO>,<MR_NO>,&
		    	   <AR_AMT>,<BIL_STATUS>,&
		    	   <OPT_USER>,SYSDATE,<OPT_TERM>,<RECEIPT_FLG>) 
insertRecode.Debug=N

//��ѯ��ʾ���ܵ�����
selRecode1.Type=TSQL
selRecode1.SQL=SELECT 'N' as FLG ,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,A.CHARGE_DATE,A.RECEIPT_TYPE,A.MR_NO,A.AR_AMT,A.BIL_STATUS &
			 FROM BIL_CONTRACT_RECODE A,BIL_REG_RECP B,OPD_ORDER C &
			WHERE A.CASE_NO=B.CASE_NO(+) AND A.CASE_NO=C.CASE_NO(+) AND (B.PRINT_NO='' OR B.PRINT_NO IS NULL) AND C.PRINT_FLG='Y' &
			AND (C.RECEIPT_NO IS NULL OR C.RECEIPT_NO='')  AND A.CHARGE_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS')
selRecode1.item=REGION_CODE;CONTRACT_CODE
selRecode1.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selRecode1.CONTRACT_CODE=A.CONTRACT_CODE=<CONTRACT_CODE>
selRecode1.Debug=N

//��ѯ��ʾ���ܵ�����
selRecode.Type=TSQL
selRecode.SQL=SELECT 'N' as FLG,A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,A.CHARGE_DATE,A.RECEIPT_TYPE,&
			A.MR_NO,A.AR_AMT,A.BIL_STATUS,A.CASHIER_CODE,B.PAT_NAME,C.USER_NAME &
			 FROM BIL_CONTRACT_RECODE A,SYS_PATINFO B,SYS_OPERATOR C &
			WHERE A.MR_NO=B.MR_NO(+) AND A.CHARGE_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
			AND A.BIL_STATUS<>'3' AND A.CASHIER_CODE=C.USER_ID ORDER BY A.CHARGE_DATE
selRecode.item=REGION_CODE;CONTRACT_CODE;BIL_STATUS
selRecode.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selRecode.CONTRACT_CODE=A.CONTRACT_CODE=<CONTRACT_CODE>
selRecode.BIL_STATUS=A.BIL_STATUS=<BIL_STATUS>
selRecode.Debug=N

//�޸�״̬��������ɡ����ˡ��˷�
updateRecode.Type=TSQL
updateRecode.SQL=UPDATE BIL_CONTRACT_RECODE SET OPT_DATE=SYSDATE, OPT_USER=<OPT_USER> ,OPT_TERM=<OPT_TERM>,BIL_STATUS=<BIL_STATUS>,RECEIPT_FLG=<RECEIPT_FLG> &
                 WHERE RECEIPT_NO=<RECEIPT_NO> AND RECEIPT_TYPE=<RECEIPT_TYPE>
updateRecode.Debug=N

//��ѯ�˹Ҳ���ʹ��
selRegRecode.Type=TSQL
selRegRecode.SQL=SELECT A.RECEIPT_NO,A.CASE_NO,A.CONTRACT_CODE,A.REGION_CODE,A.CHARGE_DATE,A.RECEIPT_TYPE,&
			A.MR_NO,A.AR_AMT,A.BIL_STATUS,A.CASHIER_CODE &
	     		 FROM BIL_CONTRACT_RECODE A
selRegRecode.item=REGION_CODE;BIL_STATUS;CASE_NO;RECEIPT_TYPE;RECEIPT_NO
selRegRecode.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selRegRecode.CASE_NO=A.CASE_NO=<CASE_NO>
selRegRecode.RECEIPT_TYPE=A.RECEIPT_TYPE=<RECEIPT_TYPE>
selRegRecode.RECEIPT_NO=A.RECEIPT_NO=<RECEIPT_NO>
selRegRecode.BIL_STATUS=A.BIL_STATUS=<BIL_STATUS>
selRegRecode.Debug=N

