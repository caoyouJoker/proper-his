   #
   # Title:申请主档
   #
   # Description:申请主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=createRequestM;queryRequestM;updateRequestM;deleteRequestM;queryRequestMOut;updateFinalFlg


//新建申请主档
createRequestM.Type=TSQL
createRequestM.SQL=INSERT INTO INV_REQUESTM( &
			REQUEST_NO , REQUEST_TYPE , REQUEST_DATE, FROM_ORG_CODE , TO_ORG_CODE ,&
			REN_CODE , URGENT_FLG , REMARK, FINAL_FLG , &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<REQUEST_NO> ,  <REQUEST_TYPE> , <REQUEST_DATE> , <FROM_ORG_CODE> , <TO_ORG_CODE> , &
	    	   	<REN_CODE> ,  <URGENT_FLG> , <REMARK> , <FINAL_FLG> , &
	    	   	<OPT_USER> , SYSDATE , <OPT_TERM> )
createRequestM.Debug=N


//更新申请主档
updateRequestM.Type=TSQL
updateRequestM.SQL=UPDATE INV_REQUESTM SET &
			REQUEST_TYPE=<REQUEST_TYPE>, REQUEST_DATE=<REQUEST_DATE>, &
			FROM_ORG_CODE=<FROM_ORG_CODE>, TO_ORG_CODE=<TO_ORG_CODE>, REN_CODE=<REN_CODE>, &
			URGENT_FLG=<URGENT_FLG>, REMARK=<REMARK>, FINAL_FLG=<FINAL_FLG>, &
			OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO>
updateRequestM.Debug=N


//查询申请主档
queryRequestM.Type=TSQL
queryRequestM.SQL=SELECT REQUEST_NO , REQUEST_TYPE , REQUEST_DATE, FROM_ORG_CODE , TO_ORG_CODE , &
			  REN_CODE , URGENT_FLG , REMARK, FINAL_FLG &
		     FROM INV_REQUESTM
queryRequestM.ITEM=REQUEST_TYPE;FINAL_FLG;TO_ORG_CODE;REQUEST_NO;START_DATE
queryRequestM.REQUEST_TYPE=REQUEST_TYPE=<REQUEST_TYPE>
queryRequestM.FINAL_FLG=FINAL_FLG=<FINAL_FLG>
queryRequestM.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryRequestM.REQUEST_NO=REQUEST_NO=<REQUEST_NO>
queryRequestM.START_DATE=REQUEST_DATE BETWEEN <START_DATE> AND <END_DATE>
queryRequestM.Debug=N


//删除申请主档
deleteRequestM.Type=TSQL
deleteRequestM.SQL=DELETE FROM INV_REQUESTM WHERE REQUEST_NO=<REQUEST_NO>
deleteRequestM.Debug=N


//查询需要出库的申请单
queryRequestMOut.Type=TSQL
queryRequestMOut.SQL=SELECT '' AS DISPENSE_NO, REQUEST_TYPE, REQUEST_NO, REQUEST_DATE, &
			    FROM_ORG_CODE, TO_ORG_CODE, '' AS DISPENSE_DATE, '' AS DISPENSE_USER, &
			    URGENT_FLG, REMARK, 'N' AS DISPOSAL_FLG, '' AS CHECK_DATE, &
			    '' AS CHECK_USER, REN_CODE, '0' AS FINA_FLG &
      		       FROM INV_REQUESTM WHERE FINAL_FLG = 'N'
queryRequestMOut.ITEM=REQUEST_TYPE;TO_ORG_CODE;REQUEST_NO;START_DATE
queryRequestMOut.REQUEST_TYPE=REQUEST_TYPE=<REQUEST_TYPE>
queryRequestMOut.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryRequestMOut.REQUEST_NO=REQUEST_NO=<REQUEST_NO>
queryRequestMOut.START_DATE=REQUEST_DATE BETWEEN <START_DATE> AND <END_DATE>
queryRequestMOut.Debug=N


//申请单主项状态(更新申请单主项状态)
updateFinalFlg.Type=TSQL
updateFinalFlg.SQL=UPDATE INV_REQUESTM SET &
			  FINAL_FLG=<FINAL_FLG>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO>
updateFinalFlg.Debug=N

       
       











