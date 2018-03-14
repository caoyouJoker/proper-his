# 
#  Title:6.1.1.	指标等级设定module
# 
#  Description:6.1.1.	指标等级设定module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.10.12
#  version 1.0
#
Module.item=queryDSSKPI;queryDSSKPIByCode;queryDSSKPIByParentCode;updateDSSKPIByCode;insertDSSKPI;deleteDSSKPI;getMaxSeq
//查询等级指标信息
queryDSSKPI.Type=TSQL
queryDSSKPI.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,PY1,&
                       PY2,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                       KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,OPT_USER,OPT_DATE, &
                       OPT_TERM &
                FROM   DSS_KPI &
                ORDER BY KPI_CODE
queryDSSKPI.Debug=N


//根据KPI_CODE查询等级指标信息
queryDSSKPIByCode.Type=TSQL
queryDSSKPIByCode.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,PY1,&
                             PY2,SEQ,DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                             KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,OPT_USER,OPT_DATE, &
                             OPT_TERM &
                       FROM  DSS_KPI &
                       WHERE KPI_CODE = <KPI_CODE>
queryDSSKPIByCode.Debug=N

//根据PARENT_CODE查询等级指标信息
queryDSSKPIByParentCode.Type=TSQL
queryDSSKPIByParentCode.SQL=SELECT KPI_CODE,KPI_DESC,PARENT_CODE,LEAF,A.PY1,&
                                   A.PY2,A.SEQ,A.DESCRIPTION,KPI_VALUE,KPI_GOAL,&
                                   KPI_STATUS,KPI_ATTRIBUTE,KPI_KIND,B.USER_NAME OPT_USER,A.OPT_DATE, &
                                   A.OPT_TERM &
                            FROM  DSS_KPI A, SYS_OPERATOR B &
                            WHERE PARENT_CODE = <PARENT_CODE> &
                            AND   A.OPT_USER = B.USER_ID
queryDSSKPIByParentCode.Debug=N


//根据KPI_CODE更新KPI相关信息
updateDSSKPIByCode.Type=TSQL
updateDSSKPIByCode.SQL=UPDATE DSS_KPI &
                       SET    KPI_CODE = <KPI_CODE>,KPI_DESC = <KPI_DESC>,PARENT_CODE=<PARENT_CODE>,&
                              LEAF = <LEAF>,PY1 = <PY1>,PY2 = <PY2>,&
                              SEQ = <SEQ>,DESCRIPTION = <DESCRIPTION>,KPI_VALUE = <KPI_VALUE>,&
                              KPI_GOAL = <KPI_GOAL>,KPI_STATUS = <KPI_STATUS>,KPI_ATTRIBUTE = <KPI_ATTRIBUTE>,&
                              KPI_KIND = <KPI_KIND>,OPT_USER = <OPT_USER>,OPT_DATE = <OPT_DATE>,&
                              OPT_TERM = <OPT_TERM> &
                       WHERE KPI_CODE = <KPI_CODE>
updateDSSKPIByCode.Debug=N



//新增DSSKPI
insertDSSKPI.Type=TSQL
insertDSSKPI.SQL=INSERT INTO DSS_KPI &
                             (KPI_CODE,KPI_DESC,PARENT_CODE,&
                              LEAF,PY1,PY2,&
                              SEQ,DESCRIPTION,KPI_VALUE,&
                              KPI_GOAL,KPI_STATUS,KPI_ATTRIBUTE,&
                              KPI_KIND,OPT_USER,OPT_DATE,&
                              OPT_TERM)&
                      VALUES(<KPI_CODE>,<KPI_DESC>,<PARENT_CODE>,&
                             <LEAF>,<PY1>,<PY2>,&
                             <SEQ>,<DESCRIPTION>,<KPI_VALUE>,&
                             <KPI_GOAL>,<KPI_STATUS>,<KPI_ATTRIBUTE>,&
                             <KPI_KIND>,<OPT_USER>,<OPT_DATE>,&
                             <OPT_TERM>)
insertDSSKPI.Debug=N

//删除DSSKPI
deleteDSSKPI.Type=TSQL
deleteDSSKPI.SQL=DELETE FROM DSS_KPI WHERE KPI_CODE = <KPI_CODE>
deleteDSSKPI.Debug=N

//删除DSSKPI
getMaxSeq.Type=TSQL
getMaxSeq.SQL=SELECT MAX(SEQ) AS SEQ FROM DSS_KPI
getMaxSeq.Debug=N