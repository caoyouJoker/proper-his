#############################################
# <p>Title:外科医师完成术式明细Menu </p>
#
# <p>Description:外科医师完成术式明细Menu </p>
#
# <p>Copyright: Copyright (c) 2012</p>
#
# <p>Company: bluecore</p>
#
# @author wangb 2014.07.24
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;export|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.enText=Window
Window.enTip=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;export|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

