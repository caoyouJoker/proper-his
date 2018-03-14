   #
   # Title:���뵥����
   #
   # Description:���뵥����
   #
   # Copyright: JavaHis (c) 2013
   # 
   # @author fux 2013/03/08  

Module.item=queryRequestM;createNewRequestM;updateRequestM;deleteRequestM;
queryOutReqNo;selectDevRequestM  

   
//��ѯ��������queryRequestM  10������    
queryRequestM.Type=TSQL           
queryRequestM.SQL=SELECT REQUEST_NO,APP_ORG_CODE,TO_ORG_CODE,REQUEST_DATE,REQUEST_USER,REQUEST_REASON, &
                          OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,FINAL_FLG & 
		          FROM DEV_RequestM &                    
                          ORDER BY REQUEST_NO                   
queryRequestM.item=REQUEST_NO;REQUEST_DATE_BEGIN;APP_ORG_CODE;TO_ORG_CODE;FINAL_FLG
//���쵥��             
queryRequestM.REQUEST_NO=REQUEST_NO LIKE <REQUEST_NO>||'%'       
//��������
queryRequestM.REQUEST_DATE_BEGIN=REQUEST_DATE BETWEEN <REQUEST_DATE_BEGIN> AND <REQUEST_DATE_END>
//��Ӧ����
queryRequestM.APP_ORG_CODE=APP_ORG_CODE =<APP_ORG_CODE>   
//�������           
queryRequestM.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>        
//���״̬ 
queryRequestM.FINAL_FLG=FINAL_FLG=<FINAL_FLG>                
queryRequestM.Debug=N
   
    
//�½�ҩ����������10������
createNewRequestM.Type=TSQL  
createNewRequestM.SQL=INSERT INTO DEV_RequestM( & 
	              REQUEST_NO,APP_ORG_CODE,TO_ORG_CODE,REQUEST_DATE,REQUEST_USER,REQUEST_REASON, &  
                      OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,FINAL_FLG) &
	    	   VALUES( &    
	    	   	<REQUEST_NO>, <APP_ORG_CODE>, <TO_ORG_CODE>, <REQUEST_DATE>,<REQUEST_USER>, &
		        <REQUEST_REASON>, <OPT_USER>, <OPT_DATE>,<OPT_TERM>, <REGION_CODE>,<FINAL_FLG>)
createNewRequestM.Debug=N               


//������������8������
updateRequestM.Type=TSQL
updateRequestM.SQL=UPDATE  DEV_RequestM SET &  
                           APP_ORG_CODE = <APP_ORG_CODE>, TO_ORG_CODE = <TO_ORG_CODE>, &
			   REQUEST_DATE = <REQUEST_DATE>,REQUEST_USER = <REQUEST_USER>,REQUEST_REASON = <REQUEST_REASON>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO>               
updateRequestM.Debug=N
  

//ɾ����������
deleteRequestM.Type=TSQL
deleteRequestM.SQL=DELETE FROM DEV_REQUESTM WHERE REQUEST_NO=<REQUEST_NO>
deleteRequestM.Debug=N  


//��ѯ���������ҵ
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











