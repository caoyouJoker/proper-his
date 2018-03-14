# 
#  Title:盒装发药机
# 
#  Description:盒装发药机
# 
#  Copyright: Copyright (c) Javahis 2013
# 
#  author fuwj 2013.09.11
#  version 1.0
#
Module.item=query;insertData;deleteData;insertOdiDspnm;selectOdiDspnm;deleteOdiDspnm;selectOpdOrderFlg;updateData;deleteSysfee;  &
            deletePhaBase;deletePhaTransUnit;deleteSysDept;insertPhaBase;insertSysfee;insertPhaTransUnit;insertSysDept;deleteRequestD;  &
            deleteRequestM;insertRequestM;insertRequestD;getDispenseM;getDispenseD;updateDispense

query.Type=TSQL
query.SQL=SELECT A.SEQ_NO,A.BILL_FLG,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.DOSAGE_QTY, &
			 A.DOSAGE_UNIT,A.FREQ_CODE,A.GIVEBOX_FLG,A.GOODS_DESC,A.MEDI_QTY,A.MEDI_UNIT, &
			 A.ORDER_CAT1_CODE,A.ORDER_CODE,A.ORDER_DESC,A.OWN_AMT,A.OWN_PRICE,A.PHA_TYPE, &
			 A.ROUTE_CODE,A.SPECIFICATION,A.TAKE_DAYS,A.CASE_NO,A.RX_NO,A.ORDER_DATE,A.MR_NO  , &
			 A.ADM_TYPE,A.EXEC_DEPT_CODE,A.PAT_NAME,A.SEX_TYPE,A.BIRTH_DATE,A.DR_CODE,A.DC_DEPT_CODE, &
			 A.COUNTER_NO,A.OPT_USER,A.OPT_TERM,A.OPT_DATE  FROM OPD_ORDER A &
			 WHERE A.CASE_NO = <CASE_NO>
query.ITEM=RX_NO
query.RX_NO=A.RX_NO=<RX_NO>
query.Debug=N

insertData.Type=TSQL
insertData.SQL=INSERT INTO OPD_ORDER &
			 （SEQ_NO,BILL_FLG,DISPENSE_QTY,DISPENSE_UNIT,DOSAGE_QTY, &
			 DOSAGE_UNIT,FREQ_CODE,GIVEBOX_FLG,GOODS_DESC,MEDI_QTY,MEDI_UNIT, &
			 ORDER_CAT1_CODE,ORDER_CODE,ORDER_DESC,OWN_AMT,OWN_PRICE,PHA_TYPE, &
			 ROUTE_CODE,SPECIFICATION,TAKE_DAYS,CASE_NO,RX_NO,ORDER_DATE,MR_NO  , &
			 ADM_TYPE,EXEC_DEPT_CODE,PAT_NAME,SEX_TYPE,BIRTH_DATE,DR_CODE, &
			 COUNTER_NO,OPT_USER,OPT_TERM,OPT_DATE,BOX_TYPE,DC_DEPT_CODE ） &
			 VALUES（  &
			 <SEQ_NO>,<BILL_FLG>,<DISPENSE_QTY>,<DISPENSE_UNIT>,<DOSAGE_QTY>, &
			 <DOSAGE_UNIT>,<FREQ_CODE>,<GIVEBOX_FLG>,<GOODS_DESC>,<MEDI_QTY>,<MEDI_UNIT>, &
			 <ORDER_CAT1_CODE>,<ORDER_CODE>,<ORDER_DESC>,<OWN_AMT>,<OWN_PRICE>,<PHA_TYPE>, &
			 <ROUTE_CODE>,<SPECIFICATION>,<TAKE_DAYS>,<CASE_NO>,<RX_NO>,TO_DATE(<ORDER_DATE>, 'YYYYMMDDHH24MISS'),<MR_NO>, &
			 <ADM_TYPE>,<EXEC_DEPT_CODE>,<PAT_NAME>,<SEX_TYPE>,TO_DATE(<BIRTH_DATE>, 'YYYYMMDD'),<DR_CODE>, &
			 <COUNTER_NO>,<OPT_USER>,<OPT_TERM>,SYSDATE ,<BOX_TYPE>,<DC_DEPT_CODE>）
insertData.Debug=N
  
deleteData.Type=TSQL
deleteData.SQL=DELETE FROM OPD_ORDER WHERE CASE_NO=<CASE_NO>	
deleteData.Debug=N
//同步住院处方信息
insertOdiDspnm.Type=TSQL
insertOdiDspnm.SQL=INSERT INTO ODI_DSPNM &
			 （CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,END_DTTM, &
			  STATION_CODE,STATION_DESC,ORDER_DR_CODE,ORDER_DR_DESC,BED_NO, &
			  PAT_NAME,SEX_TYPE,BIRTH_DATE,  &
			  MR_NO,DSPN_KIND,ORDER_CAT1_CODE,EXEC_DEPT_CODE,LINKMAIN_FLG, &
			  LINK_NO,BAR_CODE,ORDER_CODE,ORDER_DESC,SPECIFICATION, &
			  MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,DOSAGE_QTY, &
			  DISPENSE_UNIT,ORDER_DATE,ATC_FLG,OPT_USER,OPT_DATE,OPT_TERM） &
			 VALUES（  &
			 <CASE_NO>,<ORDER_NO>,<ORDER_SEQ>,<START_DTTM>,<END_DTTM>, &
			 <STATION_CODE>,<STATION_DESC>,<ORDER_DR_CODE>,<ORDER_DR_DESC>,<BED_NO>, &
			 <PAT_NAME>,<SEX_TYPE>,TO_DATE(<BIRTH_DATE>, 'YYYYMMDD'),  &
			 <MR_NO>,<DSPN_KIND>,<ORDER_CAT1_CODE>,<EXEC_DEPT_CODE>,<LINKMAIN_FLG>, &
			 <LINK_NO>,<BAR_CODE>,<ORDER_CODE>,<ORDER_DESC>,<SPECIFICATION>, &
			 <MEDI_QTY>,<MEDI_UNIT>,<FREQ_CODE>,<ROUTE_CODE>,<DOSAGE_QTY>, &
			  <DISPENSE_UNIT>,TO_DATE(<ORDER_DATE>, 'YYYYMMDDHH24MISS'),<ATC_FLG>,<OPT_USER> ,SYSDATE,<OPT_TERM>） 
insertOdiDspnm.Debug=N
//查询住院处方信息发送给住院包药机
selectOdiDspnm.Type=TSQL
selectOdiDspnm.SQL=SELECT  PAT_NAME,MR_NO,STATION_DESC,ORDER_DR_DESC,DOSAGE_QTY,ORDER_CODE,FREQ_CODE, &
                           START_DTTM,END_DTTM,DR_NOTE,IPD_NO,BIRTH_DATE,SEX_TYPE,BED_NO,ORDER_SEQ,ORDER_NO  &
                           FROM ODI_DSPNM   &
                           WHERE    ORDER_NO = <ORDER_NO>   &
                           AND    ATC_FLG = 'Y'
selectOdiDspnm.Debug=N
//=====住院处方先删后插入
deleteOdiDspnm.Type=TSQL
deleteOdiDspnm.SQL=DELETE FROM ODI_DSPNM WHERE    ORDER_NO = <ORDER_NO>
deleteOdiDspnm.Debug=N
//=====查询处方状态数据
selectOpdOrderFlg.Type=TSQL
selectOpdOrderFlg.SQL=SELECT DISTINCT RX_NO FROM OPD_ORDER   &
                      WHERE CASE_NO=<CASE_NO> AND  BOX_TYPE = <BOX_TYPE>
selectOpdOrderFlg.Debug=N
//=====更新处方状态
updateData.Type=TSQL
updateData.SQL=UPDATE OPD_ORDER SET BOX_TYPE =<BOXUPDATE_TYPE>  &
                      WHERE CASE_NO=<CASE_NO> AND  BOX_TYPE = <BOX_TYPE>
updateData.Debug=N

//====删除sysfee字典表
deleteSysfee.Type=TSQL
deleteSysfee.SQL=DELETE FROM SYS_FEE 
deleteSysfee.Debug=N
//====删除phabase字典表
deletePhaBase.Type=TSQL
deletePhaBase.SQL=DELETE FROM PHA_BASE
deletePhaBase.Debug=N
//===删除PHA_TRANSUNIT字典表
deletePhaTransUnit.Type=TSQL
deletePhaTransUnit.SQL=DELETE FROM PHA_TRANSUNIT
deletePhaTransUnit.Debug=N
//====删除sysdept字典表
deleteSysDept.Type=TSQL
deleteSysDept.SQL=DELETE FROM SYS_DEPT
deleteSysDept.Debug=N
//====新增sysfee字典表
insertSysfee.Type=TSQL
insertSysfee.SQL=INSERT INTO  SYS_FEE  &
                  (ORDER_CODE,ORDER_DESC,PY1,SPECIFICATION,MAN_CODE,  &
                   OPT_USER,OPT_DATE,OPT_TERM,ACTIVE_FLG,OWN_PRICE,CAT1_TYPE,ORDER_CAT1_CODE,CTRL_FLG)  &
                    VALUES（  &
                    <ORDER_CODE>,<ORDER_DESC>,<PY1>,<SPECIFICATION>,<MAN_CODE>,  &
                   'SPCUSER',SYSDATE,'SPCTERM',<ACTIVE_FLG>,<STOCK_PRICE>,<CAT1_TYPE>,<ORDER_CAT1_CODE>,<CTRL_FLG>)                     
insertSysfee.Debug=N
//====新增phabase字典表
insertPhaBase.Type=TSQL
insertPhaBase.SQL=INSERT INTO PHA_BASE  &
                  (ORDER_CODE,ORDER_DESC,ALIAS_DESC,SPECIFICATION,MAN_CHN_DESC,PHA_TYPE,  &
                   OPT_USER,OPT_DATE,OPT_TERM,STOCK_PRICE,RETAIL_PRICE,CTRLDRUGCLASS_CODE,PURCH_UNIT)  &
                   VALUES（  &
		   <ORDER_CODE>,<ORDER_DESC>,<ALIAS_DESC>,<SPECIFICATION>,<MAN_CODE>,<PHA_TYPE>,  &
                   'SPCUSER',SYSDATE,'SPCTERM',<STOCK_PRICE>,<RETAIL_PRICE>,<CTRLDRUGCLASS_CODE>,<PURCH_UNIT>)   
insertPhaBase.Debug=N
//===新增PHA_TRANSUNIT字典表
insertPhaTransUnit.Type=TSQL
insertPhaTransUnit.SQL=INSERT INTO PHA_TRANSUNIT  &
                       (ORDER_CODE,PURCH_UNIT,PURCH_QTY,STOCK_UNIT,STOCK_QTY, &
                        DOSAGE_UNIT,DOSAGE_QTY,OPT_USER,OPT_DATE,OPT_TERM) &
                        VALUES（  &
                        <ORDER_CODE>,<PURCH_UNIT>,<PURCH_QTY>,<STOCK_UNIT>,<STOCK_QTY>, &
                        <DOSAGE_UNIT>,<DOSAGE_QTY>,'SPCUSER',SYSDATE,'SPCTERM') 
insertPhaTransUnit.Debug=N
//====新增sysdept字典表
insertSysDept.Type=TSQL
insertSysDept.SQL=INSERT INTO SYS_DEPT   &
                  (DEPT_CODE,DEPT_CHN_DESC,COST_CENTER_CODE,ACTIVE_FLG,  &
                  OPT_USER,OPT_DATE,OPT_TERM,DEPT_ABS_DESC,REGION_CODE)   &
                  VALUES（  &
                  <DEPT_CODE>,<DEPT_CHN_DESC>,<COST_CENTER_CODE>,<ACTIVE_FLG>, &
                  'SPCUSER',SYSDATE,'SPCTERM',<DEPT_CHN_DESC>,'40127343')
insertSysDept.Debug=N

//====删除request主表
deleteRequestM.Type=TSQL
deleteRequestM.SQL=DELETE FROM IND_REQUESTM WHERE REQUEST_NO = <REQUEST_NO>
deleteRequestM.Debug=N
//====删除request细表
deleteRequestD.Type=TSQL
deleteRequestD.SQL=DELETE FROM IND_REQUESTD WHERE REQUEST_NO = <REQUEST_NO>
deleteRequestD.Debug=N
//====新增request细表
insertRequestD.Type=TSQL
insertRequestD.SQL=INSERT INTO IND_REQUESTD &
                   (REQUEST_NO,SEQ_NO,ORDER_CODE,QTY,UNIT_CODE,    &
                   RETAIL_PRICE,STOCK_PRICE,ACTUAL_QTY,UPDATE_FLG,OPT_USER,  &
                   OPT_DATE,OPT_TERM,VERIFYIN_PRICE)  &
                     VALUES（  &
                     <REQUEST_NO>,<SEQ_NO>,<ORDER_CODE>,<QTY>,<UNIT_CODE>,    &
		     <RETAIL_PRICE>,<RETAIL_PRICE>,0,'1',<OPT_USER>,  &
                   SYSDATE,<OPT_TERM>,<RETAIL_PRICE>)
insertRequestD.Debug=N
//====新增request主表
insertRequestM.Type=TSQL
insertRequestM.SQL=INSERT INTO IND_REQUESTM  &
                   (REQUEST_NO,REQTYPE_CODE,APP_ORG_CODE,TO_ORG_CODE,REQUEST_DATE,  &
                   REQUEST_USER,REASON_CHN_DESC,OPT_USER,OPT_DATE,OPT_TERM,UNIT_TYPE,URGENT_FLG,REGION_CODE,DRUG_CATEGORY )    &
                   VALUES (  &
                   <REQUEST_NO>,<REQTYPE_CODE>,<APP_ORG_CODE>,<TO_ORG_CODE>,TO_DATE(<REQUEST_DATE>,'YYYYMMDDHH24MISS'), &
                   <REQUEST_USER>,<REASON_CHN_DESC>,<OPT_USER>,SYSDATE,<OPT_TERM> ,<UNIT_TYPE>,'N',<REGION_CODE>,<DRUG_CATEGORY>)
insertRequestM.Debug=N
//=====查询出库主项
getDispenseM.Type=TSQL
getDispenseM.SQL=SELECT * FROM IND_DISPENSEM   &
                      WHERE DISPENSE_NO=<DISPENSE_NO> AND  UPDATE_FLG <> '2'
getDispenseM.Debug=N

//=====查询出库细项
getDispenseD.Type=TSQL
getDispenseD.SQL=SELECT * FROM IND_DISPENSED   &
                      WHERE DISPENSE_NO=<DISPENSE_NO> 
getDispenseD.Debug=N
//====更新出库状态
updateDispense.Type=TSQL
updateDispense.SQL=UPDATE IND_DISPENSEM  SET UPDATE_FLG = '3',OPT_DATE=SYSDATE  WHERE  &
                   DISPENSE_NO = <DISPENSE_NO>  
updateDispense.Debug=N