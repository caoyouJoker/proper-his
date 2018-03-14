Module.item=initchargehospcode

//得到院内费用名称
initchargehospcode.Type=TSQL
initchargehospcode.SQL=SELECT CHARGE_HOSP_CODE AS ID,CHARGE_HOSP_DESC AS NAME,PY1,PY2 FROM SYS_CHARGE_HOSP 
initchargehospcode.Debug=N
