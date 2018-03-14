# 
#  Title:设备盘点操作module
# 
#  Description:设备盘点操作module
# 
#  Copyright: Copyright (c) Javahis 2012
# 
#  author yuhb 2012.11.27
#  version 1.0
#
Module.item=selectSeqDevInf;updateDevStockM;updateDevStockD

//检索验收单明细信息
selectSeqDevInf.Type=TSQL
selectSeqDevInf.SQL=SELECT B.RFID,A.DEV_CHN_DESC,A.SPECIFICATION,C.QTY, B.DEV_CODE, &
			TO_CHAR(C.OPT_DATE,'yyyy-MM-dd') AS OPT_DATE,C.GUAREP_DATE &
			FROM DEV_BASE A,DEV_RFIDBASE B,DEV_STOCKD C &
			WHERE B.DEV_CODE=A.DEV_CODE AND C.RFID=B.RFID &
			AND C.STOCK_STATUS=0 &
			ORDER BY B.RFID
selectSeqDevInf.item=DEV_KEEPDEPT;DEV_LOCATIONDEPT;DEV_STARTCODE;DEV_ENDCODE;QTY
selectSeqDevInf.DEV_KEEPDEPT=B.DEV_KEEPDEPT = <DEV_KEEPDEPT>
selectSeqDevInf.DEV_LOCATIONDEPT=B.DEV_LOCATIONDEPT = <DEV_LOCATIONDEPT>
selectSeqDevInf.DEV_STARTCODE=B.DEV_CODE BETWEEN <DEV_STARTCODE> AND <DEV_ENDCODE>
selectSeqDevInf.DEV_ENDCODE=<DEV_ENDCODE>
selectSeqDevInf.QTY=C.QTY > <QTY>
selectSeqDevInf.Debug=Y


// 盘点 更新DEV_STOCKM
updateDevStockM.Type=TSQL
updateDevStockM.SQL=UPDATE DEV_STOCKM SET QTY=QTY+(<REAL_NO>-<QTY>) WHERE DEV_CODE=<DEV_CODE>
updateDevStockM.Debug=Y

// 盘点 更新DEV_STOCKD
updateDevStockD.Type=TSQL
updateDevStockD.SQL=UPDATE DEV_STOCKD SET QTY=QTY+(<REAL_NO>-<QTY>) WHERE RFID=<RFID>
updateDevStockD.Debug=Y

      
// 盘点 插入DEV_QTYCHECK    
insertdevQtycheck.Type=TSQL
insertdevQtycheck.SQL=INSERT DEV_QTYCHECK (ORG_CODE, DEV_CODE, BATCH_SEQ, CHECKREASON_CODE, CHECK_TYPE, &
                                                         DOSAGE_UNIT, RETAIL_PRICE, STOCK_QTY, MODI_QTY, ACTUAL_CHECKQTY_DATE, &
                                                         ACTUAL_CHECK_QTY, ACTUAL_CHECKQTY_USER, OPT_USER, &  
                                                         OPT_DATE, OPT_TERM )& 
                                           VALUES ( <ORG_CODE>, <DEV_CODE>, <BATCH_SEQ>, <CHECKREASON_CODE>, <CHECK_TYPE>, &
                                                         <DOSAGE_UNIT>, <RETAIL_PRICE>, <STOCK_QTY>, <MODI_QTY>, <ACTUAL_CHECKQTY_DATE>, &
                                                         <ACTUAL_CHECK_QTY>, <ACTUAL_CHECKQTY_USER>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertdevQtycheck.Debug=Y