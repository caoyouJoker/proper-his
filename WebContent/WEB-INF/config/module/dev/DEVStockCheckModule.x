  #
   # Title:盘点管理
   #
   # Description:盘点管理
   #
   # Copyright: JavaHis (c) 2011
   #
   # @author shibl 2013/04/17 
Module.item=query;Insert;update

//根据条件查询盘点记录
query.Type=TSQL
query.SQL=SELECT REGION_CODE, DEPT_CODE, DEV_CODE, BATCH_SEQ, CHECK_TYPE, STOCK_QTY, &
       		 CHECK_OPT_CODE, ACTUAL_CHECKQTY_DATE, ACTUAL_CHECK_QTY, CHECK_PHASE_QTY, CHECK_PHASE_AMT, &
       		 ACTUAL_CHECKQTY_USER, MODI_QTY, MODI_AMT, MODIQTY_OPT_CODE, MODI_DATE, &
       		 UNFREEZE_DATE, UNFREEZE_USER, &
       		 OPT_USER, OPT_DATE, OPT_TERM &
       	    FROM DEV_QTYCHECK &  
       	    ORDER BY DEV_CODE
query.ITEM=DEPT_CODE;REGION_CODE;DEV_CODE;BATCH_SEQ;ACTUAL_CHECKQTY_DATE
query.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
query.REGION_CODE=REGION_CODE=<REGION_CODE>
query.DEV_CODE=DEV_CODE=<DEV_CODE>
query.BATCH_SEQ=BATCH_SEQ=<BATCH_SEQ>
query.ACTUAL_CHECKQTY_DATE=ACTUAL_CHECKQTY_DATE=TO_DATE(<ACTUAL_CHECKQTY_DATE>,'YYYYMMDDHH24MISS')
query.Debug=Y  


//新增盘点
Insert.Type=TSQL
Insert.SQL=INSERT INTO DEV_QTYCHECK (REGION_CODE,DEPT_CODE,DEV_CODE,BATCH_SEQ,CHECK_TYPE,STOCK_QTY,CHECK_OPT_CODE,ACTUAL_CHECKQTY_DATE,ACTUAL_CHECK_QTY,CHECK_PHASE_QTY,CHECK_PHASE_AMT,ACTUAL_CHECKQTY_USER,MODI_QTY,&
					 MODI_AMT,MODIQTY_OPT_CODE,MODI_DATE,&
					 OPT_USER,OPT_DATE,&
					 OPT_TERM)&  
		    		 VALUES (<REGION_CODE>,<DEPT_CODE>,<DEV_CODE>,<BATCH_SEQ>,<CHECK_TYPE>,<STOCK_QTY>,<CHECK_OPT_CODE>,TO_DATE                                 (<ACTUAL_CHECKQTY_DATE>,'YYYYMMDDHH24MISS'),<ACTUAL_CHECK_QTY>,&
		    	          <CHECK_PHASE_QTY>,<CHECK_PHASE_AMT>,<ACTUAL_CHECKQTY_USER>,<MODI_QTY>,<MODI_AMT>,<MODIQTY_OPT_CODE>,TO_DATE                                 (<MODI_DATE>,'YYYYMMDDHH24MISS'),&
		    	          <OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDDHH24MISS'),<OPT_TERM>)  
Insert.Debug=Y
  
//更新盘点数据
update.Type=TSQL
update.SQL=UPDATE DEV_QTYCHECK SET &
				   ACTUAL_CHECKQTY_USER=<ACTUAL_CHECKQTY_USER> , &
				   ACTUAL_CHECK_QTY=<ACTUAL_CHECK_QTY> , &
				   CHECK_PHASE_QTY=<CHECK_PHASE_QTY>,&
				   CHECK_PHASE_AMT=<CHECK_PHASE_AMT>,&
				   MODI_QTY=<MODI_QTY>, &
				   MODI_AMT=<MODI_AMT>, &
				   MODIQTY_OPT_CODE=<MODIQTY_OPT_CODE>, &
				   MODI_DATE=TO_DATE(<MODI_DATE>,'YYYYMMDDHH24MISS'), &
				   OPT_USER=<OPT_USER> , &
				   OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDDHH24MISS') , &
				   OPT_TERM=<OPT_TERM> &
			     WHERE DEPT_CODE=<DEPT_CODE> & 
			       AND DEV_CODE=<DEV_CODE> &
			       AND REGION_CODE=<REGION_CODE> &   
			       AND BATCH_SEQ=<BATCH_SEQ> &
			       AND ACTUAL_CHECKQTY_DATE=TO_DATE(<ACTUAL_CHECKQTY_DATE>, 'YYYYMMDDHH24miSS')
update.Debug=Y




