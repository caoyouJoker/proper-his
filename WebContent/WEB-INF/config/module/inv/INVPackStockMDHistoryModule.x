   #
   # Title: �����������ʷ��ϸ��
   #
   # Description: �����������ʷ��ϸ��
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/07/31

Module.item=insertPackageMHistory;insertPackageDHistory;insertRepackPackageDHistory;insertRepackPackageMHistory

 
//���������������ʷ����
insertPackageMHistory.Type=TSQL
insertPackageMHistory.SQL=INSERT INTO INV_PACKSTOCKM_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, BARCODE, DESCRIPTION, QTY, &
			   USE_COST, ONCE_USE_COST, STATUS, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <BARCODE>, <DESCRIPTION>, <QTY>, &
			   <USE_COST>, <ONCE_USE_COST>, <STATUS>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertPackageMHistory.Debug=N




//���������������ʷϸ��
insertPackageDHistory.Type=TSQL
insertPackageDHistory.SQL=INSERT INTO INV_PACKSTOCKD_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, BATCH_SEQ, &
			   INVSEQ_NO, BARCODE, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, USED_QTY, NOTUSED_QTY, &
			   STOCK_UNIT, ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <BATCH_SEQ>, &
			   <INVSEQ_NO>, <BARCODE>, <DESCRIPTION>, <RECOUNT_TIME>, <COST_PRICE>, <QTY>, <USED_QTY>, <NOTUSED_QTY>, &
			   <STOCK_UNIT>, <ONCE_USE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertPackageDHistory.Debug=N




//���������������ʷϸ�������´���׶Σ�
insertRepackPackageDHistory.Type=TSQL
insertRepackPackageDHistory.SQL=INSERT INTO INV_PACKSTOCKD_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, BATCH_SEQ, &
			   INVSEQ_NO, BARCODE, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, USED_QTY, NOTUSED_QTY, &
			   STOCK_UNIT, ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <BATCH_SEQ>, &
			   <INVSEQ_NO>, <BARCODE>, <DESCRIPTION>, <RECOUNT_TIME>, <COST_PRICE>, <QTY>, <USED_QTY>, <NOTUSED_QTY>, &
			   <STOCK_UNIT>, <ONCE_USE_FLG>, <OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>)
insertRepackPackageDHistory.Debug=N


//���������������ʷ���������´���׶Σ�
insertRepackPackageMHistory.Type=TSQL
insertRepackPackageMHistory.SQL=INSERT INTO INV_PACKSTOCKM_HISTORY(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, BARCODE, DESCRIPTION, QTY, &
			   USE_COST, ONCE_USE_COST, STATUS, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <BARCODE>, <DESCRIPTION>, <QTY>, &
			   <USE_COST>, <ONCE_USE_COST>, <STATUS>, <OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>)
insertRepackPackageMHistory.Debug=N

