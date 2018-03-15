# 
#  Title: STA_IN_07ҽԺ�����������
# 
#  Description: STA_IN_07ҽԺ�����������
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.24
#  version 1.0
#
Module.item=selectDATA_01;selectDATA_02;selectDATA_03;selectDATA_07;selectDATA_08;selectDATA_09;selectDATA_12;selectDATA_13;selectDATA_14;selectDATA_26;deleteSTA_IN_07;insertSTA_IN_07;selectSTA_IN_07;updateSTA_IN_07

//��Ժ�������˴���
selectDATA_01.Type=TSQL
selectDATA_01.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.OUT_DEPT
selectDATA_01.item=OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_01.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_01.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_01.Debug=N

//��Ժ����������
selectDATA_02.Type=TSQL
selectDATA_02.SQL=SELECT COUNT(A.CASE_NO) AS NUM,A.OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			GROUP BY A.OUT_DEPT
selectDATA_02.item=OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_02.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_02.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_02.Debug=N

//--����ռ���գ���ǰռ���գ�����ƽ��סԺ�գ���ǰƽ��סԺ��
selectDATA_03.Type=TSQL
selectDATA_03.SQL=SELECT TRUNC(SUM(CASE WHEN (OUT_DATE - OP_DATE)>0 THEN (OUT_DATE - OP_DATE) ELSE 0 END)) AS DATA_03,TRUNC(SUM(CASE WHEN (OP_DATE -IN_DATE)>0 THEN OP_DATE -IN_DATE ELSE 0 END)) AS DATA_04, &
			TRUNC(AVG(CASE WHEN (OUT_DATE - OP_DATE)>0 THEN (OUT_DATE - OP_DATE) ELSE 0 END)) AS DATA_05,TRUNC(AVG(CASE WHEN (OP_DATE -IN_DATE)>0 THEN OP_DATE -IN_DATE ELSE 0 END)) AS DATA_06,OUT_DEPT &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND OP_DATE IS NOT NULL &
			GROUP BY OUT_DEPT
selectDATA_03.item=OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_03.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_03.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_03.Debug=N

//�޾�������������
selectDATA_07.Type=TSQL
selectDATA_07.SQL=SELECT COUNT(DISTINCT(A.CASE_NO)) AS NUM,A.OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND B.HEALTH_LEVEL IN ('11','12','13') &
			GROUP BY A.OUT_DEPT
selectDATA_07.item=OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_07.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_07.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_07.Debug=N

//�޾��������׼�����������
selectDATA_08.Type=TSQL
selectDATA_08.SQL=SELECT COUNT(DISTINCT(A.CASE_NO)) AS NUM,A.OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND B.HEALTH_LEVEL = '11' &
			GROUP BY A.OUT_DEPT
selectDATA_08.item=DEPT_CODE;OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_08.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_08.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_08.Debug=N

//�޾�����,�пڻ�ŧ��
selectDATA_09.Type=TSQL
selectDATA_09.SQL=SELECT COUNT(DISTINCT(A.CASE_NO)) AS NUM,A.OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND B.HEALTH_LEVEL = '13' &
			GROUP BY A.OUT_DEPT
selectDATA_09.item=OUT_STATION;OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_09.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_09.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_09.Debug=N

//��������������������
selectDATA_12.Type=TSQL
selectDATA_12.SQL=SELECT COUNT(CASE_NO) AS NUM,OUT_DEPT &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND CODE1_STATUS ='4' &
			AND TO_CHAR(OP_DATE,'YYYYMMDD') = TO_CHAR(OUT_DATE,'YYYYMMDD') &
			GROUP BY OUT_DEPT
selectDATA_12.item=OUT_STATION;OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_12.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_12.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_12.Debug=N

//��������������ʮ��������
selectDATA_13.Type=TSQL
selectDATA_13.SQL=SELECT COUNT(CASE_NO) AS NUM,OUT_DEPT &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND CODE1_STATUS ='4' &
			And OUT_DATE- OP_DATE <10 &
			GROUP BY OUT_STATION,OUT_DEPT
selectDATA_13.item=OUT_DEPT;OUT_STATION;REGION_CODE
//============pangben modify 20110525 start
selectDATA_13.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_13.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_13.Debug=N

//��������������������
selectDATA_14.Type=TSQL
selectDATA_14.SQL=SELECT COUNT(CASE_NO) AS NUM,OUT_DEPT &
			FROM MRO_RECORD &
			WHERE OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND CODE1_STATUS ='4' &
			And OP_CODE IS NOT NULL &
			GROUP BY OUT_STATION,OUT_DEPT
selectDATA_14.item=OUT_STATION;OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_14.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_14.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_14.Debug=N

//�����˴Σ�סԺ�о�
selectDATA_26.Type=TSQL
selectDATA_26.SQL=SELECT COUNT(DISTINCT(A.CASE_NO)) AS NUM,OUT_DEPT &
			FROM MRO_RECORD A,MRO_RECORD_OP B &
			WHERE A.CASE_NO = B.CASE_NO &
			AND A.MR_NO = B.MR_NO &
			AND B.MAIN_FLG='Y' &
			AND A.OUT_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
			AND B.HEALTH_LEVEL NOT IN ('11','12','13') &
			GROUP BY OUT_STATION,OUT_DEPT
selectDATA_26.item=OUT_STATION;OUT_DEPT;REGION_CODE
//============pangben modify 20110525 start
selectDATA_26.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectDATA_26.OUT_DEPT=OUT_DEPT=<OUT_DEPT>
selectDATA_26.Debug=N

//ɾ����STA_IN_07����
//============pangben modify 20110525 ������������
deleteSTA_IN_07.Type=TSQL
deleteSTA_IN_07.SQL=DELETE FROM STA_IN_07 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_IN_07.Debug=N

//����STA_IN_07������
//============pangben modify 20110525 ����������
insertSTA_IN_07.Type=TSQL
insertSTA_IN_07.SQL=INSERT INTO STA_IN_07 ( &
			STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,DATA_25,DATA_26,DATA_27, &
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM,REGION_CODE &
		) VALUES ( &
			<STA_DATE>,<DEPT_CODE>,<STATION_CODE>,<DATA_01>,<DATA_02>,<DATA_03>,&
			<DATA_04>,<DATA_05>,<DATA_06>,<DATA_07>,<DATA_08>,&
			<DATA_09>,<DATA_10>,<DATA_11>,<DATA_12>,<DATA_13>,&
			<DATA_14>,<DATA_15>,<DATA_16>,<DATA_17>,<DATA_18>,&
			<DATA_19>,<DATA_20>,<DATA_21>,<DATA_22>,<DATA_23>,&
			<DATA_24>,<DATA_25>,<DATA_26>,<DATA_27>,&
			<CONFIRM_FLG>,<CONFIRM_USER>,<CONFIRM_DATE>,<OPT_USER>,&
			SYSDATE,<OPT_TERM>,<REGION_CODE> &
		)
insertSTA_IN_07.Debug=N

//��ѯSTA_IN_07������
selectSTA_IN_07.Type=TSQL
selectSTA_IN_07.SQL=SELECT STA_DATE,DEPT_CODE,STATION_CODE,DATA_01,DATA_02,DATA_03,&
			DATA_04,DATA_05,DATA_06,DATA_07,DATA_08,&
			DATA_09,DATA_10,DATA_11,DATA_12,DATA_13,&
			DATA_14,DATA_15,DATA_16,DATA_17,DATA_18,&
			DATA_19,DATA_20,DATA_21,DATA_22,DATA_23,&
			DATA_24,DATA_25,DATA_26,DATA_27, &
			CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER,&
			OPT_DATE,OPT_TERM &
			FROM STA_IN_07
selectSTA_IN_07.item=STA_DATE;REGION_CODE
selectSTA_IN_07.STA_DATE=STA_DATE=<STA_DATE>
//============pangben modify 20110525 start
selectSTA_IN_07.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110525 stop
selectSTA_IN_07.Debug=N
//�޸�STA_IN_07������
updateSTA_IN_07.Type=TSQL
updateSTA_IN_07.SQL=UPDATE STA_IN_07 SET &
DATA_01=<DATA_01>, DATA_02=<DATA_02>, DATA_03=<DATA_03>, DATA_04=<DATA_04>, DATA_05=<DATA_05>, &
DATA_06=<DATA_06>, DATA_07=<DATA_07>, DATA_08=<DATA_08>, DATA_09=<DATA_09>, &
DATA_10=<DATA_10>, DATA_11=<DATA_11>, DATA_12=<DATA_12>, DATA_13=<DATA_13>, DATA_14=<DATA_14>, &
DATA_15=<DATA_15>, DATA_16=<DATA_16>, DATA_17=<DATA_17>, DATA_18=<DATA_18>, DATA_19=<DATA_19>, DATA_20=<DATA_20>, &
DATA_21=<DATA_21>, DATA_22=<DATA_22>, DATA_23=<DATA_23>, DATA_24=<DATA_24>, DATA_25=<DATA_25>, DATA_26=<DATA_26>, DATA_27=<DATA_27>,  &
OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>,CONFIRM_FLG=<CONFIRM_FLG>,CONFIRM_USER=<CONFIRM_USER>,CONFIRM_DATE=<CONFIRM_DATE> &
WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE> AND DEPT_CODE=<DEPT_CODE> AND STATION_CODE=<STATION_CODE>
updateSTA_IN_07.Debug=N