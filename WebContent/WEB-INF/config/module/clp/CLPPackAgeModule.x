# 
#  Title:临床路径套餐字典module
# 
#  Description:临床
# 
#  Copyright: Copyright (c) ProperSoft 2015
# 
#  author pangben 2015-8-10
#  version 1.0
#
//临床路径类别
Module.item=getMaxSeqNo;onInsert;onUpdate;onDelete;queryPackAge

//查询最大序号
getMaxSeqNo.Type=TSQL
getMaxSeqNo.SQL=SELECT MAX(SEQ_NO) SEQ_NO FROM CLP_PACKAGE
getMaxSeqNo.Debug=N

//更新临床路径套餐
onUpdate.Type=TSQL
onUpdate.SQL=UPDATE CLP_PACKAGE SET &
			PACKAGE_DESC = <PACKAGE_DESC>,PY = <PY>, &
			CLP_PACK_FLG=<CLP_PACK_FLG>,SEQ_NO=<SEQ_NO>,PACK_NOTE=<PACK_NOTE>, & 
			OPT_USER = <OPT_USER>,OPT_DATE = SYSDATE, &
			OPT_TERM = <OPT_TERM> &
      		WHERE PACKAGE_CODE = <PACKAGE_CODE>
onUpdate.Debug=N


//插入临床路径套餐
onInsert.Type=TSQL
onInsert.SQL=INSERT INTO CLP_PACKAGE( &
			PACKAGE_CODE,PACKAGE_DESC,PY,CLP_PACK_FLG,SEQ_NO,PACK_NOTE,OPT_USER,OPT_DATE,OPT_TERM) &
		VALUES( <PACKAGE_CODE>,<PACKAGE_DESC>,<PY>,<CLP_PACK_FLG>,<SEQ_NO>,<PACK_NOTE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
onInsert.Debug=N

//删除操作
onDelete.Type=TSQL
onDelete.SQL=DELETE FROM CLP_PACKAGE WHERE PACKAGE_CODE = <PACKAGE_CODE>
onDelete.Debug=N

//查询数据
queryPackAge.Type=TSQL
queryPackAge.SQL=SELECT PACKAGE_CODE,PACKAGE_DESC,PY,CLP_PACK_FLG,SEQ_NO,PACK_NOTE,OPT_USER,OPT_DATE,OPT_TERM FROM CLP_PACKAGE ORDER BY SEQ_NO
queryPackAge.item=PACKAGE_CODE
queryPackAge.PACKAGE_CODE=PACKAGE_CODE=<PACKAGE_CODE>
queryPackAge.Debug=N