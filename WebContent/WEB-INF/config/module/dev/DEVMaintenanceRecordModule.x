# 
#  Title:设备维护主档操作module
# 
#  Description:设备维护主档操作module
# 
#  Copyright: ProperSoft 2015
# 
#  author  wangjc 20150611
#  version 1.0
#
Module.item=onInsertMaintenanceRecord;onUpdateMaintenanceDate
//插入设备维护记录      
onInsertMaintenanceRecord.Type=TSQL
onInsertMaintenanceRecord.SQL=INSERT INTO DEV_MAINTENANCE_RECORD (MTN_NO,SEQ,DEPT_CODE,DEV_CODE,MTN_KIND, &
							MTN_TYPE_CODE,MTN_DATE,MTN_HOUR,MTN_ENGINEER,MTN_RESULT,MTN_EVALUATION,FILE_PATH, &
							FILE_NAME,OPT_USER,OPT_DATE,OPT_TERM,DEV_CODE_DETAIL) VALUES(<MTN_NO>,<SEQ>,<DEPT_CODE>,<DEV_CODE>,<MTN_KIND>, &
							<MTN_TYPE_CODE>,TO_DATE(<MTN_DATE>,'YYYY/MM/DD'),<MTN_HOUR>,<MTN_ENGINEER>,<MTN_RESULT>,<MTN_EVALUATION>,<FILE_PATH>, &
							<FILE_NAME>,<OPT_USER>,SYSDATE,<OPT_TERM>,<DEV_CODE_DETAIL>)
onInsertMaintenanceRecord.Debug=N
//更新设备下次维护时间      
onUpdateMaintenanceDate.Type=TSQL
onUpdateMaintenanceDate.SQL=UPDATE DEV_MAINTENANCE_DATE SET NEXT_MTN_DATE=TO_DATE(<NEXT_MTN_DATE>,'YYYY/MM/DD'),OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE DEV_CODE=<DEV_CODE> AND MTN_KIND=<MTN_KIND> AND MTN_TYPE_CODE=<MTN_TYPE_CODE> AND DEVSEQ_NO=<DEV_CODE_DETAIL>
onUpdateMaintenanceDate.Debug=N