# 
#  Title:�豸��������module
# 
#  Description:�豸��������module
# 
#  Copyright: BlueCore 2015
# 
#  author  wangjc 20150611
#  version 1.0
#
Module.item=onUpdateMaintenance
//�����豸�����ֵ�     
onUpdateMaintenance.Type=TSQL
onUpdateMaintenance.SQL=UPDATE DEV_MAINTENANCE_DATE SET NEXT_MTN_DATE=TO_DATE(<NEXT_MTN_DATE>,'YYYY/MM/DD HH24:MI:SS'), &
						OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE DEV_CODE=<DEV_CODE> AND MTN_KIND=<MTN_KIND> &
						AND MTN_TYPE_CODE=<MTN_TYPE_CODE> AND DEVSEQ_NO=<DEVSEQ_NO>
onUpdateMaintenance.Debug=Y



