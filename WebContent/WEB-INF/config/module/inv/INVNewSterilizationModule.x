   #
   # Title:手术包灭菌打包
   #
   # Description:手术包灭菌打包
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/07/01

Module.item=queryRepack;queryRepackByRepackNo;queryPackageDetailInfo;insertSterilization;updatePackageStatus;querySterilization;querySterilizationBySterilizationNo;queryBarcodeInfo;updateDisFinishFlg;queryPackDByPackCode;delPackageDInfo;insertPackageDInfo;queryPackageStatus;updatePackageDisStatus;updatePackageSterStatus;queryBackDisinfection

//查询打包单信息  --- 为再打包状态才可灭菌  
queryRepack.Type=TSQL
queryRepack.SQL=SELECT D.REPACK_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC & 
				FROM INV_REPACK D LEFT JOIN INV_PACKSTOCKM P ON D.PACK_CODE = P.PACK_CODE AND D.PACK_SEQ_NO = P.PACK_SEQ_NO &
				     LEFT JOIN INV_ORG O ON D.ORG_CODE = O.ORG_CODE & 
				WHERE D.FINISH_FLG = 'Y'  AND P.STATUS = '3'  AND D.OPT_DATE BETWEEN TO_DATE(<START_REPACK_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_REPACK_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY D.REPACK_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC ORDER BY D.REPACK_NO DESC
queryRepack.ITEM=REPACK_NO  
queryRepack.REPACK_NO=REPACK_NO=<REPACK_NO>  
queryRepack.Debug=N   
  

//根据打包单号查询打包单明细
queryRepackByRepackNo.Type=TSQL
queryRepackByRepackNo.SQL=SELECT P.PACK_DESC,D.PACK_CODE,D.PACK_SEQ_NO,D.QTY,D.BARCODE  & 
					FROM INV_REPACK D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE & 
					WHERE D.FINISH_FLG = 'Y' 
queryRepackByRepackNo.ITEM=REPACK_NO
queryRepackByRepackNo.REPACK_NO=REPACK_NO=<REPACK_NO>
queryRepackByRepackNo.Debug=N


//查询手术包明细
queryPackageDetailInfo.Type=TSQL
queryPackageDetailInfo.SQL=SELECT PD.ORG_CODE, PD.PACK_CODE, PD.PACK_SEQ_NO, PD.INV_CODE, PD.INVSEQ_NO, & 
			PD.COST_PRICE, PD.RECOUNT_TIME, PD.STOCK_UNIT, PD.ONCE_USE_FLG, PD.QTY, IB.INV_ABS_DESC AS INV_CHN_DESC & 
			FROM INV_PACKSTOCKD PD LEFT JOIN INV_BASE IB ON PD.INV_CODE = IB.INV_CODE 
queryPackageDetailInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageDetailInfo.PACK_CODE=PD.PACK_CODE=<PACK_CODE>
queryPackageDetailInfo.PACK_SEQ_NO=PD.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageDetailInfo.Debug=N


//插入灭菌记录
insertSterilization.Type=TSQL  
insertSterilization.SQL=INSERT INTO INV_STERILIZATION (STERILIZATION_NO, SEQ_NO, PACK_CODE, QTY, STERILIZATION_NUM, STERILIZATION_POTSEQ, & 
				STERILLZATION_PROGRAM, STERILLZATION_DATE, STERILLZATION_USER, AUDIT_DATE, AUDIT_USER, & 
				OPT_USER, OPT_DATE, OPT_TERM, ORG_CODE, FINISH_FLG, BARCODE ) VALUES (<STERILIZATION_NO>, <SEQ_NO>, <PACK_CODE>, & 
				<QTY>,<STERILIZATION_NUM>,<STERILIZATION_POTSEQ>, <STERILLZATION_PROGRAM>, TO_DATE(<STERILLZATION_DATE>,'yyyy/mm/dd hh24:mi:ss'), <STERILLZATION_USER>, & 
				TO_DATE(<AUDIT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <AUDIT_USER>, <OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), <OPT_TERM>, <ORG_CODE>, <FINISH_FLG>, <BARCODE> )
insertSterilization.Debug=N    


 //更新手术包状态   
updatePackageStatus.Type=TSQL
updatePackageStatus.SQL=UPDATE INV_PACKSTOCKM SET STATUS = <STATUS> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <SEQ_NO>
updatePackageStatus.Debug=N


//根据条件查询灭菌单信息
querySterilization.Type=TSQL
querySterilization.SQL=SELECT STERILIZATION_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE & 
				FROM INV_STERILIZATION & 
				WHERE OPT_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY STERILIZATION_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE ORDER BY STERILIZATION_NO DESC
querySterilization.ITEM=STERILIZATION_NO;FINISH_FLG
querySterilization.STERILIZATION_NO=STERILIZATION_NO=<STERILIZATION_NO>
querySterilization.FINISH_FLG=FINISH_FLG=<FINISH_FLG>
querySterilization.Debug=N




//根据灭菌单号查询回收单明细
querySterilizationBySterilizationNo.Type=TSQL
querySterilizationBySterilizationNo.SQL=SELECT P.PACK_DESC,D.PACK_CODE,D.SEQ_NO AS PACK_SEQ_NO, & 
					D.STERILLZATION_DATE,D.STERILLZATION_USER,D.AUDIT_DATE,D.AUDIT_USER,D.STERILIZATION_NUM, & 
					D.STERILIZATION_POTSEQ,D.STERILLZATION_PROGRAM,D.QTY,D.BARCODE  & 
					FROM INV_STERILIZATION D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE 
querySterilizationBySterilizationNo.ITEM=STERILIZATION_NO;FINISH_FLG
querySterilizationBySterilizationNo.STERILIZATION_NO=STERILIZATION_NO=<STERILIZATION_NO>
querySterilizationBySterilizationNo.FINISH_FLG=D.FINISH_FLG=<FINISH_FLG>
querySterilizationBySterilizationNo.Debug=N


//查询手术包条码打印所需信息
queryBarcodeInfo.Type=TSQL
queryBarcodeInfo.SQL=SELECT P.PACK_CODE, P.PACK_SEQ_NO, S.USER_NAME, O.ORG_DESC, D.STERILLZATION_DATE & 
			FROM INV_PACKSTOCKM P LEFT JOIN INV_STERILIZATION D ON P.PACK_CODE = D.PACK_CODE AND P.PACK_SEQ_NO = D.SEQ_NO & 
			LEFT JOIN INV_ORG O ON P.ORG_CODE = O.ORG_CODE LEFT JOIN SYS_OPERATOR S ON D.PACK_USER = S.USER_ID & 
			WHERE P.PACK_CODE = <PACK_CODE> AND P.PACK_SEQ_NO = <PACK_SEQ_NO> AND D.STERILIZATION_NO = <STERILIZATION_NO> 
queryBarcodeInfo.Debug=N


//更改回收单中手术包完成状态
updateDisFinishFlg.Type=TSQL
updateDisFinishFlg.SQL=UPDATE INV_DISINFECTION SET FINISH_FLG = <FINISH_FLG> WHERE RECYCLE_NO = <RECYCLE_NO> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateDisFinishFlg.Debug=N



//查询手术包的物资构成
queryPackDByPackCode.Type=TSQL
queryPackDByPackCode.SQL=SELECT INV_CODE, QTY, PACK_CODE FROM INV_PACKD WHERE SEQMAN_FLG = 'N' AND PACK_CODE = <PACK_CODE> 
queryPackDByPackCode.Debug=N


//删除手术包明细细项
delPackageDInfo.Type=TSQL
delPackageDInfo.SQL=DELETE INV_PACKSTOCKD  
delPackageDInfo.ITEM=PACK_CODE;PACK_SEQ_NO;ONCE_USE_FLG;INV_CODE
delPackageDInfo.PACK_CODE=PACK_CODE=<PACK_CODE>
delPackageDInfo.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>         
delPackageDInfo.ONCE_USE_FLG=ONCE_USE_FLG=<ONCE_USE_FLG>
delPackageDInfo.INV_CODE=INV_CODE=<INV_CODE>  
delPackageDInfo.Debug=N


//插入手术包明细项
insertPackageDInfo.Type=TSQL
insertPackageDInfo.SQL=INSERT INTO INV_PACKSTOCKD (ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, INVSEQ_NO, & 
						   BATCH_SEQ, RECOUNT_TIME, COST_PRICE, QTY, STOCK_UNIT, & 
						   ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM) VALUES & 
						   (<ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <INVSEQ_NO>, & 
						   <BATCH_SEQ>, 0, <UNIT_PRICE>, <STOCK_QTY>, <STOCK_UNIT>, & 
						    <ONCE_USE_FLG>, <OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd'), <OPT_TERM>)
insertPackageDInfo.Debug=N



//查询手术包状态
queryPackageStatus.Type=TSQL
queryPackageStatus.SQL=SELECT STATUS FROM INV_PACKSTOCKM 
queryPackageStatus.ITEM=PACK_CODE;PACK_SEQ_NO
queryPackageStatus.PACK_CODE=PACK_CODE=<PACK_CODE>
queryPackageStatus.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageStatus.Debug=N



//将该手术包在回收单中的先前记录置为完成
updatePackageDisStatus.Type=TSQL
updatePackageDisStatus.SQL=UPDATE INV_DISINFECTION SET FINISH_FLG = 'Y'
updatePackageDisStatus.ITEM=PACK_CODE;PACK_SEQ_NO
updatePackageDisStatus.PACK_CODE=PACK_CODE=<PACK_CODE>
updatePackageDisStatus.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
updatePackageDisStatus.Debug=N


//将该手术包在灭菌单中的先前记录置为完成
updatePackageSterStatus.Type=TSQL
updatePackageSterStatus.SQL=UPDATE INV_STERILIZATION SET FINISH_FLG = 'Y'
updatePackageSterStatus.ITEM=PACK_CODE;PACK_SEQ_NO
updatePackageSterStatus.PACK_CODE=PACK_CODE=<PACK_CODE>
updatePackageSterStatus.PACK_SEQ_NO=SEQ_NO=<PACK_SEQ_NO>
updatePackageSterStatus.Debug=N



//查询回收单信息   已用勿删
queryBackDisinfection.Type=TSQL
queryBackDisinfection.SQL=SELECT D.RECYCLE_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC & 
				FROM INV_DISINFECTION D LEFT JOIN INV_PACKSTOCKM P ON D.PACK_CODE = P.PACK_CODE AND D.PACK_SEQ_NO = P.PACK_SEQ_NO &
				     LEFT JOIN INV_ORG O ON D.ORG_CODE = O.ORG_CODE & 
				WHERE D.FINISH_FLG = 'N' AND D.OPT_DATE BETWEEN TO_DATE(<START_RECYCLE_DATE>,'yyyy/mm/dd') AND TO_DATE(<END_RECYCLE_DATE>,'yyyy/mm/dd') & 
				GROUP BY D.RECYCLE_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC ORDER BY D.RECYCLE_NO DESC
queryBackDisinfection.ITEM=RECYCLE_NO  
queryBackDisinfection.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>    
queryBackDisinfection.Debug=N    


















//取消时删除消毒记录GYSUsed
deleteValue.Type=TSQL
deleteValue.SQL=DELETE INV_DISINFECTION
deleteValue.item=PACK_CODE;PACK_SEQ_NO;DISINFECTION_DATE
deleteValue.PACK_CODE=PACK_CODE=<PACK_CODE>
deleteValue.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
deleteValue.DISINFECTION_DATE=DISINFECTION_DATE=<DISINFECTION_DATE>
deleteValue.Debug=Y

       











