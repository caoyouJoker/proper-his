   #
   # Title:手术包打包
   #
   # Description:手术包打包
   #  
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/08/02

Module.item=queryBackDisinfection;queryDisinfectionByRecycleNo;queryPackageDetailInfo;updatePackageStatus;queryRepackByRepackNo;queryBarcodeInfo;updateDisFinishFlg;queryPackDByPackCode;delPackageDInfo;insertPackageDInfo;queryPackageStatus;updatePackageDisStatus;updatePackageSterStatus;queryHighOnce;updateStockDDWastFlg;updateRepackStatus;queryHOMaterial;queryMaterialByRFID;insertRepack;updatePackstockMBarcode;updatePackstockDBarcode;queryRepack;queryPackList;queryPackageInfo;queryPackageInfoByBarcode;queryPackageDInfoByBarcode

//查询回收单信息   已用勿删
queryBackDisinfection.Type=TSQL
queryBackDisinfection.SQL=SELECT D.RECYCLE_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC & 
				FROM INV_DISINFECTION D LEFT JOIN INV_PACKSTOCKM P ON D.PACK_CODE = P.PACK_CODE AND D.PACK_SEQ_NO = P.PACK_SEQ_NO &
				     LEFT JOIN INV_ORG O ON D.ORG_CODE = O.ORG_CODE & 
				WHERE D.FINISH_FLG = 'N' AND D.OPT_DATE BETWEEN TO_DATE(<START_RECYCLE_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_RECYCLE_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY D.RECYCLE_NO,D.OPT_DATE,D.OPT_USER,D.OPT_TERM,O.ORG_DESC ORDER BY D.RECYCLE_NO DESC
queryBackDisinfection.ITEM=RECYCLE_NO
queryBackDisinfection.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryBackDisinfection.Debug=N


//根据回收单号查询回收单明细   已用勿删
queryDisinfectionByRecycleNo.Type=TSQL
queryDisinfectionByRecycleNo.SQL=SELECT P.PACK_DESC,D.PACK_CODE,D.PACK_SEQ_NO,D.QTY,D.BARCODE  & 
					FROM INV_DISINFECTION D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE & 
					WHERE D.FINISH_FLG = 'N' 
queryDisinfectionByRecycleNo.ITEM=RECYCLE_NO
queryDisinfectionByRecycleNo.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryDisinfectionByRecycleNo.Debug=N


//查询手术包明细
queryPackageDetailInfo.Type=TSQL
queryPackageDetailInfo.SQL=SELECT PD.ORG_CODE, PD.PACK_CODE, PD.PACK_SEQ_NO, PD.INV_CODE, PD.INVSEQ_NO, & 
			PD.COST_PRICE, PD.RECOUNT_TIME, PD.STOCK_UNIT, PD.ONCE_USE_FLG, PD.QTY, IB.INV_CHN_DESC & 
			FROM INV_PACKSTOCKD PD LEFT JOIN INV_BASE IB ON PD.INV_CODE = IB.INV_CODE WHERE PD.ONCE_USE_FLG = 'N'
queryPackageDetailInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageDetailInfo.PACK_CODE=PD.PACK_CODE=<PACK_CODE>
queryPackageDetailInfo.PACK_SEQ_NO=PD.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageDetailInfo.Debug=N




 //更新手术包状态  已用勿删
updatePackageStatus.Type=TSQL
updatePackageStatus.SQL=UPDATE INV_PACKSTOCKM SET STATUS = <STATUS>, OPT_DATE = TO_DATE(<OPT_DATE>,'yyyy/mm/dd hh24:mi:ss'), OPT_USER = <OPT_USER>, OPT_TERM = <OPT_TERM> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO>
updatePackageStatus.Debug=N


//查询手术包条码打印所需信息
queryBarcodeInfo.Type=TSQL
queryBarcodeInfo.SQL=SELECT P.PACK_CODE, P.PACK_SEQ_NO, S.USER_NAME, O.ORG_DESC, D.REPACK_DATE, D.VALID_DATE & 
			FROM INV_PACKSTOCKM P LEFT JOIN INV_REPACK D ON P.PACK_CODE = D.PACK_CODE AND P.PACK_SEQ_NO = D.PACK_SEQ_NO & 
			LEFT JOIN INV_ORG O ON P.ORG_CODE = O.ORG_CODE LEFT JOIN SYS_OPERATOR S ON D.REPACK_USER = S.USER_ID & 
			WHERE P.PACK_CODE = <PACK_CODE> AND P.PACK_SEQ_NO = <PACK_SEQ_NO> AND D.REPACK_NO = <REPACK_NO> 
queryBarcodeInfo.Debug=N


//更改回收单中手术包完成状态 已用勿删 
updateDisFinishFlg.Type=TSQL
updateDisFinishFlg.SQL=UPDATE INV_DISINFECTION SET FINISH_FLG = <FINISH_FLG> WHERE RECYCLE_NO = <RECYCLE_NO> AND PACK_CODE=<PACK_CODE> AND PACK_SEQ_NO=<PACK_SEQ_NO>
updateDisFinishFlg.Debug=N



//查询手术包的物资构成   已用勿删  
queryPackDByPackCode.Type=TSQL
queryPackDByPackCode.SQL=SELECT INV_CODE, QTY, PACK_CODE ,DESCRIPTION FROM INV_PACKD WHERE SEQMAN_FLG = 'N' AND PACK_TYPE = '1' AND PACK_CODE = <PACK_CODE> 
queryPackDByPackCode.Debug=N


//删除手术包明细细项    已用勿删
delPackageDInfo.Type=TSQL 
delPackageDInfo.SQL=DELETE INV_PACKSTOCKD  
delPackageDInfo.ITEM=BARCODE;ONCE_USE_FLG
delPackageDInfo.BARCODE=BARCODE=<BARCODE>
delPackageDInfo.ONCE_USE_FLG=ONCE_USE_FLG=<ONCE_USE_FLG>
delPackageDInfo.Debug=N


//插入手术包明细项   已用勿删  ---折损次数写死为0？
insertPackageDInfo.Type=TSQL     
insertPackageDInfo.SQL=INSERT INTO INV_PACKSTOCKD (ORG_CODE, PACK_CODE, PACK_SEQ_NO, INV_CODE, INVSEQ_NO, & 
						   BATCH_SEQ, DESCRIPTION, RECOUNT_TIME, COST_PRICE, QTY, STOCK_UNIT, & 
						   ONCE_USE_FLG, OPT_USER, OPT_DATE, OPT_TERM, BARCODE, PACK_BATCH_NO) VALUES & 
						   (<ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <INV_CODE>, <INVSEQ_NO>, & 
						   <BATCH_SEQ>, <DESCRIPTION> , 0 , <COST_PRICE>, <STOCK_QTY>, <STOCK_UNIT>, & 
						    <ONCE_USE_FLG>, <OPT_USER>, TO_DATE(<OPT_DATE>,'yyyy/mm/dd HH24:MI:SS'), <OPT_TERM>, <BARCODE>, <PACK_BATCH_NO>)
insertPackageDInfo.Debug=N



//查询手术包状态   已用勿删
queryPackageStatus.Type=TSQL
queryPackageStatus.SQL=SELECT STATUS FROM INV_PACKSTOCKM 
queryPackageStatus.ITEM=BARCODE
queryPackageStatus.BARCODE=BARCODE=<BARCODE>
queryPackageStatus.Debug=N



//将该手术包在回收单中的先前记录置为完成  已用勿删
updatePackageDisStatus.Type=TSQL
updatePackageDisStatus.SQL=UPDATE INV_DISINFECTION SET FINISH_FLG = 'Y'
updatePackageDisStatus.ITEM=BARCODE
updatePackageDisStatus.BARCODE=BARCODE=<BARCODE>
updatePackageDisStatus.Debug=N


//将该手术包在打包单中的先前记录置为完成   已用勿删
updateRepackStatus.Type=TSQL
updateRepackStatus.SQL=UPDATE INV_REPACK SET FINISH_FLG = 'Y' WHERE BARCODE=<BARCODE>
updateRepackStatus.Debug=N


//将该手术包在灭菌单中的先前记录置为完成  已用勿删
updatePackageSterStatus.Type=TSQL
updatePackageSterStatus.SQL=UPDATE INV_STERILIZATION SET FINISH_FLG = 'Y'
updatePackageSterStatus.ITEM=BARCODE
updatePackageSterStatus.BARCODE=BARCODE=<BARCODE>
updatePackageSterStatus.Debug=N



//查询手术包中物资属性为 “一次性且高值” 的    已用勿删
queryHighOnce.Type=TSQL
queryHighOnce.SQL=SELECT INV_CODE, INVSEQ_NO FROM INV_PACKSTOCKD WHERE INVSEQ_NO != 0 AND ONCE_USE_FLG = 'Y'
queryHighOnce.ITEM=BARCODE
queryHighOnce.BARCODE=BARCODE=<BARCODE>
queryHighOnce.Debug=N


//更新stockdd表中高值耗材的消耗属性    已用勿删
updateStockDDWastFlg.Type=TSQL
updateStockDDWastFlg.SQL=UPDATE INV_STOCKDD SET WAST_FLG = 'Y' WHERE INV_CODE = <INV_CODE> AND INVSEQ_NO = <INVSEQ_NO>
updateStockDDWastFlg.Debug=N



//查询基础手术包信息中物资属性为 “一次性且高值” 的（选择物资弹出窗口使用）    已用勿删
queryHOMaterial.Type=TSQL
queryHOMaterial.SQL=SELECT M.BARCODE,M.PACK_CODE,M.PACK_SEQ_NO,D.INV_CODE,D.QTY,B.INV_CHN_DESC &  
			FROM INV_PACKD D LEFT JOIN INV_PACKSTOCKM M ON D.PACK_CODE = M.PACK_CODE LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE & 
			WHERE M.BARCODE IN (<BARCODE>) AND D.PACK_TYPE = 1 AND D.SEQMAN_FLG = 'Y' 
queryHOMaterial.Debug=N



//插入打包表数据  已用勿删  
insertRepack.Type=TSQL
insertRepack.SQL=INSERT INTO INV_REPACK ( REPACK_NO, PACK_CODE, PACK_SEQ_NO, QTY, REPACK_DATE, & 
					  REPACK_USER, OPT_USER, OPT_DATE, OPT_TERM, AUDIT_DATE, & 
					  AUDIT_USER, VALID_DATE, ORG_CODE, FINISH_FLG, BARCODE, OLDBARCODE ) & 
					  VALUES & 
					( <REPACK_NO>, <PACK_CODE>, <PACK_SEQ_NO>, <QTY>, TO_DATE (<REPACK_DATE>, 'YYYY-MM-DD HH24:MI:SS'), & 
					  <REPACK_USER>, <OPT_USER>, TO_DATE (<OPT_DATE>, 'YYYY-MM-DD HH24:MI:SS'), <OPT_TERM>, TO_DATE (<AUDIT_DATE>, 'YYYY-MM-DD HH24:MI:SS'), & 
					  <AUDIT_USER>, TO_DATE (<VALID_DATE>, 'YYYY-MM-DD HH24:MI:SS'), <ORG_CODE>, <FINISH_FLG>, <BARCODE>, <OLDBARCODE> )
insertRepack.Debug=N



//根据RFID查询物资信息  已用勿删
queryMaterialByRFID.Type=TSQL
queryMaterialByRFID.SQL=SELECT D.INV_CODE, D.INVSEQ_NO, D.BATCH_SEQ, D.ORG_CODE, D.STOCK_QTY, D.UNIT_PRICE, D.STOCK_UNIT FROM INV_STOCKDD D WHERE RFID=<RFID>
queryMaterialByRFID.Debug=N



//更新packstockm表条形码
updatePackstockMBarcode.Type=TSQL
updatePackstockMBarcode.SQL=UPDATE INV_PACKSTOCKM SET BARCODE=<BARCODE> WHERE BARCODE=<OLDBARCODE>
updatePackstockMBarcode.Debug=N   


//更新packstockd表条形码
updatePackstockDBarcode.Type=TSQL
updatePackstockDBarcode.SQL=UPDATE INV_PACKSTOCKD SET BARCODE=<BARCODE> WHERE BARCODE=<OLDBARCODE>
updatePackstockDBarcode.Debug=N  



//根据条件查询打包单信息
queryRepack.Type=TSQL
queryRepack.SQL=SELECT REPACK_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE & 
				FROM INV_REPACK & 
				WHERE OPT_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY REPACK_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE ORDER BY REPACK_NO DESC
queryRepack.ITEM=REPACK_NO;FINISH_FLG
queryRepack.REPACK_NO=REPACK_NO=<REPACK_NO>
queryRepack.FINISH_FLG=FINISH_FLG=<FINISH_FLG>
queryRepack.Debug=N




//根据打包单号查询打包单明细 
queryRepackByRepackNo.Type=TSQL
queryRepackByRepackNo.SQL=SELECT D.BARCODE,P.PACK_DESC,D.PACK_CODE,D.PACK_SEQ_NO, & 
					D.AUDIT_DATE,D.AUDIT_USER,D.REPACK_DATE,D.REPACK_USER, & 
					D.QTY  & 
					FROM INV_REPACK D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE 
queryRepackByRepackNo.ITEM=REPACK_NO;FINISH_FLG
queryRepackByRepackNo.REPACK_NO=REPACK_NO=<REPACK_NO>
queryRepackByRepackNo.FINISH_FLG=D.FINISH_FLG=<FINISH_FLG>
queryRepackByRepackNo.Debug=N

  
//查询手术包物资构成
queryPackList.Type=TSQL  
queryPackList.SQL=SELECT D.QTY,B.INV_CHN_DESC ||'  '|| B.DESCRIPTION AS INV_CHN_DESC FROM INV_PACKD D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE WHERE D.PACK_CODE = <PACK_CODE>
queryPackList.Debug=N
    

//查询手术包条码及相关信息，用于打包界面的手术包清单打印
queryPackageInfo.Type=TSQL
queryPackageInfo.SQL=SELECT P.BARCODE,S.USER_NAME,P.OPT_DATE,M.PACK_DESC,M.SEQ_FLG & 
			FROM INV_PACKSTOCKM P LEFT JOIN INV_PACKM M ON P.PACK_CODE = M.PACK_CODE LEFT JOIN SYS_OPERATOR S ON P.OPT_USER = S.USER_ID & 
			WHERE P.PACK_CODE=<PACK_CODE> AND P.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageInfo.Debug=N

//根据条码查询手术包信息
queryPackageInfoByBarcode.Type=TSQL
queryPackageInfoByBarcode.SQL=SELECT M.ORG_CODE,M.PACK_CODE,M.PACK_SEQ_NO,M.DESCRIPTION,M.QTY, & 
				M.DISINFECTION_DATE,M.VALUE_DATE,M.DISINFECTION_USER,M.USE_COST, &
				M.ONCE_USE_COST,M.STATUS,M.OPT_USER,M.OPT_DATE,M.OPT_TERM, & 
				M.DISINFECTION_VALID_DATE,M.BARCODE & 
				FROM INV_PACKSTOCKM M WHERE M.BARCODE=<BARCODE>
queryPackageInfoByBarcode.Debug=N

  
//根据条码查询手术包明细信息
queryPackageDInfoByBarcode.Type=TSQL
queryPackageDInfoByBarcode.SQL=SELECT D.ORG_CODE, D.PACK_CODE, D.PACK_SEQ_NO, D.INV_CODE, D.INVSEQ_NO, & 
				D.BATCH_SEQ, D.DESCRIPTION, D.RECOUNT_TIME, D.COST_PRICE, & 
				D.QTY, D.STOCK_UNIT, D.ONCE_USE_FLG, D.BARCODE & 
				FROM INV_PACKSTOCKD D WHERE D.BARCODE=<BARCODE> AND D.ONCE_USE_FLG = 'N'
queryPackageDInfoByBarcode.Debug=N
