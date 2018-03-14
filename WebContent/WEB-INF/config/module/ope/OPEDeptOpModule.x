####################################################
#  Title:科常用手术module
# 
#  Description:科常用手术module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.9.24
#  version 1.0
####################################################
Module.item=selectdata;insertdata;updatedata;deletedata;insertInterData;updateInterData;deleteInterData

//查询数据
selectdata.Type=TSQL
selectdata.SQL=SELECT &
		   A.DEPT_CODE, A.OP_CODE,B.OPT_CHN_DESC, A.SEQ, &
		   A.OPT_USER, A.OPT_DATE, A.OPT_TERM,B.OPT_ENG_DESC &
		FROM OPE_DEPTOP A,SYS_OPERATIONICD B &
		WHERE A.OP_CODE=B.OPERATION_ICD(+) &
		ORDER BY A.SEQ
selectdata.item=DEPT_CODE;OP_CODE
selectdata.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdata.OP_CODE=A.OP_CODE=<OP_CODE>
selectdata.Debug=N

//插入数据
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO OPE_DEPTOP ( &
		   DEPT_CODE, OP_CODE, SEQ, &
		   OPT_USER, OPT_DATE, OPT_TERM) &
		VALUES ( &
		    <DEPT_CODE>, <OP_CODE>, <SEQ>, &
		   <OPT_USER>, SYSDATE, <OPT_TERM> &
		)
insertdata.Debug=N

//更新数据
updatedata.Type=TSQL
updatedata.SQL=UPDATE OPE_DEPTOP SET &
		   SEQ=<SEQ>, &
		   OPT_USER=<OPT_USER>, &
		   OPT_DATE=SYSDATE, &
		   OPT_TERM=<OPT_TERM> &
		WHERE DEPT_CODE=<DEPT_CODE> &
		AND OP_CODE=<OP_CODE>
updatedata.Debug=N

//删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM OPE_DEPTOP WHERE DEPT_CODE=<DEPT_CODE> AND OP_CODE=<OP_CODE>
deletedata.Debug=N



//插入介入护理平台数据
insertInterData.Type=TSQL
insertInterData.SQL=INSERT INTO OPE_INTERVENNURPLAT ( &
		   TIME, CASE_NO,SEQ_NO,STATION_CODE,PRESSURE,PAIN_ASSESSMENT,OXYGEN_SATURATION, &
		   OP_DEPT_CODE,ILLNESS_RECORD,HEART_RATE,BREATH,ORDER_DESC,&
		   OPT_USER, OPT_DATE, OPT_TERM,ORDER_CODE,MEDI_QTY,ROUTE_CODE,OPBOOK_SEQ,MEDI_UNIT,ORDER_NO,ORDER_SEQ) &
		VALUES ( &
		    TO_DATE(<TIME>,'yyyy/MM/dd HH24:mi:ss'),<CASE_NO>,<SEQ_NO>,&
		    <STATION_CODE>,<PRESSURE>,<PAIN_ASSESSMENT>,<OXYGEN_SATURATION>,<OP_DEPT_CODE>,<ILLNESS_RECORD>,<HEART_RATE>,<BREATH>,<ORDER_DESC>,&
		    <OPT_USER>,SYSDATE,<OPT_TERM>,<ORDER_CODE>,<MEDI_QTY>,<ROUTE_CODE>,<OPBOOK_SEQ>,<MEDI_UNIT>,<ORDER_NO>,<ORDER_SEQ> &		    
		)
insertInterData.Debug=N

//更新介入护理平台数据
updateInterData.Type=TSQL
updateInterData.SQL=UPDATE OPE_INTERVENNURPLAT &
			SET TIME=TO_DATE(<TIME>,'yyyy/MM/dd HH24:mi:ss'),PRESSURE=<PRESSURE>,PAIN_ASSESSMENT=<PAIN_ASSESSMENT>,&
			OXYGEN_SATURATION=<OXYGEN_SATURATION>,ILLNESS_RECORD=<ILLNESS_RECORD>,&
			HEART_RATE=<HEART_RATE>,BREATH=<BREATH>,&
			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>&
			WHERE &
			CASE_NO=<CASE_NO> AND SEQ_NO=<SEQ_NO> 
		    	    
		
updateInterData.Debug=N

//删除介入护理平台数据
deleteInterData.Type=TSQL
deleteInterData.SQL=DELETE FROM OPE_INTERVENNURPLAT WHERE TIME=TO_DATE(<TIME>,'yyyy/MM/dd HH24:mi:ss') AND &
			CASE_NO=<CASE_NO> AND SEQ_NO=<SEQ_NO> 
deleteInterData.Debug=N

