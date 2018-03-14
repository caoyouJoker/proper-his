 #
   # Title: 菜单设定
   #
   # Description:菜单设定
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009.05.08

Module.item=queryNSSMenu;insertNSSMenu;updateNSSMenu;deleteNSSMenu


//查询菜单
queryNSSMenu.Type=TSQL
queryNSSMenu.SQL=SELECT MENU_CODE, MENU_CHN_DESC, MENU_ENG_DESC, PY1, PY2, SEQ, &
             		DESCRIPTION, CALORIES, TABOO_CODE, PRICE, NSSTYPE_CODE, OPT_USER, OPT_DATE, OPT_TERM &
             	   FROM NSS_MENU ORDER BY MENU_CODE
queryNSSMenu.item=MENU_CODE;NSSTYPE_CODE
queryNSSMenu.MENU_CODE=MENU_CODE=<MENU_CODE>
queryNSSMenu.NSSTYPE_CODE=NSSTYPE_CODE=<NSSTYPE_CODE>
queryNSSMenu.Debug=N


//新增菜单
insertNSSMenu.Type=TSQL
insertNSSMenu.SQL=INSERT INTO NSS_MENU &
            		  (MENU_CODE, MENU_CHN_DESC, MENU_ENG_DESC, PY1, PY2, SEQ, &
             		   DESCRIPTION, CALORIES, TABOO_CODE, PRICE, NSSTYPE_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
     		   VALUES (<MENU_CODE>, <MENU_CHN_DESC>, <MENU_ENG_DESC>, <PY1>, <PY2>, <SEQ>, &
             		   <DESCRIPTION>, <CALORIES>, <TABOO_CODE>, <PRICE>, <NSSTYPE_CODE>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertNSSMenu.Debug=N


//更新菜单
updateNSSMenu.Type=TSQL
updateNSSMenu.SQL=UPDATE NSS_MENU SET &
			  MENU_CHN_DESC=<MENU_CHN_DESC>,MENU_ENG_DESC=<MENU_ENG_DESC>,PY1=<PY1>,PY2=<PY2>, &
			  SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,CALORIES=<CALORIES>,TABOO_CODE=<TABOO_CODE>, &
			  PRICE=<PRICE>,NSSTYPE_CODE=<NSSTYPE_CODE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
	            WHERE MENU_CODE=<MENU_CODE> 
updateNSSMenu.Debug=N


//删除菜单
deleteNSSMenu.Type=TSQL
deleteNSSMenu.SQL=DELETE FROM NSS_MENU WHERE MENU_CODE=<MENU_CODE> 
deleteNSSMenu.Debug=N


