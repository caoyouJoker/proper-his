#############################################
# <p>Title:�������Ͳ˵� </p>
#
# <p>Description:�������Ͳ˵� </p>
#
# <p>Copyright: Copyright (c) 2016</p>
#
# <p>Company:Javahis </p>
#
# @author wukai
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;clear;

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
clear.M=
clear.key=
clear.Action=onClear
clear.pic=clear.gif
