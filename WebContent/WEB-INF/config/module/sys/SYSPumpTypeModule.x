# 
#  Title:泵入方式字典
# 
#  Description:泵入方式字典module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wukai 2016.05.19
#  version 1.0
#
Module.item=save;update;delete;query

//插入一条新的临床研究
save.Type=TSQL
save.SQL=INSERT INTO SYS_PUMPTYPE (PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, SEQ, OPT_USER,OPT_DATE, OPT_TERM) &
		VALUES (  &
		<PUMP_CODE>, <PUMP_DESC>, <PY1>, <PY2>, <DESCRIPTION>, <SEQ>, <OPT_USER> , & 
		<OPT_DATE>, <OPT_TERM>)
save.Debug=Y

//更新一条数据
update.Type=TSQL
update.SQL=UPDATE SYS_PUMPTYPE  & 
           SET PY1=<PY1>, PY2=<PY2>, DESCRIPTION=<DESCRIPTION>, SEQ=<SEQ> WHERE PUMP_CODE= <PUMP_CODE>
update.Debug=Y

//删除一条数据
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_PUMPTYPE WHERE PUMP_CODE=<PUMP_CODE>
delete.Debug=Y

//进行查询
query.Type=TSQL
query.SQL=SELECT PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, SEQ, OPT_USER,OPT_DATE, OPT_TERM &
	   FROM SYS_PUMPTYPE   ORDER BY SEQ
query.item=PUMP_CODE;PUMP_DESC;PY1;PY2;DESCRIPTION
query.PUMP_CODE= PUMP_CODE LIKE <PUMP_CODE>
query.PUMP_DESC= PUMP_DESC LIKE <PUMP_DESC>
query.PY1= PY1 LIKE <PY1>
query.PY2= PY2 LIKE <PY2>
query.DESCRIPTION = DESCRIPTION LIKE <DESCRIPTION>
query.Debug=Y



