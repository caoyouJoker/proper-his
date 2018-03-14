# 
#  Title:住院费用(主表)module
# 
#  Description:住院费用(主表)module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.27
#  version 1.0
#
Module.item=insertMdata;deleteMdata;selectdata;updateBillNO;selRegionStation;selStationPatInfo;existFee;selMaxCaseNoSeq;selCaseNoSeqForPatch;&
	    deleteOrderMPatch;selRegionStationCaseNo;selBillReturnM

//新增就诊序号,帐务序号,计帐日期,住院号,病案号,科室,病区,病床,资料来源,医嘱序号,医嘱子序号,帐单号码,操作人员,操作日期,操作终端
insertMdata.Type=TSQL
insertMdata.SQL=INSERT INTO IBS_ORDM &
			    (CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,&
			    DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,&
			    OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE) &
		    VALUES  (<CASE_NO>,<CASE_NO_SEQ>,<BILL_DATE>,<IPD_NO>,<MR_NO>,&
			    <DEPT_CODE>,<STATION_CODE>,<BED_NO>,<DATA_TYPE>,<BILL_NO>,&
			    <OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE>)
insertMdata.Debug=N

//删除就诊序号,帐务序号,计帐日期,住院号,病案号,科室,病区,病床,资料来源,医嘱序号,医嘱子序号,帐单号码,操作人员,操作日期,操作终端
deleteMdata.Type=TSQL
deleteMdata.SQL=DELETE FROM IBS_ORDM WHERE CASE_NO = <CASE_NO> AND CASE_NO_SEQ = <CASE_NO_SEQ>
deleteMdata.Debug=N

//查询未产生账单的费用档数据
selectdata.Type=TSQL
selectdata.SQL=SELECT A.CASE_NO,A.CASE_NO_SEQ,A.BILL_DATE,A.IPD_NO,A.MR_NO,&
		      A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.DATA_TYPE,A.BILL_NO,&
		      A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.CTZ1_CODE,B.CTZ2_CODE,&
		      B.CTZ3_CODE &
		 FROM IBS_ORDM A,ADM_INP B &
		WHERE BILL_NO IS NULL &
		  AND A.MR_NO = B.MR_NO &
                  AND A.CASE_NO = B.CASE_NO &
		  AND A.BILL_DATE <= <BILL_DATE> &
	     ORDER BY A.CASE_NO,A.CASE_NO_SEQ
selectdata.item=STATION_CODE;MR_NO;IPD_NO;BED_NO;CASE_NO
selectdata.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectdata.MR_NO=A.MR_NO=<MR_NO>
selectdata.IPD_NO=A.IPD_NO=<IPD_NO>
selectdata.BED_NO=A.BED_NO=<BED_NO>
selectdata.CASE_NO=A.CASE_NO=<CASE_NO>
selectdata.Debug=N

//更新账单号码
updateBillNO.Type=TSQL
updateBillNO.SQL=UPDATE IBS_ORDM &
		    SET BILL_NO=<BILL_NO> &
		  WHERE CASE_NO=<CASE_NO> &
		    AND BILL_DATE <= <BILL_DATE> &
		    AND BILL_NO IS NULL
updateBillNO.Debug=N

//查询所有病区,病区人数
selRegionStation.Type=TSQL
selRegionStation.SQL=SELECT B.STATION_DESC,COUNT (DISTINCT (CASE_NO)) AS COUNT,A.STATION_CODE &
                       FROM IBS_ORDM A,SYS_STATION B &
                      WHERE A.BILL_NO IS NULL &
                        AND A.STATION_CODE = B.STATION_CODE &
                   GROUP BY B.STATION_DESC,A.STATION_CODE
selRegionStation.item=STATION_CODE;MR_NO;IPD_NO;BED_NO;REGION_CODE
selRegionStation.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selRegionStation.MR_NO=A.MR_NO=<MR_NO>
selRegionStation.IPD_NO=A.IPD_NO=<IPD_NO>
selRegionStation.BED_NO=A.BED_NO=<BED_NO>
selRegionStation.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selRegionStation.Debug=N

selRegionStationCaseNo.Type=TSQL
selRegionStationCaseNo.SQL=SELECT DISTINCT CASE_NO  &
                           FROM IBS_ORDM A,SYS_STATION B  &
                      WHERE A.BILL_NO IS NULL  &
                        AND A.STATION_CODE = B.STATION_CODE 
selRegionStationCaseNo.item=STATION_CODE;MR_NO;IPD_NO;BED_NO;REGION_CODE
selRegionStationCaseNo.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selRegionStationCaseNo.MR_NO=A.MR_NO=<MR_NO>
selRegionStationCaseNo.IPD_NO=A.IPD_NO=<IPD_NO>
selRegionStationCaseNo.BED_NO=A.BED_NO=<BED_NO>
selRegionStationCaseNo.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selRegionStationCaseNo.Debug=N


//查询病区病患信息
selStationPatInfo.Type=TSQL
selStationPatInfo.SQL=SELECT A.BED_NO,B.PAT_NAME,A.MR_NO,A.IPD_NO,A.CASE_NO &
                        FROM IBS_ORDM A, SYS_PATINFO B,ADM_INP C &
                       WHERE A.MR_NO = B.MR_NO &
                         AND BILL_NO IS NULL &
                         AND A.CASE_NO = C.CASE_NO &
                         AND C.DS_DATE IS NULL &
                         AND C.CANCEL_FLG = 'N' &
                         AND A.BED_NO = C.BED_NO &
                    GROUP BY A.BED_NO,B.PAT_NAME,A.MR_NO,A.IPD_NO,A.CASE_NO
selStationPatInfo.item=STATION_CODE;MR_NO;IPD_NO;BED_NO
selStationPatInfo.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selStationPatInfo.MR_NO=A.MR_NO=<MR_NO>
selStationPatInfo.IPD_NO=A.IPD_NO=<IPD_NO>
selStationPatInfo.BED_NO=A.BED_NO=<BED_NO>
selStationPatInfo.Debug=N

//判断是否产生费用(For ADM)
existFee.Type=TSQL
existFee.SQL=SELECT CASE_NO &
	       FROM IBS_ORDM 
existFee.Item=CASE_NO
existFee.CASE_NO=CASE_NO=<CASE_NO>
existFee.Debug=N

//查询最大账务序号
selMaxCaseNoSeq.Type=TSQL
selMaxCaseNoSeq.SQL=SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ &
		      FROM IBS_ORDM
selMaxCaseNoSeq.Item=CASE_NO
selMaxCaseNoSeq.CASE_NO=CASE_NO=<CASE_NO>
selMaxCaseNoSeq.Debug=N

//查询账务序号(批次)
selCaseNoSeqForPatch.Type=TSQL
selCaseNoSeqForPatch.SQL=SELECT CASE_NO_SEQ &
			   FROM IBS_ORDM &
			  WHERE CASE_NO =<CASE_NO> &
			    AND BILL_DATE >= TO_DATE(<S_DATE>, 'yyyyMMddhh24miss') &
			    AND BILL_DATE <= TO_DATE(<E_DATE>, 'yyyyMMddhh24miss') &
			    AND DATA_TYPE = '0'
selCaseNoSeqForPatch.Debug=N

//删除费用明细档(批次)
deleteOrderMPatch.Type=TSQL
deleteOrderMPatch.SQL=DELETE IBS_ORDM &
		       WHERE CASE_NO = <CASE_NO> &
			 AND BILL_DATE >= TO_DATE(<S_DATE>, 'yyyyMMddhh24miss') &
			 AND BILL_DATE <= TO_DATE(<E_DATE>, 'yyyyMMddhh24miss') &
			 AND DATA_TYPE = '0'
deleteOrderMPatch.Debug=N

//查询作废账单数据(作废账单)
selBillReturnM.Type=TSQL
selBillReturnM.SQL=SELECT CASE_NO,CASE_NO_SEQ,BILL_DATE,IPD_NO,MR_NO,&
			  DEPT_CODE,STATION_CODE,BED_NO,DATA_TYPE,BILL_NO,&
			  OPT_USER,OPT_DATE,OPT_TERM &
		     FROM IBS_ORDM 
selBillReturnM.item=CASE_NO;MR_NO;IPD_NO;BILL_NO
selBillReturnM.CASE_NO=CASE_NO=<CASE_NO>
selBillReturnM.MR_NO=MR_NO=<MR_NO>
selBillReturnM.IPD_NO=IPD_NO=<IPD_NO>
selBillReturnM.BILL_NO=BILL_NO=<BILL_NO>
selBillReturnM.Debug=N





