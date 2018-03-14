   #
   # Title:物资财务
   #
   # Description:物资财务
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author chenx 2013/07/23

Module.item=getDayStock;insertDDStock;updateInvStockDToZero;insertAccountData;getAgentInvCode;deleteAccountData;updateSpcInvRecordByAccountNo;updateInvDisByAccountNo; &
            insertRequestM;insertDispenseD;insertDispenseM;insertRequestD;queryDeptFee;queryInAllDispense;queryInDetailDispense;queryOutDetailDispense;queryOutDispense;queryInDispense
//日库存交易档
getDayStock.Type=TSQL
getDayStock.SQL=SELECT A.ORG_CODE, A.INV_CODE, A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, &
         	      A.REGION_CODE, A.STOCK_QTY,B.COST_PRICE , &
		      A.LASTDAY_TOLSTOCK_QTY,  A.DAYIN_QTY,  &
		      A.DAYOUT_QTY,  A.DAY_CHECKMODI_QTY,  &
		      A.DAY_VERIFYIN_QTY, A.DAY_VERIFYIN_AMT, A.GIFTIN_QTY, A.DAY_REGRESSGOODS_QTY, &
		      A.DAY_REGRESSGOODS_AMT, A.DAY_REQUESTIN_QTY, &
		      A.DAY_REQUESTOUT_QTY, A.DAY_CHANGEIN_QTY, A.DAY_CHANGEOUT_QTY, &
		      A.DAY_TRANSMITIN_QTY,A.DAY_TRANSMITOUT_QTY,A.DAY_WASTE_QTY ,  &
		      A.DAY_DISPENSE_QTY,A.DAY_REGRESS_QTY ,A.UNIT_PRICE 
    		   FROM INV_STOCKD A, INV_BASE B , INV_STOCKM C &
   	          WHERE A.INV_CODE = B.INV_CODE &
   	          AND A.ORG_CODE = C.ORG_CODE &
   	          AND A.INV_CODE = C.INV_CODE &
     		  AND C.STOCK_FLG = 'N' &
     		  AND B.ACTIVE_FLG = 'Y'  &
     		  AND A.VALID_DATE IS NOT NULL &
		  ORDER BY A.ORG_CODE, A.INV_CODE
getDayStock.Debug=N

//科室费用查询
queryDeptFee.Type=TSQL
queryDeptFee.SQL=SELECT A.EXE_DEPT_CODE ORG_CODE,B.ORG_DESC,SUM(A.AR_AMT) AMT FROM  &
                 SPC_INV_RECORD A,INV_ORG B  WHERE A.BILL_FLG='Y'  &
                 AND  A.EXE_DEPT_CODE = B.ORG_CODE   &
                 AND  A.BILL_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')   &
                 GROUP BY  A.EXE_DEPT_CODE,B.ORG_DESC
queryDeptFee.ITEM=EXE_DEPT_CODE
queryDeptFee.EXE_DEPT_CODE=A.EXE_DEPT_CODE=<EXE_DEPT_CODE>
queryDeptFee.Debug=N
//物资验证入库汇总报表查询-duzhw
queryInAllDispense.Type=TSQL
queryInAllDispense.SQL=SELECT A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,C.UNIT_CHN_DESC, &
                       SUM(A.IN_QTY) AS IN_QTY   &
                  FROM INV_VERIFYIND A,INV_BASE B,SYS_UNIT C  &
                WHERE A.INV_CODE = B.INV_CODE   &
                 AND   B.DISPENSE_UNIT = C.UNIT_CODE   &
                 AND  A.VALID_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS') &
                 GROUP BY A.INV_CODE,A.IN_QTY,B.INV_CHN_DESC,B.DESCRIPTION,C.UNIT_CHN_DESC
queryInAllDispense.Debug=N
//物资入库明细报表查询-duzhw
queryInDetailDispense.Type=TSQL
queryInDetailDispense.SQL=SELECT A.VERIFYIN_NO AS DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION, &
                       A.VALID_DATE, A.IN_QTY AS  QTY  &
                  FROM INV_VERIFYIND A,INV_BASE B   &
                WHERE A.INV_CODE = B.INV_CODE   &
                 AND  A.OPT_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')  
queryInDetailDispense.ITEM=INV_CODE
queryInDetailDispense.INV_CODE=A.INV_CODE=<INV_CODE>
queryInDetailDispense.Debug=N
//物资出库明细报表查询-duzhw
queryOutDetailDispense.Type=TSQL
queryOutDetailDispense.SQL=SELECT A.DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,E.ORG_DESC AS FROM_ORG_DESC,F.ORG_DESC AS TO_ORG_DESC, &
					D.DISPENSE_DATE AS VALID_DATE, A.QTY  &
                  FROM INV_DISPENSED A,INV_BASE B,INV_DISPENSEM D,INV_ORG E,INV_ORG F  &
                WHERE A.INV_CODE = B.INV_CODE   &
                 AND   A.DISPENSE_NO = D.DISPENSE_NO    &
                  AND  D.FROM_ORG_CODE = E.ORG_CODE    &
                  AND D.TO_ORG_CODE = F.ORG_CODE	   &
                 AND   A.IO_FLG ='2'                   &
                 AND  D.DISPENSE_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')  
queryOutDetailDispense.ITEM=INV_CODE
queryOutDetailDispense.INV_CODE=A.INV_CODE=<INV_CODE>
queryOutDetailDispense.Debug=N
//出库单报表IO_FLG = 2
queryOutDispense.Type=TSQL
queryOutDispense.SQL=SELECT A.DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,C.UNIT_CHN_DESC, &
                       A.QTY,A.COST_PRICE,A.QTY*A.COST_PRICE  AMT,E.ORG_DESC   &
                  FROM INV_DISPENSED A,INV_BASE B,SYS_UNIT C,INV_DISPENSEM D,INV_ORG E  &
                WHERE A.INV_CODE = B.INV_CODE   &
                 AND   B.DISPENSE_UNIT = C.UNIT_CODE   &
                 AND   A.DISPENSE_NO = D.DISPENSE_NO    &
                  AND  D.FROM_ORG_CODE = E.ORG_CODE    &
                 AND   A.IO_FLG ='2'                   &
                 AND  D.DISPENSE_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')  
queryOutDispense.ITEM=FROM_ORG_CODE;REQUEST_TYPE
queryOutDispense.FROM_ORG_CODE=D.FROM_ORG_CODE=<FROM_ORG_CODE>
queryOutDispense.REQUEST_TYPE=D.REQUEST_TYPE=<REQUEST_TYPE>
queryOutDispense.Debug=N
//入库单报表IO_FLG = 1
queryInDispense.Type=TSQL
queryInDispense.SQL=SELECT A.DISPENSE_NO,A.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,C.UNIT_CHN_DESC, &
                       A.QTY,A.COST_PRICE,A.QTY*A.COST_PRICE  AMT,E.ORG_DESC   &
                  FROM INV_DISPENSED A,INV_BASE B,SYS_UNIT C,INV_DISPENSEM D,INV_ORG E  &
                WHERE A.INV_CODE = B.INV_CODE   &
                 AND   B.DISPENSE_UNIT = C.UNIT_CODE   &
                 AND   A.DISPENSE_NO = D.DISPENSE_NO    &
                  AND  D.TO_ORG_CODE = E.ORG_CODE    &
                 AND   A.IO_FLG ='1'                   &
                 AND  D.DISPENSE_DATE BETWEEN TO_DATE(<START_DATE>,'YYYYMMDDHH24MISS')  &
                 AND  TO_DATE(<END_DATE>,'YYYYMMDDHH24MISS')  
queryInDispense.ITEM=TO_ORG_CODE;REQUEST_TYPE
queryInDispense.TO_ORG_CODE=D.TO_ORG_CODE=<TO_ORG_CODE>
queryInDispense.REQUEST_TYPE=D.REQUEST_TYPE=<REQUEST_TYPE>
queryInDispense.Debug=N
//数据插入日结表中
insertDDStock.Type=TSQL
insertDDStock.SQL=INSERT  INTO  INV_DDSTOCK  (   &
                          TRANDATE, ORG_CODE, INV_CODE,      &
                          BATCH_SEQ, REGION_CODE, BATCH_NO,  & 
                          VALID_DATE, DD_IN_QTY, DD_OUT_QTY, &
                          DD_CHECKMODI_QTY, DD_STOCK_QTY, DD_STOCK_AMT, LAST_STOCK_QTY,LAST_STOCK_AMT, &
                          DD_VERIFYIN_QTY, DD_VERIFYIN_AMT, DD_GIFTIN_QTY,  &
                          DD_REGRESSGOODS_QTY, DD_REGRESSGOODS_AMT, DD_ASSIGNIN_QTY,  &
                          DD_ASSIGNOUT_QTY, DD_CHANGEIN_QTY, DD_CHANGEOUT_QTY,    &
                          DD_TRANSMITIN_QTY, DD_TRANSMITOUT_QTY, DD_WASTE_QTY,    &
                          DD_DISPENSE_QTY, DD_REGRESS_QTY, DD_STOCK_PRICE,        &
                          DD_NHI_PRICE, DD_OWN_PRICE, DD_RPP_AMT,                 &
                          OPT_USER, OPT_DATE, OPT_TERM  )   &
                  VALUES( &
                          <TRANDATE>, <ORG_CODE>, <INV_CODE>,      &
		          <BATCH_SEQ>, <REGION_CODE>,<BATCH_NO>,  & 
		          <VALID_DATE>, <DD_IN_QTY>, <DD_OUT_QTY>, &
		          <DD_CHECKMODI_QTY>, <DD_STOCK_QTY>, <DD_STOCK_AMT>, &
		          <LAST_STOCK_QTY>,<LAST_STOCK_AMT>, &
		          <DD_VERIFYIN_QTY>, <DD_VERIFYIN_AMT>, <DD_GIFTIN_QTY>,  &
		          <DD_REGRESSGOODS_QTY>, <DD_REGRESSGOODS_AMT>, <DD_ASSIGNIN_QTY>,  &
		          <DD_ASSIGNOUT_QTY>, <DD_CHANGEIN_QTY>, <DD_CHANGEOUT_QTY>,    &
		          <DD_TRANSMITIN_QTY>, <DD_TRANSMITOUT_QTY>, <DD_WASTE_QTY>,    &
		          <DD_DISPENSE_QTY>, <DD_REGRESS_QTY>, <DD_STOCK_PRICE>,        &
		          <DD_NHI_PRICE>, <DD_OWN_PRICE>, <DD_RPP_AMT>,                 &
                          <OPT_USER>, <OPT_DATE>, <OPT_TERM>  )  
insertDDStock.Debug=N                          
 //日结完之后INV_STOCKD 归0 
 updateInvStockDToZero.Type=TSQL
 updateInvStockDToZero.SQL = UPDATE INV_STOCKM  SET                           &
			     LASTDAY_TOLSTOCK_QTY=<LASTDAY_TOLSTOCK_QTY>,     &
			     DAYIN_QTY=0,                                     &
			     DAYOUT_QTY=0,                                    &
			     DAY_CHECKMODI_QTY=0,                             &
			     DAY_VERIFYIN_QTY=0,                              &
			     DAY_VERIFYIN_AMT=0,                              &
			     GIFTIN_QTY=0,                                    &
			     DAY_REGRESSGOODS_QTY=0,                          &
			     DAY_REGRESSGOODS_AMT=0,                          &
			     DAY_REQUESTIN_QTY=0,                             &
			     DAY_REQUESTOUT_QTY=0,                            &
			     DAY_CHANGEIN_QTY=0,                              &
			     DAY_CHANGEOUT_QTY=0,                             &
			     DAY_TRANSMITIN_QTY=0,                            &
			     DAY_TRANSMITOUT_QTY=0,                           &
			     DAY_DISPENSE_QTY=0,                              &
			     DAY_REGRESS_QTY=0,                               &
			     OPT_USER=<OPT_USER>,                             &
			     OPT_DATE=SYSDATE,                                &
			     OPT_TERM=<OPT_TERM>,                             &
			     WHERE ORG_CODE=<ORG_CODE>                        &
			     AND INV_CODE=<INV_CODE>                          &
			     AND BATCH_SEQ=<BATCH_SEQ>
updateInvStockDToZero.Debug=N
   
//查询供应商对应的药品是否为空   
   
getAgentInvCode.Type=TSQL 
getAgentInvCode.SQL=SELECT A.SUP_CODE, A.INV_CODE     &
       	            FROM INV_AGENT A                  &
getAgentInvCode.ITEM=SUP_CODE;INV_CODE
getAgentInvCode.SUP_CODE=A.SUP_CODE=<SUP_CODE>
getAgentInvCode.INV_CODE=A.INV_CODE=<INV_CODE>
getAgentInvCode.Debug=N
   
//删除结算数据inv_account
deleteAccountData.Type=TSQL  
deleteAccountData.SQL=DELETE FROM INV_ACCOUNT WHERE    &
                      ACCOUNT_NO = <ACCOUNT_NO>
deleteAccountData.Debug=N
//取消结算清空结算单号（计入病患）
updateSpcInvRecordByAccountNo.Type=TSQL 
updateSpcInvRecordByAccountNo.SQL=UPDATE SPC_INV_RECORD SET  ACCOUNT_NO=''  WHERE   &
                       ACCOUNT_NO = <ACCOUNT_NO>
updateSpcInvRecordByAccountNo.Debug=N
//取消结算清空结算单号（出库单号）
updateInvDisByAccountNo.Type=TSQL 
updateInvDisByAccountNo.SQL=UPDATE INV_DISPENSEM SET  ACCOUNT_NO=''  WHERE   &
                       ACCOUNT_NO = <ACCOUNT_NO>
updateInvDisByAccountNo.Debug=N
//插入inv_account数据
insertAccountData.Type=TSQL 
insertAccountData.SQL=INSERT  INTO  INV_ACCOUNT (                                     &
                              REGION_CODE, ACCOUNT_NO, CLOSE_DATE,                    &
			      ORG_CODE, INV_CODE, SUP_CODE,                           &
			      TOTAL_OUT_QTY, TOTAL_UNIT_CODE, VERIFYIN_PRICE,         &
			       VERIFYIN_AMT, OPT_USER, OPT_DATE, OPT_TERM  )          &
	               VALUES(                                                        &
	                      <REGION_CODE>, <ACCOUNT_NO>, <CLOSE_DATE>,              &
		       	      <ORG_CODE>, <INV_CODE>, <SUP_CODE>,                     &
		              <TOTAL_OUT_QTY>, <TOTAL_UNIT_CODE>, <VERIFYIN_PRICE>,   &
			      <VERIFYIN_AMT>, <OPT_USER>, SYSDATE, <OPT_TERM>  )    
insertAccountData.Debug=N
//插入请领主表数据
insertRequestM.Type=TSQL 
insertRequestM.SQL=INSERT  INTO  INV_REQUESTM (                                    &
                              REQUEST_NO,  REQUEST_TYPE,  REQUEST_DATE,            &
			      FROM_ORG_CODE,  TO_ORG_CODE,  REN_CODE,              &
			        URGENT_FLG,  REMARK,  FINAL_FLG,                   &
                              OPT_USER,  OPT_DATE,  OPT_TERM  ）                   &
	               VALUES(                                                     &
	                      <REQUEST_NO>,  <REQUEST_TYPE>,  SYSDATE,             &
			      <FROM_ORG_CODE>,  <TO_ORG_CODE>,  <REN_CODE>,        &
			        <URGENT_FLG>,  <REMARK>,  <FINAL_FLG>,             &        
                              <OPT_USER>,  SYSDATE,  <OPT_TERM>    )    
insertRequestM.Debug=N 

//插入请领细表数据
insertRequestD.Type=TSQL 
insertRequestD.SQL=INSERT  INTO  INV_REQUESTD (                                    &
                              REQUEST_NO,  SEQ_NO,  INV_CODE,                      &
			      INVSEQ_NO,  QTY,  ACTUAL_QTY,                        &
			        BATCH_SEQ,  BATCH_NO,  VALID_DATE,                 &
                            FINA_TYPE,  OPT_USER,  OPT_DATE,  OPT_TERM  ）         &
	               VALUES(                                                     &
	                      <REQUEST_NO>,  <SEQ_NO>,  <INV_CODE>,                &
			      <INVSEQ_NO>,  <QTY>,  <ACTUAL_QTY>,                  &
			        <BATCH_SEQ>,  <BATCH_NO>,  <VALID_DATE>,           &        
                           <FINA_TYPE>,   <OPT_USER>,  SYSDATE,  <OPT_TERM> )    
insertRequestD.Debug=N 

//插入出入库主表数据
insertDispenseM.Type=TSQL 
insertDispenseM.SQL=INSERT  INTO  INV_DISPENSEM (                                   &
                             DISPENSE_NO,  REQUEST_TYPE,  REQUEST_NO,               &
                            REQUEST_DATE,  FROM_ORG_CODE,  TO_ORG_CODE,             &
                           DISPENSE_DATE,  DISPENSE_USER,  URGENT_FLG,              &
                           REMARK,  DISPOSAL_FLG,  CHECK_DATE,                      &
                             CHECK_USER,  REN_CODE,  FINA_FLG,                      &
                              OPT_USER,  OPT_DATE,  OPT_TERM ）                     &
	               VALUES(                                                      &
	                      <DISPENSE_NO>,  <REQUEST_TYPE>,  <REQUEST_NO>,        &
			      SYSDATE,  <FROM_ORG_CODE>,  <TO_ORG_CODE>,            &
			      SYSDATE,  <DISPENSE_USER>,  <URGENT_FLG>,             &  
			       <REMARK>,  <DISPOSAL_FLG>,  SYSDATE,                 &
                             <CHECK_USER>,  <REN_CODE>,  <FINA_FLG>,                &
                             <OPT_USER>,  SYSDATE,  <OPT_TERM> )    
insertDispenseM.Debug=N 

//插入出入库主表数据
insertDispenseD.Type=TSQL 
insertDispenseD.SQL=INSERT  INTO  INV_DISPENSED	 (                                   &
                             DISPENSE_NO,  SEQ_NO,  BATCH_SEQ,                       &
                            INV_CODE,  INVSEQ_NO,  SEQMAN_FLG,                       &
                           QTY,  DISPENSE_UNIT,  COST_PRICE,                         &
                           REQUEST_SEQ,  BATCH_NO,  VALID_DATE,                      &
                             DISPOSAL_FLG,                                           &
                              OPT_USER,  OPT_DATE,  OPT_TERM ）                      &
	               VALUES(                                                       &
	                      <DISPENSE_NO>,  <SEQ_NO>,  <BATCH_SEQ>,                &
			      <INV_CODE>,  <INVSEQ_NO>,  <SEQMAN_FLG>,               &
			      <QTY>,  <DISPENSE_UNIT>,  <COST_PRICE>,                &  
			       <REQUEST_SEQ>,  <BATCH_NO>,  SYSDATE,                 &
                             <DISPOSAL_FLG>,                                         &
                             <OPT_USER>,  SYSDATE,  <OPT_TERM> )    
insertDispenseD.Debug=N 
   
   
   
   
   
   
   
   
   
   
   
   
   
   