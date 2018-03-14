# 
#  Title:健康检查module
# 
#  Description:健康检查module
# 
#  Copyright: Copyright (c) bluecore 2012
# 
#  author pangb 2013.03.10
#  version 4.0
#
Module.item=deleteHrmOrder;selectMedApplyOrder

//删除记录
deleteHrmOrder.Type=TSQL
deleteHrmOrder.SQL=DELETE FROM HRM_ORDER WHERE  CONTRACT_CODE=<CONTRACT_CODE> AND MR_NO=<MR_NO> AND EXEC_DR_CODE IS NULL AND BILL_FLG='N'     
deleteHrmOrder.Debug=N

//查询检查项目
selectMedApplyOrder.Type=TSQL
selectMedApplyOrder.SQL=SELECT * FROM MED_APPLY A, HRM_PATADM B WHERE B.CONTRACT_CODE = <CONTRACT_CODE> AND B.MR_NO = <MR_NO> AND B.CASE_NO = A.CASE_NO
selectMedApplyOrder.Debug=N
