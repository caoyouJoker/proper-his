   #
   # Title:消毒记录
   #
   # Description:消毒记录
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04
   
Module.item=insert;insertValue;deleteValue


//新建出库清单
insert.Type=TSQL
insert.SQL=INSERT INTO INV_DISINFECTION( &
			ORG_CODE , PACK_CODE , PACK_SEQ_NO, DISINFECTION_DATE , QTY ,&
			VALUE_DATE , DISINFECTION_USER , OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<ORG_CODE> ,  <PACK_CODE> , <PACK_SEQ_NO> , <DISINFECTION_DATE> , <QTY> , &
	    	   	<VALUE_DATE> ,  <DISINFECTION_USER> , <OPT_USER> , SYSDATE , <OPT_TERM> )
insert.Debug=N


//插入消毒记录GYSUsed
insertValue.Type=TSQL
insertValue.SQL=INSERT INTO INV_DISINFECTION &
                          (ORG_CODE,PACK_CODE,PACK_SEQ_NO,DISINFECTION_DATE,DISINFECTION_VALID_DATE,RECYCLE_DATE,WASH_DATE,DISINFECTION_USER,&
                           QTY,DISINFECTION_POTSEQ,DISINFECTION_PROGRAM,DISINFECTION_OPERATIONSTAFF,OPT_USER,OPT_DATE,OPT_TERM,PAN_NO,PAN_SEQ,DISINFECTION_NUM)&
                    VALUES (<ORG_CODE>,<PACK_CODE>,<PACK_SEQ_NO>,<DISINFECTION_DATE>,<DISINFECTION_VALID_DATE>,<RECYCLE_DATE>,<WASH_DATE>,<DISINFECTION_USER>,&
                           <QTY>,<DISINFECTION_POTSEQ>,<DISINFECTION_PROGRAM>,<DISINFECTION_OPERATIONSTAFF>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<PAN_NO>,<PAN_SEQ>,<DISINFECTION_NUM>)
insertValue.Debug=N


//取消时删除消毒记录GYSUsed
deleteValue.Type=TSQL
deleteValue.SQL=DELETE INV_DISINFECTION
deleteValue.item=PACK_CODE;PACK_SEQ_NO;DISINFECTION_DATE
deleteValue.PACK_CODE=PACK_CODE=<PACK_CODE>
deleteValue.PACK_SEQ_NO=PACK_SEQ_NO=<PACK_SEQ_NO>
deleteValue.DISINFECTION_DATE=DISINFECTION_DATE=<DISINFECTION_DATE>
deleteValue.Debug=N

       











