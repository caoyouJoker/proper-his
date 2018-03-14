# 
#  Title: STA_IN_05出院者来源及其他项目台帐
# 
#  Description: STA_IN_05出院者来源及其他项目台帐
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.07.01
#  version 1.0
#
Module.item=selectBQ;selectQS

//本期数据查询语句
selectBQ.Type=TSQL
selectBQ.SQL=SELECT &
		   STA_DATE, DEPT_CODE, STATION_CODE, &
		   DATA_01, DATA_02, DATA_03, &
		   DATA_04, DATA_05, DATA_06, &
		   DATA_07, DATA_08, DATA_09, &
		   DATA_10, DATA_11, DATA_12, &
		   DATA_13, DATA_14, DATA_15, &
		   DATA_16, DATA_17, DATA_18, &
		   DATA_19, DATA_20, CONFIRM_FLG, &
		   CONFIRM_USER, CONFIRM_DATE, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		FROM STA_IN_05
selectBQ.item=Month;Year;Dept;REGION_CODE
selectBQ.Month=STA_DATE=<Month>
//============pangben modify 20110525 start
selectBQ.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectBQ.Year=SUBSTR(STA_DATE,0,4) = <Year>
selectBQ.Dept=DEPT_CODE LIKE <Dept>||'%'
selectBQ.Debug=N


//趋势数据查询语句
selectQS.Type=TSQL
selectQS.SQL=SELECT &
		   STA_DATE, DEPT_CODE, STATION_CODE, &
		   DATA_01, DATA_02, DATA_03, &
		   DATA_04, DATA_05, DATA_06, &
		   DATA_07, DATA_08, DATA_09, &
		   DATA_10, DATA_11, DATA_12, &
		   DATA_13, DATA_14, DATA_15, &
		   DATA_16, DATA_17, DATA_18, &
		   DATA_19, DATA_20, CONFIRM_FLG, &
		   CONFIRM_USER, CONFIRM_DATE, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		FROM STA_IN_05 &
		WHERE DEPT_CODE LIKE <Dept>||'%' &
		AND TO_DATE(STA_DATE,'YYYYMM') BETWEEN TO_DATE(<DATE_S>,'YYYYMM') AND TO_DATE(<DATE_E>,'YYYYMM')
//============pangben modify 20110525 start
selectQS.item=REGION_CODE
selectQS.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectQS.Debug=N