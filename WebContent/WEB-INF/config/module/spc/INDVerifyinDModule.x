   #
   # Title:验收入库明细
   #
   # Description:验收入库明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=queryVerifyinD;queryVerifyinDoneD;updateVerifyinDReg;getQueryVerifyinBuyMaster;getQueryVerifyinBuyDetail;getQueryVerifyinGiftMaster;getQueryVerifyinGiftDetail;&
            querySPCVerifyinD;querySPCVerifyinDoneD;updateSPCVerifyinDReg;getQueryVerifyinBuyMasterAcnt;getQueryVerifyinBuyDetailAcnt;getQueryVerifyinGiftMasterAcnt;getQueryVerifyinGiftDetailAcnt

//查询验收入库明细
queryVerifyinD.Type=TSQL
queryVerifyinD.SQL=SELECT VERIFYIN_NO, SEQ_NO, PURORDER_NO, PURSEQ_NO, ORDER_CODE, &
		          VERIFYIN_QTY, GIFT_QTY, BILL_UNIT, VERIFYIN_PRICE, INVOICE_AMT, &
		          INVOICE_NO, INVOICE_DATE, BATCH_NO, VALID_DATE, REASON_CHN_DESC, &
		          QUALITY_DEDUCT_AMT, RETAIL_PRICE, ACTUAL_QTY, UPDATE_FLG, OPT_USER, &
		          OPT_DATE, OPT_TERM &
		     FROM IND_VERIFYIND
queryVerifyinD.ITEM=VERIFYIN_NO;SEQ_NO;PURORDER_NO;PURSEQ_NO;ORDER_CODE
queryVerifyinD.VERIFYIN_NO=VERIFYIN_NO=<VERIFYIN_NO>
queryVerifyinD.SEQ_NO=SEQ_NO=<SEQ_NO>
queryVerifyinD.PURORDER_NO=PURORDER_NO=<PURORDER_NO>
queryVerifyinD.PURSEQ_NO=PURSEQ_NO=<PURSEQ_NO>
queryVerifyinD.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
queryVerifyinD.Debug=N

//查询验收入库明细 wanglong add 20150202
querySPCVerifyinD.Type=TSQL
querySPCVerifyinD.SQL=SELECT VERIFYIN_NO, SEQ_NO, PURORDER_NO, PURSEQ_NO, ORDER_CODE, &
                  VERIFYIN_QTY, GIFT_QTY, BILL_UNIT, VERIFYIN_PRICE, INVOICE_AMT, &
                  INVOICE_NO, INVOICE_DATE, BATCH_NO, VALID_DATE, REASON_CHN_DESC, &
                  QUALITY_DEDUCT_AMT, RETAIL_PRICE, ACTUAL_QTY, UPDATE_FLG, OPT_USER, &
                  OPT_DATE, OPT_TERM &
             FROM SPC_VERIFYIND
querySPCVerifyinD.ITEM=VERIFYIN_NO;SEQ_NO;PURORDER_NO;PURSEQ_NO;ORDER_CODE
querySPCVerifyinD.VERIFYIN_NO=VERIFYIN_NO=<VERIFYIN_NO>
querySPCVerifyinD.SEQ_NO=SEQ_NO=<SEQ_NO>
querySPCVerifyinD.PURORDER_NO=PURORDER_NO=<PURORDER_NO>
querySPCVerifyinD.PURSEQ_NO=PURSEQ_NO=<PURSEQ_NO>
querySPCVerifyinD.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
querySPCVerifyinD.Debug=N

//验收未退货明细表-luhai 2012-1-11 modify 加入batch_seq verifyInPrice
//queryVerifyinDoneD.Type=TSQL
//queryVerifyinDoneD.SQL=SELECT 'N' AS SELECT_FLG, B.VERIFYIN_NO, B.VERIFYIN_DATE, A.ORDER_CODE, C.SPECIFICATION, &
//       			      A.BATCH_NO, A.VALID_DATE, A.VERIFYIN_QTY, A.GIFT_QTY, A.BILL_UNIT, &
//       			      A.VERIFYIN_PRICE, A.ACTUAL_QTY, A.UPDATE_FLG, C.ORDER_DESC , C.OWN_PRICE * D.DOSAGE_QTY AS RETAIL_PRICE,  &
//       			      A.SEQ_NO &
//  		        FROM IND_VERIFYIND A, IND_VERIFYINM B, SYS_FEE C, PHA_TRANSUNIT D &
// 		        WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
//   			  AND A.ORDER_CODE = C.ORDER_CODE &
//   			  AND (A.UPDATE_FLG = '1' OR A.UPDATE_FLG = '3') &
//   			  AND A.ORDER_CODE = D.ORDER_CODE &
//                 	  AND C.ORDER_CODE = D.ORDER_CODE &
//   			  AND B.ORG_CODE=<ORG_CODE> &
//   			  AND B.SUP_CODE=<SUP_CODE>
//queryVerifyinDoneD.ITEM=VERIFYIN_NO;ORDER_CODE;START_CHECK_DATE;END_CHECK_DATE
//queryVerifyinDoneD.VERIFYIN_NO=B.VERIFYIN_NO=<VERIFYIN_NO>
//queryVerifyinDoneD.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
//queryVerifyinDoneD.START_CHECK_DATE=B.CHECK_DATE>=<START_CHECK_DATE>
//queryVerifyinDoneD.END_CHECK_DATE=B.CHECK_DATE<=<END_CHECK_DATE>
//queryVerifyinDoneD.Debug=N
//begin update by guoyi 2012-5-17 删除重复的 verifyInPrice
//queryVerifyinDoneD.Type=TSQL
//queryVerifyinDoneD.SQL=SELECT 'N' AS SELECT_FLG, B.VERIFYIN_NO, B.VERIFYIN_DATE, A.ORDER_CODE, C.SPECIFICATION, &
//       			      A.BATCH_NO, A.VALID_DATE, A.VERIFYIN_QTY, A.GIFT_QTY, A.BILL_UNIT, &
//       			      A.VERIFYIN_PRICE, A.ACTUAL_QTY, A.UPDATE_FLG, C.ORDER_DESC , C.OWN_PRICE * D.DOSAGE_QTY AS RETAIL_PRICE,  &
//       			      A.SEQ_NO,A.BATCH_SEQ,A.VERIFYIN_PRICE,A.INVOICE_NO,A.INVOICE_DATE &
//  		        FROM IND_VERIFYIND A, IND_VERIFYINM B, SYS_FEE C, PHA_TRANSUNIT D &
// 		        WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
//   			  AND A.ORDER_CODE = C.ORDER_CODE &
//   			  AND (A.UPDATE_FLG = '1' OR A.UPDATE_FLG = '3') &
//   			  AND A.ORDER_CODE = D.ORDER_CODE &
//                 	  AND C.ORDER_CODE = D.ORDER_CODE &
//   			  AND B.ORG_CODE=<ORG_CODE> &
//   			  AND B.SUP_CODE=<SUP_CODE>
//queryVerifyinDoneD.ITEM=VERIFYIN_NO;ORDER_CODE;START_CHECK_DATE;END_CHECK_DATE
//queryVerifyinDoneD.VERIFYIN_NO=B.VERIFYIN_NO=<VERIFYIN_NO>
//queryVerifyinDoneD.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
//queryVerifyinDoneD.START_CHECK_DATE=B.CHECK_DATE>=<START_CHECK_DATE>
//queryVerifyinDoneD.END_CHECK_DATE=B.CHECK_DATE<=<END_CHECK_DATE>
//queryVerifyinDoneD.Debug=N
//验收未退货明细表- add SUP_ORDER_CODE
queryVerifyinDoneD.Type=TSQL
queryVerifyinDoneD.SQL=SELECT 'N' AS SELECT_FLG, B.VERIFYIN_NO, B.VERIFYIN_DATE, A.ORDER_CODE, C.SPECIFICATION, &
       			      A.BATCH_NO, A.VALID_DATE, A.VERIFYIN_QTY, A.GIFT_QTY, A.BILL_UNIT, &
       			      A.ACTUAL_QTY, A.UPDATE_FLG, C.ORDER_DESC , C.OWN_PRICE * D.DOSAGE_QTY AS RETAIL_PRICE,  &
       			      A.SEQ_NO,A.BATCH_SEQ,A.VERIFYIN_PRICE,A.INVOICE_NO,A.INVOICE_DATE,A.SUP_ORDER_CODE &
  		        FROM IND_VERIFYIND A, IND_VERIFYINM B, SYS_FEE C, PHA_TRANSUNIT D &
 		        WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
   			  AND A.ORDER_CODE = C.ORDER_CODE &
   			  AND (A.UPDATE_FLG = '1' OR A.UPDATE_FLG = '3') &
   			  AND A.ORDER_CODE = D.ORDER_CODE &
                 	  AND C.ORDER_CODE = D.ORDER_CODE &
   			  AND B.ORG_CODE=<ORG_CODE> &
   			  AND B.SUP_CODE=<SUP_CODE>
queryVerifyinDoneD.ITEM=VERIFYIN_NO;ORDER_CODE;START_CHECK_DATE;END_CHECK_DATE
queryVerifyinDoneD.VERIFYIN_NO=B.VERIFYIN_NO=<VERIFYIN_NO>
queryVerifyinDoneD.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
queryVerifyinDoneD.START_CHECK_DATE=B.CHECK_DATE>=<START_CHECK_DATE>
queryVerifyinDoneD.END_CHECK_DATE=B.CHECK_DATE<=<END_CHECK_DATE>
queryVerifyinDoneD.Debug=N
//end update by guoyi 2012-5-17 删除重复的 verifyInPrice

//验收未退货明细表  wanglong add 20150202
querySPCVerifyinDoneD.Type=TSQL
querySPCVerifyinDoneD.SQL=SELECT 'N' AS SELECT_FLG, B.VERIFYIN_NO, B.VERIFYIN_DATE, A.ORDER_CODE, C.SPECIFICATION, &
                      A.BATCH_NO, A.VALID_DATE, A.VERIFYIN_QTY, A.GIFT_QTY, A.BILL_UNIT, &
                      A.ACTUAL_QTY, A.UPDATE_FLG, C.ORDER_DESC , C.OWN_PRICE * D.DOSAGE_QTY AS RETAIL_PRICE,  &
                      A.SEQ_NO,A.BATCH_SEQ,A.VERIFYIN_PRICE,A.INVOICE_NO,A.INVOICE_DATE,A.SUP_ORDER_CODE &
                FROM SPC_VERIFYIND A, SPC_VERIFYINM B, SYS_FEE C, PHA_TRANSUNIT D &
                WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
              AND A.ORDER_CODE = C.ORDER_CODE &
              AND (A.UPDATE_FLG = '1' OR A.UPDATE_FLG = '3') &
              AND A.ORDER_CODE = D.ORDER_CODE &
                      AND C.ORDER_CODE = D.ORDER_CODE &
              AND B.ORG_CODE=<ORG_CODE> &
              AND B.SUP_CODE=<SUP_CODE>
querySPCVerifyinDoneD.ITEM=VERIFYIN_NO;ORDER_CODE;START_CHECK_DATE;END_CHECK_DATE
querySPCVerifyinDoneD.VERIFYIN_NO=B.VERIFYIN_NO=<VERIFYIN_NO>
querySPCVerifyinDoneD.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
querySPCVerifyinDoneD.START_CHECK_DATE=B.CHECK_DATE>=<START_CHECK_DATE>
querySPCVerifyinDoneD.END_CHECK_DATE=B.CHECK_DATE<=<END_CHECK_DATE>
querySPCVerifyinDoneD.Debug=N

//累计退货数更新
updateVerifyinDReg.Type=TSQL
updateVerifyinDReg.SQL=UPDATE IND_VERIFYIND &
		         SET  ACTUAL_QTY=ACTUAL_QTY+<QTY>, &
		              OPT_USER=<OPT_USER>, &
			      OPT_DATE=<OPT_DATE>, &
			      OPT_TERM=<OPT_TERM> &
		        WHERE VERIFYIN_NO=<VERIFYIN_NO> &
		          AND SEQ_NO=<SEQ_NO>
updateVerifyinDReg.Debug=N

//累计退货数更新 wanglong add 20150202
updateSPCVerifyinDReg.Type=TSQL
updateSPCVerifyinDReg.SQL=UPDATE SPC_VERIFYIND &
                 SET  ACTUAL_QTY=ACTUAL_QTY+<QTY>, &
                      OPT_USER=<OPT_USER>, &
                  OPT_DATE=<OPT_DATE>, &
                  OPT_TERM=<OPT_TERM> &
                WHERE VERIFYIN_NO=<VERIFYIN_NO> &
                  AND SEQ_NO=<SEQ_NO>
updateSPCVerifyinDReg.Debug=N

//begin update by lirui 2012-5-30 加入生产厂商字段  MAN_CODE
//药品验收入库统计(购入汇总)
getQueryVerifyinBuyMaster.Type=TSQL
getQueryVerifyinBuyMaster.SQL=SELECT B.ORDER_CODE,D.ORDER_DESC AS ORDER_DESC,D.SPECIFICATION, E.UNIT_CHN_DESC, &                         
							  SUM(B.VERIFYIN_QTY) AS QTY,&
							  B.VERIFYIN_PRICE, &
							  SUM(B.VERIFYIN_QTY)*B.VERIFYIN_PRICE AS VER_AMT, &
							  B.RETAIL_PRICE AS OWN_PRICE, &
							  SUM(B.VERIFYIN_QTY)*B.RETAIL_PRICE AS OWN_AMT, &
							  SUM(B.VERIFYIN_QTY)*B.RETAIL_PRICE-SUM(B.VERIFYIN_QTY)*B.VERIFYIN_PRICE AS DIFF_AMT, &
							  D.MAN_CODE AS MAN_NAME,F.SUP_CHN_DESC AS SUP_CODE,B.SUP_ORDER_CODE  &
							  FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE D, SYS_UNIT E,SYS_SUPPLIER F &
							  WHERE A.CHECK_DATE BETWEEN TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
							  AND A.VERIFYIN_NO = B.VERIFYIN_NO AND B.ORDER_CODE = D.ORDER_CODE &
							  AND A.SUP_CODE = F.SUP_CODE &
							  AND B.BILL_UNIT = E.UNIT_CODE  &      
							  AND B.UPDATE_FLG IN ('1', '3') &
							  AND A.ORG_CODE = <ORG_CODE> &
							  GROUP BY D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, &
                              B.RETAIL_PRICE, B.ORDER_CODE,D.MAN_CODE,F.SUP_CHN_DESC,B.SUP_ORDER_CODE  &
                              ORDER BY B.ORDER_CODE
getQueryVerifyinBuyMaster.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinBuyMaster.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinBuyMaster.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinBuyMaster.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinBuyMaster.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinBuyMaster.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinBuyMaster.Debug=N  

//药品验收入库统计(购入汇总) wanglong add 20150202
getQueryVerifyinBuyMasterAcnt.Type=TSQL
getQueryVerifyinBuyMasterAcnt.SQL=SELECT D.ORDER_DESC AS ORDER_DESC,D.SPECIFICATION, E.UNIT_CHN_DESC, &                         
                              SUM(B.VERIFYIN_QTY) AS QTY,&
                              B.VERIFYIN_PRICE, &
                              SUM(B.VERIFYIN_QTY)*B.VERIFYIN_PRICE AS VER_AMT, &
                              B.RETAIL_PRICE AS OWN_PRICE, &
                              SUM(B.VERIFYIN_QTY)*B.RETAIL_PRICE AS OWN_AMT, &
                              SUM(B.VERIFYIN_QTY)*B.RETAIL_PRICE-SUM(B.VERIFYIN_QTY)*B.VERIFYIN_PRICE AS DIFF_AMT, &
                              D.MAN_CODE AS MAN_NAME &
                              FROM SPC_VERIFYINM A, SPC_VERIFYIND B, SYS_FEE D, SYS_UNIT E &
                              WHERE A.CHECK_DATE BETWEEN TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
                              AND A.VERIFYIN_NO = B.VERIFYIN_NO AND B.ORDER_CODE = D.ORDER_CODE &
                              AND B.BILL_UNIT = E.UNIT_CODE  &
                              AND B.UPDATE_FLG IN ('1', '3') &
                              AND A.ORG_CODE = <ORG_CODE> &
                              GROUP BY D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, &
                              B.RETAIL_PRICE, B.ORDER_CODE,D.MAN_CODE &
                              ORDER BY B.ORDER_CODE
getQueryVerifyinBuyMasterAcnt.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinBuyMasterAcnt.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinBuyMasterAcnt.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinBuyMasterAcnt.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinBuyMasterAcnt.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinBuyMasterAcnt.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinBuyMasterAcnt.Debug=N  

//药品验收入库统计(购入明细)
getQueryVerifyinBuyDetail.Type=TSQL
getQueryVerifyinBuyDetail.SQL=SELECT A.VERIFYIN_NO, C.SUP_CHN_DESC, A.CHECK_DATE AS VERIFYIN_DATE, &
							  D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, &
							  B.VERIFYIN_QTY, &
							  B.VERIFYIN_PRICE, &
							  B.VERIFYIN_QTY*B.VERIFYIN_PRICE AS VER_AMT, &
							  B.RETAIL_PRICE AS OWN_PRICE, &
							  B.VERIFYIN_QTY*B.RETAIL_PRICE AS OWN_AMT, &
							  B.VERIFYIN_QTY*B.RETAIL_PRICE-B.VERIFYIN_QTY*B.VERIFYIN_PRICE AS DIFF_AMT, &
							  B.INVOICE_NO, B.INVOICE_DATE, B.BATCH_NO, B.VALID_DATE, &
							  H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,B.MAN_CODE, &
							  B.VERIFYIN_QTY AS VERIFYINQTY, &
							  B.GIFT_QTY,B.BILL_UNIT,B.VERIFYIN_PRICE AS VERIFYINPRICE,B.INVOICE_AMT,B.RETAIL_PRICE, &
							  B.REASON_CHN_DESC,B.QUALITY_DEDUCT_AMT,B.PURORDER_NO,B.PURSEQ_NO &
							 FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_SUPPLIER C, SYS_FEE D, SYS_UNIT E, PHA_BASE G, &
							 PHA_DOSE H ,SYS_PHAROUTE I ,SYS_PHAFREQ J &
							 WHERE A.CHECK_DATE BETWEEN TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS') &
							   AND A.VERIFYIN_NO = B.VERIFYIN_NO &
							   AND A.SUP_CODE = C.SUP_CODE &
							   AND B.ORDER_CODE = D.ORDER_CODE &
							   AND B.BILL_UNIT = E.UNIT_CODE &
							   AND B.UPDATE_FLG IN ('1', '3') &
							   AND B.ORDER_CODE = G.ORDER_CODE &
							   AND D.ORDER_CODE = G.ORDER_CODE &
							   AND A.ORG_CODE = <ORG_CODE> &
							   AND G.DOSE_CODE=H.DOSE_CODE(+) &
							   AND G.ROUTE_CODE=I.ROUTE_CODE(+) &
							   AND G.FREQ_CODE=J.FREQ_CODE(+) & 
							   ORDER BY A.VERIFYIN_NO 
getQueryVerifyinBuyDetail.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinBuyDetail.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinBuyDetail.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinBuyDetail.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinBuyDetail.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinBuyDetail.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinBuyDetail.Debug=N
//end update by lirui 2012-5-30  加入生产厂商字段  MAN_CODE

//药品验收入库统计(购入明细) wanglong add 20150202
getQueryVerifyinBuyDetailAcnt.Type=TSQL
getQueryVerifyinBuyDetailAcnt.SQL=SELECT A.VERIFYIN_NO, C.SUP_CHN_DESC, A.CHECK_DATE AS VERIFYIN_DATE, &
                              D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, &
                              B.VERIFYIN_QTY, &
                              B.VERIFYIN_PRICE, &
                              B.VERIFYIN_QTY*B.VERIFYIN_PRICE AS VER_AMT, &
                              B.RETAIL_PRICE AS OWN_PRICE, &
                              B.VERIFYIN_QTY*B.RETAIL_PRICE AS OWN_AMT, &
                              B.VERIFYIN_QTY*B.RETAIL_PRICE-B.VERIFYIN_QTY*B.VERIFYIN_PRICE AS DIFF_AMT, &
                              B.INVOICE_NO, B.INVOICE_DATE, B.BATCH_NO, B.VALID_DATE, &
                              H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,B.MAN_CODE, &
                              B.VERIFYIN_QTY AS VERIFYINQTY, &
                              B.GIFT_QTY,B.BILL_UNIT,B.VERIFYIN_PRICE AS VERIFYINPRICE,B.INVOICE_AMT,B.RETAIL_PRICE, &
                              B.REASON_CHN_DESC,B.QUALITY_DEDUCT_AMT,B.PURORDER_NO,B.PURSEQ_NO &
                             FROM SPC_VERIFYINM A, SPC_VERIFYIND B, SYS_SUPPLIER C, SYS_FEE D, SYS_UNIT E, PHA_BASE G, &
                             PHA_DOSE H ,SYS_PHAROUTE I ,SYS_PHAFREQ J &
                             WHERE A.CHECK_DATE BETWEEN TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS') &
                               AND A.VERIFYIN_NO = B.VERIFYIN_NO &
                               AND A.SUP_CODE = C.SUP_CODE &
                               AND B.ORDER_CODE = D.ORDER_CODE &
                               AND B.BILL_UNIT = E.UNIT_CODE &
                               AND B.UPDATE_FLG IN ('1', '3') &
                               AND B.ORDER_CODE = G.ORDER_CODE &
                               AND D.ORDER_CODE = G.ORDER_CODE &
                               AND A.ORG_CODE = <ORG_CODE> &
                               AND G.DOSE_CODE=H.DOSE_CODE(+) &
                               AND G.ROUTE_CODE=I.ROUTE_CODE(+) &
                               AND G.FREQ_CODE=J.FREQ_CODE(+) & 
                               ORDER BY A.VERIFYIN_NO 
getQueryVerifyinBuyDetailAcnt.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinBuyDetailAcnt.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinBuyDetailAcnt.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinBuyDetailAcnt.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinBuyDetailAcnt.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinBuyDetailAcnt.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinBuyDetailAcnt.Debug=N

//药品验收入库统计(赠药汇总)
getQueryVerifyinGiftMaster.Type=TSQL
getQueryVerifyinGiftMaster.SQL=SELECT  D.ORDER_DESC AS ORDER_DESC,D.SPECIFICATION, E.UNIT_CHN_DESC, &                         
							  SUM(B.GIFT_QTY) AS QTY,&
							  B.VERIFYIN_PRICE, &
							  SUM(B.GIFT_QTY)*B.VERIFYIN_PRICE AS VER_AMT, &
							  B.RETAIL_PRICE AS OWN_PRICE, &
							  SUM(B.GIFT_QTY)*B.RETAIL_PRICE AS OWN_AMT, &
							  SUM(B.GIFT_QTY)*B.RETAIL_PRICE-SUM(B.GIFT_QTY)*B.VERIFYIN_PRICE AS DIFF_AMT, &
							  D.MAN_CODE AS MAN_NAME,F.SUP_CHN_DESC AS SUP_CODE,B.SUP_ORDER_CODE  &
							 FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_FEE D, SYS_UNIT E,SYS_SUPPLIER F &
							 WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
							   AND B.ORDER_CODE = D.ORDER_CODE &  
							   AND B.BILL_UNIT = E.UNIT_CODE &  
							   AND A.SUP_CODE = F.SUP_CODE &  
							   AND B.UPDATE_FLG IN ('1', '3') &
							   AND A.ORG_CODE = <ORG_CODE> &
							   AND A.CHECK_DATE BETWEEN  TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
							   AND B.GIFT_QTY > 0 &
							   GROUP BY D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, &
                               B.RETAIL_PRICE, B.ORDER_CODE,D.MAN_CODE,F.SUP_CHN_DES,B.SUP_ORDER_CODE  &
                               ORDER BY B.ORDER_CODE
getQueryVerifyinGiftMaster.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinGiftMaster.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinGiftMaster.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinGiftMaster.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinGiftMaster.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinGiftMaster.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinGiftMaster.Debug=N

//药品验收入库统计(赠药汇总) wanglong add 20150202
getQueryVerifyinGiftMasterAcnt.Type=TSQL
getQueryVerifyinGiftMasterAcnt.SQL=SELECT  D.ORDER_DESC AS ORDER_DESC,D.SPECIFICATION, E.UNIT_CHN_DESC, &                         
                              SUM(B.GIFT_QTY) AS QTY,&
                              B.VERIFYIN_PRICE, &
                              SUM(B.GIFT_QTY)*B.VERIFYIN_PRICE AS VER_AMT, &
                              B.RETAIL_PRICE AS OWN_PRICE, &
                              SUM(B.GIFT_QTY)*B.RETAIL_PRICE AS OWN_AMT, &
                              SUM(B.GIFT_QTY)*B.RETAIL_PRICE-SUM(B.GIFT_QTY)*B.VERIFYIN_PRICE AS DIFF_AMT, &
                              D.MAN_CODE AS MAN_NAME  &
                             FROM SPC_VERIFYINM A, SPC_VERIFYIND B, SYS_FEE D, SYS_UNIT E &
                             WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
                               AND B.ORDER_CODE = D.ORDER_CODE &
                               AND B.BILL_UNIT = E.UNIT_CODE &
                               AND B.UPDATE_FLG IN ('1', '3') &
                               AND A.ORG_CODE = <ORG_CODE> &
                               AND A.CHECK_DATE BETWEEN  TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
                               AND B.GIFT_QTY > 0 &
                               GROUP BY D.GOODS_DESC, D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, &
                               B.RETAIL_PRICE, B.ORDER_CODE,D.MAN_CODE &
                               ORDER BY B.ORDER_CODE
getQueryVerifyinGiftMasterAcnt.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinGiftMasterAcnt.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinGiftMasterAcnt.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinGiftMasterAcnt.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinGiftMasterAcnt.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinGiftMasterAcnt.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinGiftMasterAcnt.Debug=N

//药品验收入库统计(赠药明细)
getQueryVerifyinGiftDetail.Type=TSQL
getQueryVerifyinGiftDetail.SQL=SELECT A.VERIFYIN_NO, C.SUP_CHN_DESC, A.CHECK_DATE AS VERIFYIN_DATE, &
							  D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, &
							  B.GIFT_QTY AS VERIFYIN_QTY, &
							  B.VERIFYIN_PRICE, &
							  B.GIFT_QTY*B.VERIFYIN_PRICE AS VER_AMT, &
							  B.RETAIL_PRICE AS OWN_PRICE, &
							  B.GIFT_QTY*B.RETAIL_PRICE AS OWN_AMT, &
							  B.GIFT_QTY*B.RETAIL_PRICE-B.GIFT_QTY*B.VERIFYIN_PRICE AS DIFF_AMT, &
							  B.INVOICE_NO, B.INVOICE_DATE, B.BATCH_NO, B.VALID_DATE, &
							  H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,B.MAN_CODE, &
							  B.VERIFYIN_QTY AS VERIFYINQTY, &
							  B.GIFT_QTY,B.BILL_UNIT,B.VERIFYIN_PRICE AS VERIFYINPRICE,B.INVOICE_AMT,B.RETAIL_PRICE, &
							  B.REASON_CHN_DESC,B.QUALITY_DEDUCT_AMT,B.PURORDER_NO,B.PURSEQ_NO &
							 FROM IND_VERIFYINM A, IND_VERIFYIND B, SYS_SUPPLIER C, SYS_FEE D, SYS_UNIT E, PHA_BASE G, &
							 PHA_DOSE H ,SYS_PHAROUTE I ,SYS_PHAFREQ J &
							 WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
							   AND A.SUP_CODE = C.SUP_CODE &
							   AND B.ORDER_CODE = D.ORDER_CODE &
							   AND B.BILL_UNIT = E.UNIT_CODE &
							   AND B.UPDATE_FLG IN ('1', '3') &
							   AND B.GIFT_QTY > 0 &
							   AND B.ORDER_CODE = G.ORDER_CODE &
							   AND A.ORG_CODE = <ORG_CODE> &
							   AND A.CHECK_DATE BETWEEN  TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
							   AND G.DOSE_CODE=H.DOSE_CODE(+) &
							   AND G.ROUTE_CODE=I.ROUTE_CODE(+) &
							   AND G.FREQ_CODE=J.FREQ_CODE(+) & 
							   ORDER BY A.VERIFYIN_NO
getQueryVerifyinGiftDetail.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinGiftDetail.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinGiftDetail.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinGiftDetail.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinGiftDetail.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinGiftDetail.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinGiftDetail.Debug=N

//药品验收入库统计(赠药明细) wanglong add 201500202
getQueryVerifyinGiftDetailAcnt.Type=TSQL
getQueryVerifyinGiftDetailAcnt.SQL=SELECT A.VERIFYIN_NO, C.SUP_CHN_DESC, A.CHECK_DATE AS VERIFYIN_DATE, &
                              D.ORDER_DESC, D.SPECIFICATION, E.UNIT_CHN_DESC, &
                              B.GIFT_QTY AS VERIFYIN_QTY, &
                              B.VERIFYIN_PRICE, &
                              B.GIFT_QTY*B.VERIFYIN_PRICE AS VER_AMT, &
                              B.RETAIL_PRICE AS OWN_PRICE, &
                              B.GIFT_QTY*B.RETAIL_PRICE AS OWN_AMT, &
                              B.GIFT_QTY*B.RETAIL_PRICE-B.GIFT_QTY*B.VERIFYIN_PRICE AS DIFF_AMT, &
                              B.INVOICE_NO, B.INVOICE_DATE, B.BATCH_NO, B.VALID_DATE, &
                              H.DOSE_CHN_DESC,I.ROUTE_CHN_DESC,J.FREQ_CHN_DESC,B.MAN_CODE, &
                              B.VERIFYIN_QTY AS VERIFYINQTY, &
                              B.GIFT_QTY,B.BILL_UNIT,B.VERIFYIN_PRICE AS VERIFYINPRICE,B.INVOICE_AMT,B.RETAIL_PRICE, &
                              B.REASON_CHN_DESC,B.QUALITY_DEDUCT_AMT,B.PURORDER_NO,B.PURSEQ_NO &
                             FROM SPC_VERIFYINM A, SPC_VERIFYIND B, SYS_SUPPLIER C, SYS_FEE D, SYS_UNIT E, PHA_BASE G, &
                             PHA_DOSE H ,SYS_PHAROUTE I ,SYS_PHAFREQ J &
                             WHERE A.VERIFYIN_NO = B.VERIFYIN_NO &
                               AND A.SUP_CODE = C.SUP_CODE &
                               AND B.ORDER_CODE = D.ORDER_CODE &
                               AND B.BILL_UNIT = E.UNIT_CODE &
                               AND B.UPDATE_FLG IN ('1', '3') &
                               AND B.GIFT_QTY > 0 &
                               AND B.ORDER_CODE = G.ORDER_CODE &
                               AND A.ORG_CODE = <ORG_CODE> &
                               AND A.CHECK_DATE BETWEEN  TO_DATE(<START_DATE>,'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(<END_DATE>,'YYYY-MM-DD HH24:MI:SS')  &
                               AND G.DOSE_CODE=H.DOSE_CODE(+) &
                               AND G.ROUTE_CODE=I.ROUTE_CODE(+) &
                               AND G.FREQ_CODE=J.FREQ_CODE(+) & 
                               ORDER BY A.VERIFYIN_NO
getQueryVerifyinGiftDetailAcnt.ITEM=SUP_CODE;TYPE_CODE;ORDER_CODE;VERIFYIN_NO;INVOICE_NO
getQueryVerifyinGiftDetailAcnt.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getQueryVerifyinGiftDetailAcnt.TYPE_CODE=G.TYPE_CODE=<TYPE_CODE>
getQueryVerifyinGiftDetailAcnt.ORDER_CODE=B.ORDER_CODE=<ORDER_CODE>
getQueryVerifyinGiftDetailAcnt.VERIFYIN_NO=A.VERIFYIN_NO=<VERIFYIN_NO>
getQueryVerifyinGiftDetailAcnt.INVOICE_NO=B.INVOICE_NO=<INVOICE_NO>
getQueryVerifyinGiftDetailAcnt.Debug=N
