# 
#  Title:药库原因档module
# 
#  Description:药库原因档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.12
#  version 1.0
#
Module.item=initINDReasonCode

//科室共用combo
initINDReasonCode.Type=TSQL
initINDReasonCode.SQL=SELECT REASON_CODE AS ID,REASON_CHN_DESC AS NAME,REASON_ENG_DESC AS ENNAME,PY1,PY2 &
		   FROM IND_REASON &
	       ORDER BY REASON_CODE,SEQ
initINDReasonCode.item=REASON_TYPE
initINDReasonCode.REASON_TYPE=REASON_TYPE=<REASON_TYPE>
initINDReasonCode.Debug=N