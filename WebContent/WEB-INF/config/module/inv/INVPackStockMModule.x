   #
   # Title: 手术包库存主档
   #
   # Description: 手术包库存主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insertStockQtyByPack;updateStockQtyByPack;queryStockM;updateQtyBySupReq;updateStatusBySupReq;updateQtyAndStatus;delete;getStockSeq;checkPackCount;insertPack;updateQty;getPackDate;queryPackBatch;getPackDateBatch

 
//手术包打包新增序号管理细项的库存量
insertStockQtyByPack.Type=TSQL
insertStockQtyByPack.SQL=INSERT INTO INV_PACKSTOCKM(&
			   ORG_CODE, PACK_CODE, PACK_SEQ_NO, DESCRIPTION, QTY, &
			   USE_COST, ONCE_USE_COST, STATUS, OPT_USER, OPT_DATE, OPT_TERM) &
	                 VALUES( &
			   <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <DESCRIPTION>, <QTY>, &
			   <USE_COST>, <ONCE_USE_COST>, <STATUS>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertStockQtyByPack.Debug=N


//手术包打包更新序号管理细项的库存量
updateStockQtyByPack.Type=TSQL
updateStockQtyByPack.SQL=UPDATE INV_PACKSTOCKM SET  QTY=QTY+<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateStockQtyByPack.Debug=N


//查询手术包主档  GYSUsed
queryStockM.Type=TSQL
queryStockM.SQL=SELECT B.PACK_DESC, A.PACK_SEQ_NO, A.QTY, A.STATUS, B.USE_COST, A.ONCE_USE_COST, &
       		       A.DESCRIPTION, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.ORG_CODE, A.PACK_CODE, A.PACK_BATCH_NO &
  	          FROM INV_PACKSTOCKM A, INV_PACKM B &
  	         WHERE A.PACK_CODE = B.PACK_CODE
queryStockM.ITEM=ORG_CODE;PACK_CODE;PACK_SEQ_NO;STATUS
queryStockM.ORG_CODE=A.ORG_CODE=<ORG_CODE>
queryStockM.PACK_CODE=A.PACK_CODE=<PACK_CODE>
queryStockM.PACK_SEQ_NO=A.PACK_SEQ_NO=<PACK_SEQ_NO>
queryStockM.STATUS=A.STATUS=<STATUS>
queryStockM.Debug=N


//没有序号管理的手术包,扣除主库中手术包的库存量
updateQtyBySupReq.Type=TSQL
updateQtyBySupReq.SQL=UPDATE INV_PACKSTOCKM SET  QTY=QTY-<QTY>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO> AND PACK_BATCH_NO = <PACK_BATCH_NO>
updateQtyBySupReq.Debug=N


//有序号管理的手术包,更新手术包的状态为出库
updateStatusBySupReq.Type=TSQL
updateStatusBySupReq.SQL=UPDATE INV_PACKSTOCKM SET STATUS=<STATUS>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateStatusBySupReq.Debug=N


//没有序号管理的手术包,增加主库中手术包的库存量及变更状态
updateQtyAndStatus.Type=TSQL
updateQtyAndStatus.SQL=UPDATE INV_PACKSTOCKM SET QTY=QTY+<STOCK_QTY>, STATUS=<STATUS> , &
				DISINFECTION_DATE=<DISINFECTION>, VALUE_DATE=<VALUE_DATE>, DISINFECTION_USER=<DISINFECTION_USER>, &
			        OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	    	          WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateQtyAndStatus.Debug=N


//删除全部明细
delete.Type=TSQL
delete.SQL=DELETE FROM INV_PACKSTOCKM WHERE ORG_CODE =<ORG_CODE> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
delete.Debug=N


//查询手术包序号GYSUsed
getStockSeq.Type=TSQL
getStockSeq.SQL=SELECT MAX(PACK_SEQ_NO) FROM INV_PACKSTOCKM 
getStockSeq.item=PACK_CODE
getStockSeq.PACK_CODE=PACK_CODE=<PACK_CODE>
getStockSeq.Debug=N



//查找手术包是否存在GYSUsed
checkPackCount.Type=TSQL
checkPackCount.SQL=SELECT PACK_CODE FROM INV_PACKSTOCKM 
checkPackCount.item=PACK_CODE
checkPackCount.PACK_CODE=PACK_CODE=<PACK_CODE>
checkPackCount.Debug=N


//插入新的手术包GYSUsed
insertPack.Type=TSQL
insertPack.SQL=INSERT INTO INV_PACKSTOCKM &
                           (ORG_CODE,PACK_CODE,PACK_SEQ_NO,DESCRIPTION,QTY,DISINFECTION_DATE,&
                            VALUE_DATE,DISINFECTION_USER,USE_COST,ONCE_USE_COST,STATUS,&  
                            OPT_USER,OPT_DATE,OPT_TERM,BARCODE,PACK_BATCH_NO,PACK_QTY)&
                     VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<DESCRIPTION>,<QTY>,<DISINFECTION_DATE>,&
                            <VALUE_DATE>,<DISINFECTION_USER>,<USE_COST>,<ONCE_USE_COST>,<STATUS>,&
                            <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<BARCODE>,<PACK_BATCH_NO>,<QTY>)  
insertPack.Debug=N




//更新库存总量GYSUsed
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKM &
                    SET QTY=QTY+<QTY>
updateQty.item=PACK_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.Debug=N



//查找手术包的消毒和效期GYSUsed
getPackDate.Type=TSQL
getPackDate.SQL=SELECT PM.DISINFECTION_DATE,PM.DISINFECTION_USER,PM.OPT_USER,PM.OPT_DATE,P.VALUE_DATE,PM.BARCODE FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE
getPackDate.item=PACK_CODE;PACK_SEQ_NO
getPackDate.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
getPackDate.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
getPackDate.Debug=N


//查询诊疗包的批次
queryPackBatch.Type=TSQL
queryPackBatch.SQL=SELECT MAX(PACK_BATCH_NO) AS MAXNO FROM INV_PACKSTOCKM WHERE PACK_CODE = <PACK_CODE>
queryPackBatch.Debug=N



//查找消毒包的消毒和效期GYSUsed -------- fux modify 20140425  
getPackDateBatch.Type=TSQL
getPackDateBatch.SQL=SELECT PM.DISINFECTION_DATE,PM.DISINFECTION_USER,PM.OPT_USER,PM.OPT_DATE,P.VALUE_DATE,PM.BARCODE FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE
getPackDateBatch.item=PACK_CODE;PACK_SEQ_NO;PACK_BATCH_NO
getPackDateBatch.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
getPackDateBatch.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
getPackDateBatch.PACK_BATCH_NO=PM.PACK_BATCH_NO=<PACK_BATCH_NO>
getPackDateBatch.Debug=N





