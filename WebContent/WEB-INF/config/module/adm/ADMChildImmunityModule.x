##################################################
# <p>Title:סԺ��־ </p>
#
# <p>Description:סԺ��־ </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-10-29
# @version 4.0
##################################################
Module.item=selectData;insertData;updateData;checkNEW_BORN_FLG;checkM_CASE_NO

//��ѯ������������Ϣ
selectData.Type=TSQL
selectData.SQL=SELECT &
		   CASE_NO, MR_NO, IPD_NO, APGAR_NUMBER, BABY_VACCINE_FLG, &
		   LIVER_VACCINE_FLG, TSH_FLG, PKU_FLG, OPT_USER, OPT_TERM, &
		   OPT_DATE &
		FROM ADM_CHILD_IMMUNITY
selectData.item=CASE_NO
selectData.CASE_NO=CASE_NO=<CASE_NO>
selectData.Debug=N

//��������
insertData.Type=TSQL
insertData.SQL=INSERT INTO ADM_CHILD_IMMUNITY ( &
		CASE_NO, MR_NO, IPD_NO, &
		APGAR_NUMBER, BABY_VACCINE_FLG, LIVER_VACCINE_FLG, &
		TSH_FLG, PKU_FLG, OPT_USER, &
		OPT_TERM, OPT_DATE &
		) VALUES ( &
		<CASE_NO>, <MR_NO>, <IPD_NO>, &
		<APGAR_NUMBER>, <BABY_VACCINE_FLG>, <LIVER_VACCINE_FLG>, &
		<TSH_FLG>, <PKU_FLG>, <OPT_USER>, &
		<OPT_TERM>, SYSDATE &
		)
insertData.Debug=N

//�޸�����
updateData.Type=TSQL
updateData.SQL=UPDATE ADM_CHILD_IMMUNITY SET &
			APGAR_NUMBER=<APGAR_NUMBER>,&
			BABY_VACCINE_FLG=<BABY_VACCINE_FLG>,&
			LIVER_VACCINE_FLG=<LIVER_VACCINE_FLG>,&
			TSH_FLG=<TSH_FLG>,&
			PKU_FLG=<PKU_FLG>,&
			OPT_USER=<OPT_USER>,&
			OPT_TERM=<OPT_TERM>,&
			OPT_DATE=SYSDATE &
			WHERE CASE_NO=<CASE_NO>
updateData.Debug=N

//��鲡���Ƿ���������
checkNEW_BORN_FLG.Type=TSQL
checkNEW_BORN_FLG.SQL=SELECT NEW_BORN_FLG FROM ADM_INP WHERE CASE_NO=<CASE_NO>
checkNEW_BORN_FLG.Debug=N

//��鲡���Ƿ���ĸ��
checkM_CASE_NO.Type=TSQL
checkM_CASE_NO.SQL=SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO=<M_CASE_NO>
checkM_CASE_NO.Debug=N