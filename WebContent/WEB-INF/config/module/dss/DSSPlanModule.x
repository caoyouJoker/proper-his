# 
#  Title:6.1.1.	评估方案module
# 
#  Description:6.1.1.	评估方案module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.10.12
#  version 1.0
#
Module.item=queryDSSEvalPlanM;queryDSSEvalPlanD;queryDSSEvalPlanDByCode;deleteDSSEvalPlanM;deleteDSSEvalPlanD;deleteDSSEvalPlanDByKPI;getKPI;getSeqM;getSeqD

//查询评估方案信息主项
queryDSSEvalPlanM.Type=TSQL
queryDSSEvalPlanM.SQL=SELECT PLAN_CODE,PLAN_DESC,PY1,PY2,SEQ,&
                             DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM &
                      FROM   DSS_EVAL_PLANM &
                      ORDER BY PLAN_CODE
queryDSSEvalPlanM.Debug=N

//查询评估方案信息细项
queryDSSEvalPlanD.Type=TSQL
queryDSSEvalPlanD.SQL=  SELECT A.PLAN_CODE,A.KPI_CODE,B.KPI_DESC,A.WEIGHT,A.KPI_LEVEL1,&
                               A.KPI_LEVEL2,A.SEQ,A.DESCRIPTION,A.OPT_USER,A.OPT_DATE,&
                               A.OPT_TERM &
                        FROM   DSS_EVAL_PLAND A,DSS_KPI B &
                        WHERE  A.KPI_CODE = B.KPI_CODE &
                        ORDER BY PLAN_CODE,A.KPI_CODE
queryDSSEvalPlanD.Debug=N

//删除绩效评估表主表
deleteDSSEvalPlanM.Type=TSQL
deleteDSSEvalPlanM.SQL=DELETE FROM "DSS_EVAL_PLANM" WHERE "PLAN_CODE" = <PLAN_CODE>
deleteDSSEvalPlanM.Debug=N

//删除绩效评估表细表
deleteDSSEvalPlanD.Type=TSQL
deleteDSSEvalPlanD.SQL=DELETE FROM "DSS_EVAL_PLAND" WHERE "PLAN_CODE" = <PLAN_CODE>
deleteDSSEvalPlanD.Debug=N

//删除绩效评估表细表
deleteDSSEvalPlanDByKPI.Type=TSQL
deleteDSSEvalPlanDByKPI.SQL=DELETE FROM "DSS_EVAL_PLAND" WHERE "PLAN_CODE" = <PLAN_CODE> AND "KPI_CODE" = <KPI_CODE> 
deleteDSSEvalPlanDByKPI.Debug=N

//查询评估方案信息细项
getDSSEvalMaxMSeq.Type=TSQL
getDSSEvalMaxMSeq.SQL=SELECT MAX(SEQ) FROM   DSS_EVAL_PLAND
getDSSEvalMaxMSeq.Debug=N

//查询评估方案信息细项
getDSSEvalMaxDSeq.Type=TSQL
getDSSEvalMaxDSeq.SQL=SELECT MAX(SEQ) FROM   DSS_EVAL_PLANM
getDSSEvalMaxDSeq.Debug=N


//得到KPI指标
getKPI.Type=TSQL
getKPI.SQL=SELECT PARENT_CODE FROM DSS_KPI WHERE KPI_CODE = <KPI_CODE>
getKPI.Debug=N


//得到最大顺序号M
getSeqM.Type=TSQL
getSeqM.SQL=SELECT MAX(SEQ) SEQ FROM DSS_EVAL_PLANM
getSeqM.Debug=N


//得到最大顺序号M
getSeqD.Type=TSQL
getSeqD.SQL=SELECT MAX(SEQ) SEQ FROM DSS_EVAL_PLAND
getSeqD.Debug=N