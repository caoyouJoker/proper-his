  #
   # Title:请购作业
   #
   # Description:请购作业
   #
   # Copyright: ProperSoft (c) 2013
   #
   # @author fux 2013/06/25 
Module.item=insertM;insertD;updateM;updateD;deleteM;deleteD
 
//新增主表数据
insertM.Type=TSQL
insertM.SQL=INSERT INTO DEV_PURCHASEM(REQUEST_NO,REQUEST_DATE,REQUEST_DEPT,REQUEST_USER,RATEOFPRO_CODE, &
                   USE_DATE,TOT_AMT,FUNDSOU_CODE,PURTYPE_CODE,DEVUSE_CODE, &
                   DETAILED_USE,REQUEST_REASON,BENEFIT_PROVE,REMARK, &
                   CHK_USER,CHK_DATE,OPT_USER,OPT_DATE,OPT_TERM) & 
	   VALUES( &   
	        <REQUEST_NO>,TO_DATE(<REQUEST_DATE>,'YYYYMMDD'), <REQUEST_DEPT>, <REQUEST_USER>, <RATEOFPRO_CODE>, &
		TO_DATE(<USE_DATE>,'YYYYMMDD'), <TOT_AMT>, <FUNDSOU_CODE>, <PURTYPE_CODE>,<DEVUSE_CODE>, &
		<DETAILED_USE>, <REQUEST_REASON>, <BENEFIT_PROVE>,<REMARK>, &
		<CHK_USER>,TO_DATE(<CHK_DATE>,'YYYYMMDD'), <OPT_USER>,<OPT_DATE>, <OPT_TERM>)
insertM.Debug=N                   
     


//新增细表数据
insertD.Type=TSQL
insertD.SQL=INSERT INTO DEV_PURCHASED(REQUEST_NO,SEQ_NO,DEV_CHN_DESC, &
      		   SPECIFICATION,UNIT_CODE,DEVPRO_CODE,DEV_CODE,UNIT_PRICE, &  
      		   QTY,SUM_QTY,REMARK,OPT_USER,OPT_DATE,OPT_TERM)  &
	    VALUES( &
	        <REQUEST_NO>, <SEQ_NO>, <DEV_CHN_DESC>, <SPECIFICATION>, <UNIT_CODE>, &
		<DEVPRO_CODE>, <DEV_CODE>, <UNIT_PRICE>, <QTY>, & 
		<SUM_QTY>, <REMARK>,<OPT_USER>,<OPT_DATE>, <OPT_TERM>)  
insertD.Debug=N


  

//更新主表数据
updateM.Type=TSQL 
updateM.SQL=UPDATE DEV_PURCHASEM SET &
				   REQUEST_DATE=TO_DATE(<REQUEST_DATE>,'YYYYMMDD') , &
                                   REQUEST_DEPT=<REQUEST_DEPT> , & 
                                   REQUEST_USER=<REQUEST_USER> , &
                                   RATEOFPRO_CODE=<RATEOFPRO_CODE> , &  
                                   USER_DATE=TO_DATE(<USER_DATE>,'YYYYMMDD') , & 
                                   TOT_AMT=<TOT_AMT> , & 
                                   FUNDSOU_CODE=<FUNDSOU_CODE> , & 
                                   PURTYPE_CODE=<PURTYPE_CODE> , &
                                   DEVUSE_CODE=<DEVUSE_CODE> , &  
                                   DETAILED_USE=<DETAILED_USE> , &
                                   REQUEST_REASON=<REQUEST_REASON> , &  
                                   BENEFIT_PROVE=<BENEFIT_PROVE> , &
                                   REMARK=<REMARK> , &   
                                   CHK_USER=<CHK_USER> , &
				   OPT_USER=<OPT_USER> , &
                                   CHK_DATE=TO_DATE(<CHK_DATE>,'YYYYMMDDHH24MISS') , &
				   OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDDHH24MISS') , &
				   OPT_TERM=<OPT_TERM> &
			     WHERE REQUEST_NO=<REQUEST_NO>   
   
updateM.Debug=N
//更新细表数据
updateD.Type=TSQL
updateD.SQL=UPDATE DEV_PURCHASED SET &
				   REMARK=<REMARK> , &
				   OPT_USER=<OPT_USER> , &
				   OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDDHH24MISS') , &
				   OPT_TERM=<OPT_TERM> &
			     WHERE REQUEST_NO=<REQUEST_NO> 
   
updateD.Debug=N
//删除细表
deleteD.Type=TSQL
deleteD.SQL=DELETE * FROM DEV_PURCHASED WHERE REQUEST_NO=<REQUEST_NO> AND SEQ_NO=<SEQ_NO> 
deleteD.Debug=N
 
//删除主表
deleteM.Type=TSQL
deleteM.SQL=DELETE FROM DEV_PURCHASEM WHERE REQUEST_NO=<REQUEST_NO> 
deleteM.Debug=N






