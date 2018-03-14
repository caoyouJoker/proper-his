  #
   # Title: 重点药品检测
   #
   # Description:重点药品检测
   #
   # Copyright: JavaHis (c) 2012
   #
   # @author wukai
Module.item=selectMonitorMed;delectMonitorMed;saveMonitorMed;updateMonitorMed


selectMonitorMed.Type=TSQL
selectMonitorMed.SQL=SELECT ORDER_CODE, ORDER_DESC, SPECIFICATION, MONITOR_TYPE, &
 						CASE WHEN MONITOR_TYPE='DAN' THEN '危险' &
 							WHEN MONITOR_TYPE='LAC' THEN '短缺' &
 							WHEN MONITOR_TYPE='NVAL' THEN '非医保贵重' &
							WHEN MONITOR_TYPE='INSUL' THEN '胰岛素类' &
 							//liuyalin 20170328 add
 							WHEN MONITOR_TYPE='NUTR' THEN '肠外营养液' &
 							ELSE '' END AS MONITOR_NAME, OPT_DATE, OPT_USER, OPT_TERM, ENABLE_FLG  &
					 FROM IND_MONITOR_MED WHERE 1 = 1 &
					ORDER BY MONITOR_TYPE, ORDER_CODE
selectMonitorMed.Item=ORDER_CODE;MONITOR_TYPE;ENABLE_FLG
selectMonitorMed.ORDER_CODE=ORDER_CODE=<ORDER_CODE> 
selectMonitorMed.MONITOR_TYPE=MONITOR_TYPE=<MONITOR_TYPE> 
selectMonitorMed.ENABLE_FLG=ENABLE_FLG=<ENABLE_FLG> 
selectMonitorMed.Debug=Y

delectMonitorMed.Type=TSQL
delectMonitorMed.SQL=DELETE FROM IND_MONITOR_MED WHERE ORDER_CODE=<ORDER_CODE> AND MONITOR_TYPE=<MONITOR_TYPE>
delectMonitorMed.Debug=N


saveMonitorMed.Type=TSQL
saveMonitorMed.SQL=INSERT INTO IND_MONITOR_MED (ORDER_CODE, ORDER_DESC, SPECIFICATION, MONITOR_TYPE, OPT_DATE, OPT_USER, OPT_TERM, ENABLE_FLG) VALUES &
					(<ORDER_CODE>, <ORDER_DESC>, <SPECIFICATION>, <MONITOR_TYPE>, TO_DATE(<OPT_DATE>, 'YYYYMMDDHH24MISS'), <OPT_USER>, <OPT_TERM>, <ENABLE_FLG> ) 
saveMonitorMed.Debug=N

updateMonitorMed.Type=TSQL
updateMonitorMed.SQL=UPDATE IND_MONITOR_MED SET  ORDER_DESC = <ORDER_DESC>,  SPECIFICATION = <SPECIFICATION>,  MONITOR_TYPE = <MONITOR_TYPE>,  OPT_DATE = TO_DATE(<OPT_DATE>, 'YYYYMMDDHH24MISS'),  OPT_USER = <OPT_USER>,  OPT_TERM = <OPT_TERM>,  ENABLE_FLG = <ENABLE_FLG> & 
					 WHERE ORDER_CODE = <ORDER_CODE>  AND MONITOR_TYPE = <OLD_MONITOR_TYPE>
updateMonitorMed.Debug=N

