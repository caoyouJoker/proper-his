##################################################
# <p>Title:中包装转换工具类 </p>
#
# <p>Description:中包装转换工具类  </p>
#
# <p>Copyright: Copyright (c) 2013</p>
#
# <p>Company:JavaHis </p>
#
# @author shendongri
# @version 1.0
##################################################
Module.item=queryUnitByOrderCodeInfo;updateInfo;queryUnitDescByCode;queryByOrderCodeInfo;queryInfo

#根据药品代码查询单位
queryUnitByOrderCodeInfo.Type=TSQL
queryUnitByOrderCodeInfo.SQL=SELECT B.UNIT_CHN_DESC,C.UNIT_CHN_DESC,D.UNIT_CHN_DESC,E.UNIT_CHN_DESC &
						  FROM PHA_BASE A,SYS_UNIT B,SYS_UNIT C,SYS_UNIT D,SYS_UNIT E &
							WHERE  A.ORDER_CODE = <ORDER_CODE> &
							   AND B.UNIT_CODE = A.DOSAGE_UNIT &
							   AND C.UNIT_CODE = A.MEDI_UNIT &
							   AND D.UNIT_CODE = A.PURCH_UNIT &
							   AND E.UNIT_CODE = A.STOCK_UNIT
queryUnitByOrderCodeInfo.Debug=N


#更新
updateInfo.Type=TSQL
updateInfo.SQL=UPDATE PHA_BASE SET CONVERSION_RATIO=<CONVERSION_RATIO> WHERE ORDER_CODE=<ORDER_CODE>
updateInfo.Debug=N


#根据UNIT_CHN_DESC查询对应的UNIT_CODE
queryUnitDescByCode.Type=TSQL
queryUnitDescByCode.SQL=SELECT DISTINCT A.UNIT_CODE,B.UNIT_CODE,C.UNIT_CODE,D.UNIT_CODE FROM SYS_UNIT A,SYS_UNIT B,SYS_UNIT C, &
						SYS_UNIT D WHERE A.UNIT_CHN_DESC = <DOSAGE_UNIT> AND B.UNIT_CHN_DESC = <MEDI_UNIT> AND C.UNIT_CHN_DESC = <PURCH_UNIT> &
						AND D.UNIT_CHN_DESC = <STOCK_UNIT>
queryUnitDescByCode.Debug=N


#根据ORDER_CODE查PHA_BASE
queryByOrderCodeInfo.Type=TSQL
queryByOrderCodeInfo.SQL=SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.CONVERSION_RATIO, &
						  B.UNIT_CHN_DESC AS DOSAGE_UNIT,C.UNIT_CHN_DESC AS MEDI_UNIT,D.UNIT_CHN_DESC AS PURCH_UNIT,E.UNIT_CHN_DESC AS STOCK_UNIT  &
                          FROM PHA_BASE A,SYS_UNIT B,SYS_UNIT C,SYS_UNIT D,SYS_UNIT E  &
                             WHERE  A.ORDER_CODE = <ORDER_CODE>  &
                               AND B.UNIT_CODE = A.DOSAGE_UNIT  &
                               AND C.UNIT_CODE = A.MEDI_UNIT  &
                               AND D.UNIT_CODE = A.PURCH_UNIT  &
                               AND E.UNIT_CODE = A.STOCK_UNIT
queryByOrderCodeInfo.Debug=N


#查PHA_BASE
queryInfo.Type=TSQL
queryInfo.SQL=SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.CONVERSION_RATIO, &
						  B.UNIT_CHN_DESC AS DOSAGE_UNIT,C.UNIT_CHN_DESC AS MEDI_UNIT,D.UNIT_CHN_DESC AS PURCH_UNIT,E.UNIT_CHN_DESC AS STOCK_UNIT  &
                          FROM PHA_BASE A,SYS_UNIT B,SYS_UNIT C,SYS_UNIT D,SYS_UNIT E  &
                             WHERE B.UNIT_CODE = A.DOSAGE_UNIT  &
                               AND C.UNIT_CODE = A.MEDI_UNIT  &
                               AND D.UNIT_CODE = A.PURCH_UNIT  &
                               AND E.UNIT_CODE = A.STOCK_UNIT
queryInfo.Debug=N