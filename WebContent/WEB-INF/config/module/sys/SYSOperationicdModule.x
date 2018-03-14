Module.item=selectall;selectdata;deletedata;insertdata;updatedata;existsICD;existsICDCODE;insertTable1Data;updateTable1Data;deleteTable1Data;deleteTable1DataByTagCode

//��ѯҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա����������
//===pangben 2013-9-9 ��ӿ���ҩƷ��������PHA_PREVENCODE
selectdata.Type=TSQL
selectdata.SQL=SELECT  OPERATION_ICD , OPT_CHN_DESC ,  OPT_ENG_DESC  ,PHA_PREVENCODE,  PY1 ,   PY2 ,  SEQ ,  DESCRIPTION , AVG_IPD_DAY,  AVG_OP_FEE,OPE_LEVEL ,STA1_CODE,STA1_DESC, OPT_USER ,OPT_TERM   ,   OPT_DATE   FROM SYS_OPERATIONICD  WHERE OPERATION_ICD = <OPERATION_ICD> ORDER BY SEQ
selectdata.Debug=N


//ɾ��ҽ��ϸ������룬ҽ��ϸ�������˵����ҽ��ϸ����Ⱥ�� �������� ,������Ա���������ڣ� ������ĩ
deletedata.Type=TSQL
deletedata.SQL=DELETE SYS_OPERATIONICD  WHERE OPERATION_ICD = <OPERATION_ICD> 
deletedata.Debug=N

//����ҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա���������ڣ� ������ĩ
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_OPERATIONICD SET SEQ=<SEQ>,OPERATION_ICD=<OPERATION_ICD>,PY1=<PY1>,PY2=<PY2>,OPT_CHN_DESC=<OPT_CHN_DESC> ,OPT_ENG_DESC=<OPT_ENG_DESC> ,DESCRIPTION=<DESCRIPTION>,AVG_IPD_DAY =<AVG_IPD_DAY>,AVG_OP_FEE =<AVG_OP_FEE>,OPE_LEVEL=<OPE_LEVEL>,STA1_CODE=<STA1_CODE>,STA1_DESC=<STA1_DESC>,OPT_USER =<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> ,PHA_PREVENCODE=<PHA_PREVENCODE>,LAPA_FLG=<LAPA_FLG>,OPE_INCISION=<OPE_INCISION> WHERE OPERATION_ICD = <OPERATION_ICD> 
updatedata.Debug=N

//����ҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա���������ڣ� ������ĩ//add caoyong 2013830 PHA_PREVENCODE ��������������  
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_OPERATIONICD(OPERATION_ICD,OPT_CHN_DESC,OPT_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,AVG_IPD_DAY,AVG_OP_FEE,OPE_LEVEL,STA1_CODE,STA1_DESC,OPT_USER,OPT_DATE,OPT_TERM,PHA_PREVENCODE,LAPA_FLG,OPE_INCISION) VALUES( <OPERATION_ICD> ,<OPT_CHN_DESC>,<OPT_ENG_DESC>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,<AVG_IPD_DAY>,<AVG_OP_FEE>,<OPE_LEVEL>,<STA1_CODE>,<STA1_DESC>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<PHA_PREVENCODE>,<LAPA_FLG>,<OPE_INCISION>)
insertdata.Debug=N

//��ѯҽ��ϸ������룬ҽ��ϸ�������˵����ƴ��1��ƴ��2,ҽ��ϸ����Ⱥ�� ��������,��ע��������Ա����������
selectall.Type=TSQL
selectall.SQL=SELECT  OPERATION_ICD , OPT_CHN_DESC ,  OPT_ENG_DESC  ,  PY1 ,   PY2 ,  SEQ ,  DESCRIPTION , AVG_IPD_DAY,  AVG_OP_FEE ,OPE_LEVEL ,STA1_CODE,STA1_DESC,OPT_USER ,PHA_PREVENCODE,OPT_TERM, OPT_DATE,LAPA_FLG,OPE_INCISION    FROM SYS_OPERATIONICD  ORDER BY SEQ 
selectall.Debug=N

//�Ƿ����ҽ��ϸ�������
existsICD.type=TSQL
existsICD.SQL=SELECT COUNT(OPERATION_ICD) AS COUNT FROM SYS_OPERATIONICD WHERE OPERATION_ICD=<OPERATION_ICD>

//�Ƿ����ҽ��ϸ�������
existsICDCODE.type=TSQL
existsICDCODE.SQL=SELECT * FROM SYS_OPERATIONICD_TAGS WHERE ICD_CODE=<ICD_CODE> AND TAG_CODE=<TAG_CODE>
existsICDCODE.Debug=Y

//�����ǩ����
insertTable1Data.Type=TSQL
insertTable1Data.SQL=INSERT INTO SYS_OPERATIONICD_TAGS(ID,ICD_CODE,TAG_CODE,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<ID>,<ICD_CODE>,<TAG_CODE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertTable1Data.Debug=Y

//���±�ǩ����
updateTable1Data.Type=TSQL
updateTable1Data.SQL=UPDATE SYS_OPERATIONICD_TAGS SET TAG_CODE=<TAG_CODE>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE ICD_CODE=<ICD_CODE>
updateTable1Data.Debug=Y

//ɾ����ǩ����
deleteTable1Data.Type=TSQL
deleteTable1Data.SQL=DELETE FROM SYS_OPERATIONICD_TAGS WHERE ICD_CODE=<ICD_CODE>
deleteTable1Data.Debug=Y

//ɾ����ǩ����
deleteTable1DataByTagCode.Type=TSQL
deleteTable1DataByTagCode.SQL=DELETE FROM SYS_OPERATIONICD_TAGS WHERE ICD_CODE=<ICD_CODE> AND TAG_CODE=<TAG_CODE>
deleteTable1DataByTagCode.Debug=Y