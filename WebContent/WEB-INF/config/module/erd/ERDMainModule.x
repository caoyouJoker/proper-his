Module.item=selPat;selPatInERD;selBed;updateErdBed;&
            insertErdRecord;updateAdmStauts;updateErdRecord;selOrderExec;updateExec;&
            selERDRegionBedByPat;selERDPatInfo;updateOPDOrderBarCode;updateErdRecordBed;&
            updateErdBed2;updateErdEvalution;insertAmiESRecord;updateAmiESRecord;selERDRegionBedByPat2;&
            updateErdEvalutionOutDate

// add by wangqing 20170627
updateErdEvalutionOutDate.Type=TSQL
updateErdEvalutionOutDate.SQL=UPDATE ERD_EVALUTION &
		      SET OUT_DATE = <OUT_DATE> &
		    WHERE CASE_NO = <CASE_NO>
updateErdEvalutionOutDate.Debug=Y


//更新病人状态 REG_PATADM
updateAdmStauts.Type=TSQL
updateAdmStauts.SQL=UPDATE REG_PATADM SET ADM_STATUS = <ADM_STATUS>, &
                                          OPT_USER = <OPT_USER>, &
                                          OPT_DATE = SYSDATE, &
                                          OPT_TERM = <OPT_TERM> &
                           WHERE CASE_NO = <CASE_NO>
updateAdmStauts.Debug=N

//查询病患
selPat.Type=TSQL
selPat.SQL=SELECT A.REG_DATE, A.REG_DATE ADM_DATE, A.CASE_NO, A.MR_NO, B.PAT_NAME, A.TRIAGE_NO, &
                  A.DEPT_CODE, C.ERD_REGION_CODE, A.ERD_LEVEL, C.BED_NO, C.BED_DESC &
             FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C, ERD_RECORD D &
            WHERE A.CASE_NO = C.CASE_NO(+) &
              AND A.CASE_NO = D.CASE_NO(+) &
              AND A.MR_NO = B.MR_NO &
              AND A.ADM_TYPE = 'E' &
              AND A.REGCAN_USER IS NULL &
         ORDER BY A.REG_DATE DESC
selPat.item=ADM_STATUS;REG_DATE;CASE_NO;MR_NO;ERD_REGION;START_DATE
selPat.ADM_STATUS=A.ADM_STATUS=<ADM_STATUS>
selPat.REG_DATE=A.REG_DATE=<REG_DATE>
selPat.CASE_NO=A.CASE_NO=<CASE_NO>
selPat.MR_NO=A.MR_NO=<MR_NO>
selPat.ERD_REGION=C.ERD_REGION_CODE=<ERD_REGION>
selPat.START_DATE=D.OUT_DATE BETWEEN <START_DATE> AND <END_DATE>
selPat.Debug=N

//查询病患
selPatInERD.Type=TSQL
selPatInERD.SQL=SELECT A.REG_DATE, A.REG_DATE ADM_DATE, A.CASE_NO, A.MR_NO, B.PAT_NAME, A.TRIAGE_NO, &
                  A.DEPT_CODE, C.ERD_REGION_CODE, A.ERD_LEVEL, C.BED_NO, C.BED_DESC &
             FROM REG_PATADM A, SYS_PATINFO B, ERD_BED C, ERD_RECORD D &
            WHERE A.CASE_NO = C.CASE_NO &
              AND A.CASE_NO = D.CASE_NO(+) &
              AND A.MR_NO = B.MR_NO &
              AND A.ADM_TYPE = 'E' &
              AND A.REGCAN_USER IS NULL &
         ORDER BY A.REG_DATE DESC
selPatInERD.item=ADM_STATUS;REG_DATE;CASE_NO;MR_NO;ERD_REGION;START_DATE
selPatInERD.ADM_STATUS=A.ADM_STATUS=<ADM_STATUS>
selPatInERD.REG_DATE=A.REG_DATE=<REG_DATE>
selPatInERD.CASE_NO=A.CASE_NO=<CASE_NO>
selPatInERD.MR_NO=A.MR_NO=<MR_NO>
selPatInERD.ERD_REGION=C.ERD_REGION_CODE=<ERD_REGION>
selPatInERD.START_DATE=D.OUT_DATE BETWEEN <START_DATE> AND <END_DATE>
selPatInERD.Debug=N

//查询床位
selBed.Type=TSQL
selBed.SQL=SELECT A.BED_NO, A.BED_DESC, A.CASE_NO, A.MR_NO, B.PAT_NAME, A.ERD_REGION_CODE &
             FROM ERD_BED A, SYS_PATINFO B &
            WHERE A.MR_NO = B.MR_NO(+)
selBed.item=ERD_REGION_CODE;EMPTY_FLG
selBed.ERD_REGION_CODE=ERD_REGION_CODE=<ERD_REGION_CODE>
selBed.EMPTY_FLG=CASE_NO IS NULL
selBed.Debug=N

//急诊更新ERD_BED
updateErdBed.Type=TSQL
updateErdBed.SQL=UPDATE ERD_BED SET CASE_NO=<CASE_NO>, &
                                    MR_NO=<MR_NO>, &
                                    OCCUPY_FLG=<OCCUPY_FLG>, &
                                    OPT_USER=<OPT_USER>, &
                                    OPT_DATE=SYSDATE, &
                                    OPT_TERM=<OPT_TERM> &
                  WHERE ERD_REGION_CODE = <ERD_REGION_CODE> &
                    AND BED_NO = <BED_NO>
updateErdBed.Debug=N

// add by wangqing 20170626
//急诊更新ERD_BED
updateErdBed2.Type=TSQL
updateErdBed2.SQL=UPDATE ERD_BED SET CASE_NO=<CASE_NO>, &
                                    MR_NO=<MR_NO>, &
                                    TRIAGE_NO=<TRIAGE_NO>, &
                                    OCCUPY_FLG=<OCCUPY_FLG>, &
                                    OPT_USER=<OPT_USER>, &
                                    OPT_DATE=SYSDATE, &
                                    OPT_TERM=<OPT_TERM> &
                  WHERE ERD_REGION_CODE = <ERD_REGION_CODE> &
                    AND BED_NO = <BED_NO>
updateErdBed2.Debug=Y

// add by wangqing 20170626
//急诊更新ERD_EVALUTION
updateErdEvalution.Type=TSQL
updateErdEvalution.SQL=UPDATE ERD_EVALUTION SET BED_NO = <BED_NO>, &
                                    OPT_USER=<OPT_USER>, &
                                    OPT_DATE=SYSDATE, &
                                    OPT_TERM=<OPT_TERM> &
                    WHERE TRIAGE_NO = <TRIAGE_NO>
updateErdEvalution.Debug=Y

// add by wangqing 20170626
// 插入AMI_E_S_RECORD
insertAmiESRecord.Type=TSQL
insertAmiESRecord.SQL=INSERT INTO AMI_E_S_RECORD (TRIAGE_NO,BED_NO,S_M_TIME) VALUES(<TRIAGE_NO>,<BED_NO>,SYSDATE)
insertAmiESRecord.Debug=Y

// add by wangqing 20170626
// 更新AMI_E_S_RECORD
updateAmiESRecord.Type=TSQL
updateAmiESRecord.SQL=UPDATE AMI_E_S_RECORD SET E_M_TIME=SYSDATE WHERE TRIAGE_NO=<TRIAGE_NO> AND BED_NO=<BED_NO> AND E_M_TIME IS NULL
updateAmiESRecord.Debug=Y




//急诊病案首页更新BED_NO
//wanglong add 20150528
updateErdRecordBed.Type=TSQL
updateErdRecordBed.SQL=UPDATE ERD_RECORD SET BED_NO = <BED_NO>, &
                                             ERD_REGION = <ERD_REGION>, &
                                             OPT_USER = <OPT_USER>, &
                                             OPT_DATE = SYSDATE, &
                                             OPT_TERM = <OPT_TERM> &
                        WHERE CASE_NO = <CASE_NO> 
updateErdRecordBed.Debug=N

//更新急诊病案首页
updateErdRecord.Type=TSQL
updateErdRecord.SQL=UPDATE ERD_RECORD SET PAT_NAME=<PAT_NAME>, SEX=<SEX>, BIRTH_DATE=<BIRTH_DATE>, AGE=<AGE>, MARRIGE=<MARRIGE>, &
                                          OCCUPATION=<OCCUPATION>, RESID_PROVICE=<RESID_PROVICE>, RESID_COUNTRY=<RESID_COUNTRY>, &
                                          FOLK=<FOLK>, NATION=<NATION>, IDNO=<IDNO>, CTZ1_CODE=<CTZ1_CODE>, OFFICE=<OFFICE>, &
                                          O_ADDRESS=<O_ADDRESS>, O_TEL=<O_TEL>, O_POSTNO=<O_POSTNO>, H_ADDRESS=<H_ADDRESS>, &
                                          H_POSTNO=<H_POSTNO>, CONTACTER=<CONTACTER>, RELATIONSHIP=<RELATIONSHIP>, &
                                          CONT_ADDRESS=<CONT_ADDRESS>, CONT_TEL=<CONT_TEL>, IN_DATE=<IN_DATE>, IN_DEPT=<IN_DEPT>, &
                                          ERD_REGION=<ERD_REGION>, OUT_DATE=<OUT_DATE>, OUT_DEPT=<OUT_DEPT>, OUT_ERD_REGION=<OUT_ERD_REGION>, &
                                          REAL_STAY_DAYS=<REAL_STAY_DAYS>, OUT_DIAG_CODE=<OUT_DIAG_CODE>, CODE_REMARK=<CODE_REMARK>, &
                                          CODE_STATUS=<CODE_STATUS>, GET_TIMES=<GET_TIMES>, SUCCESS_TIMES=<SUCCESS_TIMES>, &
                                          DR_CODE=<DR_CODE>, OP_CODE=<OP_CODE>, OP_DATE=<OP_DATE>, MAIN_SUGEON=<MAIN_SUGEON>, &
                                          OP_LEVEL=<OP_LEVEL>, HEAL_LV=<HEAL_LV>, ACCOMPANY_WEEK=<ACCOMPANY_WEEK>, &
                                          ACCOMPANY_MONTH=<ACCOMPANY_MONTH>, ACCOMPANY_YEAR=<ACCOMPANY_YEAR>, ACCOMP_DATE=<ACCOMP_DATE>, &
                                          STATUS=<STATUS>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>, DISCHG_TYPE=<DISCHG_TYPE>, &
                                          DISCHG_DATE=<DISCHG_DATE>, TRAN_HOSP=<TRAN_HOSP>, IPD_IN_DEPT=<IPD_IN_DEPT>, &
                                          IPD_IN_DATE=<IPD_IN_DATE>, RETURN_DATE=<RETURN_DATE> &
					  //wanglong add 20150528
					  , BED_NO=<BED_NO> &
                                    WHERE CASE_NO=<CASE_NO>
updateErdRecord.Debug=N

//急诊病案首页插入ERD_RECORD
insertErdRecord.Type=TSQL
insertErdRecord.SQL=INSERT INTO ERD_RECORD (CASE_NO,MR_NO,ERD_NO,PAT_NAME,SEX,BIRTH_DATE,AGE,MARRIGE,&
                                            OCCUPATION,RESID_PROVICE,RESID_COUNTRY,FOLK,NATION,IDNO,&
                                            CTZ1_CODE,OFFICE,O_ADDRESS,O_TEL,O_POSTNO,H_ADDRESS,H_POSTNO,&
                                            CONTACTER,RELATIONSHIP,CONT_ADDRESS,CONT_TEL,IN_DATE,IN_DEPT,&
                                            ERD_REGION,OUT_DATE,OUT_DEPT,OUT_ERD_REGION,REAL_STAY_DAYS,&
                                            OUT_DIAG_CODE,CODE_REMARK,CODE_STATUS,GET_TIMES,SUCCESS_TIMES,DR_CODE,&
                                            OP_CODE,OP_DATE,MAIN_SUGEON,OP_LEVEL,HEAL_LV,ACCOMPANY_WEEK,ACCOMPANY_MONTH,&
                                            ACCOMPANY_YEAR,ACCOMP_DATE,STATUS,OPT_USER,OPT_DATE,OPT_TERM,&
                                            DISCHG_TYPE,DISCHG_DATE,TRAN_HOSP,IPD_IN_DEPT,IPD_IN_DATE &
					    //wanglong add 20150528
					    ,BED_NO) &
                                    VALUES (<CASE_NO>,<MR_NO>,<ERD_NO>,<PAT_NAME>,<SEX>,<BIRTH_DATE>,<AGE>,<MARRIGE>,&
                                            <OCCUPATION>,<RESID_PROVICE>,<RESID_COUNTRY>,<FOLK>,<NATION>,<IDNO>,&
                                            <CTZ1_CODE>,<OFFICE>,<O_ADDRESS>,<O_TEL>,<O_POSTNO>,<H_ADDRESS>,<H_POSTNO>,&
                                            <CONTACTER>,<RELATIONSHIP>,<CONT_ADDRESS>,<CONT_TEL>,<IN_DATE>,<IN_DEPT>,&
                                            <ERD_REGION>,<OUT_DATE>,<OUT_DEPT>,<OUT_ERD_REGION>,<REAL_STAY_DAYS>,&
                                            <OUT_DIAG_CODE>,<CODE_REMARK>,<CODE_STATUS>,<GET_TIMES>,<SUCCESS_TIMES>,<DR_CODE>,&
                                            <OP_CODE>,<OP_DATE>,<MAIN_SUGEON>,<OP_LEVEL>,<HEAL_LV>,<ACCOMPANY_WEEK>,<ACCOMPANY_MONTH>,&
                                            <ACCOMPANY_YEAR>,<ACCOMP_DATE>,<STATUS>,<OPT_USER>,SYSDATE,<OPT_TERM>,&
                                            <DISCHG_TYPE>,<DISCHG_DATE>,<TRAN_HOSP>,<IPD_IN_DEPT>,<IPD_IN_DATE> &
					    //wanglong add 20150528
					    ,<BED_NO>)
insertErdRecord.Debug=N

//------------------------------------------------护士站-------------------------------------------

//护士站执行查询
selOrderExec.Type=TSQL
selOrderExec.SQL=SELECT '' EXE_FLG,'' PRINT_FLG,B.BED_DESC AS BED_NO, A.MR_NO, C.PAT_NAME, A.LINKMAIN_FLG, A.LINK_NO, &
                        A.ORDER_DESC || CASE WHEN TRIM(GOODS_DESC) IS NOT NULL OR TRIM(GOODS_DESC) <> '' THEN '(' || GOODS_DESC || ')' ELSE '' END &
                                     || CASE WHEN TRIM(SPECIFICATION) IS NOT NULL OR TRIM(SPECIFICATION) <> '' THEN '(' || SPECIFICATION || ')' ELSE '' END &
                        AS ORDER_DESC_AND_SPECIFICATION, A.ORDER_CODE, A.ORDER_DESC, A.ORDER_DESC || SPECIFICATION ORDERDESC, A.MEDI_QTY, A.MEDI_UNIT, &
                        A.FREQ_CODE, A.ROUTE_CODE, A.DR_CODE, A.DR_NOTE, A.ORDER_DATE, A.DC_ORDER_DATE, A.NS_EXEC_CODE, A.NS_EXEC_DATE, &
                        TRUNC(A.NS_EXEC_DATE) NS_EXEC_DATE_DAY, A.NS_EXEC_DATE NS_EXEC_DATE_TIME, A.NS_NOTE, &
                        A.DC_DR_CODE, A.CASE_NO, A.SEQ_NO,  A.RX_NO, A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.DOSE_TYPE, &
                        A.CAT1_TYPE, A.ORDER_CAT1_CODE, D.CLASSIFY_TYPE, A.DOSAGE_QTY, A.DOSAGE_UNIT, A.DISPENSE_QTY, A.DISPENSE_UNIT, &
                        CASE WHEN A.CAT1_TYPE = 'PHA' THEN A.BAR_CODE ELSE A.MED_APPLY_NO END AS BAR_CODE &
                   FROM OPD_ORDER A, ERD_BED B, SYS_PATINFO C, SYS_PHAROUTE D &
                  WHERE A.CASE_NO = B.CASE_NO(+) &
                    AND A.MR_NO = C.MR_NO &
                    AND A.HIDE_FLG <> 'Y' &
                    AND A.ROUTE_CODE = D.ROUTE_CODE(+) &
                    AND A.RX_TYPE NOT IN ('7','6') &
                 ORDER BY A.RX_NO, A.SEQ_NO, A.ORDER_DATE
selOrderExec.item=CASE_NO;EXEC_NO;EXEC_YES;NS_EXEC_DATE;CAT1_TYPEPHA;CAT1_TYPEPL;DOSE_TYPEO;DOSE_TYPEE;DOSE_TYPEI;DOSE_TYPEF;DOSE_TYPEOE;DOSE_TYPEOI;DOSE_TYPEOF;DOSE_TYPEEI;DOSE_TYPEEF;DOSE_TYPEIF;DOSE_TYPEOEI;DOSE_TYPEOEF;DOSE_TYPEOIF;DOSE_TYPEEIF;DOSE_TYPEOEIF
selOrderExec.CASE_NO=A.CASE_NO=<CASE_NO>
selOrderExec.EXEC_NO=A.NS_EXEC_CODE IS NULL
selOrderExec.EXEC_YES=A.NS_EXEC_CODE IS NOT NULL
selOrderExec.NS_EXEC_DATE=A.NS_EXEC_DATE BETWEEN TO_DATE(<fromCheckDate>,'yyyyMMddhh24miss') AND TO_DATE(<toCheckDate>,'yyyyMMddhh24miss')
selOrderExec.CAT1_TYPEPHA=A.CAT1_TYPE = 'PHA'
selOrderExec.CAT1_TYPEPL=A.CAT1_TYPE <> 'PHA'
selOrderExec.DOSE_TYPEO=A.DOSE_TYPE = 'O'
selOrderExec.DOSE_TYPEE=A.DOSE_TYPE = 'E'
selOrderExec.DOSE_TYPEI=A.DOSE_TYPE = 'I'
selOrderExec.DOSE_TYPEF=A.DOSE_TYPE = 'F'
selOrderExec.DOSE_TYPEOE=A.DOSE_TYPE IN('O','E')
selOrderExec.DOSE_TYPEOI=A.DOSE_TYPE IN('O','I')
selOrderExec.DOSE_TYPEOF=A.DOSE_TYPE IN('O','F')
selOrderExec.DOSE_TYPEEI=A.DOSE_TYPE IN('E','I')
selOrderExec.DOSE_TYPEEF=A.DOSE_TYPE IN('E','F')
selOrderExec.DOSE_TYPEIF=A.DOSE_TYPE IN('I','F')
selOrderExec.DOSE_TYPEOEI=A.DOSE_TYPE IN('O','E','I')
selOrderExec.DOSE_TYPEOEF=A.DOSE_TYPE IN('O','E','F')
selOrderExec.DOSE_TYPEOIF=A.DOSE_TYPE IN('O','I','F')
selOrderExec.DOSE_TYPEEIF=A.DOSE_TYPE IN('E','I','F')
selOrderExec.DOSE_TYPEOEIF=A.DOSE_TYPE IN('O','E','I','F')
selOrderExec.Debug=N

//急诊留观护士执行—更新OPD_ORDER
updateExec.Type=TSQL
updateExec.SQL=UPDATE OPD_ORDER SET ORDER_DATE = <ORDER_DATE>, &
                                    NS_EXEC_CODE = <NS_EXEC_CODE>, &
                                    NS_EXEC_DATE = <NS_EXEC_DATE>, &
                                    NS_NOTE = <NS_NOTE>, &
                                    OPT_USER = <OPT_USER>, &
                                    OPT_DATE = SYSDATE, &
                                    OPT_TERM = <OPT_TERM> &
                WHERE CASE_NO = <CASE_NO> &
                  AND RX_NO = <RX_NO> &
                  AND SEQ_NO = <SEQ_NO>
updateExec.Debug=N

//查询床位
selERDRegionBedByPat.Type=TSQL
selERDRegionBedByPat.SQL=SELECT ERD_REGION_CODE,BED_NO FROM ERD_BED WHERE CASE_NO = <CASE_NO>
selERDRegionBedByPat.Debug=N

// add by wangqing 20170626
//查询床位
selERDRegionBedByPat2.Type=TSQL
selERDRegionBedByPat2.SQL=SELECT ERD_REGION_CODE,BED_NO FROM ERD_BED WHERE TRIAGE_NO = <TRIAGE_NO>
selERDRegionBedByPat2.Debug=Y

//查询急诊留观病患基本信息
selERDPatInfo.Type=TSQL
selERDPatInfo.SQL=SELECT A.REAL_STAY_DAYS ADM_DAYS, A.MR_NO, '' IPD_NO, B.ADM_DATE IN_DATE, B.DEPT_CODE IN_DEPT_CODE, &
                         C.ERD_REGION_CODE STATION_CODE, C.BED_NO, C.BED_DESC, D.CHN_DESC ERD_REGION_DESC, A.OUT_DATE &
                    FROM ERD_RECORD A, REG_PATADM B, ERD_BED C, SYS_DICTIONARY D &
                   WHERE A.CASE_NO = <CASE_NO> &
                     AND A.CASE_NO = B.CASE_NO &
                     AND A.BED_NO = C.BED_NO(+) &
                     AND C.ERD_REGION_CODE = D.ID(+) &
                     AND D.GROUP_ID(+) = 'ERD_REGION'
selERDPatInfo.Debug=N

//护士生成BAR_CODE动作--更新OPD_ORDER
//wanglong add 20150413
updateOPDOrderBarCode.Type=TSQL
updateOPDOrderBarCode.SQL=UPDATE OPD_ORDER SET BAR_CODE=<BAR_CODE>,OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> &
	                            WHERE CASE_NO=<CASE_NO> AND RX_NO=<RX_NO> AND SEQ_NO=<SEQ_NO>
updateOPDOrderBarCode.Debug=N