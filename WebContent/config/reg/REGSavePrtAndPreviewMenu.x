#############################################
# <p>Title:�������ȱ����ӡԤ��Menu </p>
#
# <p>Description:�������ȱ����ӡԤ��Menu </p>
#
# <p>Copyright: Copyright (c) 2017</p>
#
# <p>Company: Javahis</p>
#
# @author wangqing 20170922
# @version 5.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=print;|;close

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=print;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=��ӡ�ϴ�
print.Tip=��ӡ�ϴ�
print.M=
print.key=
print.Action=onPrint
print.pic=save.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
