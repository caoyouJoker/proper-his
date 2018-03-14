# 
#  Title:6.1.1.	科室评估方案module
# 
#  Description:6.1.1.	科室评估方案module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.10.12
#  version 1.0
#
Module.item=queryDSSDeptPlanM;queryDSSDeptPlanD;queryDSSDeptCodeLength;queryDSSDeptPlanDInf;queryEvalPlan;queryDeptInf;deleteDeptEval;deleteDeptEvalM;insertDeptEval;insertDeptEvalM;getSeqM;getSeqD;getKPIInfo;queryDeptInfByDept;getKPIInfoByPlan

//查询科室评估方案信息主项
queryDSSDeptPlanM.Type=TSQL
queryDSSDeptPlanM.SQL=SELECT A.DEPT_CODE,B.DEPT_CHN_DESC,A.PLAN_CODE,C.PLAN_DESC,A.DESCRIPTION,&
                             A.SEQ,A.OPT_USER,A.OPT_DATE,A.OPT_TERM &
                      FROM   DSS_DEPT_EVAL A,SYS_DEPT B,DSS_EVAL_PLANM C &
                      WHERE  A.DEPT_CODE = B.DEPT_CODE &
                      AND    A.PLAN_CODE = C.PLAN_CODE &
                      ORDER BY A.DEPT_CODE,A.PLAN_CODE
queryDSSDeptPlanM.Debug=N



//查询科室评估方案信息细项
queryDSSDeptPlanD.Type=TSQL
queryDSSDeptPlanD.SQL=SELECT A.DEPT_CODE,A.PLAN_CODE,A.KPI_CODE,A.KPI_LEVEL1,A.KPI_LEVEL2, &
                             A.WEIGHT,A.SEQ,A.DESCRIPTION,A.KPI_VALUE,A.KPI_GOAL,A.KPI_STATUS,&
                             A.KPI_ATTRIBUTE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.DEPT_CHN_DESC,&
                             C.PLAN_DESC,D.KPI_DESC,A.LEAF & 
                      FROM   DSS_DEPT_EVALD A,SYS_DEPT B,DSS_EVAL_PLANM C,DSS_KPI D &
                      WHERE  A.DEPT_CODE = B.DEPT_CODE &
                      AND    A.PLAN_CODE = C.PLAN_CODE &
                      AND    A.KPI_CODE = D.KPI_CODE &
                      ORDER BY A.DEPT_CODE,A.KPI_CODE,A.KPI_CODE
queryDSSDeptPlanD.Debug=N

//查询科室评估方案信息细项部分信息
queryDSSDeptPlanDInf.Type=TSQL
queryDSSDeptPlanDInf.SQL=SELECT A.DEPT_CODE,A.PLAN_CODE,B.DEPT_CHN_DESC,C.PLAN_DESC,A.LEAF,&
                                SUM(A.WEIGHT) WEIGHT & 
                      FROM      DSS_DEPT_EVALD A,SYS_DEPT B,DSS_EVAL_PLANM C &
                      WHERE     A.DEPT_CODE = B.DEPT_CODE &
                      AND       A.PLAN_CODE = C.PLAN_CODE &
                      GROUP BY  A.DEPT_CODE,A.PLAN_CODE,B.DEPT_CHN_DESC,C.PLAN_DESC,A.LEAF &
                      ORDER BY  A.DEPT_CODE,A.PLAN_CODE
queryDSSDeptPlanDInf.Debug=N


//查询科室评估方案信息细项
queryDSSDeptCodeLength.Type=TSQL
queryDSSDeptCodeLength.SQL=SELECT DISTINCT LENGTH(DEPT_CODE) CODE_LENGTH &
                           FROM   DSS_DEPT_EVALD &
                           ORDER BY CODE_LENGTH
queryDSSDeptCodeLength.Debug=N




//取得绩效等级信息
queryEvalPlan.Type=TSQL
queryEvalPlan.SQL=SELECT A.PLAN_CODE,A.KPI_CODE,A.WEIGHT,A.KPI_LEVEL1,A.KPI_LEVEL2, &
                         B.KPI_VALUE,B.KPI_GOAL,B.KPI_STATUS,B.KPI_ATTRIBUTE &
                  FROM   DSS_EVAL_PLAND A,DSS_KPI B &
                  WHERE  A.PLAN_CODE  = <PLAN_CODE> &
                  AND    A.KPI_CODE = B.KPI_CODE
queryEvalPlan.Debug=N

//取得科室信息
queryDeptInf.Type=TSQL
queryDeptInf.SQL=SELECT DEPT_CODE,FINAL_FLG LEAF &
                 FROM   SYS_DEPT &
                 WHERE  DEPT_CODE LIKE <DEPT_CODE>
queryDeptInf.Debug=N

//删除明细表
deleteDeptEval.Type=TSQL
deleteDeptEval.SQL=DELETE DSS_DEPT_EVALD WHERE DEPT_CODE = <DEPT_CODE> AND PLAN_CODE = <PLAN_CODE>
deleteDeptEval.Debug=N


//删除主表
deleteDeptEvalM.Type=TSQL
deleteDeptEvalM.SQL=DELETE DSS_DEPT_EVAL WHERE DEPT_CODE = <DEPT_CODE> AND PLAN_CODE = <PLAN_CODE>
deleteDeptEvalM.Debug=N

//写入细表
insertDeptEval.Type=TSQL
insertDeptEval.SQL=INSERT INTO DSS_DEPT_EVALD (DEPT_CODE,PLAN_CODE,KPI_CODE,KPI_LEVEL1,KPI_LEVEL2,&
                                               WEIGHT,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                                               KPI_STATUS,KPI_ATTRIBUTE,OPT_USER,OPT_DATE,OPT_TERM,&
                                               LEAF)&
                                       VALUES (<DEPT_CODE>,<PLAN_CODE>,<KPI_CODE>,<KPI_LEVEL1>,<KPI_LEVEL2>,&
                                               <WEIGHT>,<SEQ>,<DESCRIPTION>,<KPI_VALUE>,<KPI_GOAL>,&
                                               <KPI_STATUS>,<KPI_ATTRIBUTE>,<OPT_USER>,SYSDATE,<OPT_TERM>,&
                                               <LEAF>)
insertDeptEval.Debug=N

//写入主表
insertDeptEvalM.Type=TSQL
insertDeptEvalM.SQL=INSERT INTO DSS_DEPT_EVAL (DEPT_CODE,PLAN_CODE,SEQ,DESCRIPTION,OPT_USER,&
                                               OPT_DATE,OPT_TERM)&
                                       VALUES (<DEPT_CODE>,<PLAN_CODE>,<SEQ>,<DESCRIPTION>,<OPT_USER>,&
                                               SYSDATE,<OPT_TERM>)
insertDeptEvalM.Debug=N


//得到最大顺序号M
getSeqM.Type=TSQL
getSeqM.SQL=SELECT MAX(SEQ) SEQ FROM DSS_DEPT_EVAL
getSeqM.Debug=N


//得到最大顺序号M
getSeqD.Type=TSQL
getSeqD.SQL=SELECT MAX(SEQ) SEQ FROM DSS_DEPT_EVALD
getSeqD.Debug=N


//得到KPI信息
getKPIInfo.Type=TSQL
getKPIInfo.SQL=SELECT B.KPI_LEVEL1,B.KPI_LEVEL2,B.WEIGHT,A.KPI_VALUE,A.KPI_GOAL, &
                      A.KPI_STATUS,A.KPI_ATTRIBUTE &
               FROM   DSS_KPI A,DSS_EVAL_PLAND B &
               WHERE  A.KPI_CODE = <KPI_CODE> &
               AND    B.PLAN_CODE = <PLAN_CODE> &
               AND    A.KPI_CODE = B.KPI_CODE 
getKPIInfo.Debug=N


//取得科室信息DEPT_CODE
queryDeptInfByDept.Type=TSQL
queryDeptInfByDept.SQL=SELECT DEPT_CODE,FINAL_FLG LEAF &
                       FROM   SYS_DEPT &
                       WHERE  DEPT_CODE = <DEPT_CODE>
queryDeptInfByDept.Debug=N



//通过PLAN得到KPI信息
getKPIInfoByPlan.Type=TSQL
getKPIInfoByPlan.SQL=SELECT A.KPI_CODE,B.KPI_LEVEL1,B.KPI_LEVEL2,B.WEIGHT,A.KPI_VALUE,A.KPI_GOAL, &
                            A.KPI_STATUS,A.KPI_ATTRIBUTE &
               FROM   DSS_KPI A,DSS_EVAL_PLAND B &
               WHERE  B.PLAN_CODE = <PLAN_CODE> &
               AND    A.KPI_CODE = B.KPI_CODE 
getKPIInfoByPlan.Debug=N