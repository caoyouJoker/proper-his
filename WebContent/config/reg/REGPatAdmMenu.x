# 
#  Title:�Һ�����
# 
#  Description:�Һ�����
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unreg;arrive;;clear;print;|;Wrist;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=unreg;arrive;Refresh;clear;print;|;close

unreg.Type=TMenuItem
unreg.Text=�˹�
unreg.Tip=�˹�
unreg.M=U
unreg.key=
unreg.Action=onUnReg
unreg.pic=030.gif

arrive.Type=TMenuItem
arrive.Text=����
arrive.Tip=����
arrive.M=A
arrive.key=
arrive.Action=onArrive
arrive.pic=017.gif

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=��ӡ���
Wrist.Tip=��ӡ���
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif