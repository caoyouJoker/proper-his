Module.item=query;update;insert;delete

//������Ա��������
update.Type=TSQL
update.SQL=UPDATE INS_PAY_KIND SET PAY_KIND_DESC=<PAY_KIND_DESC>,PAY_KIND_PY=<PAY_KIND_PY>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE NHI_COMPANY=<NHI_COMPANY> AND PAY_KIND_CODE=<PAY_KIND_CODE>
update.Debug=N
//������Ա������������
insert.Type=TSQL
insert.SQL=INSERT INTO INS_PAY_KIND (NHI_COMPANY,PAY_KIND_CODE,PAY_KIND_DESC,PAY_KIND_PY,OPT_USER,OPT_DATE,OPT_TERM)VALUES(<NHI_COMPANY>,<PAY_KIND_CODE>,<PAY_KIND_DESC>,<PAY_KIND_PY>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insert.Debug=N
//ɾ����Ա������������
delete.Type=TSQL
delete.SQL=DELETE INS_PAY_KIND WHERE NHI_COMPANY=<NHI_COMPANY> AND PAY_KIND_CODE=<PAY_KIND_CODE>
delete.Debug=N
//��ѯ��Ա������������
query.Type=TSQL
query.SQL=SELECT NHI_COMPANY,PAY_KIND_CODE,PAY_KIND_DESC,PAY_KIND_PY,OPT_USER,OPT_DATE,OPT_TERM FROM INS_PAY_KIND WHERE NHI_COMPANY LIKE <NHI_COMPANY> AND PAY_KIND_CODE LIKE <PAY_KIND_CODE> AND PAY_KIND_DESC LIKE <PAY_KIND_DESC> AND PAY_KIND_PY LIKE <PAY_KIND_PY>
query.Debug=N
