##################################################
# <p>Title:病患动态档 </p>
#
# <p>Description:病患动态档 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
##################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh


File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=
query.key=
query.Action=onQuery
query.pic=query.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif