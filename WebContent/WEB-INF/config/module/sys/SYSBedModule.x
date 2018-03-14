#
# <p>Title:床位基本档 </p>
#
# <p>Description: </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: </p>
#
# @author JiaoY 2009-04-01
# @version 1.0
#
Module.item=insert;update;selectall;insertAll;updateForAdm;updateForResv;alloSysbed;queryRoomBed;initSYSBed;&
clearForadmin;queryYellowRed;clearForAlladmin;upDataOccu;checkBedStatus;clearForAdm;clearOCCUBed;IsICUBed;IsCCUBed;selectBed;selectLockBed;updateLockBed
 
//查询全字段
selectall.Type=TSQL
selectall.SQL=SELECT BED_NO,BED_NO_DESC,PY1,PY2,SEQ,DESCRIPTION,&
  		ROOM_CODE,STATION_CODE,REGION_CODE,BED_CLASS_CODE,BED_TYPE_CODE,&
  		ACTIVE_FLG,APPT_FLG,ALLO_FLG,BED_OCCU_FLG,RESERVE_BED_FLG,SEX_CODE,&
  		OCCU_RATE_FLG,DR_APPROVE_FLG,BABY_BED_FLG,HTL_FLG,ADM_TYPE,MR_NO,CASE_NO,IPD_NO,&
  		DEPT_CODE,BED_STATUS,OPT_USER,OPT_DATE,OPT_TERM &
  		FROM SYS_BED 
selectall.item=BED_NO;MR_NO;CASE_NO;IPD_NO;ROOM_CODE;STATION_CODE;BED_TYPE_CODE;APPT_FLG;DEPT_CODE
selectall.BED_NO=BED_NO=<BED_NO>
selectall.MR_NO=MR_NO=<MR_NO>
selectall.CASE_NO=CASE_NO=<CASE_NO>
selectall.IPD_NO=IPD_NO=<IPD_NO>
selectall.ROOM_CODE=ROOM_CODE=<ROOM_CODE>
selectall.STATION_CODE=STATION_CODE=<STATION_CODE>
selectall.BED_TYPE_CODE=BED_TYPE_CODE=<BED_TYPE_CODE>
selectall.APPT_FLG=APPT_FLG=<APPT_FLG>
selectall.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectall.Debug=N
 
 
 
//病患入住
updateForAdm.Type=TSQL
updateForAdm.SQL=UPDATE  SYS_BED SET APPT_FLG=<APPT_FLG>,& 
	ALLO_FLG=<ALLO_FLG>,MR_NO=<MR_NO>,CASE_NO=<CASE_NO>,IPD_NO=<IPD_NO>,BED_STATUS=<BED_STATUS>,&
 	OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 	WHERE BED_NO=<BED_NO> 
updateForAdm.Debug=N

//检索床位是否被抢
selectBed.Type=TSQL
selectBed.SQL=SELECT * FROM SYS_BED  &
 	WHERE BED_NO=<BED_NO>        &
        AND MR_NO IS NOT NULL
selectBed.Debug=N
//===========add  by  chenxi 
selectLockBed.Type=TSQL
selectLockBed.SQL=SELECT ALLO_FLG FROM SYS_BED  &
 	WHERE BED_NO=<BED_NO>        &
        AND ALLO_FLG ='T'
selectLockBed.Debug=N

updateLockBed.Type=TSQL
updateLockBed.SQL=UPDATE  SYS_BED SET ALLO_FLG ='T' & 
 	WHERE BED_NO=<BED_NO> 
updateLockBed.Debug=N

//预约床位修改 "Y":预约  "N":取消预约
updateForResv.Type=TSQL
updateForResv.SQL=UPDATE  SYS_BED SET APPT_FLG=<APPT_FLG>,& 
 	OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 	WHERE BED_NO=<BED_NO> 
updateForResv.Debug=N

//包床
alloSysbed.Type=TSQL
alloSysbed.SQL=UPDATE  SYS_BED &
 	SET ALLO_FLG=<ALLO_FLG>,BED_OCCU_FLG=<BED_OCCU_FLG>,CASE_NO=<CASE_NO>,MR_NO=<MR_NO>,BED_STATUS=<BED_STATUS>,&
 	IPD_NO=<IPD_NO>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 	WHERE BED_NO=<BED_NO> 
alloSysbed.Debug=N

//查询床位号所在的病房的说有病床
queryRoomBed.Type=TSQL
queryRoomBed.SQL=SELECT   BED_NO , ALLO_FLG,CASE_NO,IPD_NO,MR_NO &
  		FROM   SYS_BED &
 		WHERE   ROOM_CODE = (SELECT   ROOM_CODE &
                FROM   SYS_BED &
                WHERE   BED_NO = <BED_NO>)
queryRoomBed.Debug=N

//入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
clearForadmin.Type=TSQL
clearForadmin.SQL=UPDATE SYS_BED SET &
			 MR_NO='',IPD_NO='',CASE_NO='',BED_STATUS='0',BED_OCCU_FLG='N',ALLO_FLG='N' &
			 WHERE CASE_NO =<CASE_NO> 
clearForadmin.Debug=N
 
//清床（住院处安排床位）包床适用
clearForAlladmin.Type=TSQL
clearForAlladmin.SQL=UPDATE SYS_BED SET &
 			 MR_NO='',IPD_NO='',CASE_NO='',ALLO_FLG='N',BED_STATUS='0',BED_OCCU_FLG='N'  &
 			 WHERE CASE_NO =<CASE_NO> 
clearForAlladmin.Debug=N
 
//包床入住
upDataOccu.Type=TSQL
upDataOccu.SQL=UPDATE SYS_BED SET &
		 BED_OCCU_FLG='Y' &
		 WHERE CASE_NO =<CASE_NO> 
upDataOccu.Debug=N

//床位档共用combo
initSYSBed.Type=TSQL
initSYSBed.SQL=SELECT BED_NO AS ID,BED_NO_DESC AS NAME,ENG_DESC,PY1,PY2 &
		FROM SYS_BED &
		ORDER BY BED_NO,SEQ
initSYSBed.item=ROOM_CODE;STATION_CODE;REGION_CODE;BED_CLASS_CODE;BED_TYPE_CODE;ACTIVE_FLG;APPT_FLG;ALLO_FLG;BED_OCCU_FLG;RESERVE_BED_FLG;SEX_CODE;&
		OCCU_RATE_FLG;DR_APPROVE_FLG;BABY_BED_FLG;HTL_FLG;ADM_TYPE;BED_STATUS;REGION_CODE_ALL
initSYSBed.ROOM_CODE=ROOM_CODE=<ROOM_CODE>
initSYSBed.STATION_CODE=STATION_CODE=<STATION_CODE>
initSYSBed.REGION_CODE=REGION_CODE=<REGION_CODE>
initSYSBed.BED_CLASS_CODE=BED_CLASS_CODE=<BED_CLASS_CODE>
initSYSBed.BED_TYPE_CODE=BED_TYPE_CODE=<BED_TYPE_CODE>
initSYSBed.ACTIVE_FLG=ACTIVE_FLG=<ACTIVE_FLG>
initSYSBed.APPT_FLG=APPT_FLG=<APPT_FLG>
initSYSBed.ALLO_FLG=ALLO_FLG=<ALLO_FLG>
initSYSBed.BED_OCCU_FLG=BED_OCCU_FLG=<BED_OCCU_FLG>
initSYSBed.RESERVE_BED_FLG=RESERVE_BED_FLG=<RESERVE_BED_FLG>
initSYSBed.SEX_CODE=SEX_CODE=<SEX_CODE>
initSYSBed.OCCU_RATE_FLG=OCCU_RATE_FLG=<OCCU_RATE_FLG>
initSYSBed.DR_APPROVE_FLG=DR_APPROVE_FLG=<DR_APPROVE_FLG>
initSYSBed.BABY_BED_FLG=BABY_BED_FLG=<BABY_BED_FLG>
initSYSBed.HTL_FLG=HTL_FLG=<HTL_FLG>
initSYSBed.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
initSYSBed.BED_STATUS=BED_STATUS=<BED_STATUS>
initSYSBed.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initSYSBed.Debug=N

//查询红色警戒 黄色警戒
queryYellowRed.Type=TSQL
queryYellowRed.SQL=SELECT B.ROOM_CODE,A.BED_NO,B.YELLOW_SIGN,B.RED_SIGN,A.BED_NO_DESC &
 		      FROM SYS_BED A ,SYS_ROOM B &
 		     WHERE A.ROOM_CODE=B.ROOM_CODE &
 		       AND A.BED_NO=<BED_NO>
queryYellowRed.Debug=N

//差看病患是否入住
checkBedStatus.Type=TSQL
checkBedStatus.SQL=SELECT BED_STATUS,BED_NO,BED_OCCU_FLG &
 		      FROM SYS_BED &
 		     WHERE CASE_NO=<CASE_NO>
checkBedStatus.Debug=N

//出科清床 （根据CASE_NO清空床位）
clearForAdm.Type=TSQL
clearForAdm.SQL=UPDATE SYS_BED SET BED_OCCU_FLG='N',& 
			ALLO_FLG='N',MR_NO='',CASE_NO='',IPD_NO='',BED_STATUS='0',&
			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			WHERE CASE_NO=<CASE_NO> 
clearForAdm.Debug=N

//取消包床
clearOCCUBed.Type=TSQL
clearOCCUBed.SQL=UPDATE SYS_BED SET BED_OCCU_FLG='N',& 
			ALLO_FLG='N',MR_NO='',CASE_NO='',IPD_NO='',BED_STATUS='0',&
			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			WHERE CASE_NO=<CASE_NO> AND BED_OCCU_FLG='Y'
clearOCCUBed.Debug=N

//判断是否ICU注记
IsICUBed.Type=TSQL
IsICUBed.SQL=SELECT B.ICU_FLG FROM SYS_BED A,SYS_BED_TYPE B  &
             WHERE A.BED_TYPE_CODE=B.BED_TYPE_CODE  AND A.ACTIVE_FLG='Y'&
                  AND A.CASE_NO=<CASE_NO>
IsICUBed.Debug=N

//判断是否CCU注记
IsCCUBed.Type=TSQL
IsCCUBed.SQL=SELECT B.CCU_FLG FROM SYS_BED A,SYS_BED_TYPE B  &
             WHERE A.BED_TYPE_CODE=B.BED_TYPE_CODE  AND A.ACTIVE_FLG='Y'&
                  AND A.CASE_NO=<CASE_NO>
IsCCUBed.Debug=N














