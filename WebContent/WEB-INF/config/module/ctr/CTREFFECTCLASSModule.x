# 
#  Title:交互作用影响等级
# 
#  Description:交互作用影响等级
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author shibl   2011.6.24
#  version 1.0
#
Module.item=query;insert;update;delete

//查询表单
query.Type=TSQL
query.SQL= SELECT EFFECTCLAS_CODE, CHN_DESC, ENG_DESC, PY1, PY2, &
    SEQ, DESCRIPTION, CTRL_TYPE, MESSAGE_TEXT FROM PHA_EFFECTCLASS ORDER BY SEQ
query.item=EFFECTCLAS_CODE
query.EFFECTCLAS_CODE=TRIM(EFFECTCLAS_CODE)=<EFFECTCLAS_CODE>
query.Debug=N

//插入表单
insert.Type=TSQL
insert.SQL=INSERT INTO PHA_EFFECTCLASS &
            		  (EFFECTCLAS_CODE, CHN_DESC, ENG_DESC, PY1, PY2, &
    SEQ, DESCRIPTION, CTRL_TYPE, MESSAGE_TEXT, OPT_USER, &
    OPT_DATE, OPT_TERM) &
     		   VALUES (<EFFECTCLAS_CODE>,<CHN_DESC>,<ENG_DESC>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,&
             		   <CTRL_TYPE>, <MESSAGE_TEXT>,<OPT_USER>, SYSDATE, <OPT_TERM>)
insert.Debug=N

//更新表单
update.Type=TSQL
update.SQL=UPDATE PHA_EFFECTCLASS SET &
			  CHN_DESC=<CHN_DESC>,ENG_DESC=<ENG_DESC>,&
PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,CTRL_TYPE=<CTRL_TYPE>,MESSAGE_TEXT=<MESSAGE_TEXT>, &
			  OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
	            WHERE TRIM(EFFECTCLAS_CODE)=<EFFECTCLAS_CODE> 
update.Debug=N

//删除表单
delete.Type=TSQL
delete.SQL=DELETE FROM PHA_EFFECTCLASS WHERE TRIM(EFFECTCLAS_CODE)=<EFFECTCLAS_CODE> 
delete.Debug=N