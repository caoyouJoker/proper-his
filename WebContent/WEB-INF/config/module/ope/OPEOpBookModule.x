####################################################
#  Title:手术申请module
# 
#  Description:手术申请module
# 
#  Copyright: Copyright (c) Javahis 2008  
# 
#  author zhangk 2009.9.24
#  version 4.0
####################################################
Module.item=insertOpBook;selectOpBook;updateOpBook;updateOpBookForPersonnel;cancelOpBook;updateOPEState;insertOpeCheck;selectOpBookForSum

//插入手术申请
//=============pangben modify 20110630 区域添加      全部加入20140626 加入入录血管 GDVAS_CODE
//==add by huangtt 20161213 DAY_OPE_FLG日间手术
insertOpBook.Type=TSQL
insertOpBook.SQL=INSERT INTO OPE_OPBOOK ( &
			OPBOOK_SEQ,ADM_TYPE,MR_NO,IPD_NO,CASE_NO,&
			BED_NO,URGBLADE_FLG,OP_DATE,TF_FLG,TIME_NEED,&
			ROOM_NO,TYPE_CODE,ANA_CODE,OP_DEPT_CODE,OP_STATION_CODE,&
			DIAG_CODE1,DIAG_CODE2,DIAG_CODE3,BOOK_DEPT_CODE,OP_CODE1,&
			OP_CODE2,BOOK_DR_CODE,MAIN_SURGEON,BOOK_AST_1,BOOK_AST_2,&
			BOOK_AST_3,BOOK_AST_4,REMARK,STATE,CANCEL_FLG,&
			OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE &
			//=========================add by wanglong 20121206
			,PART_CODE,ISO_FLG,GDVAS_CODE &
			//wanglong add 20141010 增加资助金额
			//liuyalin add 20170307 增加录入途径备注
			,GRANT_AID,DAY_OPE_FLG ,GDVAS_REMARKS &
			) VALUES ( &
			<OPBOOK_SEQ>,<ADM_TYPE>,<MR_NO>,<IPD_NO>,<CASE_NO>,&
			<BED_NO>,<URGBLADE_FLG>,TO_DATE(<OP_DATE>,'YYYYMMDDHH24MISS'),<TF_FLG>,<TIME_NEED>,&
			<ROOM_NO>,<TYPE_CODE>,<ANA_CODE>,<OP_DEPT_CODE>,<OP_STATION_CODE>,&
			<DIAG_CODE1>,<DIAG_CODE2>,<DIAG_CODE3>,<BOOK_DEPT_CODE>,<OP_CODE1>,&
			<OP_CODE2>,<BOOK_DR_CODE>,<MAIN_SURGEON>,<BOOK_AST_1>,<BOOK_AST_2>,&
			<BOOK_AST_3>,<BOOK_AST_4>,<REMARK>,<STATE>,'N',&
			<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE> &
			//=========================add by wanglong 20121206
			,<PART_CODE>,<ISO_FLG>,<GDVAS_CODE> &
			//wanglong add 20141010 增加资助金额
			//liuyalin add 20170307 增加录入途径备注
            ,<GRANT_AID>,<DAY_OPE_FLG> ,<GDVAS_REMARKS>&
			)
insertOpBook.Debug=N


//插入手术核查表
insertOpeCheck.Type=TSQL
insertOpeCheck.SQL=INSERT INTO OPE_CHECK (  CHECK_NO,MR_NO,PAT_NAME,SEX,BIRTH_DATE, &
  OPBOOK_SEQ,TYPE_CODE,OPERATION_ICD,OPT_CHN_DESC,ALLERGIC_FLG, &
  READY_FLG,VALID_DATE_FLG,SPECIFICATION_FLG,CHECK_DR_CODE,CHECK_NS_CODE, &
  CHECK_DATE,OPT_USER,OPT_TERM,OPT_DATE) VALUES ( &
			<CHECK_NO>,<MR_NO>,<PAT_NAME>,<SEX>,TO_DATE(<BIRTH_DATE>,'YYYYMMDDHH24MISS'),&
			<OPBOOK_SEQ>,<TYPE_CODE>,<OPERATION_ICD>,<OPT_CHN_DESC>,<ALLERGIC_FLG>,&
			<READY_FLG>,<VALID_DATE_FLG>,<SPECIFICATION_FLG>,<CHECK_DR_CODE>,<CHECK_NS_CODE>,&
			TO_DATE(<CHECK_DATE>,'YYYYMMDDHH24MISS'),<OPT_USER>,<OPT_TERM>,TO_DATE(<OPT_DATE>,'YYYYMMDDHH24MISS')) &
insertOpeCheck.Debug=N

//修改手术
updateOpBook.Type=TSQL
updateOpBook.SQL=UPDATE OPE_OPBOOK SET &
			ADM_TYPE=<ADM_TYPE>,&
			BED_NO=<BED_NO>,&
			URGBLADE_FLG=<URGBLADE_FLG>,&
			OP_DATE=TO_DATE(<OP_DATE>,'YYYYMMDDHH24MISS'),&
			TF_FLG=<TF_FLG>,&
			TIME_NEED=<TIME_NEED>,&
			ROOM_NO=<ROOM_NO>,&
			TYPE_CODE=<TYPE_CODE>,&
			ANA_CODE=<ANA_CODE>,&
			OP_DEPT_CODE=<OP_DEPT_CODE>,&
			OP_STATION_CODE=<OP_STATION_CODE>,&
			DIAG_CODE1=<DIAG_CODE1>,&
			DIAG_CODE2=<DIAG_CODE2>,&
			DIAG_CODE3=<DIAG_CODE3>,&
			BOOK_DEPT_CODE=<BOOK_DEPT_CODE>,&
			OP_CODE1=<OP_CODE1>,&
			OP_CODE2=<OP_CODE2>,&
			BOOK_DR_CODE=<BOOK_DR_CODE>,&
			MAIN_SURGEON=<MAIN_SURGEON>,&
			BOOK_AST_1=<BOOK_AST_1>,&
			BOOK_AST_2=<BOOK_AST_2>,&
			BOOK_AST_3=<BOOK_AST_3>,&
			BOOK_AST_4=<BOOK_AST_4>,&
			REMARK=<REMARK>,&
			OPT_USER=<OPT_USER>,&
			OPT_DATE=SYSDATE,&
			OPT_TERM=<OPT_TERM> &
			//=========================add by wanglong 20121206
			,PART_CODE=<PART_CODE>&
			,ISO_FLG=<ISO_FLG> &
			,GDVAS_CODE = <GDVAS_CODE> &
					//liuyalin add 20170307 增加录入途径备注
			,GDVAS_REMARKS = <GDVAS_REMARKS> &
            //wanglong add 20141010 增加资助金额
            ,GRANT_AID = <GRANT_AID> &
             //====add by huangtt 20161213 日间手术
            //,DAY_OPE_CODE=<DAY_OPE_CODE> &
			WHERE OPBOOK_SEQ=<OPBOOK_SEQ>
updateOpBook.Debug=N

//查询
selectOpBook.Type=TSQL
selectOpBook.SQL=SELECT 'N' SELECT_FLAG, &
		   A.OPBOOK_SEQ, A.ADM_TYPE, A.MR_NO, &
		   A.IPD_NO, A.CASE_NO, A.BED_NO, &
		   A.URGBLADE_FLG, A.OP_DATE, A.TF_FLG, &
		   A.TIME_NEED, A.ROOM_NO, A.TYPE_CODE, &
		   A.ANA_CODE, A.OP_DEPT_CODE, A.OP_STATION_CODE, &
		   A.DIAG_CODE1, A.DIAG_CODE2, A.DIAG_CODE3, &
		   A.BOOK_DEPT_CODE, A.OP_CODE1, A.OP_CODE2, &
		   A.BOOK_DR_CODE, A.MAIN_SURGEON, A.BOOK_AST_1, &
		   A.BOOK_AST_2, A.BOOK_AST_3, A.BOOK_AST_4, &
		   A.CIRCULE_USER1, A.CIRCULE_USER2, A.CIRCULE_USER3, &
		   A.CIRCULE_USER4, A.SCRUB_USER1, A.SCRUB_USER2, &
		   A.SCRUB_USER3, A.SCRUB_USER4, A.ANA_USER1, &
		   A.ANA_USER2, A.EXTRA_USER1, A.EXTRA_USER2, &
		   A.PRE_NO, A.REMARK, A.STATE, &
		   A.APROVE_DATE, A.APROVE_USER, A.OPT_USER, &
		   A.OPT_DATE, A.OPT_TERM,B.PAT_NAME,A.CANCEL_FLG &
           //=========================add by wanglong 20121206
           ,A.PART_CODE,A.ISO_FLG,A.GDVAS_CODE &
             //liuyalin add 20170307 增加录入途径备注
           ,A.GDVAS_REMARKS &
           //wanglong add 20141010 增加资助金额
           ,A.GRANT_AID,A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,A.DAY_OPE_FLG  &    
		FROM OPE_OPBOOK A,SYS_PATINFO B &
		WHERE A.MR_NO=B.MR_NO
selectOpBook.item=OPBOOK_SEQ;OP_DATE_S;OP_DATE_E;ADM_TYPE;MR_NO;IPD_NO;CASE_NO;TYPE_CODE;URGBLADE_FLG;OP_DEPT_CODE;OP_STATION_CODE;BOOK_DR_CODE;ROOM_NO;CANCEL_FLG;REGION_CODE;STATE
selectOpBook.OPBOOK_SEQ=A.OPBOOK_SEQ=<OPBOOK_SEQ>
selectOpBook.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
//==================pangben modify 20110630 添加区域参数
selectOpBook.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectOpBook.OP_DATE_S=A.OP_DATE >= TO_DATE(<OP_DATE_S>,'YYYYMMDDHH24MISS')
selectOpBook.OP_DATE_E=A.OP_DATE <= TO_DATE(<OP_DATE_E>,'YYYYMMDDHH24MISS')
selectOpBook.MR_NO=A.MR_NO=<MR_NO>
selectOpBook.IPD_NO=A.IPD_NO=<IPD_NO>
selectOpBook.CASE_NO=A.CASE_NO=<CASE_NO>
selectOpBook.TYPE_CODE=A.TYPE_CODE=<TYPE_CODE>
selectOpBook.URGBLADE_FLG=A.URGBLADE_FLG=<URGBLADE_FLG>
selectOpBook.OP_DEPT_CODE=A.OP_DEPT_CODE=<OP_DEPT_CODE>
selectOpBook.OP_STATION_CODE=A.OP_STATION_CODE=<OP_STATION_CODE>
selectOpBook.BOOK_DR_CODE=A.BOOK_DR_CODE=<BOOK_DR_CODE>
selectOpBook.ROOM_NO=A.ROOM_NO=<ROOM_NO>
selectOpBook.CANCEL_FLG=A.CANCEL_FLG=<CANCEL_FLG>
//wanglong add 20141010
selectOpBook.STATE=A.STATE=<STATE>
selectOpBook.Debug=N

//修改手术申请的排程部分信息(手术排程)
updateOpBookForPersonnel.Type=TSQL
updateOpBookForPersonnel.SQL=UPDATE OPE_OPBOOK SET &
				OP_DATE=TO_DATE(<OP_DATE>,'YYYYMMDDHH24MISS'),&
				ROOM_NO=<ROOM_NO>,&
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
				STATE=<STATE>,&
				READY_FLG=<READY_FLG>,&
				VALID_DATE_FLG=<VALID_DATE_FLG>,&
				SPECIFICATION_FLG=<SPECIFICATION_FLG>,&
				APROVE_DATE=TO_DATE(<APROVE_DATE>,'YYYYMMDDHH24MISS'),&
				APROVE_USER=<APROVE_USER>,&
				OPT_USER=<OPT_USER>,&
				OPT_DATE=SYSDATE,&
				OPT_TERM=<OPT_TERM> &
				WHERE OPBOOK_SEQ=<OPBOOK_SEQ>
updateOpBookForPersonnel.Debug=N

//取消申请
cancelOpBook.Type=TSQL
cancelOpBook.SQL=UPDATE OPE_OPBOOK SET &
			CANCEL_FLG='Y', &
			CANCEL_DATE=TO_DATE(<CANCEL_DATE>,'YYYYMMDDHH24MISS'),&
			CANCEL_TERM=<CANCEL_TERM>,&
			CANCEL_USER=<CANCEL_USER> &
			WHERE OPBOOK_SEQ=<OPBOOK_SEQ>
cancelOpBook.Debug=N

//修改手术预约状态  0 申请， 1 排程完毕 ，2手术完成
updateOPEState.Type=TSQL
updateOPEState.SQL=UPDATE OPE_OPBOOK SET STATE=<STATE> WHERE OPBOOK_SEQ=<OPBOOK_SEQ>
updateOPEState.Debug=N

//体温单获取手术时间
selectOpBookForSum.Type=TSQL
selectOpBookForSum.SQL=SELECT OP_DATE &
			FROM OPE_OPBOOK  &
			WHERE OP_DATE IS NOT NULL  &
			      AND CANCEL_FLG != 'Y' &
			      AND STATE >= (CASE      &
                                            WHEN TYPE_CODE = '1' THEN '1' &
                                            WHEN TYPE_CODE = '2' THEN '7' &
                                           END) &
			ORDER BY OP_DATE DESC
selectOpBookForSum.item=CASE_NO
//selectOpBookForSum.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectOpBookForSum.CASE_NO=CASE_NO=<CASE_NO>
selectOpBookForSum.Debug=N