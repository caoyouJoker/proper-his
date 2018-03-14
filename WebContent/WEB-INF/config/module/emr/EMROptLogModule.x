# 
#  Title:电子病历操作日志module
# 
#  Description:临床
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author zhangjg 2011.04.21
#  version 1.0
#
Module.item=writeOptLog;queryOptLog;deleteOptLog

#写日志
writeOptLog.Type=TSQL
writeOptLog.SQL=INSERT INTO EMR_OPTLOG &
      (CASE_NO, &
       FILE_SEQ, &
       OPT_SEQ, &
       OPT_TYPE, &
       MR_NO, &
       IPD_NO, &
       FILE_NAME, &
       DEPT_CODE, &
       STATION_CODE, &
       BED_NO, &
       OPT_USER, &
       OPT_DATE, &
       OPT_TERM, &
       PAT_NAME) &
    VALUES &
      (<CASE_NO>, &
       <FILE_SEQ>, &
       <OPT_SEQ>, &
       <OPT_TYPE>, &
       <MR_NO>, &
       <IPD_NO>, &
       <FILE_NAME>, &
       <DEPT_CODE>, &
       <STATION_CODE>, &
       <BED_NO>, &
       <OPT_USER>, &
       TO_DATE(<OPT_DATE>, 'YYYY/MM/DD HH24:MI:SS'), &
       <OPT_TERM>, &
       <PAT_NAME>)
writeOptLog.Debug=N

#查询日志
queryOptLog.Type=TSQL
queryOptLog.SQL=SELECT 'N' AS SEL_FLG,A.OPT_TYPE,A.MR_NO,A.IPD_NO,A.CASE_NO,A.PAT_NAME,A.FILE_NAME,A.FILE_SEQ,A.OPT_SEQ,A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.OPT_USER,TO_CHAR(A.OPT_DATE, 'YYYY/MM/DD HH24:MI:SS') AS OPT_DATE,A.OPT_TERM FROM EMR_OPTLOG A,EMR_FILE_INDEX B WHERE A.CASE_NO=B.CASE_NO(+) AND A.FILE_SEQ=B.FILE_SEQ(+) ORDER BY A.OPT_DATE
queryOptLog.item=MR_NO;IPD_NO;CASE_NO;PAT_NAME;CLASS_CODE;DEPT_CODE;STATION_CODE;BED_NO;OPT_USER;OPT_TYPE;OPT_DATE_BEGIN;OPT_DATE_END;
queryOptLog.MR_NO=A.MR_NO = <MR_NO> 
queryOptLog.IPD_NO=A.IPD_NO = <IPD_NO> 
queryOptLog.CASE_NO=A.CASE_NO = <CASE_NO> 
queryOptLog.PAT_NAME=A.PAT_NAME = <PAT_NAME> 
queryOptLog.CLASS_CODE=B.CLASS_CODE = <CLASS_CODE> 
queryOptLog.DEPT_CODE=A.DEPT_CODE = <DEPT_CODE> 
queryOptLog.STATION_CODE=A.STATION_CODE = <STATION_CODE> 
queryOptLog.BED_NO=A.BED_NO = <BED_NO> 
queryOptLog.OPT_USER=A.OPT_USER = <OPT_USER> 
queryOptLog.OPT_TYPE=A.OPT_TYPE = <OPT_TYPE> 
queryOptLog.OPT_DATE_BEGIN=A.OPT_DATE >= TO_DATE(<OPT_DATE_BEGIN>, 'YYYY/MM/DD HH24:MI:SS')
queryOptLog.OPT_DATE_END=A.OPT_DATE <= TO_DATE(<OPT_DATE_END>, 'YYYY/MM/DD HH24:MI:SS')
queryOptLog.Debug=N

#删除日志
deleteOptLog.Type=TSQL
deleteOptLog.SQL=DELETE FROM EMR_OPTLOG WHERE CASE_NO = <CASE_NO> AND FILE_SEQ = <FILE_SEQ> AND OPT_SEQ = <OPT_SEQ>
deleteOptLog.Debug=N