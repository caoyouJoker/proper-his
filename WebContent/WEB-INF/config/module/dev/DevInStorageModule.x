# 
#  Title:设备入库操作module
# 
#  Description:设备入库操作module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author fux 2013.07.15 
#  version 1.0 
#
Module.item=selectDevInStorageInf1;selectDevInStorageInf2;selectDevReceipt;selectReceiptD;insertDevInwarehouseD;insertDevInwarehouseDD;insertDevInwarehouseM;selectDevInwarehouseD;selectDevInwarehouseDD;updateDevInwarehouseD;updateDevInwarehouseDD;getStock;insertStock;insertStockD;updateStock;updateStockDSumQty;getMaxDevSeqNo;getCountOfStock;getCountOfStockD;updateStockD;updateStockDD;getCountOfStockD;deleteinwarehoused;deleteinwarehousedd;insertStockDD;selectDevexwareHouse;selectDevexwarehouseD;selectDevexwarehouseDD;updateDevReceiptD;updateDevReceiptM;UpdateDevExWareHouseD;UpdateDevExWareHouseM;selectDevexwarehouseM

//检索设备主库信息1 
selectDevInStorageInf1.Type=TSQL updateStock
selectDevInStorageInf1.SQL=SELECT A.INWAREHOUSE_NO, A.VERIFY_NO,A.INWAREHOUSE_DATE,A.INWAREHOUSE_USER, &
                                  A.INWAREHOUSE_DEPT, B.RECEIPT_DATE &
	                   FROM DEV_INWAREHOUSEM A, DEV_RECEIPTM B &
	                   WHERE A.VERIFY_NO = B.RECEIPT_NO 
selectDevInStorageInf1.item=INWAREHOUSE_NO;INWARE_START_DATE;INWAREHOUSE_DEPT;INWAREHOUSE_USER;RECEIPT_NO;CHECK_FLG;RECEIPT_START_DATE
//入库单号
selectDevInStorageInf1.INWAREHOUSE_NO=A.INWAREHOUSE_NO LIKE <INWAREHOUSE_NO>||'%'
//入库日期  
selectDevInStorageInf1.INWARE_START_DATE=A.INWAREHOUSE_DATE BETWEEN <INWARE_START_DATE> AND <INWARE_END_DATE>
//入库科室
selectDevInStorageInf1.INWAREHOUSE_DEPT=A.INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>  
//入库人员
selectDevInStorageInf1.INWAREHOUSE_USER=A.INWAREHOUSE_USER = <INWAREHOUSE_USER>
//验收单号
selectDevInStorageInf1.RECEIPT_NO=A.VERIFY_NO = <RECEIPT_NO>
//审核入库标记
selectDevInStorageInf1.CHECK_FLG=A.CHECK_FLG = <CHECK_FLG>
//验收日期
selectDevInStorageInf1.RECEIPT_START_DATE=B.RECEIPT_DATE BETWEEN <RECEIPT_START_DATE> AND <RECEIPT_END_DATE>
selectDevInStorageInf1.Debug=N   


 
//检索设备主库信息2
selectDevInStorageInf2.Type=TSQL
selectDevInStorageInf2.SQL=SELECT A.INWAREHOUSE_NO,A.VERIFY_NO,A.INWAREHOUSE_DATE,A.INWAREHOUSE_USER, &
                                  A.INWAREHOUSE_DEPT, '' RECEIPT_DATE &
	                   FROM   DEV_INWAREHOUSEM A 
selectDevInStorageInf2.item=INWAREHOUSE_NO;INWARE_START_DATE;INWAREHOUSE_DEPT;INWAREHOUSE_USER;RECEIPT_NO;CHECK_FLG;RECEIPT_START_DATE
//入库单号
selectDevInStorageInf2.INWAREHOUSE_NO=A.INWAREHOUSE_NO LIKE <INWAREHOUSE_NO>||'%'
//入库日期
selectDevInStorageInf2.INWARE_START_DATE=A.INWAREHOUSE_DATE BETWEEN <INWARE_START_DATE> AND <INWARE_END_DATE>
//入库科室
selectDevInStorageInf2.INWAREHOUSE_DEPT=A.INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>
//入库人员
selectDevInStorageInf2.INWAREHOUSE_USER=A.INWAREHOUSE_USER = <INWAREHOUSE_USER>
//验收单号
selectDevInStorageInf2.RECEIPT_NO=A.VERIFY_NO = <RECEIPT_NO>
//审核入库标记
selectDevInStorageInf2.CHECK_FLG=A.CHECK_FLG = <CHECK_FLG>
//验收日期
selectDevInStorageInf2.RECEIPT_START_DATE=B.RECEIPT_DATE BETWEEN <RECEIPT_START_DATE> AND <RECEIPT_END_DATE>
selectDevInStorageInf2.Debug=N 



//检索验收单信息
selectDevReceipt.Type=TSQL
selectDevReceipt.SQL=SELECT A.RECEIPT_NO,A.PURORDER_NO,A.RECEIPT_DATE,A.SUP_CODE,A.REMARK,&
                            A.RECEIPT_DEPT,A.RECEIPT_USER,A.INVOICE_NO,A.INVOICE_DATE,A.INVOICE_AMT,&
                            A.RECEIPT_MINUTE,B.SUP_SALES1,B.SUP_SALES1_TEL &
		      FROM DEV_RECEIPTM A, SYS_SUPPLIER B &
		      WHERE A.SUP_CODE = B.SUP_CODE(+) &    
                            ORDER BY A.RECEIPT_NO
selectDevReceipt.item=RECEIPT_NO;INVOICE_NO;RECEIPT_DATE_BEGIN;RECEIPT_DEPT;RECEIPT_USER;SUP_CODE;FINAL_FLG
//验收单号
selectDevReceipt.RECEIPT_NO=A.RECEIPT_NO=<RECEIPT_NO>
//收据号     
selectDevReceipt.INVOICE_NO=A.INVOICE_NO=<INVOICE_NO>
//验收时间
selectDevReceipt.RECEIPT_DATE_BEGIN=A.RECEIPT_DATE BETWEEN <RECEIPT_DATE_BEGIN> AND <RECEIPT_DATE_END>
//验收科室
selectDevReceipt.RECEIPT_DEPT=A.RECEIPT_DEPT = <RECEIPT_DEPT>
//验收人员
selectDevReceipt.RECEIPT_USER=A.RECEIPT_USER = <RECEIPT_USER>
//供应厂商
selectDevReceipt.SUP_CODE=A.SUP_CODE = <SUP_CODE>
//完成状态  
selectDevReceipt.FINAL_FLG=A.FINAL_FLG = <FINAL_FLG>   
selectDevReceipt.Debug=N                    
 


//检索验收单明细信息   fux modify 入库单校验 去掉验收数<订购数校验     入库数  累计入库数  验收数
selectReceiptD.Type=TSQL  
selectReceiptD.SQL=SELECT B.DEVPRO_CODE, B.DEV_CODE,B.DEV_CHN_DESC,A.SPECIFICATION, A.MAN_CODE,&
	                  A.QTY AS QTY,A.RECEIPT_QTY, A.SUM_QTY, B.UNIT_CODE, A.UNIT_PRICE,&
		          B.DEPR_METHOD, B.USE_DEADLINE, B.MAN_NATION, B.SEQMAN_FLG, B.MEASURE_FLG,&
		          B.BENEFIT_FLG,A.SEQ_NO,B.DEVKIND_CODE,A.MODEL,A.BRAND  &  
		   FROM   DEV_RECEIPTD A, DEV_BASE B &        
		   WHERE  A.RECEIPT_NO=<RECEIPT_NO> &
		   AND    A.DEV_CODE=B.DEV_CODE     
selectReceiptD.Debug=N 


//取得批号
//getBatchNo.Type=TSQL  
//getBatchNo.SQL=SELECT BATCH_SEQ &   
//	       FROM   DEV_INWAREHOUSED &
//	       WHERE  DEV_CODE = <DEV_CODE> & 
//	       AND    DEP_DATE = <DEP_DATE> &
//	       AND    GUAREP_DATE = <GUAREP_DATE>
//getBatchNo.Debug=N 


//取得最大批号 
//getMaxBatchNo.Type=TSQL 
//getMaxBatchNo.SQL=SELECT MAX(BATCH_SEQ) BATCH_SEQ &
//	          FROM   DEV_INWAREHOUSED &      
//	          WHERE  DEV_CODE=<DEV_CODE>
//getMaxBatchNo.Debug=N


//写入库存明细档
insertDevInwarehouseD.Type=TSQL 
insertDevInwarehouseD.SQL=INSERT INTO DEV_INWAREHOUSED ( INWAREHOUSE_NO, SEQ_NO, DEV_CODE, SEQMAN_FLG, &
                                                         QTY, UNIT_PRICE, MAN_DATE, SCRAP_VALUE, GUAREP_DATE, &
                                                         DEP_DATE, FILES_WAY, VERIFY_NO, VERIFY_NO_SEQ, OPT_USER, &
                                                         OPT_DATE, OPT_TERM ,BRAND ,SPECIFICATION,MODEL)& 
                                                VALUES ( <INWAREHOUSE_NO>, <SEQ_NO>, <DEV_CODE>, <SEQMAN_FLG>, &
                                                         <QTY>, <UNIT_PRICE>, <MAN_DATE>, <SCRAP_VALUE>, <GUAREP_DATE>, &
                                                         <DEP_DATE>, <FILES_WAY>, <VERIFY_NO>, <VERIFY_NO_SEQ>, <OPT_USER>, &
                                                         <OPT_DATE>, <OPT_TERM> ,<BRAND> ,<SPECIFICATION>,<MODEL>) 
insertDevInwarehouseD.Debug=N

//写入库存序号明细档    
insertDevInwarehouseDD.Type=TSQL  
insertDevInwarehouseDD.SQL=INSERT INTO DEV_INWAREHOUSEDD ( INWAREHOUSE_NO, SEQ_NO, DEVSEQ_NO,DEV_CODE ,DEV_CODE_DETAIL, SETDEV_CODE, MAN_DATE, MANSEQ_NO, &
                                                           SCRAP_VALUE, GUAREP_DATE,DEP_DATE,UNIT_PRICE,OPT_USER, OPT_DATE, OPT_TERM, RFID , BARCODE, &
							   SERIAL_NUM,WIRELESS_IP,IP,TERM,LOC_CODE) &
							   VALUES ( <INWAREHOUSE_NO>, <SEQ_NO>, <DEVSEQ_NO>, <DEV_CODE>, <DEV_CODE_DETAIL>, <SETDEV_CODE>, &
							   <MAN_DATE>, <MANSEQ_NO>, <SCRAP_VALUE>, <GUAREP_DATE>,<DEP_DATE>,<UNIT_PRICE>,<OPT_USER>, <OPT_DATE>,  &
							   <OPT_TERM>,<RFID>,<BARCODE>, &
							   <SERIAL_NUM>,<WIRELESS_IP>,<IP>,<TERM>,<LOC_CODE>)
insertDevInwarehouseDD.Debug=N  



 
//写入库存主档
insertDevInwarehouseM.Type=TSQL
insertDevInwarehouseM.SQL=INSERT INTO DEV_INWAREHOUSEM ( INWAREHOUSE_NO,VERIFY_NO,INWAREHOUSE_DATE,INWAREHOUSE_USER,INWAREHOUSE_DEPT,OPT_USER,OPT_DATE,OPT_TERM ) &
                          VALUES ( <INWAREHOUSE_NO>,<VERIFY_NO>,<INWAREHOUSE_DATE>,<INWAREHOUSE_USER>,<INWAREHOUSE_DEPT>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertDevInwarehouseM.Debug=N


//更新库存主档
updateDevInwarehouseM.Type=TSQL   
updateDevInwarehouseM.SQL=UPDATE DEV_INWAREHOUSEM SET INWAREHOUSE_NO = <INWAREHOUSE_NO>,VERIFY_NO = <VERIFY_NO>,INWAREHOUSE_DATE = <INWAREHOUSE_DATE>, &
                                                      INWAREHOUSE_USER = <INWAREHOUSE_USER>,INWAREHOUSE_DEPT = <INWAREHOUSE_DEPT>,OPT_USER = <OPT_USER>,&
                                                      OPT_DATE = <OPT_DATE>,OPT_TERM = <OPT_TERM> &
                                                WHERE INWAREHOUSE_NO = <INWAREHOUSE_NO>
updateDevInwarehouseM.Debug=N

//更新库存明细档
updateDevInwarehouseD.Type=TSQL
updateDevInwarehouseD.SQL=UPDATE DEV_INWAREHOUSED SET INWAREHOUSE_NO = <INWAREHOUSE_NO>, SEQ_NO = <SEQ_NO>, DEV_CODE = <DEV_CODE>,&
                                                      SEQMAN_FLG = <SEQMAN_FLG>,QTY = <QTY>, &
                                                      UNIT_PRICE = <UNIT_PRICE>, MAN_DATE = <MAN_DATE>, SCRAP_VALUE = <LAST_PRICE>,&
                                                      GUAREP_DATE = <GUAREP_DATE>,DEP_DATE = <DEP_DATE>, FILESWAY = <FILES_WAY>,&
                                                      VERIFY_NO = <VERIFY_NO>, VERIFY_NO_SEQ = <VERIFY_NO_SEQ>, OPT_USER = <OPT_USER>, &
                                                      OPT_DATE = <OPT_DATE>, OPT_TERM = <OPT_TERM> ,BRAND = <BRAND> ,&
						      SPECIFICATION =<SPECIFICATION> , MODEL = <MODEL>&
                                                WHERE INWAREHOUSE_NO = <INWAREHOUSE_NO> &
                                                AND   SEQ_NO = <SEQ_NO> 
updateDevInwarehouseD.Debug=N


//更新库存序号明细档
updateDevInwarehouseDD.Type=TSQL
updateDevInwarehouseDD.SQL=UPDATE DEV_INWAREHOUSEDD SET INWAREHOUSE_NO = <INWAREHOUSE_NO>, SEQ_NO = <SEQ_NO>, &
                                                        DEV_CODE = <DEV_CODE>,&
                                                        SETDEV_CODE = <MAIN_DEV>, MAN_DATE = <MAN_DATE>, MANSEQ_NO = <MAN_SEQ>, &
                                                        SCRAP_VALUE = <LAST_PRICE>, GUAREP_DATE = <GUAREP_DATE>, DEP_DATE = <DEP_DATE>,&
                                                        UNIT_PRICE = <TOT_VALUE>,OPT_USER = <OPT_USER>, OPT_DATE = <OPT_DATE>, &
                                                        OPT_TERM = <OPT_TERM>,SERIAL_NUM = <SERIAL_NUM>,WIRELESS_IP =<WIRELESS_IP>, &
							IP =<IP>,TERM =<TERM>,LOC_CODE =<LOC_CODE> &
                                            WHERE   INWAREHOUSE_NO = <INWAREHOUSE_NO> & 
					    AND     SEQ_NO = <SEQ_NO> &
					    AND     DDSEQ_NO = <DDSEQ_NO>

updateDevInwarehouseDD.Debug=N


 
//查询已入库的入库明细             
selectDevInwarehouseD.Type=TSQL 
selectDevInwarehouseD.SQL=SELECT 'N' DEL_FLG,'N' SELECT_FLG,'N' PRINT_FLG,C.RECEIPT_NO VERIFY_NO,C.SEQ_NO VERIFY_NO_SEQ, &
                                  D.DEVPRO_CODE,B.DEV_CODE, D.DEV_CHN_DESC,B.SPECIFICATION, &
                                  D.MAN_CODE,B.QTY,C.SUM_QTY,C.RECEIPT_QTY,D.UNIT_CODE, &
                                  B.UNIT_PRICE,B.QTY * B.UNIT_PRICE TOT_VALUE,B.MAN_DATE,B.SCRAP_VALUE LAST_PRICE,B.GUAREP_DATE, &
                                  D.DEPR_METHOD,D.USE_DEADLINE,B.DEP_DATE,D.MAN_NATION,D.SEQMAN_FLG, &
                                  D.MEASURE_FLG,D.BENEFIT_FLG,B.FILES_WAY,B.INWAREHOUSE_NO,B.SEQ_NO,D.DEVKIND_CODE  &
                          FROM   DEV_INWAREHOUSEM A,DEV_INWAREHOUSED B,DEV_RECEIPTD C,DEV_BASE D &
                          WHERE  A.INWAREHOUSE_NO = <INWAREHOUSE_NO>  &  
                          AND    A.INWAREHOUSE_NO = B.INWAREHOUSE_NO  &   
                          AND    B.VERIFY_NO = C.RECEIPT_NO(+)  &
                          AND    B.VERIFY_NO_SEQ = C.SEQ_NO(+)  &
                          AND    B.DEV_CODE = D.DEV_CODE 
selectDevInwarehouseD.Debug=N

      
     
//查询已入库的入库序号管理明细
selectDevInwarehouseDD.Type=TSQL
selectDevInwarehouseDD.SQL=SELECT 'N' DEL_FLG,'Y' SELECT_FLG,'Y' PRINT_FLG,C.DEVPRO_CODE ,B.DEV_CODE, &
                                  C.DEV_CHN_DESC,B.SETDEV_CODE MAIN_DEV,B.MAN_DATE,B.MANSEQ_NO MAN_SEQ,B.SCRAP_VALUE LAST_PRICE, &
                                  B.GUAREP_DATE,B.DEP_DATE,B.UNIT_PRICE TOT_VALUE,B.INWAREHOUSE_NO,B.SEQ_NO, &
                                  B.DEVSEQ_NO, B.RFID,B.BARCODE,B.DEV_CODE_DETAIL  &    
                          FROM    DEV_INWAREHOUSEM A,DEV_INWAREHOUSEDD B,DEV_BASE C & 
                          WHERE   A.INWAREHOUSE_NO = <INWAREHOUSE_NO> &
                          AND     A.INWAREHOUSE_NO = B.INWAREHOUSE_NO &  
                          AND     B.DEV_CODE = C.DEV_CODE
selectDevInwarehouseDD.Debug=N

   

//取得库存科室
getStock.Type=TSQL
getStock.SQL=SELECT DEPT_CODE &
             FROM DEV_STOCKD &   
             WHERE DEPT_CODE=<DEPT> &
             AND   DEV_CODE=<DEV_CODE> 
getStock.Debug=N


//更新库存量--DEV_STOCKM
updateStock.Type=TSQL
updateStock.SQL=UPDATE DEV_STOCKM SET QTY=QTY+<QTY>,&
                                      OPT_USER=<OPT_USER>, &
                                      OPT_DATE=<OPT_DATE>, &
                                      OPT_TERM=<OPT_TERM>  &
                WHERE  DEV_CODE=<DEV_CODE>  
updateStock.Debug=N  
    
//更新库存量--DEV_STOCKD    
updateStockD.Type=TSQL 
updateStockD.SQL=UPDATE DEV_STOCKD SET QTY=QTY+<QTY>,&
                                      OPT_USER=<OPT_USER>,&
                                      OPT_DATE=<OPT_DATE>,&
                                      OPT_TERM=<OPT_TERM> &
                WHERE DEPT_CODE=<DEPT_CODE> & 
                AND   DEV_CODE=<DEV_CODE>
updateStockD.Debug=N








//写入库存主档信息
insertStock.Type=TSQL
insertStock.SQL=INSERT INTO DEV_STOCKM (REGION_CODE, DEV_CODE, &    
                                        STOCK_FLG, QTY,OPT_USER, & 
                                        OPT_DATE,OPT_TERM) &                              
                                VALUES (<REGION_CODE>,<DEV_CODE>, &  
                                        <STOCK_FLG>,<QTY>,<OPT_USER>,&
                                        <OPT_DATE>,<OPT_TERM>)         
insertStock.Debug=N     
  

//写入库存明细信息    
insertStockD.Type=TSQL
insertStockD.SQL=INSERT INTO DEV_STOCKD (DEPT_CODE, DEV_CODE,DEVKIND_CODE,DEVTYPE_CODE, & 
                         DEVPRO_CODE, SETDEV_CODE, SPECIFICATION, QTY,UNIT_PRICE, &
                         BUYWAY_CODE, MAN_NATION, MAN_CODE, SUPPLIER_CODE, MAN_DATE, &
                         MANSEQ_NO, FUNDSOURCE, APPROVE_AMT, SELF_AMT,&
                         DEPR_METHOD, SCRAP_VALUE, QUALITY_LEVEL,DEV_CLASS, &    
                         STOCK_STATUS,SERVICE_STATUS, CARE_USER, USE_USER,LOC_CODE, &
                         MEASURE_FLG, MEASURE_ITEMDESC, MEASURE_DATE, OPT_USER, OPT_DATE,  &
                         OPT_TERM, INWAREHOUSE_DATE,BRAND,MODEL) &
                VALUES ( <DEPT_CODE>, <DEV_CODE>,<DEVKIND_CODE>, <DEVTYPE_CODE>, & 
                         <DEVPRO_CODE>, <SETDEV_CODE>, <SPECIFICATION>, <QTY>, <UNIT_PRICE>,&
                         <BUYWAY_CODE>, <MAN_NATION>, <MAN_CODE>, <SUPPLIER_CODE>,<MAN_DATE>,  &
                         <MANSEQ_NO>, <FUNDSOURCE>, <APPROVE_AMT>, <SELF_AMT>, &  
                         <DEPR_METHOD>, <SCRAP_VALUE>, <QUALITY_LEVEL>, <DEV_CLASS>,&
                         <STOCK_STATUS>,<SERVICE_STATUS>, <CARE_USER>, <USE_USER>,<LOC_CODE>, &
                         <MEASURE_FLG>, <MEASURE_ITEMDESC>, <MEASURE_DATE>, <OPT_USER>,<OPT_DATE>, & 
                         <OPT_TERM>, <INWAREHOUSE_DATE>,<BRAND>,<MODEL>)        
insertStockD.Debug=N
  

//写入库存明细信息 
insertStockDD.Type=TSQL   
insertStockDD.SQL=INSERT INTO DEV_STOCKDD (DEV_CODE,DEVSEQ_NO,REGION_CODE,DEPT_CODE,& 
                         STOCK_QTY,STOCK_UNIT,&     
                         CHECKTOLOSE_FLG,WAST_FLG,INWAREHOUSE_DATE,  & 
                         WAIT_ORG_CODE,OPT_USER, OPT_DATE, OPT_TERM, RFID, &
                         BARCODE,ACTIVE_FLG,SETDEV_CODE,UNIT_PRICE,MDEP_PRICE,DEP_PRICE,CURR_PRICE, &
			 MODEL,BRAND,SPECIFICATION,SERIAL_NUM,MAN_CODE, &   
			 MAN_NATION,IP,TERM,LOC_CODE,USE_USER,DEV_CODE_DETAIL,CARE_USER,WIRELESS_IP,GUAREP_DATE,DEP_DATE) & 
                VALUES ( <DEV_CODE>, <DEVSEQ_NO>, <REGION_CODE>, <DEPT_CODE>, & 
                         <STOCK_QTY>, <STOCK_UNIT>,&        
                         <CHECKTOLOSE_FLG>, <WAST_FLG>, <INWAREHOUSE_DATE>, &  
                         <WAIT_ORG_CODE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>, <RFID>, & 
                         <BARCODE>, <ACTIVE_FLG>, <SETDEV_CODE>, <UNIT_PRICE>,<MDEP_PRICE>,<DEP_PRICE>,<CURR_PRICE>, &
			 <MODEL>,<BRAND>,<SPECIFICATION>,<SERIAL_NUM>,<MAN_CODE>, &
			 <MAN_NATION>,<IP>,<TERM>,<LOC_CODE>,<USE_USER>,<DEV_CODE_DETAIL>,<CARE_USER>,<WIRELESS_IP>,<GUAREP_DATE>,<DEP_DATE>)         
insertStockDD.Debug=N 
 
     
//更新验收单累计入库量
updateStockDSumQty.Type=TSQL 
updateStockDSumQty.SQL=UPDATE DEV_RECEIPTD SET SUM_QTY = SUM_QTY + <QTY> &
                       WHERE  RECEIPT_NO = <VERIFY_NO>&
                       AND    SEQ_NO = <VERIFY_NO_SEQ> 
updateStockDSumQty.Debug=N                              


//得到设备最大顺序号
getMaxDevSeqNo.Type=TSQL
getMaxDevSeqNo.SQL=SELECT MAX(DEVSEQ_NO) DEVSEQ_NO &  
                   FROM   DEV_STOCKDD &
                   WHERE  DEV_CODE=<DEV_CODE> 
getMaxDevSeqNo.Debug=N


//得到M表设备个数  主表统计全部最小分类
getCountOfStock.Type=TSQL  
getCountOfStock.SQL=SELECT COUNT(*) as NUM &
                   FROM   DEV_STOCKM 
getCountOfStock.item=DEV_CODE
//;DEPT_CODE 
getCountOfStock.DEV_CODE=DEV_CODE = <DEV_CODE>        
//getCountOfStock.DEPT_CODE =DEPT_CODE = <DEPT_CODE>   
getCountOfStock.Debug=N
 

//得到库存中某设备的记录数--DEV_STOCKD 系表统计全部最小分类与科室
getCountOfStockD.Type=TSQL 
getCountOfStockD.SQL=SELECT COUNT(*) as NUM &
                   FROM   DEV_STOCKD 
getCountOfStockD.item=DEV_CODE;DEPT_CODE
//;BATCH_SEQ   
getCountOfStockD.DEV_CODE=DEV_CODE = <DEV_CODE>      
getCountOfStockD.DEPT_CODE =DEPT_CODE = <DEPT_CODE>   
//getCountOfStockD.BATCH_SEQ =BATCH_SEQ = <BATCH_SEQ> 
getCountOfStockD.Debug=N


//删除库存细表DEV_INWAREHOUSED  
deleteinwarehoused.Type=TSQL
deleteinwarehoused.SQL=DELETE FROM DEV_INWAREHOUSED &
                       WHERE CASE_NO = <INWAREHOUSE_NO> &
                       AND FILE_SEQ = <DEV_CODE> &
                       AND OPT_SEQ = <SEQ_NO>    
deleteinwarehoused.Debug=N

//删除库存序列表DEV_INWAREHOUSEDD
deleteinwarehousedd.Type=TSQL
deleteinwarehousedd.SQL=DELETE FROM DEV_INWAREHOUSEDD &   
                        WHERE INWAREHOUSE_NO = <INWAREHOUSE_NO> &
                        AND DEV_CODE = <DEV_CODE> &
                        AND SEQ_NO= <SEQ_NO>  
deleteinwarehousedd.Debug=N
 

//出库单信息
selectDevexwareHouse.Type=TSQL
selectDevexwareHouse.SQL=SELECT A.EXWAREHOUSE_NO,A.EXWAREHOUSE_DATE, &
                         A.EXWAREHOUSE_DEPT,A.EXWAREHOUSE_USER,A.INWAREHOUSE_DEPT &
                         FROM DEV_EXWAREHOUSEM A
selectDevexwareHouse.item=EXWAREHOUSE_NO;EXWAREHOUSE_DATE;EXWAREHOUSE_DEPT;EXWAREHOUSE_USER
//出库单号
selectDevexwareHouse.EXWAREHOUSE_NO=A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO>
//出库时间
selectDevexwareHouse.EXWAREHOUSE_DATE=A.EXWAREHOUSE_DATE BETWEEN <EXWAREHOUSE_DATE_BEGIN> AND <EXWAREHOUSE_DATE_END>
//出库科室
selectDevexwareHouse.EXWAREHOUSE_DEPT=A.EXWAREHOUSE_DEPT = <EXWAREHOUSE_DEPT>
//出库人员
selectDevexwareHouse.EXWAREHOUSE_USER=A.EXWAREHOUSE_USER = <EXWAREHOUSE_USER>
selectDevexwareHouse.Debug=N
      
//出库单主明细信息 
selectDevexwarehouseM.Type=TSQL
selectDevexwarehouseM.SQL=SELECT A.EXWAREHOUSE_NO,A.EXWAREHOUSE_DATE,A.EXWAREHOUSE_USER,A.INWAREHOUSE_DEPT &
                      FROM     DEV_EXWAREHOUSEM A &
                      WHERE   A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO>   
selectDevexwarehouseM.Debug=N     

//出库单明细信息 
selectDevexwarehouseD.Type=TSQL
selectDevexwarehouseD.SQL=SELECT B.DEVPRO_CODE, B.DEV_CODE,B.DEV_CHN_DESC,A.SPECIFICATION,A.BRAND,A.MODEL,B.MAN_CODE, &
                      A.EXWAREHOUSE_NO, A.QTY,B.UNIT_CODE,B.DEPR_METHOD, B.USE_DEADLINE,A.DEP_DATE,A.GUAREP_DATE, &      
                      B.MAN_NATION, B.SEQMAN_FLG, B.MEASURE_FLG,B.BENEFIT_FLG,A.SEQ_NO,B.DEVKIND_CODE,A.UNIT_PRICE,A.MAN_DATE,  &
		      A.USE_USER,A.LOC_CODE &    
                      FROM   DEV_EXWAREHOUSED A, DEV_BASE B &  
                      WHERE A.DEV_CODE=B.DEV_CODE &                
                      AND   A.EXWAREHOUSE_NO=<EXWAREHOUSE_NO>   
selectDevexwarehouseD.Debug=N 
   
//出库单明细信息(序号管理) 
selectDevexwarehouseDD.Type=TSQL  
selectDevexwarehouseDD.SQL=SELECT B.DEVPRO_CODE, B.DEV_CODE,B.DEV_CHN_DESC,B.SPECIFICATION, B.MAN_CODE, &
                      A.EXWAREHOUSE_NO, 1 AS QTY,B.UNIT_CODE,B.DEPR_METHOD,C.UNIT_PRICE, B.USE_DEADLINE, & 
                      B.MAN_NATION, B.SEQMAN_FLG, B.MEASURE_FLG,B.BENEFIT_FLG,A.SEQ_NO, &  
                      B.DEVKIND_CODE,C.RFID,C.GUAREP_DATE,C.DEP_DATE,C.MAN_DATE,D.DEVSEQ_NO, & 
                      C.MANSEQ_NO,C.BARCODE,D.DEV_CODE_DETAIL, &        
		      D.SERIAL_NUM,D.IP,D.TERM,D.LOC_CODE,D.USE_USER &       
                      FROM   DEV_EXWAREHOUSED A, DEV_BASE B ,DEV_EXWAREHOUSEDD C,DEV_STOCKDD D &     
                      WHERE A.DEV_CODE=B.DEV_CODE &  
                      AND A.DEV_CODE = C.DEV_CODE & 
                      AND C.DEV_CODE = D.DEV_CODE &    
                      AND C.DEVSEQ_NO = D.DEVSEQ_NO &   
                      AND A.EXWAREHOUSE_NO = C.EXWAREHOUSE_NO & 
                      AND A.EXWAREHOUSE_NO = <EXWAREHOUSE_NO>  
selectDevexwarehouseDD.Debug=N   

//入库确认更新STOCK_DD表的DEPT_CODE和WAIT_DEPT_CODE(加上折旧相关)
updateStockDD.Type=TSQL                 
updateStockDD.SQL=UPDATE DEV_STOCKDD SET DEPT_CODE=<DEPT_CODE>, & 
                                      WAIT_ORG_CODE=<WAIT_ORG_CODE>, &
                                      OPT_USER=<OPT_USER>, &
                                      OPT_DATE=<OPT_DATE>, &             
                                      OPT_TERM=<OPT_TERM>  &
                                WHERE DEV_CODE=<DEV_CODE>  &    
								 AND DEV_CODE_DETAIL=<DEV_CODE_DETAIL> &
                                 AND  DEVSEQ_NO=<DEVSEQ_NO> 
updateStockDD.Debug=N  
 
  


//验收细表校验更新DEV_RECEIPTD
updateDevReceiptD.Type=TSQL  
updateDevReceiptD.SQL=UPDATE DEV_RECEIPTD SET FINAL_FLG=<FINAL_FLG>,&
                                      OPT_USER=<OPT_USER>,&
                                      OPT_DATE=<OPT_DATE>,&
                                      OPT_TERM=<OPT_TERM> &
                                WHERE RECEIPT_NO=<RECEIPT_NO> &
                                 AND  SEQ_NO=<SEQ_NO>  
updateDevReceiptD.Debug=N  
                                                

//验收主表校验更新DEV_RECEIPTM 
updateDevReceiptM.Type=TSQL  
updateDevReceiptM.SQL=UPDATE DEV_RECEIPTM SET FINAL_FLG=<FINAL_FLG>,&
                                      OPT_USER=<OPT_USER>,&
                                      OPT_DATE=<OPT_DATE>,& 
                                      OPT_TERM=<OPT_TERM> &  
                                WHERE RECEIPT_NO=<RECEIPT_NO> 
updateDevReceiptM.Debug=N  
                            
   

//验收细表校验更新DEV_EXWAREHOUSED
UpdateDevExWareHouseD.Type=TSQL  
UpdateDevExWareHouseD.SQL=UPDATE DEV_EXWAREHOUSED SET DISCHECK_FLG=<DISCHECK_FLG> &
                                WHERE EXWAREHOUSE_NO=<EXWAREHOUSE_NO> &
                                 AND  SEQ_NO=<SEQ_NO>  
UpdateDevExWareHouseD.Debug=N  
                                                
  
//验收主表校验更新DEV_EXWAREHOUSEM   
UpdateDevExWareHouseM.Type=TSQL    
UpdateDevExWareHouseM.SQL=UPDATE DEV_EXWAREHOUSEM SET DISCHECK_FLG=<DISCHECK_FLG> &
                                WHERE EXWAREHOUSE_NO=<EXWAREHOUSE_NO>  
UpdateDevExWareHouseM.Debug=N









 


 
