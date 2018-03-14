   #
   # Title:申请单主档
   #
   # Description:申请单主档
   #
   # Copyright: JavaHis (c) 2013
   # 
   # @author fux 2013/03/08  

Module.item=queryRequestM;createNewRequestM;updateRequestM;deleteRequestM;
queryOutReqNo;selectDevRequestM  

   
//查询申请主档queryRequestM  10个参数    
queryRequestM.Type=TSQL           
queryRequestM.SQL=SELECT REQUEST_NO,APP_ORG_CODE,TO_ORG_CODE,REQUEST_DATE,REQUEST_USER,REQUEST_REASON, &
                          OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,FINAL_FLG & 
		          FROM DEV_RequestM &                    
                          ORDER BY REQUEST_NO                   
queryRequestM.item=REQUEST_NO;REQUEST_DATE_BEGIN;APP_ORG_CODE;TO_ORG_CODE;FINAL_FLG
//请领单号             
queryRequestM.REQUEST_NO=REQUEST_NO LIKE <REQUEST_NO>||'%'       
//请领日期
queryRequestM.REQUEST_DATE_BEGIN=REQUEST_DATE BETWEEN <REQUEST_DATE_BEGIN> AND <REQUEST_DATE_END>
//供应科室
queryRequestM.APP_ORG_CODE=APP_ORG_CODE =<APP_ORG_CODE>   
//请领科室           
queryRequestM.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>        
//完成状态 
queryRequestM.FINAL_FLG=FINAL_FLG=<FINAL_FLG>                
queryRequestM.Debug=N
   
    
//新建药库申请主档10个参数
createNewRequestM.Type=TSQL  
createNewRequestM.SQL=INSERT INTO DEV_RequestM( & 
	              REQUEST_NO,APP_ORG_CODE,TO_ORG_CODE,REQUEST_DATE,REQUEST_USER,REQUEST_REASON, &  
                      OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,FINAL_FLG) &
	    	   VALUES( &    
	    	   	<REQUEST_NO>, <APP_ORG_CODE>, <TO_ORG_CODE>, <REQUEST_DATE>,<REQUEST_USER>, &
		        <REQUEST_REASON>, <OPT_USER>, <OPT_DATE>,<OPT_TERM>, <REGION_CODE>,<FINAL_FLG>)
createNewRequestM.Debug=N               


//更新申请主档8个参数
updateRequestM.Type=TSQL
updateRequestM.SQL=UPDATE  DEV_RequestM SET &  
                           APP_ORG_CODE = <APP_ORG_CODE>, TO_ORG_CODE = <TO_ORG_CODE>, &
			   REQUEST_DATE = <REQUEST_DATE>,REQUEST_USER = <REQUEST_USER>,REQUEST_REASON = <REQUEST_REASON>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO>               
updateRequestM.Debug=N
  

//删除订购主档
deleteRequestM.Type=TSQL
deleteRequestM.SQL=DELETE FROM DEV_REQUESTM WHERE REQUEST_NO=<REQUEST_NO>
deleteRequestM.Debug=N  


//查询所需出库作业
queryOutReqNo.Type=TSQL
queryOutReqNo.SQL=SELECT DISTINCT A.REQUEST_NO, A.REQTYPE_CODE, A.APP_ORG_CODE, A.TO_ORG_CODE, A.REQUEST_DATE, &
			 A.REQUEST_USER, A.REASON_CHN_DESC, A.DESCRIPTION, A.UNIT_TYPE, A.URGENT_FLG &
		    FROM DEV_REQUESTM A, DEV_REQUESTD B &
		   WHERE A.REQUEST_NO = B.REQUEST_NO &
		     AND B.UPDATE_FLG IN ('0', '1') &
		     AND A.REQTYPE_CODE <> 'THI' &
		     AND B.QTY > B.ACTUAL_QTY &
                     ORDER BY A.REQUEST_NO DESC
queryOutReqNo.ITEM=APP_ORG_CODE;REQUEST_NO;START_DATE;END_DATE;REQTYPE_CODE;REGION_CODE
queryOutReqNo.APP_ORG_CODE=A.APP_ORG_CODE=<APP_ORG_CODE>
queryOutReqNo.REQUEST_NO=A.REQUEST_NO=<REQUEST_NO>
queryOutReqNo.REQTYPE_CODE=A.REQTYPE_CODE=<REQTYPE_CODE>
queryOutReqNo.START_DATE=A.REQUEST_DATE>=<START_DATE>
queryOutReqNo.END_DATE=A.REQUEST_DATE<=<END_DATE>
queryOutReqNo.REGION_CODE=A.REGION_CODE<=<REGION_CODE>
queryOutReqNo.Debug=N











