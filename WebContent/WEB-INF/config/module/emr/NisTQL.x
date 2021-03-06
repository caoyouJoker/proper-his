Module.item=selectNis;deleteNis;saveNis;updateNis;getDict;updateAdmInp;getNo;updateDr

selectNis.Type=TSQL
selectNis.SQL=SELECT * FROM EMR_EVALUTION_RECORD WHERE NIS_ID=<NIS_ID> AND SOURCE='0'
selectNis.Debug=Y

deleteNis.Type=TSQL
deleteNis.SQL=DELETE FROM EMR_EVALUTION_RECORD WHERE NIS_ID=<NIS_ID> AND SOURCE='0'
deleteNis.Debug=Y

saveNis.Type=TSQL
saveNis.SQL=INSERT INTO EMR_EVALUTION_RECORD(EVALUTION_ID,CASE_NO,EVALUTION_CODE,EVALUTION_DESC,SOURCE,EVALUTION_CLASS,NIS_ID,WARNING_FLG,SCORE,EVALUTION_DATE,SCORE_DECS,FILE_PATH,OPT_TERM,OPT_USER,OPT_DATE) VALUES(<EVALUTION_ID>,<CASE_NO>,<EVALUTION_CODE>,<EVALUTION_DESC>,<SOURCE>,<EVALUTION_CLASS>,<NIS_ID>,<WARNING_FLG>,<SCORE>,TO_DATE(<EVALUTION_DATE>,'yyyy/MM/dd HH24:mi:ss'),<SCORE_DECS>,<FILE_PATH>,<OPT_TERM>,<OPT_USER>,sysdate)
saveNova.Debug=Y

updateNis.Type=TSQL
updateNis.SQL=UPDATE EMR_EVALUTION_RECORD &
SET CASE_NO=<CASE_NO>, &
EVALUTION_CODE=<EVALUTION_CODE>, &
EVALUTION_DESC=<EVALUTION_DESC>, &
WARNING_FLG=<WARNING_FLG>, &
SCORE=<SCORE>, &
EVALUTION_DATE=TO_DATE(<EVALUTION_DATE>,'yyyy/MM/dd HH24:mi:ss'), &
SCORE_DECS=<SCORE_DECS>, &
OPT_DATE=sysdate WHERE SOURCE='0' AND NIS_ID=<NIS_ID>
updateNis.Debug=Y

getDict.Type=TSQL
getDict.SQL=SELECT LOGIC1,SCORE1,SCORE_DESC,EVALUTION_DESC FROM SYS_EVALUTION_DICT WHERE EVALUTION_CODE=<EVALUTION_CODE>
getDict.Debug=Y


updateAdmInp.Type=TSQL
updateAdmInp.SQL=UPDATE ADM_INP SET FALL_RISK=<FALL_RISK> WHERE CASE_NO=<CASE_NO>
updateAdmInp.Debug=Y

//ȡ��ԭ��
getNo.Type=TSQL
getNo.SQL={call SYSGETNO(<REGION_CODE>,<SYSTEM_CODE>,<OPERATION>,<SECTION>,?)}
getNo.OutType=NO:VARCHAR
getNo.Debug=N
//
updateDr.Type=TSQL
updateDr.SQL=UPDATE EMR_EVALUTION_RECORD &
SET SCORE=<SCORE>, &
EVALUTION_DATE=TO_DATE(<EVALUTION_DATE>,'yyyy/MM/dd HH24:mi:ss'), &
OPT_DATE=sysdate WHERE EVALUTION_ID=<EVALUTION_ID>
updateNis.Debug=Y
