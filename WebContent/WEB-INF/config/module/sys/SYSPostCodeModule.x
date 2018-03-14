Module.item=selectdata;deletedata;insertdata;updatedata;getState;getCity;selectall;selectprovince;selectcity

//查询职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
selectdata.Type=TSQL
selectdata.SQL=SELECT POST_CODE,STATE,STATE_PY,CITY,CITY_PY,D_CITY_FLG,SEQ,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_POSTCODE ORDER BY STATE,CITY,POST_NO, SEQ
selectdata.item=STATE,CITY;POST_CODE
selectdata.STATE=STATE=<STATE>
selectdata.CITY=CITY=<CITY>
selectdata.POST_CODE=POST_CODE=<POST_CODE>
selectdata.Debug=N

//得到省
getState.Type=TSQL
getState.SQL=SELECT STATE FROM SYS_POSTCODE WHERE POST_CODE=<POST_CODE>
getState.Debug=N

//得到城市
getCity.Type=TSQL
getCity.SQL=SELECT CITY FROM POST_CODE WHERE POST_CODE=<POST_CODE>
getCity.Debug=N

//删除职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM POST_CODE WHERE POST_CODE = <POST_CODE>
deletedata.Debug=N

//SEQ;STATE;CITY;POST_NO3;D_CITY_FLG;DESCRIPTION;STATE_PY1;STATE_PY2;CITY_PY1;CITY_PY2
//新增职称代码，职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO POST_CODE VALUES(<POST_CODE>,<STATE>,<STATE_PY>,<CITY>,<CITY_PY>,<D_CITY_FLG>,<SEQ>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//SEQ:SEQ:int;STATE:STATE;CITY:CITY;POST_NO3:POST_NO3:int;D_CITY_FLG:D_CITY_FLG;DESCRIPTION:DESCRIPTION;STATE_PY1:STATE_PY1;STATE_PY2:STATE_PY2;CITY_PY1:CITY_PY1;CITY_PY2:CITY_PY2
//更新职称说明，职称类别，操作人员，操作日期，顺序编号，拼音编码，注记符，备注
//,OPT_TERM=<OPT_TERM>
updatedata.Type=TSQL
updatedata.SQL=UPDATE POST_CODE SET STATE=<STATE>,STATE_PY=<STATE_PY>,CITY=<CITY>,CITY_PY=<CITY_PY>,D_CITY_FLG=<D_CITY_FLG>,SEQ=<SEQ>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE POST_CODE=<POST_CODE>
updatedata.Debug=N


selectall.Type=TSQL
selectall.SQL=SELECT POST_CODE, D_CITY_FLG, STATE, STATE_PY, CITY, &
		CITY_PY, SEQ, OPT_USER,OPT_DATE, OPT_TERM &
	      FROM SYS_POSTCODE &
	      ORDER BY POST_CODE
selectall.item=POST_CODE
selectall.POST_CODE=POST_CODE=<POST_CODE>
selectall.Debug=N

selectprovince.Type=TSQL
selectprovince.SQL=SELECT DISTINCT SUBSTR (POST_CODE,1,2) AS ID ,STATE AS NAME,ENNAME,STATE_PY AS PY1  &
		FROM SYS_POSTCODE &
	       ORDER BY ID
selectprovince.item=POST_CODE
selectprovince.POST_CODE=POST_CODE LIKE <POST_CODE>
selectprovince.Debug=N

selectcity.Type=TSQL
selectcity.SQL=SELECT POST_CODE AS ID ,CITY AS NAME ,ENNAME,CITY_PY AS PY1 &
		FROM SYS_POSTCODE &
	       ORDER BY ID
selectcity.item=POST_CODE
selectcity.POST_CODE=POST_CODE LIKE <POST_CODE>
selectcity.Debug=N



