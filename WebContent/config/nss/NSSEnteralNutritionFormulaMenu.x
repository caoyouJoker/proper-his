 #
  # Title: Ӫ����ʳ��Ͳ˵�
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2014
  #
  # @author wangb 2014.12.17
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
