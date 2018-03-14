  #
   # Title: Â¼ÈëÍ¾¾¶¼ì²â
   #
   # Description:Â¼ÈëÍ¾¾¶¼ì²â
   #
   # Copyright: JavaHis (c) 2017
   #
   # @author liuyalin
Module.item=selectInputWay;saveInputWay;updateInputWay


selectInputWay.Type=TSQL
selectInputWay.SQL=SELECT * FROM SYS_INPUTWAY WHERE 1 = 1 ORDER BY cast(GDVAS_CODE as int)
selectInputWay.Item=GDVAS_CODE;GDVAS_DESC;ENABLE_FLG
selectInputWay.GDVAS_CODE=GDVAS_CODE=<GDVAS_CODE> 
selectInputWay.GDVAS_DESC=GDVAS_DESC=<GDVAS_DESC> 
selectInputWay.ENABLE_FLG=ENABLE_FLG=<ENABLE_FLG> 
selectInputWay.Debug=Y

//delectInputWay.Type=TSQL
//delectInputWay.SQL=DELETE FROM SYS_INPUTWAY WHERE GDVAS_CODE=<GDVAS_CODE> AND GDVAS_DESC=<GDVAS_DESC>
//delectInputWay.Debug=Y


saveInputWay.Type=TSQL
saveInputWay.SQL=INSERT INTO SYS_INPUTWAY (GDVAS_CODE, GDVAS_DESC,  OPT_DATE, OPT_USER, OPT_TERM,ENABLE_FLG) VALUES &
					(<GDVAS_CODE>, <GDVAS_DESC>, TO_DATE(<OPT_DATE>, 'YYYYMMDDHH24MISS'), <OPT_USER>, <OPT_TERM>,<ENABLE_FLG>) 
saveInputWay.Debug=Y

updateInputWay.Type=TSQL
updateInputWay.SQL=UPDATE SYS_INPUTWAY SET GDVAS_DESC = <GDVAS_DESC>,  OPT_DATE = TO_DATE(<OPT_DATE>, 'YYYYMMDDHH24MISS'),  OPT_USER = <OPT_USER>,  OPT_TERM = <OPT_TERM> ,  ENABLE_FLG = <ENABLE_FLG> & 
					 WHERE GDVAS_CODE = <GDVAS_CODE> 
updateInputWay.Debug=Y

