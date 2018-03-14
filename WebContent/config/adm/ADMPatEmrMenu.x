#################################################
# <p>Title:病患查询Menu </p>
#
# <p>Description:病患查询Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY 2009.04.30
# @version 1.0
#################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;close

save.Type=TMenuItem
save.Text=传回
save.Tip=传回
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Undo.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=
query.key=
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif