# 
#  Title:预约未结案module
# 
#  Description:预约未结案module
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-11-27
#  version 1.0
#

Module.item=queryResvNClose

//查询ADM_RESV数据
//预约单号/病案号/姓名/预约日期/预约住院日/身分证/科别/主要诊断
queryResvNClose=TSQL
queryResvNClose.SQL=SELECT E.REGION_CODE,A.RESV_NO,A.MR_NO,B.PAT_NAME,A.APP_DATE,A.RESV_DATE,&
           A.BED_CLASS_CODE,A.BED_NO,A.SEX_CODE,ADM_SOURCE,&
           C.SEX_DESC,A.DEPT_CODE,A.IN_DATE,&
           A.DR_CODE,A.ADM_DAYS,&
           A.URG_FLG,A.DIAG_CODE,D.ICD_CHN_DESC,&
           A.OPER_DATE,A.OPER_DESC,A.TEL,&
           A.TEL_NO1,A.CAN_REASON_CODE,A.CAN_CLERK,A.IN_CASE_NO,&
           A.IN_BED_NO,B.BIRTH_DATE, &
           G.ADM_TYPE,G.HOSP_CLS_CODE,&
           G.DRG_CODE, &
           A.CTZ1_CODE,A.DEPOSIT_LIMIT,&
           A.PATIENT_CONDITION,A.DRUG_FLG,A.SEPERATE_FLG,&
           A.TRANSPORT_FLG,A.SHOWER_FLG,A.DIET_FLG,A.COMMON,&
           B.IDNO,A.CLNCPATH_CODE,F.CLNCPATH_DESC,A.IN_STATION_CODE &
           FROM ADM_RESV A,SYS_PATINFO B,SYS_SEX C, SYS_DIAGNOSIS D,SYS_DEPT E ,&
           CLP_BSCINFO F,DRG_DRG G &
           WHERE A.APP_DATE BETWEEN TO_DATE (<START_DATE>, 'YYYYMMDD') &
		      	AND TO_DATE (<END_DATE>, 'YYYYMMDD') AND &
          A.CAN_DATE IS NULL AND &
          (A.IN_CASE_NO='' OR A.IN_CASE_NO IS NULL) AND &
          B.MR_NO=A.MR_NO AND &
          A.SEX_CODE(+)=C.SEX_CODE AND &
          D.ICD_CODE(+)=A.DIAG_CODE AND &
          G.DRG_CODE(+)=A.DRG_CODE AND &
          E.DEPT_CODE(+)=A.DEPT_CODE &
           AND F.CLNCPATH_CODE (+)= A.CLNCPATH_CODE &
           ORDER BY A.URG_FLG,A.RESV_NO 
queryResvNClose.item=DR_CODE;DEPT_CODE;REGION_CODE
queryResvNClose.DR_CODE=DR_CODE=<DR_CODE>
queryResvNClose.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
queryResvNClose.REGION_CODE=REGION_CODE=<REGION_CODE>
queryResvNClose.Debug=N