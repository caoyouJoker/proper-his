# 
#  Title:����ת��
# 
#  Description:����ת��
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangl 2015.9.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=new;save;;clear;|;close

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Submit.Type=TMenuItem
Submit.Text=�ύ
Submit.Tip=�ύ
Submit.M=W
Submit.Action=onWrist
Submit.pic=015.gif

