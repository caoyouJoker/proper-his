Module.item=query;update;insert;delete

//����֧����׼������
update.Type=TSQL
update.SQL=UPDATE INS_RULE SET START_RANGE=<START_RANGE>,END_RANGE=<END_RANGE>,OWN_RATE=<OWN_RATE>,AMTPAY_FLG=<AMTPAY_FLG>,ADDPAY_FLG=<ADDPAY_FLG>,LIMIT_PRICE_FLG=<LIMIT_PRICE_FLG>,STA_SEQ=<STA_SEQ>,INS_PAY_PARM=<INS_PAY_PARM>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,STARTLINE_FLG=<STARTLINE_FLG>,PLAN_FLG=<PLAN_FLG>,STARTLINE_PRICE=<STARTLINE_PRICE>,PLAN_PRICE=<PLAN_PRICE>,ALLPRICE_FLG=<ALLPRICE_FLG> WHERE NHI_COMPANY=<NHI_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND START_RANGE=<START_RANGE>
update.Debug=N
//����֧����׼����������
insert.Type=TSQL
insert.SQL=INSERT INTO INS_RULE(NHI_COMPANY,FEE_TYPE,CTZ_CODE,START_RANGE,END_RANGE,OWN_RATE,AMTPAY_FLG,ADDPAY_FLG,LIMIT_PRICE_FLG,STARTLINE_FLG,PLAN_FLG,STA_SEQ,INS_PAY_PARM,OPT_USER,OPT_DATE,OPT_TERM,STARTLINE_PRICE,PLAN_PRICE,ALLPRICE_FLG)VALUES(<NHI_COMPANY>,<FEE_TYPE>,<CTZ_CODE>,<START_RANGE>,<END_RANGE>,<OWN_RATE>,<AMTPAY_FLG>,<ADDPAY_FLG>,<LIMIT_PRICE_FLG>,<STARTLINE_FLG>,<PLAN_FLG>,<STA_SEQ>,<INS_PAY_PARM>,<OPT_USER>,SYSDATE,<OPT_TERM>,<STARTLINE_PRICE>,<PLAN_PRICE>,<ALLPRICE_FLG>)
insert.Debug=N
//ɾ��֧����׼����������
delete.Type=TSQL
delete.SQL=DELETE INS_RULE WHERE NHI_COMPANY=<NHI_COMPANY> AND FEE_TYPE=<FEE_TYPE> AND CTZ_CODE=<CTZ_CODE> AND START_RANGE=<START_RANGE>
delete.Debug=N
//��ѯ֧����׼����������
query.Type=TSQL
query.SQL=SELECT NHI_COMPANY, FEE_TYPE,CTZ_CODE,START_RANGE,END_RANGE,OWN_RATE,AMTPAY_FLG,ADDPAY_FLG,LIMIT_PRICE_FLG,STARTLINE_FLG,PLAN_FLG,STA_SEQ,INS_PAY_PARM,STARTLINE_PRICE,PLAN_PRICE,ALLPRICE_FLG FROM INS_RULE  WHERE NHI_COMPANY LIKE <NHI_COMPANY> AND FEE_TYPE LIKE <FEE_TYPE> AND CTZ_CODE LIKE <CTZ_CODE> ORDER BY STA_SEQ
query.Debug=N