# 
#  Title:�豸�����ֵ����module
# 
#  Description:�豸�����ֵ����module
# 
#  Copyright: ProperSoft 2015
# 
#  author  wangjc 20150611
#  version 1.0
#
Module.item=onInsertMeasuremDic;onUpdateMeasuremDic;onDeleteMeasuremDic;onInsertDevMtnDate
//�����豸�����ֵ�     
onInsertMeasuremDic.Type=TSQL
onInsertMeasuremDic.SQL=INSERT INTO DEV_MEASURE (DEV_CODE,MEASUREM_CODE,MEASUREM_DESC,MEASUREM_PRICE,MEASUREM_CYCLE,MEASUREM_UNIT, &
								OPT_USER,OPT_DATE,OPT_TERM,ACTIVE_FLG)VALUES(<DEV_CODE>,<MEASUREM_CODE>,<MEASUREM_DESC>,<MEASUREM_PRICE>, &
								<MEASUREM_CYCLE>,<MEASUREM_UNIT>,<OPT_USER>,SYSDATE,<OPT_TERM>,<ACTIVE_FLG>)
onInsertMeasuremDic.Debug=N
//�����豸�����ֵ�      
onUpdateMeasuremDic.Type=TSQL
onUpdateMeasuremDic.SQL=UPDATE DEV_MEASURE SET DEV_CODE=<DEV_CODE>,MEASUREM_DESC=<MEASUREM_DESC>,MEASUREM_PRICE=<MEASUREM_PRICE>,MEASUREM_CYCLE=<MEASUREM_CYCLE>, &
							MEASUREM_UNIT=<MEASUREM_UNIT>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,ACTIVE_FLG=<ACTIVE_FLG> &
							WHERE MEASUREM_CODE=<MEASUREM_CODE>
onUpdateMeasuremDic.Debug=N
//ɾ���豸�����ֵ�      
onDeleteMeasuremDic.Type=TSQL
onDeleteMeasuremDic.SQL=DELETE FROM DEV_MEASURE WHERE DEV_CODE=<DEV_CODE> AND MEASUREM_CODE=<MEASUREM_CODE>
onDeleteMeasuremDic.Debug=N
//����DEV_MTN_DATE�´�ά��ʱ��
onInsertDevMtnDate.Type=TSQL
onInsertDevMtnDate.SQL=INSERT INTO DEV_MAINTENANCE_DATE (DEV_CODE, MTN_KIND, MTN_TYPE_CODE, DEVSEQ_NO, NEXT_MTN_DATE, OPT_USER, OPT_DATE, OPT_TERM) VALUES &
(<DEV_CODE>, <MTN_KIND>, <MTN_TYPE_CODE>, <DEVSEQ_NO>, TO_DATE(<NEXT_MTN_DATE>, 'YYYY/MM/DD'), <OPT_USER>, SYSDATE, <OPT_TERM>)
onInsertDevMtnDate.Debug=Y


