 #
 # Title: �豸׷��
 #
 # Description:�豸׷��
 #
 # Copyright: JavaHis (c) 2012
 #
 # @author yuhaibao
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=quary;clear|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=quary;clear|;close

quary.Type=TMenuItem
quary.Text=��ѯ
quary.Tip=��ѯ
quary.M=Q
quary.key=Ctrl+F
quary.Action=onQuery
quary.pic=query.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif