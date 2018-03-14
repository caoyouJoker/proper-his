##################################################
# <p>Title:合理用药工具类 </p>
#
# <p>Description:合理用药工具类  </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:JavaHis </p>
#
# @author 
# @version 1.0
##################################################
Module.item=queryPatInfo;queryVisitCount;queryAllergy;queryDiag;queryOrder;queryDrug;queryadmPatInfo;queryadmVisitCount;queryamdDiag;queryodiOrder
//门诊病患查询
queryPatInfo.Type=TSQL
queryPatInfo.SQL=SELECT A.MR_NO,B.PAT_NAME,C.CHN_DESC SEX_DESC,B.BIRTH_DATE, &
                        A.WEIGHT,A.HEIGHT,A.REALDEPT_CODE,D.DEPT_CHN_DESC,A.REALDR_CODE, &
                        E.USER_NAME &
                 FROM	REG_PATADM A,SYS_PATINFO B,SYS_DICTIONARY C,SYS_DEPT D,SYS_OPERATOR E &
                 WHERE  A.CASE_NO = <CASE_NO> &
                 AND    A.MR_NO = B.MR_NO &
                 AND    B.SEX_CODE = C.ID &
                 AND    C.GROUP_ID = 'SYS_SEX' &
                 AND    A.REALDEPT_CODE = D.DEPT_CODE &
                 AND    A.REALDR_CODE = E.USER_ID
queryPatInfo.Debug=N

//住院病患查询
queryadmPatInfo.Type=TSQL
queryadmPatInfo.SQL=SELECT A.MR_NO,B.PAT_NAME,C.CHN_DESC SEX_DESC,B.BIRTH_DATE, &
                        A.WEIGHT,A.HEIGHT,A.IN_DEPT_CODE,D.DEPT_CHN_DESC,A.VS_DR_CODE, &
                        E.USER_NAME &
                 FROM	ADM_INP A,SYS_PATINFO B,SYS_DICTIONARY C,SYS_DEPT D,SYS_OPERATOR E &
                 WHERE  A.CASE_NO = <CASE_NO> &
                 AND    A.MR_NO = B.MR_NO &
                 AND    B.SEX_CODE = C.ID &
                 AND    C.GROUP_ID = 'SYS_SEX' &
                 AND    A.IN_DEPT_CODE = D.DEPT_CODE &
                 AND    A.VS_DR_CODE = E.USER_ID
queryadmPatInfo.Debug=N

//门诊访问次数
queryVisitCount.Type=TSQL
queryVisitCount.SQL=SELECT COUNT(CASE_NO) VISIT_COUNT &
                    FROM   REG_PATADM &
                    WHERE  MR_NO = <MR_NO> 
queryVisitCount.Debug=N

//住院访问次数
queryadmVisitCount.Type=TSQL
queryadmVisitCount.SQL=SELECT COUNT(CASE_NO) VISIT_COUNT &
                    FROM   ADM_INP &
                    WHERE  MR_NO = <MR_NO> 
queryadmVisitCount.Debug=N

//过敏史查询
queryAllergy.Type=TSQL
queryAllergy.SQL=SELECT A.DRUGORINGRD_CODE CODE, &
                        CASE WHEN A.DRUGORINGRD_CODE = 'ZZZ00002' THEN A.ALLERGY_NOTE ELSE B.ORDER_DESC END ORDER_DESC, &
                        A.ALLERGY_NOTE NOTE &
                 FROM   OPD_DRUGALLERGY A, SYS_FEE B &
                 WHERE  A.MR_NO = <MR_NO> &
                 AND    A.DRUGORINGRD_CODE = B.ORDER_CODE(+) &
                 ORDER BY A.DRUGORINGRD_CODE
queryAllergy.Debug=N

//门诊诊断
queryDiag.Type=TSQL
queryDiag.SQL=SELECT A.ICD_CODE CODE, &
                     CASE WHEN A.ICD_CODE = '000.0' THEN A.DIAG_NOTE ELSE B.ICD_CHN_DESC END ICD_CHN_DESC &
              FROM   OPD_DIAGREC A, SYS_DIAGNOSIS B &
              WHERE  A.CASE_NO = <CASE_NO> &
              AND    A.ICD_CODE = B.ICD_CODE(+)
queryDiag.Debug=N

//住院诊断
queryamdDiag.Type=TSQL
queryamdDiag.SQL=SELECT A.ICD_CODE CODE, &
                     CASE WHEN A.ICD_CODE = '000.0' THEN A.DESCRIPTION ELSE B.ICD_CHN_DESC END ICD_CHN_DESC &
              FROM   ADM_INPDIAG A, SYS_DIAGNOSIS B &
              WHERE  A.CASE_NO = <CASE_NO> &
              AND    A.ICD_CODE = B.ICD_CODE(+)
queryamdDiag.Debug=N

//门诊医嘱
queryOrder.Type=TSQL
queryOrder.SQL=SELECT A.RX_NO,A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,B.FREQ_TIMES||'/'||B.CYCLE FREQ,&
                      A.ORDER_DATE,A.TAKE_DAYS,C.ROUTE_CHN_DESC,A.LINK_NO,&
                      D.USER_ID||'/'||D.USER_NAME DR &
               FROM   OPD_ORDER A,SYS_PHAFREQ B,SYS_PHAROUTE C,SYS_OPERATOR D,SYS_UNIT F &
               WHERE  A.CASE_NO = <CASE_NO> &            
               AND    A.CAT1_TYPE = 'PHA' &
               AND    A.FREQ_CODE = B.FREQ_CODE(+)  &
               AND    A.ROUTE_CODE = C.ROUTE_CODE (+) &
               AND    A.DR_CODE = D.USER_ID (+) &
               AND    A.MEDI_UNIT = F.UNIT_CODE(+)
queryOrder.item=ORDER_CAT1_CODE
queryOrder.ORDER_CAT1_CODE=A.ORDER_CAT1_CODE=<ORDER_CAT1_CODE>
queryOrder.Debug=N

//住院医嘱
queryodiOrder.Type=TSQL
queryodiOrder.SQL=SELECT A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC,A.MEDI_QTY,F.UNIT_CHN_DESC,B.FREQ_TIMES||'/'||B.CYCLE FREQ,&
                      A.ORDER_DATE,A.TAKE_DAYS,C.ROUTE_CHN_DESC,A.LINK_NO,A.RX_KIND, &
                      D.USER_ID||'/'||D.USER_NAME DR &
               FROM   ODI_ORDER A,SYS_PHAFREQ B,SYS_PHAROUTE C,SYS_OPERATOR D,SYS_UNIT F &
               WHERE  A.CASE_NO = <CASE_NO>  &              
               AND    A.CAT1_TYPE = 'PHA' &
               AND    A.FREQ_CODE = B.FREQ_CODE &
               AND    A.ROUTE_CODE = C.ROUTE_CODE &
               AND    A.VS_DR_CODE = D.USER_ID &
               AND    A.MEDI_UNIT = F.UNIT_CODE
queryodiOrder.item=ORDER_CAT1_CODE
queryodiOrder.ORDER_CAT1_CODE=A.ORDER_CAT1_CODE=<ORDER_CAT1_CODE>
queryodiOrder.Debug=N

//药品信息查询
queryDrug.Type=TSQL
queryDrug.SQL=SELECT A.ORDER_CODE,A.ORDER_DESC,B.UNIT_CHN_DESC,C.ROUTE_CHN_DESC &
              FROM   PHA_BASE A,SYS_UNIT B,SYS_PHAROUTE C &
              WHERE  A.ORDER_CODE = <ORDER_CODE> &
              AND    A.DOSAGE_UNIT = B.UNIT_CODE &
              AND    A.ROUTE_CODE = C.ROUTE_CODE
queryDrug.Debug=N



























