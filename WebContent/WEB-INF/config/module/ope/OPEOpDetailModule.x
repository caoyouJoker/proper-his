####################################################
#  Title:手术记录module
# 
#  Description:手术记录module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.9.28
#  version 4.0
####################################################
Module.item=selectData;insertData;updateData;intoOPEDataForMRO;selectForEmr;selectForEmrByBookNo;selectDataForSUM

//插入手术记录
insertData.Type=TSQL
insertData.SQL=INSERT INTO OPE_OPDETAIL ( &
		   OP_RECORD_NO, OPBOOK_NO, ADM_TYPE, &
		   MR_NO, CASE_NO, IPD_NO, &
		   OP_DEPT_CODE, OP_STATION_CODE, BED_NO, &
		   OP_DATE, ROOM_NO, URGBLADE_FLG, &
		   TYPE_CODE, RANK_CODE, WAY_CODE, &
		   ANA_CODE, DIAG_CODE1, DIAG_CODE2, &
		   DIAG_CODE3, OP_CODE1, OP_CODE2, &
		   MAIN_SURGEON, REAL_AST1, REAL_AST2, &
		   REAL_AST3, REAL_AST4, CIRCULE_USER1, &
		   CIRCULE_USER2, CIRCULE_USER3, CIRCULE_USER4, &
		   SCRUB_USER1, SCRUB_USER2, SCRUB_USER3, &
		   SCRUB_USER4, ANA_USER1, ANA_USER2, &
		   EXTRA_USER1, EXTRA_USER2, DRG_CODE, &
		   NURSE_START_DATE, ENTER_DATE, OP_START_DATE, &
		   OP_END_DATE, NURSE_END_DATE, EXIT_DATE, &
		   BIOPSY_FLG, BILL_FLG, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		   //=========================add by wanglong 20121206
	           ,PART_CODE, ASA_CODE, NNIS_CODE, ISO_FLG &
		   //=========================
		) VALUES ( &
		   <OP_RECORD_NO>, <OPBOOK_NO>, <ADM_TYPE>, &
		   <MR_NO>, <CASE_NO>, <IPD_NO>, &
		   <OP_DEPT_CODE>, <OP_STATION_CODE>, <BED_NO>, &
		   TO_DATE(<OP_DATE>,'YYYYMMDDHH24MISS'), <ROOM_NO>, <URGBLADE_FLG>, &
		   <TYPE_CODE>, <RANK_CODE>, <WAY_CODE>, &
		   <ANA_CODE>, <DIAG_CODE1>, <DIAG_CODE2>, &
		   <DIAG_CODE3>, <OP_CODE1>, <OP_CODE2>, &
		   <MAIN_SURGEON>, <REAL_AST1>, <REAL_AST2>, &
		   <REAL_AST3>, <REAL_AST4>, <CIRCULE_USER1>, &
		   <CIRCULE_USER2>, <CIRCULE_USER3>, <CIRCULE_USER4>, &
		   <SCRUB_USER1>, <SCRUB_USER2>, <SCRUB_USER3>, &
		   <SCRUB_USER4>, <ANA_USER1>, <ANA_USER2>, &
		   <EXTRA_USER1>, <EXTRA_USER2>, <DRG_CODE>, &
		   <NURSE_START_DATE>, <ENTER_DATE>, TO_DATE(<OP_START_DATE>,'YYYYMMDDHH24MISS'), &
		   TO_DATE(<OP_END_DATE>,'YYYYMMDDHH24MISS'), <NURSE_END_DATE>, <EXIT_DATE>, &
		   <BIOPSY_FLG>, <BILL_FLG>, <OPT_USER>, &
		   SYSDATE, <OPT_TERM> &
		   //=========================add by wanglong 20121206
	           ,<PART_CODE>, <ASA_CODE>, <NNIS_CODE>, <ISO_FLG> &
		   //=========================
		)
insertData.Debug=N

//查询手术记录信息
selectData.Type=TSQL
selectData.SQL=SELECT &
		   OP_RECORD_NO, OPBOOK_NO, ADM_TYPE, &
		   MR_NO, CASE_NO, IPD_NO, &
		   OP_DEPT_CODE, OP_STATION_CODE, BED_NO, &
		   OP_DATE, ROOM_NO, URGBLADE_FLG, &
		   TYPE_CODE, RANK_CODE, WAY_CODE, &
		   ANA_CODE, DIAG_CODE1, DIAG_CODE2, &
		   DIAG_CODE3, OP_CODE1, OP_CODE2, &
		   MAIN_SURGEON, REAL_AST1, REAL_AST2, &
		   REAL_AST3, REAL_AST4, CIRCULE_USER1, &
		   CIRCULE_USER2, CIRCULE_USER3, CIRCULE_USER4, &
		   SCRUB_USER1, SCRUB_USER2, SCRUB_USER3, &
		   SCRUB_USER4, ANA_USER1, ANA_USER2, &
		   EXTRA_USER1, EXTRA_USER2, DRG_CODE, &
		   NURSE_START_DATE, ENTER_DATE, OP_START_DATE, &
		   OP_END_DATE, NURSE_END_DATE, EXIT_DATE, &
		   BIOPSY_FLG, BILL_FLG, OPT_USER, &
		   OPT_DATE, OPT_TERM &
		   //=========================add by wanglong 20121206
	           ,PART_CODE, ASA_CODE, NNIS_CODE, ISO_FLG &
		   //=========================
		FROM OPE_OPDETAIL
selectData.item=OP_RECORD_NO;OPBOOK_NO;ADM_TYPE;MR_NO;CASE_NO;OP_DEPT_CODE
selectData.OP_RECORD_NO=OP_RECORD_NO=<OP_RECORD_NO>
selectData.OPBOOK_NO=OPBOOK_NO=<OPBOOK_NO>
selectData.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectData.MR_NO=MR_NO=<MR_NO>
selectData.CASE_NO=CASE_NO=<CASE_NO>
selectData.OP_DEPT_CODE=OP_DEPT_CODE=<OP_DEPT_CODE>
selectData.Debug=N

//修改手术记录信息
updateData.Type=TSQL
updateData.SQL=UPDATE OPE_OPDETAIL SET &
			OP_DEPT_CODE=<OP_DEPT_CODE>,&
			OP_STATION_CODE=<OP_STATION_CODE>,&
			BED_NO=<BED_NO>,&
			OP_DATE=TO_DATE(<OP_DATE>,'YYYYMMDDHH24MISS'),&
			ROOM_NO=<ROOM_NO>,&
			URGBLADE_FLG=<URGBLADE_FLG>,&
			TYPE_CODE=<TYPE_CODE>,&
			RANK_CODE=<RANK_CODE>,&
			WAY_CODE=<WAY_CODE>,&
			ANA_CODE=<ANA_CODE>,&
			DIAG_CODE1=<DIAG_CODE1>,&
			DIAG_CODE2=<DIAG_CODE2>,&
			DIAG_CODE3=<DIAG_CODE3>,&
			OP_CODE1=<OP_CODE1>,&
			OP_CODE2=<OP_CODE2>,&
			MAIN_SURGEON=<MAIN_SURGEON>,&
			REAL_AST1=<REAL_AST1>,&
			REAL_AST2=<REAL_AST2>,&
			REAL_AST3=<REAL_AST3>,&
			REAL_AST4=<REAL_AST4>,&
			CIRCULE_USER1=<CIRCULE_USER1>,&
			CIRCULE_USER2=<CIRCULE_USER2>,&
			CIRCULE_USER3=<CIRCULE_USER3>,&
			CIRCULE_USER4=<CIRCULE_USER4>,&
			SCRUB_USER1=<SCRUB_USER1>,&
			SCRUB_USER2=<SCRUB_USER2>,&
			SCRUB_USER3=<SCRUB_USER3>,&
			SCRUB_USER4=<SCRUB_USER4>,&
			ANA_USER1=<ANA_USER1>,&
			ANA_USER2=<ANA_USER2>,&
			EXTRA_USER1=<EXTRA_USER1>,&
			EXTRA_USER2=<EXTRA_USER2>,&
			OPT_USER=<OPT_USER>,&
			OPT_DATE=SYSDATE,&
			OPT_TERM=<OPT_TERM> &
		    //=========================add by wanglong 20121206
	        ,PART_CODE=<PART_CODE>&
			,ASA_CODE=<ASA_CODE>&
			,NNIS_CODE=<NNIS_CODE>&
			,ISO_FLG=<ISO_FLG> &
			,OP_END_DATE=TO_DATE(<OP_END_DATE>,'YYYYMMDDHH24MISS') &
		    //=========================
			WHERE OP_RECORD_NO=<OP_RECORD_NO>
updateData.Debug=N

//病案手术信息转入（病案首页使用）
intoOPEDataForMRO.Type=TSQL
intoOPEDataForMRO.SQL=SELECT A.CASE_NO,ROWNUM AS SEQ_NO,A.IPD_NO,A.MR_NO,A.OP_CODE1 AS OP_CODE,&
			B.OPT_CHN_DESC AS OP_DESC,'' AS OP_REMARK,A.OP_DATE,A.ANA_CODE AS ANA_WAY,A.ANA_USER1 AS ANA_DR,&
			A.MAIN_SURGEON AS MAIN_SUGEON,A.REAL_AST1 AS AST_DR1,A.REAL_AST2 AS AST_DR2,''aS HEALTH_LEVEL,A.RANK_CODE AS OP_LEVEL &
		        //=========================add by wanglong 20121206
	                ,A.PART_CODE, A.ASA_CODE, A.NNIS_CODE, A.ISO_FLG, A.OP_END_DATE &
		        //=========================
			FROM OPE_OPDETAIL A,SYS_OPERATIONICD B &
			WHERE A.OP_CODE1=B.OPERATION_ICD &
			ORDER BY A.OP_DATE DESC
intoOPEDataForMRO.item=CASE_NO	
intoOPEDataForMRO.CASE_NO=A.CASE_NO=<CASE_NO>
intoOPEDataForMRO.Debug=N

//结构化手术记录要带入的数据
selectForEmr.Type=TSQL
//========modify by wanglong 20130515
selectForEmr.SQL=WITH OPE_ANAMETHOD AS (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_ANAMETHOD'), &
                      ANA_ASA AS (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'ANA_ASA'), &
                      OPE_SITE AS (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_SITE'), &
		      OPE_TYPE AS (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_TYPE') &
               SELECT A.MR_NO, B.DEPT_CHN_DESC AS DEPT_DESC, C.STATION_DESC, TO_CHAR( A.OP_DATE, 'yyyy/mm/dd hh24:mi:ss') AS OP_DATE, &
	              TO_CHAR( A.OP_START_DATE, 'yyyy/mm/dd hh24:mi:ss') AS OP_START_DATE, TO_CHAR( A.OP_END_DATE, 'yyyy/mm/dd hh24:mi:ss') AS OP_END_DATE, &
		      D.ICD_CHN_DESC AS ICD_DESC, E.OPT_CHN_DESC AS OPT_DESC, F.USER_NAME AS SURGEON_USER, G.USER_NAME AS AST_USER1, &
		      H.USER_NAME AS AST_USER2, I.USER_NAME AS AST_USER3, J.USER_NAME AS AST_USER4, K.USER_NAME AS ANA_USER, L.CHN_DESC AS ANA_DESC, &
		      M.CHN_DESC AS ASA_DESC, N.CHN_DESC AS PART_DESC, O.CHN_DESC AS TYPE_DESC &
                 FROM OPE_OPDETAIL A, SYS_DEPT B, SYS_STATION C, SYS_DIAGNOSIS D, SYS_OPERATIONICD E, SYS_OPERATOR F, SYS_OPERATOR G, &
		      SYS_OPERATOR H, SYS_OPERATOR I, SYS_OPERATOR J, SYS_OPERATOR K, OPE_ANAMETHOD L, ANA_ASA M, OPE_SITE N, OPE_TYPE O &
                WHERE A.OP_DEPT_CODE = B.DEPT_CODE(+) &
                  AND A.OP_STATION_CODE = C.STATION_CODE(+) &
                  AND A.DIAG_CODE1 = D.ICD_CODE(+) &
                  AND A.OP_CODE1 = E.OPERATION_ICD(+) &
                  AND A.MAIN_SURGEON = F.USER_ID(+) &
                  AND A.REAL_AST1 = G.USER_ID(+) &
                  AND A.REAL_AST2 = H.USER_ID(+) &
                  AND A.REAL_AST3 = I.USER_ID(+) &
                  AND A.REAL_AST4 = J.USER_ID(+) &
                  AND A.ANA_USER1 = K.USER_ID(+) &
                  AND A.ANA_CODE = L.ID(+) &
                  AND A.ASA_CODE = M.ID(+) &
                  AND A.PART_CODE = N.ID(+) &
                  AND A.TYPE_CODE = O.ID(+) &
                  AND A.OP_RECORD_NO = <OP_RECORD_NO>
selectForEmr.Debug=N

//根据手术申请单号查询手术信息 add by wanglong 20130608
selectForEmrByBookNo.Type=TSQL
selectForEmrByBookNo.SQL=SELECT * FROM OPE_OPBOOK A, OPE_OPDETAIL B &
                                 WHERE A.OPBOOK_SEQ = B.OPBOOK_NO &
                                   AND A.OPBOOK_SEQ = <OP_BOOK_NO>
selectForEmrByBookNo.Debug=N


//==add by wukai on 20170210 根据病案号 获取手术时间 体温单中使用
selectDataForSUM.Type=TSQL
selectDataForSUM.SQL=SELECT OP_DATE, ROOM_NO, URGBLADE_FLG, CASE_NO FROM OPE_OPDETAIL &
			WHERE 1=1 AND OP_DATE IS NOT NULL ORDER BY OP_DATE DESC
selectDataForSUM.item=ADM_TYPE;CASE_NO
selectDataForSUM.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectDataForSUM.CASE_NO=CASE_NO=<CASE_NO>
selectDataForSUM.Debug=Y
