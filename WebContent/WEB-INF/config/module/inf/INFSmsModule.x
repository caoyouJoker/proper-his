Module.item=selectAllData;selectInfWarnData;insertInfWarnData;updateInfWarnData;insertInfSmsData;sendPublishMessage


//查询全字段
selectAllData.Type=TSQL
selectAllData.SQL=SELECT MESSAGE_NO, CASE_NO, MR_NO, HANDLE_INFO, URG_FLG, SEND_INFO, INTERVENT_ID, &
		         INTERVENT_OPTIONS, HANDLE_DATE, SEND_DATE, HANDLE_USER, SEND_USER, MES_TYPE, &
			 ADM_TYPE, TESTITEM_CODE, TEST_VALUE, STATE, VS_DOC_CODE, INF_DIRECTOR &
  		  FROM INF_SMS &
		  WHERE 1 = 1 
selectAllData.item=CASE_NO;TESTITEM_CODE;STATE;MR_NO;STATE;LAB_NO
selectAllData.CASE_NO=CASE_NO=<CASE_NO>
selectAllData.TESTITEM_CODE=TESTITEM_CODE=<TESTITEM_CODE>
selectAllData.MR_NO=MR_NO=<MR_NO>
selectAllData.STATE=STATE=<STATE>
selectAllData.LAB_NO=LAB_NO=<LAB_NO>
selectAllData.Debug=N

//查询记录（传染病自动通知页面）
selectInfWarnData.Type=TSQL
selectInfWarnData.SQL=SELECT A.MESSAGE_NO, A.CASE_NO, A.ADM_TYPE, A.MR_NO, C.PAT_NAME, B.MICRO_NAME AS TESTITEM_DESC, A.TEST_VALUE, &
		      	     A.STATE, A.DEPT_CODE, A.BILL_DOC_CODE, A.INF_DIRECTOR, A.SEND_DATE, A.HANDLE_USER, A.HANDLE_DATE, A.INTERVENT_ID, A.INTERVENT_OPTIONS  &
                      FROM INF_SMS A, EMR_MICRO_CONVERT B, SYS_PATINFO C &
		      WHERE A.TESTITEM_CODE = B.MACRO_CODE AND A.MR_NO = C.MR_NO 

selectInfWarnData.item=START_DATE;END_DATE;DEPT_CODE;STATION_CODE;STATE;MR_NO;MES_TYPE

selectInfWarnData.START_DATE=A.SEND_DATE >= TO_DATE( <START_DATE>, 'YYYYMMDDHH24MISS')
selectInfWarnData.END_DATE=A.SEND_DATE <= TO_DATE( <END_DATE>, 'YYYYMMDDHH24MISS')
selectInfWarnData.DEPT_CODE=A.DEPT_CODE = <DEPT_CODE>
selectInfWarnData.STATION_CODE=A.STATION_CODE = <STATION_CODE>
selectInfWarnData.STATE=A.STATE = <STATE>
selectInfWarnData.MR_NO=A.MR_NO = <MR_NO>
selectInfWarnData.MES_TYPE=A.MES_TYPE = <MES_TYPE>
selectInfWarnData.Debug=N


//插入数据  传染病预警
insertInfWarnData.Type=TSQL
insertInfWarnData.SQL=INSERT INTO INF_SMS ( MESSAGE_NO, CASE_NO, MR_NO, SEND_INFO, SEND_DATE, SEND_USER, MES_TYPE, &
			 		ADM_TYPE, TESTITEM_CODE, TEST_VALUE, STATE, VS_DOC_CODE, INF_DIRECTOR, LAB_NO, BILL_DOC_CODE, DEPT_CODE, STATION_CODE, OPT_USER, OPT_DATE, OPT_TERM )  &
	              VALUES (<MESSAGE_NO>, <CASE_NO>, <MR_NO>, <SEND_INFO>, <SEND_DATE>, <SEND_USER>, <MES_TYPE>, &
			      <ADM_TYPE>, <TESTITEM_CODE>, <TEST_VALUE>, <STATE>, <VS_DOC_CODE>, <INF_DIRECTOR>, <LAB_NO>, <BILL_DOC_CODE>, <DEPT_CODE>, <STATION_CODE>, <OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insertInfWarnData.Debug=N


//更新数据 传染病预警处理
updateInfWarnData.Type=TSQL
updateInfWarnData.SQL=UPDATE INF_SMS SET HANDLE_USER=<HANDLE_USER>, HANDLE_DATE=<HANDLE_DATE>, INTERVENT_ID=<INTERVENT_ID>, INTERVENT_OPTIONS=<INTERVENT_OPTIONS>, STATE=<STATE>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> WHERE MESSAGE_NO = <MESSAGE_NO>
updateInfWarnData.Debug=N


//更新数据 感染消息沟通	
//updateInfSmsData.Type=TSQL
//updateInfSmsData.SQL=UPDATE INF_SMS SET HANDLE_INFO=<HANDLE_INFO>, SEND_INFO=<SEND_INFO>, INTERVENT_ID=<INTERVENT_ID>, INTERVENT_OPTIONS=<INTERVENT_OPTIONS>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> WHERE MESSAGE_NO = <MESSAGE_NO>
//updateInfSmsData.Debug=N


//传入数据  感染消息沟通
insertInfSmsData.Type=TSQL
insertInfSmsData.SQL=INSERT INTO INF_SMS ( MESSAGE_NO, CASE_NO, MR_NO, HANDLE_INFO, SEND_INFO, INTERVENT_ID, INTERVENT_OPTIONS, SEND_DATE, SEND_USER, &
			 		STATE,OPT_USER, OPT_DATE, OPT_TERM )  &
	              VALUES (<MESSAGE_NO>, <CASE_NO>, <MR_NO>,<HANDLE_INFO>, <SEND_INFO>, <INTERVENT_ID>, <INTERVENT_OPTIONS>, <SEND_DATE>, <SEND_USER>, &
			      <STATE>,<OPT_USER>, <OPT_DATE>, <OPT_TERM> )
insertInfSmsData.Debug=N	
