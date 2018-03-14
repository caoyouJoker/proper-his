   #
   # Title:备血申请单细项
   #
   # Description:备血申请单细项
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/04/29

Module.item=ApplyInsert;ApplyDelete;ApplyQuery;TakeQuery;

//新增
ApplyInsert.Type=TSQL
ApplyInsert.SQL=INSERT INTO BMS_APPLYD(APPLY_NO, BLD_CODE, APPLY_QTY, UNIT_CODE, & 
				 PRE_DATE, OPT_USER, OPT_DATE, OPT_TERM) &
			  VALUES(<APPLY_NO>, <BLD_CODE>, <APPLY_QTY>, <UNIT_CODE>, & 
				 <PRE_DATE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
ApplyInsert.Debug=N


//删除
ApplyDelete.Type=TSQL
ApplyDelete.SQL=DELETE FROM BMS_APPLYD WHERE APPLY_NO=<APPLY_NO>
ApplyDelete.Debug=N


//查询备血单
ApplyQuery.Type=TSQL
ApplyQuery.SQL=SELECT APPLY_NO, BLD_CODE, SUBCAT_CODE, &
   		      APPLY_QTY, APPLY_VOL, UNIT_CODE, &
   		      PRE_DATE, OPT_USER, OPT_DATE, OPT_TERM &
		 FROM BMS_APPLYD 
ApplyQuery.ITEM=APPLY_NO;BLD_CODE
ApplyQuery.APPLY_NO=APPLY_NO=<APPLY_NO>
ApplyQuery.BLD_CODE=BLD_CODE=<BLD_CODE>
ApplyQuery.Debug=N


//查询取血单
TakeQuery.Type=TSQL
TakeQuery.SQL=SELECT BLD_CODE, &
   		      APPLY_QTY, UNIT_CODE &
		 FROM BMS_BLDTAKED 
TakeQuery.ITEM=BLOOD_TANO
TakeQuery.BLOOD_TANO=BLOOD_TANO=<BLOOD_TANO>
TakeQuery.Debug=N