##################################################
# <p>Title:病患动态档 </p>
#
# <p>Description:病患动态档 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:JavaHis </p>
#
# @author JiaoY
# @version 1.0
##################################################
Module.item=insert;updateforchgbed;queryChg;querySeq;queryChgForMro;selectChgForMRO
  
//插入病患动态档
//============pangben modify 20110617 添加区域参数
insert.Type=TSQL
insert.SQL=INSERT INTO ADM_CHG (CASE_NO,SEQ_NO,IPD_NO,MR_NO,&
   		CHG_DATE,PSF_KIND,PSF_HOSP,CANCEL_FLG,&
   		CANCEL_DATE,CANCEL_USER,DEPT_CODE,STATION_CODE,&   		
   		BED_NO,VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE,&   		
   		OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE) &   		
   		VALUES (<CASE_NO>,<SEQ_NO>,<IPD_NO>,<MR_NO>,&   
   		SYSDATE,<PSF_KIND>,<PSF_HOSP>,<CANCEL_FLG>,&   		
   		<CANCEL_DATE>,<CANCEL_USER>,<DEPT_CODE>,<STATION_CODE>,&   		
   		<BED_NO>,<VS_CODE_CODE>,<ATTEND_DR_CODE>,<DIRECTOR_DR_CODE>,&   		
   		<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE>)
insert.Debug=N
 
//转床
updateforchgbed.Type=TSQL
updateforchgbed.SQL=UPDATE  ADM_CHG SET BED_NO=<BED_NO>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &   		
   		WHERE  CASE_NO=<CASE_NO>
updateforchgbed.Debug=N
  
  
  
//动态记录查询
queryChg.Type=TSQL
queryChg.SQL=SELECT A.MR_NO,A.IPD_NO,B.PAT_NAME,A.CHG_DATE,A.PSF_KIND,&  
  		A.BED_NO,A.DEPT_CODE,A.VS_DR_CODE,A.ATTEND_DR_CODE,A.DIRECTOR_DR_CODE,&
  		A.CANCEL_DATE,A.CANCEL_USER,A.OPT_DATE,A.OPT_TERM,A.OPT_USER,A.SEQ_NO &
  		FROM ADM_CHG A,SYS_PATINFO B  &
  		WHERE A.MR_NO=B.MR_NO &
  		AND A.CHG_DATE BETWEEN <START_DATE> AND <END_DATE> & 
  		AND A.PSF_KIND<>'CANCEL' &	
  		ORDER BY CHG_DATE
queryChg.item=MR_NO;CASE_NO;SEQ_NO
queryChg.MR_NO=A.MR_NO=<MR_NO>
queryChg.CASE_NO=A.CASE_NO=<CASE_NO>
queryChg.SEQ_NO=A.SEQ_NO=<SEQ_NO>		
queryChg.Debug=N

//动态记录查询
queryChgForMro.Type=TSQL
queryChgForMro.SQL=SELECT A.MR_NO,A.IPD_NO,B.PAT_NAME,A.CHG_DATE,A.PSF_KIND,&  
  		C.ROOM_NO,A.BED_NO,A.DEPT_CODE,A.VS_DR_CODE,A.ATTEND_DR_CODE,A.DIRECTOR_DR_CODE,&
  		A.CANCEL_DATE,A.CANCEL_USER,A.OPT_DATE,A.OPT_TERM,A.OPT_USER &
  		FROM ADM_CHG A,SYS_PATINFO B,SYS_BED C  &
  		WHERE A.MR_NO=B.MR_NO &
  		AND A.BED_NO=C.BED_NO &
  		AND PSF_KIND LIKE <PSF_KIND> &
  		ORDER BY SEQ_NO
queryChgForMro.item=MR_NO;CASE_NO
queryChgForMro.MR_NO=A.MR_NO=<MR_NO>
queryChgForMro.CASE_NO=A.CASE_NO=<CASE_NO>		
queryChgForMro.Debug=N

//查询最大seq
querySeq.Type=TSQL
querySeq.SQL=SELECT MAX(SEQ_NO) AS SEQ_NO &
  		FROM ADM_CHG &
  		WHERE CASE_NO=<CASE_NO>
querySeq.Debug=N

//查询动态记录列表（病案首页使用）
selectChgForMRO.Type=TSQL
selectChgForMRO.SQL=SELECT A.PSF_KIND, A.CHG_DATE, A.DEPT_CODE, A.BED_NO, &
			B.CHN_DESC,C.ROOM_CODE &
			FROM ADM_CHG A, SYS_DICTIONARY B,SYS_BED C &
			WHERE A.CASE_NO = <CASE_NO> &
			AND A.PSF_KIND = 'INDP' &
			AND B.GROUP_ID = 'ADM_PSFKIND' &
			AND B.ID(+) = A.PSF_KIND &
			AND A.BED_NO=C.BED_NO(+) &
			ORDER BY A.SEQ_NO
selectChgForMRO.Debug=N