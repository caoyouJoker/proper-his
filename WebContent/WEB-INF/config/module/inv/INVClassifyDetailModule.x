Module.item=selectdata

//查询
selectdata.Type=TSQL
selectdata.SQL=SELECT distinct B.INV_CODE, &  
         C.INV_CHN_DESC, &
         C.DESCRIPTION, &     
         G.MAN_CHN_DESC  AS MAN_CHN_DESC, &   
         SF.OWN_PRICE AS COST_PRICE, &
         R.CONTRACT_PRICE, &     
         H.CATEGORY_CODE AS CATEGORY_CODE &  
    FROM INV_VERIFYINM A, &
         INV_VERIFYIND B, &  
         INV_BASE C, &     
         SYS_UNIT E, &   
         INV_AGENT R, &    
         SYS_MANUFACTURER G, &
         SYS_FEE SF, &
         SYS_CATEGORY H &  
   WHERE     A.VERIFYIN_NO = B.VERIFYIN_NO  & 
         AND B.BILL_UNIT = E.UNIT_CODE &   
         AND A.SUP_CODE = R.SUP_CODE &   
         AND B.INV_CODE = C.INV_CODE &  
         AND B.INV_CODE = R.INV_CODE & 
         AND C.MAN_CODE = G.MAN_CODE  & 
         AND C.ORDER_CODE = SF.ORDER_CODE  &
         AND H.CATEGORY_CODE=C.INVTYPE_CODE &
         AND h.rule_type ='INV_BASE'
//ORDER BY C.INV_CHN_DESC,G.MAN_CODE,B.INV_CODE 
selectdata.item=CATEGORY_CODE;SUP_CODE
selectdata.CATEGORY_CODE=CATEGORY_CODE=<CATEGORY_CODE>   
selectdata.SUP_CODE=A.SUP_CODE=<SUP_CODE>   
selectdata.Debug=Y


//查询手术包明细
queryPackageDetailInfo.Type=TSQL
queryPackageDetailInfo.SQL=SELECT PD.ORG_CODE, PD.PACK_CODE, PD.PACK_SEQ_NO, PD.INV_CODE, PD.INVSEQ_NO, & 
			PD.COST_PRICE, PD.RECOUNT_TIME, PD.STOCK_UNIT, PD.ONCE_USE_FLG, PD.QTY, IB.INV_ABS_DESC AS INV_CHN_DESC,IB.INVKIND_CODE & 
			FROM INV_PACKSTOCKD PD LEFT JOIN INV_BASE IB ON PD.INV_CODE = IB.INV_CODE 
queryPackageDetailInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageDetailInfo.PACK_CODE=PD.PACK_CODE=<PACK_CODE>   
queryPackageDetailInfo.PACK_SEQ_NO=PD.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageDetailInfo.Debug=N     