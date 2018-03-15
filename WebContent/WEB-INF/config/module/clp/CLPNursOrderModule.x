#
# Title:�����ֵ�
#
# Description:�����ֵ�
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/05/28
Module.item=insertData;selectData;deleteData;updateData;checkDataExist

insertData.Type=TSQL
insertData.SQL=INSERT INTO CLP_NURSORDER (ORDER_CODE,REGION_CODE,ORDER_CHN_DESC,ORDER_ENG_DESC,PY1,PY2,UNIT,FREQ,&
							  AMOUNT,TYPE_CODE,DESCRIPTION,DEL_FLG,CHKTYPE_CODE,SEQ,OPT_USER,OPT_DATE,OPT_TERM)&
							  VALUES(<ORDER_CODE>,<REGION_CODE>,<ORDER_CHN_DESC>,<ORDER_ENG_DESC>,<PY1>,<PY2>,<UNIT>,<FREQ>,&
							  <AMOUNT>,<TYPE_CODE>,<DESCRIPTION>,<DEL_FLG>,<CHKTYPE_CODE>,&
							  (SELECT CASE WHEN <SEQ> IS NULL THEN (SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ)+1)) END FROM CLP_NURSORDER GROUP BY SEQ) ELSE <SEQ> END FROM DUAL),&
							  <OPT_USER>,TO_DATE(<OPT_DATE>,'YYYYMMDD'),<OPT_TERM>&
							  )
insertData.Debug=N

selectData.Type=TSQL
selectData.SQL=SELECT ORDER_CODE,REGION_CODE,ORDER_CHN_DESC,ORDER_ENG_DESC,PY1,PY2,UNIT,FREQ,&
							 AMOUNT,TYPE_CODE,DESCRIPTION,DEL_FLG,CHKTYPE_CODE,SEQ,OPT_USER,OPT_DATE,OPT_TERM &
							 FROM CLP_NURSORDER WHERE REGION_CODE = <REGION_CODE>
selectData.item=ORDER_CODE;PY2;ORDER_CHN_DESC;ORDER_ENG_DESC;UNIT;FREQ;AMOUNT;TYPE_CODE;DEL_FLG;CHKTYPE_CODE;ORDTYPE_CODE;CLP_RATE;CLP_UNIT;DESCRIPTION;SEQ
selectData.ORDER_CODE=ORDER_CODE LIKE <ORDER_CODE>
selectData.PY2=PY2 LIKE <PY2>
selectData.ORDER_CHN_DESC=ORDER_CHN_DESC LIKE <ORDER_CHN_DESC>
selectData.ORDER_ENG_DESC=ORDER_ENG_DESC LIKE <ORDER_ENG_DESC>
selectData.UNIT=UNIT=<UNIT>
selectData.FREQ=FREQ=<FREQ>
selectData.AMOUNT=AMOUNT=<AMOUNT>
selectData.TYPE_CODE=TYPE_CODE=<TYPE_CODE>
selectData.DEL_FLG=DEL_FLG=<DEL_FLG>
selectData.CHKTYPE_CODE=CHKTYPE_CODE=<CHKTYPE_CODE>
selectData.ORDTYPE_CODE=ORDTYPE_CODE=<ORDTYPE_CODE>
selectData.CLP_RATE=CLP_RATE=<CLP_RATE>
selectData.CLP_UNIT=CLP_UNIT=<CLP_UNIT>
selectData.DESCRIPTION=DESCRIPTION LIKE <DESCRIPTION>
selectData.SEQ=SEQ LIKE <SEQ>
selectData.Debug=N


deleteData.Type=TSQL
deleteData.SQL=	DELETE FROM  CLP_NURSORDER  WHERE ORDER_CODE = <ORDER_CODE>
deleteData.Debug=N

updateData.Type=TSQL
updateData.SQL=UPDATE CLP_NURSORDER SET REGION_CODE=<REGION_CODE>,ORDER_CHN_DESC=<ORDER_CHN_DESC>,ORDER_ENG_DESC=<ORDER_ENG_DESC>,&
							 PY1=<PY1>,PY2=<PY2>,UNIT=<UNIT>,FREQ=<FREQ>,AMOUNT=<AMOUNT>,TYPE_CODE=<TYPE_CODE>,DESCRIPTION=<DESCRIPTION>,&
							 DEL_FLG=<DEL_FLG>,CHKTYPE_CODE=<CHKTYPE_CODE>,SEQ=(SELECT CASE WHEN <SEQ> IS NULL THEN (SELECT CASE  (COUNT(MAX(SEQ))) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(SEQ)+1)) END FROM CLP_NURSORDER GROUP BY SEQ) ELSE <SEQ> END FROM DUAL),&
							 OPT_USER=<OPT_USER>,OPT_DATE=TO_DATE(<OPT_DATE>,'YYYYMMDD'),OPT_TERM=<OPT_TERM> &
  						 WHERE ORDER_CODE = <ORDER_CODE> 
updateData.Debug=N

checkDataExist.Type=TSQL
checkDataExist.SQL=	SELECT COUNT(*) AS DATACOUNT FROM  CLP_NURSORDER  WHERE REGION_CODE=<REGION_CODE>&
AND ORDER_CODE = <ORDER_CODE>
checkDataExist.Debug=N