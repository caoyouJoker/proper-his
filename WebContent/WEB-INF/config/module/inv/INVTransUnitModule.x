   #
   # Title: 物资单位
   #
   # Description: 物资单位
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete


//新增物资单位
insert.Type=TSQL
insert.SQL=INSERT INTO INV_TRANSUNIT( &
			INV_CODE, PURCH_UNIT, PURCH_QTY, STOCK_UNIT, STOCK_QTY, &
			DISPENSE_UNIT, DISPENSE_QTY, OPT_USER, OPT_DATE, OPT_TERM ) &
		       VALUES( &
	    	        <INV_CODE>, <PURCH_UNIT>, <PURCH_QTY>, <STOCK_UNIT>, <STOCK_QTY>, &
			<DISPENSE_UNIT>, <DISPENSE_QTY>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N

//更新物资单位
update.Type=TSQL
update.SQL=UPDATE INV_TRANSUNIT SET &
			PURCH_UNIT=<PURCH_UNIT>, PURCH_QTY=<PURCH_QTY>, STOCK_UNIT=<STOCK_UNIT>, STOCK_QTY=<STOCK_QTY>, &
			DISPENSE_UNIT=<DISPENSE_UNIT>, DISPENSE_QTY=<DISPENSE_QTY>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE INV_CODE = <INV_CODE>
update.Debug=Y

//删除物资单位
delete.Type=TSQL
delete.SQL=DELETE FROM INV_TRANSUNIT WHERE INV_CODE = <INV_CODE>
delete.Debug=N



