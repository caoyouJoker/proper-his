# 
#  Title:门诊收据主档module
# 
#  Description:门诊收据主档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=insertReceipt;getReceipt;updateBackReceipt;getOneReceipt;updatePrintNO;account;accountAll;getSumAramt;getSumAramtAll;getReceiptCount;getAccountPrint;&
            getContractReceipt;getSumEKTFee;updateBackReceiptOne;getCountEKT;updateEKTPrintNO;&
            updateUnPrintNo;selectMedicalCardAmt;seletEktState;getOpbRecpCount;getOpbResetCount
//updataData;insertData;CheckCounter
//;selectData;insertData;deleteData
//添加一条新的票据
insertReceipt.Type=TSQL
insertReceipt.SQL=INSERT INTO BIL_OPB_RECP( &
                   CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
                   RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,&
                   PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04,&
                   CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,&
                   CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14,&
                   CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,&
                   CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24,&
                   CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,&
                   CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT,&
                   REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND,&
                   AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
                   PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
                   PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK,&
                   CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,ALIPAY,QE_PAY_TYPE) &
                   values(<CASE_NO>,<RECEIPT_NO>,<ADM_TYPE>,&
                   <REGION_CODE>,<MR_NO>,<RESET_RECEIPT_NO>,&
                   <PRINT_NO>,<BILL_DATE>,<CHARGE_DATE>,<PRINT_DATE>,&
                   <CHARGE01>,<CHARGE02>,<CHARGE03>,<CHARGE04>,<CHARGE05>,&
                   <CHARGE06>,<CHARGE07>,<CHARGE08>,<CHARGE09>,<CHARGE10>,&
                   <CHARGE11>,<CHARGE12>,<CHARGE13>,<CHARGE14>,<CHARGE15>,&
                   <CHARGE16>,<CHARGE17>,<CHARGE18>,<CHARGE19>,<CHARGE20>,&
                   <CHARGE21>,<CHARGE22>,<CHARGE23>,<CHARGE24>,<CHARGE25>,&
                   <CHARGE26>,<CHARGE27>,<CHARGE28>,<CHARGE29>,<CHARGE30>,&
                   <TOT_AMT>,<REDUCE_REASON>,<REDUCE_AMT>,<REDUCE_DATE>,&
                   <REDUCE_DEPT_CODE>,<REDUCE_RESPOND>,<AR_AMT>,<PAY_CASH>,&
                   <PAY_MEDICAL_CARD>,<PAY_BANK_CARD>,<PAY_INS_CARD>,&
                   <PAY_CHECK>,<PAY_DEBIT>,<PAY_BILPAY>,<PAY_INS>,&
                   <PAY_OTHER1>,<PAY_OTHER2>,<PAY_REMARK>,<CASHIER_CODE>,<OPT_USER>,&
                   <OPT_DATE>,<OPT_TERM>,<ALIPAY>,<QE_PAY_TYPE>)
insertReceipt.Debug=N

//查询病患的票据列表
getReceipt.Type=TSQL
getReceipt.SQL=SELECT B.RECP_TYPE,A.CASE_NO,B.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO,&
                      A.PRINT_NO,B.PRINT_DATE AS BILL_DATE,&
                      SUM(CHARGE01) AS CHARGE01,SUM(CHARGE02) AS CHARGE02,SUM(CHARGE03) AS CHARGE03,SUM(CHARGE04) AS CHARGE04,&
                      SUM(CHARGE05) AS CHARGE05,SUM(CHARGE06) AS CHARGE06,SUM(CHARGE07) AS CHARGE07,SUM(CHARGE08) AS CHARGE08,SUM(CHARGE09) AS CHARGE09,&
                      SUM(CHARGE10) AS CHARGE10,SUM(CHARGE11) AS CHARGE11,SUM(CHARGE12) AS CHARGE12,SUM(CHARGE13) AS CHARGE13,SUM(CHARGE14) AS CHARGE14,&
                      SUM(CHARGE15) AS CHARGE15,SUM(CHARGE16) AS CHARGE16,SUM(CHARGE17) AS CHARGE17,SUM(CHARGE18) AS CHARGE18,SUM(CHARGE19) AS CHARGE19,&
                      SUM(CHARGE20) AS CHARGE20,SUM(CHARGE21) AS CHARGE21,SUM(CHARGE22) AS CHARGE22,SUM(CHARGE23) AS CHARGE23,SUM(CHARGE24) AS CHARGE24,&
                      SUM(CHARGE25) AS CHARGE25,SUM(CHARGE26) AS CHARGE26,SUM(CHARGE27) AS CHARGE27,SUM(CHARGE28) AS CHARGE28,SUM(CHARGE29) AS CHARGE29,&
                      SUM(CHARGE30) AS CHARGE30,SUM(TOT_AMT) AS TOT_AMT,SUM(REDUCE_AMT) AS REDUCE_AMT,&
                      SUM(A.AR_AMT) AS AR_AMT,SUM(A.PAY_CASH) AS PAY_CASH ,SUM(A.PAY_MEDICAL_CARD) AS PAY_MEDICAL_CARD,SUM(A.PAY_BANK_CARD) AS PAY_BANK_CARD,&
                      SUM(A.PAY_INS_CARD) AS PAY_INS_CARD,SUM(A.PAY_CHECK) AS PAY_CHECK, SUM(A.PAY_DEBIT) AS PAY_DEBIT, SUM(A.PAY_BILPAY) AS PAY_BILPAY,&
                      SUM(A.PAY_INS) AS PAY_INS, SUM(A.PAY_OTHER1) AS PAY_OTHER1, SUM(A.PAY_OTHER2) AS PAY_OTHER2,SUM(A.ALIPAY) AS ALIPAY, A.PAY_REMARK,&
                      A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PRINT_USER,A.QE_PAY_TYPE &
                 FROM BIL_OPB_RECP A,BIL_INVRCP B &
                WHERE A.PRINT_NO=B.INV_NO AND A.CASE_NO=<CASE_NO> AND B.RECP_TYPE='OPB' &
                      AND A.RESET_RECEIPT_NO IS NULL AND A.PRINT_NO IS NOT NULL  AND A.TOT_AMT>=0 &
                      AND A.RECEIPT_NO NOT IN (SELECT RESET_RECEIPT_NO FROM BIL_OPB_RECP WHERE CASE_NO =<CASE_NO> AND RESET_RECEIPT_NO IS NOT NULL AND PRINT_NO IS NOT NULL ) &
                      GROUP BY B.RECP_TYPE,A.CASE_NO,B.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO,&
                      A.RESET_RECEIPT_NO,A.PRINT_NO,B.PRINT_DATE , A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM ,A.RESET_RECEIPT_NO,A.PRINT_NO,B.PRINT_USER,A.PAY_REMARK,A.QE_PAY_TYPE &
             ORDER BY B.RECEIPT_NO
getReceipt.Debug=N


//查询病患的票据列表：记账查找，没有执行结算操作的数据退费
//==========================pangben 20110823
getContractReceipt.Type=TSQL
getContractReceipt.SQL=SELECT A.CASE_NO,A.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO,&
                      A.RESET_RECEIPT_NO,A.PRINT_NO,BILL_DATE,A.CHARGE_DATE,&
                      A.PRINT_DATE,A.CHARGE01,A.CHARGE02,A.CHARGE03,A.CHARGE04,&
                      A.CHARGE05,A.CHARGE06,A.CHARGE07,A.CHARGE08,A.CHARGE09,&
                      A.CHARGE10,A.CHARGE11,A.CHARGE12,A.CHARGE13,A.CHARGE14,&
                      A.CHARGE15,A.CHARGE16,A.CHARGE17,A.CHARGE18,A.CHARGE19,&
                      A.CHARGE20,A.CHARGE21,A.CHARGE22,A.CHARGE23,A.CHARGE24,&
                      A.CHARGE25,A.CHARGE26,A.CHARGE27,A.CHARGE28,A.CHARGE29,&
                      A.CHARGE30,A.TOT_AMT,A.REDUCE_REASON,A.REDUCE_AMT,&
                      A.REDUCE_DATE,A.REDUCE_DEPT_CODE,A.REDUCE_RESPOND,&
                      A.AR_AMT,A.PAY_CASH,A.PAY_MEDICAL_CARD,A.PAY_BANK_CARD,&
                      A.PAY_INS_CARD,A.PAY_CHECK,A.PAY_DEBIT,A.PAY_BILPAY,&
                      A.PAY_INS,A.PAY_OTHER1,A.PAY_OTHER2,A.PAY_REMARK,&
                      A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.ALIPAY &
                 FROM BIL_OPB_RECP A ,BIL_CONTRACT_RECODE B &
                WHERE A.CASE_NO = <CASE_NO> &
               	      AND  A.RECEIPT_NO=B.RECEIPT_NO(+) &
               	      AND B.RECEIPT_TYPE='OPB' &
                      AND A.RESET_RECEIPT_NO IS NULL AND A.PRINT_NO IS NULL  &
             ORDER BY A.RECEIPT_NO
getContractReceipt.Debug=N

//更新退费票据
updateBackReceipt.Type=TSQL
updateBackReceipt.SQL=UPDATE BIL_OPB_RECP &
                         SET RESET_RECEIPT_NO=<RESET_RECEIPT_NO>, &
                             OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
                       WHERE RECEIPT_NO=<RECEIPT_NO> &
                             AND CASE_NO=<CASE_NO>
updateBackReceipt.Debug=N


//查询一条票据：现金打票、医疗卡打票,医生修改医嘱时使用
getOneReceipt.Type=TSQL
getOneReceipt.SQL=SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
                      RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,&
                      PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04,&
                      CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,&
                      CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14,&
                      CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,&
                      CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24,&
                      CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,&
                      CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT,&
                      REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND,&
                      AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
                      PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
                      PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK,&
                      CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,ALIPAY,QE_PAY_TYPE &
                 FROM BIL_OPB_RECP &
                WHERE RECEIPT_NO = <RECEIPT_NO>
getOneReceipt.item=CASE_NO
getOneReceipt.CASE_NO=CASE_NO=<CASE_NO>                
getOneReceipt.Debug=N



//更新打印票号：现金
updatePrintNO.Type=TSQL
updatePrintNO.SQL=UPDATE BIL_OPB_RECP &
                     SET PRINT_NO=<PRINT_NO>,PRINT_DATE=SYSDATE, &
                         OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                   WHERE RECEIPT_NO=<RECEIPT_NO> &
                     AND CASE_NO=<CASE_NO>
updatePrintNO.Debug=N

//更新打印票号：医疗卡
updateEKTPrintNO.Type=TSQL
updateEKTPrintNO.SQL=UPDATE BIL_OPB_RECP &
                        SET PRINT_NO=<NEWPRINT_NO>,PRINT_DATE=SYSDATE, &
                            OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                      WHERE PRINT_NO=<PRINT_NO> &
                            AND CASE_NO=<CASE_NO>
updateEKTPrintNO.Debug=N

//account日结
account.Type=TSQL
account.SQL=UPDATE BIL_OPB_RECP SET ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_FLG='Y',ACCOUNT_DATE=SYSDATE &
            WHERE ADM_TYPE=<ADM_TYPE> &
              AND CASHIER_CODE=<CASHIER_CODE> &
              AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
              AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
              //add by wanglong 201224 未打票不参与日结
              AND PRINT_NO IS NOT NULL
account.Debug=N

//accountAll日结o e h
accountAll.Type=TSQL
accountAll.SQL=UPDATE BIL_OPB_RECP SET ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_FLG='Y',ACCOUNT_DATE=SYSDATE &
                WHERE AND CASHIER_CODE=<CASHIER_CODE> &
                  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
                  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                  //add by wanglong 201224 未打票不参与日结
		  AND PRINT_NO IS NOT NULL
accountAll.Debug=N

//得到日结金额
getSumAramt.Type=TSQL
getSumAramt.SQL=SELECT SUM(AR_AMT) AR_AMT FROM BIL_OPB_RECP &
		WHERE CASHIER_CODE=<CASHIER_CODE> &
		  AND ADM_TYPE = <ADM_TYPE> &
		  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
		  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                  //add by wanglong 201210 未打票不参与日结
		  AND PRINT_NO IS NOT NULL
getSumAramt.Debug=N

//得到日结金额(O,E,H)
getSumAramtAll.Type=TSQL
getSumAramtAll.SQL=SELECT SUM(AR_AMT) AR_AMT FROM BIL_OPB_RECP &
		WHERE CASHIER_CODE=<CASHIER_CODE> &
		  AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
		  AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)
getSumAramtAll.Debug=N

//得到日结票据数
getReceiptCount.Type=TSQL
getReceiptCount.SQL=SELECT COUNT(RECEIPT_NO) COUNT AR_AMT FROM BIL_OPB_RECP &
		WHERE ADM_TYPE=<ADM_TYPE> &
                      AND CASHIER_CODE=<CASHIER_CODE> &
                      AND BILL_DATE<TO_DATE(<BILL_DATE>,'YYYYMMDDHH24MISS') &
                      AND ACCOUNT_FLG<>'Y'
getReceiptCount.Debug=N


//得到日结号内的收据
getAccountPrint.Type=TSQL
getAccountPrint.SQL=SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO,&
                      RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,&
                      PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04,&
                      CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09,&
                      CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14,&
                      CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,&
                      CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24,&
                      CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,&
                      CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT,&
                      REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND,&
                      AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD,&
                      PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,&
                      PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK,&
                      CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,ALIPAY &
                 FROM BIL_OPB_RECP &
                WHERE ACCOUNT_SEQ IN (<ACCOUNT_SEQ>)
getAccountPrint.Debug=N

//得到医疗卡、绿色通道金额
//============pangben 20111024
getSumEKTFee.Type=TSQL
getSumEKTFee.SQL=SELECT SUM(CHARGE01) AS CHARGE01,SUM(CHARGE02) AS CHARGE02,SUM(CHARGE03) AS CHARGE03,SUM(CHARGE04) AS CHARGE04,&
                      SUM(CHARGE05) AS CHARGE05,SUM(CHARGE06) AS CHARGE06,SUM(CHARGE07) AS CHARGE07,SUM(CHARGE08) AS CHARGE08,SUM(CHARGE09) AS CHARGE09,&
                      SUM(CHARGE10) AS CHARGE10,SUM(CHARGE11) AS CHARGE11,SUM(CHARGE12) AS CHARGE12,SUM(CHARGE13) AS CHARGE13,SUM(CHARGE14) AS CHARGE14,&
                      SUM(CHARGE15) AS CHARGE15,SUM(CHARGE16) AS CHARGE16,SUM(CHARGE17) AS CHARGE17,SUM(CHARGE18) AS CHARGE18,SUM(CHARGE19) AS CHARGE19,&
                      SUM(CHARGE20) AS CHARGE20,SUM(CHARGE21) AS CHARGE21,SUM(CHARGE22) AS CHARGE22,SUM(CHARGE23) AS CHARGE23,SUM(CHARGE24) AS CHARGE24,&
                      SUM(CHARGE25) AS CHARGE25,SUM(CHARGE26) AS CHARGE26,SUM(CHARGE27) AS CHARGE27,SUM(CHARGE28) AS CHARGE28,SUM(CHARGE29) AS CHARGE29,&
                      SUM(CHARGE30) AS CHARGE30,SUM(TOT_AMT) AS TOT_AMT,SUM(REDUCE_AMT) AS REDUCE_AMT,&
                      SUM(AR_AMT) AS AR_AMT,SUM(PAY_CASH) AS PAY_CASH,SUM(PAY_MEDICAL_CARD) AS PAY_MEDICAL_CARD,SUM(PAY_BANK_CARD) AS PAY_BANK_CARD,&
                      SUM(PAY_INS_CARD) AS PAY_INS_CARD,SUM(PAY_CHECK) AS PAY_CHECK,SUM(PAY_DEBIT) AS PAY_DEBIT,SUM(PAY_BILPAY) AS PAY_BILPAY,&
                      SUM(PAY_INS) AS PAY_INS,SUM(PAY_OTHER1) AS PAY_OTHER1,SUM(PAY_OTHER2) AS PAY_OTHER2,SUM(ALIPAY) AS ALIPAY &
                 FROM BIL_OPB_RECP &
                WHERE CASE_NO =<CASE_NO> AND (PRINT_NO IS NULL OR PRINT_NO ='') AND (RESET_RECEIPT_NO  IS NULL OR RESET_RECEIPT_NO ='')
getSumEKTFee.item=REGION_CODE
getSumEKTFee.REGION_CODE=REGION_CODE=<REGION_CODE>
getSumEKTFee.Debug=N

getCountEKT.Type=TSQL
getCountEKT.SQL=SELECT COUNT(*) AS COUNTSUM  FROM BIL_OPB_RECP &
                WHERE CASE_NO =<CASE_NO> AND (PRINT_NO IS NULL OR PRINT_NO ='') AND (RESET_RECEIPT_NO  IS NULL OR RESET_RECEIPT_NO ='')
getCountEKT.Debug=N

//更新退费票据====根据打票票号退费
updateBackReceiptOne.Type=TSQL
updateBackReceiptOne.SQL=UPDATE BIL_OPB_RECP &
                         SET RESET_RECEIPT_NO=<RESET_RECEIPT_NO>, &
                             OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
                       WHERE PRINT_NO=<PRINT_NO> &
                             AND CASE_NO=<CASE_NO>
updateBackReceiptOne.Debug=N

//医疗卡退费清将收据执行作废状态

//============pangben 20111028
updateUnPrintNo.Type=TSQL
updateUnPrintNo.SQL=UPDATE BIL_OPB_RECP &
                         SET RESET_RECEIPT_NO = <RESET_RECEIPT_NO>, &
                             OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                       WHERE PRINT_NO=<PRINT_NO> AND (RESET_RECEIPT_NO  IS NULL OR RESET_RECEIPT_NO ='') &
                             AND CASE_NO=<CASE_NO>
updateUnPrintNo.Debug=N

//获得医疗卡金额 
selectMedicalCardAmt.Type=TSQL
selectMedicalCardAmt.SQL=SELECT TOT_AMT,PAY_MEDICAL_CARD,PAY_INS_CARD FROM BIL_OPB_RECP &
                       WHERE RECEIPT_NO=<RECEIPT_NO> AND CASE_NO=<CASE_NO>
selectMedicalCardAmt.Debug=N

//通过PRINT_NO 查询此次就诊是否是医疗卡操作
seletEktState.Type=TSQL
seletEktState.SQL=SELECT B.CASE_NO FROM EKT_TRADE A,BIL_OPB_RECP B &
                       WHERE A.CASE_NO=B.CASE_NO(+) AND B.PRINT_NO=<PRINT_NO> 
seletEktState.Debug=N

//得到日结票据张数
getOpbRecpCount.Type=TSQL
getOpbRecpCount.SQL=SELECT COUNT(PRINT_NO) COUNT FROM BIL_OPB_RECP &
		             WHERE ADM_TYPE = <ADM_TYPE> &
                       AND CASHIER_CODE=<CASHIER_CODE> &
                       AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                       AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N') &
                       AND (RESET_RECEIPT_NO IS NULL OR RESET_RECEIPT_NO = '') &
                       //add by wanglong 201210 未打票不参与日结
                       AND PRINT_NO IS NOT NULL
getOpbRecpCount.Debug=N

//得到日结人员作废张数
getOpbResetCount.Type=TSQL
getOpbResetCount.SQL=SELECT COUNT(PRINT_NO) AS COUNT FROM BIL_OPB_RECP &
		              WHERE CASHIER_CODE=<CASHIER_CODE> &
                        AND ADM_TYPE = <ADM_TYPE> &
                        AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                        AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                        AND AR_AMT < 0
getOpbResetCount.Debug=N
