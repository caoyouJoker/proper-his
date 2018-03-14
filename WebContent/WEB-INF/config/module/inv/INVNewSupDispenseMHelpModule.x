   #
   # Title:供应室出库辅助
   #
   # Description:供应室出库辅助
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/06/26

Module.item=queryPackageInfo


//查询手术包信息
queryPackageInfo.Type=TSQL
queryPackageInfo.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC AS INV_CHN_DESC, PM.STATUS, PM.USE_COST + PM.ONCE_USE_COST AS COST_PRICE & 
			FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE  
queryPackageInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageInfo.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
queryPackageInfo.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageInfo.Debug=N






















