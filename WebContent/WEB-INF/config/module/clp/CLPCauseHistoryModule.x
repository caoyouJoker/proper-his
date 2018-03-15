# 
#  Title:临床路径不进入原因module
# 
#  Description:临床
# 
#  Copyright: Copyright (c) ProperSoft 2015
# 
#  author pangben 2015-8-10
#  version 1.0
#
//临床路径类别
Module.item=insertClpCauseHistory;queryCauseHistory;queryMaxSeqNo

//插入临床路径不进入原因
insertClpCauseHistory.Type=TSQL
insertClpCauseHistory.SQL=INSERT INTO CLP_CAUSE_HISTORY( &
			CASE_NO,SEQ_NO,MR_NO,CAUSE_CODE,CAUSE_DATE,CAUSE_USER,OPT_USER,OPT_DATE,OPT_TERM) &
		VALUES( <CASE_NO>,<SEQ_NO>,<MR_NO>,<CAUSE_CODE>,SYSDATE,<CAUSE_USER>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertClpCauseHistory.Debug=N

//查询所有数据
queryCauseHistory.Type=TSQL
queryCauseHistory.SQL=SELECT CASE_NO,SEQ_NO,MR_NO,CAUSE_CODE,CAUSE_DATE,CAUSE_USER,OPT_USER,OPT_DATE,OPT_TERM FROM CLP_CAUSE_HISTORY
queryCauseHistory.item=CASE_NO;MR_NO
queryCauseHistory.CASE_NO=CASE_NO=<CASE_NO>
queryCauseHistory.MR_NO=MR_NO=<MR_NO>
queryCauseHistory.Debug=N

//查询最大的序号
queryMaxSeqNo.Type=TSQL
queryMaxSeqNo.SQL=SELECT MAX(SEQ_NO) SEQ_NO FROM CLP_CAUSE_HISTORY
queryMaxSeqNo.item=CASE_NO;MR_NO
queryMaxSeqNo.CASE_NO=CASE_NO=<CASE_NO>
queryMaxSeqNo.MR_NO=MR_NO=<MR_NO>
queryMaxSeqNo.Debug=N