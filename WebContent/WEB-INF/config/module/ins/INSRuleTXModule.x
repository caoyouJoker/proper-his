# 
#  Title:医保卡三目字典收费标准module
# 
#  Description:医保卡三目字典收费标准module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author pangben 2011-11-08
#  version 1.0
#

Module.item=insertINSRule;deleteINSRule;updateINSRule;selectINSRule;&
selectMateSysFee;selectSumSame;updateSysFeeNhi;saveFeeHistory;&
selectNotMateSysFee;updateSysFee;updateSysFeehistory

//查询三目基本档数据
selectINSRule.Type=TSQL
selectINSRule.SQL=SELECT 'N' FLG,SFDLBM, SFXMBM, XMBM, &
		   XMMC, XMRJ, TXBZ, &
		   XMLB, JX, GG, &
		   DW, YF, YL, &
		   SL, PZWH, BZJG, &
		   SJJG, ZGXJ, ZFBL1, &
		   ZFBL2, ZFBL3, BPXE, &
		   BZ, TJDM, FLZB1, &
		   FLZB2, FLZB3, FLZB4, &
		   FLZB5, FLZB6, FLZB7, &
		   SPMC, SPMCRJ, LJZFBZ, &
		   YYZJBZ, YYSMBM, FPLB, &
		   KSSJ, JSSJ, SYFW, &
		   SCQY, FCFYBS, MZYYBZ, &
		   XMBZ, FYMGL, YKD228, &
		   YKD241, AZBBZ, YKD242, &
		   TXXMBZ FROM INS_RULE
selectINSRule.item=SFDLBM;XMBM
selectINSRule.SFDLBM=SFDLBM=<SFDLBM>
selectINSRule.XMBM=XMBM=<XMBM>
selectINSRule.Debug=N

//修改医保药品
updateINSRule.Type=TSQL
updateINSRule.SQL=UPDATE INS_RULE SET  SFDLBM=<SFDLBM>, SFXMBM=<SFXMBM>, XMBM=<XMBM>, XMMC=<XMMC>, XMRJ=<XMRJ>, BZJG=<BZJG>, SJJG=<SJJG>, JX=<JX>, GG=<GG>,&
                  DW=<DW>, YF=<YF>, SL=<SL>,BZ=<BZ>,XMLB=<XMLB>,&
                  OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                  WHERE  SFDLBM=<SFDLBM> AND XMBM=<XMBM>
updateINSRule.Debug=N

//删除数据
deleteINSRule.Type=TSQL
deleteINSRule.SQL=DELETE FROM INS_RULE WHERE SFDLBM=<SFDLBM> AND XMBM=<XMBM>
deleteINSRule.Debug=N

//新增

insertINSRule.Type=TSQL
insertINSRule.SQL=INSERT INTO INS_RULE &
            (SFDLBM, SFXMBM, XMBM, XMMC,XMRJ,JX,GG,&
            DW, YF, SL, BZJG,SJJG, BZ,XMLB,OPT_USER,OPT_DATE,OPT_TERM)&
             VALUES&
             (<SFDLBM>, <SFXMBM>, <XMBM>, <XMMC>,<XMRJ>,<JX>,<GG>,&
            <DW>, <YF>, <SL>, <BZJG>,<SJJG>, <BZ>,<XMLB>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertINSRule.Debug=N

//查询药品字典数据已经存在医保编码的数据-----所有药品

selectMateSysFee.Type=TSQL
selectMateSysFee.SQL=SELECT 'N' AS FLG, A.ORDER_CODE, A.ORDER_DESC ||CASE &
					 WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <>''&
					 THEN '(' || A.SPECIFICATION || ')'&
					 ELSE ''&
					  END AS ORDER_DESC, B.DOSE_CODE,&
		 A.DESCRIPTION, A.SPECIFICATION,MAN_CODE,NHI_FEE_DESC,A.HYGIENE_TRADE_CODE, &
		OWN_PRICE, NHI_PRICE, &
		UNIT_CODE,&
		IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG,& 
		EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, &
		ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
		NHI_CODE_I, &
		CRT_FLG, SYS_GRUG_CLASS, NOADDTION_FLG, &
		SYS_PHA_CLASS &
		FROM SYS_FEE A LEFT JOIN PHA_BASE B ON A.ORDER_CODE=B.ORDER_CODE WHERE  &
		((NHI_CODE_O IS NOT NULL OR NHI_CODE_O <>'') OR (NHI_CODE_E IS NOT NULL OR NHI_CODE_E <>'') &
		OR  (NHI_CODE_I IS NOT NULL OR NHI_CODE_I <>'')) AND  A.CHARGE_HOSP_CODE IN ('M01','N01','L01','Z01')
selectMateSysFee.Debug=N

//完全匹配数据单选按钮选中执行查询数据-----所有药品
selectSumSame.Type=TSQL
selectSumSame.SQL=SELECT ORDER_CODE, ORDER_DESC,&
		 DESCRIPTION, SPECIFICATION,MAN_CODE, &
		OWN_PRICE, NHI_PRICE,HYGIENE_TRADE_CODE, &
		UNIT_CODE,&
		IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG,& 
		EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, &
		ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
		NHI_CODE_I, &
		CRT_FLG, SYS_GRUG_CLASS, NOADDTION_FLG, &
		SYS_PHA_CLASS &
		FROM SYS_FEE  WHERE  &
		 CHARGE_HOSP_CODE IN ('M01','N01','L01','Z01')
selectSumSame.Debug=N

//修改SYS_FEE 医保字段

updateSysFeeNhi.Type=TSQL
updateSysFeeNhi.SQL=UPDATE SYS_FEE SET NHI_CODE_O=<NHI_CODE_O>,NHI_CODE_E=<NHI_CODE_E>,NHI_CODE_I=<NHI_CODE_I>,&
		    NHI_FEE_DESC=<NHI_FEE_DESC>,NHI_PRICE=<NHI_PRICE>,INSPAY_TYPE=<INSPAY_TYPE>, &
                  OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                  WHERE  ORDER_CODE=<ORDER_CODE>
updateSysFeeNhi.Debug=N

//保存SYS_FEE_HISTORY 医保字段

saveFeeHistory.Type=TSQL
saveFeeHistory.SQL=INSERT INTO SYS_FEE_HISTORY       &                        
                   (ORDER_CODE, START_DATE, END_DATE, &
                    ORDER_DESC, ACTIVE_FLG, &
                    PY1, PY2, SEQ, &
                    DESCRIPTION, TRADE_ENG_DESC, GOODS_DESC, &
                    GOODS_PYCODE, ALIAS_DESC, ALIAS_PYCODE, &
                    SPECIFICATION, NHI_FEE_DESC, HABITAT_TYPE, &
                    MAN_CODE, HYGIENE_TRADE_CODE, ORDER_CAT1_CODE,& 
                    CHARGE_HOSP_CODE, OWN_PRICE, NHI_PRICE, &
                    GOV_PRICE, UNIT_CODE, LET_KEYIN_FLG, &
                    DISCOUNT_FLG, EXPENSIVE_FLG, OPD_FIT_FLG,& 
                    EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, &
                    DR_ORDER_FLG, INTV_ORDER_FLG, LCS_CLASS_CODE,& 
                    TRANS_OUT_FLG, TRANS_HOSP_CODE, USEDEPT_CODE, &
                    EXEC_ORDER_FLG, EXEC_DEPT_CODE, INSPAY_TYPE, &
                    ADDPAY_RATE, ADDPAY_AMT, NHI_CODE_O, &
                    NHI_CODE_E, NHI_CODE_I, CTRL_FLG, &
                    CLPGROUP_CODE, ORDERSET_FLG, INDV_FLG,& 
                    SUB_SYSTEM_CODE, RPTTYPE_CODE, DEV_CODE, &
                    OPTITEM_CODE, MR_CODE, DEGREE_CODE, &
                    CIS_FLG, OPT_USER, OPT_DATE, &
                    OPT_TERM, ACTION_CODE, &
                    ATC_FLG, OWN_PRICE2, OWN_PRICE3, &
                    TUBE_TYPE, CAT1_TYPE, IS_REMARK, &
                    ATC_FLG_I, REMARK_1, REMARK_2, &
                    REGION_CODE, SYS_GRUG_CLASS, NOADDTION_FLG,& 
                        SYS_PHA_CLASS)&
    VALUES&
               (  <ORDER_CODE>, <START_DATE>, <END_DATE>, &
                 <ORDER_DESC>, <ACTIVE_FLG>, &
                 <PY1>, <PY2>, <SEQ>, &
                <DESCRIPTION>, <TRADE_ENG_DESC>, <GOODS_DESC>, &
                <GOODS_PYCODE>, <ALIAS_DESC>, <ALIAS_PYCODE>, &
               <SPECIFICATION>, <NHI_FEE_DESC>, <HABITAT_TYPE>, &
               <MAN_CODE>, <HYGIENE_TRADE_CODE>, <ORDER_CAT1_CODE>,& 
               <CHARGE_HOSP_CODE>, <OWN_PRICE>, <NHI_PRICE>, &
               <GOV_PRICE>, <UNIT_CODE>, <LET_KEYIN_FLG>, &
               <DISCOUNT_FLG>, <EXPENSIVE_FLG>, <OPD_FIT_FLG>,& 
               <EMG_FIT_FLG>, <IPD_FIT_FLG>, <HRM_FIT_FLG>, &
               <DR_ORDER_FLG>, <INTV_ORDER_FLG>, <LCS_CLASS_CODE>, &
               <TRANS_OUT_FLG>, <TRANS_HOSP_CODE>, <USEDEPT_CODE>, &
               <EXEC_ORDER_FLG>, <EXEC_DEPT_CODE>, <INSPAY_TYPE>, &
               <ADDPAY_RATE>, <ADDPAY_AMT>, <NHI_CODE_O>, &
              <NHI_CODE_E>, <NHI_CODE_I>, <CTRL_FLG>, &
              <CLPGROUP_CODE>, <ORDERSET_FLG>, <INDV_FLG>,& 
              <SUB_SYSTEM_CODE>, <RPTTYPE_CODE>, <DEV_CODE>,& 
             <OPTITEM_CODE>, <MR_CODE>, <DEGREE_CODE>, &
            <CIS_FLG>, <OPT_USER>, SYSDATE, &
            <OPT_TERM>, <ACTION_CODE>, &
             <ATC_FLG>, <OWN_PRICE2>, <OWN_PRICE3>, &
            <TUBE_TYPE>, <CAT1_TYPE>, <IS_REMARK>, &
           <ATC_FLG_I>, <REMARK_1>, <REMARK_2>, &
            <REGION_CODE>, <SYS_GRUG_CLASS>, <NOADDTION_FLG>, &
         <SYS_PHA_CLASS>)
saveFeeHistory.Debug=N 

//未匹配数据查询操作
selectNotMateSysFee.Type=TSQL
selectNotMateSysFee.SQL=SELECT 'N' AS FLG, A.ORDER_CODE, A.ORDER_DESC , B.DOSE_CODE,&
		 A.DESCRIPTION, A.SPECIFICATION,MAN_CODE,NHI_FEE_DESC,A.HYGIENE_TRADE_CODE, &
		OWN_PRICE, NHI_PRICE, &
		UNIT_CODE,&
		IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG,& 
		EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, &
		ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
		NHI_CODE_I, &
		CRT_FLG, SYS_GRUG_CLASS, NOADDTION_FLG, &
		SYS_PHA_CLASS &
		FROM SYS_FEE A LEFT JOIN PHA_BASE B ON A.ORDER_CODE=B.ORDER_CODE WHERE  &
		((NHI_CODE_O IS NULL OR NHI_CODE_O ='') AND (NHI_CODE_E IS NULL OR NHI_CODE_E ='') &
		AND  (NHI_CODE_I IS NULL OR NHI_CODE_I ='')) AND  A.CHARGE_HOSP_CODE IN ('M01','N01','L01','Z01')
selectNotMateSysFee.Debug=N

selectNotMateSysFee1.Type=TSQL
selectNotMateSysFee1.SQL=SELECT 'N' AS FLG, A.ORDER_CODE, A.ORDER_DESC ||CASE &
					 WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <>''&
					 THEN '(' || A.SPECIFICATION || ')'&
					 ELSE ''&
					  END AS ORDER_DESC, B.DOSE_CODE,&
		 A.DESCRIPTION, A.SPECIFICATION,MAN_CODE, &
		OWN_PRICE, NHI_PRICE, &
		UNIT_CODE,&
		IPD_FIT_FLG, HRM_FIT_FLG, DR_ORDER_FLG,& 
		EXEC_DEPT_CODE, INSPAY_TYPE, ADDPAY_RATE, &
		ADDPAY_AMT, NHI_CODE_O, NHI_CODE_E, &
		NHI_CODE_I, &
		CRT_FLG, SYS_GRUG_CLASS, NOADDTION_FLG, &
		SYS_PHA_CLASS &
		FROM SYS_FEE A LEFT JOIN PHA_BASE B ON A.ORDER_CODE=B.ORDER_CODE WHERE  &
		((NHI_CODE_O IS NULL OR NHI_CODE_O ='') OR (NHI_CODE_E IS NULL OR NHI_CODE_E ='') &
		OR  (NHI_CODE_I IS NULL OR NHI_CODE_I ='')) AND  A.CHARGE_HOSP_CODE IN ('M01','N01','L01','Z01')
selectNotMateSysFee1.Debug=N

//修改SYS_FEE 医保字段(修改后)

updateSysFee.Type=TSQL
updateSysFee.SQL=UPDATE SYS_FEE SET NHI_CODE_O=<NHI_CODE_O>,NHI_CODE_E=<NHI_CODE_E>,NHI_CODE_I=<NHI_CODE_I>,&
		    NHI_FEE_DESC=<NHI_FEE_DESC>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>, ADDPAY_RATE=<ADDPAY_RATE> &
                    WHERE  ORDER_CODE=<ORDER_CODE>
updateSysFee.Debug=N

//修改SYS_FEE_HISTORY 医保字段(修改后)

updateSysFeehistory.Type=TSQL
updateSysFeehistory.SQL=UPDATE SYS_FEE_HISTORY SET NHI_CODE_O=<NHI_CODE_O>,NHI_CODE_E=<NHI_CODE_E>,NHI_CODE_I=<NHI_CODE_I>,&
		    NHI_FEE_DESC=<NHI_FEE_DESC>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>, ADDPAY_RATE=<ADDPAY_RATE> &
                    WHERE ORDER_CODE=<ORDER_CODE> &
                    AND START_DATE=<UPDATESTARTDATE>
updateSysFeehistory.Debug=N