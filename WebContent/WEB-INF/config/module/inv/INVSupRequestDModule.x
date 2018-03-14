   #
   # Title:申请明细
   #
   # Description:申请明细
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2009/05/04

Module.item=insert;update;delete;queryInv;queryPack;query;updateFlgAndQtyBySupDispense;deleteAll


//新建申请明细
insert.Type=TSQL
insert.SQL=INSERT INTO INV_SUPREQUESTD( &
			REQUEST_NO , SEQ_NO , PACK_MODE, SUPTYPE_CODE , INV_CODE ,&
			QTY , STOCK_UNIT , ACTUAL_QTY , UPDATE_FLG, &
			OPT_USER , OPT_DATE , OPT_TERM) &
	    	      VALUES( &
	    	   	<REQUEST_NO> ,  <SEQ_NO> , <PACK_MODE> , <SUPTYPE_CODE> , <INV_CODE> , &
	    	   	<QTY> ,  <STOCK_UNIT> , <ACTUAL_QTY> , <UPDATE_FLG>,&
	    	   	<OPT_USER> , SYSDATE , <OPT_TERM> )
insert.Debug=N


//更新申请明细
update.Type=TSQL
update.SQL=UPDATE INV_SUPREQUESTD SET &
			PACK_MODE=<PACK_MODE>, SUPTYPE_CODE=<SUPTYPE_CODE>, &
			INV_CODE=<INV_CODE>, QTY=<QTY>, STOCK_UNIT=<STOCK_UNIT>, &
			ACTUAL_QTY=<ACTUAL_QTY>, UPDATE_FLG=<UPDATE_FLG>, &
			OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    WHERE REQUEST_NO=<REQUEST_NO> AND SEQ_NO=<SEQ_NO>
update.Debug=N


//查询一般出库的细项
queryInv.Type=TSQL
queryInv.SQL=SELECT A.SEQ_NO, A.INV_CODE, B.INV_CHN_DESC, A.QTY, A.STOCK_UNIT, A.PACK_MODE, &
       		    A.SUPTYPE_CODE, A.UPDATE_FLG, A.REQUEST_NO FROM INV_SUPREQUESTD A, INV_BASE B &
		    WHERE A.INV_CODE = B.INV_CODE AND A.REQUEST_NO = <REQUEST_NO> ORDER BY A.SEQ_NO
queryInv.Debug=N


//查询手术包出库的细项
queryPack.Type=TSQL
queryPack.SQL=SELECT A.SEQ_NO, A.INV_CODE, B.PACK_DESC, A.QTY, A.STOCK_UNIT, A.PACK_MODE, &
       		     A.SUPTYPE_CODE, A.UPDATE_FLG, A.REQUEST_NO FROM INV_SUPREQUESTD A, INV_PACKM B &
		     WHERE A.INV_CODE = B.PACK_CODE AND A.REQUEST_NO = <REQUEST_NO> ORDER BY A.SEQ_NO
queryPack.Debug=N


//查询申请细项
query.Type=TSQL
query.SQL=SELECT SEQ_NO, INV_CODE, QTY - ACTUAL_QTY AS QTY FROM INV_SUPREQUESTD &
	         WHERE REQUEST_NO = <REQUEST_NO> ORDER BY SEQ_NO
query.Debug=N


//删除申请主档
delete.Type=TSQL
delete.SQL=DELETE FROM INV_SUPREQUESTD WHERE REQUEST_NO=<REQUEST_NO> AND SEQ_NO=<SEQ_NO>
delete.Debug=N


//删除申请全部细项
deleteAll.Type=TSQL
deleteAll.SQL=DELETE FROM INV_SUPREQUESTD WHERE REQUEST_NO=<REQUEST_NO>
deleteAll.Debug=N
       

//供应室出库更新请领单状态及出库量
updateFlgAndQtyBySupDispense.Type=TSQL
updateFlgAndQtyBySupDispense.SQL=UPDATE INV_SUPREQUESTD SET &
					ACTUAL_QTY=ACTUAL_QTY+<ACTUAL_QTY>, UPDATE_FLG=<UPDATE_FLG>, &
					OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
		    		  WHERE REQUEST_NO=<REQUEST_NO> AND SEQ_NO=<SEQ_NO> 
updateFlgAndQtyBySupDispense.Debug=N











