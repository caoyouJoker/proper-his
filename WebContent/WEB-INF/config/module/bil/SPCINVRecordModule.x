# 
#  Title:���������ü�¼module
# 
#  Description:���������ü�¼module
# 
#  Copyright: Copyright (c) bluecore
# 
#  author caowl 20130521
#  version 1.0
#
Module.item=insertData;updInvStockD;updInvStockM;updInvStockDD;updData;deleteSpcInvRecord;updSpcInvRecordStockFlg;updCommitFlg;updInvSupDispenseD;updInvSupDispenseDD


//������ü�¼����
insertData.Type=TSQL
//wanglong modify 20140702
//pangbe 2015-10-16 ����ٴ�·��ʱ���ֶ�
insertData.SQL=INSERT INTO SPC_INV_RECORD( &
                     BUSINESS_NO, SEQ, CASE_NO, MR_NO, CLASS_CODE, BAR_CODE, INV_CODE, INV_DESC, &
                     OPMED_CODE, ORDER_CODE, ORDER_DESC, OWN_PRICE, QTY, UNIT_CODE, AR_AMT, &
                     BATCH_SEQ, VALID_DATE, BILL_FLG, BILL_DATE, NS_CODE, OP_ROOM, DEPT_CODE, &
                     EXE_DEPT_CODE, CASE_NO_SEQ, SEQ_NO, OPT_USER, OPT_DATE, OPT_TERM, YEAR_MONTH, &
                     RECLAIM_USER, RECLAIM_DATE, REQUEST_NO, PACK_BARCODE, STOCK_FLG, COMMIT_FLG, &
                     CASHIER_CODE, PAT_NAME, PACK_GROUP_NO, USED_FLG, CHECK_FLG, CHECK_NO, SCAN_ORG_CODE, &
                     PACK_DESC, CANCEL_FLG, RESET_BUSINESS_NO, RESET_SEQ_NO, ORDER_DATE, SPECIFICATION, &
                     SETMAIN_FLG, ORDERSET_CODE, ORDERSET_GROUP_NO, ORDER_DEPT_CODE, ORDER_DR_CODE,SCHD_CODE) &
               VALUES(<BUSINESS_NO>, <SEQ>, <CASE_NO>, <MR_NO>, <CLASS_CODE>, <BAR_CODE>, <INV_CODE>, &
                     <INV_DESC>, <OPMED_CODE>, <ORDER_CODE>, <ORDER_DESC>, <OWN_PRICE>, <QTY>, <UNIT_CODE>, <AR_AMT>, &
                     <BATCH_SEQ>, <VALID_DATE>, <BILL_FLG>, <BILL_DATE>, <NS_CODE>, <OP_ROOM>, <DEPT_CODE>, &
                     <EXE_DEPT_CODE>, <CASE_NO_SEQ>, <SEQ_NO>, <OPT_USER>, SYSDATE, <OPT_TERM>, <YEAR_MONTH>, &
                     <RECLAIM_USER>, <RECLAIM_DATE>, <REQUEST_NO>, <PACK_BARCODE>, 'N', 'N', &
                     <CASHIER_CODE>, <PAT_NAME>, <PACK_GROUP_NO>, <USED_FLG>, <CHECK_FLG>, <CHECK_NO>, <SCAN_ORG_CODE>, &
                     <PACK_DESC>, <CANCEL_FLG>, <RESET_BUSINESS_NO>, <RESET_SEQ_NO>, <ORDER_DATE>, <SPECIFICATION>, &
                     <SETMAIN_FLG>, <ORDERSET_CODE>, <ORDERSET_GROUP_NO>, <ORDER_DEPT_CODE>, <ORDER_DR_CODE>,<SCHD_CODE>)  
insertData.Debug=N

//�Ʒ���ɺ��д������
updData.Type=TSQL
//wanglong modify 20140627 ����bill_date
updData.SQL= UPDATE SPC_INV_RECORD SET CASE_NO_SEQ = <CASE_NO_SEQ> , SEQ_NO = <SEQ_NO>,CASHIER_CODE = <CASHIER_CODE>,BILL_DATE=<BILL_DATE> &
             WHERE BUSINESS_NO = <BUSINESS_NO> AND SEQ = <SEQ>  
updData.Debug=N

//�ۿ� ����INVStockD��
updInvStockD.Type=TSQL
updInvStockD.SQL=UPDATE INV_STOCKD SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , &
			DAYOUT_QTY=DAYOUT_QTY+<STOCK_QTY>, &
			DAY_REQUESTOUT_QTY=DAY_REQUESTOUT_QTY+<STOCK_QTY>, &
			DAY_CHANGEOUT_QTY=DAY_CHANGEOUT_QTY+<STOCK_QTY>, &
			DAY_TRANSMITOUT_QTY=DAY_TRANSMITOUT_QTY+<STOCK_QTY>, &
			DAY_WASTE_QTY=DAY_WASTE_QTY+<STOCK_QTY>, &
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ>
updInvStockD.Debug=N

//�ۿ� ����INVStockM��
updInvStockM.Type=TSQL
updInvStockM.SQL=UPDATE INV_STOCKM SET &
			STOCK_QTY=STOCK_QTY-<STOCK_QTY> , OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE INV_CODE =<INV_CODE> AND ORG_CODE=<ORG_CODE>
updInvStockM.Debug=N

//�ۿ� ����INVStockDD��
updInvStockDD.Type=TSQL
updInvStockDD.SQL=UPDATE INV_STOCKDD SET &
		        WAST_FLG=<WAST_FLG>, OUT_DATE = <OUT_DATE>,OUT_USER = <OUT_USER>,MR_NO = <MR_NO>,CASE_NO = <CASE_NO>, &
                        RX_SEQ = <RX_SEQ>,ADM_TYPE = 'I',SEQ_NO = <SEQ_NO> ,WAST_ORG = <WAST_ORG>, &
			OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	      WHERE RFID = <RFID>
updInvStockDD.Debug=N

//ɾ�����ü�¼
deleteSpcInvRecord.Type=TSQL
deleteSpcInvRecord.SQL=DELETE FROM SPC_INV_RECORD WHERE BUSINESS_NO = <BUSINESS_NO> AND SEQ = <SEQ>
deleteSpcInvRecord.Debug=N

//���¿ۿ��ֶ�
updSpcInvRecordStockFlg.Type=TSQL
updSpcInvRecordStockFlg.SQL=UPDATE SPC_INV_RECORD SET STOCK_FLG = 'Y' WHERE BUSINESS_NO = <BUSINESS_NO> AND SEQ = <SEQ>
updSpcInvRecordStockFlg.Debug=N

//�����ύ�ֶ�
updCommitFlg.Type=TSQL
updCommitFlg.SQL=UPDATE SPC_INV_RECORD SET COMMIT_FLG = 'Y' WHERE BUSINESS_NO = <BUSINESS_NO> AND SEQ = <SEQ>
updCommitFlg.Debug=N

//���°��Ŀ��
updInvSupDispenseD.Type=TSQL
updInvSupDispenseD.SQL=UPDATE INV_SUP_DISPENSED SET ACTUAL_QTY = ACTUAL_QTY -<QTY> WHERE DISPENSE_NO=<DISPENSE_NO> AND SEQ_NO = <SEQ_NO>
updInvSupDispenseD.Debug=N


//����ϸ��Ŀ��
updInvSupDispenseDD.Type=TSQL
updInvSupDispenseDD.SQL=UPDATE INV_SUP_DISPENSEDD SET QTY=QTY-<QTY> WHERE  DISPENSE_NO=<DISPENSE_NO> AND PACK_CODE = <PACK_CODE> AND PACK_BATCH_NO = <PACK_BATCH_NO> AND INV_CODE=<INV_CODE>
updInvSupDispenseDD.Debug=N


