# 
#  Title:费用module
# 
#  Description:费用module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.09.22
#  version 1.0
#
Module.item=getGroupList;selectOrder;getFee;getChargHospCode;getCodeDesc;getOrderPycode;getTradeEndDesc;getGoodsDesc;getAliasDesc;getAliasPycode;&
	    getPrice;insert;getOldPrice;update;getFeeData;getCat1Code;getFeeAllData;getCtrFlg;updateCtrflg;updateINSToSysFee;querySysFeeIns


//得到组列表
getGroupList.Type=TSQL
getGroupList.SQL=SELECT ORDER_CODE AS ID,ORDER_DESC AS NAME,ORDER_PYCODE AS PY1 FROM SYS_FEE  ORDER BY ORDER_CODE

//查询
//第一行where条件：基本条件
//第二行where条件：排除专用处方签之管制药品
//第三行where条件：适用科室判断
//第四行where条件：显示数据
selectOrder.Type=TSQL
selectOrder.SQL=SELECT D.ORDER_CODE AS ID,&
CASE <GOODS_SHOW>& 
	WHEN 'N' THEN D.ORDER_DESC&
 	WHEN 'Y' THEN D.GOODS_DESC&
END NAME, &
CASE <GOODS_SHOW>&
	WHEN 'N' THEN D.ORDER_PYCODE&
	WHEN 'Y' THEN D.GOODS_PYCODE&
END PY1,&
  D.DESCRIPTION AS TEXT,D.ALIAS_DESC AS VALUE FROM SYS_FEE D ,PHA_BASE G ,SYS_ORDERUSEDEPT R,SYS_CTRLDRUGCLASS O&
WHERE D.ORDER_CODE=<ORDER_CODE> AND  D.INDV_FLG = ‘Y’AND D.ORDER_CAT1 = ‘PHA’&
AND G.CTRLDRUGCLASS_CODE IS NULL OR (G.CTRLDRUGCLASS_CODE IS NOT NULL and O.PRNSPCFORM_FLG = ‘N’)&
AND D.USEDEPT_CODE IS NULL OR ( D.USEDEPT_CODE IS NOT NULL AND R.ORDER_CODE= D.ORDER_CODE AND R.USEDEPT_CODE =<DEPT_CODE>) ORDER BY ID
selectOrder.Debug=N


//根据ORDER_CODE查询费用
getFee.Type=TSQL
getFee.SQL=SELECT OWN_PRICE,OWN_PRICE2,OWN_PRICE3 FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getFee.Debug=N

//根据ORDER_CODE查询CHARGE_HOSP_CODE
getChargHospCode.Type=TSQL
getChargHospCode.SQL=SELECT CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getChargHospCode.Debug=N

//根据ORDER_CODE查询ORDER_DESC
getCodeDesc.Type=TSQL
getCodeDesc.SQL=SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getCodeDesc.Debug=N

//根据ORDER_CODE查询ORDER_PYCODE
getOrderPycode.Type=TSQL
getOrderPycode.SQL=SELECT ORDER_PYCODE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getOrderPycode.Debug=N

//根据ORDER_CODE查询TRADE_ENG_DESC
getTradeEndDesc.Type=TSQL
getTradeEndDesc.SQL=SELECT TRADE_ENG_DESC FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getTradeEndDesc.Debug=N

//根据ORDER_CODE查询GOODS_DESC
getGoodsDesc.Type=TSQL
getGoodsDesc.SQL=SELECT GOODS_DESC FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getGoodsDesc.Debug=N

//根据ORDER_CODE查询GOODS_PYCODE
getGoodsPycode.Type=TSQL
getGoodsPycode.SQL=SELECT GOODS_PYCODE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getGoodsPycode.Debug=N

//根据ORDER_CODE查询费用ALIAS_DESC
getAliasDesc.Type=TSQL
getAliasDesc.SQL=SELECT ALIAS_DESC FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getAliasDesc.Debug=N

//根据ORDER_CODE查询ALIAS_PYCODE
getAliasPycode.Type=TSQL
getAliasPycode.SQL=SELECT ALIAS_PYCODE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getAliasPycode.Debug=N


//根据ORDER_CODE查询OWN_PRICE,NHI_PRICE,GOV_PRICE
getPrice.Type=TSQL
getPrice.SQL=SELECT OWN_PRICE, NHI_PRICE, GOV_PRICE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getPrice.Debug=N


//新增医嘱
//===========pangben modify 20110816 添加SYS_GRUG_CLASS，NOADDTION_FLG,SYS_PHA_CLASS
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_FEE( &
		ORDER_CODE, ORDER_DESC, PY1, PY2, SEQ, DESCRIPTION, &
  		TRADE_ENG_DESC, GOODS_DESC, GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, SPECIFICATION, &
   		NHI_FEE_DESC, HABITAT_TYPE, MAN_CODE, HYGIENE_TRADE_CODE, ORDER_CAT1_CODE, CHARGE_HOSP_CODE, &
   		OWN_PRICE, NHI_PRICE, GOV_PRICE, UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, &
   		EXPENSIVE_FLG, OPD_FIT_FLG, EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, &
   		INTV_ORDER_FLG, LCS_CLASS_CODE, TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, EXEC_ORDER_FLG, &
   		EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
   		NHI_CODE_I, CTRL_FLG, CLPGROUP_CODE, ORDERSET_FLG, INDV_FLG, SUB_SYSTEM_CODE, &
   		RPTTYPE_CODE, DEV_CODE, OPTITEM_CODE, MR_CODE, DEGREE_CODE, CIS_FLG, &
   		OPT_USER, OPT_DATE, OPT_TERM, CAT1_TYPE, OWN_PRICE2, OWN_PRICE3, &
   		TUBE_TYPE, ACTIVE_FLG, IS_REMARK, ACTION_CODE, ATC_FLG, ATC_FLG_I, REGION_CODE,SYS_GRUG_CLASS,NOADDTION_FLG,SYS_PHA_CLASS, &
   		DRUG_NOTES_PATIENT,DRUG_NOTES_DR,DRUG_NOTE,DRUG_FORM,SUPPLIES_TYPE,DDD,ORD_SUPERVISION, &
		REMARK_1,REMARK_2 &
		) &
           VALUES( &
           	<ORDER_CODE>, <ORDER_DESC>, <PY1>, <PY2>, <SEQ>, <DESCRIPTION>, &
   		<TRADE_ENG_DESC>, <GOODS_DESC>, <GOODS_PYCODE>, <ALIAS_DESC>, <ALIAS_PYCODE>, <SPECIFICATION>, &
   		<NHI_FEE_DESC>, <HABITAT_TYPE>, <MAN_CODE>, <HYGIENE_TRADE_CODE>, <ORDER_CAT1_CODE>, <CHARGE_HOSP_CODE>, &
   		<OWN_PRICE>, <NHI_PRICE>, <GOV_PRICE>, <UNIT_CODE>, <LET_KEYIN_FLG>, <DISCOUNT_FLG>, &
   		<EXPENSIVE_FLG>, <OPD_FIT_FLG>, <EMG_FIT_FLG>, <IPD_FIT_FLG>, <HRM_FIT_FLG>, <DR_ORDER_FLG>, &
   		<INTV_ORDER_FLG>, <LCS_CLASS_CODE>, <TRANS_OUT_FLG>, <TRANS_HOSP_CODE>, <USEDEPT_CODE>, <EXEC_ORDER_FLG>, &
   		<EXEC_DEPT_CODE>, <INSPAY_TYPE>, <ADDPAY_RATE>, <ADDPAY_AMT>, <NHI_CODE_O>, <NHI_CODE_E>, &
   		<NHI_CODE_I>, <CTRL_FLG>, <CLPGROUP_CODE>, <ORDERSET_FLG>, <INDV_FLG>, <SUB_SYSTEM_CODE>, &
   		<RPTTYPE_CODE>, <DEV_CODE>, <OPTITEM_CODE>, <MR_CODE>, <DEGREE_CODE>, <CIS_FLG>, &
   		<OPT_USER>, <OPT_DATE>, <OPT_TERM>, <CAT1_TYPE>, <OWN_PRICE2>, <OWN_PRICE3>, &
   		<TUBE_TYPE>, <ACTIVE_FLG>, <IS_REMARK>, <ACTION_CODE>, <ATC_FLG>, <ATC_FLG_I>, <REGION_CODE>,<SYS_GRUG_CLASS>,<NOADDTION_FLG>,<SYS_PHA_CLASS>, &
   		<DRUG_NOTES_PATIENT>,<DRUG_NOTES_DR>,<DRUG_NOTE>,<DRUG_FORM>,<SUPPLIES_TYPE>,<DDD>,<ORD_SUPERVISION>, &
		<REMARK_1>,<REMARK_2> &
		) 
insert.Debug=Y


//更新医嘱
//===========pangben modify 20110816 添加SYS_GRUG_CLASS，NOADDTION_FLG,SYS_PHA_CLASS
update.Type=TSQL
update.SQL=UPDATE SYS_FEE SET &
		ORDER_DESC=<ORDER_DESC>, PY1=<PY1>, PY2=<PY2>, SEQ=<SEQ>, DESCRIPTION=<DESCRIPTION>, &
  		TRADE_ENG_DESC=<TRADE_ENG_DESC>, GOODS_DESC=<GOODS_DESC>, GOODS_PYCODE=<GOODS_PYCODE>, ALIAS_DESC=<ALIAS_DESC>, & 
  		ALIAS_PYCODE=<ALIAS_PYCODE>, SPECIFICATION=<SPECIFICATION>, &
   		NHI_FEE_DESC=<NHI_FEE_DESC>, HABITAT_TYPE=<HABITAT_TYPE>, MAN_CODE=<MAN_CODE>, HYGIENE_TRADE_CODE=<HYGIENE_TRADE_CODE>, & 
   		ORDER_CAT1_CODE=<ORDER_CAT1_CODE>, CHARGE_HOSP_CODE=<CHARGE_HOSP_CODE>, &
   		OWN_PRICE=<OWN_PRICE>, NHI_PRICE=<NHI_PRICE>, GOV_PRICE=<GOV_PRICE>, UNIT_CODE=<UNIT_CODE>, &
   		LET_KEYIN_FLG=<LET_KEYIN_FLG>, DISCOUNT_FLG=<DISCOUNT_FLG>, &
   		EXPENSIVE_FLG=<EXPENSIVE_FLG>, OPD_FIT_FLG=<OPD_FIT_FLG>, EMG_FIT_FLG=<EMG_FIT_FLG>, IPD_FIT_FLG=<IPD_FIT_FLG>, &
   		HRM_FIT_FLG=<HRM_FIT_FLG>, DR_ORDER_FLG=<DR_ORDER_FLG>, &
   		INTV_ORDER_FLG=<INTV_ORDER_FLG>, LCS_CLASS_CODE=<LCS_CLASS_CODE>, TRANS_OUT_FLG=<TRANS_OUT_FLG>, &
   		TRANS_HOSP_CODE=<TRANS_HOSP_CODE>, USEDEPT_CODE=<USEDEPT_CODE>, EXEC_ORDER_FLG=<EXEC_ORDER_FLG>, &
   		EXEC_DEPT_CODE=<EXEC_DEPT_CODE>, INSPAY_TYPE=<INSPAY_TYPE>, ADDPAY_RATE=<ADDPAY_RATE>, ADDPAY_AMT=<ADDPAY_AMT>, & 
   		NHI_CODE_O=<NHI_CODE_O>, NHI_CODE_E=<NHI_CODE_E>, &
   		NHI_CODE_I=<NHI_CODE_I>, CTRL_FLG=<CTRL_FLG>, CLPGROUP_CODE=<CLPGROUP_CODE>, ORDERSET_FLG=<ORDERSET_FLG>, &
   		INDV_FLG=<INDV_FLG>, SUB_SYSTEM_CODE=<SUB_SYSTEM_CODE>, &
   		RPTTYPE_CODE=<RPTTYPE_CODE>, DEV_CODE=<DEV_CODE>, OPTITEM_CODE=<OPTITEM_CODE>, &
   		MR_CODE=<MR_CODE>, DEGREE_CODE=<DEGREE_CODE>, CIS_FLG=<CIS_FLG>, &
   		OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM>, CAT1_TYPE=<CAT1_TYPE>, &
   		OWN_PRICE2=<OWN_PRICE2>, OWN_PRICE3=<OWN_PRICE3>, TUBE_TYPE=<TUBE_TYPE>, ACTIVE_FLG=<ACTIVE_FLG>,SYS_PHA_CLASS=<SYS_PHA_CLASS>, &
   		IS_REMARK=<IS_REMARK>, ACTION_CODE=<ACTION_CODE>, ATC_FLG=<ATC_FLG>, ATC_FLG_I=<ATC_FLG_I> , REGION_CODE=<REGION_CODE>,SYS_GRUG_CLASS=<SYS_GRUG_CLASS>, &
   		DRUG_NOTES_PATIENT=<DRUG_NOTES_PATIENT>,NOADDTION_FLG=<NOADDTION_FLG>,DRUG_NOTES_DR=<DRUG_NOTES_DR>,DRUG_NOTE=<DRUG_NOTE>,SUPPLIES_TYPE=<SUPPLIES_TYPE>, &
   		DDD=<DDD>,ORD_SUPERVISION=<ORD_SUPERVISION>, &
                DRUG_FORM=<DRUG_FORM>,REMARK_1=<REMARK_1>,REMARK_2=<REMARK_2> &
           WHERE ORDER_CODE = <ORDER_CODE>
update.Debug=N



//删除医嘱
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_FEE WHERE ORDER_CODE = <ORDER_CODE>
delete.Debug=N


//根据ORDER_CODE查询费用
getOldPrice.Type=TSQL
getOldPrice.SQL=SELECT OWN_PRICE, NHI_PRICE, GOV_PRICE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getOldPrice.Debug=N

//根据ORDER_CODE查询OWN_PRICE,NHI_PRICE,CHARGE_HOSP_CODE
getFeeData.Type=TSQL
getFeeData.SQL=SELECT OWN_PRICE,NHI_PRICE,OWN_PRICE2,OWN_PRICE3 ,CHARGE_HOSP_CODE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getFeeData.Debug=N

//根据ORDER_CODE查询CAT1_TYPE
getCat1Code.Type=TSQL
getCat1Code.SQL=SELECT CAT1_TYPE FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getCat1Code.Debug=N

/根据ORDER_CODE查询CTR_FLG
getCtrFlg.Type=TSQL
getCtrFlg.SQL=SELECT CRT_FLG FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
getCtrFlg.Debug=N

/根据ORDER_CODE改变CTR_FLG
updateCtrflg.Type=TSQL
updateCtrflg.SQL=UPDATE SYS_FEE SET CRT_FLG=<CRT_FLG> WHERE ORDER_CODE=<ORDER_CODE>
updateCtrflg.Debug=N

//根据ORDER_CODE查询医嘱信息
getFeeAllData.Type=TSQL
getFeeAllData.SQL=SELECT ORDER_CODE, ORDER_DESC, PY1, PY2, SEQ, DESCRIPTION, &
			 TRADE_ENG_DESC, GOODS_DESC, GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, SPECIFICATION, &
			 NHI_FEE_DESC, HABITAT_TYPE, MAN_CODE, HYGIENE_TRADE_CODE, ORDER_CAT1_CODE, CHARGE_HOSP_CODE, &
			 OWN_PRICE, NHI_PRICE, GOV_PRICE, UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, &
			 EXPENSIVE_FLG, OPD_FIT_FLG, EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, &
			 INTV_ORDER_FLG, LCS_CLASS_CODE, TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, EXEC_ORDER_FLG, &
			 EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
			 NHI_CODE_I, CTRL_FLG, CLPGROUP_CODE, ORDERSET_FLG, INDV_FLG, SUB_SYSTEM_CODE, &
			 RPTTYPE_CODE, DEV_CODE, OPTITEM_CODE, MR_CODE, DEGREE_CODE, CIS_FLG, &
			 OPT_USER, OPT_DATE, OPT_TERM, CAT1_TYPE, OWN_PRICE2, OWN_PRICE3, &
			 TUBE_TYPE, ACTIVE_FLG, IS_REMARK, ACTION_CODE, ATC_FLG, ATC_FLG_I &
		    FROM SYS_FEE &
		   WHERE ORDER_CODE = <ORDER_CODE>
getFeeAllData.Debug=N

//医保三目字典修改药品医保对应
updateINSToSysFee.Type=TSQL
updateINSToSysFee.SQL=UPDATE SYS_FEE SET NHI_CODE_I=<NHI_CODE_I>,NHI_CODE_O=<NHI_CODE_O>,NHI_CODE_E=<NHI_CODE_E>,&
                      NHI_FEE_DESC=<NHI_FEE_DESC>,NHI_PRICE=<NHI_PRICE> WHERE ORDER_CODE=<ORDER_CODE>
updateINSToSysFee.Debug=N

//住院费用分割
//判断order是否存在sys_Fee
querySysFeeIns.Type=TSQL
querySysFeeIns.SQL=SELECT  NHI_CODE_I,ORDER_DESC,SPECIFICATION,ADDPAY_AMT FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE>
querySysFeeIns.Debug=N


