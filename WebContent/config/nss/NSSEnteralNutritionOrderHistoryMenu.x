 #
  # Title: ����Ӫ��������ʷ���ݲ˵�
  #
  # Description:
  #
  # Copyright: JavaHis (c) 2015
  #
  # @author wangb 2015.4.20
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=return;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=return;|;close

return.Type=TMenuItem
return.Text=����
return.Tip=����
return.M=R
return.Action=onReturn
return.pic=054.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
