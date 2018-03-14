# 
#  Title:诊疗项目备案管理module
# 
#  Description:诊疗项目备案管理module
# 
#  Copyright: Copyright (c) Javahis 2014
# 
#  author zhangs 2014.04.16
#  version 1.0
#
Module.item=deleteINSNeedRegisterItem;insertINSNeedRegisterItemOne;updateINSRegisterItemOne;onCancelInsRegisterItemOne;&
onDeleteInsRegisterItem;onInsertInsRegisterItem;onInsRegisterItemUpdate


//需备案目录信息删除
deleteINSNeedRegisterItem.Type=TSQL
deleteINSNeedRegisterItem.SQL=DELETE FROM INS_NEEDREGISTER_ITEM	     
deleteINSNeedRegisterItem.Debug=N

//需备案目录信息新增
insertINSNeedRegisterItemOne.Type=TSQL
insertINSNeedRegisterItemOne.SQL=INSERT INTO INS_NEEDREGISTER_ITEM &
			   (NHI_CODE,NHI_DESC,CHARGE_CODE,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<NHI_CODE>,<NHI_DESC>,<TJ_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertINSNeedRegisterItemOne.Debug=N

//诊疗项目备案信息下载更新数据
updateINSRegisterItemOne.Type=TSQL
updateINSRegisterItemOne.SQL=UPDATE INS_REGISTERITEM SET &
			   CATEGORY=<CATEGORY>,ITEM_CLASSIFICATION=<ITEM_CLASSIFICATION>,OUTSIDE_FLG=<OUTEXM_FLG>, &
                           MODIFY_PROJECT_REASON=<MODIFY_REASON>,ISVERIFY=<CHECK_FLG>,AUDIT_OPINION=<CHECK_DESC>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE NHI_CODE=<NHI_CODE> AND DEL_FLG='N' AND ADM_TYPE=<ITEM_CLASSIFICATION>
updateINSRegisterItemOne.Debug=N

//诊疗项目备案信息取消更新数据
onCancelInsRegisterItemOne.Type=TSQL
onCancelInsRegisterItemOne.SQL=UPDATE INS_REGISTERITEM SET &
			       ISVERIFY=<ISVERIFY>,UPDATE_FLG=<UPDATE_FLG>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE NHI_CODE=<NHI_CODE> AND DEL_FLG='N' AND ADM_TYPE=<ADM_TYPE>
onCancelInsRegisterItemOne.Debug=N

//诊疗项目备案信息删除更新数据
onDeleteInsRegisterItem.Type=TSQL
onDeleteInsRegisterItem.SQL=UPDATE INS_REGISTERITEM SET &
			       DEL_FLG='Y', &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE NHI_CODE=<NHI_CODE> AND DEL_FLG='N' AND ADM_TYPE=<ADM_TYPE>
onDeleteInsRegisterItem.Debug=N

//诊疗项目备案信息新增
onInsertInsRegisterItem.Type=TSQL
onInsertInsRegisterItem.SQL=INSERT INTO INS_REGISTERITEM &
			  (NHI_CODE,NHI_DESC,ADM_TYPE,MINISTRY_HEALTHNO,CONNOTATION_PROJECT, &
                           UNIT,OWT_AMT,ICD_DESC_LIST,ICD_CODE_LIST,CLINICAL_SIGNIFICANCE, &
                           DEPT_CODE,APPARATUS,REMARK,OUTSIDE_FLG,OUTSIDE_HOSP_CODE, &
                           SPECIAL_CASE,REAGENT,MEDICAL_MATERIALS,ISVERIFY,UPDATE_FLG, &
                           OPT_USER,OPT_DATE,OPT_TERM,DEL_FLG) &
		    VALUES (<NHI_CODE>,<NHI_DESC>,<NHI_TYPE>,<FILE_NO>,<ITEM_DESC>, &
                            <UNIT>,<PRICE>,<ICD_DESC>,<ICD_CODE>,<CLINICAL_DESC>, &
                            <DEPT_CODE>,<DEVICE>,<REMARK>,<OUTEXM_FLG>,<OUTEXM_HOSP_NO>, &
                            <SPECIAL_DESC>,<DRUG>,<MATERIAL>,'4','1', &
                            <OPT_USER>,SYSDATE,<OPT_TERM>,'N')
onInsertInsRegisterItem.Debug=N

//诊疗项目备案信息更新数据
onInsRegisterItemUpdate.Type=TSQL
onInsRegisterItemUpdate.SQL=UPDATE INS_REGISTERITEM SET &
                           NHI_DESC=<NHI_DESC>,ADM_TYPE=<NHI_TYPE>, &
                           MINISTRY_HEALTHNO=<FILE_NO>,CONNOTATION_PROJECT=<ITEM_DESC>, &
                           UNIT=<UNIT>,OWT_AMT=<PRICE>,ICD_DESC_LIST=<ICD_DESC>, & 
                           ICD_CODE_LIST=<ICD_CODE>,CLINICAL_SIGNIFICANCE=<CLINICAL_DESC>, &
                           DEPT_CODE=<DEPT_CODE>,APPARATUS=<DEVICE>,REMARK=<REMARK>, & 
                           OUTSIDE_FLG=<OUTEXM_FLG>,OUTSIDE_HOSP_CODE=<OUTEXM_HOSP_NO>, &
                           SPECIAL_CASE=<SPECIAL_DESC>,REAGENT=<DRUG>, & 
                           MEDICAL_MATERIALS=<MATERIAL>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE NHI_CODE=<NHI_CODE> AND DEL_FLG='N' AND ADM_TYPE=<NHI_TYPE>
onInsRegisterItemUpdate.Debug=N

