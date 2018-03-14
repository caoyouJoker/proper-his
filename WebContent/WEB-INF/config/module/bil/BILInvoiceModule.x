# 
#  Title:Ʊ������module
# 
#  Description:Ʊ������module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectAllData;selData;updataData;insertData;updatainData;checkData;updateDatePrint;selectUpdateNo;selectNowReceipt;upadjustData;checkUpdateNo;insertFeeDate;cancelDate;updateprintNo

//��ѯ �վ����ͣ���ʼƱ�ţ�����Ʊ�ţ�����ʱ�䣬����ʱ�䣬����״̬��������Ա��������Ա����������
selectAllData.Type=TSQL
selectAllData.SQL=SELECT 'N' AS FLG,RECP_TYPE,START_INVNO,END_INVNO,UPDATE_NO, &
                        CASHIER_CODE,START_VALID_DATE,END_VALID_DATE,STATUS, &
			OPT_USER,OPT_DATE,OPT_TERM,'N' AS STATU,TERM_IP &
			FROM BIL_INVOICE &
			ORDER BY STATUS
selectAllData.item=RECP_TYPE;CASHIER_CODE;STATUS;STATU
selectAllData.RECP_TYPE=RECP_TYPE=<RECP_TYPE>
selectAllData.CASHIER_CODE=CASHIER_CODE=<CASHIER_CODE>
selectAllData.STATUS = STATUS IN ('0','1')
//selectAllData.START_VALID_DATE=START_VALID_DATE BETWEEN (<STARTDATE> AND <ENDDATE>)
selectAllData.Debug=N


//��ѯ �վ����ͣ���ʼƱ�ţ�����Ʊ�ţ�����ʱ�䣬����ʱ�䣬����״̬��������Ա��������Ա����������
selData.Type=TSQL
selData.SQL=SELECT 'N' AS FLG,A.RECP_TYPE,A.START_INVNO,A.END_INVNO,A.UPDATE_NO, &
		   A.CASHIER_CODE,A.START_VALID_DATE,A.END_VALID_DATE,A.STATUS, &
		   A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'N' AS STATU,A.TERM_IP &
	      FROM BIL_INVOICE A,SYS_OPERATOR B  &
	     WHERE A.CASHIER_CODE = B.USER_ID &
	  ORDER BY A.STATUS
selData.item=RECP_TYPE;CASHIER_CODE;STATUS;REGION_CODE
selData.RECP_TYPE=A.RECP_TYPE=<RECP_TYPE>
selData.CASHIER_CODE=A.CASHIER_CODE=<CASHIER_CODE>
selData.STATUS = A.STATUS = <STATUS>
selData.REGION_CODE = A.REGION_CODE = <REGION_CODE>
selData.Debug=N


//��Ʊ����������
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_INVOICE (RECP_TYPE,START_INVNO,START_VALID_DATE,END_INVNO,UPDATE_NO, &
                                            CASHIER_CODE,END_VALID_DATE,STATUS,OPT_USER,OPT_DATE,OPT_TERM,TERM_IP,REGION_CODE) &
                                 VALUES(<RECP_TYPE>,<START_INVNO>,<START_VALID_DATE>,<END_INVNO>,<START_INVNO>, &
                                        <CASHIER_CODE>,NULL,<STATUS>,<OPT_USER>,SYSDATE,<OPT_TERM>,<TERM_IP>,<REGION_CODE>)
insertData.Debug=N

//���ˣ����ʣ�ȷ�Ͻ��أ���������
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_INVOICE SET STATUS=<STATUS>,OPT_USER=<OPT_USER>, &
                                  OPT_DATE=SYSDATE,UPDATE_NO=<UPDATE_NO>, &
                                  OPT_TERM=<OPT_TERM>,TERM_IP=<TERM_IP> &
                                  WHERE RECP_TYPE=<RECP_TYPE> &
                                  AND START_INVNO=<START_INVNO> 
updataData.Debug=N

//���ظ�������//״̬2������Ʊ��=��ǰƱ��
updatainData.Type=TSQL
updatainData.SQL=UPDATE BIL_INVOICE SET STATUS=<STATUS>,START_INVNO=<UPDATE_NO_SUB>, &
                                    UPDATE_NO=<UPDATE_NO>,OPT_USER=<OPT_USER>, &
                                    OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			      WHERE RECP_TYPE=<RECP_TYPE> &
				AND START_INVNO=<START_INVNO> 
updatainData.Debug=N

//��ƱcheckƱ���Ƿ�ʹ��
 checkData.Type=TSQL 
 checkData.SQL=SELECT RECP_TYPE,START_INVNO,END_INVNO,CASHIER_CODE FROM BIL_INVOICE &
                      WHERE RECP_TYPE=<RECP_TYPE> &
                      AND (<START_INVNO> BETWEEN START_INVNO AND END_INVNO) &
                      OR (<END_INVNO> BETWEEN START_INVNO AND END_INVNO) &
                      OR (<START_INVNO> <=START_INVNO &
                      AND <END_INVNO> >=END_INVNO)
 checkData.Debug=N
 
 //��ӡƱ�ݸ�����һƱ��
 updateDatePrint.Type=TSQL
 updateDatePrint.SQL=UPDATE BIL_INVOICE SET OPT_DATE=SYSDATE,UPDATE_NO=<UPDATE_NO> &
 				  WHERE RECP_TYPE=<RECP_TYPE> &
 				  AND CASHIER_CODE=<CASHIER_CODE> &
 				  AND STATUS=<STATUS> &
 				  AND START_INVNO=<START_INVNO>
 updateDatePrint.Debug=N
 
 //��ѯ��ǰƱ��
 selectUpdateNo.Type=TSQL
 selectUpdateNo.SQL=SELECT B.UPDATE_NO,B.START_INVNO &
                        FROM BIL_COUNTER A,BIL_INVOICE B &
                        WHERE B.RECP_TYPE=A.RECP_TYPE &
                        AND A.CASHIER_CODE=B.CASHIER_CODE &
                        AND A.RECP_TYPE=<RECP_TYPE> &
                        AND A.CASHIER_CODE=<CASHIER_CODE> &
                        AND B.STATUS=<STATUS> &
                        AND A.CLS_DATE IS NULL
 selectUpdateNo.Debug=N
 
 //���ҵ�ǰʹ�õ�Ʊ��
 selectNowReceipt.Type=TSQL
 selectNowReceipt.SQL=SELECT RECP_TYPE,START_INVNO,START_VALID_DATE,END_INVNO,UPDATE_NO,CASHIER_CODE, &
                              END_VALID_DATE,STATUS,OPT_USER,OPT_DATE,OPT_TERM,TERM_IP &
                         FROM BIL_INVOICE &
                        WHERE STATUS=<STATUS> &
                              AND RECP_TYPE=<RECP_TYPE> &
                              AND CASHIER_CODE=<CASHIER_CODE> &
                              AND TERM_IP=<TERM_IP> &
                              AND UPDATE_NO IS NOT NULL
 selectNowReceipt.Debug=N
 
//����Ʊ��
upadjustData.Type=TSQL
upadjustData.SQL=UPDATE BIL_INVOICE &
		  SET OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,UPDATE_NO=<UPDATE_NO>, &
		      OPT_TERM=<OPT_TERM>,TERM_IP=<TERM_IP> &
		WHERE RECP_TYPE=<RECP_TYPE> &
		  AND START_INVNO=<START_INVNO> 
upadjustData.Debug=N


//��ֵ�˷�checkƱ���Ƿ�ʹ��
 checkUpdateNo.Type=TSQL 
 checkUpdateNo.SQL=SELECT * FROM BIL_INVRCP  WHERE RECP_TYPE=<RECP_TYPE> &
 			AND INV_NO=<INV_NO> AND CASHIER_CODE=<CASHIER_CODE>
 			
 checkUpdateNo.Debug=N
 
 
 //��ֵ�˷���BIL_INVRCP���������
  insertFeeDate.Type=TSQL 
  insertFeeDate.SQL=INSERT INTO BIL_INVRCP (RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CANCEL_FLG,CANCEL_USER,CANCEL_DATE, &
 			OPT_USER,OPT_DATE,OPT_TERM,ACCOUNT_FLG,ACCOUNT_SEQ,ACCOUNT_USER,ACCOUNT_DATE,PRINT_USER, &
 			PRINT_DATE,ADM_TYPE,STATUS) &
                        VALUES(<RECP_TYPE>,<INV_NO>,<RECEIPT_NO>,<CASHIER_CODE>,<AR_AMT>,<CANCEL_FLG>,<CANCEL_USER>,TO_DATE(<CANCEL_DATE>, 'YYYYMMDDHH24MISS'), &
 			<OPT_USER>,SYSDATE,<OPT_TERM>,<ACCOUNT_FLG>,<ACCOUNT_SEQ>,<ACCOUNT_USER>,&
 			TO_DATE(<ACCOUNT_DATE>, 'YYYYMMDDHH24MISS'),<PRINT_USER>, &
 			TO_DATE(<PRINT_DATE>, 'YYYYMMDDHH24MISS'),<ADM_TYPE>,<STATUS>) 			
 insertFeeDate.Debug=N
 
 
 //��ӡ���Ͼ�����
 cancelDate.Type=TSQL
cancelDate.SQL=UPDATE BIL_INVRCP &
 		  SET CANCEL_FLG=<CANCEL_FLG>,CANCEL_USER=<CANCEL_USER>,CANCEL_DATE=TO_DATE(<CANCEL_DATE>,'YYYYMMDDHH24MISS') WHERE RECP_TYPE=<RECP_TYPE> AND RECEIPT_NO=<RECEIPT_NO> & 		
 		  AND INV_NO=<INV_NO> 
cancelDate.Debug=N

//��ӡ����EKT_BIL_PAY�� PRINT_NO
 updateprintNo.Type=TSQL
updateprintNo.SQL=UPDATE  EKT_BIL_PAY &
 		  SET PRINT_NO=<PRINT_NO> WHERE   BIL_BUSINESS_NO=<BIL_BUSINESS_NO>		
 		 
updateprintNo.Debug=N