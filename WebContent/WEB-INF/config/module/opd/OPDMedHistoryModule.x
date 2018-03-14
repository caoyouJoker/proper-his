Module.item=insertdata;selectdata;updatedata;deletedata;existsMedHistory

// 查询全字段
selectdata.Type=TSQL
selectdata.SQL=SELECT MR_NO,ADM_DATE,ICD_CODE,ICD_TYPE,ADM_TYPE,CASE_NO,DEPT_CODE,DR_CODE,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM FROM OPD_MEDHISTORY ORDER BY ADM_DATE,ICD_TYPE  ,ICD_CODE
selectdata.item=MR_NO;ADM_DATE;ICD_CODE;ICD_TYPE
selectdata.MR_NO=MR_NO=<MR_NO>
selectdata.ADM_DATE=ADM_DATE=<ADM_DATE>
selectdata.ICD_CODE=ICD_CODE=<ICD_CODE>
selectdata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
selectdata.Debug=N

//插入数据（测试用）
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO OPD_MEDHISTORY  VALUES(<MR_NO>,<ADM_DATE>,<ICD_CODE>,<ICD_TYPE>,<ADM_TYPE>,<CASE_NO>,<DEPT_CODE>,<DR_CODE>,<DESCRIPTION>,<OPT_USER>,SYSDATE,<OPT_TERM> )
insertdata.Debug=N

//修改数据（测试用）
updatedata.Type=TSQL
updatedata.SQL=UPDATE OPD_MEDHISTORY SET ADM_TYPE=<ADM_TYPE>,DEPT_CODE=<DEPT_CODE>,DR_CODE=<DR_CODE>,DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>
updatedata.item= MR_NO;ADM_DATE;ICD_CODE;ICD_TYPE
updatedata.MR_NO=MR_NO=<MR_NO>
updatedata.ADM_DATE=ADM_DATE=<ADM_DATE>
updatedata.ICD_CODE=ICD_CODE=<ICD_CODE>
updatedata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
updatedata.Debug=N

//删除数据（测试用）
deletedata.Type=TSQL
deletedata.SQL=DELETE OPD_MEDHISTORY
deletedata.item= MR_NO;ADM_DATE;ICD_CODE;ICD_TYPE
deletedata.MR_NO=MR_NO=<MR_NO>
deletedata.ADM_DATE=ADM_DATE=<ADM_DATE>
deletedata.ICD_CODE=ICD_CODE=<ICD_CODE>
deletedata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
deletedata.Debug=N

//判断数据是否存在（测试用）
existsDrugAllergy.Type=TSQL
existsDrugAllergy.SQL=SELECT COUNT(CASE_NO) AS COUNT FROM OPD_MEDHISTORY
existsDrugAllergy.item= MR_NO;ADM_DATE;ICD_CODE;ICD_TYPE
existsDrugAllergy.MR_NO=MR_NO=<MR_NO>
existsDrugAllergy.ADM_DATE=ADM_DATE=<ADM_DATE>
existsDrugAllergy.ICD_CODE=ICD_CODE=<ICD_CODE>
existsDrugAllergy.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
existsDrugAllergy.Debug=N