Module.item=insertdata;selectdata;updatedata;deletedata;existsOrder;insertOpdOrderHistory

// 查询全字段
selectdata.Type=TSQL
selectdata.SQL=SELECT CASE_NO,RX_NO,SEQ_NO,DC_ORDER_DATE,OPT_USER,OPT_DATE,OPT_TERM,PRESRT_NO,REGION_CODE,MR_NO,ADM_TYPE,RX_TYPE,RELEASE_FLG,LINKMAIN_FLG,LINK_NO,ORDER_CODE,ORDER_DESC,GOODS_DESC,ORDER_CAT1,TAKE_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,TAKE_DAYS,TOT_QTY,DGT_TOT,DISPENSE_UNIT,OPD_GIVEBOX_FLG,OWN_PRICE,NHI_PRICE,DISCN_RATE,OWN_AMT,TOT_AMT,NS_NOTE,DR_CODE,ORDER_DATE,DEPT_CODE,DC_DR_CODE,DC_DEPT_CODE,RBORDER_DEPT_CODE,SETMAIN_FLG,ORDSET_GROUP_NO,ORDERSET_CODE,HIDE_FLG,RPTTYPE_CODE,OPTITEM_CODE,DEV_CODE,MR_CODE,FILE_NO,DEGREE_CODE,URGENT_FLG,INSPAY_TYPE,PHA_TYPE,DOSE_TYPE,PRINTTYPEFLG_INFANT,CTRLDRUGCLASS_CODE,DCTAGENT_CODE,DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT FROM OPD_ORDER_HISTORY ORDER BY SEQ_NO 
selectdata.item=CASE_NO;RX_NO;SEQ_NO;DC_ORDER_DATE
selectdata.CASE_NO=CASE_NO=<CASE_NO>
selectdata.RX_NO=RX_NO=<RX_NO>
selectdata.SEQ_NO=SEQ_NO=<SEQ_NO>
selectdata.DC_ORDER_DATE=SEQ_NO=<DC_ORDER_DATE> 
selectdata.Debug=N

//插入数据（测试用）
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO OPD_ORDER_HISTORY VALUES(<CASE_NO>,<RX_NO>,<SEQ_NO>,<DC_ORDER_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<PRESRT_NO>,<REGION_CODE>,<MR_NO>,<ADM_TYPE>,<RX_TYPE>,<RELEASE_FLG>,<LINKMAIN_FLG>,<LINK_NO>,<ORDER_CODE>,<ORDER_DESC>,<GOODS_DESC>,<ORDER_CAT1>,<TAKE_QTY>,<MEDI_UNIT>,<FREQ_CODE>,<ROUTE_CODE>,<TAKE_DAYS>,<TOT_QTY>,<DGT_TOT>,<DISPENSE_UNIT>,<OPD_GIVEBOX_FLG>,<OWN_PRICE>,<NHI_PRICE>,<DISCN_RATE>,<OWN_AMT>,<TOT_AMT>,<NS_NOTE>,<DR_CODE>,<ORDER_DATE>,<DEPT_CODE>,<DC_DR_CODE>,<DC_DEPT_CODE>,<RBORDER_DEPT_CODE>,<SETMAIN_FLG>,<ORDSET_GROUP_NO>,<ORDERSET_CODE>,<HIDE_FLG>,<RPTTYPE_CODE>,<OPTITEM_CODE>,<DEV_CODE>,<MR_CODE>,<FILE_NO>,<DEGREE_CODE>,<URGENT_FLG>,<INSPAY_TYPE>,<PHA_TYPE>,<DOSE_TYPE>,<PRINTTYPEFLG_INFANT>,<CTRLDRUGCLASS_CODE>,<DCTAGENT_CODE>,<DCTEXCEP_CODE>,<DCT_TAKE_QTY>,<PACKAGE_TOT>)
insertdata.Debug=N

//修改数据（测试用）
updatedata.Type=TSQL
updatedata.SQL=UPDATE OPD_ORDER_HISTORY SET OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>,PRESRT_NO=<PRESRT_NO>,REGION_CODE=<REGION_CODE>,MR_NO=<MR_NO>,ADM_TYPE=<ADM_TYPE>,RX_TYPE=<RX_TYPE>,RELEASE_FLG=<RELEASE_FLG>,LINKMAIN_FLG=<LINKMAIN_FLG>,LINK_NO=<LINK_NO>,ORDER_CODE=<ORDER_CODE>,ORDER_DESC=<ORDER_DESC>,GOODS_DESC=<GOODS_DESC>,ORDER_CAT1=<ORDER_CAT1>,TAKE_QTY=<TAKE_QTY>,MEDI_UNIT=<MEDI_UNIT>,FREQ_CODE=<FREQ_CODE>,ROUTE_CODE=<ROUTE_CODE>,TAKE_DAYS=<TAKE_DAYS>,TOT_QTY=<TOT_QTY>,DGT_TOT=<DGT_TOT>,DISPENSE_UNIT=<DISPENSE_UNIT>,OPD_GIVEBOX_FLG=<OPD_GIVEBOX_FLG>,OWN_PRICE=<OWN_PRICE>,NHI_PRICE=<NHI_PRICE>,DISCN_RATE=<DISCN_RATE>,OWN_AMT=<OWN_AMT>,TOT_AMT=<TOT_AMT>,NS_NOTE=<NS_NOTE>,DR_CODE=<DR_CODE>,ORDER_DATE=<ORDER_DATE>,DEPT_CODE=<DEPT_CODE>,DC_DR_CODE=<DC_DR_CODE>,DC_DEPT_CODE=<DC_DEPT_CODE>,RBORDER_DEPT_CODE=<RBORDER_DEPT_CODE>,SETMAIN_FLG=<SETMAIN_FLG>,ORDSET_GROUP_NO=<ORDSET_GROUP_NO>,ORDERSET_CODE=<ORDERSET_CODE>,HIDE_FLG=<HIDE_FLG>,RPTTYPE_CODE=<RPTTYPE_CODE>,OPTITEM_CODE=<OPTITEM_CODE>,DEV_CODE=<DEV_CODE>,MR_CODE=<MR_CODE>,FILE_NO=<FILE_NO>,DEGREE_CODE=<DEGREE_CODE>,URGENT_FLG=<URGENT_FLG>,INSPAY_TYPE=<INSPAY_TYPE>,PHA_TYPE=<PHA_TYPE>,DOSE_TYPE=<DOSE_TYPE>,PRINTTYPEFLG_INFANT=<PRINTTYPEFLG_INFANT>,CTRLDRUGCLASS_CODE=<CTRLDRUGCLASS_CODE>,DCTAGENT_CODE=<DCTAGENT_CODE>,DCTEXCEP_CODE=<DCTEXCEP_CODE>,DCT_TAKE_QTY=<DCT_TAKE_QTY>,PACKAGE_TOT=<PACKAGE_TOT>
updatedata.item=CASE_NO;RX_NO;SEQ_NO;DC_ORDER_DATE
updatedata.CASE_NO=CASE_NO=<CASE_NO>
updatedata.RX_NO=RX_NO=<RX_NO>
updatedata.SEQ_NO=SEQ_NO=<SEQ_NO>
updatedata.DC_ORDER_DATE=DC_ORDER_DATE=<DC_ORDER_DATE>
updatedata.Debug=N

//删除数据（测试用）
deletedata.Type=TSQL
deletedata.SQL=DELETE OPD_ORDER_HISTORY
deletedata.item=CASE_NO;RX_NO;SEQ_NO;deletedata
deletedata.CASE_NO=CASE_NO=<CASE_NO>
deletedata.RX_NO=RX_NO=<RX_NO>
deletedata.SEQ_NO=SEQ_NO=<SEQ_NO>
deletedata.DC_ORDER_DATE=DC_ORDER_DATE=<DC_ORDER_DATE>
deletedata.Debug=N

//判断数据是否存在（测试用）
existsOrder.Type=TSQL
existsOrder.SQL=SELECT COUNT(CASE_NO) AS COUNT FROM OPD_ORDER_HISTORY
existsOrder.item=CASE_NO;RX_NO;SEQ_NO;DC_ORDER_DATE
existsOrder.CASE_NO=CASE_NO=<CASE_NO>
existsOrder.RX_NO=RX_NO=<RX_NO>
existsOrder.SEQ_NO=SEQ_NO=<SEQ_NO>
existsOrder.DC_ORDER_DATE=DC_ORDER_DATE=<DC_ORDER_DATE> 
existsOrder.Debug=N

//插入数据

insertOpdOrderHistory.Type=TSQL
insertOpdOrderHistory.SQL=INSERT INTO OPD_ORDER_HISTORY(CASE_NO, RX_NO, SEQ_NO, &
   	       DC_ORDER_DATE, PRESRT_NO, REGION_CODE, &
               MR_NO, ADM_TYPE, RX_TYPE, &
               RELEASE_FLG, LINKMAIN_FLG, LINK_NO, &
               ORDER_CODE, ORDER_DESC, GOODS_DESC, &
               SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, &
 	       MEDI_UNIT, FREQ_CODE, ROUTE_CODE, &
  	       TAKE_DAYS, DOSAGE_QTY, DOSAGE_UNIT, &
  	       DISPENSE_QTY, DISPENSE_UNIT, GIVEBOX_FLG, &
 	       OWN_PRICE, NHI_PRICE, DISCOUNT_RATE, &
 	       OWN_AMT, AR_AMT, DR_NOTE, &
	       NS_NOTE, DR_CODE, ORDER_DATE, &
	       DEPT_CODE, DC_DR_CODE, DC_DEPT_CODE, &
	       EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG, &
	       ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG, &
	       RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, &
	       MR_CODE, FILE_NO, DEGREE_CODE, &
	       URGENT_FLG, INSPAY_TYPE, PHA_TYPE,& 
 	       DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT,& 
 	       CTRLDRUGCLASS_CODE, PRESCRIPT_NO, HEXP_CODE, &
 	       CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, &
	       CTZ3_CODE, NS_EXEC_CODE, &
  	       NS_EXEC_DEPT, DCTAGENT_CODE, DCTEXCEP_CODE, &
  	       DCT_TAKE_QTY, PACKAGE_TOT, OPT_USER, &
 	       OPT_DATE, OPT_TERM) &
 	       VALUES(<CASE_NO>, <RX_NO>, <SEQ_NO>, &
   	       TO_CHAR(SYSDATE,'YYYYMMDDHHmmSS'), <PRESRT_NO>, <REGION_CODE>, &
               <MR_NO>, <ADM_TYPE>, <RX_TYPE>, &
               <RELEASE_FLG>, <LINKMAIN_FLG>, <LINK_NO>, &
               <ORDER_CODE>, <ORDER_DESC>, <GOODS_DESC>, &
               <SPECIFICATION>, <ORDER_CAT1_CODE>, <MEDI_QTY>,& 
 	       <MEDI_UNIT>, <FREQ_CODE>, <ROUTE_CODE>, &
  	       <TAKE_DAYS>, <DOSAGE_QTY>, <DOSAGE_UNIT>, &
  	       <DISPENSE_QTY>, <DISPENSE_UNIT>, <GIVEBOX_FLG>, &
 	       <OWN_PRICE>, <NHI_PRICE>, <DISCOUNT_RATE>, &
 	       <OWN_AMT>, <AR_AMT>, <DR_NOTE>, &
	       <NS_NOTE>, <DR_CODE>, <ORDER_DATE>, &
	       <DEPT_CODE>, <DC_DR_CODE>, <DC_DEPT_CODE>, &
	       <EXEC_DEPT_CODE>, <EXEC_DR_CODE>, <SETMAIN_FLG>,& 
	       <ORDERSET_GROUP_NO>, <ORDERSET_CODE>, <HIDE_FLG>,& 
	       <RPTTYPE_CODE>, <OPTITEM_CODE>, <DEV_CODE>, &
	       <MR_CODE>, <FILE_NO>, <DEGREE_CODE>, &
	       <URGENT_FLG>, <INSPAY_TYPE>, <PHA_TYPE>, &
 	       <DOSE_TYPE>, <EXPENSIVE_FLG>, <PRINTTYPEFLG_INFANT>,& 
 	       <CTRLDRUGCLASS_CODE>, <PRESCRIPT_NO>, <HEXP_CODE>, &
 	       <CONTRACT_CODE>, <CTZ1_CODE>, <CTZ2_CODE>, &
	       <CTZ3_CODE>, <NS_EXEC_CODE>, &
  	       <NS_EXEC_DEPT>, <DCTAGENT_CODE>, <DCTEXCEP_CODE>, &
  	       <DCT_TAKE_QTY>, <PACKAGE_TOT>, <OPT_USER>, &
 	       SYSDATE, <OPT_TERM>)
insertOpdOrderHistory.Debug=N