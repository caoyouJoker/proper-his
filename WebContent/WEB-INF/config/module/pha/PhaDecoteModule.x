# 
#  Title:��ҽ��ҩ
# 
#  Description:��ҽ��ҩ
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangy 2010.05.21
#  version 1.0
#
Module.item=queryOE;queryI;queryDetailOE;queryDetailI;updateSendDctUserOE;updateSendDctUserI;updateDecoctUserOE;updateDecoctUserI;updateSendOrgUserOE;updateSendOrgUserI

//�ż�����ҽ��ҩ
queryOE.Type=TSQL
queryOE.SQL=SELECT DISTINCT A.RX_NO, 'N' AS SELECT_FLG, A.URGENT_FLG, A.FINAL_TYPE, &
                A.ADM_TYPE, A.PRINT_NO, B.ORG_CHN_DESC, A.MR_NO, C.PAT_NAME, &
                E.CLINICROOM_DESC, A.DCT_TAKE_QTY, F.FREQ_CHN_DESC, &
                A.TAKE_DAYS, G.CHN_DESC, &
                A.TAKE_DAYS * F.FREQ_TIMES AS PACKAGE_AMT, A.DECOCT_REMARK, &
                A.CASE_NO, A.FREQ_CODE, H.DEPT_ABS_DESC &
           FROM OPD_ORDER A, &
                IND_ORG B, &
                SYS_PATINFO C, &
                REG_PATADM D, &
                REG_CLINICROOM E, &
                SYS_PHAFREQ F, &
                SYS_DICTIONARY G, &
                SYS_DEPT H &
          WHERE A.EXEC_DEPT_CODE = B.ORG_CODE &
            AND A.MR_NO = C.MR_NO &
            AND A.CASE_NO = D.CASE_NO &
            AND D.CLINICROOM_NO = E.CLINICROOM_NO &
            AND A.FREQ_CODE = F.FREQ_CODE &
            AND A.DCTAGENT_CODE = G.ID(+) &
            AND G.GROUP_ID(+) = 'PHA_DCTAGENT' &
            AND A.DEPT_CODE = H.DEPT_CODE &
            AND TO_CHAR (A.ORDER_DATE, 'YYYYMMDD') = <PHARM_DATE> &
            AND A.PHA_TYPE = 'G' &
            AND A.DCTAGENT_FLG = 'Y' &
            ORDER BY A.CASE_NO, A.RX_NO
queryOE.ITEM=REGION_CODE;ORG_CODE;DECOCT_CODE;TYPE_A_1;TYPE_A_2;TYPE_B;TYPE_C;TYPE_D;ADM_TYPE;MR_NO
queryOE.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryOE.ORG_CODE=B.ORG_CODE=<ORG_CODE>
queryOE.DECOCT_CODE=B.DECOCT_CODE=<DECOCT_CODE>
queryOE.TYPE_A_1=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'F'
queryOE.TYPE_A_2=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = ''
queryOE.TYPE_B=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'S'
queryOE.TYPE_C=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'E'
queryOE.TYPE_D=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'O'
queryOE.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
queryOE.MR_NO=A.MR_NO=<MR_NO>
queryOE.Debug=N


//�ż�����ҽ��ҩ��ϸ
queryDetailOE.Type=TSQL
queryDetailOE.SQL=SELECT A.ORDER_CODE, B.ORDER_DESC, A.DCT_TAKE_QTY, C.UNIT_CHN_DESC, &
       			 D.CHN_DESC, A.DR_NOTE, E.CTRLDRUGCLASS_CHN_DESC, A.OWN_AMT, &
       			 A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE, &
       			 A.PHA_DOSAGE_DATE, A.SEND_DCT_USER, A.SEND_DCT_DATE, A.DECOCT_USER, &
       			 A.DECOCT_DATE, A.SEND_ORG_USER, A.SEND_ORG_DATE, A.PHA_DISPENSE_CODE, &
       			 A.PHA_DISPENSE_DATE, A.TAKE_DAYS,A.FREQ_CODE, A.ROUTE_CODE, A.DCTEXCEP_CODE, &
       			 A.DOSAGE_QTY, A.MEDI_QTY, G.DEPT_CHN_DESC, F.ROUTE_CHN_DESC, A.DCTAGENT_CODE &
  		    FROM OPD_ORDER A, &
       			 PHA_BASE B, &
       			 SYS_UNIT C, &
       			 SYS_DICTIONARY D, &
       			 SYS_CTRLDRUGCLASS E, &
       			 SYS_PHAROUTE F, &
       			 SYS_DEPT G &
 		   WHERE A.ORDER_CODE = B.ORDER_CODE &
   		     AND B.DOSAGE_UNIT = C.UNIT_CODE &
   	             AND A.DCTEXCEP_CODE = D.ID(+) &
   		     AND B.CTRLDRUGCLASS_CODE = E.CTRLDRUGCLASS_CODE(+) &
   		     AND A.ROUTE_CODE = F.ROUTE_CODE &
   		     AND A.DEPT_CODE = G.DEPT_CODE &
   		     AND D.GROUP_ID(+) = 'PHA_DCTEXCEP' &
	             AND A.CASE_NO = <CASE_NO> &
		     AND A.RX_NO = <RX_NO>
//==========pangben modify 20110516 start
queryDetailOE.ITEM=REGION_CODE
queryDetailOE.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==========pangben modify 20110516 stop
queryDetailOE.Debug=N


//���´��ͼ�ҩ��״̬
updateSendDctUserOE.Type=TSQL
updateSendDctUserOE.SQL=UPDATE OPD_ORDER SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, SEND_DCT_USER=<SEND_DCT_USER>,  &
			       SEND_DCT_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND RX_NO=<RX_NO> 
updateSendDctUserOE.Debug=N


//���´����ռ�ҩ״̬
updateDecoctUserOE.Type=TSQL
updateDecoctUserOE.SQL=UPDATE OPD_ORDER SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, DECOCT_USER=<DECOCT_USER>,  &
			       DECOCT_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND RX_NO=<RX_NO>
updateDecoctUserOE.Debug=N


//���´��ͷ�ҩҩ��״̬
updateSendOrgUserOE.Type=TSQL
updateSendOrgUserOE.SQL=UPDATE OPD_ORDER SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, SEND_ORG_USER=<SEND_ORG_USER>,  &
			       SEND_ORG_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND RX_NO=<RX_NO>
updateSendOrgUserOE.Debug=N


//סԺ��ҽ��ҩ
queryI.Type=TSQL
queryI.SQL=SELECT DISTINCT A.ORDER_NO AS RX_NO, 'N' AS SELECT_FLG, A.URGENT_FLG, &
                A.FINAL_TYPE, 'I' AS ADM_TYPE, '' AS PRINT_NO, B.ORG_CHN_DESC, &
                A.MR_NO, C.PAT_NAME, &
                D.STATION_DESC || E.BED_NO_DESC AS CLINICROOM_DESC, &
                A.DCT_TAKE_QTY, F.FREQ_CHN_DESC, A.TAKE_DAYS, G.CHN_DESC, &
                A.TAKE_DAYS * F.FREQ_TIMES AS PACKAGE_AMT, A.DECOCT_REMARK, &
                A.CASE_NO, A.FREQ_CODE, H.DEPT_ABS_DESC  &
           FROM ODI_DSPNM A, &
                IND_ORG B, &
                SYS_PATINFO C, &
                SYS_STATION D, &
                SYS_BED E, &
                SYS_PHAFREQ F, &
                SYS_DICTIONARY G, &
                SYS_DEPT H &
          WHERE A.EXEC_DEPT_CODE = B.ORG_CODE &
            AND A.MR_NO = C.MR_NO &
            AND A.STATION_CODE = D.STATION_CODE &
            AND A.BED_NO = E.BED_NO &
            AND A.FREQ_CODE = F.FREQ_CODE &
            AND A.DCTAGENT_CODE = G.ID(+) &
            AND G.GROUP_ID(+) = 'PHA_DCTAGENT'  &
            AND A.DEPT_CODE = H.DEPT_CODE  &
            AND TO_CHAR (A.ORDER_DATE, 'YYYYMMDD') = <PHARM_DATE>  &
            AND A.PHA_TYPE = 'G'  &
            AND A.DCTAGENT_FLG = 'Y' &
            ORDER BY A.CASE_NO, A.ORDER_NO
queryI.ITEM=REGION_CODE;ORG_CODE;DECOCT_CODE;TYPE_A_1;TYPE_A_2;TYPE_B;TYPE_C;TYPE_D;MR_NO
queryI.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryI.ORG_CODE=B.ORG_CODE=<ORG_CODE>
queryI.DECOCT_CODE=B.DECOCT_CODE=<DECOCT_CODE>
queryI.TYPE_A_1=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'F'
queryI.TYPE_A_2=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = ''
queryI.TYPE_B=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'S'
queryI.TYPE_C=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'E'
queryI.TYPE_D=A.PHA_DOSAGE_DATE IS NOT NULL AND A.PHA_RETN_DATE IS NULL AND A.FINAL_TYPE = 'O'
queryI.MR_NO=A.MR_NO=<MR_NO>
queryI.Debug=N


//סԺ��ҽ��ҩ��ϸ
queryDetailI.Type=TSQL
queryDetailI.SQL=SELECT A.ORDER_CODE, B.ORDER_DESC, A.DCT_TAKE_QTY, C.UNIT_CHN_DESC,&
       			D.CHN_DESC, A.DR_NOTE, E.CTRLDRUGCLASS_CHN_DESC, A.OWN_AMT,&
       			A.PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.PHA_DOSAGE_CODE,&
       			A.PHA_DOSAGE_DATE, A.SEND_DCT_USER, A.SEND_DCT_DATE, A.DECOCT_USER, &
       			A.DECOCT_DATE, A.SEND_ORG_USER, A.SEND_ORG_DATE, A.PHA_DISPENSE_CODE, &
       			A.PHA_DISPENSE_DATE, A.TAKE_DAYS, A.FREQ_CODE, A.ROUTE_CODE, &
       			A.DCTEXCEP_CODE, A.DOSAGE_QTY, A.MEDI_QTY, G.DEPT_CHN_DESC, F.ROUTE_CHN_DESC, A.DCTAGENT_CODE &
  		   FROM ODI_DSPNM A, &
       			PHA_BASE B, &
       			SYS_UNIT C, &
       			SYS_DICTIONARY D, &
       			SYS_CTRLDRUGCLASS E, &
       			SYS_PHAROUTE F, &
       			SYS_DEPT G &
 		  WHERE A.ORDER_CODE = B.ORDER_CODE &
   			AND A.DOSAGE_UNIT = C.UNIT_CODE &
   			AND A.DCTAGENT_CODE = D.ID &
   			AND B.CTRLDRUGCLASS_CODE = E.CTRLDRUGCLASS_CODE(+) &
   			AND A.ROUTE_CODE = F.ROUTE_CODE &
   			AND A.DEPT_CODE = G.DEPT_CODE &
   		        AND D.GROUP_ID = 'PHA_DCTEXCEP' &
	                AND A.CASE_NO = <CASE_NO> &
		        AND A.ORDER_NO = <RX_NO>
//==========pangben modify 20110516 start
queryDetailI.ITEM=REGION_CODE
queryDetailI.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//==========pangben modify 20110516 stop
queryDetailI.Debug=N


//���´��ͼ�ҩ��״̬
updateSendDctUserI.Type=TSQL
updateSendDctUserI.SQL=UPDATE ODI_DSPNM SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, SEND_DCT_USER=<SEND_DCT_USER>,  &
			       SEND_DCT_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND ORDER_NO=<RX_NO> 
updateSendDctUserI.Debug=N


//���´����ռ�ҩ״̬
updateDecoctUserI.Type=TSQL
updateDecoctUserI.SQL=UPDATE ODI_DSPNM SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, DECOCT_USER=<DECOCT_USER>,  &
			       DECOCT_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND ORDER_NO=<RX_NO>
updateDecoctUserI.Debug=N


//���´��ͷ�ҩҩ��״̬
updateSendOrgUserI.Type=TSQL
updateSendOrgUserI.SQL=UPDATE ODI_DSPNM SET &
			       FINAL_TYPE=<FINAL_TYPE>, DECOCT_REMARK=<DECOCT_REMARK>, SEND_ORG_USER=<SEND_ORG_USER>,  &
			       SEND_ORG_DATE=<DATE>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
			 WHERE CASE_NO = <CASE_NO> AND ORDER_NO=<RX_NO>
updateSendOrgUserI.Debug=N