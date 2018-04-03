锘�##############################################
# <p>Title:浠ｈ浆鐥呮偅妗� </p>
#
# <p>Description:浠ｈ浆鐥呮偅妗� </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 4.0
##############################################
Module.item=selectall;insert;selpatInfo;selAdmPat;deleteIn;insertForInOutDept

//鏌ヨ
selectall.Type=TSQL
selectall.SQL=SELECT CASE_NO , MR_NO,IPD_NO ,WAIT_DATE,&
 		OUT_DEPT_CODE,OUT_STATION_CODE,IN_DEPT_CODE,IN_STATION_CODE &
 		FROM ADM_WAIT_TRANS  
selectall.item=CASE_NO
selectall.CASE_NO=CASE_NO=<CASE_NO>
selectall.Debug=N
 
//浣忛櫌鐧昏鏃舵彃鍏�
insert.Type=TSQL
insert.SQL=INSERT INTO ADM_WAIT_TRANS  &
 	(CASE_NO,MR_NO,IPD_NO,IN_DEPT_CODE,IN_STATION_CODE,&
 	OPT_USER,OPT_DATE,OPT_TERM) &
 	VALUES (<CASE_NO>,<MR_NO>,<IPD_NO>,<DEPT_CODE>,<STATION_CODE>,&
 	<OPT_USER>,SYSDATE,<OPT_TERM>) 
insert.Debug=N

//杞鏃舵彃鍏�
insertForInOutDept.Type=TSQL
insertForInOutDept.SQL=INSERT INTO ADM_WAIT_TRANS  &
 	(CASE_NO,MR_NO,IPD_NO,OUT_BED_NO,IN_DEPT_CODE,IN_STATION_CODE,&
 	OUT_DEPT_CODE,OUT_STATION_CODE ,&
 	OPT_USER,OPT_DATE,OPT_TERM) &
 	VALUES (<CASE_NO>,<MR_NO>,<IPD_NO>,<BED_NO>,<IN_DEPT_CODE>,<IN_STATION_CODE>,&
 	<OUT_DEPT_CODE>,<OUT_STATION_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>) 
insertForInOutDept.Debug=N

//鏌ヨ寰呰浆鍏ヤ紶鍑虹梾鎮ｄ俊鎭�
selpatInfo.Type=TSQL
selpatInfo.SQL=SELECT C.DEPT_CODE AS IN_DEPT_CODE, B.IN_STATION_CODE AS IN_STATION_CODE, A.MR_NO , A.PAT_NAME,A.BIRTH_DATE , A.SEX_CODE &
 		FROM SYS_PATINFO A,ADM_WAIT_TRANS B,SYS_DEPT C &
 		WHERE A.MR_NO=B.MR_NO AND B.IN_DEPT_CODE=C.DEPT_CODE
//=======pangben modify 20110516 start		
selpatInfo.item=REGION_CODE
selpatInfo.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//=======pangben modify 20110516 stop
selpatInfo.Debug=N

//鏌ヨ鍦ㄩ櫌鐥呮偅
selAdmPat.Type=TSQL
selAdmPat.SQL=SELECT A.MR_NO , A.PAT_NAME,A.BIRTH_DATE , A.SEX_CODE &
 		FROM SYS_PATINFO A,ADM_INP B &
 		WHERE A.MR_NO=B.MR_NO
//=======pangben modify 20110516 start		
selAdmPat.item=REGION_CODE
selAdmPat.REGION_CODE=B.REGION_CODE=<REGION_CODE>
//=======pangben modify 20110516 stop		
selAdmPat.Debug=N

//鏍规嵁CASE_NO鍒犻櫎
deleteIn.Type=TSQL
deleteIn.SQL=DELETE FROM ADM_WAIT_TRANS WHERE  CASE_NO=<CASE_NO>
deleteIn.Debug=N