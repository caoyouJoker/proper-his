Module.item=insertdata;selectdata;updatedata;deletedata;existsDiagRec;queryInsData

// 查询全字段
selectdata.Type=TSQL
selectdata.SQL=SELECT CASE_NO,ICD_CODE,ICD_TYPE,MAIN_DIAG_FLG,ADM_TYPE,DIAG_NOTE,DR_CODE,ORDER_DATE,FILE_NO,OPT_USER,OPT_DATE,OPT_TERM FROM OPD_DIAGREC ORDER BY ICD_CODE
selectdata.item=CASE_NO;ICD_CODE;ICD_TYPE
selectdata.CASE_NO=CASE_NO=<CASE_NO>
selectdata.ICD_CODE=ICD_CODE=<ICD_CODE>
selectdata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
selectdata.Debug=N

//插入数据（测试用）
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO OPD_DIAGREC VALUES (<CASE_NO>,<ICD_TYPE>,<ICD_CODE>,<MAIN_DIAG_FLG>,<ADM_TYPE>,<DIAG_NOTE>,<DR_CODE>,<ORDER_DATE>,<FILE_NO>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//修改数据（测试用）
updatedata.Type=TSQL
updatedata.SQL=UPDATE OPD_DIAGREC SET  MAIN_DIAG_FLG=<MAIN_DIAG_FLG>, ADM_TYPE=<ADM_TYPE>, DIAG_NOTE=<DIAG_NOTE>, DR_CODE=<DR_CODE>, ORDER_DATE=<ORDER_DATE>, FILE_NO=<FILE_NO>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM>
updatedata.item=CASE_NO;ICD_CODE;ICD_TYPE
updatedata.CASE_NO=CASE_NO=<CASE_NO>
updatedata.ICD_CODE=ICD_CODE=<ICD_CODE>
updatedata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
updatedata.Debug=N

//删除数据（测试用）
deletedata.Type=TSQL
deletedata.SQL=DELETE OPD_DIAGREC
deletedata.item=CASE_NO;ICD_CODE;ICD_TYPE
deletedata.CASE_NO=CASE_NO=<CASE_NO>
deletedata.ICD_CODE=ICD_CODE=<ICD_CODE>
deletedata.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
deletedata.Debug=N

//判断数据是否存在（测试用）
existsDiagRec.Type=TSQL
existsDiagRec.SQL=SELECT COUNT(CASE_NO) AS COUNT FROM OPD_DIAGREC
existsDiagRec.item=CASE_NO;ICD_CODE;ICD_TYPE
existsDiagRec.CASE_NO=CASE_NO=<CASE_NO>
existsDiagRec.ICD_CODE=ICD_CODE=<ICD_CODE>
existsDiagRec.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
existsDiagRec.Debug=N

//查询医保数据
queryInsData.Type=TSQL
queryInsData.SQL=SELECT  A.CASE_NO, A.ICD_CODE, A.ICD_TYPE, A.MAIN_DIAG_FLG, A.ADM_TYPE, A.DIAG_NOTE,&
                 A.DR_CODE, A.ORDER_DATE, A.FILE_NO, A.OPT_USER, A.OPT_DATE, A.OPT_TERM,B.DR_QUALIFY_CODE,E.ICD_CHN_DESC &
                 FROM OPD_DIAGREC A ,SYS_OPERATOR B,REG_PATADM D,SYS_DIAGNOSIS E WHERE A.DR_CODE=B.USER_ID(+) &
                 AND A.CASE_NO=D.CASE_NO(+)  AND A.ICD_CODE=E.ICD_CODE(+) &
                 ORDER BY ICD_CODE 
queryInsData.item=CASE_NO;ICD_CODE;ICD_TYPE
queryInsData.CASE_NO=A.CASE_NO=<CASE_NO>
queryInsData.ICD_CODE=A.ICD_CODE=<ICD_CODE>
queryInsData.ICD_TYPE=A.ICD_TYPE=<ICD_TYPE>
queryInsData.Debug=N