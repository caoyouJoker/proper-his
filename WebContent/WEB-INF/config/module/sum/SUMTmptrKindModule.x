# 
#  Title:挂号方式module
# 
#  Description:挂号方式module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;exists

//查询挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT TMPTRKINDCODE,TMPTRKINDDESC,PRESENTNOTATION,OPT_USER,OPT_DATE,OPT_TERM FROM SUM_TMPATUREKIND
selectdata.Debug=N

//删除挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM SUM_TMPATUREKIND WHERE TMPTRKINDCODE = <TMPTRKINDCODE>
deletedata.Debug=N

//新增挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
insertdata.Type=TSQL
insertdata.SQL= INSERT INTO SUM_TMPATUREKIND (TMPTRKINDCODE,TMPTRKINDDESC,PRESENTNOTATION,OPT_USER,OPT_DATE,OPT_TERM) &
		VALUES(<TMPTRKINDCODE>,<TMPTRKINDDESC>,<PRESENTNOTATION>,&
		<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//更新挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
updatedata.Type=TSQL
updatedata.SQL= UPDATE SUM_TMPATUREKIND SET TMPTRKINDCODE=<TMPTRKINDCODE>,TMPTRKINDDESC=<TMPTRKINDDESC>,PRESENTNOTATION=<PRESENTNOTATION>,&
		OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE TMPTRKINDCODE=<TMPTRKINDCODE>
updatedata.Debug=N

//是否存在挂号方式
exists.type=TSQL
exists.SQL=SELECT COUNT(*) AS COUNT FROM SUM_TMPATUREKIND WHERE TMPTRKINDCODE=<TMPTRKINDCODE>



