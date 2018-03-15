#
# Title:�ٴ�������ѯSQL
#
# Description:�ٴ���Ŀmodule
#
# Copyright: JavaHis (c) 2016
# @author wukai 2016/05/24

Module.item=queryAll;query;delete

//��ѯ���еĲ�����Ϣ
queryAll.Type=TSQL
queryAll.SQL=SELECT s.PAT_NAME,a.MR_NO,c.CLIPRO_DESC,c.CLIPRO_CODE,c.CLIPRO_CHARGER,a.PATLOGY_DEPT_CODE,&
		a.PATLOGY_DOC_CODE,a.PATLOGY_PRO_DATE,c.CLASSIFY_CODE & 
		FROM ADM_INP a,SYS_PATINFO s,CLP_CLIPROJECT c &
		WHERE a.MR_NO = s.MR_NO AND a.PATLOGY_PRO_CODE = c.CLIPRO_CODE;
queryAll.Debug=N


//����������ѯ
query.Type = TSQL
query.SQL = SELECT m.PAT_NAME,m.MR_NO,m.CLIPRO_DESC,m.CLIPRO_CHARGER,m.PATLOGY_DEPT_CODE,m.PATLOGY_DOC_CODE,m.PATLOGY_PRO_DATE,m.CLASSIFY_CODE &
	  	FROM (SELECT s.PAT_NAME,a.MR_NO,c.CLIPRO_DESC,c.CLIPRO_CHARGER,a.PATLOGY_DEPT_CODE,&
			a.PATLOGY_DOC_CODE,a.PATLOGY_PRO_DATE,c.CLASSIFY_CODE & 
			FROM ADM_INP a,SYS_PATINFO s,CLP_CLIPROJECT c &
			WHERE a.MR_NO = s.MR_NO AND a.PATLOGY_PRO_CODE = c.CLIPRO_CODE ) m   ORDER BY m.MR_NO
query.Item = PATLOGY_DEPT_CODE;PATLOGY_DOC_CODE;MR_NO;CLASSIFY_CODE;START_DATE;END_DATE
query.START_DATE = m.PATLOGY_PRO_DATE >= <START_DATE>
query.END_DATE = m.PATLOGY_PRO_DATE <= <END_DATE>
query.PATLOGY_DEPT_CODE = m.PATLOGY_DEPT_CODE LIKE <PATLOGY_DEPT_CODE>
query.PATLOGY_DOC_CODE = m.PATLOGY_DOC_CODE LIKE <PATLOGY_DOC_CODE>
query.MR_NO = m.MR_NO LIKE <MR_NO>
query.CLASSIFY_CODE = m.CLASSIFY_CODE LIKE <CLASSIFY_CODE>
query.Debug = N

delete.Type = TSQL
delete.SQL = UPDATE ADM_INP SET PATLOGY_PRO_CODE='',PATLOGY_DEPT_CODE='', PATLOGY_DOC_CODE='',PATLOGY_PRO_DATE='', PATLOGY_PRO_REMARK='' &
		WHERE MR_NO=<MR_NO>
delete.Debug = N