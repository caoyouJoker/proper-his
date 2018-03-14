##################################################
# <p>Title:</p>
#
# <p>Description:</p>
#
# <p>Copyright: Copyright (c) 2013</p>
#
# <p>Company:JavaHis </p>
#
# @author huangtt 2013-04-18
# @version 4.0
##################################################
Module.item=insertDataD;insertDataM;deleteDataD;deleteDataM


//新增数据
insertDataM.Type=TSQL
insertDataM.SQL=INSERT INTO SYS_ORDER_OPMEDM( &
	OPMED_CODE, OPMED_NAME, OPERATION_ICD,&
      OPT_USER, OPT_DATE, OPT_TERM &
		) VALUES ( &
		<OPMED_CODE>, <OPMED_NAME>, <OPERATION_ICD>,   &
  <OPT_USER>, SYSDATE, <OPT_TERM>)
insertDataM.Debug=Y

//新增数据
insertDataD.Type=TSQL
insertDataD.SQL=INSERT INTO SYS_ORDER_OPMEDD( &
	OPMED_CODE, SEQ_NO, ORDER_CODE, ORDER_DESC, QTY, UNIT_PRICE, &
     UNIT_CODE, SPECIFICATION, OPT_USER, OPT_DATE, OPT_TERM &
		) VALUES ( &
		<OPMED_CODE>, <SEQ_NO>, <ORDER_CODE>, <ORDER_DESC>,  &
<QTY>, <UNIT_PRICE>, <UNIT_CODE>, <SPECIFICATION>, &
  <OPT_USER>, SYSDATE, <OPT_TERM>)
insertDataD.Debug=Y




//删除数据
deleteDataD.Type=TSQL
deleteDataD.SQL=DELETE FROM SYS_ORDER_OPMEDD WHERE OPMED_CODE=<OPMED_CODE> AND ORDER_CODE=<ORDER_CODE>
deleteDataD.Debug=Y

//删除数据
deleteDataM.Type=TSQL
deleteDataM.SQL=DELETE FROM SYS_ORDER_OPMEDM WHERE OPMED_CODE=<OPMED_CODE> 
deleteDataM.Debug=Y

