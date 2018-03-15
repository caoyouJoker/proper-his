Module.item=selectall;selectdatawithkeys;deletedata;insertdata;updatedata;initlimitdeptcode;selectdatawithcode;selectdatawithtype;initcombo

//���� ICD_CODE ,ICD_TYPE ��ѯ
selectdatawithkeys.Type=TSQL
selectdatawithkeys.SQL=SELECT ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,CCMD_CODE,LIMIT_DEPT_CODE,MAIN_DIAG_FLG,LIMIT_SEX_CODE,STANDARD_STAY_DAYS,CHRONIC_FLG,CHLR_FLG,DISEASETYPE_CODE,START_AGE,END_AGE,AVERAGE_FEE,CAT_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_DIAGNOSIS WHERE ICD_CODE=<ICD_CODE> AND ICD_TYPE=<ICD_TYPE> ORDER BY ICD_TYPE DESC,ICD_CODE, SEQ
selectdatawithkeys.Debug=N

//combo��
initcombo.Type=TSQL
initcombo.SQL=SELECT ICD_CODE AS ID,ICD_CHN_DESC AS TEXT,ICD_ENG_DESC AS NAME,ICD_ENG_DESC AS ENNAME,PY1,PY2,SEQ &
		FROM SYS_DIAGNOSIS ORDER BY ICD_TYPE,ICD_CODE,SEQ
initcombo.item=ICD_TYPE
initcombo.ICD_TYPE=ICD_TYPE=<ICD_TYPE>
initcombo.Debug=N

//���� ICD_CODE  ��ѯ
selectdatawithcode.Type=TSQL
selectdatawithcode.SQL=SELECT ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,CCMD_CODE,LIMIT_DEPT_CODE,MAIN_DIAG_FLG,LIMIT_SEX_CODE,STANDARD_DAYS,CHRONIC_FLG,CHLR_FLG,DISEASETYPE_CODE,START_AGE,END_AGE,AVERAGE_FEE,CAT_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_DIAGNOSIS WHERE ICD_CODE=<ICD_CODE>  ORDER BY ICD_TYPE DESC,ICD_CODE, SEQ
selectdatawithcode.Debug=N

//���� ICD_TYPE ��ѯ
selectdatawithtype.Type=TSQL
selectdatawithtype.SQL=SELECT ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,CCMD_CODE,LIMIT_DEPT_CODE,MAIN_DIAG_FLG,LIMIT_SEX_CODE,STANDARD_DAYS,CHRONIC_FLG,CHLR_FLG,DISEASETYPE_CODE,START_AGE,END_AGE,AVERAGE_FEE,CAT_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_DIAGNOSIS WHERE  ICD_TYPE=<ICD_TYPE> ORDER BY ICD_TYPE DESC,ICD_CODE, SEQ
selectdatawithtype.Debug=N


//���� ICD_CODE ɾ������
deletedata.Type=TSQL
deletedata.SQL=DELETE SYS_DIAGNOSIS  WHERE ICD_CODE = <ICD_CODE> AND ICD_TYPE=<ICD_TYPE>
deletedata.Debug=N

//���� ICD_CODE ,ICD_TYPE ��������
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_DIAGNOSIS SET ICD_TYPE=<NEWICD_TYPE>, ICD_CHN_DESC=<ICD_CHN_DESC>,ICD_ENG_DESC=<ICD_ENG_DESC>,PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,SYNDROME_FLG=<SYNDROME_FLG>,MDC_CODE=<MDC_CODE>,CCMD_CODE=<CCMD_CODE>,LIMIT_DEPT_CODE=<LIMIT_DEPT_CODE>,MAIN_DIAG_FLG=<MAIN_DIAG_FLG>,LIMIT_SEX_CODE=<LIMIT_SEX_CODE>,STANDARD_STAY_DAYS=<STANDARD_STAY_DAYS>,CHRONIC_FLG=<CHRONIC_FLG>,CHLR_FLG=<CHLR_FLG>,DISEASETYPE_CODE=<DISEASETYPE_CODE>,START_AGE=<START_AGE>,END_AGE=<END_AGE>,AVERAGE_FEE=<AVERAGE_FEE>,CAT_FLG=<CAT_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE ICD_CODE=<ICD_CODE> AND ICD_TYPE=<ICD_TYPE>
updatedata.Debug=N

//���뼲�������
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_DIAGNOSIS VALUES(<ICD_CODE>,<ICD_TYPE>,<ICD_CHN_DESC>,<ICD_ENG_DESC>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,<SYNDROME_FLG>,<MDC_CODE>,<CCMD_CODE>,<LIMIT_DEPT_CODE>,<MAIN_DIAG_FLG>,<LIMIT_SEX_CODE>,<STANDARD_STAY_DAYS>,<CHRONIC_FLG>,<CHLR_FLG>,<DISEASETYPE_CODE>,<START_AGE>,<END_AGE>,<AVERAGE_FEE>,<CAT_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//��ѯ���������
selectall.Type=TSQL
selectall.SQL=SELECT ICD_CODE,ICD_TYPE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,CCMD_CODE,LIMIT_DEPT_CODE,MAIN_DIAG_FLG,LIMIT_SEX_CODE,STANDARD_STAY_DAYS,CHRONIC_FLG,CHLR_FLG,DISEASETYPE_CODE,START_AGE,END_AGE,AVERAGE_FEE,CAT_FLG,OPT_USER,OPT_DATE,OPT_TERM FROM SYS_DIAGNOSIS ORDER BY ICD_TYPE DESC,ICD_CODE, SEQ
selectall.Debug=N

//��ʼ��ר���Ʊ�
initlimitdeptcode.Type=TSQL
initlimitdeptcode.SQL=SELECT DEPT_CODE AS ID, DEPT_ABS_DESC AS NAME FROM SYS_DEPT WHERE CLASSIFY = <CLASSIFY> AND FINAL_FLG = <FINAL_FLG > AND ACTIVE_FLG=<ACTIVE_FLG> ORDER BY SEQ
initlimitdeptcode.Debug=N

//��ʼ���������濨�� todo
