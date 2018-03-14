# 
#  Title:合同单位信息维护module
# 
#  Description:合同单位信息维护module
# 
#  Copyright: Copyright (c) bluecore
# 
#  author caowl 20130114
#  version 1.0
#
Module.item=selectAll;deleteData;checkDataExist;updateData;insertData;queryByContractCode;updPrePay

//查询全部数据
selectAll.Type=TSQL
selectAll.SQL=SELECT CONTRACT_CODE,CONTRACT_DESC, LIMIT_AMT, PREPAY_AMT,CONTACT,TEL2,TEL1,REGION_CODE,ADDRESS,DESCRIPTION &
		FROM BIL_CONTRACTM &
		WHERE DEL_FLG = 'N' 
selectAll.item=CONTRACT_CODE;CONTRACT_DESC;REGION_CODE
selectAll.CONTRACT_CODE=CONTRACT_CODE LIKE <CONTRACT_CODE>
selectAll.CONTRACT_DESC=CONTRACT_DESC LIKE <CONTRACT_DESC>
selectAll.REGION_CODE =REGION_CODE = <REGION_CODE>
selectAll.Debug=N


//删除合同单位数据
deleteData.Type=TSQL
deleteData.SQL=	UPDATE BIL_CONTRACTM SET DEL_FLG = 'Y' WHERE CONTRACT_CODE = <CONTRACT_CODE> &
AND REGION_CODE = <REGION_CODE>
deleteData.Debug=N

//查询合同单位数据是否存在
checkDataExist.Type=TSQL
checkDataExist.SQL=SELECT COUNT(*) AS DATACOUNT FROM  BIL_CONTRACTM   WHERE &
 CONTRACT_CODE = <CONTRACT_CODE> AND DEL_FLG = 'N'
checkDataExist.Debug=N

//更新合同单位数据
updateData.Type=TSQL
updateData.SQL= UPDATE BIL_CONTRACTM &
      		SET CONTRACT_CODE =<CONTRACT_CODE>, &
      		CONTRACT_DESC=<CONTRACT_DESC>,&
      		PY1 = <PY1>, &
      		DESCRIPTION=<DESCRIPTION>, &
      		ADDRESS=<ADDRESS>, &
      		TEL1=<TEL1>, &
      		TEL2=<TEL2>,&  
       		CONTACT=<CONTACT>,&
		LIMIT_AMT=<LIMIT_AMT>,&		
		REGION_CODE = <REGION_CODE>,&
		OPT_USER=<OPT_USER>,&
		OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),&
		OPT_TERM=<OPT_TERM> &       
        WHERE CONTRACT_CODE = <CONTRACT_CODE>
updateData.Debug=N


//插入合同单位数据
insertData.Type=TSQL
insertData.SQL= INSERT INTO BIL_CONTRACTM(CONTRACT_CODE,CONTRACT_DESC,CONTRACT_ABS_DESC,PY1,PY2,&
			SEQ,DESCRIPTION, POST_CODE, ADDRESS, TEL1, &
			TEL2,CONTACT, LIMIT_AMT, PREPAY_AMT, DEL_FLG, &
			OPT_USER, OPT_DATE, OPT_TERM,REGION_CODE) &
		VALUES(<CONTRACT_CODE>,<CONTRACT_DESC>,<CONTRACT_ABS_DESC>,<PY1>,<PY2>,&
			<SEQ>,<DESCRIPTION>,<POST_CODE>,<ADDRESS>,<TEL1>, &
			<TEL2>,<CONTACT>,<LIMIT_AMT>, <PREPAY_AMT>,<DEL_FLG>, &
			<OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'), <OPT_TERM>,<REGION_CODE>)
insertData.Debug=N

//查询某合同单位的预交金
queryByContractCode.Type=TSQL
queryByContractCode.SQL=SELECT PREPAY_AMT FROM BIL_CONTRACTM WHERE CONTRACT_CODE = <CONTRACT_CODE>
queryByContractCode.Debug=N

//更新合同单位预交金余额
updPrePay.Type=TSQL
updPrePay.SQL=UPDATE BIL_CONTRACTM SET PREPAY_AMT = <PREPAY_AMT> , &
	OPT_USER = <OPT_USER>, &
	OPT_TERM = <OPT_TERM>, &
	OPT_DATE = <OPT_DATE> &
	WHERE CONTRACT_CODE = <CONTRACT_CODE>
updPrePay.Debug=N


