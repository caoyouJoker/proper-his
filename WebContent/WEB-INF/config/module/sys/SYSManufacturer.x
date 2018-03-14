# 
#  Title:生产厂商module
# 
#  Description:生产厂商module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=initSYSManufacturerCode

//共用combo
initSYSManufacturerCode.Type=TSQL
initSYSManufacturerCode.SQL=SELECT MAN_CODE AS ID,MAN_CHN_DESC AS NAME,MAN_ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_MANUFACTURER ORDER BY MAN_CODE,SEQ
initSYSManufacturerCode.item=PHA_FLG;MAT_FLG;DEV_FLG;OTHER_FLG;SUP_STOP_FLG
initSYSManufacturerCode.PHA_FLG=PHA_FLG=<PHA_FLG>
initSYSManufacturerCode.MAT_FLG=MAT_FLG=<MAT_FLG>
initSYSManufacturerCode.DEV_FLG=DEV_FLG=<DEV_FLG>
initSYSManufacturerCode.OTHER_FLG=OTHER_FLG=<OTHER_FLG>
initSYSManufacturerCode.SUP_STOP_FLG=SUP_STOP_FLG=<SUP_STOP_FLG>
initSYSManufacturerCode.Debug=N
