##################################################
# <p>Title:动态记录查询Menu </p>
#
# <p>Description:动态记录查询Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2009-08-14
# @version 4.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=S
query.key=Ctrl+F
query.Action=onBedTable
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=查询
clear.Tip=查询
clear.M=C
clear.key=Ctrl+Z
clear.Action=onBedTable
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif