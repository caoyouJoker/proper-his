Module.item=selectStation;selectPatient;deleteManaged;deleteCLPBill;selectCLPPackData;insertManagerdData;insertCLPManagedWithCLPPack;selectCLPManaedFromCLPPack;&
            selectCLPManaedFromIBSOrdd;checkIBSOrddExistInCLPManaged;insertCLPManagedWithIBSOrdd;getUpdateDataInCLPManagedWithPatienInfo;&
            updateCLPManagedWithIBSOrdd;deleteCLPManagedWithPatientInfo;updateCLPManagedWithPatientInfo;selectCLPManagedWithCondition;selectOwnPrice;&
            saveCLPBill;updateCLPBill;checkCLPBillExist;selectPatientOrderCodeList;selectCLPManaedFromCLPPackWithVersion;selectCLPManaedFromIBSOrddTwo;&
            updateCLPManagedWithIBSOrddOrderSet

//pangb �޸��Ż�
selectStation.Type=TSQL
selectStation.SQL= SELECT A.STATION_CODE,A.STATION_DESC, COUNT(A.STATION_CODE) AS TOTALPATIENT &
			  FROM SYS_STATION A,ADM_INP B WHERE A.STATION_CODE=B.STATION_CODE AND B.DS_DATE IS NULL AND B.CANCEL_FLG = 'N' &
			  AND A.REGION_CODE=<REGION_CODE> GROUP BY A.STATION_CODE,A.STATION_DESC
selectStation.Debug=N


selectPatient.Type=TSQL
selectPatient.SQL=SELECT 'N' AS STATUS,A.DEPT_CODE,A.MR_NO, A.CASE_NO,A.IPD_NO,A.CLNCPATH_CODE,A.STATION_CODE,TO_CHAR(A.IN_DATE,'YYYYMMDDHH24MISS') &
									,B.PAT_NAME,A.BED_NO ,A.REGION_CODE,C.VERSION &
									FROM ADM_INP A,SYS_PATINFO B,CLP_MANAGEM C  &
									WHERE A.CASE_NO=C.CASE_NO(+) AND A.CLNCPATH_CODE=C.CLNCPATH_CODE(+) &
									AND A.MR_NO=B.MR_NO(+) AND A.REGION_CODE=<REGION_CODE> &
									#AND A.DS_DATE IS NULL &
									AND A.CANCEL_FLG ='N' &
									AND A.CLNCPATH_CODE IS NOT NULL 
selectPatient.item=CASE_NO;BED_NO;MR_NO;IPD_NO;STATION_CODE;DEPT_CODE
selectPatient.CASE_NO=A.CASE_NO LIKE <CASE_NO>
selectPatient.BED_NO=A.BED_NO LIKE <BED_NO>
selectPatient.MR_NO=A.MR_NO LIKE <MR_NO>
selectPatient.IPD_NO=A.IPD_NO LIKE <IPD_NO>
selectPatient.STATION_CODE=A.STATION_CODE=<STATION_CODE>
selectPatient.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectPatient.Debug=N

//pangb �޸��Ż�
deleteManaged.Type=TSQL
deleteManaged.SQL=DELETE FROM CLP_MANAGED WHERE  CASE_NO=<CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE> 
deleteManaged.item=STANDARD_FLG
deleteManaged.STANDARD_FLG=STANDARD_FLG=<STANDARD_FLG>
deleteManaged.Debug=N

//pangb �޸��Ż�
deleteCLPBill.Type=TSQL
deleteCLPBill.SQL=DELETE FROM CLP_BILL WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE>
deleteCLPBill.item=SCHD_TYPE
deleteCLPBill.SCHD_TYPE=SCHD_TYPE=<SCHD_TYPE>
deleteCLPBill.Debug=N


selectCLPPackData.Type=TSQL
selectCLPPackData.SQL=SELECT A.SCHD_CODE, A.CHKTYPE_CODE, A.ORDER_CODE, A.EXEC_FLG,A.DOSE_UNIT, A.STANDARD, A.ORDER_FLG,
		      A.FREQ_CODE,A.DOSE,A.ORDER_TYPE,B.CAT1_TYPE,A.CHKUSER_CODE &
	       	      FROM CLP_PACK A ,SYS_FEE B &
	              WHERE  A.ORDER_CODE = B.ORDER_CODE(+) &
	              AND A.REGION_CODE = <REGION_CODE> AND A.CLNCPATH_CODE =<CLNCPATH_CODE>
selectCLPPackData.Debug=N
  										
  										
insertManagerdData.Type=TSQL
insertManagerdData.SQL=INSERT INTO CLP_MANAGED( &
		      CASE_NO,CLNCPATH_CODE,SCHD_CODE,ORDER_NO,ORDER_SEQ,REGION_CODE,ORDER_CODE,CHKTYPE_CODE,STANDING_DTTM,&
		      CHKUSER_CODE,EXEC_FLG,TOT,DISPENSE_UNIT,STANDARD,ORDER_FLG,SCHD_DESC,CHANGE_FLG,STANDARD_FLG,MAINORD_CODE,&
		      MAINTOT,MAINDISPENSE_UNIT,CFM_DTTM,CFM_USER,PROGRESS_CODE,MEDICAL_MONCAT,MEDICAL_VARIANCE,MEDICAL_NOTE,&
		      MANAGE_MONCAT,MANAGE_VARIANCE,MANAGE_NOTE,MANAGE_DTTM,MANAGE_USER,R_DEPT_CODE,R_USER,TOT_AMT,MAIN_AMT,&
		      MAINCFM_USER,ORDTYPE_CODE,DEPT_CODE,EXE_DEPT_CODE,OPT_USER,OPT_DATE,OPT_TERM&
		      )VALUES(
		     <CASE_NO>,<CLNCPATH_CODE>,<SCHD_CODE>,<ORDER_NO>,<ORDER_SEQ>,<REGION_CODE>,&
		     <ORDER_CODE>,<CHKTYPE_CODE>,<STANDING_DTTM>,<CHKUSER_CODE>,<EXEC_FLG>,<TOT>,&
		     <DISPENSE_UNIT>,<STANDARD>,<ORDER_FLG>,<SCHD_DESC>,<CHANGE_FLG>,<STANDARD_FLG>,&
		     <MAINORD_CODE>,<MAINTOT>,<MAINDISPENSE_UNIT>,<CFM_DTTM>,<CFM_USER>,<PROGRESS_CODE>,&
		     <MEDICAL_MONCAT>,<MEDICAL_VARIANCE>,<MEDICAL_NOTE>,<MANAGE_MONCAT>,<MANAGE_VARIANCE>,&
		     <MANAGE_NOTE>,<MANAGE_DTTM>,<MANAGE_USER>,<R_DEPT_CODE>,<R_USER>,<TOT_AMT>,<MAIN_AMT>,&
		     <MAINCFM_USER>,<ORDTYPE_CODE>,<DEPT_CODE>,<EXE_DEPT_CODE>,<OPT_USER>,<OPT_DATE>,&
		      <OPT_TERM>)
insertManagerdData.Debug=N

#20110705¬���޸�
//=============pangben 2012-05-22 �޸�	�Ż� CHANGE_FLG д�� ='Y'
insertCLPManagedWithCLPPack.Type=TSQL
insertCLPManagedWithCLPPack.SQL=INSERT INTO CLP_MANAGED(&
			        CASE_NO,CLNCPATH_CODE,SCHD_CODE,ORDER_SEQ,REGION_CODE,ORDER_CODE,CHKTYPE_CODE,STANDING_DTTM,&
			        CHKUSER_CODE,EXEC_FLG,TOT,DISPENSE_UNIT,STANDARD,ORDER_FLG,SCHD_DESC,CHANGE_FLG,STANDARD_FLG,&
			        DEPT_CODE,OPT_USER,OPT_DATE,OPT_TERM,ORDER_NO,ORDTYPE_CODE,TOT_AMT,ORDER_SEQ_NO,START_DAY &
			        )VALUES(<CASE_NO>,<CLNCPATH_CODE>,<SCHD_CODE>,<ORDER_SEQ>,<REGION_CODE>,&
		                <ORDER_CODE>,<CHKTYPE_CODE>,TO_DATE(<STANDING_DTTM>,'YYYYMMDD'),<CHKUSER_CODE>,<EXEC_FLG>,<TOT>,&
		                <DOSE_UNIT>,<STANDARD>,<ORDER_FLG>,<SCHD_DESC>,'Y', 'Y',<DEPT_CODE>,&
	          	        <OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>,<ORDER_NO>,<ORDTYPE_CODE>,&
			        <TOT_AMT>,<ORDER_SEQ_NO>,<START_DAY> )	 
insertCLPManagedWithCLPPack.Debug=N
#20110705¬���޸Ĳ�ѯORDER_SEQ_NO
selectCLPManaedFromCLPPack.Type=TSQL
selectCLPManaedFromCLPPack.SQL=SELECT CLNCPATH_CODE,SCHD_CODE,ORDER_CODE,CHKTYPE_CODE,CHKUSER_CODE,&
			       EXEC_FLG,STANDARD,ORDER_FLG,DOSE_UNIT,DOSE,FREQ_CODE,DOSE_DAYS,ORDER_SEQ_NO &
		               FROM CLP_PACK WHERE CLNCPATH_CODE = <CLNCPATH_CODE> AND REGION_CODE=<REGION_CODE>
selectCLPManaedFromCLPPack.Debug=N

#������ݰ汾���ұ�׼���ݹ���
//=============pangben 2012-05-22 �޸�				  
selectCLPManaedFromCLPPackWithVersion.Type=TSQL
selectCLPManaedFromCLPPackWithVersion.SQL=SELECT A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_CODE,A.CHKTYPE_CODE,A.CHKUSER_CODE,D.ORDERSET_FLG,F.MEDI_QTY,&
				          A.EXEC_FLG,A.STANDARD,A.ORDER_FLG,A.DOSE_UNIT,A.DOSE,A.FREQ_CODE,A.DOSE_DAYS,A.ORDER_SEQ_NO,&
				          B.DURATION_CHN_DESC AS SCHD_DESC,C.ORDTYPE_CODE,A.OWN_PRICE,E.FREQ_TIMES,A.START_DAY,F.DOSAGE_UNIT,D.UNIT_CODE &
					  FROM CLP_PACK A ,CLP_DURATION B,CLP_ORDERTYPE C,SYS_FEE D,SYS_PHAFREQ E,PHA_BASE F WHERE A.SCHD_CODE=B.DURATION_CODE(+) &
					  AND A.ORDER_CODE=C.ORDER_CODE  AND A.ORDER_CODE=F.ORDER_CODE(+) AND A.ORDER_CODE=D.ORDER_CODE AND A.FREQ_CODE=E.FREQ_CODE(+) &
					  AND A.CLNCPATH_CODE = <CLNCPATH_CODE> AND A.REGION_CODE=<REGION_CODE> &
					  AND A.VERSION=<VERSION> 
					  #UNION ALL &
					  #SELECT CLNCPATH_CODE,SCHD_CODE,ORDER_CODE,CHKTYPE_CODE,CHKUSER_CODE,&
				          #EXEC_FLG,STANDARD,ORDER_FLG,DOSE_UNIT,DOSE,FREQ_CODE,DOSE_DAYS,ORDER_SEQ_NO &
					  #FROM CLP_PACK_HISTORY WHERE CLNCPATH_CODE = <CLNCPATH_CODE> AND REGION_CODE=<REGION_CODE> &
					  #AND VERSION=<VERSION> 
selectCLPManaedFromCLPPackWithVersion.Debug=N

#DOSAGE_QTY �ĳ�MEDI_UNIT����ҩ������
selectCLPManaedFromIBSOrdd.Type=TSQL 
selectCLPManaedFromIBSOrdd.SQL=SELECT SUM(TOT_AMT) AS MAIN_AMT, A.ORDER_CODE, A.DOSAGE_UNIT, A.ORDER_CAT1_CODE, A.CHK_USER, A.EXEC_USER,'' AS PROGRESS_CODE,'' &
                               AS MEDICAL_NOTE,SUM(QTY) AS QTY, A.CLNCPATH_CODE,A.SCHD_CODE,A.DEPT_CODE,A.EXE_DEPT_CODE,A.ORDTYPE_CODE & 
			       FROM (& 
			       SELECT TOT_AMT, D.ORDER_CODE AS ORDER_CODE,D.DOSAGE_UNIT,D.DOSAGE_QTY AS QTY,S.ORDER_CAT1_CODE,&
			       CASE WHEN M.DATA_TYPE NOT IN ('0','2','B') THEN '002' ELSE '001' END AS CHK_USER, '' AS EXEC_USER,&
			       D.CLNCPATH_CODE ,D.SCHD_CODE ,M.DEPT_CODE,D.EXE_DEPT_CODE,P.ORDTYPE_CODE &
			       FROM IBS_ORDM M, IBS_ORDD D, SYS_FEE S,CLP_ORDERTYPE P &
			       WHERE  D.CASE_NO = M.CASE_NO &
			       AND D.CASE_NO_SEQ = M.CASE_NO_SEQ & 
			       AND S.ORDER_CODE = D.ORDER_CODE &
			       AND D.ORDER_CODE = P.ORDER_CODE &
			       AND M.CASE_NO = <CASE_NO> &
			       AND (D.ORDERSET_CODE IS NULL OR D.ORDER_CODE = D.ORDERSET_CODE) &
			       AND D.CLNCPATH_CODE = <CLNCPATH_CODE>  &   
			       AND D.SCHD_CODE IS NOT NULL &
			       ) A &
			       GROUP BY A.ORDER_CODE, A.DOSAGE_UNIT, A.ORDER_CAT1_CODE, A.CHK_USER, A.EXEC_USER,A.CLNCPATH_CODE,A.SCHD_CODE,A.DEPT_CODE,A.EXE_DEPT_CODE,A.ORDTYPE_CODE & 
			       ORDER BY A.ORDER_CODE, A.ORDER_CAT1_CODE, A.CHK_USER, A.EXEC_USER, A.CLNCPATH_CODE,A.SCHD_CODE,A.DEPT_CODE,A.EXE_DEPT_CODE
selectCLPManaedFromIBSOrdd.Debug=N

//�ڶ��λ��IBS_ORDD����û��ִ�е��ٴ�·��ҽ��
selectCLPManaedFromIBSOrddTwo.Type=TSQL
selectCLPManaedFromIBSOrddTwo.SQL=SELECT CASE_NO FROM CLP_MANAGED WHERE CASE_NO = <CASE_NO>  AND CLNCPATH_CODE = <CLNCPATH_CODE> AND MAINORD_CODE IS NOT NULL 
AND ORDER_CODE<>MAINORD_CODE
selectCLPManaedFromIBSOrddTwo.Debug=N

#ɾ��������AND A.DISPENSE_UNIT=<DOSAGE_UNIT> 
//=========pangben 2012-5-30 �Ż�SQL
checkIBSOrddExistInCLPManaged.Type=TSQL
checkIBSOrddExistInCLPManaged.SQL= SELECT COUNT(*) AS TOTALCOUNT FROM CLP_MANAGED A,CLP_ORDERTYPE B  WHERE A.ORDTYPE_CODE =B.ORDTYPE_CODE &
				   AND A.CASE_NO=<CASE_NO> &
				   AND B.ORDER_CODE =<ORDER_CODE> &
				   AND A.CLNCPATH_CODE = <CLNCPATH_CODE> AND A.SCHD_CODE = <SCHD_CODE> 
checkIBSOrddExistInCLPManaged.Debug=N

#���� clpManaged from IBSOrdd  
//============pangben 2012-5-30 �Ż�sql
insertCLPManagedWithIBSOrdd.Type=TSQL
insertCLPManagedWithIBSOrdd.SQL= INSERT INTO CLP_MANAGED (TOT,&
				 ORDER_NO,REGION_CODE,CASE_NO,CLNCPATH_CODE, SCHD_CODE, &
				 ORDER_SEQ, &
				 CHKTYPE_CODE, STANDING_DTTM, CHKUSER_CODE, ORDER_FLG, &
				 SCHD_DESC, STANDARD_FLG, MAINORD_CODE, MAINTOT, MAINDISPENSE_UNIT, &
				 OPT_USER, OPT_DATE, OPT_TERM, EXEC_FLG, CHANGE_FLG, MAIN_AMT, &
				 MAINCFM_USER,PROGRESS_CODE,MEDICAL_NOTE,DEPT_CODE,EXE_DEPT_CODE,ORDTYPE_CODE,ORDER_CODE,TOT_AMT,ORDER_SEQ_NO)&
				 VALUES(&
				 0,<ORDER_NO>,<REGION_CODE>,<CASE_NO>,<CLNCPATH_CODE>,<SCHD_CODE>,&
				<ORDER_SEQ>,&
				(SELECT   CASE (SELECT COUNT(*) FROM CLP_CHKTYPE WHERE CHKTYPE_CODE=<ORDER_CAT1_CODE>)&
	WHEN 0 THEN '99999999' ELSE (SELECT CHKTYPE_CODE FROM CLP_CHKTYPE WHERE CHKTYPE_CODE=<ORDER_CAT1_CODE>) END FROM DUAL),&
				TO_DATE(<STANDING_DTTM>,'YYYYMMDD'),<CHK_USER>,'Y', &
				(SELECT DURATION_CHN_DESC FROM CLP_DURATION WHERE DURATION_CODE=<SCHD_CODE>),'N',&
				<ORDER_CODE>,<QTY>,<DOSAGE_UNIT>,<OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>,'N','N',&
				<MAIN_AMT>,<EXEC_USER>,<PROGRESS_CODE>,<MEDICAL_NOTE>,<DEPT_CODE>,&
				<EXE_DEPT_CODE>,&
					(&
					SELECT CASE &
			(SELECT COUNT(CLP_ORDERTYPE.ORDTYPE_CODE)  FROM  CLP_ORDERTYPE  WHERE  CLP_ORDERTYPE.ORDER_CODE=<ORDER_CODE>) &
		WHEN 0 THEN '' ELSE &
					(SELECT ORDTYPE_CODE FROM (SELECT CLP_ORDERTYPE.ORDTYPE_CODE,ROWNUM AS ROWNUMBER  FROM  CLP_ORDERTYPE  WHERE  CLP_ORDERTYPE.ORDER_CODE=<ORDER_CODE>) &
					 WHERE ROWNUMBER<=1)END FROM DUAL &
		)&
				,<ORDER_CODE>,<TOT_AMT>,0)
insertCLPManagedWithIBSOrdd.Debug=N
        

#ɾ����AND A.DISPENSE_UNIT=<MEDI_UNIT> 
#���ݲ�����Ϣ��ѯ����Ҫ���µ�clpManaged������Ϣ
//==============pangben 2012-05-25 �Ż�sql ���
getUpdateDataInCLPManagedWithPatienInfo.Type=TSQL
getUpdateDataInCLPManagedWithPatienInfo.SQL=SELECT A.MAINORD_CODE, A.ORDER_CODE, A.CASE_NO,A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_NO,A.ORDER_SEQ,&
			   		    A.ORDTYPE_CODE,B.CLP_UNIT,B.CLP_QTY,C.ORDERSET_FLG &
                                            FROM CLP_MANAGED A,CLP_ORDERTYPE B,SYS_FEE C,IBS_ORDD D &
 					    WHERE A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE AND A.ORDER_CODE=D.ORDER_CODE AND A.ORDTYPE_CODE=B.ORDTYPE_CODE AND A.CASE_NO = D.CASE_NO AND A.SCHD_CODE = D.SCHD_CODE  &
                                            AND D.CASE_NO=<CASE_NO> AND D.CLNCPATH_CODE = <CLNCPATH_CODE> &
                                            AND A.SCHD_CODE = <SCHD_CODE>
                                            //AND A.ORDER_CODE IN &
                                            //(SELECT ORDER_CODE FROM IBS_ORDD WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE>)
getUpdateDataInCLPManagedWithPatienInfo.Debug=N



#����managedfrom IBSOrdd 
updateCLPManagedWithIBSOrdd.Type=TSQL
updateCLPManagedWithIBSOrdd.SQL=UPDATE CLP_MANAGED SET &
				MAINORD_CODE=<ORDER_CODE>,&
				MAINTOT=<QTY>,&
				MAINDISPENSE_UNIT=<DOSAGE_UNIT>,&
				MAINCFM_USER=<EXEC_USER>,&
				MAIN_AMT=<MAIN_AMT>,&
				MAINORDTYPE_CODE=<ORDTYPE_CODE>,&
				PROGRESS_CODE=<PROGRESS_CODE>,&
				MEDICAL_NOTE=<MEDICAL_NOTE>,&
				OPT_USER=<OPT_USER>,&
				OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),&
				OPT_TERM=<OPT_TERM>,&
				DEPT_CODE=<DEPT_CODE>,&
				EXE_DEPT_CODE=<EXE_DEPT_CODE> &
							WHERE     CASE_NO = <CASE_NO> &
					AND CLNCPATH_CODE=<CLNCPATH_CODE> &
				      AND SCHD_CODE=<SCHD_CODE> &
				      AND ORDER_NO =<ORDER_NO> &
				      AND ORDER_SEQ = <ORDER_SEQ> 
updateCLPManagedWithIBSOrdd.Debug=N

//==========pangben 2012-7-6 ����ҽ��û��ϸ�� ����ʾ���� ����
updateCLPManagedWithIBSOrddOrderSet.Type=TSQL
updateCLPManagedWithIBSOrddOrderSet.SQL=UPDATE CLP_MANAGED SET &
				MAINORD_CODE=<ORDER_CODE>,&
				MAINTOT=<QTY>,&
				MAINDISPENSE_UNIT=<DOSAGE_UNIT>,&
				MAINCFM_USER=<EXEC_USER>,&
				MAIN_AMT=TOT_AMT*<QTY>,&
				MAINORDTYPE_CODE=<ORDTYPE_CODE>,&
				PROGRESS_CODE=<PROGRESS_CODE>,&
				MEDICAL_NOTE=<MEDICAL_NOTE>,&
				OPT_USER=<OPT_USER>,&
				OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),&
				OPT_TERM=<OPT_TERM>,&
				DEPT_CODE=<DEPT_CODE>,&
				EXE_DEPT_CODE=<EXE_DEPT_CODE> &
							WHERE     CASE_NO = <CASE_NO> &
					AND CLNCPATH_CODE=<CLNCPATH_CODE> &
				      AND SCHD_CODE=<SCHD_CODE> &
				      AND ORDER_NO =<ORDER_NO> &
				      AND ORDER_SEQ = <ORDER_SEQ> 
updateCLPManagedWithIBSOrddOrderSet.Debug=N

#���ݲ�����Ϣɾ��չ����Ϣ - ·����ԭ
deleteCLPManagedWithPatientInfo.Type=TSQL
deleteCLPManagedWithPatientInfo.SQL=DELETE FROM  CLP_MANAGED WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE> AND STANDARD_FLG NOT IN ('Y')
deleteCLPManagedWithPatientInfo.Debug=N

# ���ݲ�����Ϣ����չ����Ϣ - ·����ԭ
updateCLPManagedWithPatientInfo.Type=TSQL
updateCLPManagedWithPatientInfo.SQL=UPDATE CLP_MANAGED SET MAINORD_CODE = NULL, &
																		MAINTOT = NULL,  MAINDISPENSE_UNIT = NULL, &
																		MAIN_AMT = NULL, MAINCFM_USER = NULL,PROGRESS_CODE = NULL,&
																		MEDICAL_NOTE = NULL, CFM_DTTM = NULL,CFM_USER = NULL,&
																		OPT_USER = <OPT_USER> ,OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),&
																		OPT_TERM=<OPT_TERM>  &
																		WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE> &
																		AND STANDARD_FLG = 'Y' 
updateCLPManagedWithPatientInfo.Debug=N

//ҽ��Ϊ����״̬������ CLP_MANAGED����ҽ��������SYS_FEE �д���
selectCLPManagedWithCondition.Type=TSQL
selectCLPManagedWithCondition.SQL=SELECT A.CASE_NO,A.CLNCPATH_CODE,A.SCHD_CODE,SUM(A.TOT_AMT) AS TOT_AMT,SUM(A.MAIN_AMT) AS MAIN_AMT,C.IPD_CHARGE_CODE &
                                  FROM CLP_MANAGED A ,SYS_FEE B,SYS_CHARGE_HOSP C &
                                  WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE(+) &
                                  AND  A.CASE_NO=<CASE_NO> AND A.CLNCPATH_CODE=<CLNCPATH_CODE> GROUP BY A.CASE_NO,A.CLNCPATH_CODE,A.SCHD_CODE,C.IPD_CHARGE_CODE &
                                  ORDER BY A.SCHD_CODE, C.IPD_CHARGE_CODE
selectCLPManagedWithCondition.Debug=N

selectCLPManagedWithCondition1.Type=TSQL
selectCLPManagedWithCondition1.SQL=SELECT A.CASE_NO,A.CLNCPATH_CODE,A.SCHD_CODE,A.ORDER_CODE,A.TOT_AMT,A.MAIN_AMT,A.MAINORD_CODE,B.CHARGE_HOSP_CODE ,C.IPD_CHARGE_CODE &
                                  FROM CLP_MANAGED A ,SYS_FEE B,SYS_CHARGE_HOSP C &
                                  WHERE A.ORDER_CODE=B.ORDER_CODE(+) AND  B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE(+) &
                                  AND  A.CASE_NO=<CASE_NO> AND A.CLNCPATH_CODE=<CLNCPATH_CODE> ORDER BY A.SCHD_CODE, C.IPD_CHARGE_CODE
selectCLPManagedWithCondition1.Debug=N

selectOwnPrice.Type=TSQL
selectOwnPrice.SQL=	SELECT OWN_PRICE  FROM SYS_FEE WHERE ORDER_CODE=<ORDER_CODE> 
selectOwnPrice.Debug=N

saveCLPBill.Type=TSQL
saveCLPBill.SQL=
saveCLPBill.Debug=N

updateCLPBill.Type=TSQL
updateCLPBill.SQL=
updateCLPBill.Debug=N

checkCLPBillExist.Type=TSQL
checkCLPBillExist.SQL=SELECT COUNT(*) AS TOTALCOUNT FROM CLP_BILL WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE> AND SCHD_TYPE=<SCHD_TYPE> &
										  AND SCHD_CODE=<SCHD_CODE> 
checkCLPBillExist.Debug=N

selectPatientOrderCodeList.Type=TSQL
selectPatientOrderCodeList.SQL=SELECT D.CASE_NO,D.EXE_DR_CODE,D.MEDI_QTY,D.MEDI_UNIT,D.FREQ_CODE,S.ORDER_DESC,TOT_AMT, D.ORDER_CODE AS ORDER_CODE, S.UNIT_CODE AS MEDI_UNIT,D.DOSAGE_QTY AS QTY,S.ORDER_CAT1_CODE AS ORDER_CAT1_CODE,&
												       CASE WHEN M.DATA_TYPE NOT IN ('0','2','B') THEN '002' ELSE '001' END AS CHK_USER, '' AS EXEC_USER,D.CLNCPATH_CODE CLNCPATH_CODE,D.SCHD_CODE SCHD_CODE,M.DEPT_CODE,D.EXE_DEPT_CODE &
												       FROM IBS_ORDM M, IBS_ORDD D, SYS_FEE S &
												       WHERE &
												       M.CASE_NO = <CASE_NO> &
												       AND D.CASE_NO = M.CASE_NO &
												       AND D.CASE_NO_SEQ = M.CASE_NO_SEQ &
												       AND (D.ORDERSET_CODE IS NULL OR D.ORDER_CODE = D.ORDERSET_CODE) &
												       AND S.ORDER_CODE = D.ORDER_CODE &
												       AND D.CLNCPATH_CODE = <CLNCPATH_CODE> 
selectPatientOrderCodeList.Debug=N
