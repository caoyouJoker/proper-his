   #
   # Title:���ư��˻�
   #
   # Description:���ư��˻�
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/11/22

Module.item=insertPReturnM;insertPReturnD;queryPReturnM;queryPReturnD;delPReturnM;delPReturnD;updatePConfirmStatus;updatePReturnDSec;updatePackstockMQty;queryPackD;updatePackstockDQty;querySupDispense;updateSupDispenseD;updateSupDispenseDD;querySupDispenseDD;queryPackDSec



//�������ư��˻�������
insertPReturnM.Type=TSQL
insertPReturnM.SQL=INSERT INTO INV_SUP_PACKAGERETURNM ( &
			PACKAGERETURNED_NO,FROM_ORG_CODE,TO_ORG_CODE,CONFIRM_FLG, & 
			OPT_USER,OPT_DATE,OPT_TERM,CHECK_USER,CHECK_DATE ) &
	    	      VALUES ( &
	    	   	<PACKAGERETURNED_NO>, <FROM_ORG_CODE>, <TO_ORG_CODE>, <CONFIRM_FLG>, & 
			<OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <CHECK_USER>, TO_DATE(<CHECK_DATE>,'yyyy/mm/dd hh24:mi:ss') )
insertPReturnM.Debug=N

//�������ư��˻���ϸ��
insertPReturnD.Type=TSQL
insertPReturnD.SQL=INSERT INTO INV_SUP_PACKAGERETURND ( &
			PACKAGERETURNED_NO,SEQ,PACK_CODE,QTY, & 
			OPT_USER,OPT_DATE,OPT_TERM ) &
			VALUES ( &
			<PACKAGERETURNED_NO>, <SEQ>, <PACK_CODE>, <QTY>,  &
			<OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM> )
insertPReturnD.Debug=N




//����������ѯ�˻�������
queryPReturnM.Type=TSQL
queryPReturnM.SQL=SELECT M.PACKAGERETURNED_NO, M.FROM_ORG_CODE, M.TO_ORG_CODE, M.CONFIRM_FLG, M.OPT_USER, M.OPT_DATE, &
			M.OPT_TERM, M.CONFIRM_USER, M.CONFIRM_DATE, M.CHECK_DATE, M.CHECK_USER &
  	      FROM INV_SUP_PACKAGERETURNM M &
	      ORDER BY M.PACKAGERETURNED_NO DESC
queryPReturnM.ITEM=PACKAGERETURNED_NO;FROM_ORG_CODE;TO_ORG_CODE;START_DATE;END_DATE;CONFIRM_FLG
queryPReturnM.PACKAGERETURNED_NO=PACKAGERETURNED_NO=<PACKAGERETURNED_NO>
queryPReturnM.FROM_ORG_CODE=FROM_ORG_CODE=<FROM_ORG_CODE>
queryPReturnM.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryPReturnM.START_DATE=CHECK_DATE>=TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss')
queryPReturnM.END_DATE=CHECK_DATE<=TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss')
queryPReturnM.CONFIRM_FLG=CONFIRM_FLG=<CONFIRM_FLG>
queryPReturnM.Debug=N



//����������ѯ�˻���ϸ��
queryPReturnD.Type=TSQL
queryPReturnD.SQL=SELECT D.PACKAGERETURNED_NO, D.SEQ, B.PACK_DESC, D.PACK_CODE,  D.QTY,  D.OPT_USER, D.OPT_DATE, &
			D.OPT_TERM &
  	      FROM INV_SUP_PACKAGERETURND D LEFT JOIN INV_PACKM B ON D.PACK_CODE = B.PACK_CODE &
	      ORDER BY D.SEQ ASC
queryPReturnD.ITEM=PACKAGERETURNED_NO
queryPReturnD.PACKAGERETURNED_NO=PACKAGERETURNED_NO=<PACKAGERETURNED_NO>
queryPReturnD.Debug=N 


//ɾ���˻���������Ϣ
delPReturnM.Type=TSQL
delPReturnM.SQL=DELETE INV_SUP_PACKAGERETURNM WHERE PACKAGERETURNED_NO = <PACKAGERETURNED_NO>
delPReturnM.Debug=N


//ɾ���˻���ϸ����Ϣ
delPReturnD.Type=TSQL
delPReturnD.SQL=DELETE INV_SUP_PACKAGERETURND WHERE PACKAGERETURNED_NO = <PACKAGERETURNED_NO>
delPReturnD.Debug=N



//�޸��˻���ȷ��״̬
updatePConfirmStatus.Type=TSQL
updatePConfirmStatus.SQL=UPDATE INV_SUP_PACKAGERETURNM SET CONFIRM_FLG = 'Y', CONFIRM_USER = <CONFIRM_USER>, CONFIRM_DATE = TO_DATE(<CONFIRM_DATE>,'yyyy/mm/dd hh24:mi:ss') WHERE PACKAGERETURNED_NO = <PACKAGERETURNED_NO> 
updatePConfirmStatus.Debug=N

//�������������˻���ϸ��(����ȷ����Ա��ʱ��)
updatePReturnDSec.Type=TSQL
updatePReturnDSec.SQL=UPDATE INV_SUP_PACKAGERETURND SET CONFIRM_USER = <CONFIRM_USER>, CONFIRM_DATE = TO_DATE(<CONFIRM_DATE>,'yyyy/mm/dd hh24:mi:ss') WHERE PACKAGERETURNED_NO = <PACKAGERETURNED_NO> 
updatePReturnDSec.Debug=N



//�ӻ�inv_packstockm����
updatePackstockMQty.Type=TSQL
updatePackstockMQty.SQL=UPDATE INV_PACKSTOCKM M SET M.QTY = M.QTY + <QTY> WHERE M.PACK_CODE = <PACK_CODE> AND M.PACK_BATCH_NO = ( SELECT MAX(M.PACK_BATCH_NO) FROM INV_PACKSTOCKM M WHERE M.PACK_CODE = <PACK_CODE> )
updatePackstockMQty.Debug=N


//��ѯ����������
queryPackD.Type=TSQL
queryPackD.SQL=SELECT PACK_CODE, INV_CODE, QTY FROM INV_PACKD D WHERE D.PACK_CODE = <PACK_CODE>
queryPackD.Debug=N


//��ѯ���������ɲ�������ΪINV_CODE
queryPackDSec.Type=TSQL
queryPackDSec.SQL=SELECT PACK_CODE, INV_CODE, QTY FROM INV_PACKD D WHERE D.PACK_CODE = <INV_CODE>
queryPackDSec.Debug=N



//�ӻ�inv_packstockd����
updatePackstockDQty.Type=TSQL
updatePackstockDQty.SQL=UPDATE INV_PACKSTOCKD D SET D.QTY = D.QTY + <QTY> WHERE D.PACK_CODE = <PACK_CODE> AND D.INV_CODE = <INV_CODE> AND D.PACK_BATCH_NO = ( SELECT MAX(M.PACK_BATCH_NO) FROM INV_PACKSTOCKM M WHERE M.PACK_CODE = <PACK_CODE> ) AND D.BATCH_SEQ = ( SELECT MAX(D.BATCH_SEQ) FROM INV_PACKSTOCKD D  WHERE D.PACK_CODE = <PACK_CODE> AND D.INV_CODE = <INV_CODE> AND D.PACK_BATCH_NO = ( SELECT MAX(M.PACK_BATCH_NO) FROM INV_PACKSTOCKM M WHERE M.PACK_CODE = <PACK_CODE> ) )
updatePackstockDQty.Debug=N


//��ѯ��Ӧ�ҳ�����е�ͬ����������棨������ʴΣ�
querySupDispense.Type=TSQL
querySupDispense.SQL=SELECT * FROM INV_SUP_DISPENSED D WHERE D.INV_CODE = <PACK_CODE> AND D.ACTUAL_QTY > 0 AND D.ACTUAL_QTY IS NOT NULL AND D.MATERIAL_LOCATION IN ( SELECT M.MATERIAL_LOC_CODE FROM INV_MATERIALLOC M WHERE M.CORRSUPDEPT = <FROM_ORG_CODE> ) ORDER BY DISPENSE_NO ASC, PACK_BATCH_NO DESC
querySupDispense.Debug=N


//��ѯ��Ӧ�ҳ�����е�ͬ�������������ϸ
querySupDispenseDD.Type=TSQL
querySupDispenseDD.SQL=SELECT * FROM INV_SUP_DISPENSEDD D WHERE D.DISPENSE_NO = <DISPENSE_NO> AND D.PACK_CODE = <INV_CODE> AND D.INV_CODE = <INV_CODE_SEC> AND D.QTY > 0 AND D.PACK_BATCH_NO = <PACK_BATCH_NO> ORDER BY D.SEQ_NO ASC
querySupDispenseDD.Debug=N



//��ȥ���ⵥactual_qty��������������������
updateSupDispenseD.Type=TSQL
updateSupDispenseD.SQL=UPDATE INV_SUP_DISPENSED D SET D.ACTUAL_QTY = D.ACTUAL_QTY + <QTY> WHERE D.DISPENSE_NO = <DISPENSE_NO> AND D.INV_CODE = <INV_CODE> AND D.PACK_BATCH_NO = <PACK_BATCH_NO>
updateSupDispenseD.Debug=N





//��ȥ���ⵥ������ϸ����
updateSupDispenseDD.Type=TSQL
updateSupDispenseDD.SQL=UPDATE INV_SUP_DISPENSEDD D SET D.QTY = D.QTY + <QTY> WHERE D.DISPENSE_NO = <DISPENSE_NO> AND D.PACK_CODE = <PACK_CODE> AND D.PACK_BATCH_NO = <PACK_BATCH_NO> AND D.INV_CODE = <INV_CODE> AND D.SEQ_NO = <SEQ_NO>
updateSupDispenseDD.Debug=N


