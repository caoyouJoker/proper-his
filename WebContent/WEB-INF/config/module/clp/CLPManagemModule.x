# 
#  Title:临床路径准进准出module
# 
#  Description:临床路径准进准出
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author luhai 2011-05-10
#  version 1.0
#
Module.item=selectData;selectCancelData;selectOverData;selectPatientData;insertPatientManagem;deleteData;updatePatientManagem;&
            updateChangeManagem;moveManagermData;selectDurationData;saveDurationData;delDurationData;openDuraction;&
            moveDeleteAndEndManagemIntoHistory;insertAMDINPIntoCLNCPathCode;queryClpManagem

#查询准入路径语句//fux modify 20120704 将P.BED_NO——》P.BED_NO_DESC
selectData.Type=TSQL
selectData.SQL=SELECT DECODE(A.START_DTTM,NULL,'N','Y') AS IS_IN ,B.VERSION,&
							 				DECODE(A.DELETE_DTTM,NULL,'N','Y') AS IS_CANCELLATION ,&
							 				DECODE(A.END_DTTM,NULL,'N','Y') AS IS_OVERFLOW,&
							 				D.BED_NO_DESC,P.MR_NO,O.PAT_NAME,S.ICD_CHN_DESC,&
							 				B.CLNCPATH_CHN_DESC,B.ACPT_CODE,B.EXIT_CODE,&
							 				TO_CHAR(P.IN_DATE,'YYYY/MM/DD HH24:MI:SS') AS IN_DATE ,&									
							 				TO_CHAR(A.START_DTTM,'YYYY/MM/DD HH24:MI:SS') AS START_DTTM ,&
							 				TO_CHAR(A.DELETE_DTTM,'YYYY/MM/DD HH24:MI:SS') AS DELETE_DTTM ,&							
							 				TO_CHAR(A.END_DTTM,'YYYY/MM/DD HH24:MI:SS') AS END_DTTM ,&								
							 				DECODE(O.SEX_CODE,'1','男','2','女','9','不详') AS SEX_CODE ,&																					
							 				TO_CHAR(O.BIRTH_DATE,'YYYY/MM/DD') AS BIRTH_DATE ,&																							
							 				P.VS_DR_CODE,&																					
							 				P.DS_DEPT_CODE,&																
							 				D.STATION_CODE,&																		
							 				A.EVL_CODE,&																				
							 				DECODE(A.STATUS,'1','Y','N') AS STATUS ,&													
							 				OP.USER_NAME,&																							  
							 				K.DEPT_CHN_DESC,&
							 				N.STATION_DESC,&
							 				A.CLNCPATH_CODE,&
							 				P.CASE_NO,		&
							 				A.CLNCPATH_CODE, &
							 				A.OUTISSUE_CODE,P.DEPT_CODE &						
						   FROM CLP_MANAGEM A,&
						   			ADM_INP P,&																							
						   			SYS_PATINFO O,&																				
						   			SYS_DIAGNOSIS S,&																				
						   			CLP_BSCINFO B,&																
						   		  SYS_BED D,&																					
						   		  SYS_OPERATOR OP,&
						   		  SYS_DEPT K,&																								
						   		  SYS_STATION N &
						   WHERE A.DELETE_DTTM IS NULL &
								     AND A.END_DTTM IS NULL &
								     AND A.REGION_CODE = P.REGION_CODE &
								     AND A.CASE_NO = P.CASE_NO(+) &
								     //出院不能显示问题修改--xiongwg20150507//AND P.DS_DATE IS NULL &
								     AND A.MR_NO = O.MR_NO(+) &
								     AND P.DRG_CODE=S.ICD_CODE(+) &
								     AND A.REGION_CODE = B.REGION_CODE(+) &
								     AND A.CLNCPATH_CODE = B.CLNCPATH_CODE(+) &
								     AND P.BED_NO = D.BED_NO(+) &
								     AND P.REGION_CODE = D.REGION_CODE(+)  &
								     AND P.VS_DR_CODE = OP.USER_ID(+) &
								     AND P.REGION_CODE=OP.REGION_CODE(+) &
								     AND P.DEPT_CODE = K.DEPT_CODE(+) &
								     AND P.REGION_CODE=K.REGION_CODE(+) &
								     AND P.STATION_CODE = N.STATION_CODE(+) &
								     AND P.REGION_CODE = N.REGION_CODE(+) &
								     AND A.REGION_CODE=<REGION_CODE> &
										 ORDER BY P.CASE_NO  
selectData.item=MR_NO;CASE_NO;CLNCPATH_CODE;EVL_CODE
selectData.MR_NO= A.MR_NO = <MR_NO>
selectData.CASE_NO= A.CASE_NO = <CASE_NO>
selectData.Debug=N

#查询作废路径语句//fux modify 20120704 将P.BED_NO——》P.BED_NO_DESC
selectCancelData.Type=TSQL
selectCancelData.SQL=SELECT 'N' AS IS_IN ,A.VERSION,&
							 				DECODE(A.DELETE_DTTM,NULL,'N','Y') AS IS_CANCELLATION ,&
							 				DECODE(A.END_DTTM,NULL,'N','Y') AS IS_OVERFLOW,&
							 				D.BED_NO_DESC,P.MR_NO,O.PAT_NAME,S.ICD_CHN_DESC,&
							 				B.CLNCPATH_CHN_DESC,B.ACPT_CODE,B.EXIT_CODE,&
							 				TO_CHAR(P.IN_DATE,'YYYY/MM/DD HH24:MI:SS') AS IN_DATE ,&									
							 				TO_CHAR(A.START_DTTM,'YYYY/MM/DD HH24:MI:SS') AS START_DTTM ,&
							 				TO_CHAR(A.DELETE_DTTM,'YYYY/MM/DD HH24:MI:SS') AS DELETE_DTTM ,&							
							 				TO_CHAR(A.END_DTTM,'YYYY/MM/DD HH24:MI:SS') AS END_DTTM ,&								
							 				DECODE(O.SEX_CODE,'1','男','2','女','9','不详') AS SEX_CODE ,&																					
							 				TO_CHAR(O.BIRTH_DATE,'YYYY/MM/DD') AS BIRTH_DATE ,&																							
							 				P.VS_DR_CODE,&																					
							 				P.DS_DEPT_CODE,&																
							 				D.STATION_CODE,&																		
							 				A.EVL_CODE,&																				
							 				DECODE(A.STATUS,'1','Y','N') AS STATUS ,&													
							 				OP.USER_NAME,&																							  
							 				K.DEPT_CHN_DESC,&
							 				N.STATION_DESC,&
							 				A.CLNCPATH_CODE,&
							 				P.CASE_NO,		&
							 				A.CLNCPATH_CODE, &
							 				A.OUTISSUE_CODE,P.DEPT_CODE &						
						   FROM CLP_MANAGEM A,&
						   			ADM_INP P,&																							
						   			SYS_PATINFO O,&																				
						   			SYS_DIAGNOSIS S,&																				
						   			CLP_BSCINFO B,&																
						   		  SYS_BED D,&																					
						   		  SYS_OPERATOR OP,&
						   		  SYS_DEPT K,&																								
						   		  SYS_STATION N &
						   WHERE A.DELETE_DTTM IS NOT NULL &
								     AND A.END_DTTM IS NULL &
								     AND A.REGION_CODE = P.REGION_CODE &
								     AND A.CASE_NO = P.CASE_NO(+) &
								     //出院不能显示问题修改--xiongwg20150507//AND P.DS_DATE IS NULL &
								     AND A.MR_NO = O.MR_NO(+) &
								     AND P.DRG_CODE=S.ICD_CODE(+) &
								     AND A.REGION_CODE = B.REGION_CODE(+) &
								     AND A.CLNCPATH_CODE = B.CLNCPATH_CODE(+) &
								     AND P.BED_NO = D.BED_NO(+) &
								     AND P.REGION_CODE = D.REGION_CODE(+)  &
								     AND P.VS_DR_CODE = OP.USER_ID(+) &
								     AND P.REGION_CODE=OP.REGION_CODE(+) &
								     AND P.DEPT_CODE = K.DEPT_CODE(+) &
								     AND P.REGION_CODE=K.REGION_CODE(+) &
								     AND P.STATION_CODE = N.STATION_CODE(+) &
								     AND P.REGION_CODE = N.REGION_CODE(+) &
								     AND A.REGION_CODE=<REGION_CODE> &
										 ORDER BY P.CASE_NO  
selectCancelData.item=MR_NO;CASE_NO;CLNCPATH_CODE;EVL_CODE
selectCancelData.MR_NO= A.MR_NO = <MR_NO>
selectCancelData.CASE_NO= A.CASE_NO = <CASE_NO>
selectCancelData.Debug=N

#查询溢出路径语句//fux modify 20120704 将P.BED_NO——》P.BED_NO_DESC
selectOverData.Type=TSQL
selectOverData.SQL=SELECT 'N' AS IS_IN ,A.VERSION,&
							 				'N' AS IS_CANCELLATION ,&
							 				DECODE(A.END_DTTM,NULL,'N','Y') AS IS_OVERFLOW,&
							 				D.BED_NO_DESC,P.MR_NO,O.PAT_NAME,S.ICD_CHN_DESC,&
							 				B.CLNCPATH_CHN_DESC,B.ACPT_CODE,B.EXIT_CODE,&
							 				TO_CHAR(P.IN_DATE,'YYYY/MM/DD HH24:MI:SS') AS IN_DATE ,&									
							 				TO_CHAR(A.START_DTTM,'YYYY/MM/DD HH24:MI:SS') AS START_DTTM ,&
							 				TO_CHAR(A.DELETE_DTTM,'YYYY/MM/DD HH24:MI:SS') AS DELETE_DTTM ,&							
							 				TO_CHAR(A.END_DTTM,'YYYY/MM/DD HH24:MI:SS') AS END_DTTM ,&								
							 				DECODE(O.SEX_CODE,'1','男','2','女','9','不详') AS SEX_CODE ,&																					
							 				TO_CHAR(O.BIRTH_DATE,'YYYY/MM/DD') AS BIRTH_DATE ,&																							
							 				P.VS_DR_CODE,&																					
							 				P.DS_DEPT_CODE,&																
							 				D.STATION_CODE,&																		
							 				A.EVL_CODE,&																				
							 				DECODE(A.STATUS,'1','Y','N') AS STATUS ,&													
							 				OP.USER_NAME,&																							  
							 				K.DEPT_CHN_DESC,&
							 				N.STATION_DESC,&
							 				A.CLNCPATH_CODE,&
							 				P.CASE_NO,		&
							 				A.CLNCPATH_CODE, &
							 				A.OUTISSUE_CODE,P.DEPT_CODE &						
						   FROM CLP_MANAGEM A,&
						   			ADM_INP P,&																							
						   			SYS_PATINFO O,&																				
						   			SYS_DIAGNOSIS S,&																				
						   			CLP_BSCINFO B,&																
						   		  SYS_BED D,&																					
						   		  SYS_OPERATOR OP,&
						   		  SYS_DEPT K,&																								
						   		  SYS_STATION N &
						   WHERE  A.END_DTTM IS NOT NULL &
								     AND A.REGION_CODE = P.REGION_CODE &
								     AND A.CASE_NO = P.CASE_NO(+) &
								     //出院不能显示问题修改--xiongwg20150507//AND P.DS_DATE IS NULL &
								     AND A.MR_NO = O.MR_NO(+) &
								     AND P.DRG_CODE=S.ICD_CODE(+) &
								     AND A.REGION_CODE = B.REGION_CODE(+) &
								     AND A.CLNCPATH_CODE = B.CLNCPATH_CODE(+) &
								     AND P.BED_NO = D.BED_NO(+) &
								     AND P.REGION_CODE = D.REGION_CODE(+)  &
								     AND P.VS_DR_CODE = OP.USER_ID(+) &
								     AND P.REGION_CODE=OP.REGION_CODE(+) &
								     AND P.DEPT_CODE = K.DEPT_CODE(+) &
								     AND P.REGION_CODE=K.REGION_CODE(+) &
								     AND P.STATION_CODE = N.STATION_CODE(+) &
								     AND P.REGION_CODE = N.REGION_CODE(+) &
								     AND A.REGION_CODE=<REGION_CODE> &
										 ORDER BY P.CASE_NO  
selectOverData.item=MR_NO;CASE_NO;CLNCPATH_CODE;EVL_CODE
selectOverData.MR_NO= A.MR_NO = <MR_NO>
selectOverData.CASE_NO= A.CASE_NO = <CASE_NO>
selectOverData.Debug=N


#查询语句查询住院病人信息
selectPatientData.Type=TSQL
selectPatientData.SQL=SELECT &
							 				P.BED_NO,P.MR_NO,O.PAT_NAME,S.ICD_CHN_DESC,&
							 				TO_CHAR(P.IN_DATE,'YYYY/MM/DD') AS IN_DATE ,&															
							 				DECODE(O.SEX_CODE,'1','男','2','女','9','不详') AS SEX_CODE,&																						
							 				TO_CHAR(O.BIRTH_DATE,'YYYY/MM/DD HH:MI:SS') AS BIRTH_DATE ,&																							
							 				P.VS_DR_CODE,&																					
							 				P.DS_DEPT_CODE,&																
							 				D.STATION_CODE,&																																													
							 				OP.USER_NAME,&																							  
							 				K.DEPT_CHN_DESC,&
							 				N.STATION_DESC,&
							 				P.CASE_NO		&						
						   FROM &
						   			ADM_INP P,&																							
						   			SYS_PATINFO O,&																				
						   			SYS_DIAGNOSIS S,&																																		
						   		  SYS_BED D,&																					
						   		  SYS_OPERATOR OP,&
						   		  SYS_DEPT K,&																								
						   		  SYS_STATION N &
						   WHERE &
								     //出院不能显示问题修改--xiongwg20150507//P.DS_DATE IS NULL &
								     P.DRG_CODE=S.ICD_CODE(+) &
								     AND P.BED_NO = D.BED_NO(+) &
								     AND P.REGION_CODE = D.REGION_CODE(+)  &
								     AND P.VS_DR_CODE = OP.USER_ID(+) &
								     AND P.REGION_CODE=OP.REGION_CODE(+) &
								     AND P.DEPT_CODE = K.DEPT_CODE(+) &
								     AND P.REGION_CODE=K.REGION_CODE(+) &
								     AND P.STATION_CODE = N.STATION_CODE(+) &
								     AND P.REGION_CODE = N.REGION_CODE(+) &
								     AND P.MR_NO=<MR_NO> &
								     AND P.MR_NO = O. MR_NO(+)  &
								     AND P.REGION_CODE=<REGION_CODE> &
										 ORDER BY P.CASE_NO
selectPatientData.item=MR_NO;CASE_NO
selectPatientData.MR_NO= P.MR_NO = <MR_NO>
selectPatientData.CASE_NO= P.CASE_NO = <CASE_NO>										 
selectPatientData.Debug=N


#插入病人对应的临床路径
insertPatientManagem.Type=TSQL
insertPatientManagem.SQL= INSERT INTO CLP_MANAGEM &
													(CASE_NO,CLNCPATH_CODE,EVL_CODE,MR_NO,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM,IN_DATE,VERSION,START_DTTM)&
													 SELECT <CASE_NO>,<CLNCPATH_CODE>,<EVL_CODE>,<MR_NO>,<REGION_CODE>,<OPT_USER>,&
														TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>,&
														(SELECT IN_DATE FROM ADM_INP WHERE CASE_NO=<CASE_NO> )AS IN_DATE, &
														VERSION,TO_DATE(<START_DTTM>,'YYYYMMDDHH24MISS') FROM CLP_BSCINFO WHERE CLNCPATH_CODE = <CLNCPATH_CODE>
insertPatientManagem.Debug=N






insertAMDINPIntoCLNCPathCode.Type=TSQL
insertAMDINPIntoCLNCPathCode.SQL=	UPDATE ADM_INP SET CLNCPATH_CODE=<CLNCPATH_CODE>,SCHD_CODE=<SCHD_CODE> WHERE CASE_NO=<CASE_NO>
insertAMDINPIntoCLNCPathCode.Debug=N

#删除病人的临床路径
deleteData.Type=TSQL
deleteData.SQL= DELETE FROM CLP_MANAGEM WHERE CASE_NO = <CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE>
deleteData.Debug=N



#在变更病人临床路径信息时更新原路径信息
updateChangeManagem.Type=TSQL
updateChangeManagem.SQL=UPDATE CLP_MANAGEM SET & 
													CHANGE_REASON=<CHANGE_REASON> &
													WHERE CASE_NO = <CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE>
updateChangeManagem.Debug=N


moveManagermData.Type=TSQL
moveManagermData.SQL=	 INSERT INTO CLP_MANAGEM_HISTORY (&
												CASE_NO,CLNCPATH_CODE,REGION_CODE,VERSION,MR_NO,IN_DATE,START_DTTM,DELETE_DTTM,END_DTTM,EVL_CODE,&
												STATUS,DESCRIPTION,OUTISSUE_CODE,CHANGE_REASON,OPT_USER,OPT_DATE,OPT_TERM,SEQ_NO &
											 )&
											 SELECT CASE_NO,CLNCPATH_CODE,REGION_CODE,VERSION,MR_NO,IN_DATE,START_DTTM,DELETE_DTTM,&
											 END_DTTM,EVL_CODE,STATUS,DESCRIPTION,OUTISSUE_CODE,CHANGE_REASON,OPT_USER,OPT_DATE,OPT_TERM, &
											 (SELECT CASE  (COUNT(MAX(SEQ_NO)+1)) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ_NO)+1)) END FROM CLP_MANAGEM_HISTORY WHERE  CASE_NO = <CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE> &
											 GROUP BY SEQ_NO) AS SEQ_NO &
											 FROM CLP_MANAGEM WHERE CASE_NO = <CASE_NO> AND CLNCPATH_CODE = <CLNCPATH_CODE>
moveManagermData.Debug=N

//============pangben 2012-05-25 优化sql
selectDurationData.Type=TSQL
selectDurationData.SQL=SELECT A.CLNCPATH_CODE,B.SCHD_CODE,C.SUSTAINED_DAYS ,C.SCHD_DAY ,B.SUSTAINED_DAYS AS STANDARD_SUSTAINED_DAYS,B.SCHD_DAY AS STANDARD_SCHD_DAY, &
				TO_CHAR(C.START_DATE,'YYYY/MM/DD HH24:MI:SS') AS START_DATE,TO_CHAR(C.END_DATE,'YYYY/MM/DD HH24:MI:SS') AS END_DATE ,A.CASE_NO, &
				TO_CHAR(A.IN_DATE,'YYYY/MM/DD') AS IN_DATE &
				FROM CLP_MANAGEM A, CLP_THRPYSCHDM B ,CLP_THRPYSCHDM_REAL C &
				WHERE  A.CLNCPATH_CODE=B.CLNCPATH_CODE &
				AND B.CLNCPATH_CODE=C.CLNCPATH_CODE(+) &
				AND B.SCHD_CODE=C.SCHD_CODE(+) &
				AND A.CASE_NO=<CASE_NO> AND C.CASE_NO=<CASE_NO> &
				AND A.CLNCPATH_CODE=<CLNCPATH_CODE>  AND A.REGION_CODE = <REGION_CODE> &			
			  ORDER BY B.SEQ
selectDurationData.Debug=N

saveDurationData.Type=TSQL
saveDurationData.SQL=INSERT INTO CLP_THRPYSCHDM_REAL (CLNCPATH_CODE,SCHD_CODE,CASE_NO,SEQ,REGION_CODE,SCHD_DAY,SUSTAINED_DAYS,OPT_USER, &
		 OPT_TERM,START_DATE,END_DATE,OPT_DATE)VALUES ( &
		 <CLNCPATH_CODE>,<SCHD_CODE>,<CASE_NO>, <SEQ>, &
			 <REGION_CODE>,<SCHD_DAY>,<SUSTAINED_DAYS>,<OPT_USER>,<OPT_TERM>,TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS'),TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS'),TO_DATE(<OPT_DATE>,'YYYYMMDD')&
		 )
saveDurationData.Debug=N

delDurationData.Type=TSQL
delDurationData.SQL=DELETE FROM CLP_THRPYSCHDM_REAL WHERE CASE_NO=<CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE> AND SCHD_CODE=<SCHD_CODE>
delDurationData.Debug=N

openDuraction.Type=TSQL
openDuraction.SQL=UPDATE  IBS_ORDD SET CLNCPATH_CODE =<CLNCPATH_CODE> ,SCHD_CODE=<SCHD_CODE>,OPT_USER=<OPT_USER>, &
									OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),OPT_TERM=<OPT_TERM> WHERE &
									BILL_FLG='Y'AND BILL_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS') AND TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
									AND CASE_NO=<CASE_NO> 
openDuraction.Debug=N

moveDeleteAndEndManagemIntoHistory.Type=TSQL
moveDeleteAndEndManagemIntoHistory.SQL=INSERT INTO CLP_MANAGEM_HISTORY (CASE_NO,CLNCPATH_CODE,VERSION,MR_NO,IN_DATE,START_DTTM,DELETE_DTTM,END_DTTM,EVL_CODE,STATUS,DESCRIPTION,&
					OUTISSUE_CODE,CHANGE_REASON,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM,SEQ_NO)&
					SELECT CASE_NO,CLNCPATH_CODE,VERSION,MR_NO,IN_DATE,START_DTTM,DELETE_DTTM,END_DTTM,EVL_CODE,STATUS,DESCRIPTION,&
					OUTISSUE_CODE,CHANGE_REASON,<REGION_CODE>,<OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>,&
					(SELECT CASE  (COUNT(MAX(SEQ_NO)+1)) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ_NO)+1)) END FROM CLP_MANAGEM_HISTORY &
					WHERE CASE_NO = <CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE> &
					GROUP BY SEQ_NO) AS SEQ_NO FROM CLP_MANAGEM WHERE CASE_NO = <CASE_NO> AND CLNCPATH_CODE=<CLNCPATH_CODE>
moveDeleteAndEndManagemIntoHistory.Debug=N

//查询CLP_MANAGEM表数据
queryClpManagem.Type=TSQL
queryClpManagem.SQL=SELECT C.CASE_NO, C.CLNCPATH_CODE, C.REGION_CODE,& 
   			  C.VERSION, C.MR_NO,B.DS_DATE, &
   			  C.START_DTTM, C.DELETE_DTTM, C.END_DTTM,& 
  			  C.EVL_CODE, C.STATUS, C.DESCRIPTION,& 
  			  C.OUTISSUE_CODE, C.CHANGE_REASON &
  			  FROM CLP_MANAGEM C,ADM_INP B WHERE B.CASE_NO=C.CASE_NO(+)
queryClpManagem.item=MR_NO;CASE_NO;CLNCPATH_CODE
queryClpManagem.MR_NO= B.MR_NO = <MR_NO>
queryClpManagem.CASE_NO= B.CASE_NO = <CASE_NO>
queryClpManagem.CLNCPATH_CODE= C.CLNCPATH_CODE = <CLNCPATH_CODE>
queryClpManagem.Debug=N