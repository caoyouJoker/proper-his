#############################################
# <p>Title:传染病报告卡查询Menu </p>
#
# <p>Description:传染病报告卡查询Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.10.14
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;back;|;new;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;new;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

back.Type=TMenuItem
back.Text=回传选中值
back.Tip=回传选中值
back.Action=onBack
back.pic=Undo.gif

new.Type=TMenuItem
new.Text=新建报告卡
new.Tip=新建报告卡
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=039.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=Z
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
