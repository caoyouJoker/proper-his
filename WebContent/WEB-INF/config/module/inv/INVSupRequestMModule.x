   #
   # Title:��������
   #
   # Description:��������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete;query;updateFlgBySupDispense


//�½���������
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SUPREQUESTM( &
			REQUEST_NO , SUPTYPE_CODE , APP_ORG_CODE, TO_ORG_CODE , REQUEST_DATE ,&
			REQUEST_USER , REASON_CHN_DESC , DESCRIPTION, URGENT_FLG , UPDATE_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<REQUEST_NO> ,  <SUPTYPE_CODE> , <APP_ORG_CODE> , <TO_ORG_CODE> , <REQUEST_DATE> , &
	    	   	<REQUEST_USER> ,  <REASON_CHN_DESC> , <DESCRIPTION> , <URGENT_FLG> , <UPDATE_FLG>,&
	    	   	<OPT_USER> , SYSDATE , <OPT_TERM> )
insert.Debug=N


//������������
update.Type=TSQL
update.SQL=UPDATE INV_SUPREQUESTM SET &
			SUPTYPE_CODE=<SUPTYPE_CODE>, APP_ORG_CODE=<APP_ORG_CODE>, &
			TO_ORG_CODE=<TO_ORG_CODE>, REQUEST_DATE=<REQUEST_DATE>, REQUEST_USER=<REQUEST_USER>, &
			REASON_CHN_DESC=<REASON_CHN_DESC>, DESCRIPTION=<DESCRIPTION>, URGENT_FLG=<URGENT_FLG>, UPDATE_FLG=<UPDATE_FLG>, &
			OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO>
update.Debug=N


//��ѯ��������
query.Type=TSQL
query.SQL=SELECT REQUEST_NO,SUPTYPE_CODE,APP_ORG_CODE,TO_ORG_CODE,REQUEST_USER, &
	         REQUEST_DATE,REASON_CHN_DESC,URGENT_FLG,UPDATE_FLG,DESCRIPTION FROM INV_SUPREQUESTM &
	         ORDER BY REQUEST_NO
query.ITEM=SUPTYPE_CODE;APP_ORG_CODE;TO_ORG_CODE;REQUEST_NO;START_DATE;UPDATE_FLG_A;UPDATE_FLG_B
query.SUPTYPE_CODE=SUPTYPE_CODE=<SUPTYPE_CODE>
query.APP_ORG_CODE=APP_ORG_CODE=<APP_ORG_CODE>
query.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
query.REQUEST_NO=REQUEST_NO=<REQUEST_NO>
query.START_DATE=REQUEST_DATE BETWEEN <START_DATE> AND <END_DATE>
query.UPDATE_FLG_B=UPDATE_FLG IN ('0','1')
query.UPDATE_FLG_A=UPDATE_FLG IN ('2','3')
query.Debug=N


//ɾ����������
delete.Type=TSQL
delete.SQL=DELETE FROM INV_SUPREQUESTM WHERE REQUEST_NO=<REQUEST_NO>
delete.Debug=N


//��Ӧ�ҳ���������쵥״̬��������
updateFlgBySupDispense.Type=TSQL
updateFlgBySupDispense.SQL=UPDATE INV_SUPREQUESTM SET &
				  UPDATE_FLG=<UPDATE_FLG>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    	    WHERE REQUEST_NO=<REQUEST_NO> 
updateFlgBySupDispense.Debug=N
       










