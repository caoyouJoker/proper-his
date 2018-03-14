# 
#  Title:剂型module
# 
#  Description:剂型module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=initPHADoseCode

//共用combo
initPHADoseCode.Type=TSQL
initPHADoseCode.SQL=SELECT DOSE_CODE AS ID,DOSE_CHN_DESC AS NAME,PY1,PY2 FROM PHA_DOSE ORDER BY DOSE_CODE,SEQ
initPHADoseCode.item=DOSE_TYPE
initPHADoseCode.DOSE_TYPE=DOSE_TYPE=<DOSE_TYPE>
initPHADoseCode.Debug=N

