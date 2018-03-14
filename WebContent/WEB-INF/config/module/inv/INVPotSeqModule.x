   #
   # Title: ¹øºÅÉèÖÃ
   #
   # Description: ¹øºÅÉèÖÃ
   #
   # Copyright: JavaHis (c) 2015
   #
   # @author wangjc 2015/03/04

Module.item=insert;update;query;delete
 
//ÐÂÔö¹øºÅ
insert.Type=TSQL
insert.SQL=INSERT INTO INV_POTSEQ (POTSEQ,HL_FLG,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<POTSEQ>, <HL_FLG>, <OPT_USER>, <OPT_DATE>, <OPT_TERM>)
insert.Debug=N


//¸üÐÂ¹øºÅ
update.Type=TSQL
update.SQL=UPDATE INV_POTSEQ SET HL_FLG=<HL_FLG>, OPT_USER=<OPT_USER> , OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	    	          WHERE POTSEQ =<POTSEQ> 
update.Debug=N


//²éÑ¯¹øºÅ
query.Type=TSQL
query.SQL=SELECT A.POTSEQ,A.HL_FLG,B.USER_NAME,A.OPT_DATE,A.OPT_TERM FROM INV_POTSEQ A,SYS_OPERATOR B WHERE A.OPT_USER = B.USER_ID &
				ORDER BY A.POTSEQ
query.ITEM=POTSEQ;HL_FLG
query.POTSEQ=A.POTSEQ=<POTSEQ>
query.HL_FLG=A.HL_FLG=<HL_FLG>
query.Debug=N


//É¾³ý¹øºÅ
delete.Type=TSQL
delete.SQL=DELETE FROM INV_POTSEQ WHERE POTSEQ =<POTSEQ> 
delete.Debug=N








