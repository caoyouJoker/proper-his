###############################
# <p>Title:医师分值统计表Tool </p>
#
# <p>Description:医师分值统计表Tool </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2009-9-13
# @version 4.0
#
###############################
Module.item=selectOUT;selectIN

//查询出院患者信息
selectOUT.Type=TSQL
selectOUT.SQL=SELECT E.REGION_CHN_DESC, D.DEPT_CHN_DESC, C.STATION_DESC, B.USER_NAME,&
		       A.MR_NO, A.IPD_NO, A.PAT_NAME, 100 - A.SUMSCODE AS SCORE &
		  FROM MRO_RECORD A,&
		       SYS_OPERATOR B,&
		       SYS_STATION C,&
		       SYS_DEPT D,&
		       SYS_REGION E &
		 WHERE   A.OUT_DATE IS NOT NULL &
		   AND A.VS_DR_CODE = B.USER_ID &
		   AND A.REGION_CODE = E.REGION_CODE &
		   AND A.IN_STATION = C.STATION_CODE &
		   AND A.IN_DEPT = D.DEPT_CODE &
		   AND A.OUT_DATE BETWEEN TO_DATE (<DATE_S>, 'YYYYMMDD') &
				      AND TO_DATE (<DATE_E> || '235959', 'YYYYMMDDHH24MISS')
selectOUT.item=STATION;VS_CODE;DEPT;REGION_CODE
selectOUT.STATION=A.IN_STATION=<STATION>
//========pangben modify 20110518 start
selectOUT.REGION_CODE=E.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectOUT.VS_CODE=A.VS_DR_CODE=<VS_CODE>
selectOUT.DEPT=D.DEPT_CODE=<DEPT>
selectOUT.Debug=N

//查询在院患者信息
selectIN.Type=TSQL
selectIN.SQL=SELECT E.REGION_CHN_DESC, D.DEPT_CHN_DESC, C.STATION_DESC, B.USER_NAME,&
		       A.MR_NO, A.IPD_NO, A.PAT_NAME, 100 - A.SUMSCODE AS SCORE &
		  FROM MRO_RECORD A,&
		       SYS_OPERATOR B,&
		       SYS_STATION C,&
		       SYS_DEPT D,&
		       SYS_REGION E &
		 WHERE   A.OUT_DATE IS NULL &
		   AND A.VS_DR_CODE = B.USER_ID &
		   AND A.REGION_CODE = E.REGION_CODE &
		   AND A.IN_STATION = C.STATION_CODE &
		   AND A.IN_DEPT = D.DEPT_CODE &
		   AND A.IN_DATE BETWEEN TO_DATE (<DATE_S>, 'YYYYMMDD') &
				      AND TO_DATE (<DATE_E> || '235959', 'YYYYMMDDHH24MISS')
selectIN.item=STATION;VS_CODE;DEPT;REGION_CODE
selectIN.STATION=A.IN_STATION=<STATION>
//========pangben modify 20110518 start
selectIN.REGION_CODE=E.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectIN.VS_CODE=A.VS_DR_CODE=<VS_CODE>
selectIN.DEPT=D.DEPT_CODE=<DEPT>
selectIN.Debug=N