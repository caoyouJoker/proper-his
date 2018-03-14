   #
   # Title:科室核收血袋
   #
   # Description:科室核收血袋
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author yangjj 2015/04/24

Module.item=updateReceive



//修改
updateReceive.Type = TSQL
updateReceive.SQL = UPDATE BMS_BLOOD SET  RECEIVED_USER = <RECEIVED_USER> , &
						RECEIVED_DATE = <RECEIVED_DATE>	
updateReceive.ITEM = BLOOD_NO
updateReceive.BLOOD_NO = BLOOD_NO = <BLOOD_NO>
updateReceive.Debug = N
