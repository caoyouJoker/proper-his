#
# <p>Title:医嘱替换时程操作 </p>
#
# <p>Description: </p>
#
# <p>Copyright: Copyright (c) 2014</p>
#
# <p>Company: </p>
#
# @author pangb 2014-9-11
# @version 4.0
#
Module.item=selectAdmInp;queryIbsOrder;updateIbsOrddBySchdCode

//查询住院病患表数据
selectAdmInp.Type=TSQL
selectAdmInp.SQL=SELECT A.CASE_NO,CASE WHEN A.DS_DATE IS NULL THEN '在院' ELSE '出院' END ADM_STATUS ,A.MR_NO,B.PAT_NAME,A.IN_DATE,A.CLNCPATH_CODE &
                   FROM ADM_INP A ,SYS_PATINFO B &
                   WHERE A.MR_NO=B.MR_NO AND (A.CANCEL_FLG IS NULL OR A.CANCEL_FLG='N') &
                   AND A.IN_DATE BETWEEN TO_DATE(<START_IN_DATE>,'YYYYMMDDHH24MISS') &
                   AND TO_DATE(<END_IN_DATE>,'YYYYMMDDHH24MISS') AND A.CLNCPATH_CODE IS NOT NULL
selectAdmInp.item=DEPT_CODE;STATUS;STATION_CODE;MR_NO
selectAdmInp.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectAdmInp.STATUS=A.DS_DATE =<STATUS>
selectAdmInp.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectAdmInp.MR_NO=A.MR_NO=<MR_NO>
selectAdmInp.Debug=n


//查询计费医嘱数据
queryIbsOrder.Type=TSQL
queryIbsOrder.SQL=SELECT 'N' FLG,A.CLNCPATH_CODE,A.SCHD_CODE,B.ORDER_DESC,B.DESCRIPTION,A.MEDI_QTY,A.MEDI_UNIT,&
                   A.FREQ_CODE,A.DOSAGE_QTY,A.TOT_AMT,A.BILL_DATE,A.OPT_USER,A.CASE_NO_SEQ,A.SEQ_NO,A.CASE_NO &
                   FROM IBS_ORDD A ,SYS_FEE B &
                   WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CASE_NO=<CASE_NO>
queryIbsOrder.item=CLNCPATH_CODE;SCHD_CODE;EXE_DEPT_CODE;OPT_USER;DEPT_CODE;STATION_CODE;DR_CODE;BILL_DATE
queryIbsOrder.CLNCPATH_CODE=A.CLNCPATH_CODE=<CLNCPATH_CODE>
queryIbsOrder.SCHD_CODE=A.SCHD_CODE=<SCHD_CODE>
queryIbsOrder.EXE_DEPT_CODE=A.EXE_DEPT_CODE=<EXE_DEPT_CODE>
queryIbsOrder.OPT_USER=A.OPT_USER=<OPT_USER>
queryIbsOrder.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryIbsOrder.STATION_CODE=A.STATION_CODE=<STATION_CODE>
queryIbsOrder.DR_CODE=A.DR_CODE=<DR_CODE>
queryIbsOrder.BILL_DATE=A.BILL_DATE BETWEEN TO_DATE(<START_BILL_DATE>,'YYYYMMDDHH24MISS') AND TO_DATE(<END_BILL_DATE>,'YYYYMMDDHH24MISS')
queryIbsOrder.Debug=n

//更新收费医嘱路径代码和时程
updateIbsOrddBySchdCode.Type=TSQL
updateIbsOrddBySchdCode.SQL=UPDATE IBS_ORDD SET VERSION=<VERSION>,OPT_USER=<OPT_USER>,&
                                  OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> 
updateIbsOrddBySchdCode.Debug=N