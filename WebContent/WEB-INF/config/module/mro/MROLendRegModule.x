###############################################
#  Title:�������ĵǼ�module
# 
#  Description:�������ĵǼ�module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.5.11
#  version 4.0
###############################################
Module.item=selectLendDay;insertQueue;selectQueue

//��ѯ����ԭ���Ӧ������
selectLendDay.Type=TSQL
selectLendDay.SQL=SELECT LEND_DAY,VALID_DAY &
			FROM MRO_LEND &
			WHERE LEND_CODE=<LEND_CODE>
selectLendDay.Debug=N

//���벡��������
insertQueue.Type=TSQL
insertQueue.SQL=INSERT INTO MRO_QUEUE ( &
                QUE_SEQ,QUE_DATE,MR_NO,IPD_NO,ADM_HOSP, &
                SESSION_CODE,REQ_DEPT,MR_PERSON,ISSUE_CODE,RTN_DATE, &
                DUE_DATE,LEND_CODE,CAN_FLG,ADM_TYPE,CASE_NO, &
                QUE_HOSP,OPT_USER,IN_DATE,IN_PERSON,OPT_DATE,OPT_TERM &
             ) &
             VALUES ( &
		<QUE_SEQ>,<QUE_DATE>,<MR_NO>,<IPD_NO>,<ADM_HOSP>, &
                <SESSION_CODE>,<REQ_DEPT>,<MR_PERSON>,<ISSUE_CODE>,<RTN_DATE>, &
                <DUE_DATE>,<LEND_CODE>,<CAN_FLG>,<ADM_TYPE>,<CASE_NO>, &
                <QUE_HOSP>,<OPT_USER>,<IN_DATE>,<IN_PERSON>,SYSDATE,<OPT_TERM> &
             )
insertQueue.Debug=N

//��ѯ�����ڽ�����Ƿ�黹
selectQueue.Type=TSQL
selectQueue.SQL=SELECT ISSUE_CODE FROM MRO_QUEUE WHERE MR_NO=<MR_NO> ORDER BY QUE_SEQ DESC
selectQueue.Debug=N