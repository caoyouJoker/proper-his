   #
   # Title: ��������������Ӧ +  ������������������
   #
   # Description: ��������������Ӧ +  ������������������ 
   #
   # Copyright: JavaHis (c) 2014
   #
   # @author zhangy 2014/05/04

Module.item=insert;update;delete;insertAutoRequest;updateAutoRequest;deleteAutoRequest


//������������������Ӧ
insert.Type=TSQL
insert.SQL=INSERT INTO OPE_ICDPACKAGE( &
			OPERATION_ICD, OPT_CHN_DESC, PACK_CODE, PACK_DESC, QTY,SEQ_FLG,GDVAS_CODE, &
			OPT_USER, OPT_DATE, OPT_TERM ) &     
		      VALUES( &
	    	        <OPERATION_ICD>, <OPT_CHN_DESC>, <PACK_CODE>, <PACK_DESC>, <QTY>,<SEQ_FLG>,<GDVAS_CODE>, &
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N  
      
//������������������Ӧ(ֻ���޸�qty)  
update.Type=TSQL    
update.SQL=UPDATE OPE_ICDPACKAGE SET &  
			QTY=<QTY>,GDVAS_CODE = <GDVAS_CODE>,  &  
			OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE OPERATION_ICD = <OPERATION_ICD> AND PACK_CODE = <PACK_CODE>
update.Debug=N  

//ɾ����������������Ӧ
delete.Type=TSQL
delete.SQL=DELETE FROM OPE_ICDPACKAGE WHERE OPERATION_ICD = <OPERATION_ICD> AND PACK_CODE = <PACK_CODE>
delete.Debug=N       


//������������������Ӧ
insertAutoRequest.Type=TSQL
insertAutoRequest.SQL=INSERT INTO OPE_PACKAGE( &
			OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,STATE,OP_DATE, &
			MR_NO,PAT_NAME,GDVAS_CODE,REMARK, & 
			OPT_USER, OPT_DATE, OPT_TERM, FINAL_FLG) &                      
		      VALUES( &
	    	        <OPBOOK_SEQ>,<OP_CODE>, <SUPTYPE_CODE>,<STATE>,<OP_DATE>, &
			<MR_NO>,<PAT_NAME>,<GDVAS_CODE>,<REMARK>, &  
			<OPT_USER>, <OPT_DATE>, <OPT_TERM>, <FINAL_FLG>)            
insertAutoRequest.Debug=N                                  
    
//������������������Ӧ(ֻ���޸���ɱ��)     
updateAutoRequest.Type=TSQL  
updateAutoRequest.SQL=UPDATE OPE_PACKAGE SET &  
                        FINAL_FLG = <FINAL_FLG>, &
			OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE>, OPT_TERM=<OPT_TERM> &
		  WHERE OPBOOK_SEQ = <OPBOOK_SEQ> AND OP_CODE = <OP_CODE>
updateAutoRequest.Debug=N     

//ɾ����������������Ӧ
deleteAutoRequest.Type=TSQL
deleteAutoRequest.SQL=DELETE FROM OPE_PACKAGE WHERE OPBOOK_SEQ = <OPBOOK_SEQ> AND OP_CODE = <OP_CODE>
deleteAutoRequest.Debug=N

