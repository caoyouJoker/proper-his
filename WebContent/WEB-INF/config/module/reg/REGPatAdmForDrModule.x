# 
#  Title:ԤԼ�Һ�����module
# 
#  Description:ԤԼ�Һ�����module
# 
#  Copyright: Copyright (c) Javahis 2010
# 
#  author wangl 2010.07.14
#  version 1.0
#
Module.item=updateForUnReg;selVIPDate;insertInfoP;insertInfoV;UPDATE;UPDATE1;UPDATEvip
//�˹Ҹ���(FOR REG)
updateForUnReg.Type=TSQL
updateForUnReg.SQL=UPDATE REG_PATADM &
		      SET REGCAN_USER=<REGCAN_USER>,REGCAN_DATE=<REGCAN_DATE>,ADM_STATUS=<ADM_STATUS> &
		    WHERE CASE_NO=<CASE_NO>
updateForUnReg.Debug=N

selVIPDate.Type=TSQL
selVIPDate.SQL=SELECT A.CLINICROOM_NO,A.QUE_NO,A.QUE_STATUS,A.START_TIME,A.SESSION_CODE,&
		      B.DEPT_CODE,B.DR_CODE,B.CLINICTYPE_CODE &
		 FROM REG_CLINICQUE A, REG_SCHDAY B &
		WHERE A.ADM_DATE = B.ADM_DATE &
		  AND A.ADM_TYPE = B.ADM_TYPE &
		  AND A.SESSION_CODE = B.SESSION_CODE &
		  AND A.CLINICROOM_NO = B.CLINICROOM_NO
selVIPDate.item=ADM_TYPE;VIP_ADM_DATE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE
selVIPDate.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selVIPDate.VIP_ADM_DATE=A.ADM_DATE=<VIP_ADM_DATE>
selVIPDate.VIP_SESSION_CODE=A.SESSION_CODE=<VIP_SESSION_CODE>
selVIPDate.VIP_DEPT_CODE=B.DEPT_CODE=<VIP_DEPT_CODE>
selVIPDate.VIP_DR_CODE=B.DR_CODE=<VIP_DR_CODE>
selVIPDate.Debug=N

//������ͨ�Һ�
//====================pangben modify 20110808 ���� NHI_NO�����񿨺�
insertInfoP.Type=TSQL
insertInfoP.SQL=INSERT INTO REG_PATADM ( &
		CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,&
		REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,CLINICTYPE_CODE,&
		DEPT_CODE,DR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,&
		CTZ1_CODE,ARRIVE_FLG,ADM_REGION,HEAT_FLG,ADM_STATUS,&
		REPORT_STATUS, ERD_LEVEL,OPT_USER,OPT_DATE,OPT_TERM,&
		QUE_NO,NHI_NO&
		,REALDEPT_CODE,REALDR_CODE)VALUES(&
		<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,<ADM_DATE>,&
		<REG_DATE>,<SESSION_CODE>,<CLINICAREA_CODE>,<CLINICROOM_NO>,&
		<CLINICTYPE_CODE>,<DEPT_CODE>,<DR_CODE>,<APPT_CODE>,<VISIT_CODE>,&
		<REGMETHOD_CODE>,<CTZ1_CODE>,<ARRIVE_FLG>,<ADM_REGION>,<HEAT_FLG>,&
		<ADM_STATUS>,<REPORT_STATUS>,<ERD_LEVEL>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,&
		<QUE_NO>,<NHI_NO>,<REALDEPT_CODE>,<REALDR_CODE>)
insertInfoP.Debug=Y

//����VIP�Һ�
//====================pangben modify 20110808 ���� NHI_NO�����񿨺�
insertInfoV.Type=TSQL
insertInfoV.SQL=INSERT INTO REG_PATADM (CASE_NO,ADM_TYPE,MR_NO,REGION_CODE,ADM_DATE,REG_DATE,SESSION_CODE,CLINICAREA_CODE,CLINICROOM_NO,CLINICTYPE_CODE,DEPT_CODE,DR_CODE,APPT_CODE,VISIT_CODE,REGMETHOD_CODE,CTZ1_CODE,ARRIVE_FLG,ADM_REGION,HEAT_FLG,ADM_STATUS,REPORT_STATUS, ERD_LEVEL,OPT_USER,OPT_DATE,OPT_TERM,VIP_FLG,REG_ADM_TIME,QUE_NO,NHI_NO,REALDEPT_CODE,REALDR_CODE)VALUES(<CASE_NO>,<ADM_TYPE>,<MR_NO>,<REGION_CODE>,<ADM_DATE>,<REG_DATE>,<SESSION_CODE>,<CLINICAREA_CODE>,<CLINICROOM_NO>,<CLINICTYPE_CODE>,<DEPT_CODE>,<DR_CODE>,<APPT_CODE>,<VISIT_CODE>,<REGMETHOD_CODE>,<CTZ1_CODE>,<ARRIVE_FLG>,<ADM_REGION>,<HEAT_FLG>,<ADM_STATUS>,<REPORT_STATUS>,<ERD_LEVEL>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<VIP_FLG>,<REG_ADM_TIME>,<QUE_NO>,<NHI_NO>,<REALDEPT_CODE>,<REALDR_CODE>)
insertInfoV.Debug=Y

//�޸����
UPDATE1.Type=TSQL
UPDATE1.SQL=UPDATE  REG_SCHDAY SET QUE_NO=<QUE_NO> WHERE ADM_DATE=<ADM_DATE> AND DEPT_CODE=<DEPT_CODE> AND DR_CODE=<DR_CODE>
UPDATE1.Debug=N

//�޸�ռ��
UPDATE.Type=TSQL
UPDATE.SQL=UPDATE  REG_CLINICQUE SET QUE_STATUS='Y' WHERE START_TIME=<REG_ADM_TIME> AND CLINICROOM_NO=<CLINICROOM_NO>
UPDATE.Debug=N

//�޸�ռ��  
//==========add by huangtt 20131128
UPDATEvip.Type=TSQL
UPDATEvip.SQL=UPDATE  REG_CLINICQUE SET QUE_STATUS='Y' WHERE QUE_NO=<QUE_NO> AND CLINICROOM_NO=<CLINICROOM_NO> AND SESSION_CODE=<SESSION_CODE> AND ADM_DATE=<ADM_DATE> AND ADM_TYPE=<ADM_TYPE>
UPDATEvip.Debug=N


