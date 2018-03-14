# 
#  Title:科室病种溢出管理module
# 
#  Description:科室病种溢出管理
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-07-07
#  version 1.0
#
Module.item=selectManagem;selectManagemHistory
//(fux modify 20120627  将MRO_RECORD表替代为ADM_INP表)
selectManagem.Type=TSQL
selectManagem.SQL=SELECT R.REGION_CHN_ABN,ST.STATION_DESC,A.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE,&
                  D.DEPT_CHN_DESC,COUNT(C.CLNCPATH_CODE) AS SUM,0 AS HISTORYSUM,'0.00%' AS OVERBATILITY &
                     FROM CLP_BSCINFO A, CLP_MANAGEM C,SYS_DEPT D,ADM_INP MR,SYS_STATION ST,SYS_REGION R &
                  WHERE MR.CASE_NO=C.CASE_NO AND A.CLNCPATH_CODE=C.CLNCPATH_CODE &
                        AND ST.STATION_CODE=MR.STATION_CODE &
                        AND MR.DEPT_CODE=D.DEPT_CODE AND C.REGION_CODE=R.REGION_CODE(+) &
                        AND MR.DS_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
                        AND MR.DS_DATE IS NOT NULL &
                  GROUP BY A.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE,D.DEPT_CHN_DESC,ST.STATION_DESC,R.REGION_CHN_ABN &
                  ORDER BY D.DEPT_CHN_DESC,ST.STATION_DESC,A.CLNCPATH_CODE
selectManagem.item=REGION_CODE
selectManagem.REGION_CODE=C.REGION_CODE=<REGION_CODE>
selectManagem.Debug=N

selectManagemHistory.Type=TSQL
selectManagemHistory.SQL=SELECT ST.STATION_DESC,A.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE,&
                         D.DEPT_CHN_DESC,COUNT(B.CLNCPATH_CODE) AS HISTORYSUM,R.REGION_CHN_ABN  &
                             FROM CLP_BSCINFO A,CLP_MANAGEM_HISTORY B,SYS_DEPT D,ADM_INP MR,SYS_STATION ST,SYS_REGION R &
                         WHERE MR.CASE_NO=B.CASE_NO AND A.CLNCPATH_CODE=B.CLNCPATH_CODE &
                            AND MR.DEPT_CODE=D.DEPT_CODE AND B.REGION_CODE=R.REGION_CODE(+) &
                            AND ST.STATION_CODE=MR.STATION_CODE &
                            AND MR.DS_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS') &
                            AND MR.DS_DATE IS NOT NULL AND B.END_DTTM IS NOT NULL  &
                         GROUP BY A.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE,D.DEPT_CHN_DESC,ST.STATION_DESC, R.REGION_CHN_ABN &
                         ORDER BY D.DEPT_CHN_DESC,ST.STATION_DESC,A.CLNCPATH_CODE
selectManagemHistory.item=REGION_CODE
selectManagemHistory.REGION_CODE=B.REGION_CODE=<REGION_CODE>
selectManagemHistory.Debug=N
