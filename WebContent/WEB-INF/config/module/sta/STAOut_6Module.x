# 
#  Title: 对外统计报表 损伤中毒和中毒小计的外部原因分类年报表（合计）（卫统32表2）
# 
#  Description: 对外统计报表 损伤中毒和中毒小计的外部原因分类年报表（合计）（卫统32表2）
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.14
#  version 1.0
#
Module.item=deleteSTA_OUT_06;insertSTA_OUT_06;selectPrint

//删除指定日期的数据
//==========pangben modify 20110523 添加区域条件
deleteSTA_OUT_06.Type=TSQL
deleteSTA_OUT_06.SQL=DELETE FROM STA_OUT_06 WHERE STA_DATE=<STA_DATE> AND DATA_TYPE=<DATA_TYPE> AND REGION_CODE=<REGION_CODE>
deleteSTA_OUT_06.Debug=N

//插入数据
//==========pangben modify 20110523 添加区域列
insertSTA_OUT_06.Type=TSQL
insertSTA_OUT_06.SQL=INSERT INTO  STA_OUT_06 ( &
                        STA_DATE,SEQ,DATA_TYPE,DATA_01,DATA_02, &
                        DATA_03,DATA_04,DATA_05,DATA_06,DATA_07,CONFIRM_FLG, &
                        CONFIRM_USER,CONFIRM_DATE,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE &
                        ) &
			VALUES ( &
			<STA_DATE>,<SEQ>,<DATA_TYPE>,<DATA_01>,<DATA_02>, &
                        <DATA_03>,<DATA_04>,<DATA_05>,<DATA_06>,<DATA_07>,<CONFIRM_FLG>, &
                        <CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE> &
			)
insertSTA_OUT_06.Debug=N

//查询数据
selectPrint.Type=TSQL
selectPrint.SQL=SELECT &
		       A.STA_DATE, A.SEQ, A.DATA_01, &
		       A.DATA_02, A.DATA_03, A.DATA_04, &
		       A.DATA_05, A.DATA_06,A.DATA_07, B.ICD_DESC &
		    FROM STA_OUT_06 A,STA_EX_LIST B &
		    WHERE A.SEQ=B.SEQ &
		    AND A.STA_DATE=<STA_DATE> &
		    AND A.DATA_TYPE=<DATA_TYPE> &
		    ORDER BY TO_NUMBER(SEQ)
//==========pangben modify 20110523 start
selectPrint.item=REGION_CODE
selectPrint.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==========pangben modify 20110523 stop
selectPrint.Debug=N