 #
   # Title: �ײͲ���
   #
   # Description:�ײͲ���
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSPackDD;insertNSSPackDD;updateNSSPackDD;deleteNSSPackDD


//��ѯ�ײͲ���
queryNSSPackDD.Type=TSQL
queryNSSPackDD.SQL=SELECT PACK_CODE, MEAL_CODE, MENU_CODE, MENU_CHN_DESC, MENU_ENG_DESC, &
		          MEAL_TYPE, PY1, PY2, SEQ, DESCRIPTION, ORDER_PRICE, PACK_PRICE, &
		          OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_PACKDD ORDER BY PACK_CODE, MEAL_CODE, MENU_CODE
queryNSSPackDD.item=PACK_CODE;MEAL_CODE;MENU_CODE
queryNSSPackDD.PACK_CODE=PACK_CODE=<PACK_CODE>
queryNSSPackDD.MEAL_CODE=MEAL_CODE=<MEAL_CODE>
queryNSSPackDD.MENU_CODE=MENU_CODE=<MENU_CODE>
queryNSSPackDD.Debug=N


//�����ײͲ���
insertNSSPackDD.Type=TSQL
insertNSSPackDD.SQL=INSERT INTO NSS_PACKDD &
            		  (PACK_CODE, MEAL_CODE, MENU_CODE, MENU_CHN_DESC, MENU_ENG_DESC, &
		          MEAL_TYPE, PY1, PY2, SEQ, DESCRIPTION, ORDER_PRICE, PACK_PRICE, &
		          OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<PACK_CODE>, <MEAL_CODE>, <MENU_CODE>, <MENU_CHN_DESC>, <MENU_ENG_DESC>, &
		          <MEAL_TYPE>, <PY1>, <PY2>, <SEQ>, <DESCRIPTION>, <ORDER_PRICE>, <PACK_PRICE>, &
		          <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSPackDD.Debug=N


//�����ײͲ���
updateNSSPackDD.Type=TSQL
updateNSSPackDD.SQL=UPDATE NSS_PACKDD SET &
			  MENU_CHN_DESC=<MENU_CHN_DESC>,MENU_ENG_DESC=<MENU_ENG_DESC>,MEAL_TYPE=<MEAL_TYPE>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,ORDER_PRICE=<ORDER_PRICE>,PACK_PRICE=<PACK_PRICE>, &
			  OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
	            WHERE PACK_CODE=<PACK_CODE>  AND MEAL_CODE=<MEAL_CODE> AND MENU_CODE=<MENU_CODE>
updateNSSPackDD.Debug=N


//ɾ���ײͲ���
deleteNSSPackDD.Type=TSQL
deleteNSSPackDD.SQL=DELETE FROM NSS_PACKDD 
deleteNSSPackDD.item=PACK_CODE;MEAL_CODE;MENU_CODE
deleteNSSPackDD.PACK_CODE=PACK_CODE=<PACK_CODE>
deleteNSSPackDD.MEAL_CODE=MEAL_CODE=<MEAL_CODE>
deleteNSSPackDD.MENU_CODE=MENU_CODE=<MENU_CODE>
deleteNSSPackDD.Debug=N


