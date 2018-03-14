Module.item=getStockM;insertStockM;updateStockM;getStockQty;getPackDesc

//查询主库数据
getStockM.Type=TSQL
getStockM.SQL=SELECT * FROM INV_STOCKM 
getStockM.item=INV_CODE;ORG_CODE;
getStockM.INV_CODE=INV_CODE=<INV_CODE>
getStockM.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockM.Debug=N

//插入新物资
insertStockM.Type=TSQL
insertStockM.SQL=INSERT INTO INV_STOCKM (ORG_CODE,INV_CODE,REGION_CODE,&
                                         STOCK_FLG,STOCK_QTY,OPT_USER,&
                                         OPT_DATE,OPT_TERM)&
                                  VALUES (<ORG_CODE>,<INV_CODE>,<REGION_CODE>,&
                                          <STOCK_FLG>,<STOCK_QTY>,<OPT_USER>,&
                                          <OPT_DATE>,<OPT_TERM>)
insertStockM.Debug=N


//更新库存总量
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET STOCK_QTY=<STOCK_QTY>
updateStockM.item=INV_CODE;ORG_CODE
updateStockM.INV_CODE=INV_CODE=<INV_CODE>
updateStockM.ORG_CODE=ORG_CODE=<ORG_CODE>
updateStockM.Debug=N


//查找全院物资库存量
getStockQty.Type=TSQL
getStockQty.SQL=SELECT SUM(STOCK_QTY) FROM INV_STOCKM 
getStockQty.item=INV_CODE;ORG_CODE;
getStockQty.INV_CODE=INV_CODE=<INV_CODE>
getStockQty.ORG_CODE=ORG_CODE=<ORG_CODE>
getStockQty.Debug=N


//查找手术包名称GYSUsed
getPackDesc.Type=TSQL
getPackDesc.SQL=SELECT PACK_DESC FROM INV_PACKM 
getPackDesc.item=IPACK_CODE
getPackDesc.IPACK_CODE=PACK_CODE=<IPACK_CODE>       
getPackDesc.Debug=Y




