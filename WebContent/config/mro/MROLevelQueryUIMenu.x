#############################################
# <p>Title:电子病历分级分块Menu </p>
#
# <p>Description:电子病历分级分块Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author YangJj 2016.10.10
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;mrshow;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=clear;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

mrshow.Type=TMenuItem
mrshow.Text=病历浏览
mrshow.Tip=病历浏览(Ctrl+W)
mrshow.M=W
mrshow.key=Ctrl+W
mrshow.Action=onShow
mrshow.pic=012.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
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

