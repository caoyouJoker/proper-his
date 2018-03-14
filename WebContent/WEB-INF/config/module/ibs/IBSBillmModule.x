# 
#  Title:住院账务(主表)module
# 
#  Description:住院账务(主表)module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.27
#  version 1.0
#
Module.item=selectAllData;insertMdata;deleteMdata;selDataForCharge;selMaxSeq;updataData;selDataForBill;selBillData;upBillData;selAuditFee

//查询所有数据
selectAllData.Type=TSQL
selectAllData.SQL=SELECT BILL_NO,BILL_SEQ,CASE_NO,IPD_NO,MR_NO,&
			 BILL_DATE,REFUND_FLG,REFUND_BILL_NO,RECEIPT_NO,CHARGE_DATE,&
			 CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,BEGIN_DATE,END_DATE,&
			 DISCHARGE_FLG,DEPT_CODE,STATION_CODE,BED_NO,OWN_AMT,&
			 NHI_AMT,APPROVE_FLG,REDUCE_REASON,REDUCE_AMT,REDUCE_DATE,&
			 REDUCE_DEPT_CODE,REDUCE_RESPOND,AR_AMT,PAY_AR_AMT,CANDEBT_CODE,&
			 CANDEBT_PERSON,OPT_USER,OPT_DATE,OPT_TERM,REFUND_CODE,REFUND_DATE &
                    FROM IBS_BILLM &
                   WHERE REFUND_FLG <> 'Y' &
                     AND AR_AMT > 0 
selectAllData.Item=BILL_NO;BILL_SEQ;CASE_NO;MR_NO;IPD_NO;RECEIPT_NO
selectAllData.BILL_NO=BILL_NO=<BILL_NO>
selectAllData.BILL_SEQ=BILL_SEQ=<BILL_SEQ>
selectAllData.CASE_NO=CASE_NO=<CASE_NO>
selectAllData.MR_NO=MR_NO=<MR_NO>
selectAllData.IPD_NO=IPD_NO=<IPD_NO>
selectAllData.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selectAllData.Debug=N

//新增就诊序号,帐务序号,计帐日期,住院号,病案号,科室,病区,病床,资料来源,医嘱序号,医嘱子序号,帐单号码,操作人员,操作日期,操作终端
insertMdata.Type=TSQL
insertMdata.SQL=INSERT INTO IBS_BILLM &
			    (BILL_NO,BILL_SEQ,CASE_NO,IPD_NO,MR_NO,&
			    BILL_DATE,REFUND_FLG,REFUND_BILL_NO,RECEIPT_NO,CHARGE_DATE,&
			    CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,BEGIN_DATE,END_DATE,&
			    DISCHARGE_FLG,DEPT_CODE,STATION_CODE,BED_NO,OWN_AMT,&
			    NHI_AMT,APPROVE_FLG,REDUCE_REASON,REDUCE_AMT,REDUCE_DATE,&
			    REDUCE_DEPT_CODE,REDUCE_RESPOND,AR_AMT,PAY_AR_AMT,CANDEBT_CODE,&
			    CANDEBT_PERSON,OPT_USER,OPT_DATE,OPT_TERM,REFUND_CODE,REFUND_DATE,REGION_CODE) &
		     VALUES (<BILL_NO>,<BILL_SEQ>,<CASE_NO>,<IPD_NO>,<MR_NO>,&
		    	    <BILL_DATE>,<REFUND_FLG>,<REFUND_BILL_NO>,<RECEIPT_NO>,<CHARGE_DATE>,&
		    	    <CTZ1_CODE>,<CTZ2_CODE>,<CTZ3_CODE>,<BEGIN_DATE>,<END_DATE>,&
		    	    <DISCHARGE_FLG>,<DEPT_CODE>,<STATION_CODE>,<BED_NO>,<OWN_AMT>,&
		    	    <NHI_AMT>,<APPROVE_FLG>,<REDUCE_REASON>,<REDUCE_AMT>,<REDUCE_DATE>,&
		    	    <REDUCE_DEPT_CODE>,<REDUCE_RESPOND>,<AR_AMT>,<PAY_AR_AMT>,<CANDEBT_CODE>,&
		    	    <CANDEBT_PERSON>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REFUND_CODE>,<REFUND_DATE>,<REGION_CODE>)
insertMdata.Debug=N

//删除就诊序号,帐务序号,计帐日期,住院号,病案号,科室,病区,病床,资料来源,医嘱序号,医嘱子序号,帐单号码,操作人员,操作日期,操作终端
deleteMdata.Type=TSQL
deleteMdata.SQL=DELETE FROM IBS_BILLM WHERE BILL_NO = <BILL_NO> AND BILL_SEQ = <BILL_SEQ>
deleteMdata.Debug=N

//查询账单数据(缴费作业)
selDataForCharge.Type=TSQL
selDataForCharge.SQL=SELECT 'N' AS REDUCE_FLG,CASE_NO,BILL_NO,BEGIN_DATE,END_DATE,&
			    AR_AMT,PAY_AR_AMT,0 AS ALREADY_PAY,CTZ1_CODE,CTZ2_CODE,&
			    CTZ3_CODE,NHI_AMT,OWN_AMT,REDUCE_AMT,AR_AMT AS TOT_AMT,&
			    DISCHARGE_FLG,BILL_SEQ,APPROVE_FLG &
                       FROM IBS_BILLM &
                      WHERE REFUND_BILL_NO IS NULL &
                        AND ( (AR_AMT - PAY_AR_AMT - REDUCE_AMT) != 0 OR (AR_AMT = 0 AND RECEIPT_NO IS NULL)) &
                        AND (REFUND_FLG <> 'Y' OR REFUND_FLG IS NULL) &
                        AND RECEIPT_NO IS NULL &
                   ORDER BY CASE_NO,BILL_NO
selDataForCharge.Item=CASE_NO
selDataForCharge.CASE_NO=CASE_NO=<CASE_NO>
selDataForCharge.Debug=N

//查询最大账务序号
selMaxSeq.Type=TSQL
selMaxSeq.SQL=SELECT MAX(BILL_SEQ) AS BILL_SEQ  FROM IBS_BILLM
selMaxSeq.Item=CASE_NO;RECEIPT_NO
selMaxSeq.CASE_NO=CASE_NO=<CASE_NO>
selMaxSeq.BILL_NO=BILL_NO=<BILL_NO>
selMaxSeq.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selMaxSeq.Debug=N

//更新数据
updataData.Type=TSQL
updataData.SQL=UPDATE IBS_BILLM &
		  SET REFUND_FLG = <REFUND_FLG>,REFUND_BILL_NO=<REFUND_BILL_NO>,REFUND_CODE=<REFUND_CODE>,REFUND_DATE = SYSDATE &
		WHERE RECEIPT_NO = <RECEIPT_NO> 

updataData.Debug=N

//查询作废账单
selDataForBill.Type=TSQL
selDataForBill.SQL=SELECT '' AS PAY_SEL, APPROVE_FLG, BILL_NO, BEGIN_DATE, END_DATE,&
			  AR_AMT,PAY_AR_AMT, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE,&
			  OWN_AMT, NHI_AMT,REDUCE_AMT,DISCHARGE_FLG,MR_NO,IPD_NO,&
			  CASE_NO,RECEIPT_NO &
		     FROM IBS_BILLM &
		    WHERE (REFUND_FLG ='N' OR REFUND_FLG IS NULL) &
		      AND RECEIPT_NO IS NULL &
		      AND AR_AMT >=0
selDataForBill.Item=MR_NO;IPD_NO
selDataForBill.MR_NO=MR_NO=<MR_NO>
selDataForBill.IPD_NO=IPD_NO=<IPD_NO>
selDataForBill.Debug=N

//查询作废账单全部数据
selBillData.Type=TSQL
selBillData.SQL=SELECT BILL_NO,BILL_SEQ,CASE_NO,IPD_NO,MR_NO,&
			 BILL_DATE,REFUND_FLG,REFUND_BILL_NO,RECEIPT_NO,CHARGE_DATE,&
			 CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,BEGIN_DATE,END_DATE,&
			 DISCHARGE_FLG,DEPT_CODE,STATION_CODE,BED_NO,OWN_AMT,&
			 NHI_AMT,APPROVE_FLG,REDUCE_REASON,REDUCE_AMT,REDUCE_DATE,&
			 REDUCE_DEPT_CODE,REDUCE_RESPOND,AR_AMT,PAY_AR_AMT,CANDEBT_CODE,&
			 CANDEBT_PERSON,OPT_USER,OPT_DATE,OPT_TERM,REFUND_CODE,REFUND_DATE &
                    FROM IBS_BILLM &
		   WHERE (REFUND_FLG ='N' OR REFUND_FLG IS NULL) &
		     AND RECEIPT_NO IS NULL
selBillData.Item=BILL_NO;BILL_SEQ;CASE_NO;MR_NO;IPD_NO;RECEIPT_NO
selBillData.BILL_NO=BILL_NO=<BILL_NO>
selBillData.BILL_SEQ=BILL_SEQ=<BILL_SEQ>
selBillData.CASE_NO=CASE_NO=<CASE_NO>
selBillData.MR_NO=MR_NO=<MR_NO>
selBillData.IPD_NO=IPD_NO=<IPD_NO>
selBillData.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selBillData.Debug=N

//更新数据
upBillData.Type=TSQL
upBillData.SQL=UPDATE IBS_BILLM &
		  SET REFUND_FLG = <REFUND_FLG>,REFUND_BILL_NO=<REFUND_BILL_NO>,REFUND_CODE=<REFUND_CODE>,REFUND_DATE = SYSDATE &
		WHERE BILL_NO = <BILL_NO> 
upBillData.Item=CASE_NO;MR_NO;IPD_NO
upBillData.CASE_NO=CASE_NO=<CASE_NO>
upBillData.MR_NO=MR_NO=<MR_NO>
upBillData.IPD_NO=IPD_NO=<IPD_NO>
upBillData.Debug=N

//查询账单审核数据(账单审核)
//modify by caowl 20120911 删除approve_flg='N'
selAuditFee.Type=TSQL
selAuditFee.SQL=SELECT APPROVE_FLG, MR_NO, IPD_NO, BILL_NO, BEGIN_DATE, END_DATE, CTZ1_CODE,&
		       AR_AMT AS TOT_AMT, PAY_AR_AMT, 0 AS ALREADY_PAY, AR_AMT, REDUCE_AMT,CASE_NO &
		  FROM IBS_BILLM &
		 WHERE  &                  
		   //AND AR_AMT >= 0 &		  
                    RECEIPT_NO IS NULL &
		   AND (REFUND_FLG IS NULL OR REFUND_FLG = 'N') 
selAuditFee.Item=CASE_NO;MR_NO;IPD_NO
selAuditFee.CASE_NO=CASE_NO=<CASE_NO>
selAuditFee.MR_NO=MR_NO=<MR_NO>
selAuditFee.IPD_NO=IPD_NO=<IPD_NO>
selAuditFee.Debug=N





