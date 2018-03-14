  #
   # Title库存养护保养
   #
   # Description:库存养护保养
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author shibl 2011/08/18

Module.item=query;update;delete;insert

//查询库存养护数据
query.Type=TSQL
query.SQL= SELECT * FROM IND_CONSERVATION_MED  WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
query.Debug=N

//插入库存养护数据
insert.Type=TSQL
insert.SQL=INSERT INTO IND_CONSERVATION_MED &
            		  (ORG_CODE, BATCH_SEQ,ORDER_CODE,ORDER_DESC,&
             		   SPECIFICATION, UNIT_CODE, MAN_CODE, BATCH_NO, VALID_DATE, WAS_QTY, WAS_REASON,QUALIFIED_QTY,OPT_USER,OPT_DATE,OPT_TERM) &
     		   VALUES (<ORG_CODE>,<BATCH_SEQ>,<ORDER_CODE>,<ORDER_DESC>,<SPECIFICATION>,&
             		   <UNIT_CODE>, <MAN_CODE>, <BATCH_NO>,TO_DATE(<VALID_DATE>,'YYYYMMDD'), <WAS_QTY>, <WAS_REASON>,<QUALIFIED_QTY>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insert.Debug=N


//更新库存养护数据
update.Type=TSQL
update.SQL=UPDATE IND_CONSERVATION_MED SET &
			  WAS_QTY=<WAS_QTY>,WAS_REASON=<WAS_REASON> &
	            WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
update.Debug=N

//删除库存养护数据
delete.Type=TSQL
delete.SQL=DELETE FROM IND_CONSERVATION_MED  WHERE ORG_CODE=<ORG_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND ORDER_CODE=<ORDER_CODE>
delete.Debug=N			