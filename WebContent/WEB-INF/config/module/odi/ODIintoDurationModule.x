#
# Title:进入时程
#
# Description:进入时程
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/06/13
Module.item=updateDuration;isDurationEnd
#
updateDuration.Type=TSQL
updateDuration.SQL=UPDATE CLP_THRPYSCHDM_REAL SET SCHD_DAY = ROUND(TO_NUMBER(TO_DATE(<CUR_DATE>,'YYYYMMDDHH24MISS')-START_DATE)),&
									 END_DATE=TO_DATE(<CUR_DATE>,'YYYYMMDDHH24MISS')&
									 WHERE CLNCPATH_CODE=<CLNC_PATHCODE> AND SCHD_CODE=<CURRENT_DURATION> AND CASE_NO=<CASE_NO>
updateDuration.Debug=N

isDurationEnd.Type=TSQL
isDurationEnd.SQL=SELECT CASE WHEN END_DATE IS NULL THEN 1 ELSE 2 END AS ISEND FROM CLP_THRPYSCHDM_REAL &
									WHERE CLNCPATH_CODE=<CLNC_PATHCODE> AND SCHD_CODE=<CURRENT_DURATION> AND CASE_NO=<CASE_NO>
isDurationEnd.Debug=N