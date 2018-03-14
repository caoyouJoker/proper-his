# 
#  Title:感控系统报表module
# 
#  Description:感控系统报表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=countMroRecord;countDeptInfCount;countDeptMroRecord;selectAllDept;selectHeatDayReport;selectInfAntibiotrcd;selestInfCaseForReport;selectDeptAllGainPoint;selectDeptAllEXMItem;selectDeptMonCount;selectYearMonEXMCount;selectYearMonEXMDate

//查询当月所有科室出院病人病历总数
countMroRecord.Type=TSQL
countMroRecord.SQL=SELECT COUNT(CASE_NO) COUNT &
                   FROM  MRO_RECORD &
                   WHERE TO_CHAR(OUT_DATE,'YYYYMM') = <DATE>
countMroRecord.item=REGION_CODE
countMroRecord.REGION_CODE=REGION_CODE=<REGION_CODE>
countMroRecord.Debug=N


//统计科室感控病历信息
countDeptInfCount.Type=TSQL
countDeptInfCount.SQL=SELECT A.INF_COUNT,B.REPORT_COUNT,C.ETIOLGEXM_COUNT &
                      FROM ( SELECT COUNT(INF_NO) AS INF_COUNT &
                             FROM INF_CASE &
                             WHERE DEPT_CODE=<DEPT_CODE> &
                             AND TO_CHAR(INF_DATE,'YYYYMM') = <DATE> ) A, &
                           ( SELECT COUNT(INF_NO) AS REPORT_COUNT &
                             FROM INF_CASE &
                             WHERE DEPT_CODE=<DEPT_CODE> &
                             AND TO_CHAR(INF_DATE,'YYYYMM') = <DATE> &
                             AND REPORT_DATE IS NOT NULL ) B, &
                           ( SELECT COUNT(INF_NO) AS ETIOLGEXM_COUNT &
                             FROM INF_CASE &
                             WHERE DEPT_CODE=<DEPT_CODE> &
                             AND TO_CHAR(INF_DATE,'YYYYMM') = <DATE> &
                             AND ETIOLGEXM_FLG ='Y') C
countDeptInfCount.Debug=N


//取得科室总病历数
countDeptMroRecord.Type=TSQL
countDeptMroRecord.SQL=SELECT COUNT(CASE_NO) COUNT &
                       FROM MRO_RECORD &
                       WHERE TO_CHAR(OUT_DATE,'YYYYMM') = <DATE> &
                       AND   OUT_DEPT = <DEPT_CODE>
countDeptMroRecord.Debug=N

//取得所有临床科室
selectAllDept.Type=TSQL
selectAllDept.SQL=SELECT DEPT_CODE, DEPT_CHN_DESC &
                  FROM SYS_DEPT &
                  WHERE FINAL_FLG = 'Y' &
                  AND   CLASSIFY = '0'
selectAllDept.item=REGION_CODE
selectAllDept.REGION_CODE=REGION_CODE=<REGION_CODE>              
selectAllDept.Debug=N



//取得发热日报表信息
selectHeatDayReport.Type=TSQL
selectHeatDayReport.SQL=SELECT E.STATION_DESC,C.PAT_NAME,B.MR_NO, &
                               CASE WHEN OP_DATE IS NULL THEN '' ELSE TO_CHAR(G.OP_DATE,'YYYY/MM/DD') END OP_DATE , &
                               A.TEMPERATURE &
                        FROM   SUM_NEWARRIVALSIGNDTL A LEFT JOIN  &
                               (SELECT CASE_NO,MAX(OP_DATE) AS OP_DATE  &
                                FROM OPE_OPBOOK  &
                                WHERE CANCEL_FLG IS NULL  &
                                GROUP BY CASE_NO) G &
                               ON TO_CHAR(G.OP_DATE,'YYYYMMDD') <= A.EXAMINE_DATE  &
                               AND G.CASE_NO = A.CASE_NO , &
                               ADM_INP B,SYS_PATINFO C,SYS_BED D,SYS_STATION E,SYS_BED_TYPE F  &
                        WHERE  A.ADM_TYPE = 'I' &
                        AND    A.EXAMINE_DATE  = <EXAMINE_DATE> &
                        AND    ((A.TEMPERATURE >= '38.2' AND G.OP_DATE IS NOT NULL) OR &
                                (A.TEMPERATURE >= '37.5' AND G.OP_DATE IS  NULL) ) &
                        AND    A.CASE_NO = B.CASE_NO   &
                        AND    B.MR_NO = C.MR_NO &
                        AND    B.STATION_CODE = E.STATION_CODE &
                        AND    B.STATION_CODE = <STATION_CODE> &
                        AND    B.BED_NO = D.BED_NO &
                        AND    D.BED_CLASS_CODE = F.BED_TYPE_CODE &
                        AND    F.ICU_FLG <>'Y' &
                        UNION &
                        SELECT E.STATION_DESC,C.PAT_NAME,B.MR_NO, &
                               CASE WHEN OP_DATE IS NULL THEN '' ELSE TO_CHAR(G.OP_DATE,'YYYYMMDD') END OP_DATE , &
                               A.TEMPERATURE &
                        FROM   SUM_VTSNTPRDTL A LEFT JOIN  &
                               (SELECT CASE_NO,MAX(OP_DATE) AS OP_DATE  &
                                FROM OPE_OPBOOK  &
                                WHERE CANCEL_FLG IS NULL  &
                                GROUP BY CASE_NO) G &
                               ON TO_CHAR(G.OP_DATE,'YYYYMMDD') <= A.EXAMINE_DATE  &
                               AND G.CASE_NO = A.CASE_NO , &
                               ADM_INP B,SYS_PATINFO C,SYS_BED D,SYS_STATION E,SYS_BED_TYPE F  &
                        WHERE  A.ADM_TYPE = 'I' &
                        AND    A.EXAMINE_DATE  = <EXAMINE_DATE> &
                        AND    ((A.TEMPERATURE >= '38.2' AND G.OP_DATE IS NOT NULL) OR &
                                (A.TEMPERATURE >= '37.5' AND G.OP_DATE IS  NULL) ) &
                        AND    A.CASE_NO = B.CASE_NO &
                        AND    B.MR_NO = C.MR_NO &
                        AND    B.STATION_CODE = E.STATION_CODE &
                        AND    B.STATION_CODE = <STATION_CODE> &
                        AND    B.BED_NO = D.BED_NO &
                        AND    D.BED_CLASS_CODE = F.BED_TYPE_CODE &
                        AND    F.ICU_FLG <>'Y'
//selectHeatDayReport.item=STATION_CODE
//selectHeatDayReport.STATION_CODE=B.STATION_CODE = <STATION_CODE>
selectHeatDayReport.Debug=N



//取得病患抗生素不合理使用信息
//=========pangben modify 20110629 添加区域
selectInfAntibiotrcd.Type=TSQL
selectInfAntibiotrcd.SQL=SELECT F.DEPT_CHN_DESC,G.STATION_DESC,H.USER_NAME,D.PAT_NAME,C.ORDER_DESC,B.MEDI_QTY, &
                                TO_CHAR(B.EFF_DATE,'YYYY/MM/DD')||'至'||TO_CHAR(B.DC_DATE,'YYYY/MM/DD') SE_DATE, &
                                E.ILLEGIT_REMARK,E.MEDALLERG_SYMP &
                         FROM   ODI_ORDER B,PHA_BASE C,SYS_PATINFO D,INF_ANTIBIOTRCD E , &
                                SYS_DEPT F,SYS_STATION G,SYS_OPERATOR H &
                         WHERE  B.EFF_DATE BETWEEN  <START_DATE> AND <END_DATE> &
                         AND    E.ILLEGIT_FLG = 'Y' &
                         AND    B.ORDER_CODE = C.ORDER_CODE  &
                         AND    B.MR_NO = D.MR_NO &
                         AND    B.ORDER_NO = E.ORDER_NO &
                         AND    B.ORDER_SEQ = E.ORDER_SEQ &
                         AND    B.ORDER_CODE = E.ORDER_CODE &
                         AND    B.DEPT_CODE = F.DEPT_CODE &
                         AND    B.STATION_CODE = G.STATION_CODE &
                         AND    B.VS_DR_CODE = H.USER_ID 
selectInfAntibiotrcd.item=DEPT_CODE;STATION_CODE;VS_DR_CODE;REGION_CODE
selectInfAntibiotrcd.DEPT_CODE=B.DEPT_CODE = <DEPT_CODE>
selectInfAntibiotrcd.REGION_CODE=B.REGION_CODE = <REGION_CODE>
selectInfAntibiotrcd.STATION_CODE=B.STATION_CODE = <STATION_CODE>
selectInfAntibiotrcd.VS_DR_CODE=B.VS_DR_CODE = <VS_DR_CODE>
selectInfAntibiotrcd.Debug=N



//医院感染病例检测汇总表1
//==============pangben modify 20110629
selestInfCaseForReport.Type=TSQL
selestInfCaseForReport.SQL=SELECT A.DEPT_CODE,C.DEPT_CHN_DESC,A.MR_NO,D.INF_NO,A.DS_DATE, &
                                  D.INFPOSITION_CODE,D.DIEINFLU_CODE,B.CODE1_STATUS &
                           FROM   ADM_INP A LEFT JOIN MRO_RECORD B ON A.CASE_NO = B.CASE_NO AND B.CODE1_STATUS = '4',  &
                                  SYS_DEPT C,INF_CASE D &
                           WHERE  TO_CHAR(D.INF_DATE,'YYYYMM') = <INF_DATE> &
                           AND    D.CASE_NO = A.CASE_NO &
                           AND    A.DEPT_CODE = C.DEPT_CODE &
                           AND    A.REGION_CODE=<REGION_CODE> &
                           GROUP  BY D.INF_NO,A.DS_DATE,D.INFPOSITION_CODE,D.DIEINFLU_CODE,B.CODE1_STATUS, &
			             A.DEPT_CODE,C.DEPT_CHN_DESC,A.MR_NO &
                           ORDER  BY A.DEPT_CODE,A.MR_NO
selestInfCaseForReport.Debug=N


//取得科室检测汇总信息
selectDeptAllGainPoint.Type=TSQL
selectDeptAllGainPoint.SQL=SELECT C.DEPT_CHN_DESC,TO_CHAR(A.EXAM_DATE,'YYYY/MM/DD') EXAM_DATE,&
                                  SUM(CASE WHEN A.PASS_FLG = 'Y' THEN B.ITEM_GAINPOINT ELSE 0 END) ITEM_GAINPOINT,&
                                  A.DEPT_CODE,A.EXAM_NO  &
                           FROM   INF_EXAM_RECORD A,INF_EXAMSTANDDETAIL B,SYS_DEPT C  &
                           WHERE  A.EXAM_DATE BETWEEN <START_DATE> AND <END_DATE> &
                           AND    A.EXAM_STANDM = B.EXAMSTAND_CODE &
                           AND    A.INFITEM_CODE = B.INFITEM_CODE &
                           AND    A.DEPT_CODE = C.DEPT_CODE &
                           AND    A.REGION_CODE=<REGION_CODE> &
                           GROUP  BY A.DEPT_CODE ,C.DEPT_CHN_DESC,A.EXAM_DATE,A.EXAM_NO  &
                           ORDER  BY A.DEPT_CODE ,C.DEPT_CHN_DESC,A.EXAM_DATE,A.EXAM_NO                    
selectDeptAllGainPoint.Debug=N


//取得科室检测项目信息
selectDeptAllEXMItem.Type=TSQL
selectDeptAllEXMItem.SQL=SELECT B.CHN_DESC,CASE WHEN A.PASS_FLG = 'Y' THEN '合格' ELSE '不合格' END PASS_FLG, &
                                A.REMARK,A.DEPT_CODE,TO_CHAR(A.EXAM_DATE,'YYYY/MM/DD') EXAM_DATE,A.EXAM_NO &
                         FROM   INF_EXAM_RECORD A,SYS_DICTIONARY B &
                         WHERE  A.EXAM_DATE BETWEEN <START_DATE> AND <END_DATE> &
                         AND    B.GROUP_ID = 'INF_EXMITEM' &
                         AND    A.INFITEM_CODE = B.ID &
                         ORDER BY A.EXAM_DATE,A.EXAM_NO,A.INFITEM_CODE 
selectDeptAllEXMItem.item=REGION_CODE
selectDeptAllEXMItem.REGION_CODE=A.REGION_CODE=<REGION_CODE>                        
selectDeptAllEXMItem.Debug=N


//取得科室年度检测信息
selectDeptMonCount.Type=TSQL
selectDeptMonCount.SQL=SELECT TO_CHAR(A.EXAM_DATE,'MM') || '月份'EXAM_MONTH,TO_CHAR(A.EXAM_DATE,'YYYY/MM/DD') EXAM_DATE,&
                              SUM(B.ITEM_GAINPOINT) ITEM_GAINPOINT, '' ITEM_GAINPOINT_AVERAGE,A.EXAM_NO &
                       FROM INF_EXAM_RECORD A,INF_EXAMSTANDDETAIL B &
                       WHERE TO_CHAR(A.EXAM_DATE,'YYYY') = <YEAR> &
                       AND   A.DEPT_CODE = <DEPT_CODE> &
                       AND   A.PASS_FLG = 'Y' &
                       AND   A.EXAM_STANDM = B.EXAMSTAND_CODE &
                       AND   A.INFITEM_CODE = B.INFITEM_CODE &
                       GROUP BY EXAM_DATE,A.EXAM_NO &
                       ORDER BY EXAM_DATE,A.EXAM_NO
selectDeptMonCount.item=REGION_CODE
selectDeptMonCount.REGION_CODE=A.REGION_CODE=<REGION_CODE>                        
selectDeptMonCount.Debug=N


//取得所有科室检测信息
selectYearMonEXMCount.Type=TSQL
selectYearMonEXMCount.SQL=SELECT A.DEPT_CODE,C.DEPT_CHN_DESC,SUM(B.ITEM_GAINPOINT) ITEM_GAINPOINT,&
                                 '' REMARK,'' DATE_ALL &
                          FROM   INF_EXAM_RECORD A,INF_EXAMSTANDDETAIL B ,SYS_DEPT C &
                          WHERE  TO_CHAR(A.EXAM_DATE,'YYYYMM') = <YEAR_MONTH> &
                          AND    A.PASS_FLG = 'Y'  &
                          AND    A.EXAM_STANDM = B.EXAMSTAND_CODE  &
                          AND    A.INFITEM_CODE = B.INFITEM_CODE  &
                          AND    A.DEPT_CODE = C.DEPT_CODE &
                          GROUP BY A.DEPT_CODE,C.DEPT_CHN_DESC &
                          ORDER BY A.DEPT_CODE
selectYearMonEXMCount.item=REGION_CODE
selectYearMonEXMCount.REGION_CODE=A.REGION_CODE=<REGION_CODE>                          
selectYearMonEXMCount.Debug=N 

//取得所有科室统计日期
selectYearMonEXMDate.Type=TSQL
selectYearMonEXMDate.SQL=SELECT DEPT_CODE,TO_CHAR(EXAM_DATE,'DD') EXAM_DATE,EXAM_NO &
                         FROM   INF_EXAM_RECORD  &
                         WHERE  TO_CHAR(EXAM_DATE,'YYYYMM') = <YEAR_MONTH> &
                         AND    PASS_FLG = 'Y' &
                         GROUP BY DEPT_CODE,EXAM_DATE,EXAM_NO &
                         ORDER BY DEPT_CODE,EXAM_DATE,EXAM_NO
selectYearMonEXMDate.item=REGION_CODE
selectYearMonEXMDate.REGION_CODE=REGION_CODE=<REGION_CODE>
selectYearMonEXMDate.Debug=N