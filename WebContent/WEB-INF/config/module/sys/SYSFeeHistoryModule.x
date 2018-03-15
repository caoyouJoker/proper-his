# 
#  Title:ҽ����ʷ��
# 
#  Description:ҽ����ʷ��
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2009.07.22
#
#  version 1.0
#
Module.item=insert;update


//����ҽ����ʷ��
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_FEE_HISTORY( &
		ORDER_CODE, START_DATE, END_DATE, ORDER_DESC, ACTIVE_FLG, LAST_FLG, &
   		PY1, PY2, SEQ, DESCRIPTION, TRADE_ENG_DESC, GOODS_DESC, &
   		GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, SPECIFICATION, NHI_FEE_DESC, HABITAT_TYPE, &
   		MAN_CODE, HYGIENE_TRADE_CODE, ORDER_CAT1_CODE, CHARGE_HOSP_CODE, OWN_PRICE, NHI_PRICE, &
   		GOV_PRICE, UNIT_CODE, LET_KEYIN_FLG, DISCOUNT_FLG, EXPENSIVE_FLG, OPD_FIT_FLG, &
  		EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG, INTV_ORDER_FLG, LCS_CLASS_CODE, &
   		TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, EXEC_ORDER_FLG, EXEC_DEPT_CODE, INSPAY_TYPE, &
   		ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, NHI_CODE_I, CTRL_FLG, &
   		CLPGROUP_CODE, ORDERSET_FLG, INDV_FLG, SUB_SYSTEM_CODE, RPTTYPE_CODE, DEV_CODE, &
   		OPTITEM_CODE, MR_CODE, DEGREE_CODE, CIS_FLG, OPT_USER, OPT_DATE, &
   		OPT_TERM, RPP_CODE, OWN_PRICE2, OWN_PRICE3, TUBE_TYPE, ATC_FLG, & 
   		IS_REMARK, ACTION_CODE, CAT1_TYPE, ATC_FLG_I, REGION_CODE) &
           VALUES( &
           	<ORDER_CODE>, <START_DATE>, <END_DATE>, <ORDER_DESC>, <ACTIVE_FLG>, <LAST_FLG>, &
   		<PY1>, <PY2>, <SEQ>, <DESCRIPTION>, <TRADE_ENG_DESC>, <GOODS_DESC>, &
   		<GOODS_PYCODE>, <ALIAS_DESC>, <ALIAS_PYCODE>, <SPECIFICATION>, <NHI_FEE_DESC>, <HABITAT_TYPE>, &
   		<MAN_CODE>, <HYGIENE_TRADE_CODE>, <ORDER_CAT1_CODE>, <CHARGE_HOSP_CODE>, <OWN_PRICE>, <NHI_PRICE>, &
   		<GOV_PRICE>, <UNIT_CODE>, <LET_KEYIN_FLG>, <DISCOUNT_FLG>, <EXPENSIVE_FLG>, <OPD_FIT_FLG>, &
  		<EMG_FIT_FLG>, <IPD_FIT_FLG>, <HRM_FIT_FLG>, <DR_ORDER_FLG>, <INTV_ORDER_FLG>, <LCS_CLASS_CODE>, &
   		<TRANS_OUT_FLG>, <TRANS_HOSP_CODE>, <USEDEPT_CODE>, <EXEC_ORDER_FLG>, <EXEC_DEPT_CODE>, <INSPAY_TYPE>, &
   		<ADDPAY_RATE>, <ADDPAY_AMT>, <NHI_CODE_O>, <NHI_CODE_E>, <NHI_CODE_I>, <CTRL_FLG>, &
   		<CLPGROUP_CODE>, <ORDERSET_FLG>, <INDV_FLG>, <SUB_SYSTEM_CODE>, <RPTTYPE_CODE>, <DEV_CODE>, &
   		<OPTITEM_CODE>, <MR_CODE>, <DEGREE_CODE>, <CIS_FLG>, <OPT_USER>, <OPT_DATE>, &
   		<OPT_TERM>, <RPP_CODE>, <OWN_PRICE2>, <OWN_PRICE3>, <TUBE_TYPE>, <ATC_FLG>, &
   		<IS_REMARK>, <ACTION_CODE>, <CAT1_TYPE>, <ATC_FLG_I>, <REGION_CODE>)
insert.Debug=N


//����ҽ����ʷ��
update.Type=TSQL
update.SQL=UPDATE SYS_FEE_HISTORY SET &
		END_DATE = <END_DATE> , &
		ACTIVE_FLG = <ACTIVE_FLG> , &
		RPP_CODE = <RPP_CODE> , &
		OPT_USER = <OPT_USER> , &
		OPT_DATE = <OPT_DATE> , &
		OPT_TERM = <OPT_TERM> &
	    WHERE ORDER_CODE = <ORDER_CODE> &
	      AND START_DATE = <START_DATE>
update.Debug=N