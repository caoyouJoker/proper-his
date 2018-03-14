 #
   # Title: 每周套餐
   #
   # Description:每周套餐
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSWeeklyMenu;insertNSSWeeklyMenu;updateNSSWeeklyMenu;deleteNSSWeeklyMenu


//查询每周套餐
queryNSSWeeklyMenu.Type=TSQL
queryNSSWeeklyMenu.SQL=SELECT WEEKLY_CODE, DIET_TYPE, PACK_CODE, PACK_CHN_DESC, PACK_ENG_DESC, &
			PY1, PY2, SEQ, DESCRIPTION, PRICE, OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_WEEKLY_MENU ORDER BY WEEKLY_CODE, DIET_TYPE, PACK_CODE
queryNSSWeeklyMenu.item=WEEKLY_CODE;DIET_TYPE;PACK_CODE
queryNSSWeeklyMenu.WEEKLY_CODE=WEEKLY_CODE=<WEEKLY_CODE>
queryNSSWeeklyMenu.DIET_TYPE=DIET_TYPE=<DIET_TYPE>
queryNSSWeeklyMenu.PACK_CODE=PACK_CODE=<PACK_CODE>
queryNSSWeeklyMenu.Debug=N


//新增每周套餐
insertNSSWeeklyMenu.Type=TSQL
insertNSSWeeklyMenu.SQL=INSERT INTO NSS_WEEKLY_MENU &
            		  (WEEKLY_CODE, DIET_TYPE, PACK_CODE, PACK_CHN_DESC, PACK_ENG_DESC, &
			   PY1, PY2, SEQ, DESCRIPTION, PRICE, OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<WEEKLY_CODE>, <DIET_TYPE>, <PACK_CODE>, <PACK_CHN_DESC>, <PACK_ENG_DESC>, &
             		   <PY1>, <PY2>, <SEQ>, <DESCRIPTION>, <PRICE>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSWeeklyMenu.Debug=N


//更新每周套餐
updateNSSWeeklyMenu.Type=TSQL
updateNSSWeeklyMenu.SQL=UPDATE  NSS_WEEKLY_MENU SET &
			  PACK_CHN_DESC=<PACK_CHN_DESC>,PACK_ENG_DESC=<PACK_ENG_DESC>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,PRICE=<PRICE>, OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
	            WHERE WEEKLY_CODE=<WEEKLY_CODE> AND DIET_TYPE=<DIET_TYPE> AND PACK_CODE=<PACK_CODE>
updateNSSWeeklyMenu.Debug=N


//删除每周套餐
deleteNSSWeeklyMenu.Type=TSQL
deleteNSSWeeklyMenu.SQL=DELETE FROM NSS_WEEKLY_MENU WHERE WEEKLY_CODE=<WEEKLY_CODE> AND DIET_TYPE=<DIET_TYPE> AND PACK_CODE=<PACK_CODE>
deleteNSSWeeklyMenu.Debug=N


