##################################################
# <p>Title:转科记录 </p>
#
# <p>Description:转科记录 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##################################################
Module.item=insertAll;selectData;deleteData;updateData;getTranHospFormro;updateADM;getTranDeptData;insertLogForCancel;getOldTranDeptData;selectTrandept
 
//插入转科记录
insertAll.Type=TSQL
insertAll.SQL=INSERT INTO ADM_TRANS_LOG (CASE_NO, IN_DATE, MR_NO, IPD_NO, OUT_DEPT_CODE,& 
  		OUT_STATION_CODE,OUT_DATE,IN_DEPT_CODE, IN_STATION_CODE, OPT_USER, OPT_DATE,OPT_TERM )&  
  		VALUES (<CASE_NO>,TO_CHAR(SYSDATE,'YYYYMMDD HH24MISS'),<MR_NO>,<IPD_NO>,<OUT_DEPT_CODE>,&
 		<OUT_STATION_CODE>,<OUT_DATE>,<IN_DEPT_CODE>,<IN_STATION_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>) 
insertAll.Debug=N

//查询转科记录
selectData.Type=TSQL
selectData.SQL=SELECT ROWNUM,A.CASE_NO,A.MR_NO,A.IPD_NO,A.OUT_DEPT_CODE,A.OUT_STATION_CODE,A.IN_DEPT_CODE,A.IN_STATION_CODE,A.OUT_DATE,&
               A.PSF_KIND,A.DEPT_ATTR_FLG,A.IN_DATE,B.PAT_NAME &
               FROM ADM_TRANS_LOG A,SYS_PATINFO B &
               WHERE A.MR_NO=B.MR_NO  ORDER BY A.IN_DATE
selectData.item=CASE_NO;MR_NO;IPD_NO;IN_DATE
selectData.CASE_NO=A.CASE_NO=<CASE_NO>
selectData.MR_NO=A.MR_NO=<MR_NO>
selectData.IPD_NO=A.IPD_NO=<IPD_NO>
selectData.IN_DATE=TO_DATE(A.IN_DATE,'yyyyMMddhh24miss') BETWEEN TO_DATE(<START_DATE>,'yyyyMMddhh24miss') AND TO_DATE(<END_DATE>,'yyyyMMddhh24miss')
selectData.Debug=N
//查询最新转科科室的数据
getTranDeptData.Type=TSQL
getTranDeptData.SQL=SELECT *  &
                    FROM ADM_TRANS_LOG &
                    WHERE IN_DATE = (SELECT MAX (IN_DATE) AS IN_DATE &
                    FROM ADM_TRANS_LOG &
                    WHERE CASE_NO = <CASE_NO>) &
                     AND CASE_NO = <CASE_NO>
getTranDeptData.Debug=N
//查询最新转科科室的数据
getOldTranDeptData.Type=TSQL
getOldTranDeptData.SQL=SELECT MIN(IN_DATE) AS  IN_DATE FROM ADM_TRANS_LOG WHERE  (PSF_KIND<>'CANCEL' OR PSF_KIND IS NULL)
getOldTranDeptData.item=CASE_NO
getOldTranDeptData.CASE_NO=CASE_NO=<CASE_NO>
getOldTranDeptData.Debug=N
//删除转科记录
deleteData.Type=TSQL
deleteData.SQL=DELETE FROM ADM_TRANS_LOG WHERE CASE_NO=<CASE_NO> 
deleteData.item=IN_DATE
deleteData.IN_DATE=IN_DATE=<IN_DATE>
deleteData.Debug=N
//字典更新转科记录
updateData.Type=TSQL
updateData.SQL=UPDATE  ADM_TRANS_LOG SET &
	OUT_DATE=<OUT_DATE>,OUT_DEPT_CODE=<OUT_DEPT_CODE>,OUT_STATION_CODE=<OUT_STATION_CODE> ,IN_DEPT_CODE=<IN_DEPT_CODE>,&
	IN_STATION_CODE=<IN_STATION_CODE>,PSF_KIND=<PSF_KIND>,DEPT_ATTR_FLG=<DEPT_ATTR_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>
updateData.item=CASE_NO;IN_DATE
updateData.CASE_NO=CASE_NO=<CASE_NO>
updateData.IN_DATE=IN_DATE=<IN_DATE>
updateData.Debug=N

//转科更新转科记录
updateADM.Type=TSQL
updateADM.SQL=UPDATE  ADM_TRANS_LOG SET &
	OUT_DATE=<OUT_DATE>,OUT_DEPT_CODE=<OUT_DEPT_CODE>,OUT_STATION_CODE=<OUT_STATION_CODE> ,&
	PSF_KIND=<PSF_KIND>,DEPT_ATTR_FLG=<DEPT_ATTR_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>
updateADM.item=CASE_NO;IN_DATE
updateADM.CASE_NO=CASE_NO=<CASE_NO>
updateADM.IN_DATE=IN_DATE=<IN_DATE>
updateADM.Debug=N

//查询病案首页转科记录
getTranHospFormro.Type=TSQL
getTranHospFormro.SQL=SELECT A.IPD_NO,E.PAT_NAME,A.IN_DEPT_CODE,A.IN_STATION_CODE,A.CASE_NO,A.IN_DATE,A.OUT_DATE,A.OUT_DEPT_CODE,A.OUT_STATION_CODE &      
                      FROM ADM_TRANS_LOG  A,SYS_PATINFO E &
                      WHERE  A.MR_NO=E.MR_NO AND ( A.PSF_KIND='OUDP' OR A.PSF_KIND IS NULL) AND A.CASE_NO=<CASE_NO> ORDER BY A.IN_DATE DESC
getTranHospFormro.item=MR_NO;IPD_NO
getTranHospFormro.MR_NO=A.MR_NO=<MR_NO>
getTranHospFormro.IPD_NO=A.IPD_NO=<IPD_NO>
getTranHospFormro.Debug=N
//取消转科时,插入转科记录
insertLogForCancel.Type=TSQL
insertLogForCancel.SQL=INSERT INTO ADM_TRANS_LOG (CASE_NO, IN_DATE, MR_NO, IPD_NO, OUT_DEPT_CODE,& 
  		OUT_STATION_CODE,OUT_DATE,IN_DEPT_CODE, IN_STATION_CODE, OPT_USER, OPT_DATE,OPT_TERM,PSF_KIND )&  
  		VALUES (<CASE_NO>,<IN_DATE>,<MR_NO>,<IPD_NO>,<OUT_DEPT_CODE>,&
 		<OUT_STATION_CODE>,<OUT_DATE>,<IN_DEPT_CODE>,<IN_STATION_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<PSF_KIND>) 
insertLogForCancel.Debug=N





