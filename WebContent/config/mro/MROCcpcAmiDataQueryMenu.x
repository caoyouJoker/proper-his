#
# Title: CCPC-AMI数据统计菜单
#
# Description:CCPC-AMI数据统计菜单
#
# Copyright: JavaHis (c) 2017
#
# @author wangb 2017.12.29
# @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;export;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
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

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出(Ctrl+O)
export.M=O
export.key=Ctrl+O
export.Action=onExport
export.pic=exportexcel.gif