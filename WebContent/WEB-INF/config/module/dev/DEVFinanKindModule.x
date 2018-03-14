  #
   # Title:盘点管理工具
   #
   # Description:盘点管理工具
   #
   # Copyright: JavaHis (c) 2013
   #
   # @author fux 2013/09/22
Module.item=updateFinan;insertFinan


//保存盘点数据
updateFinan.Type=TSQL
updateFinan.SQL=UPDATE DEV_FINANKIND SET &
				   FINAN_DESC=<FINAN_DESC> , &
                                   DEP_DEADLINE=<DEP_DEADLINE> , &
				   DESCRIPTION=<DESCRIPTION> , & 
				   OPT_USER=<OPT_USER> , &
				   OPT_DATE=<OPT_DATE> , &
				   OPT_TERM=<OPT_TERM> &  
			     WHERE FINAN_KIND=<FINAN_KIND> 
updateFinan.Debug=N
//新增细表数据
insertFinan.Type=TSQL
insertFinan.SQL=INSERT INTO DEV_FINANKIND( &
		FINAN_KIND,FINAN_DESC,DEP_DEADLINE,DESCRIPTION,CLASSIFY, 
                CLASSIFY_DESC,OPT_USER,OPT_DATE,OPT_TERM) &
	   VALUES( &
	        <FINAN_KIND>, <FINAN_DESC>, <DEP_DEADLINE>, <DESCRIPTION>, <CLASSIFY>, &
		<CLASSIFY_DESC>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insertFinan.Debug=N
 





