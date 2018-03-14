# 
#  Title: 对外统计报表 出院病人疾病分类年龄别情况月报表(卫统5表2)
# 
#  Description: 对外统计报表 出院病人疾病分类年龄别情况月报表(卫统5表2)
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.14
#  version 1.0
#
Module.item=deleteSTA_OUT_03;insertSTA_OUT_03;selectPrint

//删除指定日期的数据
//==============pangben modify 20110523 添加区域条件
deleteSTA_OUT_03.Type=TSQL
deleteSTA_OUT_03.SQL=DELETE FROM STA_OUT_03 WHERE STA_DATE=<STA_DATE> AND DATA_TYPE=<DATA_TYPE> AND REGION_CODE =<REGION_CODE>
deleteSTA_OUT_03.Debug=N

//插入数据
insertSTA_OUT_03.Type=TSQL
insertSTA_OUT_03.SQL=INSERT INTO  STA_OUT_03 ( &
                        STA_DATE,SEQ,DATA_TYPE,DATA_01,DATA_02, &
                        DATA_03,DATA_04,DATA_05,DATA_06,CONFIRM_FLG, &
                        CONFIRM_USER,CONFIRM_DATE,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE &
                        ) &
			VALUES ( &
			<STA_DATE>,<SEQ>,<DATA_TYPE>,<DATA_01>,<DATA_02>, &
                        <DATA_03>,<DATA_04>,<DATA_05>,<DATA_06>,<CONFIRM_FLG>, &
                        <CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE> &
			)
insertSTA_OUT_03.Debug=N

//查询数据
//==============pangben modify 20110523 添加区域条件
selectPrint.Type=TSQL
selectPrint.SQL=SELECT &
		       A.STA_DATE, A.SEQ, A.DATA_01, &
		       A.DATA_02, A.DATA_03, A.DATA_04, &
		       A.DATA_05, A.DATA_06, B.ICD_DESC &
		    FROM STA_OUT_03 A,STA_173_LIST B &
		    WHERE A.SEQ=B.SEQ &
		    AND A.STA_DATE=<STA_DATE> &
		    AND A.DATA_TYPE=<DATA_TYPE> &
		    AND A.REGION_CODE=<REGION_CODE> &
		    ORDER BY TO_NUMBER(SEQ)
selectPrint.Debug=N