################################################
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
################################################
Module.item=selectInHosp;selectOutHosp;selectDead;selectOUPR;selectINPR;selectHave

//��ѯ��Ժ������Ϣ
selectInHosp.Type=TSQL
selectInHosp.SQL=SELECT F.REGION_CHN_DESC,B.BED_NO_DESC,C.PAT_NAME,E.CHN_DESC,A.IPD_NO,D.ICD_CHN_DESC &
			FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,SYS_REGION F &
			WHERE A.BED_NO = B.BED_NO &
			AND A.MR_NO = C.MR_NO &
			AND A.REGION_CODE = F.REGION_CODE(+) &
			AND A.MAINDIAG = D.ICD_CODE(+) &
			AND C.SEX_CODE = E.ID(+) &
			AND E.GROUP_ID='SYS_SEX' &
			AND A.CANCEL_FLG <> 'Y' &
			AND A.IN_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS')
selectInHosp.item=IN_DEPT_CODE;IN_STATION_CODE;REGION_CODE
selectInHosp.IN_DEPT_CODE=A.IN_DEPT_CODE = <IN_DEPT_CODE>
selectInHosp.IN_STATION_CODE=A.IN_STATION_CODE = <IN_STATION_CODE>
//========pangben modify 20110510 �����������
selectInHosp.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectInHosp.Debug=N

//��ѯ��Ժ������Ϣ
selectOutHosp.Type=TSQL
selectOutHosp.SQL=SELECT F.REGION_CHN_DESC,B.BED_NO_DESC,C.PAT_NAME,E.CHN_DESC,A.IPD_NO,D.ICD_CHN_DESC &
			FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,SYS_REGION F &
			WHERE A.BED_NO = B.BED_NO &
			AND A.MR_NO = C.MR_NO &
			AND A.MAINDIAG = D.ICD_CODE(+) &
			AND A.REGION_CODE = F.REGION_CODE(+) &
			AND C.SEX_CODE = E.ID(+) &
			AND E.GROUP_ID='SYS_SEX' &
			AND A.CANCEL_FLG <> 'Y' &
			AND A.DS_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS')
selectOutHosp.item=DS_DEPT_CODE;DS_STATION_CODE;REGION_CODE
selectOutHosp.DS_DEPT_CODE=A.DS_DEPT_CODE = <DS_DEPT_CODE>
selectOutHosp.DS_STATION_CODE=A.DS_STATION_CODE = <DS_STATION_CODE>
//========pangben modify 20110510 �����������
selectOutHosp.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectOutHosp.Debug=N

//��ѯ ����������Ϣ
selectDead.Type=TSQL
selectDead.SQL=SELECT F.REGION_CHN_DESC,B.BED_NO_DESC,C.PAT_NAME,E.CHN_DESC,A.IPD_NO,D.ICD_CHN_DESC &
			FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,SYS_REGION F &
			WHERE A.BED_NO = B.BED_NO &
			AND A.MR_NO = C.MR_NO &
			AND A.MAINDIAG = D.ICD_CODE(+) &
			AND A.REGION_CODE = F.REGION_CODE(+) &
			AND C.SEX_CODE = E.ID(+) &
			AND E.GROUP_ID='SYS_SEX' &
			AND A.CANCEL_FLG <> 'Y' &
			AND A.DISCH_CODE='4' &
			AND A.DS_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS')
selectDead.item=DS_DEPT_CODE;DS_STATION_CODE;REGION_CODE
selectDead.DS_DEPT_CODE=A.DS_DEPT_CODE = <DS_DEPT_CODE>
selectDead.DS_STATION_CODE=A.DS_STATION_CODE = <DS_STATION_CODE>
//========pangben modify 20110510 �����������
selectDead.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectDead.Debug=N

//��ѯת�벡����Ϣ
selectINPR.Type=TSQL
selectINPR.SQL=SELECT J.REGION_CHN_DESC,B.BED_NO_DESC,C.PAT_NAME,E.CHN_DESC,A.IPD_NO,I.DEPT_CHN_DESC,D.ICD_CHN_DESC &
		FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,ADM_CHG F,ADM_CHG H,SYS_DEPT I,SYS_REGION J &
		WHERE A.BED_NO = B.BED_NO &
		AND A.MR_NO = C.MR_NO &
		AND A.MAINDIAG = D.ICD_CODE(+) &
		AND C.SEX_CODE = E.ID(+) &
		AND E.GROUP_ID='SYS_SEX' &
		AND A.REGION_CODE = J.REGION_CODE(+) &
		AND A.CANCEL_FLG <> 'Y' &
		AND A.CASE_NO=F.CASE_NO &
		AND A.CASE_NO=H.CASE_NO &
		AND F.PSF_KIND='OUPR' &
		AND H.PSF_KIND='INPR' &
		AND F.CHG_DATE=H.CHG_DATE &
		AND F.DEPT_CODE = I.DEPT_CODE &
		AND H.CHG_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS')
selectINPR.item=DEPT_CODE;STATION_CODE;REGION_CODE
selectINPR.DEPT_CODE=H.DEPT_CODE = <DEPT_CODE>
selectINPR.STATION_CODE=H.STATION_CODE = <STATION_CODE>
//========pangben modify 20110510 �����������
selectINPR.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectINPR.Debug=N

//��ѯת��������Ϣ
selectOUPR.Type=TSQL
selectOUPR.SQL=SELECT J.REGION_CHN_DESC,B.BED_NO_DESC,C.PAT_NAME,E.CHN_DESC,A.IPD_NO,I.DEPT_CHN_DESC,D.ICD_CHN_DESC &
		FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,SYS_DICTIONARY E,ADM_CHG F,ADM_CHG H,SYS_DEPT I,SYS_REGION J &
		WHERE A.BED_NO = B.BED_NO &
		AND A.MR_NO = C.MR_NO &
		AND A.MAINDIAG = D.ICD_CODE(+) &
		AND C.SEX_CODE = E.ID(+) &
		AND A.REGION_CODE = J.REGION_CODE(+) &
		AND E.GROUP_ID='SYS_SEX' &
		AND A.CANCEL_FLG <> 'Y' &
		AND A.CASE_NO=F.CASE_NO &
		AND A.CASE_NO=H.CASE_NO &
		AND F.PSF_KIND='OUPR' &
		AND H.PSF_KIND='INPR' &
		AND F.CHG_DATE=H.CHG_DATE &
		AND H.DEPT_CODE = I.DEPT_CODE &
		AND F.CHG_DATE BETWEEN TO_DATE(<DATE>,'YYYYMMDD') AND TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS')
selectOUPR.item=DEPT_CODE;STATION_CODE;REGION_CODE
selectOUPR.DEPT_CODE=F.DEPT_CODE = <DEPT_CODE>
selectOUPR.STATION_CODE=F.STATION_CODE = <STATION_CODE>
//========pangben modify 20110510 �����������
selectOUPR.REGION_CODE=A.REGION_CODE = <REGION_CODE>
selectOUPR.Debug=N

//��ѯĳһ���ʵ�в�����
selectHave.Type=TSQL
selectHave.SQL=SELECT COUNT(CASE_NO) AS NUM &
		FROM ADM_INP &
		WHERE CANCEL_FLG<>'Y' &
		AND IN_DATE <= TO_DATE(<DATE>||'235959','YYYYMMDDHH24MISS') AND (DS_DATE IS NULL &
		OR DS_DATE >= TO_DATE(<DATE>,'YYYYMMDD'))
selectHave.item=DEPT_CODE;STATION_CDOE;REGION_CODE
selectHave.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectHave.STATION_CDOE=STATION_CDOE=<STATION_CDOE>
//========pangben modify 20110510 �����������
selectHave.REGION_CODE=REGION_CODE = <REGION_CODE>
selectHave.Debug=N