# 
#  Title:感染控制科室检测结果录入module
# 
#  Description:感染控制科室检测结果录入module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author sundx 2009.06.15
#  version 1.0
#
Module.item=insertINFDeptExamM;selectINFExamStand;getDeptExamPeriodMaxSeq;selectINFExamRecordD;selectINFExamRecordM;updateINFExamRecord;deleteINFExamRecord

//写入检测结果主表
//========pangben modify 20110624 区域添加
insertINFDeptExamM.Type=TSQL
insertINFDeptExamM.SQL=INSERT INTO INF_EXAM_RECORD (EXAM_NO,EXAM_PERIOD,DEPT_CODE,EXAM_STANDM,INFITEM_CODE,&
                                                    PASS_FLG,REMARK,EXAM_DATE,OPT_USER,OPT_DATE,&
                                                    OPT_TERM,REGION_CODE) &
                                            VALUES (<EXAM_NO>,<EXAM_PERIOD>,<DEPT_CODE>,<EXAM_STANDM>,<INFITEM_CODE>,&
                                                    <PASS_FLG>,<REMARK>,<EXAM_DATE>,<OPT_USER>,<OPT_DATE>,&
                                                    <OPT_TERM>,<REGION_CODE>)
              
insertINFDeptExamM.Debug=N



//根据检测标准编码查询检测标准以及检测项目对照关系
selectINFExamStand.Type=TSQL
selectINFExamStand.SQL=SELECT A.EXAMSTAND_CODE EXAM_STANDM,A.INFITEM_CODE,'N' PASS_FLG,'' REMARK,A.ITEM_GAINPOINT &
                       FROM   INF_EXAMSTANDDETAIL A, SYS_DICTIONARY B, SYS_DICTIONARY C &
                       WHERE  A.EXAMSTAND_CODE = <EXAMSTAND_CODE> &
                       AND    B.GROUP_ID = 'INF_EXAMSTANDM' &
                       AND    A.EXAMSTAND_CODE = B.ID &
                       AND    C.GROUP_ID = 'INF_EXMITEM' &
                       AND    A.INFITEM_CODE = C.ID 
selectINFExamStand.Debug=N



//查询某科室一天最大检测记录序号
getDeptExamPeriodMaxSeq.Type=TSQL
getDeptExamPeriodMaxSeq.SQL=SELECT MAX(SUBSTR(EXAM_PERIOD,7,LENGTH(EXAM_PERIOD))) EXAM_PERIOD &
                            FROM   INF_EXAM_RECORD &
                            WHERE  DEPT_CODE = <DEPT_CODE> &
                            AND    SUBSTR(EXAM_PERIOD,1,6) = <EXAM_PERIOD>
getDeptExamPeriodMaxSeq.Debug=N





//取得监测记录主信息
selectINFExamRecordD.Type=TSQL
selectINFExamRecordD.SQL=SELECT A.EXAM_STANDM,A.INFITEM_CODE,A.PASS_FLG,A.REMARK,B.ITEM_GAINPOINT  &
                         FROM   INF_EXAM_RECORD A,INF_EXAMSTANDDETAIL B &
                         WHERE  A.EXAM_STANDM = B.EXAMSTAND_CODE &
                         AND    A.INFITEM_CODE = B.INFITEM_CODE
selectINFExamRecordD.item=EXAM_NO;EXAM_PERIOD;DEPT_CODE;EXAM_STANDM;EXAM_DATE 
selectINFExamRecordD.EXAM_NO=A.EXAM_NO= <EXAM_NO> 
selectINFExamRecordD.EXAM_PERIOD=A.EXAM_PERIOD LIKE <EXAM_PERIOD>||'%' 
selectINFExamRecordD.DEPT_CODE=A.DEPT_CODE= <DEPT_CODE> 
selectINFExamRecordD.EXAM_STANDM=A.EXAM_STANDM= <EXAM_STANDM> 
selectINFExamRecordD.EXAM_DATE=A.EXAM_DATE= <EXAM_DATE>
selectINFExamRecordD.Debug=N

//取得监测记录主信息
//========pangben modify 20110624 区域添加
selectINFExamRecordM.Type=TSQL
selectINFExamRecordM.SQL=SELECT A.REGION_CODE,A.EXAM_NO,A.EXAM_PERIOD,A.DEPT_CODE,A.EXAM_DATE,A.EXAM_STANDM,&
                         SUM(CASE WHEN PASS_FLG = 'Y' THEN B.ITEM_GAINPOINT ELSE 0 END) TOT_GAINPOINT &
                         FROM   INF_EXAM_RECORD A, INF_EXAMSTANDDETAIL B &
                         WHERE  A.EXAM_STANDM = B.EXAMSTAND_CODE &
                         AND    A.INFITEM_CODE = B.INFITEM_CODE &
                         GROUP BY EXAM_NO,EXAM_PERIOD,DEPT_CODE,EXAM_DATE,A.EXAM_STANDM,A.REGION_CODE
selectINFExamRecordM.item=EXAM_NO;EXAM_PERIOD;DEPT_CODE;EXAM_STANDM;EXAM_DATE;REGION_CODE
selectINFExamRecordM.EXAM_NO=EXAM_NO= <EXAM_NO>
selectINFExamRecordM.REGION_CODE=A.REGION_CODE= <REGION_CODE>
selectINFExamRecordM.EXAM_PERIOD=EXAM_PERIOD LIKE <EXAM_PERIOD>||'%'
selectINFExamRecordM.DEPT_CODE=DEPT_CODE= <DEPT_CODE>
selectINFExamRecordM.EXAM_STANDM=EXAM_STANDM= <EXAM_STANDM>
selectINFExamRecordM.EXAM_DATE=EXAM_DATE= <EXAM_DATE>
selectINFExamRecordM.Debug=N




//更新监测记录主信息
updateINFExamRecord.Type=TSQL
updateINFExamRecord.SQL=UPDATE INF_EXAM_RECORD SET  PASS_FLG = <PASS_FLG>, &
                                                    REMARK = <REMARK>, &
                                                    OPT_USER = <OPT_USER>,&
					            OPT_DATE = <OPT_DATE>,&
                                                    OPT_TERM = <OPT_TERM> &
                                              WHERE EXAM_NO = <EXAM_NO> &
                                              AND   EXAM_PERIOD = <EXAM_PERIOD> &
                                              AND   DEPT_CODE = <DEPT_CODE> &
                                              AND   EXAM_STANDM = <EXAM_STANDM> &
                                              AND   INFITEM_CODE = <INFITEM_CODE>
updateINFExamRecord.Debug=N




//删除监测记录主信息
deleteINFExamRecord.Type=TSQL
deleteINFExamRecord.SQL=DELETE FROM INF_EXAM_RECORD &
                        WHERE EXAM_NO = <EXAM_NO> &
                        AND   EXAM_PERIOD = <EXAM_PERIOD> &
                        AND   DEPT_CODE = <DEPT_CODE> &
                        AND   EXAM_STANDM = <EXAM_STANDM> &
                        AND   INFITEM_CODE = <INFITEM_CODE>
deleteINFExamRecord.Debug=N