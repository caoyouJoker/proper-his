###############################
# <p>Title:病案外部接口Tool </p>
#
# <p>Description:病案外部接口Tool </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2009-5-14
# @version 1.0
#
###############################
Module.item=insertPatInfo

//插入住院患者基本信息（住院登记接口）
//=============pangben modify 20110617 添加区域参数
insertPatInfo.Type=TSQL
insertPatInfo.SQL=INSERT INTO MRO_RECORD ( &
                        MR_NO,IPD_NO,PAT_NAME,IDNO,SEX, &
                        BIRTH_DATE,CASE_NO,MARRIGE,AGE,NATION, &
                        FOLK,CTZ1_CODE,IN_COUNT,H_TEL,HOEMPLACE_CODE &
                        H_ADDRESS,H_POSTNO,OCCUPATION,OFFICE, &
                        O_TEL,O_ADDRESS,O_POSTNO,CONTACTER,RELATIONSHIP, &
                        CONT_TEL,CONT_ADDRESS,ADMCHK_FLG,DIAGCHK_FLG,BILCHK_FLG,QTYCHK_FLG,&
			IN_DATE,IN_DEPT,IN_STATION,IN_ROOM_NO,OE_DIAG_CODE, &
			IN_CONDITION,PG_OWNER,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE &
                        ) &  
                 VALUES ( &
                        <MR_NO>,<IPD_NO>,<PAT_NAME>,<IDNO>,<SEX>, &
                        <BIRTH_DATE>,<CASE_NO>,<MARRIGE>,<AGE>,<NATION>, &
                        <FOLK>,<CTZ1_CODE>,<IN_COUNT>,<H_TEL>,<HOEMPLACE_CODE> &
			<H_ADDRESS>,<H_POSTNO>,<OCCUPATION>,<OFFICE>, &
                        <O_TEL>,<O_ADDRESS>,<O_POSTNO>,<CONTACTER>,<RELATIONSHIP>, &
                        <CONT_TEL>,<CONT_ADDRESS>,'N','N','N','N',&
			<IN_DATE>,<IN_DEPT>,<IN_STATION>,<IN_ROOM_NO>,<OE_DIAG_CODE>, &
			<IN_CONDITION>,<PG_OWNER>,<OPT_USER>,SYSDATE,<OPT_TERM>,<REGION_CODE> &
                        )
insertPatInfo.Debug=N
