 #
   # Title: Ì×²Í×Öµä
   #
   # Description:Ì×²Í×Öµä
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSPackM;insertNSSPackM;updateNSSPackM;deleteNSSPackM


//²éÑ¯Ì×²Í
queryNSSPackM.Type=TSQL
queryNSSPackM.SQL=SELECT PACK_CODE, PACK_CHN_DESC, PACK_ENG_DESC, PY1, PY2, SEQ, &
             		DESCRIPTION, MEAL_COUNT, DIET_TYPE, DIET_KIND, PRICE, &
             		CALORIES, OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_PACKM ORDER BY PACK_CODE
queryNSSPackM.item=PACK_CODE
queryNSSPackM.PACK_CODE=PACK_CODE=<PACK_CODE>
queryNSSPackM.Debug=N


//ÐÂÔöÌ×²Í
insertNSSPackM.Type=TSQL
insertNSSPackM.SQL=INSERT INTO NSS_PACKM &
            		  (PACK_CODE, PACK_CHN_DESC, PACK_ENG_DESC, PY1, PY2, SEQ, &
             		   DESCRIPTION, MEAL_COUNT, DIET_TYPE, DIET_KIND, &
             		   PRICE, CALORIES,OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<PACK_CODE>, <PACK_CHN_DESC>, <PACK_ENG_DESC>, <PY1>, <PY2>, <SEQ>, &
             		   <DESCRIPTION>, <MEAL_COUNT>, <DIET_TYPE>, <DIET_KIND>, &
             		   <PRICE>, <CALORIES>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSPackM.Debug=N


//¸üÐÂÌ×²Í
updateNSSPackM.Type=TSQL
updateNSSPackM.SQL=UPDATE NSS_PACKM SET &
			  PACK_CHN_DESC=<PACK_CHN_DESC>,PACK_ENG_DESC=<PACK_ENG_DESC>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,MEAL_COUNT=<MEAL_COUNT>,DIET_TYPE=<DIET_TYPE>, &
			  DIET_KIND=<DIET_KIND>, PRICE=<PRICE>, CALORIES=<CALORIES>, OPT_USER=<OPT_USER>, &
			  OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
	            WHERE PACK_CODE=<PACK_CODE> 
updateNSSPackM.Debug=N


//É¾³ýÌ×²Í
deleteNSSPackM.Type=TSQL
deleteNSSPackM.SQL=DELETE FROM NSS_PACKM WHERE PACK_CODE=<PACK_CODE> 
deleteNSSPackM.Debug=N


