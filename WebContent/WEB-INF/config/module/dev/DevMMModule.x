# 
#  Title:主库部门设定module
# 
#  Description:主库部门设定module
# 
#  Copyright: Copyright (c) ProperSoft 2013
# 
#  author fux 20130723 
#  version 1.0
#
Module.item=queryDevMMStock;queryDevMMStockCount;queryDevMonthRKData;queryDevMonthCKTRData;queryDevMonthCKTCData;queryDevMonthTHData;queryDevMonthSHData;queryDevMonthPDData;insertMMStock;updateMMStock;deleteMMStock

   
//REGION_CODE;YYYYMM;DEPT_CODE;DEV_CODE;BATCH_SEQ
//查询相应月份月结数据
queryDevMMStock.Type=TSQL
queryDevMMStock.SQL=SELECT A.REGION_CODE,A.YYYYMM,A.DEPT_CODE,A.BATCH_SEQ,A.MM_IN_QTY, &
                    A.MM_IN_SCRAPAMT,A.MM_OUT_QTY,A.MM_OUT_SCRAPAMT,A.MM_CHECKMODI_QTY,A.MM_CHECKMODI_SCRAPAMT, &
                    A.MM_STOCK_QTY,A.MM_STOCK_SCRAPAMT,A.LAST_MM_STOCK_QTY,A.LAST_MM_STOCK_SCRAPAMT,A.MM_INWAREHOUSE_QTY, &
                    A.MM_INWAREHOUSE_SCRAPAMT,A.MM_REGRESSGOODS_QTY,A.MM_REGRESSGOODS_SCRAPAMT,A.MM_GIFTIN_QTY,A.MM_GIFTIN_SCRAPAMT, &
                    A.MM_GIFTOUT_QTY,A.MM_GIFTOUT_SCRAPAMT,A.MM_WASTE_QTY,A.MM_WASTE_SCRAPAMT,A.MM_SCRAP_VALUE &
                    FROM DEV_MMSTOCK A ,DEV_BASE B &
                    WHERE A.DEV_CODE = B.DEV_CODE
queryDevMMStock.ITEM=REGION_CODE;YYYYMM;DEVKIND_CODE;DEVPRO_CODE;DEV_CLASS;DEPT_CODE
queryDevMMStock.REGION_CODE=A.DEPT_CODE=<REGION_CODE>
queryDevMMStock.YYYYMM=A.YYYYMM=<YYYYMM>
queryDevMMStock.DEVKIND_CODE=B.DEVKIND_CODE=<DEVKIND_CODE>
queryDevMMStock.DEVPRO_CODE=B.DEVPRO_CODE=<DEVPRO_CODE>
queryDevMMStock.DEV_CLASS=B.DEV_CLASS=<DEV_CLASS>
queryDevMMStock.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryDevMMStock.Debug=Y
  
//查询相应月份是否有月结数据 
queryDevMMStockCount.Type=TSQL  
queryDevMMStockCount.SQL=SELECT COUNT(A.DEV_CODE) AS COUNT &
                     FROM DEV_MMSTOCK A,DEV_BASE B &
		     WHERE A.DEV_CODE = B.DEV_CODE 
queryDevMMStockCount.ITEM=REGION_CODE;YYYYMM;DEVKIND_CODE;DEVPRO_CODE;DEV_CLASS;DEPT_CODE
queryDevMMStockCount.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryDevMMStockCount.YYYYMM=A.YYYYMM=<YYYYMM>   
queryDevMMStockCount.DEVKIND_CODE=B.DEVKIND_CODE=<DEVKIND_CODE>
queryDevMMStockCount.DEVPRO_CODE=B.DEVPRO_CODE=<DEVPRO_CODE>
queryDevMMStockCount.DEV_CLASS=B.DEV_CLASS=<DEV_CLASS>
queryDevMMStockCount.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE> 
queryDevMMStockCount.Debug=Y  



//月结入库数据  
queryDevMonthRKData.Type=TSQL
queryDevMonthRKData.SQL=SELECT F.REGION_CODE AS REGION_CODE,A.INWAREHOUSE_DEPT AS DEPT_CODE,D.DEV_CODE AS DEV_CODE,D.BATCH_SEQ AS BATCH_SEQ, &
                        NVL(SUM(D.QTY),0) AS MM_INWAREHOUSE_QTY, &
                        NVL(SUM(D.QTY*C.SCRAP_VALUE),0) AS MM_INWAREHOUSE_SCRAPAMT &
                        FROM DEV_INWAREHOUSEM A,DEV_SYSPARM B,DEV_STOCKD C,DEV_INWAREHOUSED D,DEV_BASE E,DEV_ORG F &
                        WHERE A.INWAREHOUSE_NO = D.INWAREHOUSE_NO &
                        AND A.INWAREHOUSE_DEPT = F.DEPT_CODE &
                        AND C.DEPT_CODE = A.INWAREHOUSE_DEPT &
                        AND D.DEV_CODE = C.DEV_CODE  &
                        AND D.BATCH_SEQ = C.BATCH_SEQ  &
                        AND C.DEV_CODE = E.DEV_CODE &
                        //STOCK_STATUS在途标记?
//                        AND C.STOCK_STATUS = 'N' &
                        GROUP BY D.DEV_CODE,D.BATCH_SEQ,A.INWAREHOUSE_DEPT,F.REGION_CODE ORDER BY D.DEV_CODE  
queryDevMonthRKData.ITEM=INWAREHOUSE_DATE;DEVKIND_CODE        
queryDevMonthRKData.INWAREHOUSE_DATE=A.INWAREHOUSE_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')
queryDevMonthRKData.DEVKIND_CODE=E.DEVKIND_CODE=<DEVKIND_CODE>      
queryDevMonthRKData.Debug=Y



//月结出库调入数据 
queryDevMonthCKTRData.Type=TSQL
queryDevMonthCKTRData.SQL=SELECT F.REGION_CODE AS REGION_CODE,A.INWAREHOUSE_DEPT AS DEPT_CODE,D.DEV_CODE AS DEV_CODE,D.BATCH_SEQ AS BATCH_SEQ, & 
                          NVL(SUM(D.QTY ),0) AS MM_GIFTIN_QTY, &
                          NVL(SUM(D.QTY * C.SCRAP_VALUE ),0) AS MM_GIFTIN_SCRAPAMT & 
                          FROM DEV_EXWAREHOUSEM A,DEV_SYSPARM B,DEV_STOCKD C,DEV_EXWAREHOUSED D,DEV_BASE E,DEV_ORG F & 
                          WHERE A.EXWAREHOUSE_NO = D.EXWAREHOUSE_NO & 
                          AND A.INWAREHOUSE_DEPT = F.DEPT_CODE & 
                          AND C.DEPT_CODE = A.INWAREHOUSE_DEPT & 
                          AND C.DEV_CODE = D.DEV_CODE & 
                          AND C.BATCH_SEQ = D.BATCH_SEQ & 
//                          AND C.STOCK_STATUS = 'N' &
                          AND C.DEV_CODE = E.DEV_CODE & 
                          GROUP BY D.DEV_CODE ,D.BATCH_SEQ,A.INWAREHOUSE_DEPT,F.REGION_CODE ORDER BY D.DEV_CODE 
queryDevMonthCKTRData.ITEM=EXWAREHOUSE_DATE;DEVKIND_CODE        
queryDevMonthCKTRData.EXWAREHOUSE_DATE=A.EXWAREHOUSE_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')
queryDevMonthCKTRData.DEVKIND_CODE=E.DEVKIND_CODE=<DEVKIND_CODE> 
queryDevMonthRKData.Debug=Y


//月结出库调出数据
queryDevMonthCKTCData.Type=TSQL
queryDevMonthCKTCData.SQL=SELECT F.REGION_CODE AS REGION_CODE,A.EXWAREHOUSE_DEPT AS DEPT_CODE ,D.DEV_CODE AS DEV_CODE,D.BATCH_SEQ AS BATCH_SEQ, & 
                          NVL(SUM(D.QTY ),0) AS MM_GIFTOUT_QTY, &  
                          NVL(SUM(D.QTY * C.SCRAP_VALUE ),0) AS MM_GIFTOUT_SCRAPAMT & 
                          FROM DEV_EXWAREHOUSEM A,DEV_SYSPARM B,DEV_STOCKD C,DEV_EXWAREHOUSED D,DEV_BASE E,DEV_ORG F & 
                          WHERE A.EXWAREHOUSE_NO = D.EXWAREHOUSE_NO & 
                          AND A.EXWAREHOUSE_DEPT = F.DEPT_CODE &
                          AND C.DEPT_CODE = A.EXWAREHOUSE_DEPT & 
                          AND C.DEV_CODE = D.DEV_CODE &   
                          AND C.BATCH_SEQ = D.BATCH_SEQ & 
//                          AND C.STOCK_STATUS = 'N' & 
                          AND C.DEV_CODE = E.DEV_CODE & 
                          GROUP BY D.DEV_CODE ,D.BATCH_SEQ,A.EXWAREHOUSE_DEPT,F.REGION_CODE ORDER BY D.DEV_CODE 
queryDevMonthCKTCData.ITEM=EXWAREHOUSE_DATE;DEVKIND_CODE        
queryDevMonthCKTCData.EXWAREHOUSE_DATE=A.EXWAREHOUSE_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')
queryDevMonthCKTCData.DEVKIND_CODE=E.DEVKIND_CODE=<DEVKIND_CODE>
queryDevMonthCKTCData.Debug=Y
 


////月结退货数据
//queryDevMonthTHData.Type=TSQL
//queryDevMonthTHData.SQL= SELECT D.HOSP_AREA AS HOSP_AREA,A.REGRESSGOODS_DEPT AS DEPT_CODE,D.DEV_CODE AS DEV_CODE,D.BATCH_SEQ AS BATCH_SEQ, & 
//                         NVL(SUM(D.QTY ),0) AS MM_REGRESSGOODS_QTY, & 
//                         NVL(SUM(D.QTY * C.SCRAP_VALUE ),0) AS MM_REGRESSGOODS_SCRAPAMT &  
//                         FROM DEV_REGRESSGOODSM A,DEV_SYSPARM B,DEV_STOCKM C,DEV_REGRESSGOODSD D,DEV_BASE E,DEV_ORG F & 
//                         WHERE (A.REGRESSGOODS_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')) & 
//                         AND A.REGRESSGOODS_NO = D.REGRESSGOODS_NO  &
//                         AND A.REGRESSGOODS_DEPT = F.DEPT_CODE &
//                         AND C.DEPT_CODE = A.REGRESSGOODS_DEPT AND C.DEV_CODE = D.DEV_CODE AND C.BATCH_SEQ = D.BATCH_SEQ AND C.STOCK_FLG = 'N' &
//                         AND C.DEV_CODE = E.DEV_CODE AND (E.DEVKIND_CODE = '<YLSB>' OR E.DEVKIND_CODE = '<XXSB>' OR E.DEVKIND_CODE = '<GDZC>') &
//                         GROUP BY D.DEV_CODE,D.BATCH_SEQ,A.REGRESSGOODS_DEPT,D.HOSP_AREA ORDER BY D.DEV_CODE"
//queryDevMonthTHData.Debug=Y


////损耗初始化
//queryDevMonthSHData.Type=TSQL
//queryDevMonthSHData.SQL= SELECT D.HOSP_AREA AS HOSP_AREA,A.WASTE_DEPT AS DEPT_CODE,D.DEV_CODE,D.BATCH_SEQ AS BATCH_SEQ, &
//                         NVL(SUM(D.QTY ),0) AS MM_WASTE_QTY, &
//                         NVL(SUM(D.QTY * C.SCRAP_VALUE ),0) AS MM_WASTE_SCRAPAMT &
//                         FROM DEV_WASTEM A,DEV_SYSPARM B,DEV_STOCKM C,DEV_WASTED D,DEV_BASE E,DEV_ORG F &
//                         WHERE (A.WASTE_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')) &
//                         AND A.WASTE_NO = D.WASTE_NO &
//                         AND A.WASTE_DEPT = F.DEPT_CODE &
//                         AND C.DEPT_CODE = A.WASTE_DEPT &
//                         AND C.DEV_CODE = D.DEV_CODE AND C.BATCH_SEQ = D.BATCH_SEQ AND C.STOCK_FLG = 'N' &
//                         AND C.DEV_CODE = E.DEV_CODE AND (E.DEVKIND_CODE = '<YLSB>' OR E.DEVKIND_CODE = '<XXSB>' OR E.DEVKIND_CODE = '<GDZC>') &
//                         GROUP BY D.DEV_CODE,D.BATCH_SEQ,A.WASTE_DEPT,D.HOSP_AREA ORDER BY D.DEV_CODE
//queryDevMonthSHData.Debug=Y
  

////盘点初始化 
//queryDevMonthPDData.Type=TSQL
//queryDevMonthPDData.SQL= SELECT F.REGION_CODE AS REGION_CODE,A.DEPT_CODE AS DEPT_CODE,A.DEV_CODE AS DEV_CODE,A.BATCH_SEQ AS BATCH_SEQ, &
//                         NVL(SUM(A.CHECK_PHASE_QTY),0) AS MM_CHECKMODI_QTY, & 
//                         NVL(SUM(A.CHECK_PHASE_QTY * A.CHECK_PHASE_AMT),0) AS MM_CHECKMODI_SCRAPAMT &  
//                         FROM DEV_QTYCHECK A,DEV_SYSPARM B,DEV_STOCKD C,DEV_BASE D,DEV_ORG F &  
//                         WHERE A.DEPT_CODE = F.DEPT_CODE & 
//                         AND C.DEPT_CODE = A.DEPT_CODE &  
//                         AND C.DEV_CODE = A.DEV_CODE &   
//                         AND C.BATCH_SEQ = A.BATCH_SEQ & 
//                         AND C.STOCK_STATUS = 'N' &     
//                         AND C.DEV_CODE = D.DEV_CODE & 
//                         GROUP BY A.DEV_CODE,A.BATCH_SEQ,A.DEPT_CODE,F.REGION_CODE ORDER BY A.DEV_CODE 
//queryDevMonthPDData.ITEM=UNFREEZE_DATE;DEVKIND_CODE        
//queryDevMonthPDData.UNFREEZE_DATE=A.UNFREEZE_DATE BETWEEN TO_DATE('<START_DATE>'||B.MM_DAY+1,'YYYYMMDD') AND TO_DATE('<END_DATE>'||B.MM_DAY,'YYYYMMDD')
//queryDevMonthPDData.DEVKIND_CODE=D.DEVKIND_CODE=<DEVKIND_CODE>
//queryDevMonthPDData.Debug=Y  
        
  
//更新月结信息------未完成
updateMMStock.Type=TSQL
updateMMStock.SQL=UPDATE DEV_ORG SET DEPT_CODE = <DEPT_CODE>,DEPT_DESC = <DEPT_DESC>,DEPT_DESCRIBE = <DEPT_DESCRIBE>,&
                                        MEDDEV_FLG = <MEDDEV_FLG>,INFDEV_FLG = <INFDEV_FLG>,OTHERDEV_FLG = <OTHERDEV_FLG>,&
                                        OPT_USER = <OPT_USER>,OPT_DATE = <OPT_DATE>,OPT_TERM = <OPT_TERM>,&
                                        PY1 = <PY1>,PY2 = <PY2>,SEQ = <SEQ>,REGION_CODE = <REGION_CODE> &
                     WHERE  DEPT_CODE = <DEPT_CODE>
updateMMStock.Debug=N


  //REGION_CODE,YYYYMM,DEPT_CODE,DEV_CODE,BATCH_SEQ,MM_IN_QTY                 
  //,MM_IN_SCRAPAMT,MM_OUT_QTY,MM_OUT_SCRAPAMT,MM_CHECKMODI_QTY         
  //,MM_CHECKMODI_SCRAPAMT,MM_STOCK_QTY,MM_STOCK_SCRAPAMT,LAST_MM_STOCK_QTY,LAST_MM_STOCK_SCRAPAMT    
  //,MM_INWAREHOUSE_QTY,MM_INWAREHOUSE_SCRAPAMT,MM_REGRESSGOODS_QTY,MM_REGRESSGOODS_SCRAPAMT,MM_GIFTIN_QTY             
  //,MM_GIFTIN_SCRAPAMT,MM_GIFTOUT_QTY,MM_GIFTOUT_SCRAPAMT,MM_WASTE_QTY,MM_WASTE_SCRAPAMT       
  //,OPT_USER,OPT_DATE,OPT_TERM,MM_SCRAP_VALUE  
//写入月结信息
insertMMStock.Type=TSQL
insertMMStock.SQL=INSERT INTO DEV_MMSTOCK (REGION_CODE,YYYYMM,DEPT_CODE,DEV_CODE,BATCH_SEQ, &
                                           MM_IN_QTY,MM_IN_SCRAPAMT,MM_OUT_QTY,MM_OUT_SCRAPAMT, &
                                           MM_CHECKMODI_QTY,MM_CHECKMODI_SCRAPAMT,MM_STOCK_QTY,MM_STOCK_SCRAPAMT,LAST_MM_STOCK_QTY, &
                                           LAST_MM_STOCK_SCRAPAMT,MM_INWAREHOUSE_QTY,MM_INWAREHOUSE_SCRAPAMT,MM_REGRESSGOODS_QTY,MM_REGRESSGOODS_SCRAPAMT, &
                                           MM_GIFTIN_QTY,MM_GIFTIN_SCRAPAMT,MM_GIFTOUT_QTY,MM_GIFTOUT_SCRAPAMT,MM_WASTE_QTY, &
                                           MM_WASTE_SCRAPAMT,OPT_USER,OPT_DATE,OPT_TERM,MM_SCRAP_VALUE) &
                                  VALUES (<REGION_CODE>,<YYYYMM>,<DEPT_CODE>,<DEV_CODE>,<BATCH_SEQ>, &
                                          <MM_IN_QTY>,<MM_IN_SCRAPAMT>,<MM_OUT_QTY>,<MM_OUT_SCRAPAMT>, &
                                          <MM_CHECKMODI_QTY>,<MM_CHECKMODI_SCRAPAMT>,<MM_STOCK_QTY>,<MM_STOCK_SCRAPAMT>,<LAST_MM_STOCK_QTY>, &
                                          <LAST_MM_STOCK_SCRAPAMT>,<MM_INWAREHOUSE_QTY>,<MM_INWAREHOUSE_SCRAPAMT>,<MM_REGRESSGOODS_QTY>,<MM_REGRESSGOODS_SCRAPAMT>, &
                                          <MM_GIFTIN_QTY>,<MM_GIFTIN_SCRAPAMT>,<MM_GIFTOUT_QTY>,<MM_GIFTOUT_SCRAPAMT>,<MM_WASTE_QTY>, &
                                          <MM_WASTE_SCRAPAMT>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<MM_SCRAP_VALUE>)  
insertMMStock.Debug=Y

   

//删除月结信息
deleteMMStock.Type=TSQL
deleteMMStock.SQL=DELETE * FROM DEV_MMSTOCK &
                     WHERE  DEPT_CODE = <DEPT_CODE>
deleteMMStock.Debug=N



//检索设备主库最大顺序号
getDevDeptMaxSeq.Type=TSQL
getDevDeptMaxSeq.SQL=SELECT MAX(SEQ) SEQ &
                     FROM   DEV_ORG 
getDevDeptMaxSeq.Debug=N  