   #
   # Title:出入库主档
   #
   # Description:出入库主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04  

Module.item=queryDispenseMOut;queryDispenseMOI;queryDispenseMIn;createDispenseOutM;createDispenseOutInM;queryDispenseMOutDetail;updateFinaFlg;deleteDispenseMOut   
   
  
//查询出库单 
queryDispenseMOut.Type=TSQL
queryDispenseMOut.SQL=SELECT DISPENSE_NO, REQUEST_TYPE, REQUEST_NO, REQUEST_DATE, &
			     FROM_ORG_CODE, TO_ORG_CODE, DISPENSE_DATE, DISPENSE_USER, &
			     URGENT_FLG, REMARK, DISPOSAL_FLG, CHECK_DATE, &
			     CHECK_USER, REN_CODE, FINA_FLG &
      		       FROM INV_DISPENSEM WHERE DISPOSAL_FLG = 'N' AND IO_FLG = '2' &
                             ORDER BY DISPENSE_NO,REQUEST_NO                 
queryDispenseMOut.ITEM=REQUEST_TYPE;TO_ORG_CODE;REQUEST_NO;START_DATE;FINA_FLG
queryDispenseMOut.REQUEST_TYPE=REQUEST_TYPE=<REQUEST_TYPE>
queryDispenseMOut.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryDispenseMOut.REQUEST_NO=REQUEST_NO=<REQUEST_NO>   
queryDispenseMOut.START_DATE=REQUEST_DATE BETWEEN <START_DATE> AND <END_DATE>
queryDispenseMOut.FINA_FLG=FINA_FLG=<FINA_FLG>
queryDispenseMOut.Debug=N
  
//查询需要入库的出库单(等同申请地位,入库要看到出库完成的部分,出库在途状态) 
queryDispenseMOI.Type=TSQL  
queryDispenseMOI.SQL=SELECT  DISPENSE_NO AS DISPENSE_NO_OUT, REQUEST_TYPE, REQUEST_NO, REQUEST_DATE, &
			     FROM_ORG_CODE, TO_ORG_CODE, DISPENSE_DATE, DISPENSE_USER, &
			     URGENT_FLG, REMARK, DISPOSAL_FLG, CHECK_DATE, &
			     CHECK_USER, REN_CODE, FINA_FLG &
      		       FROM INV_DISPENSEM WHERE DISPOSAL_FLG = 'N'  &
                            AND IO_FLG = '2'  &
                            AND FINA_FLG = '0'  &
                            ORDER BY DISPENSE_NO      
queryDispenseMOI.ITEM=REQUEST_TYPE;TO_ORG_CODE;DISPENSE_NO_OUT;START_DATE  
queryDispenseMOI.REQUEST_TYPE=REQUEST_TYPE=<REQUEST_TYPE>
queryDispenseMOI.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE> 
queryDispenseMOI.DISPENSE_NO=DISPENSE_NO=<DISPENSE_NO_OUT>  
queryDispenseMOI.START_DATE=DISPENSE_DATE BETWEEN <START_DATE> AND <END_DATE>
queryDispenseMOI.Debug=N   
    
//查询入库单(等同申请地位,入库要看到出库完成的部分,入库在途(0)和完成(1)状态)  
queryDispenseMIn.Type=TSQL 
queryDispenseMIn.SQL=SELECT  DISPENSE_NO AS DISPENSE_NO_IN, REQUEST_TYPE, REQUEST_NO, REQUEST_DATE, &
			     FROM_ORG_CODE, TO_ORG_CODE, DISPENSE_DATE, DISPENSE_USER, &
			     URGENT_FLG, REMARK, DISPOSAL_FLG, CHECK_DATE, &
			     CHECK_USER, REN_CODE, FINA_FLG &
      		       FROM INV_DISPENSEM  &  
                       WHERE DISPOSAL_FLG = 'N' & 
                       AND IO_FLG = '1' &
                       ORDER BY DISPENSE_NO
queryDispenseMIn.ITEM=REQUEST_TYPE;TO_ORG_CODE;DISPENSE_NO;START_DATE;FINA_FLG
queryDispenseMIn.REQUEST_TYPE=REQUEST_TYPE=<REQUEST_TYPE>
queryDispenseMIn.TO_ORG_CODE=TO_ORG_CODE=<TO_ORG_CODE>
queryDispenseMIn.REQUEST_NO=REQUEST_NO=<REQUEST_NO> 
queryDispenseMIn.START_DATE=CHECK_DATE BETWEEN <START_DATE> AND <END_DATE>
queryDispenseMIn.FINA_FLG=FINA_FLG=<FINA_FLG>
queryDispenseMIn.Debug=N    


//新建出库单(出库在途)
createDispenseOutM.Type=TSQL
createDispenseOutM.SQL=INSERT INTO INV_DISPENSEM( &
			DISPENSE_NO , REQUEST_TYPE , REQUEST_NO, REQUEST_DATE , FROM_ORG_CODE ,&
			TO_ORG_CODE , DISPENSE_DATE , DISPENSE_USER, URGENT_FLG , REMARK ,&
			DISPOSAL_FLG, REN_CODE, FINA_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM,IO_FLG) &
	    	      VALUES( &
	    	   	<DISPENSE_NO>, <REQUEST_TYPE> , <REQUEST_NO> , <REQUEST_DATE> , <FROM_ORG_CODE> , &
	    	   	<TO_ORG_CODE> ,  <DISPENSE_DATE> , <DISPENSE_USER> , <URGENT_FLG> , <REMARK> , &
	    	   	<DISPOSAL_FLG>, <REN_CODE>, <FINA_FLG>, &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> , <IO_FLG>) 
createDispenseOutM.Debug=N 


//新建出库单(出库即入库)
createDispenseOutInM.Type=TSQL
createDispenseOutInM.SQL=INSERT INTO INV_DISPENSEM( &
			DISPENSE_NO , REQUEST_TYPE , REQUEST_NO, REQUEST_DATE , FROM_ORG_CODE ,&
			TO_ORG_CODE , DISPENSE_DATE , DISPENSE_USER, URGENT_FLG , REMARK ,&
			DISPOSAL_FLG, REN_CODE, FINA_FLG, CHECK_DATE, CHECK_USER,  &   
			OPT_USER , OPT_DATE , OPT_TERM, IO_FLG) &
	    	      VALUES( &
	    	   	<DISPENSE_NO>, <REQUEST_TYPE> , <REQUEST_NO> , <REQUEST_DATE> , <FROM_ORG_CODE> , &
	    	   	<TO_ORG_CODE> ,  <DISPENSE_DATE> , <DISPENSE_USER> , <URGENT_FLG> ,  <REMARK> ,  &
	    	   	<DISPOSAL_FLG>, <REN_CODE>, <FINA_FLG>,<CHECK_DATE>, <CHECK_USER>,  &
	    	   	<OPT_USER> , <OPT_DATE> , <OPT_TERM> , <IO_FLG> ) 
createDispenseOutInM.Debug=Y 

//查询出库单细项
queryDispenseMOutDetail.Type=TSQL
queryDispenseMOutDetail.SQL=SELECT B.DISPENSE_NO, B.REQUEST_TYPE, B.REQUEST_NO, B.REQUEST_DATE, &
			     B.FROM_ORG_CODE, B.TO_ORG_CODE, B.DISPENSE_DATE, B.DISPENSE_USER, &
			     B.URGENT_FLG, B.REMARK, B.DISPOSAL_FLG, B.CHECK_DATE, &
			     B.CHECK_USER, B.REN_CODE, B.FINA_FLG, &                         
                             A.SEQ_NO,A.BATCH_SEQ,A.INV_CODE,A.INVSEQ_NO,A.SEQMAN_FLG, &   
                             A.QTY,A.DISPENSE_UNIT,A.COST_PRICE,A.REQUEST_SEQ,    & 
                             A.BATCH_NO,A.VALID_DATE,A.DISPOSAL_FLG,C.VALIDATE_FLG   &
      		       FROM INV_DISPENSED A, INV_DISPENSEM B, INV_BASE C, INV_TRANSUNIT D &
                       WHERE  A.DISPENSE_NO = B.DISPENSE_NO &
			      AND A.INV_CODE = C.INV_CODE & 
			      AND A.INV_CODE = D.INV_CODE &    
			      AND C.INV_CODE = D.INV_CODE &
                              AND B.IO_FLG = A.IO_FLG &
                              AND A.IO_FLG = '2'  &
                              ORDER BY B.DISPENSE_NO 
queryDispenseMOutDetail.ITEM=FROM_ORG_CODE;TO_ORG_CODE;DISPENSE_NO;START_DATE;FINA_FLG
queryDispenseMOutDetail.FROM_ORG_CODE=B.APP_ORG_CODE=<APP_ORG_CODE>
queryDispenseMOutDetail.TO_ORG_CODE=B.TO_ORG_CODE=<TO_ORG_CODE>  
queryDispenseMOutDetail.DISPENSE_NO=B.DISPENSE_NO=<DISPENSE_NO>     
queryDispenseMOutDetail.START_DATE=B.REQUEST_DATE BETWEEN <START_DATE> AND <END_DATE>  
queryDispenseMOutDetail.FINA_FLG=B.FINA_FLG = <FINA_FLG> 
queryDispenseMOutDetail.Debug=N  
       
//出库单主项状态(更新出库单主项状态)   是否还要更新 操作人员，时间？
updateFinaFlg.Type=TSQL
updateFinaFlg.SQL=UPDATE  INV_DISPENSEM SET &    
			  FINA_FLG=<FINA_FLG>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    WHERE DISPENSE_NO=<DISPENSE_NO>   
updateFinaFlg.Debug=N   

 
//删除出库单（在途）
deleteDispenseMOut.Type=TSQL
deleteDispenseMOut.SQL=DELETE * FROM INV_DISPENSEM  &    
		    WHERE DISPENSE_NO=<DISPENSE_NO>    
deleteDispenseMOut.Debug=N        











