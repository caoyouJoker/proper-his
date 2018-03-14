# 
#  Title: 单病种医疗质量综合统计表
# 
#  Description: 单病种医疗质量综合统计表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.07.03
#  version 1.0
#
Module.item=selectInDays;

//查询首页中住院天数信息
selectInDays.Type=TSQL
selectInDays.SQL=SELECT MIN(REAL_STAY_DAYS) AS MINDAYS,MAX(REAL_STAY_DAYS) AS MAXDAYS,AVG(REAL_STAY_DAYS) AS AVGDAYS,OUT_DIAG_CODE1 &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND OUT_DIAG_CODE1 IN ('I10.05','E23.004') &
			GROUP BY OUT_DIAG_CODE1
selectInDays.item=DEPTCODE
selectInDays.DEPTCODE=OUT_DEPT=<DEPTCODE>
selectInDays.Debug=N