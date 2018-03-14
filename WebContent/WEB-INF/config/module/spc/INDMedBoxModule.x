  #
   # Title:病区药盒对照维护
   #
   # Description:病区药盒对照维护
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author shendr 2013-05-21
   
Module.item=queryInfo;insertInfo;updateInfo;deleteInfo

#查询
queryInfo.Type=TSQL
queryInfo.SQL=SELECT ELETAG_CODE,AP_REGION,OPT_USER,OPT_DATE,OPT_TERM FROM IND_MEDBOX
queryInfo.item=ELETAG_CODE
queryInfo.ELETAG_CODE=ELETAG_CODE=<ELETAG_CODE>
queryInfo.Debug=N

#保存
insertInfo.Type=TSQL
insertInfo.SQL=INSERT INTO IND_MEDBOX &
		   (ELETAG_CODE,AP_REGION,OPT_USER,OPT_DATE,OPT_TERM) VALUES (<ELETAG_CODE>,<AP_REGION>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertInfo.Debug=N

#更新
updateInfo.Type=TSQL
updateInfo.SQL=UPDATE IND_MEDBOX SET AP_REGION=<AP_REGION> WHERE ELETAG_CODE=<ELETAG_CODE>
updateInfo.Debug=N

#删除
deleteInfo.Type=TSQL
deleteInfo.SQL=DELETE FROM IND_MEDBOX WHERE ELETAG_CODE=<ELETAG_CODE>
deleteInfo.Debug=N