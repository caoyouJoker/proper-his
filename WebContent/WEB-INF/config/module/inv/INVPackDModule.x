Module.item=getPackD;getPackType

//根据包号查找明细GYSUsed
getPackD.Type=TSQL
getPackD.SQL=SELECT * FROM INV_PACKD  
getPackD.item=PACK_CODE
getPackD.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackD.Debug=N


//查找手术包明细的使用方式GYSUsed
getPackType.Type=TSQL
getPackType.SQL=SELECT * FROM INV_PACKD
getPackType.item=PACK_CODE;INV_CODE
getPackType.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackType.INV_CODE=INV_CODE=<INV_CODE>
getPackType.Debug=N






