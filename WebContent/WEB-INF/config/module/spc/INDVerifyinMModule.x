   #
   # Title:验收入库主档
   #
   # Description:验收入库主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=queryPlanNo;createVerifyinM;queryVerifyinM;updateVerifyinM;deleteVerifyinM;queryDoneVerifyin;&
            queryDoneSPCVerifyin;deleteVerifyinMAcnt;updateVerifyinMAcnt;createVerifyinMAcnt;queryVerifyinMAcnt

//查询验收入库主档中的采购计划单号
queryPlanNo.Type=TSQL
queryPlanNo.SQL=SELECT PLAN_NO FROM IND_VERIFYINM WHERE PLAN_NO = <PLAN_NO>
queryPlanNo.Debug=N

//新增验收主项
createVerifyinM.Type=TSQL
createVerifyinM.SQL=INSERT INTO IND_VERIFYINM( &
		      VERIFYIN_NO,VERIFYIN_DATE,VERIFYIN_USER,ORG_CODE,SUP_CODE, &
		      CHECK_USER,CHECK_DATE,DESCRIPTION,REASON_CHN_DESC,PLAN_NO, &
		      OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,DRUG_CATEGORY) &
		    VALUES( &
		      <VERIFYIN_NO>,<VERIFYIN_DATE>,<VERIFYIN_USER>,<ORG_CODE>,<SUP_CODE>, &
		      <CHECK_USER>,<CHECK_DATE>,<DESCRIPTION>,<REASON_CHN_DESC>,<PLAN_NO>, &
		      <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<REGION_CODE>,<DRUG_CATEGORY>) 
createVerifyinM.Debug=N

//新增验收主项 wanglong add 20150202
createVerifyinMAcnt.Type=TSQL
createVerifyinMAcnt.SQL=INSERT INTO SPC_VERIFYINM( &
              VERIFYIN_NO,VERIFYIN_DATE,VERIFYIN_USER,ORG_CODE,SUP_CODE, &
              CHECK_USER,CHECK_DATE,DESCRIPTION,REASON_CHN_DESC,PLAN_NO, &
              OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,DRUG_CATEGORY) &
            VALUES( &
              <VERIFYIN_NO>,<VERIFYIN_DATE>,<VERIFYIN_USER>,<ORG_CODE>,<SUP_CODE>, &
              <CHECK_USER>,<CHECK_DATE>,<DESCRIPTION>,<REASON_CHN_DESC>,<PLAN_NO>, &
              <OPT_USER>,<OPT_DATE>,<OPT_TERM>,<REGION_CODE>,<DRUG_CATEGORY>) 
createVerifyinMAcnt.Debug=N

//查询订购主档
queryVerifyinM.Type=TSQL
queryVerifyinM.SQL=SELECT VERIFYIN_NO, VERIFYIN_DATE, VERIFYIN_USER, ORG_CODE, SUP_CODE, &
			  CHECK_USER, CHECK_DATE, DESCRIPTION, REASON_CHN_DESC, BILL_DATE, &
			  BILLPRINT_FLG, PLAN_NO, OPT_USER, OPT_DATE, OPT_TERM,DRUG_CATEGORY &
		     FROM IND_VERIFYINM ORDER BY VERIFYIN_NO DESC
queryVerifyinM.ITEM=ORG_CODE;SUP_CODE;VERIFYIN_NO;START_DATE;END_DATE;REGION_CODE;DRUG_CATEGORY
queryVerifyinM.ORG_CODE=ORG_CODE=<ORG_CODE>
queryVerifyinM.SUP_CODE=SUP_CODE=<SUP_CODE>
queryVerifyinM.VERIFYIN_NO=VERIFYIN_NO=<VERIFYIN_NO>
queryVerifyinM.REGION_CODE=REGION_CODE=<REGION_CODE>
queryVerifyinM.START_DATE=VERIFYIN_DATE>=<START_DATE>
queryVerifyinM.END_DATE=VERIFYIN_DATE<=<END_DATE>
queryVerifyinM.DRUG_CATEGORY=DRUG_CATEGORY=<DRUG_CATEGORY>
queryVerifyinM.Debug=N

//查询订购主档 wanglong add 20150202
queryVerifyinMAcnt.Type=TSQL
queryVerifyinMAcnt.SQL=SELECT VERIFYIN_NO, VERIFYIN_DATE, VERIFYIN_USER, ORG_CODE, SUP_CODE, &
              CHECK_USER, CHECK_DATE, DESCRIPTION, REASON_CHN_DESC, BILL_DATE, &
              BILLPRINT_FLG, PLAN_NO, OPT_USER, OPT_DATE, OPT_TERM,DRUG_CATEGORY &
             FROM SPC_VERIFYINM ORDER BY VERIFYIN_NO DESC
queryVerifyinMAcnt.ITEM=ORG_CODE;SUP_CODE;VERIFYIN_NO;START_DATE;END_DATE;REGION_CODE;DRUG_CATEGORY
queryVerifyinMAcnt.ORG_CODE=ORG_CODE=<ORG_CODE>
queryVerifyinMAcnt.SUP_CODE=SUP_CODE=<SUP_CODE>
queryVerifyinMAcnt.VERIFYIN_NO=VERIFYIN_NO=<VERIFYIN_NO>
queryVerifyinMAcnt.REGION_CODE=REGION_CODE=<REGION_CODE>
queryVerifyinMAcnt.START_DATE=VERIFYIN_DATE>=<START_DATE>
queryVerifyinMAcnt.END_DATE=VERIFYIN_DATE<=<END_DATE>
queryVerifyinMAcnt.DRUG_CATEGORY=DRUG_CATEGORY=<DRUG_CATEGORY>
queryVerifyinMAcnt.Debug=N

//更新验收主项
updateVerifyinM.Type=TSQL
updateVerifyinM.SQL=UPDATE IND_VERIFYINM SET &
		      ORG_CODE=<ORG_CODE>, &
		      SUP_CODE=<SUP_CODE>, &
		      CHECK_USER=<CHECK_USER>, &
		      CHECK_DATE=<CHECK_DATE>, &
		      DESCRIPTION=<DESCRIPTION>, &
		      REASON_CHN_DESC=<REASON_CHN_DESC>, &
		      PLAN_NO=<PLAN_NO>, &
		      OPT_USER=<OPT_USER>, &
		      OPT_DATE=<OPT_DATE>, &
		      OPT_TERM=<OPT_TERM> &
		   WHERE VERIFYIN_NO=<VERIFYIN_NO>
updateVerifyinM.Debug=N

//更新验收主项 wanglong add 20150202
updateVerifyinMAcnt.Type=TSQL
updateVerifyinMAcnt.SQL=UPDATE SPC_VERIFYINM SET &
              ORG_CODE=<ORG_CODE>, &
              SUP_CODE=<SUP_CODE>, &
              CHECK_USER=<CHECK_USER>, &
              CHECK_DATE=<CHECK_DATE>, &
              DESCRIPTION=<DESCRIPTION>, &
              REASON_CHN_DESC=<REASON_CHN_DESC>, &
              PLAN_NO=<PLAN_NO>, &
              OPT_USER=<OPT_USER>, &
              OPT_DATE=<OPT_DATE>, &
              OPT_TERM=<OPT_TERM>, &
              VERIFYIN_DATE=<VERIFYIN_DATE> &
           WHERE VERIFYIN_NO=<VERIFYIN_NO>
updateVerifyinMAcnt.Debug=N

//删除订购主档
deleteVerifyinM.Type=TSQL
deleteVerifyinM.SQL=DELETE FROM IND_VERIFYINM WHERE VERIFYIN_NO=<VERIFYIN_NO>
deleteVerifyinM.Debug=N

//删除订购主档 wanglong add 20150202
deleteVerifyinMAcnt.Type=TSQL
deleteVerifyinMAcnt.SQL=DELETE FROM SPC_VERIFYINM WHERE VERIFYIN_NO=<VERIFYIN_NO>
deleteVerifyinMAcnt.Debug=N

//查询所有验收单的信息
queryDoneVerifyin.Type=TSQL
queryDoneVerifyin.SQL=SELECT A.VERIFYIN_NO, A.VERIFYIN_DATE, B.ORDER_CODE, B.VERIFYIN_QTY, B.GIFT_QTY, &
			       B.BILL_UNIT, B.VERIFYIN_PRICE, B.ACTUAL_QTY, A.SUP_CODE , 'N' AS SELECT_FLG, &
			       C.ORDER_DESC, A.PLAN_NO, B.SEQ_NO, C.PHA_TYPE, C.RETAIL_PRICE, &
			       C.STOCK_PRICE &
			  FROM IND_VERIFYINM A, IND_VERIFYIND B, PHA_BASE C &
			  WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
			    AND B.ORDER_CODE = C.ORDER_CODE 
queryDoneVerifyin.ITEM=PURORDER_NO;SUP_CODE;ORDER_CODE;SEQ_NO
queryDoneVerifyin.PURORDER_NO=A.VERIFYIN_NO =<VERIFYIN_NO>
queryDoneVerifyin.SUP_CODE=A.SUP_CODE =<SUP_CODE>
queryDoneVerifyin.ORDER_CODE=B.ORDER_CODE =<ORDER_CODE>
queryDoneVerifyin.SEQ_NO=B.SEQ_NO =<SEQ_NO>
queryDoneVerifyin.Debug=N

//查询所有验收单的信息 wanglong add 20150202
queryDoneSPCVerifyin.Type=TSQL
queryDoneSPCVerifyin.SQL=SELECT A.VERIFYIN_NO, A.VERIFYIN_DATE, B.ORDER_CODE, B.VERIFYIN_QTY, B.GIFT_QTY, &
                   B.BILL_UNIT, B.VERIFYIN_PRICE, B.ACTUAL_QTY, A.SUP_CODE , 'N' AS SELECT_FLG, &
                   C.ORDER_DESC, A.PLAN_NO, B.SEQ_NO, C.PHA_TYPE, C.RETAIL_PRICE, &
                   C.STOCK_PRICE &
              FROM SPC_VERIFYINM A, SPC_VERIFYIND B, PHA_BASE C &
              WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
                AND B.ORDER_CODE = C.ORDER_CODE 
queryDoneSPCVerifyin.ITEM=PURORDER_NO;SUP_CODE;ORDER_CODE;SEQ_NO
queryDoneSPCVerifyin.PURORDER_NO=A.VERIFYIN_NO =<VERIFYIN_NO>
queryDoneSPCVerifyin.SUP_CODE=A.SUP_CODE =<SUP_CODE>
queryDoneSPCVerifyin.ORDER_CODE=B.ORDER_CODE =<ORDER_CODE>
queryDoneSPCVerifyin.SEQ_NO=B.SEQ_NO =<SEQ_NO>
queryDoneSPCVerifyin.Debug=N
