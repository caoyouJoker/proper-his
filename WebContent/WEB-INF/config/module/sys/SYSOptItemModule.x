Module.item=selectForCombol

selectForCombol.Type=TSQL
selectForCombol.SQL=SELECT L.OPTITEM_CODE AS ID,L.OPTITEM_CHN_DESC AS NAME,L.PY1 AS PY1,L.PY2 AS PY2 FROM SYS_OPTITEM L,SYS_ORDEROPTITEM K WHERE K.OPTITEM_CODE =  L.OPTITEM_CODE