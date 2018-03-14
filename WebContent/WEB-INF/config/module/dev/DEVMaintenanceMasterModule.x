# 
#  Title:�豸ά����������module
# 
#  Description:�豸ά����������module
# 
#  Copyright: BlueCore 2015
# 
#  author  wangjc 20150611
#  version 1.0
#
Module.item=onInsertMaintenanceMaster;onUpdateMaintenanceMaster;onInsertMaintenanceMasterDetail;onUpdateMaintenanceMasterDetail;onDeleteMaintenanceMaster;onDeleteAllMaintenanceMasterDetail;onDeleteOneMaintenanceMasterDetail;onInsertDevMtnDate
//�����豸ά��������Ϣ      
onInsertMaintenanceMaster.Type=TSQL
onInsertMaintenanceMaster.SQL=INSERT INTO DEV_MAINTENANCEM (DEV_CODE,MTN_KIND,MTN_TYPE_CODE,MTN_TYPE_DESC,MTN_CYCLE, &
						   MTN_UNIT,OPT_USER,OPT_DATE,OPT_TERM,ACTIVE_FLG) VALUES(<DEV_CODE>,<MTN_KIND>,<MTN_TYPE_CODE>, &
						   <MTN_TYPE_DESC>,<MTN_CYCLE>,<MTN_UNIT>,<OPT_USER>,SYSDATE,<OPT_TERM>,<ACTIVE_FLG>)
onInsertMaintenanceMaster.Debug=N
//�����豸ά��������Ϣ      
onUpdateMaintenanceMaster.Type=TSQL
onUpdateMaintenanceMaster.SQL=UPDATE DEV_MAINTENANCEM SET DEV_CODE=<DEV_CODE>,MTN_KIND=<MTN_KIND>,MTN_CYCLE=<MTN_CYCLE>,MTN_UNIT=<MTN_UNIT>, &
							  MTN_TYPE_DESC=<MTN_TYPE_DESC>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,ACTIVE_FLG=<ACTIVE_FLG> &
							  WHERE MTN_TYPE_CODE=<MTN_TYPE_CODE>
onUpdateMaintenanceMaster.Debug=N
//�����豸ά��ϸ����Ϣ      
onInsertMaintenanceMasterDetail.Type=TSQL
onInsertMaintenanceMasterDetail.SQL=INSERT INTO DEV_MAINTENANCED (DEV_CODE,MTN_KIND,MTN_TYPE_CODE,MTN_DETAIL_CODE,MTN_DETAIL_DESC, &
								OPT_USER,OPT_DATE,OPT_TERM) VALUES(<DEV_CODE>,<MTN_KIND>,<MTN_TYPE_CODE>, &
								<MTN_DETAIL_CODE>,<MTN_DETAIL_DESC>,<OPT_USER>,SYSDATE,<OPT_TERM>)
onInsertMaintenanceMasterDetail.Debug=N
//�����豸ά��ϸ����Ϣ
onUpdateMaintenanceMasterDetail.Type=TSQL
onUpdateMaintenanceMasterDetail.SQL=UPDATE DEV_MAINTENANCED SET MTN_DETAIL_DESC=<MTN_DETAIL_DESC>,OPT_USER=<OPT_USER>, &
									OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,DEV_CODE=<DEV_CODE>,MTN_KIND=<MTN_KIND> &
									WHERE MTN_TYPE_CODE=<MTN_TYPE_CODE> AND MTN_DETAIL_CODE=<MTN_DETAIL_CODE>
onUpdateMaintenanceMasterDetail.Debug=N
//ɾ���豸ά��������Ϣ
onDeleteMaintenanceMaster.Type=TSQL
onDeleteMaintenanceMaster.SQL=DELETE FROM DEV_MAINTENANCEM WHERE DEV_CODE=<DEV_CODE> AND MTN_KIND=<MTN_KIND> AND MTN_TYPE_CODE=<MTN_TYPE_CODE>
onDeleteMaintenanceMaster.Debug=N
//ɾ���豸ά��ϸ����Ϣĳһ����Ϣ
onDeleteAllMaintenanceMasterDetail.Type=TSQL
onDeleteAllMaintenanceMasterDetail.SQL=DELETE FROM DEV_MAINTENANCED WHERE DEV_CODE=<DEV_CODE> AND MTN_KIND=<MTN_KIND> AND MTN_TYPE_CODE=<MTN_TYPE_CODE>
onDeleteAllMaintenanceMasterDetail.Debug=N
//ɾ���豸ά��ϸ����Ϣĳһ����Ϣ
onDeleteOneMaintenanceMasterDetail.Type=TSQL
onDeleteOneMaintenanceMasterDetail.SQL=DELETE FROM DEV_MAINTENANCED WHERE DEV_CODE=<DEV_CODE> AND MTN_KIND=<MTN_KIND> AND MTN_TYPE_CODE=<MTN_TYPE_CODE> AND MTN_DETAIL_CODE=<MTN_DETAIL_CODE>
onDeleteOneMaintenanceMasterDetail.Debug=N
//����DEV_MTN_DATE�´�ά��ʱ��
onInsertDevMtnDate.Type=TSQL
onInsertDevMtnDate.SQL=INSERT INTO DEV_MAINTENANCE_DATE (DEV_CODE, MTN_KIND, MTN_TYPE_CODE, DEVSEQ_NO, NEXT_MTN_DATE, OPT_USER, OPT_DATE, OPT_TERM) VALUES &
(<DEV_CODE>, <MTN_KIND>, <MTN_TYPE_CODE>, <DEVSEQ_NO>, TO_DATE(<NEXT_MTN_DATE>, 'YYYY/MM/DD'), <OPT_USER>, SYSDATE, <OPT_TERM>)
onInsertDevMtnDate.Debug=N
