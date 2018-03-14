 
  #
   # Title: 使用抗菌药物出院患者汇总
   #
   # Description: 使用抗菌药物出院患者汇总
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author yanj 2013.3.21

Module.item=getPatientDetail
//查询使用抗菌药物出院患者的信息
getPatientDetail.Type=TSQL
getPatientDetail.SQL=SELECT K.REGION_CHN_ABN AS REGION_CHN_DESC, A.CASE_NO, F.MR_NO AS MR_NO, &
			    G.PAT_NAME AS NAME, H.CHN_DESC AS SEX_CODE, FLOOR(MONTHS_BETWEEN(SYSDATE,BIRTH_DATE)/12) AS GRADE,&
                            I.DEPT_CHN_DESC AS DEPT_DESC, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC,B.SPECIFICATION AS SPECIFICATION, &
			    C.UNIT_CHN_DESC AS UNIT_DESC, A.OWN_PRICE AS OWN_PRICE, SUM (A.DOSAGE_QTY) AS  SUM_QTY, &
			    SUM (A.DOSAGE_QTY * A.OWN_PRICE) AS SUM_AMT, G.IPD_NO AS CASE_NO, J.DS_DATE AS DS_DATE,TRUNC(J.DS_DATE-J.IN_DATE) AS DAYS &
				FROM IBS_ORDD A, SYS_FEE B,SYS_UNIT C,PHA_BASE E,IBS_ORDM F,SYS_PATINFO G,&
				     SYS_dICTIONARY H,SYS_DEPT I,ADM_INP J,SYS_REGION,SYS_REGION K   &
					WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DOSAGE_UNIT = C.UNIT_CODE AND A.ORDER_CODE = E.ORDER_CODE AND A.CASE_NO = F.CASE_NO &
                                              AND A.CASE_NO_SEQ = F.CASE_NO_SEQ AND F.MR_NO = G.MR_NO AND G.SEX_CODE = H.ID AND J.DS_DEPT_CODE = I.DEPT_CODE &
                                              AND A.CASE_NO = J.CASE_NO AND J.DS_DATE BETWEEN TO_DATE (<START_DATE>,'yyyy/MM/dd HH24:mi:ss') &
                                              AND TO_DATE (<END_DATE>, 'yyyy/MM/dd HH24:mi:ss') AND H.GROUP_ID = 'SYS_SEX' AND A.CAT1_TYPE = 'PHA' &
                                              AND E.ANTIBIOTIC_CODE IS NOT NULL &
                                                  GROUP BY A.ORDER_CODE,A.CASE_NO,F.MR_NO,B.ORDER_DESC,b.SPECIFICATION,C.UNIT_CHN_DESC,A.OWN_PRICE,G.PAT_NAME,a.ORDER_CODE,K.REGION_CHN_ABN, &
                                                        B.ORDER_DESC,B.SPECIFICATION,C.UNIT_CHN_DESC,G.IPD_NO,I.DEPT_CHN_DESC,H.CHN_DESC,J.DS_DATE,J.IN_DATE,BIRTH_DATE &
                                                            ORDER BY A.ORDER_CODE, A.CASE_NO
getPatientDetail.item=ORDER_CODE;DEPT_CODE;MR_NO;DS_FLG
getPatientDetail.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>   
getPatientDetail.DEPT_CODE=I.DEPT_CHN_DESC =<DEPT_CODE>
getPatientDetail.MR_NO=F.MR_NO=<MR_NO>
getPatientDetail.DS_FLG=A.DS_FLG=<DS_FLG>
getPatientDetail.Debug=N



  
  
  
  
  