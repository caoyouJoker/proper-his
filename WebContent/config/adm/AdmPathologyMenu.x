#############################################
# <p>Title:病理类型菜单 </p>
#
# <p>Description:病理类型菜单 </p>
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
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;clear;

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=
clear.key=
clear.Action=onClear
clear.pic=clear.gif
