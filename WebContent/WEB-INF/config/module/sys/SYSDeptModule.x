Module.item=initdeptcode;initregdept;initorgdept;getDescByCode;selectOrgDept;selectDept;selDeptForOprt;selUserDept

//���ҹ���combo
initdeptcode.Type=TSQL
initdeptcode.SQL=SELECT DEPT_CODE AS ID,DEPT_ABS_DESC AS NAME,DEPT_ENG_DESC AS ENNAME,PY1,PY2 &
		   FROM SYS_DEPT &
		  WHERE ACTIVE_FLG='Y' &
	       ORDER BY SEQ
initdeptcode.item=DEPT_GRADE;CLASSIFY;FINAL_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;USER_ID;REGION_CODE_ALL
initdeptcode.DEPT_GRADE=DEPT_GRADE=<DEPT_GRADE>
initdeptcode.CLASSIFY=CLASSIFY=<CLASSIFY>
initdeptcode.FINAL_FLG=FINAL_FLG=<FINAL_FLG>
initdeptcode.OPD_FIT_FLG=OPD_FIT_FLG=<OPD_FIT_FLG>
initdeptcode.EMG_FIT_FLG=EMG_FIT_FLG=<EMG_FIT_FLG>
initdeptcode.IPD_FIT_FLG=IPD_FIT_FLG=<IPD_FIT_FLG>
initdeptcode.HRM_FIT_FLG=HRM_FIT_FLG=<HRM_FIT_FLG>
initdeptcode.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initdeptcode.USER_ID=DEPT_CODE in (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID>)
initdeptcode.Debug=N

//�õ��ż������ÿ�������
initregdept.Type=TSQL
initregdept.SQL=SELECT DISTINCT A.DEPT_CODE AS ID,B.DEPT_ABS_DESC AS NAME,B.DEPT_ENG_DESC AS ENNAME, B.PY1 AS PY1, B.PY2 AS PY2 &
		  FROM REG_SCHDAY A,SYS_DEPT B &
		 WHERE B.ACTIVE_FLG='Y' AND A.DEPT_CODE = B.DEPT_CODE &
	      ORDER BY A.DEPT_CODE
initregdept.item=REGION_CODE;ADM_TYPE;SESSION_CODE;ADM_DATE;REGION_CODE_ALL
initregdept.REGION_CODE=A.REGION_CODE=<REGION_CODE>
initregdept.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
initregdept.SESSION_CODE=A.SESSION_CODE=<SESSION_CODE>
initregdept.ADM_DATE=A.ADM_DATE=<ADM_DATE>
initregdept.REGION_CODE_ALL=B.REGION_CODE=<REGION_CODE_ALL>
initregdept.Debug=N

initorgdept.Type=TSQL
initorgdept.SQL=SELECT A.DEPT_CODE AS ID,A.DEPT_ABS_DESC AS NAME,A.PY1 AS PY1,A.PY2 AS PY2 FROM SYS_DEPT A,IND_ORG B WHERE A.ACTIVE_FLG='Y' ORDER BY A.SEQ
initorgdept.item=DEPT_GRADE;CLASSIFY;FINAL_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;USER_ID;ORG_TYPE;REGION_CODE_ALL
initorgdept.DEPT_GRADE=A.DEPT_GRADE=<DEPT_GRADE>
initorgdept.CLASSIFY=A.CLASSIFY=<CLASSIFY>
initorgdept.FINAL_FLG=A.FINAL_FLG=<FINAL_FLG>
initorgdept.OPD_FIT_FLG=A.OPD_FIT_FLG=<OPD_FIT_FLG>
initorgdept.EMG_FIT_FLG=A.EMG_FIT_FLG=<EMG_FIT_FLG>
initorgdept.IPD_FIT_FLG=A.IPD_FIT_FLG=<IPD_FIT_FLG>
initorgdept.HRM_FIT_FLG=A.HRM_FIT_FLG=<HRM_FIT_FLG>
initorgdept.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initorgdept.ORG_TYPE=B.ORG_CODE=A.DEPT_CODE AND B.ORG_TYPE=<ORG_TYPE>
initorgdept.USER_ID=A.DEPT_CODE in (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID>)
initorgdept.Debug=N

//���ݿ���CODEȡ�ÿ���DESCRIPTION
getDescByCode.Type=TSQL
getDescByCode.SQL=SELECT DEPT_ABS_DESC FROM SYS_DEPT &
		   WHERE DEPT_CODE=<DEPT_CODE>
		   ORDER BY SEQ
getDescByCode.Debug=N

//����ҩ��ҩ������
selectOrgDept.Type=TSQL
selectOrgDept.SQL=SELECT DEPT_CODE,DEPT_CHN_DESC &
                   FROM SYS_DEPT &
		   WHERE CLASSIFY='2'
		   ORDER BY SEQ
selectOrgDept.Debug=N

//����ҩ��ҩ������
selectDept.Type=TSQL
selectDept.SQL=SELECT DEPT_CODE,DEPT_CHN_DESC,DEPT_ABS_DESC,DEPT_ENG_DESC,PY1 &
                   FROM SYS_DEPT
		   ORDER BY SEQ
selectDept.Debug=N

//���ҹ���combo
selDeptForOprt.Type=TSQL
selDeptForOprt.SQL=SELECT DEPT_CODE AS ID,DEPT_ABS_DESC AS NAME,DEPT_ENG_DESC AS ENNAME,PY1,PY2 &
		   FROM SYS_DEPT &
		  WHERE ACTIVE_FLG='Y' AND CLASSIFY IN ('0','1')&
	       ORDER BY SEQ
selDeptForOprt.item=DEPT_GRADE;FINAL_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;USER_ID;REGION_CODE_ALL
selDeptForOprt.DEPT_GRADE=DEPT_GRADE=<DEPT_GRADE>
selDeptForOprt.FINAL_FLG=FINAL_FLG=<FINAL_FLG>
selDeptForOprt.OPD_FIT_FLG=OPD_FIT_FLG=<OPD_FIT_FLG>
selDeptForOprt.EMG_FIT_FLG=EMG_FIT_FLG=<EMG_FIT_FLG>
selDeptForOprt.IPD_FIT_FLG=IPD_FIT_FLG=<IPD_FIT_FLG>
selDeptForOprt.HRM_FIT_FLG=HRM_FIT_FLG=<HRM_FIT_FLG>
selDeptForOprt.USER_ID=DEPT_CODE in (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID>)
selDeptForOprt.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
selDeptForOprt.Debug=N

//��ѯ�ٴ�ҽ������
selUserDept.Type=TSQL
selUserDept.SQL=SELECT DEPT_CODE,DEPT_CHN_DESC,DEPT_ABS_DESC,DEPT_ENG_DESC,PY1,&
		       PY2,SEQ,DESCRIPTION,FINAL_FLG,REGION_CODE,&
		       DEPT_GRADE,CLASSIFY,DEPT_CAT1,OPD_FIT_FLG,EMG_FIT_FLG,&
		       IPD_FIT_FLG,HRM_FIT_FLG,DEFAULT_TERM_NO,DEFAULT_PRINTER_NO,STATISTICS_FLG,&
		       ACTIVE_FLG,OPT_USER,OPT_DATE,OPT_TERM &
		  FROM SYS_DEPT &
		 WHERE ACTIVE_FLG='Y' AND CLASSIFY IN ('0','1') &
		 ORDER BY SEQ
selUserDept.item=DEPT_GRADE;FINAL_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;USER_ID;DEPT_CODE
selUserDept.DEPT_GRADE=DEPT_GRADE=<DEPT_GRADE>
selUserDept.FINAL_FLG=FINAL_FLG=<FINAL_FLG>
selUserDept.OPD_FIT_FLG=OPD_FIT_FLG=<OPD_FIT_FLG>
selUserDept.EMG_FIT_FLG=EMG_FIT_FLG=<EMG_FIT_FLG>
selUserDept.IPD_FIT_FLG=IPD_FIT_FLG=<IPD_FIT_FLG>
selUserDept.HRM_FIT_FLG=HRM_FIT_FLG=<HRM_FIT_FLG>
selUserDept.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selUserDept.USER_ID=DEPT_CODE in (SELECT DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE USER_ID=<USER_ID>)
selUserDept.Debug=N