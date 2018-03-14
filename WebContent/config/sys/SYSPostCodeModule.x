 #
 # <p>Title:ÓÊÕþ±àÂë </p>
 #
 # <p>Description: </p>
 #
 # <p>Copyright: Copyright (c) 2008</p>
 #
 # <p>Company:JavaHis </p>
 #
 # @author JiaoY
 # @version 1.0
 #

Module.item=selectall;selectprovince;selectcity

selectall.Type=TSQL
selectall.SQL=SELECT POST_CODE, D_CITY_FLG, STATE, STATE_PY, CITY, &
		CITY_PY, SEQ, OPT_USER,OPT_DATE, OPT_TERM &
	      FROM RSYS_POSTCODE&
	      ORDER BY POST_CODE
selectall.item=POST_CODE
selectall.POST_CODE=POST_CODE=<POST_CODE>
selectall.Debug=Y


selectprovince.Type=TSQL
selectprovince.SQL=SELECT DISTINCT SUBSTR (POST_CODE,1,2) AS ID ,STATE AS NAME &
		FROM SYS_POSTCODE &
	       ORDER BY ID
selectprovince.Debug=Y

selectcity.Type=TSQL
selectcity.SQL=SELECT POST_CODE AS ID ,CITY AS NAME &
		FROM SYS_POSTCODE &
	       ORDER BY ID
selectcity.item=POST_CODE
selectcity.POST_CODE=POST_CODE=<POST_CODE>
selectcity.Debug=N





