# 
#  Title:感染控制感染病例筛选module
# 
#  Description:感染控制感染病例筛选module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=caseRegisterPatInfo;caseRegisterDiag;caseRegisterCase;selectInfReasonByCaseInfNo;selectInfIntvoprecByCaseInfNo;selectInfResultByCaseInfNo;insertInfCase;updateInfCase;insertInfInfreasrcd;deleteInfInfreasrcd;selectMaxInfNo;deleteAntibiotrcd;insertAntibiotrcd;selectAntibiotrcd;selectLastAdmCase;selectInfCaseReport;updateInfCaseReport;deleteInfantibiotest;insertInfantibiotest;updateInfCaseCancelFlg;selectInfCaseCardInf;selectInfReasonForCard;updateMROINFDiag;selectInfIoByCaseInfNo;selectInfIoFromOdiByCase;deleteInfICDPart;insertInfICDPart;deleteInfIO;insertInfIO;selectInfICDPartByCaseInfNo;selectInvOpt

//感染病历登记病人资料
//============pangben modify 20110624 添加区域
caseRegisterPatInfo.Type=TSQL
caseRegisterPatInfo.SQL=SELECT A.MR_NO,A.CASE_NO,B.STATION_CODE,A.VS_DR_CODE, &
                               C.PAT_NAME,C.BIRTH_DATE,C.SEX_CODE,A.IN_DATE,A.DS_DATE,&
                               A.TOTAL_AMT,A.BED_NO,A.IPD_NO,A.DEPT_CODE &
                        FROM   ADM_INP A,SYS_BED B,SYS_PATINFO C &
                        WHERE  A.MR_NO=<MR_NO> &
                        AND    A.CASE_NO=(SELECT MAX(CASE_NO) &
                                          FROM   ADM_INP &
                                          WHERE  MR_NO=<MR_NO> AND REGION_CODE=<REGION_CODE>) &
                        AND B.BED_NO=A.BED_NO &
                        AND C.MR_NO=A.MR_NO
                        AND A.REGION_CODE=<REGION_CODE>
caseRegisterPatInfo.Debug=Y


//感染病历登记诊断信息
caseRegisterDiag.Type=TSQL
caseRegisterDiag.SQL=SELECT B.ICD_CHN_DESC,A.ICD_CODE,A.IO_TYPE,A.MAINDIAG_FLG,A.SEQ_NO &
                     FROM   ADM_INPDIAG A,SYS_DIAGNOSIS B &
                     WHERE A.CASE_NO=<CASE_NO> &
                     AND   A.IO_TYPE IN ('M','O')&
                     AND   A.MAINDIAG_FLG IN ('Y','N') &
                     AND   B.ICD_CODE = A.ICD_CODE &
                     ORDER BY MAINDIAG_FLG,SEQ_NO DESC 
caseRegisterDiag.Debug=N


//感染病历登记感染信息
caseRegisterCase.Type=TSQL
caseRegisterCase.SQL=SELECT A.INF_NO,A.OPBOOK_SEQ,A.INF_AREA,A.INF_SOURCE, A.REGISTER_DATE, A.INFCASE_SEQ, A.INF_DATE,&
                            A.INFPOSITION_CODE, A.INFPOSITION_DTL, A.DIEINFLU_CODE,&
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.INF_DIAG1) INF_DIAG1_DESC, &
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS  WHERE ICD_CODE = A.INF_DIAG2) INF_DIAG2_DESC,&
                            A.INFRETN_CODE, B.OPT_CHN_DESC, A.OPCUT_TYPE, A.ANA_TYPE,&
                            A.URGTOP_FLG, A.OP_TIME, A.OP_DR, A.ADM_DAYS, A.CHARGE_FEE,&
                            A.ETIOLGEXM_FLG, A.EXAM_DATE, A.SPECIMEN_CODE, A.LABWAY,&
                            A.LABPOSITIVE, A.PATHOGEN1_CODE, A.PATHOGEN2_CODE, A.PATHOGEN3_CODE,&
                            A.ANTIBIOTEST_FLG, A.CLINICAL_SYMP, A.REPORT_NO, A.REPORT_DATE,&
                            A.INF_DIAG1, A.INF_DIAG2, A.OP_CODE, A.INICU_DATE, A.OUTICU_DATE,&
                            A.OP_DATE, A.IN_DIAG1, A.IN_DIAG2, A.OUT_DIAG1, A.OUT_DIAG2,&
                            A.OUT_DIAG3, &
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.IN_DIAG1) IN_DIAG1_DESC,&
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.IN_DIAG2) IN_DIAG2_DESC, &
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.OUT_DIAG1) OUT_DIAG1_DESC,&
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.OUT_DIAG2) OUT_DIAG2_DESC, &
                            (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = A.OUT_DIAG3) OUT_DIAG3_DESC, &
                            A.INF_DR,A.IPD_NO,A.DEPT_CODE &
                       FROM INF_CASE A LEFT JOIN SYS_OPERATIONICD B ON B.OPERATION_ICD = A.OP_CODE &
                       WHERE A.CASE_NO = <CASE_NO> &
                       AND A.CANCEL_FLG ='N' &
                       ORDER BY A.INFCASE_SEQ
caseRegisterCase.item=INF_NO;INFCASE_SEQ
caseRegisterCase.INF_NO=A.INF_NO = <INF_NO>
caseRegisterCase.INFCASE_SEQ=A.INFCASE_SEQ = <INFCASE_SEQ>
caseRegisterCase.Debug=Y

//取得病患感染原因
selectInfReasonByCaseInfNo.Type=TSQL
selectInfReasonByCaseInfNo.SQL=SELECT CASE WHEN B.INFREASON_CODE IS NULL THEN 'N' ELSE 'Y' END SEL_FLG ,&
                                      A.CHN_DESC,B.INFREASON_NOTE,A.ID &
                               FROM   SYS_DICTIONARY A LEFT JOIN INF_INFREASRCD B  &
                               ON     B.INFREASON_CODE = A.ID &
                               AND    B.CASE_NO=<CASE_NO>  &
                               AND    B.INF_NO=<INF_NO> &
                               WHERE  A.GROUP_ID = 'INF_INFREASON' 
selectInfReasonByCaseInfNo.Debug=N

//取得病患介入信息 待确认
selectInfIntvoprecByCaseInfNo.Type=TSQL
selectInfIntvoprecByCaseInfNo.SQL=SELECT B.CHN_DESC, A.REMARK,A.ORDER_NO,A.ORDER_SEQ  &
                                  FROM   INF_INTVOPREC A, SYS_DICTIONARY B &
                                  WHERE A.CASE_NO=<CASE_NO>  &
                                  AND   A.INF_NO=<INF_NO>  &
                                  AND   A.CANCEL_FLG = 'N' &
                                  AND   B.GROUP_ID = 'INF_INTVOPTYPE' &
                                  AND   A.INTVOPTYPE_CODE=B.ID
selectInfIntvoprecByCaseInfNo.Debug=N

//取得病患试验结果信息 待确认
selectInfResultByCaseInfNo.Type=TSQL
selectInfResultByCaseInfNo.SQL=SELECT 'N' DEL_FLG,E.CUL_ABS_DESC,F.ANTI_DESC,A.SENS_LEVEL,A.CULTURE_CODE,A.ANTI_CODE &
                               FROM   INF_ANTIBIOTEST A ,LAB_CULTURE E,LAB_SENSTSTATBTC F &
                               WHERE  A.CASE_NO=<CASE_NO> &
                               AND    A.INF_NO=<INF_NO> &
                               AND    E.CULTURE_CODE = A.CULTURE_CODE &
                               AND    F.ANTI_CODE = A.ANTI_CODE
selectInfResultByCaseInfNo.Debug=N

//写入感控记录表
//==============pangben modify 20110624 添加区域
insertInfCase.Type=TSQL
insertInfCase.SQL=INSERT INTO INF_CASE ( INF_NO, CASE_NO, INFCASE_SEQ, IPD_NO, MR_NO, &
                       INF_DATE, ADM_DAYS, DEPT_CODE, STATION_CODE, BED_NO, &
                       VS_DR, INF_DIAG1, INF_DIAG2, INFPOSITION_CODE, INFPOSITION_DTL,  &
                       DIEINFLU_CODE, INFRETN_CODE, OP_CODE, OP_DATE, OPCUT_TYPE, &
                       ANA_TYPE, URGTOP_FLG, OP_TIME, OP_DR, IN_DIAG1,  &
                       IN_DIAG2, OUT_DIAG1, OUT_DIAG2, OUT_DIAG3, CHARGE_FEE,  &
                       ETIOLGEXM_FLG, EXAM_DATE, SPECIMEN_CODE, LABWAY, LABPOSITIVE,  &
                       PATHOGEN1_CODE, PATHOGEN2_CODE, PATHOGEN3_CODE, ANTIBIOTEST_FLG, REGISTER_DATE,  &
                       INF_DR, CLINICAL_SYMP, INF_PLAN, REPORT_DATE, REPORT_NO,  &
                       INICU_DATE, OUTICU_DATE, CANCEL_FLG, OPT_USER, OPT_DATE,  &
                       OPT_TERM,REGION_CODE,OPBOOK_SEQ,INF_AREA,INF_SOURCE )  &
		 VALUES ( <INF_NO>, <CASE_NO>, <INFCASE_SEQ>, <IPD_NO>, <MR_NO>,  &
		          <INF_DATE>, <ADM_DAYS>, <DEPT_CODE>, <STATION_CODE>, <BED_NO>,  &
		          <VS_DR>, <INF_DIAG1>, <INF_DIAG2>, <INFPOSITION_CODE>, <INFPOSITION_DTL>,  &
		          <DIEINFLU_CODE>, <INFRETN_CODE>, <OP_CODE>, <OP_DATE>, <OPCUT_TYPE>,  &
		          <ANA_TYPE>, <URGTOP_FLG>, <OP_TIME>, <OP_DR>, <IN_DIAG1>,  &
		          <IN_DIAG2>, <OUT_DIAG1>, <OUT_DIAG2>, <OUT_DIAG3>, <CHARGE_FEE>,  &
		          <ETIOLGEXM_FLG>, <EXAM_DATE>, <SPECIMEN_CODE>, <LABWAY>, <LABPOSITIVE>,  &
		          <PATHOGEN1_CODE>, <PATHOGEN2_CODE>, <PATHOGEN3_CODE>, <ANTIBIOTEST_FLG>,<REGISTER_DATE>, &
		          <INF_DR>, <CLINICAL_SYMP>, <INF_PLAN>, <REPORT_DATE>, <REPORT_NO>,  &
		          <INICU_DATE>, <OUTICU_DATE>, <CANCEL_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>,<REGION_CODE>,<OPBOOK_SEQ>,<INF_AREA>,<INF_SOURCE> ) 
insertInfCase.Debug=N

//更新感控记录表
updateInfCase.Type=TSQL
updateInfCase.SQL=UPDATE INF_CASE SET INF_NO=<INF_NO>, CASE_NO=<CASE_NO>, INFCASE_SEQ=<INFCASE_SEQ>, IPD_NO=<IPD_NO>, MR_NO=<MR_NO>, &
                                      INF_DATE=<INF_DATE>, ADM_DAYS=<ADM_DAYS>, DEPT_CODE=<DEPT_CODE>, STATION_CODE=<STATION_CODE>,BED_NO=<BED_NO>, &
                                      VS_DR=<VS_DR>, INF_DIAG1=<INF_DIAG1>, INF_DIAG2=<INF_DIAG2>, INFPOSITION_CODE=<INFPOSITION_CODE>, INFPOSITION_DTL=<INFPOSITION_DTL>,  &
                                      DIEINFLU_CODE=<DIEINFLU_CODE>, INFRETN_CODE=<INFRETN_CODE>, OP_CODE=<OP_CODE>, OP_DATE=<OP_DATE>, OPCUT_TYPE=<OPCUT_TYPE>,  &
                                      ANA_TYPE=<ANA_TYPE>, URGTOP_FLG=<URGTOP_FLG>, OP_TIME=<OP_TIME>, OP_DR=<OP_DR>, IN_DIAG1=<IN_DIAG1>,  &
                                      IN_DIAG2=<IN_DIAG2>, OUT_DIAG1=<OUT_DIAG1>,OUT_DIAG2=<OUT_DIAG2>, OUT_DIAG3=<OUT_DIAG3>, CHARGE_FEE=<CHARGE_FEE>,  &
                                      ETIOLGEXM_FLG=<ETIOLGEXM_FLG>, EXAM_DATE=<EXAM_DATE>, SPECIMEN_CODE=<SPECIMEN_CODE>, LABWAY=<LABWAY>, LABPOSITIVE=<LABPOSITIVE>,  &
                                      PATHOGEN1_CODE=<PATHOGEN1_CODE>, PATHOGEN2_CODE=<PATHOGEN2_CODE>,PATHOGEN3_CODE=<PATHOGEN3_CODE>, ANTIBIOTEST_FLG=<ANTIBIOTEST_FLG>,REGISTER_DATE=<REGISTER_DATE>,  &
                                      INF_DR=<INF_DR>, CLINICAL_SYMP=<CLINICAL_SYMP>, INF_PLAN=<INF_PLAN>, REPORT_DATE=<REPORT_DATE>, REPORT_NO=<REPORT_NO>,  &
                                      INICU_DATE=<INICU_DATE>,OUTICU_DATE=<OUTICU_DATE>, CANCEL_FLG=<CANCEL_FLG>, OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>,  &
                                      OPT_TERM=<OPT_TERM>,OPBOOK_SEQ=<OPBOOK_SEQ>,INF_AREA=<INF_AREA>,INF_SOURCE=<INF_SOURCE>  &
                  WHERE INF_NO=<INF_NO>  &
                  AND   CASE_NO=<CASE_NO>  &
                  AND   INFCASE_SEQ=<INFCASE_SEQ> 
updateInfCase.Debug=N

//查询病案首页是否已有记录 待确认
selectMroFrontpgM.Type=TSQL
selectMroFrontpgM.SQL=SELECT CASE_NO,DIAGCHK_FLG &
                      FROM MRO_FRONTPG_M &
                      WHERE CASE_NO=<CASE_NO> &
                      AND DIAGCHK_FLG='Y'
selectMroFrontpgM.Debug=N

//删除病案诊断记录 待确认
deleteMroFrontpgDiag.Type=TSQL
deleteMroFrontpgDiag.SQL=DELETE FROM MRO_FRONTPG_DIAG &
                         WHERE CASE_NO=<CASE_NO> &
                         AND DIAG_TYPE='S'
deleteMroFrontpgDiag.Debug

//写入病案诊断记录 带确认
insertMroFrontpgDiag.Type=TSQL
insertMroFrontpgDiag.SQL=INSERT INTO MRO_FRONTPG_DIAG(MR_NO, CASE_NO, DIAG_TYPE, &
                                                      ICD10, DIAG_CHN_DESC, MAIN_FLG, OUT_STATUS,  &
                                                     OPT_USER, OPT_DATE, OPT_TERM, SEQ_NO)  &
                                              VALUES (<MR_NO>, <CASE_NO>, <DIAG_TYPE>, &
                                                      <ICD10>, <DIAG_CHN_DESC>, <MAIN_FLG>, <OUT_STATUS>,  &
                                                      <OPT_USER>, <OPT_DATE>, <OPT_TERM>, <SEQ_NO>)  
insertMroFrontpgDiag.Debug=N


//删除易感染因素信息
deleteInfInfreasrcd.Type=TSQL
deleteInfInfreasrcd.SQL=DELETE FROM INF_INFREASRCD &
                        WHERE CASE_NO=<CASE_NO> &
                        AND   INF_NO=<INF_NO>
deleteInfInfreasrcd.Debug=N

//写入易感染因素信息
insertInfInfreasrcd.Type=TSQL
insertInfInfreasrcd.SQL=INSERT INTO INF_INFREASRCD ( INF_NO, INFREASON_CODE, CASE_NO, IPD_NO, MR_NO, &
                                                     INFCASE_SEQ, INFREASON_NOTE, OPT_USER, OPT_DATE, OPT_TERM )  &
                                            VALUES ( <INF_NO>, <INFREASON_CODE>, <CASE_NO>, <IPD_NO>, <MR_NO>,  &
                                                     <INFCASE_SEQ>, <INFREASON_NOTE>,<OPT_USER>, <OPT_DATE>, <OPT_TERM> ) 
insertInfInfreasrcd.Debug=N


//删除试验结果记录
deleteInfantibiotest.Type=TSQL
deleteInfantibiotest.SQL=DELETE FROM INF_ANTIBIOTEST  &
                         WHERE CASE_NO=<CASE_NO>  &
                         AND INF_NO=<INF_NO> &
                         AND INFCASE_SEQ=<INFCASE_SEQ>
deleteInfantibiotest.Debug=N

//写入试验结果记录
insertInfantibiotest.Type=TSQL
insertInfantibiotest.SQL=INSERT INTO INF_ANTIBIOTEST ( INF_NO, CULURE_CODE, ANTI_CODE,HOSP_AREA, CASE_NO, &
                                                       INFCASE_SEQ, SENS_LEVEL, INFECTLEVEL, GRAMSTAIN, COLONYCOUNT, &
                                                       CANCEL_FLG, OPT_USER, OPT_DATE, OPT_TERM ) &
                                              VALUES ( <INF_NO>, <CULURE_CODE>, <ANTI_CODE>,<HOSP_AREA>, <CASE_NO>, &
                                                       <INFCASE_SEQ>, <SENS_LEVEL>, <INFECTLEVEL>, <GRAMSTAIN>, <COLONYCOUNT>, &
                                                       <CANCEL_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM> ) 
insertInfantibiotest.Debug=N



//查询最大感染病人编号
selectMaxInfNo.Type=TSQL
selectMaxInfNo.SQL=SELECT MAX(INF_NO) INF_NO &
                   FROM INF_CASE &
                   WHERE CASE_NO=<CASE_NO>
selectMaxInfNo.Debug=N

//删除抗生素信息
deleteAntibiotrcd.Type=TSQL
deleteAntibiotrcd.SQL=DELETE FROM INF_ANTIBIOTRCD &
                      WHERE CASE_NO=<CASE_NO>
deleteAntibiotrcd.Debug=N


//写入抗生素信息
insertAntibiotrcd.Type=TSQL
insertAntibiotrcd.SQL=INSERT INTO INF_ANTIBIOTRCD ( ORDER_NO, ORDER_SEQ, ORDER_CODE, CASE_NO, IPD_NO, &
                                                    MR_NO, INF_NO, ILLEGIT_FLG, ILLEGIT_REMARK, MEDALLERG_SYMP, &
                                                    OPT_USER, OPT_DATE, OPT_TERM ) &
                                           VALUES ( <ORDER_NO>, <ORDER_SEQ>, <ORDER_CODE>, <CASE_NO>, <IPD_NO>, &
                                                    <MR_NO>, <INF_NO>, <ILLEGIT_FLG>, <ILLEGIT_REMARK>, <MEDALLERG_SYMP>, &
                                                    <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertAntibiotrcd.Debug=N


//查询抗生素信息
//============pangben modify 20110624 添加区域
selectAntibiotrcd.Type=TSQL
selectAntibiotrcd.SQL=SELECT A.DEPT_CODE,A.STATION_CODE,A.BED_NO,D.PAT_NAME,B.ORDER_DESC, &
                             B.EFF_DATE,B.DC_DATE,B.MEDI_QTY,B.MEDI_UNIT,B.FREQ_CODE,B.ROUTE_CODE, &
                             B.ORDER_DR_CODE, A.CASE_NO, A.MR_NO,C.ANTIBIOTIC_CODE, &
                             (CASE WHEN E.ILLEGIT_FLG IS NULL THEN 'N'ELSE E.ILLEGIT_FLG END) ILLEGIT_FLG, &
                             E.ILLEGIT_REMARK,E.MEDALLERG_SYMP,A.VS_DR_CODE,D.BIRTH_DATE,B.ORDER_NO, &
                             B.ORDER_SEQ,B.ORDER_CODE,A.IPD_NO,B.RX_KIND,F.CHN_DESC AS ANTIBIOTIC_WAY_DESC &
                      FROM   ADM_INP A,ODI_ORDER B,PHA_BASE C,SYS_PATINFO D,INF_ANTIBIOTRCD E,SYS_DICTIONARY F &
                      WHERE  B.EFF_DATE BETWEEN TO_DATE(<START_DATE>,'yyyy-mm-dd hh24-mi-ss') AND TO_DATE(<END_DATE>,'yyyy-mm-dd hh24-mi-ss') &
                      AND    A.MR_NO = B.MR_NO &
                      AND    A.CASE_NO = B.CASE_NO &
                      AND    B.ORDER_CODE = C.ORDER_CODE &
                      AND    A.MR_NO = D.MR_NO &
                      AND    C.ANTIBIOTIC_CODE IS NOT NULL & 
                      AND    B.CASE_NO = E.CASE_NO(+) &
                      AND    B.ORDER_CODE = E.ORDER_CODE(+) &
                      AND    B.ORDER_NO = E.ORDER_NO(+) &
                      AND    B.ROUTE_CODE !='PS' &
                      AND    B.ANTIBIOTIC_WAY(+) = F.ID &
                      AND    F.GROUP_ID = 'ANTIBIOTIC_WAY' &
                      AND    B.ORDER_SEQ = E.ORDER_SEQ(+) 
selectAntibiotrcd.item=MR_NO;IPD_NO;DEPT_CODE;STATION_CODE;ANTIBIOTIC_CODE;REGION_CODE;RX_KIND
//就诊序号
selectAntibiotrcd.MR_NO=A.MR_NO = <MR_NO>
//住院号
selectAntibiotrcd.IPD_NO=A.IPD_NO = <IPD_NO>
//区域
selectAntibiotrcd.REGION_CODE=A.REGION_CODE = <REGION_CODE>
//科室
selectAntibiotrcd.DEPT_CODE=A.DEPT_CODE = <DEPT_CODE>
//病区
selectAntibiotrcd.STATION_CODE=A.STATION_CODE = <STATION_CODE>
//==========liuyalin modify 20170621 添加长期医嘱，临时医嘱筛选
selectAntibiotrcd.RX_KIND =B.RX_KIND = <RX_KIND>
//抗生素等级
selectAntibiotrcd.ANTIBIOTIC_CODE=C.ANTIBIOTIC_CODE = <ANTIBIOTIC_CODE>
//开立医师
selectAntibiotrcd.ORDER_DR_CODE=B.ORDER_DR_CODE = <ORDER_DR_CODE>
selectAntibiotrcd.Debug=Y

//查询最后一次住院记录
selectLastAdmCase.Type=TSQL
selectLastAdmCase.SQL=SELECT MAX(CASE_NO) CASE_NO FROM ADM_INP WHERE  MR_NO=<MR_NO>
selectLastAdmCase.Debug=N


//查询上报感控信息
selectInfCaseReport.Type=TSQL
selectInfCaseReport.SQL=SELECT CASE WHEN B.REGISTER_DATE IS NULL THEN 'N' ELSE 'Y' END REGISTER_FLG, &
                               CASE WHEN B.REPORT_DATE IS NULL  THEN 'N' ELSE 'Y' END REPORT_FLG, &
                               A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.CASE_NO,A.MR_NO, &
                               C.PAT_NAME,A.IN_DATE,B.INF_DATE,A.VS_DR_CODE,B.OP_DATE, &
                               B.REPORT_DATE,B.REPORT_NO,B.INF_NO,B.INFCASE_SEQ,A.IPD_NO &
                         FROM  ADM_INP A,INF_CASE B,SYS_PATINFO C &
                         WHERE A.CASE_NO = B.CASE_NO &
                         AND   A.MR_NO = C.MR_NO
selectInfCaseReport.item=DS_DATE_NULL;DS_DATE_NOT_NULL;REPORT_DATE;REPORT_NO;START_DATE;END_DATE;REPORT_DATE_NULL;REPORT_DATE_NOT_NULL;MR_NO;REGION_CODE
selectInfCaseReport.DS_DATE_NULL=A.DS_DATE IS NULL
selectInfCaseReport.DS_DATE_NOT_NULL=A.DS_DATE IS NOT NULL
selectInfCaseReport.REPORT_DATE=B.REPORT_DATE = <REPORT_DATE>
//==========pangben modify 20110624 添加区域
selectInfCaseReport.REGION_CODE=B.REGION_CODE = <REGION_CODE>
selectInfCaseReport.REPORT_NO=B.REPORT_NO = <REPORT_NO>
selectInfCaseReport.START_DATE=B.INF_DATE >= <START_DATE>
selectInfCaseReport.END_DATE=B.INF_DATE <= <END_DATE>
selectInfCaseReport.REPORT_DATE_NULL=B.REPORT_DATE IS NULL
selectInfCaseReport.REPORT_DATE_NOT_NULL=B.REPORT_DATE IS NOT NULL
selectInfCaseReport.MR_NO=A.MR_NO = <MR_NO>
selectInfCaseReport.Debug=N


//更新感控记录上报信息
updateInfCaseReport.Type=TSQL
updateInfCaseReport.SQL=UPDATE INF_CASE SET REPORT_DATE = <REPORT_DATE>, &
                                            REPORT_NO = <REPORT_NO> &
                                      WHERE INF_NO = <INF_NO> &
                                      AND   CASE_NO = <CASE_NO> &
                                      AND   INFCASE_SEQ = <INFCASE_SEQ>
updateInfCaseReport.Debug=N

//更行感控记录表取消注记
updateInfCaseCancelFlg.Type=TSQL
updateInfCaseCancelFlg.SQL=UPDATE INF_CASE SET CANCEL_FLG = 'Y' &
                           WHERE INF_NO = <INF_NO> &
                           AND   CASE_NO = <CASE_NO> &
                           AND   INFCASE_SEQ = <INFCASE_SEQ>
updateInfCaseCancelFlg.Debug=N



//取得感染病例报告卡信息
selectInfCaseCardInf.Type=TSQL
selectInfCaseCardInf.SQL=SELECT B.DEPT_CHN_DESC INF_DEPT,C.USER_NAME INF_DR,D.PAT_NAME,D.BIRTH_DATE,TO_CHAR(E.IN_DATE,'YYYY/MM/DD') IN_DATE, &
                                A.MR_NO,TO_CHAR(A.INF_DATE,'YYYY/MM/DD') INF_DATE,I.ICD_CHN_DESC INF_DIAG1,J.ICD_CHN_DESC INF_DIAG2, &
                                A.ETIOLGEXM_FLG,F.CHN_DESC SPECIMEN_CODE,G.CHN_DESC PATHOGEN1_CODE,H.CHN_DESC PATHOGEN2_CODE, &
                                K.ICD_CHN_DESC IN_DIAG1,L.ICD_CHN_DESC IN_DIAG2, M.CHN_DESC SEX_CODE,A.OP_CODE,A.REPORT_DATE,A.LABPOSITIVE,A.OPBOOK_SEQ, &
								A.OPCUT_TYPE &
                         FROM   INF_CASE A LEFT JOIN SYS_DICTIONARY F ON F.GROUP_ID = 'INF_LABSPECIMEN' AND A.SPECIMEN_CODE = F.ID &
                                           LEFT JOIN SYS_DICTIONARY G ON G.GROUP_ID = 'INF_PATHOGENKIND' AND A.PATHOGEN1_CODE = G.ID &
                                           LEFT JOIN SYS_DICTIONARY H ON H.GROUP_ID = 'INF_PATHOGENKIND' AND A.PATHOGEN2_CODE = H.ID &
                                           LEFT JOIN SYS_DIAGNOSIS I ON I.ICD_TYPE = 'W' AND A.INF_DIAG1 = I.ICD_CODE  &
                                           LEFT JOIN SYS_DIAGNOSIS J ON J.ICD_TYPE = 'W' AND A.INF_DIAG2 = J.ICD_CODE &
                                           LEFT JOIN SYS_DIAGNOSIS K ON K.ICD_TYPE = 'W' AND A.IN_DIAG1 = K.ICD_CODE &
                                           LEFT JOIN SYS_DIAGNOSIS L ON L.ICD_TYPE = 'W' AND A.IN_DIAG2 = L.ICD_CODE, &
                                SYS_DEPT B,SYS_OPERATOR C,SYS_PATINFO D,ADM_INP E,SYS_DICTIONARY M &
                         WHERE  A.INF_NO = <INF_NO>  &
                         AND    A.INFCASE_SEQ = <INFCASE_SEQ> &
                         AND    A.CASE_NO = <CASE_NO> &
                         AND    A.DEPT_CODE = B.DEPT_CODE &
                         AND    A.INF_DR = C.USER_ID &
                         AND    A.MR_NO = D.MR_NO &
                         AND    A.CASE_NO = E.CASE_NO &
                         AND    M.GROUP_ID = 'SYS_SEX' &
                         AND    M.ID = D.SEX_CODE 
						
selectInfCaseCardInf.Debug=Y


//取得病患感染病例报告卡感染原因信息
selectInfReasonForCard.Type=TSQL
selectInfReasonForCard.SQL=SELECT  A.CHN_DESC,B.INFREASON_CODE &
                           FROM    SYS_DICTIONARY A ,INF_INFREASRCD B  &
                           WHERE   B.CASE_NO=<CASE_NO>  &
                           AND     B.INF_NO=<INF_NO>  &
                           AND     A.GROUP_ID = 'INF_INFREASON' &
                           AND     B.INFREASON_CODE = A.ID 
selectInfReasonForCard.Debug=Y

//更新病案感控诊断
updateMROINFDiag.Type=TSQL 
updateMROINFDiag.SQL=UPDATE MRO_RECORD &
                     SET    INTE_DIAG_CODE = <INF_DIAG1> &
                     WHERE  CASE_NO = <CASE_NO>
updateMROINFDiag.Debug=N

//取得病患侵入性操作信息 add by wanglong 20140217
selectInfIoByCaseInfNo.Type=TSQL
selectInfIoByCaseInfNo.SQL=SELECT CASE WHEN B.IO_CODE IS NULL THEN 'N' ELSE 'Y' END AS SEL_FLG, A.ID AS IO_CODE, &
                                  CASE WHEN B.IO_NOTE IS NULL THEN '' ELSE B.IO_NOTE END AS IO_NOTE &
                             FROM SYS_DICTIONARY A, INF_IO B &
                            WHERE A.GROUP_ID = 'IO_TYPE' &
                              AND A.ID = B.IO_CODE(+) &
                              AND B.CASE_NO(+) = <CASE_NO> &
                              AND B.INF_NO(+) = <INF_NO> &
                         ORDER BY A.ID
selectInfIoByCaseInfNo.Debug=N

//从ODI_ORDER表取得病患侵入性操作信息 add by wanglong 20140217
selectInfIoFromOdiByCase.Type=TSQL
selectInfIoFromOdiByCase.SQL=SELECT DISTINCT CASE WHEN B.IO_CODE IS NULL THEN 'N' ELSE 'Y' END AS SEL_FLG, A.ID AS IO_CODE, '' AS IO_NOTE &
                               FROM SYS_DICTIONARY A, (SELECT B.IO_CODE &
                                                         FROM SYS_FEE B, ODI_ORDER C &
                                                        WHERE B.ORDER_CODE = C.ORDER_CODE &
                                                          AND B.IO_CODE IS NOT NULL &
                                                          AND C.CASE_NO = <CASE_NO>) B &
                              WHERE A.GROUP_ID = 'IO_TYPE' &
                                AND A.ID = B.IO_CODE(+) &
                           ORDER BY A.ID
selectInfIoFromOdiByCase.Debug=Y

//删除感染部位诊断信息 //add by wanglong 20140217
deleteInfICDPart.Type=TSQL
deleteInfICDPart.SQL=DELETE FROM INF_ICDPART &
                           WHERE CASE_NO=<CASE_NO> &
                             AND INF_NO=<INF_NO> &
                             AND INFCASE_SEQ=<INFCASE_SEQ>
deleteInfICDPart.item=SEQ
deleteInfICDPart.REPORT_NO=SEQ=<SEQ>
deleteInfICDPart.Debug=N

//写入感染部位诊断信息 //add by wanglong 20140217
insertInfICDPart.Type=TSQL
insertInfICDPart.SQL=INSERT INTO INF_ICDPART (INF_NO,INFCASE_SEQ,SEQ,CASE_NO,IPD_NO,MR_NO,MAIN_FLG,PART_CODE,ICD_CODE,UM_CODE,OPT_USER,OPT_DATE,OPT_TERM,INF_SYSTEMCODE) &
                          VALUES (<INF_NO>,<INFCASE_SEQ>,<SEQ>,<CASE_NO>,<IPD_NO>,<MR_NO>,<MAIN_FLG>,<PART_CODE>,<ICD_CODE>,<UM_CODE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<INF_SYSTEMCODE>)
insertInfICDPart.Debug=N

//删除侵入性操作信息 //add by wanglong 20140217
deleteInfIO.Type=TSQL
deleteInfIO.SQL=DELETE FROM INF_IO &
                      WHERE CASE_NO=<CASE_NO> &
                        AND INF_NO=<INF_NO>
deleteInfIO.Debug=N

//写入侵入性操作信息 //add by wanglong 20140217
insertInfIO.Type=TSQL
insertInfIO.SQL=INSERT INTO INF_IO (INF_NO,IO_CODE,CASE_NO,IPD_NO,MR_NO,INFCASE_SEQ,IO_NOTE,OPT_USER,OPT_DATE,OPT_TERM) &
                     VALUES (<INF_NO>,<IO_CODE>,<CASE_NO>,<IPD_NO>,<MR_NO>,<INFCASE_SEQ>,<IO_NOTE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertInfIO.Debug=N

//取得病患感染部位诊断信息 add by wanglong 20140217
selectInfICDPartByCaseInfNo.Type=TSQL
selectInfICDPartByCaseInfNo.SQL=SELECT A.*, B.ICD_CHN_DESC AS ICD_DESC, C.ICD_CHN_DESC AS UM_DESC &
                                  FROM INF_ICDPART A, SYS_DIAGNOSIS B, SYS_DIAGNOSIS C &
                                 WHERE A.ICD_CODE = B.ICD_CODE(+) &
                                   AND A.UM_CODE = C.ICD_CODE(+) &
                                   AND A.CASE_NO = <CASE_NO> &
                                   AND A.INF_NO = <INF_NO> &
                              ORDER BY A.SEQ
selectInfICDPartByCaseInfNo.Debug=N

//查询介入操作信息 add by liuyalin 20170522
selectInvOpt.Type=TSQL
selectInvOpt.SQL=SELECT B.DEPT_CODE, B.STATION_CODE, B.BED_NO, C.PAT_NAME, A.ORDER_DESC, A.EFF_DATE, A.DC_DATE, &
                        A.MEDI_QTY, A.MEDI_UNIT, A.FREQ_CODE, A.ROUTE_CODE, A.ORDER_DR_CODE, B.CASE_NO, B.MR_NO, &
                        B.IPD_NO, B.VS_DR_CODE, C.BIRTH_DATE, A.ORDER_NO, A.ORDER_SEQ, A.ORDER_CODE &
                  FROM ODI_ORDER A, ADM_INP B, SYS_PATINFO C,SYS_FEE D &
                 WHERE A.EFF_DATE BETWEEN <START_DATE> AND <END_DATE> &
                   AND A.MR_NO = B.MR_NO &
                   AND A.CASE_NO = B.CASE_NO &
                   AND B.MR_NO = C.MR_NO &
		   AND A.ORDER_CODE = D.ORDER_CODE & 
                   AND D.IN_OPFLG = 'Y'
selectInvOpt.item=REGION_CODE;MR_NO;IPD_NO;DEPT_CODE;STATION_CODE;VS_DR_CODE;ORDER_CODE
selectInvOpt.REGION_CODE=B.REGION_CODE = <REGION_CODE>
selectInvOpt.MR_NO=B.MR_NO = <MR_NO>
selectInvOpt.ORDER_CODE=A.ORDER_CODE = <ORDER_CODE>
selectInvOpt.IPD_NO=B.IPD_NO = <IPD_NO>
selectInvOpt.DEPT_CODE=B.DEPT_CODE = <DEPT_CODE>
selectInvOpt.STATION_CODE=B.STATION_CODE = <STATION_CODE>
selectInvOpt.VS_DR_CODE=B.VS_DR_CODE = <VS_DR_CODE>
selectInvOpt.Debug=Y