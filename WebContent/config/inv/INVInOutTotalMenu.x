#############################################
# <p>Title:物资管理Menu </p>
#
# <p>Description:物资管理Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangh 2013.05.3
# @version 1.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;preview;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;preview;|;close


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+Q)
query.M=Q
query.key=Ctrl+Q
query.Action=onQuery
query.pic=query.gif

preview.Type=TMenuItem
preview.Text=预览
preview.Tip=预览(Ctrl+E)
preview.M=E
preview.key=Ctrl+E
preview.Action=onPreview
preview.pic=preview.gif

