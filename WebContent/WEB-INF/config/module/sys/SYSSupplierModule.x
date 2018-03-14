# 
#  Title:供应厂商module
# 
#  Description:供应厂商module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=initSupCode

//共用combo
initSupCode.Type=TSQL
initSupCode.SQL=SELECT SUP_CODE AS ID,SUP_ABS_DESC AS NAME,SUP_ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_SUPPLIER  ORDER BY SEQ,SUP_CODE
initSupCode.item=PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG;SUP_STOP_FLG
initSupCode.PHA_FLG=PHA_FLG=<PHA_FLG>
initSupCode.MAT_FLG=MAT_FLG=<MAT_FLG>
initSupCode.DEV_FLG=DEV_FLG=<DEV_FLG>
initSupCode.OTHER_FLG=OTHER_FLG=<OTHER_FLG>
initSupCode.SUP_STOP_FLG=SUP_STOP_FLG=<SUP_STOP_FLG>
initSupCode.Debug=N

//取得供应厂商订购状态
selectdata.Type=TSQL
selectdata.SQL=SELECT PHA_FLG, MAT_FLG,DEV_FLG, OTHER_FLG, SUP_STOP_FLG, SUP_STOP_DATE, &
		      SUP_END_DATE &
		 FROM SYS_SUPPLIER  &
		WHERE SUP_CODE=<SUP_CODE> &
	        ORDER BY SEQ 
selectdata.Debug=N

