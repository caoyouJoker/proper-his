# 
#  Title:设备计量字典操作module
# 
#  Description:设备计量字典操作module
# 
#  Copyright: ProperSoft 2015
# 
#  author  wangjc 20150611
#  version 1.0
#
Module.item=onInsertMeasuremDic;onUpdateMeasuremDic;onDeleteMeasuremDic;onInsertDevMtnDate
//插入设备计量字典     
onInsertMeasuremDic.Type=TSQL
onInsertMeasuremDic.SQL=INSERT INTO DEV_MEASURE (DEV_CODE,MEASUREM_CODE,MEASUREM_DESC,MEASUREM_PRICE,MEASUREM_CYCLE,MEASUREM_UNIT, &
								OPT_USER,OPT_DATE,OPT_TERM,ACTIVE_FLG)VALUES(<DEV_CODE>,<MEASUREM_CODE>,<MEASUREM_DESC>,<MEASUREM_PRICE>, &
								<MEASUREM_CYCLE>,<MEASUREM_UNIT>,<OPT_USER>,SYSDATE,<OPT_TERM>,<ACTIVE_FLG>)
onInsertMeasuremDic.Debug=N
//更新设备计量字典      
onUpdateMeasuremDic.Type=TSQL
onUpdateMeasuremDic.SQL=UPDATE DEV_MEASURE SET DEV_CODE=<DEV_CODE>,MEASUREM_DESC=<MEASUREM_DESC>,MEASUREM_PRICE=<MEASUREM_PRICE>,MEASUREM_CYCLE=<MEASUREM_CYCLE>, &
							MEASUREM_UNIT=<MEASUREM_UNIT>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,ACTIVE_FLG=<ACTIVE_FLG> &
							WHERE MEASUREM_CODE=<MEASUREM_CODE>
onUpdateMeasuremDic.Debug=N
//删除设备计量字典      
onDeleteMeasuremDic.Type=TSQL
onDeleteMeasuremDic.SQL=DELETE FROM DEV_MEASURE WHERE DEV_CODE=<DEV_CODE> AND MEASUREM_CODE=<MEASUREM_CODE>
onDeleteMeasuremDic.Debug=N
//插入DEV_MTN_DATE下次维护时间
onInsertDevMtnDate.Type=TSQL
onInsertDevMtnDate.SQL=INSERT INTO DEV_MAINTENANCE_DATE (DEV_CODE, MTN_KIND, MTN_TYPE_CODE, DEVSEQ_NO, NEXT_MTN_DATE, OPT_USER, OPT_DATE, OPT_TERM) VALUES &
(<DEV_CODE>, <MTN_KIND>, <MTN_TYPE_CODE>, <DEVSEQ_NO>, TO_DATE(<NEXT_MTN_DATE>, 'YYYY/MM/DD'), <OPT_USER>, SYSDATE, <OPT_TERM>)
onInsertDevMtnDate.Debug=Y


