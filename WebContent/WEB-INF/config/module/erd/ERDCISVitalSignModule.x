 #
   # Title:����CIS����������ݽӿ�
   #
   # Description:����CIS����������ݽӿ�
   #
   # Copyright: Bluecore (c) 2015
   #
   # @author wangbin 2015.05.07

Module.item=insertERDCISVitalSign;updateOPDSysparm

//������֢��������¼��ODI_CISVITALSIGN��������
insertERDCISVitalSign.Type=TSQL
insertERDCISVitalSign.SQL=INSERT INTO ERD_CISVITALSIGN &
							(BED_NO, MONITOR_ITEM_EN, MONITOR_TIME, &
							ADM_TYPE, MR_NO, CASE_NO, MONITOR_ITEM_CH, &
							MONITOR_VALUE, UNIT_DESC, NORMAL_RANGE_L, &
							NORMAL_RANGE_H, REMARKS, OPT_USER, OPT_DATE, OPT_TERM) &
						  VALUES &
							(<BED_NO>,<MONITOR_ITEM_EN>,<MONITOR_TIME>, &
							<ADM_TYPE>,<MR_NO>,<CASE_NO>,<MONITOR_ITEM_CH>, &
							<MONITOR_VALUE>,<UNIT_DESC>,<NORMAL_RANGE_L>, &
							<NORMAL_RANGE_H>,<REMARKS>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertERDCISVitalSign.Debug=N

//��������������Ĳ�׽����ʱ�����䣬������һ������ͬ��
updateOPDSysparm.Type=TSQL
updateOPDSysparm.SQL=UPDATE OPD_SYSPARM SET START_POOLING_TIME=<START_POOLING_TIME>,END_POOLING_TIME=<END_POOLING_TIME>
updateOPDSysparm.Debug=N
