   #
   # Title:����ⵥ����
   #
   # Description:����ⵥ����
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/25
   
Module.item=queryDispenseOutA;queryDispenseOutB;createNewDispenseM;updateDispenseM;updateDispenseFlg;getQueryDispense


//��ѯ������;�ĳ��ⵥ
queryDispenseOutA.Type=TSQL
queryDispenseOutA.SQL=SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, &
			     TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, &
			     WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, &
			     OPT_USER, OPT_DATE, OPT_TERM &
		      FROM IND_DISPENSEM &
		      WHERE UPDATE_FLG = '1'
queryDispenseOutA.ITEM=APP_ORG_CODE;REQUEST_NO;START_DATE;END_DATE;REQTYPE_CODE;DISPENSE_NO;REGION_CODE
queryDispenseOutA.APP_ORG_CODE=APP_ORG_CODE=<APP_ORG_CODE>
queryDispenseOutA.REQUEST_NO=REQUEST_NO=<REQUEST_NO>
queryDispenseOutA.REQTYPE_CODE=REQTYPE_CODE=<REQTYPE_CODE>
queryDispenseOutA.START_DATE=REQUEST_DATE>=<START_DATE>
queryDispenseOutA.END_DATE=REQUEST_DATE<=<END_DATE>
queryDispenseOutA.DISPENSE_NO=DISPENSE_NO<=<DISPENSE_NO>
queryDispenseOutA.REGION_CODE=REGION_CODE=<REGION_CODE>
queryDispenseOutA.Debug=N

//��ѯ��������ɵĳ��ⵥ
queryDispenseOutB.Type=TSQL
queryDispenseOutB.SQL=SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, &
			     TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, &
			     WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, &
			     OPT_USER, OPT_DATE, OPT_TERM &
		      FROM IND_DISPENSEM &
		      WHERE (UPDATE_FLG = '2' OR UPDATE_FLG = '3') ORDER BY DISPENSE_NO
queryDispenseOutB.ITEM=APP_ORG_CODE;REQUEST_NO;START_DATE;END_DATE;REQTYPE_CODE;DISPENSE_NO;REGION_CODE
queryDispenseOutB.APP_ORG_CODE=APP_ORG_CODE=<APP_ORG_CODE>
queryDispenseOutB.REQUEST_NO=REQUEST_NO=<REQUEST_NO>
queryDispenseOutB.REQTYPE_CODE=REQTYPE_CODE=<REQTYPE_CODE>
queryDispenseOutB.START_DATE=REQUEST_DATE>=<START_DATE>
queryDispenseOutB.END_DATE=REQUEST_DATE<=<END_DATE>
queryDispenseOutB.DISPENSE_NO=DISPENSE_NO<=<DISPENSE_NO>
queryDispenseOutB.REGION_CODE=REGION_CODE=<REGION_CODE>
queryDispenseOutB.Debug=N


//��������ⵥ����
createNewDispenseM.Type=TSQL
createNewDispenseM.SQL=INSERT INTO IND_DISPENSEM( &
			DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, &
			TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, &
			WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, &
			OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE) &
	    	   VALUES( &
	    	   	<DISPENSE_NO>, <REQTYPE_CODE>, <REQUEST_NO>, <REQUEST_DATE>, <APP_ORG_CODE>, &
			<TO_ORG_CODE>, <URGENT_FLG>, <DESCRIPTION>, <DISPENSE_DATE>, <DISPENSE_USER>, &
			<WAREHOUSING_DATE>, <WAREHOUSING_USER>, <REASON_CHN_DESC>, <UNIT_TYPE>, <UPDATE_FLG>, &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>, <REGION_CODE>)
createNewDispenseM.Debug=N


//��������
updateDispenseM.Type=TSQL
updateDispenseM.SQL=UPDATE IND_DISPENSEM SET &
			   DESCRIPTION=<DESCRIPTION>, &
			   WAREHOUSING_DATE=<WAREHOUSING_DATE>, &
			   WAREHOUSING_USER=<WAREHOUSING_USER>, &
			   REASON_CHN_DESC=<REASON_CHN_DESC>, &
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO>
updateDispenseM.Debug=N


//��������״̬
updateDispenseFlg.Type=TSQL
updateDispenseFlg.SQL=UPDATE IND_DISPENSEM SET &
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE DISPENSE_NO=<DISPENSE_NO>
updateDispenseFlg.Debug=N



//ҩƷ����������
//luhai modify 2012-1-24 ��RETAIL_PRICE �ĳ�verifyin_price
getQueryDispense.Type=TSQL
getQueryDispense.SQL=SELECT CASE WHEN (A.UPDATE_FLG = '2') THEN 'Y' ELSE 'N' &
 			    END AS STOP_FLG, A.REQTYPE_CODE, A.DISPENSE_NO, C.DEPT_CHN_DESC, &
       			    D.ORG_CHN_DESC, CASE WHEN (E.GOODS_DESC IS NULL) THEN E.ORDER_DESC &
          		    ELSE E.ORDER_DESC || ' (' || E.GOODS_DESC || ')' &
       			    END AS ORDER_DESC, E.SPECIFICATION, F.UNIT_CHN_DESC, B.ACTUAL_QTY AS QTY, &
       			    B.VERIFYIN_PRICE AS OWN_PRICE, B.VERIFYIN_PRICE * B.ACTUAL_QTY AS OWN_AMT, &
       			    B.BATCH_NO, B.VALID_DATE, A.DISPENSE_DATE, A.WAREHOUSING_DATE &
		       FROM IND_DISPENSEM A, &
		            IND_DISPENSED B, &
		            SYS_DEPT C, &
		            IND_ORG D, &
		            SYS_FEE E, &
		            SYS_UNIT F, &
		            PHA_TRANSUNIT G &
		      WHERE A.DISPENSE_NO = B.DISPENSE_NO &
		        AND A.APP_ORG_CODE = C.DEPT_CODE(+) &
		        AND A.TO_ORG_CODE = D.ORG_CODE(+)  &
		        AND B.ORDER_CODE = E.ORDER_CODE &
		        AND B.UNIT_CODE = F.UNIT_CODE &
		        AND B.ORDER_CODE = G.ORDER_CODE & 
			AND D.REGION_CODE = <REGION_CODE> & 
		        ORDER BY B.ORDER_CODE
getQueryDispense.ITEM=REQUEST_TYPE;APP_ORG_CODE;TO_ORG_CODE;DISPENSE_OUT;DISPENSE_IN;STOP_FLG;ORDER_CODE
getQueryDispense.REQUEST_TYPE=A.REQTYPE_CODE=<REQUEST_TYPE>
getQueryDispense.APP_ORG_CODE=A.APP_ORG_CODE=<APP_ORG_CODE>
getQueryDispense.TO_ORG_CODE=A.TO_ORG_CODE=<TO_ORG_CODE>
getQueryDispense.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryDispense.DISPENSE_OUT=A.DISPENSE_DATE BETWEEN <START_DATE> AND <END_DATE>
getQueryDispense.DISPENSE_IN=A.WAREHOUSING_DATE IS NOT NULL AND A.WAREHOUSING_DATE BETWEEN <START_DATE> AND <END_DATE>
getQueryDispense.STOP_FLG=A.UPDATE_FLG='2'
getQueryDispense.Debug=N








                 
                






