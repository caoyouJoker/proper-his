 #
  # Title: ��Ӧ����
  #
  # Description: ��Ӧ����
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author wukai 2017.02.14
 # @version 1.0

<Type=TMenuBar>
UI.Item=File
UI.button=refresh;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=refresh;close

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

refresh.Type=TMenuItem
refresh.Text=ˢ��
refresh.Tip=ˢ��
refresh.M=R
refresh.key=F5
refresh.Action=onReresh
refresh.pic=Refresh.gif

