#
# Title:静点报到
#
# Description:静点报到
#
# Copyright: JavaHis (c) 2009
#
# @author wangl 2009/10/271

Module.item=queryRegister;queryOrderDetail


//根据条件查询静点室报到人员
queryRegister.Type=TSQL
queryRegister.SQL=SELECT DISTINCT A.ADM_TYPE, A.CASE_NO, A.QUE_NO, A.ADM_DATE, A.APPT_CODE, &
                         A.ARRIVE_FLG, A.SESSION_CODE, D.SESSION_DESC, A.DR_CODE, &
                         B.USER_NAME AS DR_NAME, A.REALDR_CODE, C.USER_NAME AS REALDR_NAME, A.DEPT_CODE, &
			 E.DEPT_CHN_DESC, A.CLINICROOM_NO, F.CLINICROOM_DESC, &
			 A.SEE_DR_FLG,  A.CTZ1_CODE, A.CTZ2_CODE, &
			 A.CTZ3_CODE, A.VISIT_CODE, A.CONTRACT_CODE, &
                         A.MR_NO,G.PAT_NAME, G.SEX_CODE, G.BIRTH_DATE &
	           FROM  REG_PATADM A, &
			 SYS_OPERATOR B, &
			 SYS_OPERATOR C, &
			 REG_SESSION D, &
			 SYS_DEPT E, &
			 REG_CLINICROOM F, &
			 SYS_PATINFO G, &
			 OPD_ORDER H ,&
			 PHL_REGION I &
	           WHERE A.DR_CODE = B.USER_ID &
		     AND A.REALDR_CODE = C.USER_ID &
		     AND A.ADM_TYPE = D.ADM_TYPE &
		     AND A.SESSION_CODE = D.SESSION_CODE &
		     AND A.DEPT_CODE = E.DEPT_CODE &
		     AND A.ADM_TYPE = F.ADM_TYPE &
		     AND A.CLINICROOM_NO = F.CLINICROOM_NO &
		     AND A.MR_NO = G.MR_NO &
		     AND A.ADM_TYPE = H.ADM_TYPE &
		     AND A.CASE_NO = H.CASE_NO &
		     AND A.REGCAN_USER IS  NULL &
		     //AND A.ADM_STATUS IN ('3','4') &
		     AND A.ADM_DATE = <ADM_DATE> &
		     AND H.DOSE_TYPE IN ('I','F') &
                     AND H.PHA_RETN_CODE IS NULL &
                     AND F.PHL_REGION_CODE = I.REGION_CODE
queryRegister.ITEM=MR_NO;ADM_TYPE_O;ADM_TYPE_E;ADM_TYPE;SESSION_CODE;DEPT_CODE;CLINICROOM_NO;DR_CODE;PHL_REGION_CODE;REGION_CODE
queryRegister.MR_NO=A.MR_NO=<MR_NO>
queryRegister.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryRegister.ADM_TYPE_O=A.ADM_TYPE='O'
queryRegister.ADM_TYPE_E=A.ADM_TYPE='E'
queryRegister.ADM_TYPE=A.ADM_TYPE IN ('O','E')
queryRegister.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
queryRegister.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryRegister.CLINICROOM_NO=A.CLINICROOM_NO=<CLINICROOM_NO>
queryRegister.DR_CODE=A.DR_CODE=<DR_CODE>
queryRegister.PHL_REGION_CODE=I.REGION_CODE=<PHL_REGION_CODE>
queryRegister.Debug=N


//根据条件查询静点室区域
queryOrderDetail.Type=TSQL
queryOrderDetail.SQL=SELECT 'Y' AS SELECT_FLG, A.LINKMAIN_FLG, A.LINK_NO, B.ORDER_DESC, B.SPECIFICATION, A.DR_NOTE, &
                            C.DOSE_CHN_DESC, A.ORDER_DATE, D.USER_NAME AS DR_NAME, &
                            A.PHA_DISPENSE_DATE, E.USER_NAME AS PHA_DISPENSE_NAME, &
                            A.RX_NO, A.DR_CODE, A.ROUTE_CODE, A.FREQ_CODE, A.TAKE_DAYS, &
                            A.NS_NOTE,A.ORDER_CODE, A.DISPENSE_QTY, F.UNIT_CHN_DESC, G.ROUTE_CHN_DESC, A.SEQ_NO &
	              FROM  OPD_ORDER A, PHA_BASE B, PHA_DOSE C, SYS_OPERATOR D, SYS_OPERATOR E, SYS_UNIT F, SYS_PHAROUTE G &
	           WHERE A.ORDER_CODE = B.ORDER_CODE &
		     AND B.DOSE_CODE = C.DOSE_CODE &
		     AND A.DR_CODE = D.USER_ID &
		     AND A.DISPENSE_UNIT = F.UNIT_CODE &
    	             AND A.ROUTE_CODE = G.ROUTE_CODE &
		     AND A.PHA_DISPENSE_CODE = E.USER_ID &
		     AND A.PHA_DISPENSE_CODE IS NOT NULL &
		     AND A.DC_DR_CODE IS NULL &
		     AND A.DOSE_TYPE IN ('I','F') &
		     ORDER BY A.LINK_NO
queryOrderDetail.ITEM=MR_NO;CASE_NO
queryOrderDetail.MR_NO=A.MR_NO=<MR_NO>
queryOrderDetail.CASE_NO=A.CASE_NO=<CASE_NO>
queryOrderDetail.Debug=N


