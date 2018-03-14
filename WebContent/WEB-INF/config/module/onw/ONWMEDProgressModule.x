######################################################
# <p>Title:门诊护士站 医技进度查询 </p>
#
# <p>Description:门诊护士站 医技进度查询 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author zhangk  2009-11-26
# @version 4.0
#
######################################################
Module.item=selectData

selectData.Type=TSQL
selectData.SQL=SELECT A.CAT1_TYPE, B.DEPT_CHN_DESC, &
			 COUNT (CASE &
				   WHEN A.STATUS != '3' AND A.STATUS != '4' &
				      THEN 1 &
				   ELSE NULL &
				END &
			       ) AS ORDER_NUM, &
			 COUNT (CASE &
				   WHEN A.STATUS = '2' &
				      THEN 1 &
				   ELSE NULL &
				END) AS WAIT_NUM &
		    FROM MED_APPLY A, SYS_DEPT B &
		   WHERE A.EXEC_DEPT_CODE = B.DEPT_CODE &
		   AND ORDER_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS') &
		GROUP BY A.CAT1_TYPE, A.EXEC_DEPT_CODE, B.DEPT_CHN_DESC
selectData.item=EXEC_DEPT_CODE;CAT1_TYPE
selectData.EXEC_DEPT_CODE=A.EXEC_DEPT_CODE=<EXEC_DEPT_CODE>
selectData.CAT1_TYPE=A.CAT1_TYPE=<CAT1_TYPE>
selectData.Debug=N