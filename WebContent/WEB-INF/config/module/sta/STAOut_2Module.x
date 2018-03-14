# 
#  Title: 对外统计报表	-- 卫统5表1
# 
#  Description: 对外统计报表	-- 卫统5表1
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.12
#  version 1.0
#
Module.item=deleteSTA_OUT_02;insertSTA_OUT_02;selectPrint

//删除指定日期的数据
//===========pangben modify 20110520 添加区域条件
deleteSTA_OUT_02.Type=TSQL
deleteSTA_OUT_02.SQL=DELETE FROM STA_OUT_02 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_OUT_02.Debug=N

//插入数据
//===========pangben modify 20110520 添加区域列
insertSTA_OUT_02.Type=TSQL
insertSTA_OUT_02.SQL=INSERT INTO  STA_OUT_02 ( &
                        STA_DATE,SEQ,DATA_01,DATA_02,DATA_03, &
                        DATA_04,DATA_05,DATA_06,DATA_07,CONFIRM_FLG, &
                        CONFIRM_USER,CONFIRM_DATE,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE &
                        ) &
			VALUES ( &
			<STA_DATE>,<SEQ>,<DATA_01>,<DATA_02>,<DATA_03>, &
                        <DATA_04>,<DATA_05>,<DATA_06>,<DATA_07>,<CONFIRM_FLG>, &
                        <CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE> &
			)
insertSTA_OUT_02.Debug=N

//查询数据
selectPrint.Type=TSQL
selectPrint.SQL=SELECT &
		       A.STA_DATE, A.SEQ, A.DATA_01, &
		       A.DATA_02, A.DATA_03, A.DATA_04, &
		       A.DATA_05, A.DATA_06, A.DATA_07,&
		       B.ICD_DESC &
		    FROM STA_OUT_02 A,STA_173_LIST B &
		    WHERE A.SEQ=B.SEQ &
		    AND STA_DATE=<STA_DATE> &
		    ORDER BY TO_NUMBER(SEQ)
//===========pangben modify 20110520 start	
selectPrint.item=REGION_CODE
selectPrint.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//===========pangben modify 20110520 stop
selectPrint.Debug=N