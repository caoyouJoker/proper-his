# 
#  Title:�ٴ��о���ĿSQL
# 
#  Description:�ٴ��о���Ŀmodule
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2016.05.19
#  version 1.0
#
Module.item=insert;update;delete;query

//����һ���µ��ٴ��о�
insert.Type=TSQL
insert.SQL=INSERT INTO SYS_CLINICALSORT (CLINICAL_CODE, CLINICAL_DESC, PY1, PY2, DESCRIPTION, SEQ, OPT_USER,OPT_DATE, OPT_TERM) &
		VALUES (  &
		<CLINICAL_CODE>, <CLINICAL_DESC>, <PY1>, <PY2>, <DESCRIPTION>, <SEQ>, <OPT_USER> , & 
		<OPT_DATE>, <OPT_TERM>)
insert.Debug=Y

//����һ������
update.Type=TSQL
update.SQL=UPDATE SYS_CLINICALSORT  & 
           SET PY1=<PY1>, PY2=<PY2>, DESCRIPTION=<DESCRIPTION>, SEQ=<SEQ> WHERE CLINICAL_CODE= <CLINICAL_CODE>
update.Debug=Y

//ɾ��һ������
delete.Type=TSQL
delete.SQL=DELETE FROM SYS_CLINICALSORT WHERE CLINICAL_CODE=<CLINICAL_CODE>
delete.Debug=Y

//���в�ѯ
query.Type=TSQL
query.SQL=SELECT CLINICAL_CODE, CLINICAL_DESC, PY1, PY2, DESCRIPTION, SEQ, OPT_USER,OPT_DATE, OPT_TERM &
	   FROM SYS_CLINICALSORT    ORDER BY SEQ
query.item=CLINICAL_CODE;CLINICAL_DESC;PY1;PY2;DESCRIPTION
query.CLINICAL_CODE= CLINICAL_CODE LIKE <CLINICAL_CODE>
query.CLINICAL_DESC= CLINICAL_DESC LIKE <CLINICAL_DESC>
query.PY1= PY1 LIKE <PY1>
query.PY2= PY2 LIKE <PY2>
query.DESCRIPTION = DESCRIPTION LIKE <DESCRIPTION>
query.Debug=Y



