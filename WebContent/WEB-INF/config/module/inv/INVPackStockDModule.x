   #
   # Title: 手术包库存明细档
   #
   # Description: 手术包库存明细档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insertStockQtyByPack;updateStockQtyByPack;updateQtyBySupReq;deleteOnceUserInvBySupReq;deleteAll;insertInv;updateQty;changeHOStatus;updateQtyBySupDispense


//手术包打包新增序号管理细项的库存量
insertStockQtyByPack.Type=TSQL
insertStockQtyByPack.SQL=INSERT INTO INV_PACKSTOCKD(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, BATCH_SEQ, &
			   INVSEQ_NO, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, &
			   STOCK_UNIT, ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <BATCH_SEQ>, &
			   <INVSEQ_NO>, <DESCRIPTION>, <RECOUNT_TIME>, <COST_PRICE>, <QTY>, &
			   <STOCK_UNIT>, <ONCE_USE_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertStockQtyByPack.Debug=N


//手术包打包更新序号管理细项的库存量
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_PACKSTOCKD SET  QTY=QTY+<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
updateStockQtyByPack.Debug=N


//没有序号管理的手术包,扣除手术包库存明细中的库存量
updateQtyBySupReq.Type=TSQL
updateQtyBySupReq.SQL=UPDATE INV_PACKSTOCKD SET  QTY=QTY-<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO> AND PACK_BATCH_NO = <PACK_BATCH_NO>
updateQtyBySupReq.Debug=N


//有序号管理的手术包,删除手术包明细中的一次性物资
deleteOnceUserInvBySupReq.Type=TSQL
deleteOnceUserInvBySupReq.SQL=DELETE FROM INV_PACKSTOCKD &  
	    	              WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND INV_CODE=<INV_CODE> AND INVSEQ_NO=<INVSEQ_NO>
deleteOnceUserInvBySupReq.Debug=N       


//删除全部明细
deleteAll.Type=TSQL
deleteAll.SQL=DELETE FROM INV_PACKSTOCKD WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
deleteAll.Debug=N




//插入新物资GYSUsed
insertInv.Type=TSQL
insertInv.SQL=INSERT INTO INV_PACKSTOCKD &
                          (ORG_CODE,PACK_CODE,PACK_SEQ_NO,INV_CODE,INVSEQ_NO,BATCH_SEQ,&
                           DESCRIPTION,RECOUNT_TIME,COST_PRICE,QTY,STOCK_UNIT,&
                           ONCE_USE_FLG,OPT_USER,OPT_DATE,OPT_TERM,BARCODE,PACK_BATCH_NO)&
                    VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<INV_CODE>,<INVSEQ_NO>,<BATCH_SEQ>,&
                           <DESCRIPTION>,<RECOUNT_TIME>,<COST_PRICE>,<QTY>,<STOCK_UNIT>,&
                           <ONCE_USE_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<BARCODE>,<PACK_BATCH_NO>)
insertInv.Debug=N



//更新库存量GYSUsed
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKD SET QTY=<QTY>+QTY, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> 
updateQty.item=PACK_CODE;PACK_SEQ_NO;INV_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
updateQty.INV_CODE=INV_CODE=<INV_CODE>
updateQty.Debug=N


//更新高值且一次性物品
changeHOStatus.Type=TSQL
changeHOStatus.SQL=UPDATE INV_STOCKDD SET WAST_FLG = 'Y' WHERE INV_CODE = <INV_CODE>  AND INVSEQ_NO = <INVSEQ_NO>
changeHOStatus.Debug=N


//没有序号管理的手术包,扣除手术包库存明细中的库存量（新改的供应室出库扣packstockd库存）
updateQtyBySupDispense.Type=TSQL
updateQtyBySupDispense.SQL=UPDATE INV_PACKSTOCKD SET  QTY=QTY-<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND  INV_CODE=<INV_CODE> AND BATCH_SEQ=<BATCH_SEQ> AND PACK_BATCH_NO = <PACK_BATCH_NO>
updateQtyBySupDispense.Debug=N