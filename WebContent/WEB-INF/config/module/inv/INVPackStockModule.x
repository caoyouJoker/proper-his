Module.item=getStockSeq;updateQty;checkPackCount;insertPack;getPackQty;updatePackStatus;deletePack;getPackDate;getSumPackQty;updatePackStockMQty


//查询手术包序号
getStockSeq.Type=TSQL
getStockSeq.SQL=SELECT MAX(PACK_SEQ_NO) FROM INV_PACKSTOCKM 
getStockSeq.item=PACK_CODE
getStockSeq.PACK_CODE=PACK_CODE=<PACK_CODE>
getStockSeq.Debug=Y



//更新库存总量
updateQty.Type=TSQL
updateQty.SQL=UPDATE INV_PACKSTOCKM &
                    SET QTY=QTY+<QTY>
updateQty.item=PACK_CODE
updateQty.PACK_CODE=PACK_CODE=<PACK_CODE>
updateQty.Debug=Y


//查找手术包是否存在
checkPackCount.Type=TSQL
checkPackCount.SQL=SELECT PACK_CODE FROM INV_PACKSTOCKM 
checkPackCount.item=PACK_CODE
checkPackCount.PACK_CODE=PACK_CODE=<PACK_CODE>
checkPackCount.Debug=Y


//插入新的手术包
insertPack.Type=TSQL
insertPack.SQL=INSERT INTO INV_PACKSTOCKM &
                           (ORG_CODE,PACK_CODE,PACK_SEQ_NO,DESCRIPTION,QTY,DISINFECTION_DATE,&
                        	DISINFECTION_VALID_DATE,DISINFECTION_USER,USE_COST,ONCE_USE_COST,STATUS,&
                            OPT_USER,OPT_DATE,OPT_TERM)&
                     VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<DESCRIPTION>,<QTY>,<DISINFECTION_DATE>,&
                            <DISINFECTION_VALID_DATE>,<DISINFECTION_USER>,<USE_COST>,<ONCE_USE_COST>,<STATUS>,&
                            <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertPack.Debug=Y

//查找不是序号管理的手术包
getPackQty.Type=TSQL
getPackQty.SQL=SELECT QTY FROM INV_PACKSTOCKM 
getPackQty.item=PACK_CODE
getPackQty.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackQty.Debug=Y



//更新手术包的状态
updatePackStatus.Type=TSQL
updatePackStatus.SQL=UPDATE INV_PACKSTOCKM &
                        SET STATUS=<STATUS>
updatePackStatus.item=PACK_CODE;PACK_SEQ_NO
updatePackStatus.PACK_CODE=PACK_CODE=<PACK_CODE>
updatePackStatus.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
updatePackStatus.Debug=Y




//删除手术包
deletePack.Type=TSQL
deletePack.SQL=DELETE INV_PACKSTOCKM &
                        WHERE PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
deletePack.Debug=Y

//查找手术包的消毒和效期
getPackDate.Type=TSQL
getPackDate.SQL=SELECT DISINFECTION_DATE,DISINFECTION_VALID_DATE,DISINFECTION_USER,OPT_USER FROM INV_PACKSTOCKM 
getPackDate.item=PACK_CODE;PACK_SEQ_NO
getPackDate.PACK_CODE=PACK_CODE=<PACK_CODE>
getPackDate.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
getPackDate.Debug=Y


//手术包的数量
getSumPackQty.Type=TSQL
getSumPackQty.SQL=SELECT SUM(QTY) QTY FROM INV_PACKSTOCKM WHERE PACK_CODE=<PACK_CODE> AND STATUS IN ('0','3')
getSumPackQty.Debug=Y


//更新库存总量
updatePackStockMQty.Type=TSQL
updatePackStockMQty.SQL=UPDATE INV_PACKSTOCKM SET QTY=QTY-<QTY>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		        WHERE PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updatePackStockMQty.Debug=Y








