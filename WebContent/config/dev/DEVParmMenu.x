 #
 # Title: �豸������
 #
 # Description:6.1.1.	�豸������
 #
 # Copyright: JavaHis (c) 2013
 #
 # @author fux
 # @version 1.0 
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh 
 
File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif