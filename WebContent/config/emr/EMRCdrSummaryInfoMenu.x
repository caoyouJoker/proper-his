 #
  # Title: CDR�ۺϲ�ѯ�˵�
  #
  # Description:
  #
  # Copyright: Bluecore (c) 2015
  #
  # @author wangb 2015.5.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File
UI.button=query;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif