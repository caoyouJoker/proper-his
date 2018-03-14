#
   # Title: 销售排名
   #
   # Description:销售排名
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author yanj 2013.3.18

Module.item=getSellOrder;getAllOrder;getSellOrder1;getAllOrder1;getSellOrderOPD;getSellOrderODI;getAllOrderOPD;&
           getAllOrderODI;getSellOrderOPD1;getAllOrderOPD1;getSellOrderODI1;getAllOrderODI1;getAllOrder6;getSellOrder3;getAllOrder3;getSellOrder6;getSellOrderO;getSellOrderE; &
	   getAllOrderE;getAllOrderO;getSellOrderE1;getSellOrderO1;getAllOrderE1;getAllOrderO1
//抗生素查询
getSellOrder.Type=TSQL
getSellOrder.SQL=SELECT DD.REGION_CHN_ABN,DD.ORDER_CODE, DD.ORDER_DESC, &
  			 DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
   			 SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT  &
    		 FROM (&
    			(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
         			 A.SPECIFICATION AS SPECIFICATION,&
         			 A.OWN_PRICE AS OWN_PRICE,SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
          	   FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C &
                     WHERE C.ADM_TYPE IN ('O', 'E') &
                        AND A.ORDER_CODE = B.ORDER_CODE &
                        AND A.CASE_NO=C.CASE_NO &
                        AND F.REGION_CODE=C.REGION_CODE &
          	        AND b.ANTIBIOTIC_CODE IS NOT NULL &
     			AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                        AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                        //fux modify 20150803
                        AND A.PHA_RETN_DATE IS  NULL  &
                        AND A.CAT1_TYPE = 'PHA' &
		    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
		       A.SPECIFICATION, A.OWN_PRICE) &
                   UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                        B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                     SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
               FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F,ADM_INP D  &
                     WHERE A.ORDER_CODE = B.ORDER_CODE &
                       AND A.ORDER_CODE = C.ORDER_CODE &
                       AND A.CASE_NO = D.CASE_NO &
		       AND A.CAT1_TYPE= 'PHA'  &
		       AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                       AND D.REGION_CODE=F.REGION_CODE &
		       AND A.DS_FLG = <DS_FLG> &
                       AND A.BILL_DATE  &
                     BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                       AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                     GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                  B.SPECIFICATION, A.OWN_PRICE)&
                      ) DD  &
              GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, DD.SPECIFICATION, DD.OWN_PRICE &
                   ORDER BY SUM_AMT DESC
getSellOrder.item = 0RDER_CODE
getSellOrder.0RDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getSellOrder.Debug=N

//抗生素查询
getSellOrder6.Type=TSQL
getSellOrder6.SQL=SELECT DD.REGION_CHN_ABN,DD.ORDER_CODE, DD.ORDER_DESC, &
  			 DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
   			 SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT  &
    		 FROM (&
    			(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
         			 A.SPECIFICATION AS SPECIFICATION,&
         			 A.OWN_PRICE AS OWN_PRICE,SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
          	   FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C &
                     WHERE C.ADM_TYPE IN ('O', 'E') &
                        AND A.ORDER_CODE = B.ORDER_CODE &
                        AND A.CASE_NO=C.CASE_NO &
                        AND F.REGION_CODE=C.REGION_CODE &
          	        AND b.ANTIBIOTIC_CODE IS NOT NULL &
     			AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                        AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                        //fux modify 20150803
                        AND A.PHA_RETN_DATE IS  NULL  &
                        AND A.CAT1_TYPE = 'PHA' &
		    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
		       A.SPECIFICATION, A.OWN_PRICE) &
                   UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                        B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                     SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
               FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F,ADM_INP D  &
                     WHERE A.ORDER_CODE = B.ORDER_CODE &
                       AND A.ORDER_CODE = C.ORDER_CODE &
                       AND A.CASE_NO = D.CASE_NO &
		       AND A.CAT1_TYPE= 'PHA'  &
		       AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                       AND D.REGION_CODE=F.REGION_CODE &
                       AND A.BILL_DATE  &
                     BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                       AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                     GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                  B.SPECIFICATION, A.OWN_PRICE)&
                      ) DD  &
              GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, DD.SPECIFICATION, DD.OWN_PRICE &
                   ORDER BY SUM_AMT DESC
getSellOrder6.item = 0RDER_CODE
getSellOrder6.0RDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getSellOrder6.Debug=N


//抗生素查询门诊
getSellOrderOPD.Type=TSQL
getSellOrderOPD.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                              A.SPECIFICATION AS SPECIFICATION,&
                              A.OWN_PRICE AS OWN_PRICE, &
                             SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                         FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C &
                         WHERE C.ADM_TYPE IN ('O', 'E') &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND B.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.CASE_NO=C.CASE_NO &
                            AND C.REGION_CODE=F.REGION_CODE &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                         GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                             A.SPECIFICATION, A.OWN_PRICE
                      ORDER BY SUM_AMT DESC
getSellOrderOPD.item = 0RDER_CODE
getSellOrderOPD.0RDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getSellOrderOPD.Debug=N

//抗生素查询住院
getSellOrderODI.Type=TSQL
getSellOrderODI.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                        B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                     SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
               FROM IBS_ORDD A, ADM_INP D,SYS_FEE B, PHA_BASE C,SYS_REGION F  &
                     WHERE A.ORDER_CODE = B.ORDER_CODE &
                       AND A.ORDER_CODE = C.ORDER_CODE &
                       AND A.CASE_NO = D.CASE_NO &
		        AND A.CAT1_TYPE= 'PHA'  &
                       AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                       AND D.REGION_CODE=F.REGION_CODE &
                       AND A.BILL_DATE  &
                     BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                       AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                     GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                  B.SPECIFICATION, A.OWN_PRICE &
                   ORDER BY SUM_AMT DESC
getSellOrderODI.item = 0RDER_CODE;DS_FLG
getSellOrderODI.0RDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getSellOrderODI.DS_FLG=A.DS_FLG=<DS_FLG>
getSellOrderODI.Debug=N

//全品种查询

getAllOrder.Type=TSQL
getAllOrder.SQL=SELECT DD.REGION_CHN_ABN, DD.ORDER_CODE, DD.ORDER_DESC, &
                  DD.SPECIFICATION, DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                  SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT &
             FROM ((SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                   A.SPECIFICATION AS SPECIFICATION,&
                   A.OWN_PRICE AS OWN_PRICE, &
                   SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                  SUM (A.AR_AMT) AS SUM_AMT  &
               FROM OPD_ORDER A ,SYS_REGION F ,REG_PATADM B &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                    AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                    AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                    AND A.CAT1_TYPE= 'PHA' &
                    //fux modify 20150803
                    AND A.PHA_RETN_DATE IS  NULL  &   
                  GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN,&
                   A.SPECIFICATION, A.OWN_PRICE) &
          UNION ALL  &
              (SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                  B.SPECIFICATION AS SPECIFICATION, &
                 A.OWN_PRICE AS OWN_PRICE, &
                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
               FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
              WHERE A.CASE_NO = D.CASE_NO &
                    AND D.REGION_CODE=F.REGION_CODE &
                    AND A.ORDER_CODE = B.ORDER_CODE &
		    AND A.CAT1_TYPE= 'PHA'  &
                    AND A.BILL_DATE &
               BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                   AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                 B.SPECIFICATION, A.OWN_PRICE)) DD &
              GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN,  &
                    DD.SPECIFICATION, DD.OWN_PRICE &
                  ORDER BY SUM_AMT DESC
getAllOrder.item = ORDER_CODE
getAllOrder.ORDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getAllOrder.Debug=N

//全品种查询

getAllOrder3.Type=TSQL
getAllOrder3.SQL=SELECT DD.REGION_CHN_ABN, DD.ORDER_CODE, DD.ORDER_DESC, &
                  DD.SPECIFICATION, DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                  SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT &
             FROM ((SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                   A.SPECIFICATION AS SPECIFICATION,&
                   A.OWN_PRICE AS OWN_PRICE, &
                   SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                  SUM (A.AR_AMT) AS SUM_AMT  &
               FROM OPD_ORDER A ,SYS_REGION F ,REG_PATADM B &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                    AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                    AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                    //fux modify 20150803
                    AND A.PHA_RETN_DATE IS  NULL  &
                    AND A.CAT1_TYPE= 'PHA' &
                  GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN,&
                   A.SPECIFICATION, A.OWN_PRICE) &
          UNION ALL  &
              (SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                  B.SPECIFICATION AS SPECIFICATION, &
                 A.OWN_PRICE AS OWN_PRICE, &
                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
               FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
              WHERE A.CASE_NO = D.CASE_NO &
                    AND D.REGION_CODE=F.REGION_CODE &
                    AND A.ORDER_CODE = B.ORDER_CODE &
		    AND A.DS_FLG= <DS_FLG> &
		    AND A.CAT1_TYPE= 'PHA'  &
                    AND A.BILL_DATE &
               BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                   AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                 B.SPECIFICATION, A.OWN_PRICE)) DD &
              GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN,  &
                    DD.SPECIFICATION, DD.OWN_PRICE &
                  ORDER BY SUM_AMT DESC
getAllOrder3.item = ORDER_CODE
getAllOrder3.ORDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getAllOrder3.Debug=N

//全品种查询---门诊

getAllOrderOPD.Type=TSQL
getAllOrderOPD.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                      A.SPECIFICATION AS SPECIFICATION,&
                      A.OWN_PRICE AS OWN_PRICE, &
                      SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                     SUM (A.AR_AMT) AS SUM_AMT  &
                  FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B  &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                  AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                  AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                  //fux modify 20150803
                  AND A.PHA_RETN_DATE IS  NULL  &
                  AND A.CAT1_TYPE= 'PHA' &
                GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN,&
                  A.SPECIFICATION, A.OWN_PRICE &
                 ORDER BY SUM_AMT DESC
getAllOrderOPD.item = ORDER_CODE
getAllOrderOPD.ORDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getAllOrderOPD.Debug=N

//全品种查询--- 住院

getAllOrderODI.Type=TSQL
getAllOrderODI.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                  B.SPECIFICATION AS SPECIFICATION, &
                 A.OWN_PRICE AS OWN_PRICE, &
                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
               FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
              WHERE A.CASE_NO = D.CASE_NO &
                    AND D.REGION_CODE=F.REGION_CODE &
                    AND A.ORDER_CODE = B.ORDER_CODE &
		     AND A.CAT1_TYPE= 'PHA'  &
                    AND A.BILL_DATE &
               BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                   AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                 B.SPECIFICATION, A.OWN_PRICE &
                  ORDER BY SUM_AMT DESC
getAllOrderODI.item = ORDER_CODE;DS_FLG
getAllOrderODI.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
getAllOrderODI.DS_FLG=A.DS_FLG=<DS_FLG>
getAllOrderODI.Debug=N


//全品种查询2

getAllOrder1.Type=TSQL
getAllOrder1.SQL=SELECT DD.REGION_CHN_ABN , DD.ORDER_CODE, DD.ORDER_DESC, &
                      DD.SPECIFICATION, DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                      SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT &
                FROM ((SELECT F.REGION_CHN_ABN , &
	                A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                        A.SPECIFICATION AS SPECIFICATION,&
                        A.OWN_PRICE AS OWN_PRICE, &
                        SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                        SUM (A.AR_AMT) AS SUM_AMT  &
                    FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                      AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                      AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                      //fux modify 20150803
                      AND A.PHA_RETN_DATE IS  NULL  &
                      AND A.CAT1_TYPE= 'PHA' AND A.ORDER_CODE = <ORDER_CODE> &
                    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN ,&
                         A.SPECIFICATION, A.OWN_PRICE) &
                  UNION ALL  &
                    (SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                         B.SPECIFICATION AS SPECIFICATION, &
                         A.OWN_PRICE AS OWN_PRICE, &
                         SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
                     FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
                        WHERE A.ORDER_CODE = B.ORDER_CODE &
                         AND A.CASE_NO = D.CASE_NO &
                         AND D.REGION_CODE=F.REGION_CODE &
                         AND A.ORDER_CODE = <ORDER_CODE> &
			 AND A.CAT1_TYPE= 'PHA'  &
                         AND A.BILL_DATE &
                         BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                             AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                         GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN ,&
                                B.SPECIFICATION, A.OWN_PRICE)) DD &
                    GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, &
                              DD.SPECIFICATION, DD.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrder1.Debug=N


//全品种查询2

getAllOrder6.Type=TSQL
getAllOrder6.SQL=SELECT DD.REGION_CHN_ABN , DD.ORDER_CODE, DD.ORDER_DESC, &
                      DD.SPECIFICATION, DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                      SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT &
                FROM ((SELECT F.REGION_CHN_ABN , &
	                A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                        A.SPECIFICATION AS SPECIFICATION,&
                        A.OWN_PRICE AS OWN_PRICE, &
                        SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                        SUM (A.AR_AMT) AS SUM_AMT  &
                    FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                      AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                      AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                      //fux modify 20150803
                      AND A.PHA_RETN_DATE IS  NULL  &
                      AND A.CAT1_TYPE= 'PHA' AND A.ORDER_CODE = <ORDER_CODE> &
                    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN ,&
                         A.SPECIFICATION, A.OWN_PRICE) &
                  UNION ALL  &
                    (SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                         B.SPECIFICATION AS SPECIFICATION, &
                         A.OWN_PRICE AS OWN_PRICE, &
                         SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
                     FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
                        WHERE A.ORDER_CODE = B.ORDER_CODE &
                         AND A.CASE_NO = D.CASE_NO &
                         AND D.REGION_CODE=F.REGION_CODE &
                         AND A.ORDER_CODE = <ORDER_CODE> &
			 AND A.DS_FLG = <DS_FLG> &
			 AND A.CAT1_TYPE= 'PHA'  &
                         AND A.BILL_DATE &
                         BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                             AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                         GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN ,&
                                B.SPECIFICATION, A.OWN_PRICE)) DD &
                    GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, &
                              DD.SPECIFICATION, DD.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrder6.Debug=N


//全品种查询2--门诊

getAllOrderOPD1.Type=TSQL
getAllOrderOPD1.SQL=SELECT F.REGION_CHN_ABN , &
	                A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                        A.SPECIFICATION AS SPECIFICATION,&
                        A.OWN_PRICE AS OWN_PRICE, &
                        SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                        SUM (A.AR_AMT) AS SUM_AMT  &
                    FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B &
               WHERE B.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                      AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                      AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                      //fux modify 20150803
                      AND A.PHA_RETN_DATE IS  NULL  &
                      AND A.CAT1_TYPE= 'PHA' AND A.ORDER_CODE = <ORDER_CODE> &
                    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN ,&
                         A.SPECIFICATION, A.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrderOPD1.Debug=N

//全品种查询2---住院

getAllOrderODI1.Type=TSQL
getAllOrderODI1.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                         B.SPECIFICATION AS SPECIFICATION, &
                         A.OWN_PRICE AS OWN_PRICE, &
                         SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT &
                     FROM IBS_ORDD A, SYS_FEE B,SYS_REGION F,ADM_INP D &
                        WHERE A.ORDER_CODE = B.ORDER_CODE &
                         AND A.CASE_NO = D.CASE_NO &
                         AND D.REGION_CODE=F.REGION_CODE &
                         AND A.ORDER_CODE = <ORDER_CODE> &
			 AND A.CAT1_TYPE= 'PHA'  &
                         AND A.BILL_DATE &
                         BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                             AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                         GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN ,&
                                B.SPECIFICATION, A.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrderODI1.item = DS_FLG
getAllOrderODI1.DS_FLG=A.DS_FLG=<DS_FLG>
getAllOrderODI1.Debug=N

//抗生素查询1
getSellOrder1.Type=TSQL
getSellOrder1.SQL=SELECT DD.REGION_CHN_ABN,&
                       DD.ORDER_CODE, DD.ORDER_DESC, &
                       DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                       SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT  &
                  FROM ((SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                                 A.SPECIFICATION AS SPECIFICATION,&
                                 A.OWN_PRICE AS OWN_PRICE, &
                                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                            FROM OPD_ORDER A, PHA_BASE B,SYS_REGION F,REG_PATADM C &
                        WHERE C.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=C.CASE_NO AND F.REGION_CODE=C.REGION_CODE &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND A.ORDER_CODE = <ORDER_CODE> &
                            AND b.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                          GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                           A.SPECIFICATION, A.OWN_PRICE) &
                     UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                                  B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                                  SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
                              FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F ,ADM_INP D  &
                                 WHERE A.ORDER_CODE = B.ORDER_CODE &
                                  AND A.ORDER_CODE = C.ORDER_CODE  &
                                  AND A.ORDER_CODE = <ORDER_CODE> &
                                  AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                                  AND A.CASE_NO = D.CASE_NO &
                                  AND D.REGION_CODE=F.REGION_CODE &
				  AND A.CAT1_TYPE= 'PHA'  &
                                  AND A.BILL_DATE  &
                                  BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                                    AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                                    B.SPECIFICATION, A.OWN_PRICE)) DD  &
                          GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, &
                             DD.SPECIFICATION, DD.OWN_PRICE &
                      ORDER BY SUM_AMT DESC
getSellOrder1.Debug=N

//抗生素查询3 YANJING 20131128
getSellOrder3.Type=TSQL
getSellOrder3.SQL=SELECT DD.REGION_CHN_ABN,&
                       DD.ORDER_CODE, DD.ORDER_DESC, &
                       DD.SPECIFICATION,DD.OWN_PRICE,SUM (DD.SUM_QTY) AS SUM_QTY, &
                       SUM (DD.OWN_PRICE * DD.SUM_QTY) AS SUM_AMT  &
                  FROM ((SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                                 A.SPECIFICATION AS SPECIFICATION,&
                                 A.OWN_PRICE AS OWN_PRICE, &
                                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                            FROM OPD_ORDER A, PHA_BASE B,SYS_REGION F,REG_PATADM C &
                        WHERE C.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=C.CASE_NO AND F.REGION_CODE=C.REGION_CODE &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND A.ORDER_CODE = <ORDER_CODE> &
                            AND b.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                          GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                           A.SPECIFICATION, A.OWN_PRICE) &
                     UNION ALL(SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                                  B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                                  SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
                              FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F ,ADM_INP D  &
                                 WHERE A.ORDER_CODE = B.ORDER_CODE &
                                  AND A.ORDER_CODE = C.ORDER_CODE  &
                                  AND A.ORDER_CODE = <ORDER_CODE> &
				  AND A.DS_FLG = <DS_FLG> &
				  AND A.CAT1_TYPE= 'PHA'  &
                                  AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                                  AND A.CASE_NO = D.CASE_NO &
                                  AND D.REGION_CODE=F.REGION_CODE &
                                  AND A.BILL_DATE  &
                                  BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                                    AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                                    B.SPECIFICATION, A.OWN_PRICE)) DD  &
                          GROUP BY DD.ORDER_CODE, DD.ORDER_DESC,DD.REGION_CHN_ABN, &
                             DD.SPECIFICATION, DD.OWN_PRICE &
                      ORDER BY SUM_AMT DESC
getSellOrder3.Debug=N

//抗生素查询1---门诊
getSellOrderOPD1.Type=TSQL
getSellOrderOPD1.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                                 A.SPECIFICATION AS SPECIFICATION,&
                                 A.OWN_PRICE AS OWN_PRICE, &
                                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                            FROM OPD_ORDER A, PHA_BASE B,SYS_REGION F,REG_PATADM C &
                        WHERE C.ADM_TYPE IN ('O', 'E') AND A.CASE_NO=C.CASE_NO AND F.REGION_CODE=C.REGION_CODE &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND A.ORDER_CODE = <ORDER_CODE> &
                            AND b.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                          GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                           A.SPECIFICATION, A.OWN_PRICE  &
                      ORDER BY SUM_AMT DESC
getSellOrderOPD1.Debug=N



//抗生素查询1---住院
getSellOrderODI1.Type=TSQL
getSellOrderODI1.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, &
                                  B.SPECIFICATION AS SPECIFICATION, A.OWN_PRICE AS OWN_PRICE, &
                                  SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (TOT_AMT) AS SUM_AMT   &
                              FROM IBS_ORDD A, SYS_FEE B, PHA_BASE C,SYS_REGION F ,ADM_INP D  &
                                 WHERE A.ORDER_CODE = B.ORDER_CODE &
                                  AND A.ORDER_CODE = C.ORDER_CODE  &
                                  AND A.ORDER_CODE = <ORDER_CODE> &
                                  AND C.ANTIBIOTIC_CODE IS NOT NULL  &
                                  AND A.CASE_NO = D.CASE_NO &
                                  AND D.REGION_CODE=F.REGION_CODE &
				  AND A.CAT1_TYPE= 'PHA'  &
                                  AND A.BILL_DATE  &
                                  BETWEEN TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                                    AND TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                                 GROUP BY A.ORDER_CODE, B.ORDER_DESC,F.REGION_CHN_ABN, &
                                    B.SPECIFICATION, A.OWN_PRICE &
                      ORDER BY SUM_AMT DESC
getSellOrderODI1.item = DS_FLG
getSellOrderODI1.DS_FLG=A.DS_FLG=<DS_FLG>
getSellOrderODI1.Debug=N

//抗生素查询门诊
getSellOrderO.Type=TSQL
getSellOrderO.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                              A.SPECIFICATION AS SPECIFICATION,&
                              A.OWN_PRICE AS OWN_PRICE, &
                             SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                         FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C &
                         WHERE C.ADM_TYPE ='O' &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND B.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.CASE_NO=C.CASE_NO &
                            AND C.REGION_CODE=F.REGION_CODE &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                         GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                             A.SPECIFICATION, A.OWN_PRICE
                      ORDER BY SUM_AMT DESC
getSellOrderO.item = 0RDER_CODE
getSellOrderO.0RDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getSellOrderO.Debug=N

//抗生素查询急诊
getSellOrderE.Type=TSQL
getSellOrderE.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                              A.SPECIFICATION AS SPECIFICATION,&
                              A.OWN_PRICE AS OWN_PRICE, &
                             SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                         FROM OPD_ORDER A, PHA_BASE B ,SYS_REGION F,REG_PATADM C &
                         WHERE C.ADM_TYPE ='E' &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND B.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.CASE_NO=C.CASE_NO &
                            AND C.REGION_CODE=F.REGION_CODE &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                         GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                             A.SPECIFICATION, A.OWN_PRICE
                      ORDER BY SUM_AMT DESC
getSellOrderE.item = 0RDER_CODE
getSellOrderE.0RDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getSellOrderE.Debug=N

//全品种查询---门诊

getAllOrderO.Type=TSQL
getAllOrderO.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                      A.SPECIFICATION AS SPECIFICATION,&
                      A.OWN_PRICE AS OWN_PRICE, &
                      SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                     SUM (A.AR_AMT) AS SUM_AMT  &
                  FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B  &
               WHERE B.ADM_TYPE ='O' AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                  AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                  AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                  //fux modify 20150803
                  AND A.PHA_RETN_DATE IS  NULL  &
                  AND A.CAT1_TYPE= 'PHA' &
                GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN,&
                  A.SPECIFICATION, A.OWN_PRICE &
                 ORDER BY SUM_AMT DESC
getAllOrderO.item = ORDER_CODE
getAllOrderO.ORDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getAllOrderO.Debug=N

//全品种查询---急诊

getAllOrderE.Type=TSQL
getAllOrderE.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                      A.SPECIFICATION AS SPECIFICATION,&
                      A.OWN_PRICE AS OWN_PRICE, &
                      SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                     SUM (A.AR_AMT) AS SUM_AMT  &
                  FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B  &
               WHERE B.ADM_TYPE ='E' AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                  AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                  AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                  //fux modify 20150803
                  AND A.PHA_RETN_DATE IS  NULL  &
                  AND A.CAT1_TYPE= 'PHA' &
                GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN,&
                  A.SPECIFICATION, A.OWN_PRICE &
                 ORDER BY SUM_AMT DESC
getAllOrderE.item = ORDER_CODE
getAllOrderE.ORDER_CODE=DD.ORDER_CODE=<ORDER_CODE>
getAllOrderE.Debug=N


//全品种查询2--门诊

getAllOrderO1.Type=TSQL
getAllOrderO1.SQL=SELECT F.REGION_CHN_ABN , &
	                A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                        A.SPECIFICATION AS SPECIFICATION,&
                        A.OWN_PRICE AS OWN_PRICE, &
                        SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                        SUM (A.AR_AMT) AS SUM_AMT  &
                    FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B &
               WHERE B.ADM_TYPE ='O' AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                      AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                      AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                      //fux modify 20150803
                      AND A.PHA_RETN_DATE IS  NULL  &
                      AND A.CAT1_TYPE= 'PHA' AND A.ORDER_CODE = <ORDER_CODE> &
                    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN ,&
                         A.SPECIFICATION, A.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrderO1.Debug=N
//全品种查询2--急诊
getAllOrderE1.Type=TSQL
getAllOrderE1.SQL=SELECT F.REGION_CHN_ABN , &
	                A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                        A.SPECIFICATION AS SPECIFICATION,&
                        A.OWN_PRICE AS OWN_PRICE, &
                        SUM (A.DOSAGE_QTY) AS SUM_QTY, &
                        SUM (A.AR_AMT) AS SUM_AMT  &
                    FROM OPD_ORDER A ,SYS_REGION F,REG_PATADM B &
               WHERE B.ADM_TYPE ='E' AND A.CASE_NO=B.CASE_NO AND F.REGION_CODE=B.REGION_CODE &
                      AND A.BILL_DATE>= TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                      AND A.BILL_DATE<= TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                      //fux modify 20150803
                      AND A.PHA_RETN_DATE IS  NULL  &
                      AND A.CAT1_TYPE= 'PHA' AND A.ORDER_CODE = <ORDER_CODE> &
                    GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN ,&
                         A.SPECIFICATION, A.OWN_PRICE &
                    ORDER BY SUM_AMT DESC
getAllOrderE1.Debug=N

//抗生素查询1---门诊
getSellOrderO1.Type=TSQL
getSellOrderO1.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                                 A.SPECIFICATION AS SPECIFICATION,&
                                 A.OWN_PRICE AS OWN_PRICE, &
                                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                            FROM OPD_ORDER A, PHA_BASE B,SYS_REGION F,REG_PATADM C &
                        WHERE C.ADM_TYPE ='O' AND A.CASE_NO=C.CASE_NO AND F.REGION_CODE=C.REGION_CODE &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND A.ORDER_CODE = <ORDER_CODE> &
                            AND b.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                          GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                           A.SPECIFICATION, A.OWN_PRICE  &
                      ORDER BY SUM_AMT DESC
getSellOrderO1.Debug=N

//抗生素查询1---急诊
getSellOrderE1.Type=TSQL
getSellOrderE1.SQL=SELECT F.REGION_CHN_ABN, A.ORDER_CODE, A.ORDER_DESC AS ORDER_DESC, &
                                 A.SPECIFICATION AS SPECIFICATION,&
                                 A.OWN_PRICE AS OWN_PRICE, &
                                 SUM (A.DOSAGE_QTY) AS SUM_QTY, SUM (A.AR_AMT) AS SUM_AMT &
                            FROM OPD_ORDER A, PHA_BASE B,SYS_REGION F,REG_PATADM C &
                        WHERE C.ADM_TYPE ='E' AND A.CASE_NO=C.CASE_NO AND F.REGION_CODE=C.REGION_CODE &
                            AND A.ORDER_CODE = B.ORDER_CODE &
                            AND A.ORDER_CODE = <ORDER_CODE> &
                            AND b.ANTIBIOTIC_CODE IS NOT NULL &
                            AND A.BILL_DATE >=TO_DATE (<START_DATE>,'YYYY/MM/DD HH24:MI:SS') &
                            AND A.BILL_DATE<=TO_DATE (<END_DATE>, 'YYYY/MM/DD HH24:MI:SS') &
                            //fux modify 20150803
                            AND A.PHA_RETN_DATE IS  NULL  &
                            AND A.CAT1_TYPE = 'PHA' &
                          GROUP BY A.ORDER_CODE, A.ORDER_DESC,F.REGION_CHN_ABN, &
                           A.SPECIFICATION, A.OWN_PRICE  &
                      ORDER BY SUM_AMT DESC
getSellOrderE1.Debug=N