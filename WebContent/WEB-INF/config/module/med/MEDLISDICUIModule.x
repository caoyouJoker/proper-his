###############################################
# <p>Title:项目编码与检验编码对应 </p>
#
# <p>Description:项目编码与检验编码对应 </p>
#
# <p>Copyright: Copyright (c) 2014</p>
#
# <p>Company:bluecore </p>
#
# @author caoyong
# @version 1.0
###############################################

Module.item=selectdata;insertdata;updatedata;deletedata;selectdataAll




deletedata.Type=TSQL
deletedata.SQL=DELETE  FROM　MED_LIS_MAP WHERE MAP_ID=<MAP_ID>
deletedata.Debug=N


selectdataAll.Type=TSQL
selectdataAll.SQL=SELECT MAP_ID,MAP_DESC,SEQ,LIS_ID,LIS_DESC,PY1, &
		       TYPE,MAP_TYPE,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM　MED_LIS_MAP ORDER BY SEQ  
                
selectdataAll.Debug=N

selectdata.Type=TSQL
selectdata.SQL=SELECT MAP_ID,MAP_DESC,SEQ,LIS_ID,LIS_DESC,PY1, &
		       TYPE,MAP_TYPE,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM &
		  FROM　MED_LIS_MAP   &
                  WHERE MAP_ID=<MAP_ID> 
selectdata.Debug=N


//插入全字段
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO  MED_LIS_MAP &
		      (MAP_ID,MAP_DESC,SEQ,LIS_ID,LIS_DESC,PY1, &
		       TYPE,MAP_TYPE,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM &
                       )&
		VALUES (<MAP_ID>,<MAP_DESC>,<SEQ>,<LIS_ID>,<LIS_DESC>,<PY1>, &
		       <TYPE>,<MAP_TYPE>,<DESCRIPTION>,<OPT_USER>,<OPT_DATE>,<OPT_TERM> )
insertdata.Debug=N

//根据 SMS_CODE 更新字段
updatedata.Type=TSQL
updatedata.SQL=UPDATE MED_LIS_MAP SET &
		 	MAP_DESC=<MAP_DESC>,SEQ=<SEQ>,LIS_ID=<LIS_ID>,PY1=<PY1>, &
		 	LIS_DESC=<LIS_DESC>,TYPE=<TYPE>,MAP_TYPE=<MAP_TYPE>,DESCRIPTION=<DESCRIPTION>, &
			OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM > &
		        WHERE MAP_ID=<MAP_ID> 
updatedata.Debug=N

