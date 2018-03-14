# 
#  Title:主库部门设定module
# 
#  Description:主库部门设定module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=selectDevDeptInf;updateDevDeptInf;insertDevDeptInf;deleteDevDeptInf;getDevDeptMaxSeq

//检索设备主库信息
selectDevDeptInf.Type=TSQL
selectDevDeptInf.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_DESCRIBE,MEDDEV_FLG,INFDEV_FLG, &
                            OTHERDEV_FLG,OPT_USER,OPT_DATE,OPT_TERM,&
                            PY1,PY2,SEQ &
                     FROM   DEV_ORG &
                     WHERE  DEPT_CODE = <DEPT_CODE>
selectDevDeptInf.Debug=N

//更新设备主库信息
updateDevDeptInf.Type=TSQL
updateDevDeptInf.SQL=UPDATE DEV_ORG SET DEPT_CODE = <DEPT_CODE>,DEPT_DESC = <DEPT_DESC>,DEPT_DESCRIBE = <DEPT_DESCRIBE>,&
                                        MEDDEV_FLG = <MEDDEV_FLG>,INFDEV_FLG = <INFDEV_FLG>,OTHERDEV_FLG = <OTHERDEV_FLG>,&
                                        OPT_USER = <OPT_USER>,OPT_DATE = <OPT_DATE>,OPT_TERM = <OPT_TERM>,&
                                        PY1 = <PY1>,PY2 = <PY2>,SEQ = <SEQ>,REGION_CODE = <REGION_CODE> &
                     WHERE  DEPT_CODE = <DEPT_CODE>
updateDevDeptInf.Debug=N

//写入设备主库信息
insertDevDeptInf.Type=TSQL
insertDevDeptInf.SQL=INSERT INTO DEV_ORG (DEPT_CODE,DEPT_DESC,DEPT_DESCRIBE,MEDDEV_FLG,INFDEV_FLG,&
                                          OTHERDEV_FLG,OPT_USER,OPT_DATE,REGION_CODE, &
                                          OPT_TERM,PY1,PY2,SEQ) &
                                  VALUES (<DEPT_CODE>,<DEPT_DESC>,<DEPT_DESCRIBE>,<MEDDEV_FLG>,<INFDEV_FLG>,&
                                          <OTHERDEV_FLG>,<OPT_USER>,<OPT_DATE>,<REGION_CODE>,&
                                          <OPT_TERM>,<PY1>,<PY2>,<SEQ>)
insertDevDeptInf.Debug=N



//写入设备主库信息
deleteDevDeptInf.Type=TSQL
deleteDevDeptInf.SQL=DELETE FROM DEV_ORG &
                     WHERE  DEPT_CODE = <DEPT_CODE>
deleteDevDeptInf.Debug=N



//检索设备主库最大顺序号
getDevDeptMaxSeq.Type=TSQL
getDevDeptMaxSeq.SQL=SELECT MAX(SEQ) SEQ &
                     FROM   DEV_ORG 
getDevDeptMaxSeq.Debug=N