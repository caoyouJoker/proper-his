   #
   # Title:���Һ���Ѫ��
   #
   # Description:���Һ���Ѫ��
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author yangjj 2015/04/24

Module.item=updateReceive



//�޸�
updateReceive.Type = TSQL
updateReceive.SQL = UPDATE BMS_BLOOD SET  RECEIVED_USER = <RECEIVED_USER> , &
						RECEIVED_DATE = <RECEIVED_DATE>	
updateReceive.ITEM = BLOOD_NO
updateReceive.BLOOD_NO = BLOOD_NO = <BLOOD_NO>
updateReceive.Debug = N
