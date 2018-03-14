Module.item=selectPatCaseNo;selectdataInDate;selectdataOutDate;queryNsCheckFlg;queryPhaBase;updateDspnmBilFlg; &
updateDspndBilFlg;insertEmrFile;updateEmrFile;queryChnSysParm;modifBedNoUD;writeEmrFile;writePDFEmrFile;  &
checkEmrFileExist;updateEmrFileByFile;insertOdiOrder
//查询病患就诊序号
selectPatCaseNo.Type=TSQL
//modify by wanglong 20120814 select增加查询科室信息
selectPatCaseNo.SQL=SELECT A.DEPT_CODE,A.STATION_CODE,A.CASE_NO,A.MR_NO,A.IPD_NO,B.PAT_NAME,TO_CHAR(A.IN_DATE,'YYYY/MM/DD') AS IN_DATE &
		      FROM ADM_INP A,SYS_PATINFO B &
		     WHERE A.MR_NO=B.MR_NO &
		     ORDER BY A.CASE_NO DESC
//modify by wanglong 20120814 增加NOT_DS项，来启动一个新的where条件
selectPatCaseNo.Item=MR_NO;IPD_NO;NOT_DS
selectPatCaseNo.MR_NO=A.MR_NO=<MR_NO>
selectPatCaseNo.IPD_NO=A.IPD_NO=<IPD_NO>
//modify by wanglong 20120814 查出院病患时，要增加以下条件
selectPatCaseNo.NOT_DS=(A.DS_DATE IS NOT NULL OR (A.DS_DATE IS NULL AND A.LAST_DS_DATE IS NOT NULL ))
selectPatCaseNo.Debug=N
//查询病患基本信息在院
selectdataInDate.Type=TSQL
selectdataInDate.SQL=SELECT A.BED_NO,A.MR_NO,B.PAT_NAME,B.BIRTH_DATE,A.IN_DATE,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.CASE_NO, &
                     A.CTZ1_CODE,B.SEX_CODE,A.HEIGHT,A.WEIGHT,A.YELLOW_SIGN,A.GREENPATH_VALUE,A.VS_DR_CODE,A.TOTAL_BILPAY,A.CUR_AMT,A.DS_DATE,A.IPD_NO &
                     FROM ADM_INP A,SYS_PATINFO B &
                     WHERE A.MR_NO = B.MR_NO &
                     AND A.DS_DATE IS NULL
selectdataInDate.Item=CASE_NO;DEPT_CODE;STATION_CODE
selectdataInDate.CASE_NO=A.CASE_NO=<CASE_NO>
selectdataInDate.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdataInDate.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectdataInDate.Debug=N
//查询病患基本信息出院
selectdataOutDate.Type=TSQL
selectdataOutDate.SQL=SELECT A.BED_NO,A.MR_NO,B.PAT_NAME,B.BIRTH_DATE,A.IN_DATE,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.CTZ1_CODE, &
                      B.SEX_CODE,A.HEIGHT,A.WEIGHT,A.YELLOW_SIGN,A.GREENPATH_VALUE,A.VS_DR_CODE,A.TOTAL_BILPAY,A.CUR_AMT,A.CASE_NO,A.DS_DATE,A.IPD_NO &
                      FROM ADM_INP A,SYS_PATINFO B &
                      WHERE A.MR_NO = B.MR_NO &
                      AND A.IN_DATE BETWEEN <START_DATE> AND <END_DATE> &
                      AND A.DS_DATE IS NOT NULL
selectdataOutDate.Item=CASE_NO;DEPT_CODE;STATION_CODE
selectdataOutDate.CASE_NO=A.CASE_NO=<CASE_NO>
selectdataOutDate.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdataOutDate.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectdataOutDate.Debug=N

//护士审核注记
queryNsCheckFlg.Type=TSQL
queryNsCheckFlg.SQL=SELECT NS_CHECK_FLG,DSPN_TIME,IVA_EXPANDTIME,ODI_DEFA_FREG,ODI_STAT_CODE,START_TIME,UDD_STAT_CODE FROM ODI_SYSPARM
queryNsCheckFlg.Debug=N

//查询门诊中药参数
queryChnSysParm.Type=TSQL
queryChnSysParm.SQL=SELECT AGE,PAGE_NUM,DCT_TAKE_DAYS,DCT_TAKE_QTY,PREGNANT_WEEKS,SAVERDU_FLG,W_NHICHECK_FLG,W_TYPE_NUM,W_TAKE_DAYS,W_TOT_AMT,G_NHICHECK_FLG,G_TYPE_NUM,G_TAKE_DAYS,G_TOT_AMT,G_DCTAGENT_CODE,G_FREQ_CODE,G_ROUTE_CODE FROM OPD_SYSPARM
queryChnSysParm.Debug=N

//查询PHA_BASE相关数据
queryPhaBase.Type=TSQL
queryPhaBase.SQL=SELECT A.ORDER_CODE, A.ORDER_DESC, A.GOODS_DESC, A.ALIAS_DESC, A.SPECIFICATION, &
                 A.MAN_CHN_DESC, A.PHA_TYPE, A.TYPE_CODE, A.DOSE_CODE, A.FREQ_CODE, A.ROUTE_CODE, &
                 A.TAKE_DAYS, A.MEDI_QTY, A.MEDI_UNIT, A.DSPNSTOTDOSE_FLG, &
                 A.DEFAULT_TOTQTY, A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, &
                 A.RETAIL_PRICE, A.TRADE_PRICE, A.STOCK_PRICE, A.CTRLDRUGCLASS_CODE, & 
                 A.HALF_USE_FLG, A.REUSE_FLG, A.ODD_FLG, A.UDCARRY_FLG, A.REFUND_FLG, &
                 A.ATC_FLG, A.ANTIBIOTIC_CODE, A.GIVEBOX_FLG, A.BID_FLG, A.PRE_FLG, & 
                 A.PIVAS_FLG, A.EXCPHATYPE_CODE, A.SUP_CODE, A.SNDPHARM_FLG, &
                 A.DRUG_NOTES_PATIENT, A.DRUG_NOTES_DR, A.DRUG_NOTE, A.DRUG_FORM,B.DOSE_TYPE & 
                 FROM PHA_BASE A,PHA_DOSE B WHERE A.ORDER_CODE=<ORDER_CODE> AND A.DOSE_CODE = B.DOSE_CODE
//============pangben modify 20110516 start
queryPhaBase.Item=REGION_CODE
queryPhaBase.REGION_CODE=A.REGION_CODE=<REGION_CODE>  
//============pangben modify 20110516 stop
queryPhaBase.Debug=N
//保存EMR病例
insertEmrFile.Type=TSQL
insertEmrFile.SQL=INSERT INTO EMR_FILE_INDEX(CASE_NO,FILE_SEQ,MR_NO,IPD_NO,FILE_PATH,FILE_NAME,DESIGN_NAME, &
                  CLASS_CODE,SUBCLASS_CODE,DISPOSAC_FLG,CREATOR_USER,CREATOR_DATE,OPT_USER,OPT_DATE,OPT_TERM,REPORT_FLG,OPBOOK_SEQ) &
                  VALUES(<CASE_NO>,<FILE_SEQ>,<MR_NO>,<IPD_NO>,<FILE_PATH>,<FILE_NAME>,<DESIGN_NAME>, &
                  <CLASS_CODE>,<SUBCLASS_CODE>,<DISPOSAC_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<REPORT_FLG>,<OPBOOK_SEQ>)
insertEmrFile.Debug=N
//更新状态
updateEmrFile.Type=TSQL
updateEmrFile.SQL=UPDATE EMR_FILE_INDEX SET OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE CASE_NO=<CASE_NO> AND FILE_SEQ=<FILE_SEQ>
updateEmrFile.Debug=N
//修改长期医嘱转床
modifBedNoUD.Type=TSQL
modifBedNoUD.SQL=UPDATE ODI_ORDER SET BED_NO=<BED_NO> WHERE CASE_NO=<CASE_NO> AND RX_KIND='UD' AND DC_DATE IS NULL
modifBedNoUD.Debug=N
//更新状态(病历书写)
writeEmrFile.Type=TSQL
writeEmrFile.SQL=UPDATE EMR_FILE_INDEX SET OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>, &
		CANPRINT_FLG=<CANPRINT_FLG>, &
		MODIFY_FLG=<MODIFY_FLG>, &
		CURRENT_USER=<CURRENT_USER>, &
		CHK_USER1=<CHK_USER1>, &
		CHK_DATE1=<CHK_DATE1>, &
		CHK_USER2=<CHK_USER2>, &
		CHK_DATE2=<CHK_DATE2>, &
		CHK_USER3=<CHK_USER3>, &
		CHK_DATE3=<CHK_DATE3>, &
		COMMIT_USER=<COMMIT_USER>, &
		COMMIT_DATE=<COMMIT_DATE>, &
		IN_EXAMINE_USER=<IN_EXAMINE_USER>, &
		IN_EXAMINE_DATE=<IN_EXAMINE_DATE>, &
		DS_EXAMINE_USER=<DS_EXAMINE_USER>, &
		DS_EXAMINE_DATE=<DS_EXAMINE_DATE>, &
		PDF_CREATOR_USER=<PDF_CREATOR_USER>, &
		PDF_CREATOR_DATE=<PDF_CREATOR_DATE> &
		WHERE CASE_NO=<CASE_NO> AND FILE_SEQ=<FILE_SEQ>
writeEmrFile.Debug=N


//生成PDF
writePDFEmrFile.Type=TSQL
writePDFEmrFile.SQL=UPDATE EMR_FILE_INDEX SET OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>, &
		PDF_CREATOR_USER=<PDF_CREATOR_USER>, &
		PDF_CREATOR_DATE=<PDF_CREATOR_DATE> &
		WHERE CASE_NO=<CASE_NO> AND FILE_SEQ=<FILE_SEQ>
writeEmrFile.Debug=N


//判断病历是否存在
checkEmrFileExist.Type=TSQL
checkEmrFileExist.SQL=SELECT COUNT(*) AS TOTAL FROM EMR_FILE_INDEX WHERE CASE_NO=<CASE_NO> AND FILE_PATH=<FILE_PATH> AND FILE_NAME=<FILE_NAME>
checkEmrFileExist.Debug=N

//通过文件名更新状态
updateEmrFileByFile.Type=TSQL
updateEmrFileByFile.SQL=UPDATE EMR_FILE_INDEX SET OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
		WHERE CASE_NO=<CASE_NO> AND FILE_PATH=<FILE_PATH> AND FILE_NAME=<FILE_NAME>
updateEmrFileByFile.Debug=N

//保存审批的医嘱到odi_order
insertOdiOrder.Type=TSQL
insertOdiOrder.SQL=INSERT INTO ODI_ORDER(CASE_NO,ORDER_NO,ORDER_SEQ,REGION_CODE,IPD_NO,MR_NO,BED_NO,STATION_CODE,RX_KIND,DEPT_CODE,VS_DR_CODE, &
                   ORDER_CODE,ORDER_DESC,MEDI_QTY,MEDI_UNIT,FREQ_CODE,ROUTE_CODE,ORDER_DEPT_CODE,INDV_FLG,HIDE_FLG,ORDER_CAT1_CODE,CAT1_TYPE,  &
                    URGENT_FLG,DISPENSE_FLG,DR_NOTE,EXEC_DEPT_CODE,EFF_DATE,DC_DATE,NS_NOTE,ANTIBIOTIC_WAY,INSPAY_TYPE,DOSAGE_QTY,  &
		   DOSAGE_UNIT,ORDER_DR_CODE,ORDER_DATE,NS_CHECK_CODE,NS_CHECK_DATE,DC_DR_CODE,DC_RSN_CODE,DC_NS_CHECK_CODE,DC_NS_CHECK_DATE,  &
			   ORDER_STATE,ACUMMEDI_QTY,ACUMDSPN_QTY,OPT_USER,OPT_DATE,OPT_TERM,START_DTTM,ANTIBIOTIC_CODE,TAKE_DAYS,LINKMAIN_FLG,LINK_NO) &
                  VALUES(<CASE_NO>,<ORDER_NO>,<ORDER_SEQ>,<REGION_CODE>,<IPD_NO>,<MR_NO>,<BED_NO>,<STATION_CODE>,<RX_KIND>,<DEPT_CODE>,<VS_DR_CODE>, &
		  <ORDER_CODE>,<ORDER_DESC>,<MEDI_QTY>,<MEDI_UNIT>,<FREQ_CODE>,<ROUTE_CODE>,<ORDER_DEPT_CODE>,<INDV_FLG>,<HIDE_FLG>,<ORDER_CAT1_CODE>,<CAT1_TYPE>, &
                  <URGENT_FLG>,<DISPENSE_FLG>,<DR_NOTE>,<EXEC_DEPT_CODE>,<EFF_DATEDAY>,<DC_DATE>,<NS_NOTE>,<ANTIBIOTIC_WAY>,<INSPAY_TYPE>,<DOSAGE_QTY>, &
		  <DOSAGE_UNIT>,<ORDER_DR_CODE>,<ORDER_DATE>,<NS_CHECK_CODE>,<NS_CHECK_DATE>,<DC_DR_CODE>,<DC_RSN_CODE>,<DC_NS_CHECK_CODE>,<DC_NS_CHECK_DATE>, &
		  <ORDER_STATE>,<ACUMMEDI_QTY>,<ACUMDSPN_QTY>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<START_DTTM>,<ANTIBIOTIC_CODE>,<TAKE_DAYS>,<LINKMAIN_FLG>,<LINK_NO>)
insertOdiOrder.Debug=N
