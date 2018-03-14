 #
   # Title: 药库药房
   #
   # Description:药库药房
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=getOrgCode;updateBatchFlg

//得到药房代码
getOrgCode.Type=TSQL
getOrgCode.SQL=SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME,ORG_ENG_DESC AS ENNAME ,PY1,PY2 FROM IND_ORG ORDER BY ORG_CODE,SEQ
getOrgCode.item=REGION_CODE;ORG_TYPE;ORG_FLG;EXINV_FLG;STATION_FLG;INJ_ORG_FLG;REGION_CODE_ALL
getOrgCode.REGION_CODE=REGION_CODE=<REGION_CODE>
getOrgCode.ORG_TYPE=ORG_TYPE=<ORG_TYPE>
getOrgCode.ORG_FLG=ORG_FLG=<ORG_FLG>
getOrgCode.EXINV_FLG=EXINV_FLG=<EXINV_FLG>
getOrgCode.STATION_FLG=STATION_FLG=<STATION_FLG>
getOrgCode.INJ_ORG_FLG=INJ_ORG_FLG=<INJ_ORG_FLG>
getOrgCode.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
getOrgCode.Debug=N


//更新该过账部门的IND_ORG.BATCH_FLG
updateBatchFlg.Type=TSQL
updateBatchFlg.SQL=UPDATE IND_ORG SET &
			   BATCH_FLG=<BATCH_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE ORG_CODE=<ORG_CODE> 
updateBatchFlg.Debug=N

