# 
#  Title:医保执行操作时在途状态管控module
# 
#  Description:医保执行操作时在途状态管控module 操作 INS_RUN
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-12-29
#  version 1.0
#
Module.item=queryInsRun;updateInsRun;insertInsRun;deleteInsRun;deleteInsRunConcel

//更新
updateInsRun.Type=TSQL
updateInsRun.SQL=UPDATE INS_RUN SET STUTS=<STUTS> ,OPT_USER=<OPT_USER>,OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE &
		WHERE CASE_NO=<CASE_NO> AND EXE_USER=<EXE_USER> AND EXE_TERM=<EXE_TERM> AND EXE_TYPE=<EXE_TYPE>
updateInsRun.Debug=N
//插入
insertInsRun.Type=TSQL
insertInsRun.SQL=INSERT INTO INS_RUN(CASE_NO, STUTS, EXE_USER, &
		   EXE_TERM, OPT_USER, OPT_DATE, &
		   OPT_TERM, EXE_TYPE, EXE_WAY )&
		   VALUES(<CASE_NO>, <STUTS>, <EXE_USER>, &
		   <EXE_TERM>, <OPT_USER>, SYSDATE, &
		   <OPT_TERM>, <EXE_TYPE>, <EXE_WAY>)
insertInsRun.Debug=N
//删除
deleteInsRun.Type=TSQL
deleteInsRun.SQL=DELETE INS_RUN WHERE CASE_NO=<CASE_NO> AND EXE_USER=<EXE_USER> AND EXE_TERM=<EXE_TERM> AND EXE_TYPE=<EXE_TYPE>
deleteInsRun.Debug=N
//删除对账使用======pangben 2013-7-30 
deleteInsRunConcel.Type=TSQL
deleteInsRunConcel.SQL=DELETE INS_RUN WHERE CASE_NO=<CASE_NO> AND EXE_TYPE=<EXE_TYPE>
deleteInsRunConcel.Debug=N
//查询
queryInsRun.Type=TSQL
queryInsRun.SQL=SELECT CASE_NO,STUTS, EXE_USER, &
		   EXE_TERM, OPT_USER, OPT_DATE, &
		   OPT_TERM, EXE_TYPE, EXE_WAY &
		   FROM INS_RUN 
queryInsRun.item=CASE_NO;EXE_USER;EXE_TERM;EXE_TYPE
queryInsRun.CASE_NO=CASE_NO=<CASE_NO>
queryInsRun.EXE_USER=EXE_USER=<EXE_USER>
queryInsRun.EXE_TERM=EXE_TERM=<EXE_TERM>
queryInsRun.EXE_TYPE=EXE_TYPE=<EXE_TYPE>
queryInsRun.STUTS=STUTS=<STUTS>
queryInsRun.Debug=N

