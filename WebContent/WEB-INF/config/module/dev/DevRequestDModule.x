   #
   # Title:申请单明细
   #
   # Description:申请单明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author fux 2013/03/08

Module.item=queryRequestD;updateRequestD;createNewRequestD;updateRequestDFlg;updateActualQtyCancel
   
   
//查询申请明细档             
queryRequestD.Type=TSQL     
queryRequestD.SQL=SELECT 'N' AS DEL_FLG,SEQMAN_FLG,DEVPRO_CODE,DEV_CODE, &    
                          DEV_CHN_DESC,SPECIFICATION,QTY,STORGE_QTY,SETDEV_CODE,UNIT_CODE,UNIT_PRICE,TOT_VALUE,FINA_TYPE &
                   FROM  DEV_RequestD &
                   ORDER BY  REQUEST_NO,DEV_CODE   
queryRequestD.item= REQUEST_NO               
queryRequestD.REQUEST_NO=REQUEST_NO = <REQUEST_NO>                      
queryRequestD.Debug=N                  
     

  //更新申请明细档(库存，急件标注)
updateRequestD.Type=TSQL  
updateRequestD.SQL=UPDATE DEV_RequestD SET & 
			   QTY=<QTY>, &       
			   UPDATE_FLG=<UPDATE_FLG>, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &   
			   OPT_TERM=<OPT_TERM> &
		     WHERE REQUEST_NO=<REQUEST_NO> 
updateRequestD.Debug=N
   


       

  //插入申请明细档     
createNewRequestD.Type=TSQL  
createNewRequestD.SQL=INSERT INTO DEV_REQUESTD( &
			REQUEST_NO, SEQMAN_FLG,DEVPRO_CODE,DEV_CODE, DEV_CHN_DESC, &
			SPECIFICATION,QTY, STORGE_QTY, SETDEV_CODE,UNIT_CODE, &
			UNIT_PRICE,TOT_VALUE,UPDATE_FLG, &
                        OPT_USER, OPT_DATE, OPT_TERM,FINA_TYPE) &     
	    	   VALUES( &    
	    	   	<REQUEST_NO>, <SEQMAN_FLG>,<DEVPRO_CODE>,<DEV_CODE>,<DEV_CHN_DESC>, &
			<SPECIFICATION>,<QTY>, <STORGE_QTY>, <SETDEV_CODE>, <UNIT_CODE>, &
                        <UNIT_PRICE>,<TOT_VALUE>,<UPDATE_FLG>, &             
		        <OPT_USER>, <OPT_DATE>, <OPT_TERM>,<FINA_TYPE>)      
createNewRequestD.Debug=N                
  

  //更新申请单状态    
  updateRequestDFlg.Type=TSQL
  updateRequestDFlg.SQL=UPDATE DEV_RequestD SET &
			   UPDATE_FLG=<UPDATE_FLG>, & 
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
		     WHERE REQUEST_NO=<REQUEST_NO> 
  updateRequestDFlg.Debug=N

  //取消出库-更新申请D表 
  updateActualQtyCancel.Type=TSQL
  updateActualQtyCancel.SQL=UPDATE DEV_REQUESTD SET &  
			   STORGE_QTY=0, &
			   UPDATE_FLG=0, &
			   OPT_USER=<OPT_USER>, &
			   OPT_DATE=<OPT_DATE>, &
			   OPT_TERM=<OPT_TERM> &
 		     WHERE REQUEST_NO=<REQUEST_NO>
  updateActualQtyCancel.Debug=N

