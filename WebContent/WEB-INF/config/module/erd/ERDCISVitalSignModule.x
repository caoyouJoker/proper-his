 #
   # Title:急诊CIS体征监测数据接口
   #
   # Description:急诊CIS体征监测数据接口
   #
   # Copyright: Bluecore (c) 2015
   #
   # @author wangbin 2015.05.07

Module.item=insertERDCISVitalSign;updateOPDSysparm

//向病区重症体征监测记录表ODI_CISVITALSIGN插入数据
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

//更新门诊参数档的捕捉数据时间区间，用于下一次数据同步
updateOPDSysparm.Type=TSQL
updateOPDSysparm.SQL=UPDATE OPD_SYSPARM SET START_POOLING_TIME=<START_POOLING_TIME>,END_POOLING_TIME=<END_POOLING_TIME>
updateOPDSysparm.Debug=N
