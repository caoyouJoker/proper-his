   #
   # Title:�������ϸ��
   #
   # Description:���������
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04
 
Module.item=createDispenseDD;queryDispenseDD;deleteDispenseDDOut

 
//�½����ⵥ��ϸ
createDispenseDD.Type=TSQL 
createDispenseDD.SQL=INSERT INTO INV_DISPENSEDD(DISPENSE_NO,SEQ_NO,DDSEQ_NO,RFID,OPT_USER , OPT_DATE , OPT_TERM, &
      BATCH_NO,VALID_DATE,BATCH_SEQ,UNIT_PRICE,STOCK_UNIT) &
	    VALUES(<DISPENSE_NO>, <SEQ_NO> , <DDSEQ_NO> , <RFID>, <OPT_USER> , <OPT_DATE> , <OPT_TERM> &
	    ,<BATCH_NO>,<VALID_DATE>,<BATCH_SEQ>,<UNIT_PRICE>,<STOCK_UNIT> ) 
createDispenseDD.Debug=N     

   
    
//��ѯ��Ҫ��������ϸ��DD��         
queryDispenseDD.Type=TSQL 
queryDispenseDD.SQL=SELECT  'Y' AS SELECT_FLG,A.DISPENSE_NO,A.SEQ_NO,B.INV_CODE,A.INVSEQ_NO, &  
                            B.DISPENSE_UNIT,B.COST_PRICE,A.RFID,B.INV_CHN_DESC,B.DESCRIPTION  &
                         FROM  INV_DISPENSEDD A,INV_BASE B,INV_DISPENSEM C &
			 WHERE A.INV_CODE = B.INV_CODE   &
                           AND A.DISPENSE_NO = C.DISPENSE_NO  
queryDispenseDD.ITEM=DISPENSE_NO;IO_FLG;FINA_FLG   
queryDispenseDD.DISPENSE_NO=A.DISPENSE_NO=<DISPENSE_NO>  
queryDispenseDD.Debug=N   


//ɾ�����ⵥ����;��
deleteDispenseDDOut.Type=TSQL
deleteDispenseDDOut.SQL=DELETE * FROM INV_DISPENSEM  &    
		    WHERE DISPENSE_NO=<DISPENSE_NO>    
deleteDispenseDDOut.Debug=N  
   


	



       
       











