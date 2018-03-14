##############################################
# <p>Title:放射科销售统计 </p>
#
# <p>Description:放射科销售统计 </p>
#
# <p>Copyright: Copyright (c) 2013</p>
#
# <p>Company:Javahis </p>
#
# @author wangm  2013-3-18
# @version 1.0
##############################################
Module.item=selectRad


//放射科销售统计
selectRad.Type=TSQL
selectRad.SQL=SELECT a.DISPENSE_NO AS DISPENSE_NO, a.order_code AS ORDER_CODE, B.ORDER_DESC AS ORDER_DESC, a.QTY AS QTY, &
		a.VERIFYIN_PRICE AS VERIFYIN_PRICE, (a.QTY * a.VERIFYIN_PRICE) AS VERIFYIN_PRICE_QTY, a.RETAIL_PRICE AS RETAIL_PRICE, &
		(a.QTY * a.RETAIL_PRICE) AS  RETAIL_PRICE_QTY &
	      FROM ind_DISPENSEd a, sys_fee b &
              WHERE A.ORDER_CODE = B.ORDER_CODE &
			AND DISPENSE_NO IN &
			(SELECT DISPENSE_NO &
				FROM ind_DISPENSEm &
				WHERE REQTYPE_CODE = 'EXM' &
					AND APP_ORG_CODE = '0404' &
					AND TO_ORG_CODE = '040102' &
					AND DISPENSE_DATE BETWEEN TO_DATE (<DATE_START>,'yyyy-mm-dd hh24:mi:ss') &
                                              AND TO_DATE (<DATE_END>,'yyyy-mm-dd hh24:mi:ss')) &
	      GROUP BY a.DISPENSE_NO, a.order_code, a.QTY, B.ORDER_DESC, a.RETAIL_PRICE, a.VERIFYIN_PRICE, (a.QTY * a.VERIFYIN_PRICE), (a.QTY * a.RETAIL_PRICE)
selectRad.Debug=N


































