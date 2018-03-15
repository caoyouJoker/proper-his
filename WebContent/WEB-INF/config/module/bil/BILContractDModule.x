# 
#  Title:病患信息维护module
# 
#  Description:病患信息维护module
# 
#  Copyright: Copyright (c) ProperSoft
# 
#  author caowl 20130114
#  version 1.0
#
Module.item=selectAllPat;deleteDataPat;checkPatDataExist;updateDataPat;insertDataPat;selectPat

//查询全部数据
selectAllPat.Type=TSQL
selectAllPat.SQL=SELECT A.CONTRACT_CODE,A.CONTRACT_DESC ,B.MR_NO,C.SEX_CODE, &
		C.IDNO,C.TEL_HOME,B. LIMIT_AMT AS LIMIT,C.PAT_NAME,C.FOREIGNER_FLG, &
		C.BIRTH_DATE,C.CTZ1_CODE,C.CTZ2_CODE,C.CTZ3_CODE,C.ADDRESS, &
		B.COMPANY_FLG,B.PAY_FLG,B.DEL_FLG &
	      FROM BIL_CONTRACTM A,BIL_CONTRACTD B,SYS_PATINFO C &
	      WHERE A.CONTRACT_CODE = B.CONTRACT_CODE &
		AND B.MR_NO = C.MR_NO &		
		AND A.CONTRACT_CODE = <CONTRACT_CODE1> &
		AND B.DEL_FLG = 'N'
selectAllPat.item=MR_NO;PAT_NAME;CONTRACT_DESC
selectAllPat.MR_NO =C.MR_NO LIKE <MR_NO>
selectAllPat.PAT_NAME=C.PAT_NAME LIKE <PAT_NAME>
selectAllPat.CONTRACT_DESC=A.CONTRACT_DESC LIKE <CONTRACT_DESC1>
selectAll.Debug=N

//删除合同单位数据
deleteDataPat.Type=TSQL
deleteDataPat.SQL=UPDATE BIL_CONTRACTD SET DEL_FLG = 'Y' WHERE CONTRACT_CODE = <CONTRACT_CODE> AND MR_NO = <MR_NO>	
deleteDataPat.Debug=N

//查询合同单位数据是否存在
checkPatDataExist.Type=TSQL
checkPatDataExist.SQL=SELECT COUNT(*) AS DATACOUNT FROM  BIL_CONTRACTD   WHERE &
 CONTRACT_CODE = <CONTRACT_CODE> AND MR_NO = <MR_NO>
checkPatDataExist.Debug=N

//更新合同单位数据
updateDataPat.Type=TSQL
updateDataPat.SQL=UPDATE BIL_CONTRACTD SET LIMIT_AMT = <LIMIT_AMT> WHERE CONTRACT_CODE = <CONTRACT_CODE> AND MR_NO = <MR_NO>
updateDataPat.Debug=N



//插入合同单位数据
insertDataPat.Type=TSQL
insertDataPat.SQL=INSERT INTO BIL_CONTRACTD (CONTRACT_CODE,MR_NO,LIMIT_AMT,PREPAY_AMT,DEL_FLG, &
			COMPANY_FLG,PAY_FLG,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE)  &
		  VALUES(<CONTRACT_CODE>,<MR_NO>,<LIMIT_AMT>,<PREPAY_AMT>,<DEL_FLG>, &
			<COMPANY_FLG>,<PAY_FLG>,<OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>,<REGION_CODE>)
insertDataPat.Debug=N


