# 
#  Title:������ҽ��-��ʹ���Ĳ���
# 
#  Description:CT����
# 
#  Copyright: Copyright (c) Javahis 2017
# 
#  author WangQing 2017.2.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;save;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;Refresh;close

Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
