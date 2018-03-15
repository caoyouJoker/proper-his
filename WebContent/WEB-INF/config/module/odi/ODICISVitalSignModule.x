 #
   # Title:����CIS����������ݽӿ�
   #
   # Description:����CIS����������ݽӿ�
   #
   # Copyright: ProperSoft (c) 2015
   #
   # @author wangbin 2015.05.04

Module.item=insertODICISVitalSign;updateODISysparmByICU;updateODISysparmByCCU;updateODISysparmByWARD;insertSysPatchLog

//������֢��������¼��ODI_CISVITALSIGN��������
insertODICISVitalSign.Type=TSQL
insertODICISVitalSign.SQL=INSERT INTO ODI_CISVITALSIGN &
							(CASE_NO, MONITOR_ITEM_EN, MONITOR_TIME, &
							ADM_TYPE, MR_NO, BED_NO, &
							MONITOR_ITEM_CH, MONITOR_VALUE, UNIT_DESC, &
							NORMAL_RANGE_L, NORMAL_RANGE_H, REMARKS, &
							OPT_USER, OPT_DATE, OPT_TERM) &
						  VALUES &
							(<CASE_NO>,<MONITOR_ITEM_EN>,<MONITOR_TIME>, &
							<ADM_TYPE>,<MR_NO>,<BED_NO>,<MONITOR_ITEM_CH>, &
							<MONITOR_VALUE>,<UNIT_DESC>,<NORMAL_RANGE_L>, &
							<NORMAL_RANGE_H>,<REMARKS>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertODICISVitalSign.Debug=N

//����סԺ�������Ĳ�׽����ʱ�����䣬������һ������ͬ��(ICU)
updateODISysparmByICU.Type=TSQL
updateODISysparmByICU.SQL=UPDATE ODI_SYSPARM SET ICU_SPOOL_TIME=<START_POOLING_TIME>,ICU_EPOOL_TIME=<END_POOLING_TIME>
updateODISysparmByICU.Debug=N

//����סԺ�������Ĳ�׽����ʱ�����䣬������һ������ͬ��(CCU)
updateODISysparmByCCU.Type=TSQL
updateODISysparmByCCU.SQL=UPDATE ODI_SYSPARM SET CCU_SPOOL_TIME=<START_POOLING_TIME>,CCU_EPOOL_TIME=<END_POOLING_TIME>
updateODISysparmByCCU.Debug=N

//����סԺ�������Ĳ�׽����ʱ�����䣬������һ������ͬ��(WARD)
updateODISysparmByWARD.Type=TSQL
updateODISysparmByWARD.SQL=UPDATE ODI_SYSPARM SET WARD_SPOOL_TIME=<START_POOLING_TIME>,WARD_EPOOL_TIME=<END_POOLING_TIME>
updateODISysparmByWARD.Debug=N

//�������ݼ��ɽӿ���־
insertSysPatchLog.Type=TSQL
insertSysPatchLog.SQL=INSERT INTO SYS_PATCH_LOG &
							(PATCH_CODE, PATCH_START_DATE, PATCH_DESC, &
							PATCH_SRC, PATCH_TYPE, PATCH_DATE, PATCH_REOMIT_COUNT, &
							PATCH_REOMIT_INTERVAL, PATCH_REOMIT_POINT, PATCH_REOMIT_INDEX, &
							PATCH_END_DATE, PATCH_STATUS, PATCH_MESSAGE, SERVER_IP, &
							OPT_USER, OPT_DATE, OPT_TERM) &
						  VALUES &
							(<PATCH_CODE>,<PATCH_START_DATE>,<PATCH_DESC>,<PATCH_SRC>, &
							<PATCH_TYPE>,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),<PATCH_REOMIT_COUNT>, &
							<PATCH_REOMIT_INTERVAL>,<PATCH_REOMIT_POINT>,<PATCH_REOMIT_INDEX>, &
							<PATCH_END_DATE>,<PATCH_STATUS>,<PATCH_MESSAGE>,<SERVER_IP>, &
							<OPT_USER>,SYSDATE,<OPT_TERM>)
insertSysPatchLog.Debug=N