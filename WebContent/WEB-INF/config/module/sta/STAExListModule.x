# 
#  Title:中间档损伤中毒单档module
# 
#  Description:中间档损伤中毒单档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.02
#  version 1.0
#
Module.item=selectData;insertData;updateData;deleteData;selectDataList

//查询数据
selectData.Type=TSQL
selectData.SQL=SELECT &
		   SEQ, ICD_DESC, CONDITION, &
		   OPT_USER, OPT_DATE, OPT_TERM &
		FROM STA_EX_LIST 
selectData.item=SEQ;ICD_DESC
selectData.SEQ=SEQ=<SEQ>
selectData.ICD_DESC=ICD_DESC LIKE <ICD_DESC>
selectData.Debug=N

//插入数据
insertData.Type=TSQL
insertData.SQL=INSERT INTO STA_EX_LIST( &
		    SEQ, ICD_DESC, CONDITION, &
		    OPT_USER, OPT_DATE, OPT_TERM &
		    ) &
	    VALUES ( &
		    <SEQ>, <ICD_DESC>, <CONDITION>, &
		    <OPT_USER>, SYSDATE, <OPT_TERM> &
	     )
insertData.Debug=N

//修改数据
updateData.Type=TSQL
updateData.SQL=UPDATE STA_EX_LIST SET &
			 ICD_DESC=<ICD_DESC>, &
			 CONDITION=<CONDITION>, &
			 OPT_USER=<OPT_USER>, &
			 OPT_DATE=SYSDATE, &
			 OPT_TERM=<OPT_TERM> &
			 WHERE  SEQ=<SEQ>  
updateData.Debug=N

deleteData.Type=TSQL
deleteData.SQL=DELETE FROM STA_EX_LIST WHERE SEQ=<SEQ>  
deleteData.Debug=N

//查询所有病种信息
selectDataList.Type=TSQL
selectDataList.SQL=SELECT SEQ, ICD_DESC, CONDITION FROM STA_EX_LIST ORDER BY TO_NUMBER(SEQ)
selectDataList.Debug=N