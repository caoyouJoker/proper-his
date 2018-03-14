 #
   # Title: Ì×²Í²Íµã
   #
   # Description:Ì×²Í²Íµã
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSPackD;insertNSSPackD;updateNSSPackD;deleteNSSPackD


//²éÑ¯Ì×²Í
queryNSSPackD.Type=TSQL
queryNSSPackD.SQL=SELECT PACK_CODE, MEAL_CODE, MEAL_CHN_DESC, MEAL_ENG_DESC, PY1, PY2, SEQ, &
             		DESCRIPTION, ORDER_PRICE, PACK_PRICE, ORDER_CODE, OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_PACKD ORDER BY PACK_CODE, MEAL_CODE
queryNSSPackD.item=PACK_CODE;MEAL_CODE
queryNSSPackD.PACK_CODE=PACK_CODE=<PACK_CODE>
queryNSSPackD.MEAL_CODE=MEAL_CODE=<MEAL_CODE>
queryNSSPackD.Debug=N


//ÐÂÔöÌ×²Í
insertNSSPackD.Type=TSQL
insertNSSPackD.SQL=INSERT INTO NSS_PACKD &
            		  (PACK_CODE, MEAL_CODE, MEAL_CHN_DESC, MEAL_ENG_DESC, PY1, PY2, SEQ, &
             		   DESCRIPTION, ORDER_PRICE, PACK_PRICE, ORDER_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<PACK_CODE>, <MEAL_CODE>, <MEAL_CHN_DESC>, <MEAL_ENG_DESC>, <PY1>, <PY2>, <SEQ>, &
             		   <DESCRIPTION>, <ORDER_PRICE>, <PACK_PRICE>, <ORDER_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSPackD.Debug=N


//¸üÐÂÌ×²Í
updateNSSPackD.Type=TSQL
updateNSSPackD.SQL=UPDATE NSS_PACKD SET &
			  MEAL_CHN_DESC=<MEAL_CHN_DESC>,MEAL_ENG_DESC=<MEAL_ENG_DESC>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,ORDER_PRICE=<ORDER_PRICE>,PACK_PRICE=<PACK_PRICE>, &
			  ORDER_CODE=<ORDER_CODE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
	            WHERE PACK_CODE=<PACK_CODE>  AND MEAL_CODE=<MEAL_CODE>
updateNSSPackD.Debug=N


//É¾³ýÌ×²Í
deleteNSSPackD.Type=TSQL
deleteNSSPackD.SQL=DELETE FROM NSS_PACKD 
deleteNSSPackD.item=PACK_CODE;MEAL_CODE
deleteNSSPackD.PACK_CODE=PACK_CODE=<PACK_CODE>
deleteNSSPackD.MEAL_CODE=MEAL_CODE=<MEAL_CODE>
deleteNSSPackD.Debug=N


