   #
   # Title: 物资供应厂商
   #
   # Description: 物资供应厂商
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete


//新增物资供应厂商
insert.Type=TSQL
insert.SQL=INSERT INTO INV_AGENT( &
			SUP_CODE, INV_CODE, GIFT_RATE, GIFT_QTY, DISCOUNT_RATE, &
			CONTRACT_PRICE, STOCK_UNIT, BILL_UNIT, CONTRACT_NO, &
			OPT_USER, OPT_DATE, OPT_TERM ) &
		      VALUES( &
	    	        <SUP_CODE>, <INV_CODE>, <GIFT_RATE>, <GIFT_QTY>, <DISCOUNT_RATE>, &
			<CONTRACT_PRICE>, <STOCK_UNIT>, <BILL_UNIT>, <CONTRACT_NO>,  &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N

//更新物资供应厂商
update.Type=TSQL
update.SQL=UPDATE INV_AGENT SET &
			GIFT_RATE=<GIFT_RATE>, GIFT_QTY=<GIFT_QTY>, DISCOUNT_RATE=<DISCOUNT_RATE>, CONTRACT_PRICE=<CONTRACT_PRICE>, &
			STOCK_UNIT=<STOCK_UNIT>, BILL_UNIT=<BILL_UNIT>, CONTRACT_NO=<CONTRACT_NO>, &
			OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE INV_CODE = <INV_CODE> AND SUP_CODE = <SUP_CODE>
update.Debug=N

//删除物资供应厂商
delete.Type=TSQL
delete.SQL=DELETE FROM INV_AGENT WHERE INV_CODE = <INV_CODE> AND SUP_CODE = <SUP_CODE>
delete.Debug=N



