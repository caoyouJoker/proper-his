# 
#  Title: STA_IN_04医院医疗诊断质量报表
# 
#  Description: STA_IN_04医院医疗诊断质量报表
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.21
#  version 1.0
#
Module.item=selectQUYCHK_OI;selectQUYCHK_INOUT;selectQUYCHK_OPBFAF;selectQUYCHK_CLPA;deleteSTA_IN_04;insertSTA_IN_04;selectSTA_IN_04;updateSTA_IN_04;selectOut

//查询诊断符合情况-门诊与住院
selectQUYCHK_OI.Type=TSQL
selectQUYCHK_OI.SQL=SELECT COUNT(CASE_NO) AS NUM,OUT_DEPT,QUYCHK_OI &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY OUT_DEPT,QUYCHK_OI
selectQUYCHK_OI.item=DEPT_CODE;REGION_CODE
//=============pangben modify 20110524 start
selectQUYCHK_OI.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110524 stop
selectQUYCHK_OI.DEPT_CODE=OUT_DEPT=<DEPT_CODE>
selectQUYCHK_OI.Debug=N

//诊断符合情况-入院与出院
selectQUYCHK_INOUT.Type=TSQL
selectQUYCHK_INOUT.SQL=SELECT COUNT(CASE_NO) as NUM,OUT_DEPT,QUYCHK_INOUT &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY OUT_DEPT,QUYCHK_INOUT
selectQUYCHK_INOUT.item=DEPT_CODE;STATION_CODE;REGION_CODE
//=============pangben modify 20110524 start
selectQUYCHK_INOUT.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110524 stop
selectQUYCHK_INOUT.DEPT_CODE=OUT_DEPT=<DEPT_CODE>
selectQUYCHK_INOUT.Debug=N

//诊断符合情况-术前术后
selectQUYCHK_OPBFAF.Type=TSQL
selectQUYCHK_OPBFAF.SQL=SELECT COUNT(CASE_NO) as NUM,OUT_DEPT,QUYCHK_OPBFAF &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY OUT_DEPT,QUYCHK_OPBFAF
selectQUYCHK_OPBFAF.item=DEPT_CODE;STATION_CODE;REGION_CODE
//=============pangben modify 20110524 start
selectQUYCHK_OPBFAF.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110524 stop
selectQUYCHK_OPBFAF.DEPT_CODE=OUT_DEPT=<DEPT_CODE>
selectQUYCHK_OPBFAF.Debug=N

//诊断符合情况-临床与病理
selectQUYCHK_CLPA.Type=TSQL
selectQUYCHK_CLPA.SQL=SELECT COUNT(CASE_NO) as NUM,OUT_DEPT,QUYCHK_CLPA &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY OUT_DEPT,OUT_STATION,QUYCHK_CLPA
selectQUYCHK_CLPA.item=DEPT_CODE;REGION_CODE
//=============pangben modify 20110524 start
selectQUYCHK_CLPA.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110524 stop
selectQUYCHK_CLPA.DEPT_CODE=OUT_DEPT=<DEPT_CODE>
selectQUYCHK_CLPA.Debug=N

//删除表STA_IN_04数据
//=============pangben modify 20110524 添加区域条件
deleteSTA_IN_04.Type=TSQL
deleteSTA_IN_04.SQL=DELETE FROM STA_IN_04 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_IN_04.Debug=N

//插入STA_IN_04表数据
//=============pangben modify 20110524 添加区域列
insertSTA_IN_04.Type=TSQL
insertSTA_IN_04.SQL=INSERT INTO STA_IN_04 ( &
			STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,DATA_25, &
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM,REGION_CODE &
		) VALUES ( &
			<STA_DATE>,<DEPT_CODE>,<STATION_CODE>,<DATA_01>,<DATA_02>,<DATA_03>,&
			<DATA_04>,<DATA_05>,<DATA_06>,<DATA_07>,<DATA_08>,&
			<DATA_09>,<DATA_10>,<DATA_11>,<DATA_12>,<DATA_13>,&
			<DATA_14>,<DATA_15>,<DATA_16>,<DATA_17>,<DATA_18>,&
			<DATA_19>,<DATA_20>,<DATA_21>,<DATA_22>,<DATA_23>,&
			<DATA_24>,<DATA_25>, &
			<CONFIRM_FLG>,<CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,&
			SYSDATE,<OPT_TERM>,<REGION_CODE> &
		)
insertSTA_IN_04.Debug=N

//查询STA_IN_04表数据
selectSTA_IN_04.Type=TSQL
selectSTA_IN_04.SQL=SELECT STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,&
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM &
			FROM STA_IN_04
selectSTA_IN_04.item=STA_DATE
selectSTA_IN_04.STA_DATE=STA_DATE=<STA_DATE>
selectSTA_IN_04.Debug=N
//修改STA_IN_04表数据
updateSTA_IN_04.Type=TSQL
updateSTA_IN_04.SQL=UPDATE STA_IN_04 SET &
DATA_01=<DATA_01>,DATA_02=<DATA_02>, DATA_03=<DATA_03>, DATA_04=<DATA_04>, DATA_05=<DATA_05>, &
DATA_06=<DATA_06>, DATA_07=<DATA_07>, DATA_08=<DATA_08>, DATA_09=<DATA_09>, &
DATA_10=<DATA_10>, DATA_11=<DATA_11>, DATA_12=<DATA_12>, DATA_13=<DATA_13>, DATA_14=<DATA_14>, &
DATA_15=<DATA_15>, DATA_16=<DATA_16>, DATA_17=<DATA_17>, DATA_18=<DATA_18>, DATA_19=<DATA_19>, DATA_20=<DATA_20>, &
DATA_21=<DATA_21>, DATA_22=<DATA_22>, DATA_23=<DATA_23>, DATA_24=<DATA_24>, DATA_25=<DATA_25>, &
OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>,CONFIRM_FLG=<CONFIRM_FLG>,CONFIRM_USER=<CONFIRM_USER>,CONFIRM_DATE=<CONFIRM_DATE> &
WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE> AND DEPT_CODE=<DEPT_CODE> AND STATION_CODE=<STATION_CODE>
updateSTA_IN_04.Debug=N

//查询出院人数
selectOut.Type=TSQL
selectOut.SQL=SELECT COUNT(CASE_NO) AS OUT,OUT_DEPT &
	      FROM MRO_RECORD &
	      WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
	      GROUP BY OUT_DEPT
selectOut.Debug=N