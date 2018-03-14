   #
   # Title:血品规格
   #
   # Description:血品规格
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/29

Module.item=initCombo

initCombo.Type=TSQL
initCombo.SQL=SELECT SUBCAT_CODE ID,SUBCAT_DESC NAME,ENNAME &
		FROM BMS_BLDSUBCAT &
	    ORDER BY SUBCAT_CODE
initCombo.item=BLD_CODE
initCombo.BLD_CODE=BLD_CODE=<BLD_CODE>
initCombo.Debug=N
