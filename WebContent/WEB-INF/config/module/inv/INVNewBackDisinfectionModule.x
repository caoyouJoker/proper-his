   #
   # Title:手术包回收清洗消毒
   #
   # Description:手术包回收清洗消毒
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author wangm 2013/06/26

Module.item=queryPackageInfo;queryPackageDetailInfo;insertDisinfection;updatePackageStatus;updateRecountTime;queryBatchNo;updateStockM;updateStockD;queryBackDisinfection;queryDisinfectionByRecycleNo;queryBarcodeInfo;queryDeptName;queryUserName;queryPackageInfoByBarcode;queryPackageInfoByAllBarcode;queryPMInfoByBarcode


//查询手术包信息
queryPackageInfo.Type=TSQL
queryPackageInfo.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC, P.VALUE_DATE & 
			FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE
queryPackageInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageInfo.PACK_CODE=PM.PACK_CODE=<PACK_CODE>
queryPackageInfo.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageInfo.Debug=N

//查询手术包明细
queryPackageDetailInfo.Type=TSQL
queryPackageDetailInfo.SQL=SELECT PD.ORG_CODE, PD.PACK_CODE, PD.PACK_SEQ_NO, PD.INV_CODE, PD.INVSEQ_NO, & 
			PD.COST_PRICE, PD.RECOUNT_TIME, PD.STOCK_UNIT, PD.ONCE_USE_FLG, PD.QTY, IB.INV_ABS_DESC AS INV_CHN_DESC,IB.INVKIND_CODE & 
			FROM INV_PACKSTOCKD PD LEFT JOIN INV_BASE IB ON PD.INV_CODE = IB.INV_CODE 
queryPackageDetailInfo.item=PACK_CODE;PACK_SEQ_NO
queryPackageDetailInfo.PACK_CODE=PD.PACK_CODE=<PACK_CODE>   
queryPackageDetailInfo.PACK_SEQ_NO=PD.PACK_SEQ_NO=<PACK_SEQ_NO>
queryPackageDetailInfo.Debug=N         


//插入新建回收单记录
insertDisinfection.Type=TSQL  
insertDisinfection.SQL=INSERT INTO INV_DISINFECTION ( &
			RECYCLE_NO, ORG_CODE, PACK_CODE, PACK_SEQ_NO, QTY, RECYCLE_DATE, &
			RECYCLE_USER , WASH_DATE, WASH_USER, DISINFECTION_NUM, DISINFECTION_POTSEQ, DISINFECTION_PROGRAM, &
			DISINFECTION_OPERATIONSTAFF, DISINFECTION_DATE, DISINFECTION_VALID_DATE, DISINFECTION_USER, & 
			OPT_USER, OPT_DATE, OPT_TERM, FINISH_FLG, BARCODE ) &
	    	      VALUES( &    
	    	   	<RECYCLE_NO>, <ORG_CODE>, <PACK_CODE>, <PACK_SEQ_NO>, <QTY>, & 
			TO_DATE(<RECYCLE_DATE>,'yyyy/mm/dd hh24:mi:ss'), <RECYCLE_USER>, TO_DATE(<WASH_DATE>,'yyyy/mm/dd hh24:mi:ss'), <WASH_USER>, <DISINFECTION_NUM>, <DISINFECTION_POTSEQ>, & 
			<DISINFECTION_PROGRAM>, <DISINFECTION_OPERATIONSTAFF>, TO_DATE(<DISINFECTION_DATE>,'yyyy/mm/dd hh24:mi:ss'), TO_DATE(<DISINFECTION_VALID_DATE>,'yyyy/mm/dd hh24:mi:ss'), <DISINFECTION_USER>, & 
			<OPT_USER>, SYSDATE, <OPT_TERM>, <FINISH_FLG>, <BARCODE> & 
			)
insertDisinfection.Debug=N


//更新手术包状态 
updatePackageStatus.Type=TSQL
updatePackageStatus.SQL=UPDATE INV_PACKSTOCKM SET STATUS = <STATUS> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO>
updatePackageStatus.Debug=N



//更新器械折损次数
updateRecountTime.Type=TSQL
updateRecountTime.SQL=UPDATE INV_PACKSTOCKD SET RECOUNT_TIME = <RECOUNT_TIME> & 
			WHERE ORG_CODE = <ORG_CODE> AND PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO> AND &
			      INV_CODE = <INV_CODE> AND INVSEQ_NO = <INVSEQ_NO> 
updateRecountTime.Debug=N


//查询器械的批号
queryBatchNo.Type=TSQL
queryBatchNo.SQL=SELECT BATCH_NO FROM INV_STOCKDD WHERE PACK_CODE = <PACK_CODE> AND PACK_SEQ_NO = <PACK_SEQ_NO>
queryBatchNo.Debug=N



//更新主表库存
updateStockM.Type=TSQL
updateStockM.SQL=UPDATE INV_STOCKM SET STOCK_QTY = STOCK_QTY + 1 WHERE ORG_CODE = <ORG_CODE> AND INV_CODE = <INV_CODE>
updateStockM.Debug=N


  
//更新细表库存
updateStockD.Type=TSQL
updateStockD.SQL=UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY + 1 & 
			WHERE ORG_CODE = <ORG_CODE> AND INV_CODE = <INV_CODE> AND BATCH_NO = <BATCH_NO>
updateStockD.Debug=N




//查询回收单信息
queryBackDisinfection.Type=TSQL
queryBackDisinfection.SQL=SELECT RECYCLE_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE & 
				FROM INV_DISINFECTION & 
				WHERE OPT_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy/mm/dd hh24:mi:ss') AND TO_DATE(<END_DATE>,'yyyy/mm/dd hh24:mi:ss') & 
				GROUP BY RECYCLE_NO,OPT_DATE,OPT_USER,OPT_TERM,ORG_CODE ORDER BY RECYCLE_NO DESC
queryBackDisinfection.ITEM=RECYCLE_NO  
queryBackDisinfection.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryBackDisinfection.Debug=N


//根据回收单号查询回收单明细
queryDisinfectionByRecycleNo.Type=TSQL  
queryDisinfectionByRecycleNo.SQL=SELECT P.PACK_DESC,D.PACK_CODE,D.PACK_SEQ_NO,D.RECYCLE_DATE,D.RECYCLE_USER, & 
					D.WASH_DATE,D.WASH_USER,D.DISINFECTION_DATE,D.DISINFECTION_VALID_DATE,D.DISINFECTION_USER,D.DISINFECTION_NUM, & 
					D.DISINFECTION_POTSEQ,D.DISINFECTION_PROGRAM,D.QTY,D.BARCODE  & 
					FROM INV_DISINFECTION D LEFT JOIN INV_PACKM P ON D.PACK_CODE = P.PACK_CODE 
queryDisinfectionByRecycleNo.ITEM=RECYCLE_NO
queryDisinfectionByRecycleNo.RECYCLE_NO=RECYCLE_NO=<RECYCLE_NO>
queryDisinfectionByRecycleNo.Debug=N  


//查询手术包条码打印所需信息
queryBarcodeInfo.Type=TSQL
queryBarcodeInfo.SQL=SELECT P.PACK_CODE, P.PACK_SEQ_NO, S.USER_NAME, O.ORG_DESC, D.DISINFECTION_DATE, D.DISINFECTION_VALID_DATE, D.DISINFECTION_USER & 
			FROM INV_PACKSTOCKM P LEFT JOIN INV_DISINFECTION D ON P.PACK_CODE = D.PACK_CODE AND P.PACK_SEQ_NO = D.PACK_SEQ_NO & 
			LEFT JOIN INV_ORG O ON P.ORG_CODE = O.ORG_CODE LEFT JOIN SYS_OPERATOR S ON D.DISINFECTION_USER = S.USER_ID & 
			WHERE P.PACK_CODE = <PACK_CODE> AND P.PACK_SEQ_NO = <PACK_SEQ_NO> AND D.RECYCLE_NO = <RECYCLE_NO> 
queryBarcodeInfo.Debug=N


//查询科室名称
queryDeptName.Type=TSQL
queryDeptName.SQL=SELECT O.ORG_DESC FROM INV_ORG O WHERE O.ORG_CODE = <ORG_CODE>
queryDeptName.Debug=N


//查询用户名称
queryUserName.Type=TSQL
queryUserName.SQL=SELECT S.USER_NAME FROM SYS_OPERATOR S WHERE S.USER_ID = <USER_ID>
queryUserName.Debug=N

//有效条码  SUBSTR(PM.BARCODE,0,9)
//根据条码查询手术包信息 --- 回收状态才能打包    打包状态才能灭菌      灭菌状态才能出库   出库状态(在库状态)能回收  注意损耗管理   ,PM.USED_NUMBLE
queryPackageInfoByBarcode.Type=TSQL  
queryPackageInfoByBarcode.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC, P.VALUE_DATE, PM.BARCODE & 
			FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE       
queryPackageInfoByBarcode.item=BARCODE;PACK_CODE;PACK_SEQ_NO
queryPackageInfoByBarcode.BARCODE=subStr(PM.BARCODE,0,9)=subStr(<BARCODE>,0,9)
queryPackageInfoByBarcode.PACK_CODE=PM.PACK_CODE=<PACK_CODE>           
queryPackageInfoByBarcode.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>  
queryPackageInfoByBarcode.STATUS=PM.STATUS=<STATUS>
queryPackageInfoByBarcode.Debug=N       

//全条码(包含流水号)
//根据条码查询手术包信息 --- 回收状态才能打包    打包状态才能灭菌      灭菌状态才能出库   出库状态(在库状态)能回收  注意损耗管理   ,PM.USED_NUMBLE
queryPackageInfoByAllBarcode.Type=TSQL  
queryPackageInfoByAllBarcode.SQL=SELECT PM.PACK_CODE, PM.PACK_SEQ_NO, PM.STATUS, PM.QTY, P.PACK_DESC, P.VALUE_DATE, PM.BARCODE & 
			          FROM INV_PACKSTOCKM PM LEFT JOIN INV_PACKM P ON PM.PACK_CODE = P.PACK_CODE       
queryPackageInfoByAllBarcode.item=BARCODE;PACK_CODE;PACK_SEQ_NO
queryPackageInfoByAllBarcode.BARCODE=PM.BARCODE=<BARCODE>              
queryPackageInfoByAllBarcode.PACK_CODE=PM.PACK_CODE=<PACK_CODE>           
queryPackageInfoByAllBarcode.PACK_SEQ_NO=PM.PACK_SEQ_NO=<PACK_SEQ_NO>  
queryPackageInfoByAllBarcode.STATUS=PM.STATUS=<STATUS>
queryPackageInfoByAllBarcode.Debug=N      


//根据条码查询手术包物资
queryPMInfoByBarcode.Type=TSQL
queryPMInfoByBarcode.SQL=SELECT B.INV_ABS_DESC AS INV_CHN_DESC, D.STOCK_UNIT, D.QTY, B.COST_PRICE, D.RECOUNT_TIME & 
				FROM INV_PACKSTOCKD D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE LEFT JOIN SYS_UNIT S ON D.STOCK_UNIT = S.UNIT_CODE & 
				WHERE D.ONCE_USE_FLG = 'N' AND D.BARCODE = <BARCODE>
queryPMInfoByBarcode.Debug=N















       











