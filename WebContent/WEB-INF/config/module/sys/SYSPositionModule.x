Module.item=selectdata;deletedata;insertdata;updatedata;existsPosition

//查询职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
selectdata.Type=TSQL
selectdata.SQL=SELECT POS_CODE,POS_DESC,POS_TYPE,OPT_USER,OPT_DATE,SEQ,PY1,PY2,REMARK FROM SYS_POSITION WHERE POS_CODE LIKE <POS_CODE> ORDER BY SEQ
selectdata.Debug=N

//删除职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM SYS_POSITION WHERE POS_CODE = <POS_CODE>
deletedata.Debug=N

//新增职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_POSITION (POS_CODE,POS_DESC,POS_TYPE,SEQ,PY1,PY2,REMARK) VALUES(<POS_CODE>,<POS_DESC>,<POS_TYPE>,<SEQ>,<PY1>,<PY2>,<REMARK>)
insertdata.Debug=N

//更新职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_POSITION SET POS_DESC=<POS_DESC>,POS_TYPE=<POS_TYPE>,SEQ=<SEQ>,PY1=<PY1>,PY2=<PY2>,REMARK=<REMARK> WHERE POS_CODE=<POS_CODE>
updatedata.Debug=N

//是否存在职别
existsPosition.type=TSQL
existsPosition.SQL=SELECT COUNT(*) AS COUNT FROM SYS_POSITION WHERE POS_CODE=<POS_CODE>



