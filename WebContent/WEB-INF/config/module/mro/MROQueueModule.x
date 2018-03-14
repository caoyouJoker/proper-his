###############################################
#  Title:病案出入库管理module
# 
#  Description:病案出入库管理module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.5.12
#  version 4.0
###############################################
Module.item=selectOut;updateIn_flg;updateOut;insertTRANHIS;selectTRANHIS;selectIn;selectMRO_MRV;selectMRO_MRV_TECH;selectOutHp;selectOutQueue;insertMRO_MRV;cancelQueueByCASE_NO

//病历待出库档 单表查询
selectOut.Type=TSQL
selectOut.SQL=SELECT &
		   QUE_SEQ, QUE_DATE, MR_NO,IPD_NO, ADM_HOSP, &
		   SESSION_CODE,REQ_DEPT, MR_PERSON, ISSUE_CODE,RTN_DATE, &
		   DUE_DATE, LEND_CODE,CAN_FLG, ADM_TYPE, CASE_NO, &
		   QUE_HOSP, OPT_USER, IN_DATE,IN_PERSON, OPT_DATE, &
		   OPT_TERM &
		FROM MRO_QUEUE 
selectOut.item=IPD_NO;MR_NO;ISSUE_CODE;QUE_SEQ;QUE_DATE
selectOut.IPD_NO=IPD_NO=<IPD_NO>
selectOut.MR_NO=MR_NO=<MR_NO>
selectOut.QUE_SEQ=QUE_SEQ=<QUE_SEQ>
selectOut.ISSUE_CODE=ISSUE_CODE=<ISSUE_CODE>
selectOut.QUE_DATE=QUE_DATE=TO_DATE(<QUE_DATE>,'YYYYMMDD')
selectOut.Debug=N

//病历出库
updateOut.Type=TSQL
updateOut.SQL=UPDATE MRO_QUEUE SET &
				ISSUE_CODE=<ISSUE_CODE>, &
				OPT_USER=<OPT_USER>, &
				OPT_DATE=SYSDATE, &
				OPT_TERM=<OPT_TERM> &
				WHERE QUE_SEQ=<QUE_SEQ>
updateOut.Debug=N

//修改病历主表 病历在库状态
updateIn_flg.Type=TSQL
updateIn_flg.SQL=UPDATE MRO_MRV SET &
				IN_FLG=<IN_FLG>, &
				OPT_USER=<OPT_USER>, &
				OPT_DATE=SYSDATE, &
				OPT_TERM=<OPT_TERM> &
				WHERE MR_NO=<MR_NO>
updateIn_flg.Debug=N

//插入病案借阅历史表 MRO_TRANHIS
insertTRANHIS.Type=TSQL
insertTRANHIS.SQL=INSERT INTO MRO_TRANHIS ( &
                   IPD_NO, MR_NO, QUE_DATE,TRAN_KIND, LEND_CODE, &
                   CURT_LOCATION, REGION_CODE, MR_PERSON, TRAN_HOSP, IN_DATE, &
                   IN_PERSON, OPT_USER, OPT_DATE, OPT_TERM,QUE_SEQ) &
           VALUES ( &
		   <IPD_NO>, <MR_NO>, <QUE_DATE>,<TRAN_KIND>, <LEND_CODE>, &
                   <CURT_LOCATION>, <REGION_CODE>, <MR_PERSON>, <TRAN_HOSP>, <IN_DATE>, &
                   <IN_PERSON>, <OPT_USER>, SYSDATE, <OPT_TERM>,<QUE_SEQ> &
                    )
insertTRANHIS.Debug=N

//查询病案借阅历史表 MRO_TRANHIS
selectTRANHIS.Type=TSQL
selectTRANHIS.SQL=SELECT &
		   IPD_NO, MR_NO, QUE_DATE, &
		   TRAN_KIND, LEND_CODE, CURT_LOCATION, &
		   REGION_CODE, MR_PERSON, TRAN_HOSP, &
		   IN_DATE, IN_PERSON, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		FROM MRO_TRANHIS
selectTRANHIS.item=IPD_NO;REGION_CODE;MR_NO;QUE_DATE_START;QUE_DATE_END;IN_DATE_START;IN_DATE_END
selectTRANHIS.IPD_NO=IPD_NO=<IPD_NO>
//============pangben modify 20110518 start
selectTRANHIS.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110518 stop
selectTRANHIS.MR_NO=MR_NO=<MR_NO>
selectTRANHIS.QUE_DATE_START=TO_DATE(SUBSTR(QUE_DATE,0,8),'YYYY-MM-DD')>=TO_DATE(<QUE_DATE_START>,'YYYYMMDD')
selectTRANHIS.QUE_DATE_END=TO_DATE(SUBSTR(QUE_DATE,0,8),'YYYY-MM-DD')<=TO_DATE(<QUE_DATE_END>,'YYYYMMDD')
selectTRANHIS.IN_DATE_START=TRUNC(IN_DATE)>=TO_DATE(<IN_DATE_START>,'YYYYMMDD')
selectTRANHIS.IN_DATE_END=TRUNC(IN_DATE)<=TO_DATE(<IN_DATE_END>,'YYYYMMDD')
selectTRANHIS.Debug=N

//查询病历主档
selectMRO_MRV.Type=TSQL
selectMRO_MRV.SQL=SELECT &
		   MR_NO, IPD_NO, CREATE_HOSP, &
		   IN_FLG, CURT_HOSP, CURT_LOCATION, &
		   TRAN_HOSP, BOX_CODE, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		FROM MRO_MRV
selectMRO_MRV.item=MR_NO;IPD_NO;IN_FLG
selectMRO_MRV.MR_NO=MR_NO=<MR_NO>
selectMRO_MRV.IPD_NO=IPD_NO=<IPD_NO>
selectMRO_MRV.IN_FLG=IN_FLG=<IN_FLG>
selectMRO_MRV.Debug=N

//查询病历是否归档
selectMRO_MRV_TECH.Type=TSQL
selectMRO_MRV_TECH.SQL=SELECT * FROM MRO_MRV_TECH WHERE MR_NO=<MR_NO>
selectMRO_MRV_TECH.Debug=N

//插入病历主档
insertMRO_MRV.Type=TSQL
insertMRO_MRV.SQL=INSERT INTO MRO_MRV( &
			    MR_NO, IPD_NO, CREATE_HOSP, &
			    IN_FLG, CURT_HOSP, CURT_LOCATION, &
			    TRAN_HOSP, BOX_CODE, OPT_USER, &
			    OPT_DATE, OPT_TERM) &
			VALUES( &
			    <MR_NO>, <IPD_NO>, <CREATE_HOSP>, &
			    <IN_FLG>, <CURT_HOSP>, <CURT_LOCATION>, &
			    <TRAN_HOSP>, <BOX_CODE>, <OPT_USER>, &
			    SYSDATE, <OPT_TERM>)
insertMRO_MRV.Debug=N

//病历待入库信息查询
selectIn.Type=TSQL
selectIn.SQL=SELECT A.MR_NO,A.IPD_NO, B.QUE_SEQ, B.QUE_DATE, B.ADM_HOSP, &
		       B.SESSION_CODE,B.REQ_DEPT, B.MR_PERSON, B.ISSUE_CODE,B.RTN_DATE, &
		       B.DUE_DATE, B.LEND_CODE,B.CAN_FLG, B.ADM_TYPE, B.CASE_NO, &
		       B.QUE_HOSP, B.OPT_USER, B.IN_DATE,B.IN_PERSON, B.OPT_DATE, &
		       B.OPT_TERM &
		FROM MRO_MRV A,MRO_QUEUE B &
		WHERE A.MR_NO=B.MR_NO(+) &
		AND A.IPD_NO=B.IPD_NO(+) &
		AND A.IN_FLG='1' &
		AND B.ISSUE_CODE='1' &
		ORDER BY QUE_SEQ DESC
selectIn.item=IPD_NO;MR_NO
selectIn.IPD_NO=A.IPD_NO=<IPD_NO>
selectIn.MR_NO=A.MR_NO=<MR_NO>
selectIn.Debug=N

//查询出院病历归档（页签3使用）
selectOutHp.Type=TSQL
selectOutHp.SQL=SELECT A.MR_NO,A.IPD_NO,B.DEPT_CODE,B.VS_DR_CODE,C.PAT_NAME,B.DS_DATE &
		FROM MRO_MRV A,ADM_INP B,SYS_PATINFO C &
		WHERE A.MR_NO=B.MR_NO &
		AND A.IPD_NO=B.IPD_NO &
		AND A.MR_NO = C.MR_NO &
		AND B.DS_DATE IS NOT NULL &
		AND A.IN_FLG='2'
		ORDER BY DS_DATE
selectOutHp.item=MR_NO;DS_DATE;REGION_CODE
selectOutHp.MR_NO=A.MR_NO=<MR_NO>
//===============pangben modify 20110518 start
selectOutHp.REGION_CODE=B.REGION_CODE=<REGION_CODE>
//===============pangben modify 20110518 stop
selectOutHp.DS_DATE=TRUNC(B.DS_DATE)=TO_DATE(<DS_DATE>,'YYYYMMDD')
selectOutHp.Debug=N

//查询出库病历信息（页签3使用）
selectOutQueue.Type=TSQL
selectOutQueue.SQL=SELECT A.MR_NO,A.IPD_NO,B.DEPT_CODE,B.VS_DR_CODE,C.PAT_NAME, &
			B.DS_DATE,A.QUE_SEQ,A.QUE_DATE,A.ADM_HOSP,A.MR_PERSON, &
			A.REQ_DEPT,A.ISSUE_CODE,A.RTN_DATE,A.DUE_DATE,A.LEND_CODE, &
			A.CAN_FLG,A.ADM_TYPE,A.CASE_NO,A.IN_DATE,A.IN_PERSON &
			FROM MRO_QUEUE A,ADM_INP B,SYS_PATINFO C &
			WHERE A.MR_NO=B.MR_NO &
			AND A.IPD_NO=B.IPD_NO &
			AND A.MR_NO=C.MR_NO &
			ORDER BY A.QUE_DATE 
selectOutQueue.item=ISSUE_CODE;LEND_CODE;RTN_DATE;REQ_DEPT;MR_PERSON;REGION_CODE
selectOutQueue.ISSUE_CODE=A.ISSUE_CODE=<ISSUE_CODE>
//===============pangben modify 20110518 start
selectOutQueue.REGION_CODE=B.REGION_CODE=<REGION_CODE>
//===============pangben modify 20110518 stop
selectOutQueue.LEND_CODE=A.LEND_CODE=<LEND_CODE>
selectOutQueue.RTN_DATE=TRUNC(A.RTN_DATE)=TO_DATE(<RTN_DATE>,'YYYYMMDD')
selectOutQueue.REQ_DEPT=B.DEPT_CODE=<REQ_DEPT>
selectOutQueue.MR_PERSON=A.MR_PERSON=<MR_PERSON>
selectOutQueue.Debug=N


//根据CASE_NO取消待出库病历(住院登记取消时使用)
cancelQueueByCASE_NO.Type=TSQL
cancelQueueByCASE_NO.SQL=UPDATE MRO_QUEUE SET CAN_FLG='Y',&
				OPT_USER=<OPT_USER>,&
				OPT_DATE=SYSDATE,&
				OPT_TERM=<OPT_TERM> WHERE CASE_NO=<CASE_NO>
cancelQueueByCASE_NO.Debug=N
