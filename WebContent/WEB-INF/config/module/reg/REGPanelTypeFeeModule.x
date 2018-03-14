# 
#  Title:号别费用module
# 
#  Description:号别费用module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinictypefee;getOrderCode;getOrderCodeDetial

//查询
queryTree.Type=TSQL
queryTree.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
		     OPT_DATE,OPT_TERM &
		FROM REG_CLINICTYPE_FEE &
	       WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE> &
	    ORDER BY CLINICTYPE_CODE
queryTree.Debug=N

//查询门急诊,号别,费用代码,收费类别,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
		      OPT_DATE,OPT_TERM &
		 FROM REG_CLINICTYPE_FEE &
	     ORDER BY CLINICTYPE_CODE
selectdata.item=ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.CLINICTYPE_CODE=CLINICTYPE_CODE=<CLINICTYPE_CODE>
selectdata.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
selectdata.Debug=N

//删除门急诊,号别,费用代码,收费类别,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICTYPE_FEE &
		     WHERE ADM_TYPE=<ADM_TYPE> &
		       AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
		       AND ORDER_CODE = <ORDER_CODE>
deletedata.Debug=N

//新增门急诊,号别,费用代码,收费类别,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICTYPE_FEE &
			   (ADM_TYPE,CLINICTYPE_CODE,ORDER_CODE,RECEIPT_TYPE,OPT_USER,&
			   OPT_DATE,OPT_TERM) &
		    VALUES (<ADM_TYPE>,<CLINICTYPE_CODE>,<ORDER_CODE>,<RECEIPT_TYPE>,<OPT_USER>,&
		           SYSDATE,<OPT_TERM>)
insertdata.Debug=N              

//更新门急诊,号别,费用代码,收费类别,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICTYPE_FEE &
		  SET ADM_TYPE=<ADM_TYPE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,ORDER_CODE=<ORDER_CODE>,&
		      RECEIPT_TYPE=<RECEIPT_TYPE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
		      OPT_TERM=<OPT_TERM> &
		WHERE ADM_TYPE=<ADM_TYPE> &
		  AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
		  AND ORDER_CODE = <ORDER_CODE>								
updatedata.Debug=N

//是否存在号别费用
existsClinictypefee.type=TSQL
existsClinictypefee.SQL=SELECT COUNT(*) AS COUNT &
			  FROM REG_CLINICTYPE_FEE &
                         WHERE ADM_TYPE=<ADM_TYPE> &
                           AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
                           AND ORDER_CODE = <ORDER_CODE>

//根据ADM_TYPE,CLINICTYPE_CODE查找ORDER_CODE
getOrderCode.Type=TSQL
getOrderCode.SQL=SELECT ORDER_CODE &
		   FROM REG_CLINICTYPE_FEE &
		  WHERE ADM_TYPE=<ADM_TYPE> &
		    AND CLINICTYPE_CODE=<CLINICTYPE_CODE>
getOrderCode.Debug=N

//根据ADM_TYPE,CLINICTYPE_CODE,RECEIPT_TYPE查找ORDER_CODE
getOrderCodeDetial.Type=TSQL
getOrderCodeDetial.SQL=SELECT ORDER_CODE &
			 FROM REG_CLINICTYPE_FEE &
			WHERE ADM_TYPE=<ADM_TYPE> &
			  AND CLINICTYPE_CODE=<CLINICTYPE_CODE> &
			  AND RECEIPT_TYPE=<RECEIPT_TYPE>
getOrderCodeDetial.Debug=N


