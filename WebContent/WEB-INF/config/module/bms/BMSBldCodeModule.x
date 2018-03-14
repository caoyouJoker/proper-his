   #
   # Title:
   #
   # Description:
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/29

Module.item=initCombo;query

initCombo.Type=TSQL
initCombo.SQL=SELECT BLD_CODE ID,BLDCODE_DESC NAME,ENNAME,PY1,PY2 &
		FROM BMS_BLDCODE &
	    ORDER BY BLD_CODE
initCombo.Debug=N


//≤È—Ø
query.Type=TSQL
query.SQL=SELECT BLD_CODE, BLDCODE_DESC, PY1, PY2, DESCRIPTION, &
   		 FRONTPG_TYPE, UNIT_CODE, VALUE_DAYS &
   		 FROM BMS_BLDCODE
query.ITEM=BLD_CODE
query.BLD_CODE=BLD_CODE=<BLD_CODE>
query.Debug=N


