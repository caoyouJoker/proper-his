   #
   # Title: 物资拨补参数设定档
   #
   # Description: 物资拨补参数设定档
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author fux 2013/05/27
 
Module.item=insert;updatesysparm

//新增物资拨补参数设定档
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SYSPARM( &
			ADD_STOCK_FLG,FIXEDAMOUNT_FLG,REUPRICE_FLG,HISTORY_DAYS,DISCHECK_FLG, &
			MM_DAY,HIGH_VALUE_INV, &
			OPT_USER, OPT_DATE, OPT_TERM ) &
		      VALUES( &
	    	        <ADD_STOCK_FLG>, <FIXEDAMOUNT_FLG>, <REUPRICE_FLG>, <HISTORY_DAYS>, <DISCHECK_FLG>, &
			<MM_DAY>, HIGH_VALUE_INV>,&
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N 
                                             
//更新物资拨补参数设定档 
  
updatesysparm.Type=TSQL 
updatesysparm.SQL=UPDATE INV_SYSPARM SET &       
			 FIXEDAMOUNT_FLG=<FIXEDAMOUNT_FLG>, AUTO_FILL_TYPE=<AUTO_FILL_TYPE> WHERE 1=1  
//updatesysparm.SQL=SELECT * FROM INV_SYSPARM  
updatesysparm.Debug=N   
 
  



