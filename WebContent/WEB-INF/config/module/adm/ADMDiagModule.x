##########################################
# <p>Title:住院诊断 </p>
#
# <p>Description:住院诊断 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk  2009-9-9
# @version 4.0
##########################################

Module.item=queryDiagForMro;queryData;insertDiag

//根据CASE_NO查询某一病患的所有诊断信息（病案首页使用）
queryDiagForMro.Type=TSQL
queryDiagForMro.SQL=SELECT IO_TYPE AS type, MAINDIAG_FLG AS main, ICD_CODE AS name, &
			ICD_CODE AS code, '' AS status, DESCRIPTION AS remark,SEQ_NO,'' as ADDITIONAL,'' AS  ADDITIONAL_DESC,ICD_TYPE AS KIND &
			FROM ADM_INPDIAG &
			WHERE CASE_NO=<CASE_NO>  ORDER BY IO_TYPE ASC,MAINDIAG_FLG DESC,SEQ_NO
queryDiagForMro.Debug=N

//查询病患的诊断===pangben 2016-5-5 添加排序条件
queryData.Type=TSQL
queryData.SQL=SELECT A.CASE_NO, A.IO_TYPE, A.ICD_CODE, &
		A.MAINDIAG_FLG, A.ICD_TYPE, A.SEQ_NO, &
		A.MR_NO, A.IPD_NO, A.DESCRIPTION, &
		A.OPT_USER, A.OPT_DATE, A.OPT_TERM,B.ICD_CHN_DESC &
		FROM ADM_INPDIAG A,SYS_DIAGNOSIS B &
		WHERE A.ICD_CODE=B.ICD_CODE &
		ORDER BY A.IO_TYPE ,A.MAINDIAG_FLG DESC,A.SEQ_NO
queryData.item=IO_TYPE;MAINDIAG_FLG;CASE_NO;ICD_TYPE
queryData.IO_TYPE=A.IO_TYPE=<IO_TYPE>
queryData.MAINDIAG_FLG=A.MAINDIAG_FLG=<MAINDIAG_FLG>
queryData.CASE_NO=A.CASE_NO=<CASE_NO>
queryData.ICD_TYPE=A.ICD_TYPE=<ICD_TYPE>
queryData.Debug=N

//插入诊断信息
insertDiag.Type=TSQL
insertDiag.SQL=INSERT INTO ADM_INPDIAG ( &
		   CASE_NO, IO_TYPE, ICD_CODE,MAINDIAG_FLG, ICD_TYPE, &
		   SEQ_NO, MR_NO, IPD_NO, DESCRIPTION, OPT_USER, &
		   OPT_DATE, OPT_TERM) &
		VALUES ( &
		   <CASE_NO>, <IO_TYPE>, <ICD_CODE>,<MAINDIAG_FLG>, <ICD_TYPE>, &
		   <SEQ_NO>, <MR_NO>, <IPD_NO>, <DESCRIPTION>, <OPT_USER>, &
		   SYSDATE, <OPT_TERM> &
		)   
insertDiag.Debug=N