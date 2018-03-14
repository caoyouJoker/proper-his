#
# Title:ÁÙ´²Â·¾¶-ÆÀ¹ÀÖ´ÐÐ
#
# Description:ÁÙ´²Â·¾¶-ÆÀ¹ÀÖ´ÐÐ
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/05/04
Module.item=selectPatientInfo;selectManagemWithPatientInfo

selectPatientInfo.Type=TSQL
selectPatientInfo.SQL= SELECT A.CASE_NO,A.MR_NO,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.VS_DR_CODE, &
											 S.PAT_NAME &
											 FROM ADM_INP A,SYS_PATINFO S WHERE A.MR_NO=<MR_NO> AND A.MR_NO=S.MR_NO(+) &
											 AND A.REGION_CODE=<REGION_CODE> 
selectPatientInfo.Debug=N

selectManagemWithPatientInfo.Type=TSQL
selectManagemWithPatientInfo.SQL=SELECT B.STATUS,TO_CHAR (B.OPT_DATE, 'YYYYMMDD') AS OPT_DATE,B.OPT_USER, A.BED_NO,&
   															 A.MR_NO, A.CASE_NO,A.VS_DR_CODE,TO_CHAR(A.IN_DATE,'YYYY/MM/DD') AS IN_DATE,TO_CHAR(A.DS_DATE,'YYYY/MM/DD') AS DS_DATE,C.PAT_NAME,C.SEX_CODE,&
   															 (SELECT CHN_DESC  FROM  SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX' AND ID= C.SEX_CODE )AS SEX_DESC, &
   															 B.CLNCPATH_CODE, &
												   			 (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = &
												         (SELECT ICD_CODE FROM ADM_INPDIAG WHERE  CASE_NO = A.CASE_NO  AND IO_TYPE = 'M' AND MAINDIAG_FLG = 'Y'  &
												          UNION SELECT ICD_CODE FROM ADM_INPDIAG WHERE  CASE_NO = A.CASE_NO &
												                AND CASE_NO NOT IN ( SELECT CASE_NO FROM ADM_INPDIAG WHERE  CASE_NO = A.CASE_NO AND IO_TYPE = 'M' &
												                AND MAINDIAG_FLG = 'Y') AND IO_TYPE = 'I' AND MAINDIAG_FLG = 'Y') &
												         ) AS ICD_CHN_DESC &
                                 FROM CLP_MANAGEM B ,ADM_INP A,SYS_PATINFO C  WHERE A.CASE_NO(+)=B.CASE_NO AND B.MR_NO=C.MR_NO(+)
selectManagemWithPatientInfo.ITEM=MR_NO;DEPT_CODE;STATION_CODE;BED_NO;CLNCPATH_CODE;VS_DR_CODE
selectManagemWithPatientInfo.MR_NO=B.MR_NO=<MR_NO>
selectManagemWithPatientInfo.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectManagemWithPatientInfo.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectManagemWithPatientInfo.BED_NO=A.BED_NO LIKE <BED_NO>
selectManagemWithPatientInfo.CLNCPATH_CODE=B.CLNCPATH_CODE=<CLNCPATH_CODE>
selectManagemWithPatientInfo.VS_DR_CODE=A.VS_DR_CODE=<VS_DR_CODE>                    
selectManagemWithPatientInfo.Debug=N

