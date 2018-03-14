# 
#  Title:病房module
# 
#  Description:病房module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.09.22
#  version 1.0
#
Module.item=initroomcode;selectdata

//共用combo
initroomcode.Type=TSQL
initroomcode.SQL=SELECT ROOM_CODE AS ID,ROOM_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
		   FROM SYS_ROOM &
	       ORDER BY SEQ,ROOM_CODE
initroomcode.item=STATION_CODE;REGION_CODE;SEX_LIMIT_FLG;RED_SIGN;YELLOW_SIGN;REGION_CODE_ALL
initroomcode.STATION_CODE=STATION_CODE=<STATION_CODE>
initroomcode.REGION_CODE=REGION_CODE=<REGION_CODE>
initroomcode.SEX_LIMIT_FLG=SEX_LIMIT_FLG=<SEX_LIMIT_FLG>
initroomcode.RED_SIGN=RED_SIGN=<RED_SIGN>
initroomcode.YELLOW_SIGN=YELLOW_SIGN=<YELLOW_SIGN>
initroomcode.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initroomcode.Debug=N

//根据病房号取得病房信息
selectdata.Type=TSQL
selectdata.SQL=SELECT ROOM_CODE, ROOM_DESC, PY1, PY2, SEQ, &
		      DESCRIPT, STATION_CODE, REGION_CODE, SEX_LIMIT_FLG, RED_SIGN, &
		      YELLOW_SIGN, OPT_USER, OPT_DATE, OPT_TERM &
		 FROM SYS_ROOM &
		WHERE ROOM_CODE=<ROOM_CODE> &
	        ORDER BY SEQ 
selectdata.Debug=N


