#
  # Title: 诊疗项目备案管理
  #
  # Description:诊疗项目备案管理
  #
  # Copyright: ProperSoft (c) 2014
  #
  # @author zhangs
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;export;|;exportTxt;|;newPats;|;delete;|;clear;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;export;|;exportTxt;|;newPats;|;delete;|;clear;|;close

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

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=D
delete.key=Ctrl+D
delete.Action=onDelete
delete.pic=delete.gif

exportTxt.Type=TMenuItem
exportTxt.Text=医保码汇出
exportTxt.Tip=医保码汇出
exportTxt.M=
exportTxt.key=
exportTxt.Action=onExportTxt
exportTxt.pic=exportword.gif

newPats.Type=TMenuItem
newPats.Text=导入信息
newPats.Tip=导入信息
newPats.M=Q
newPats.key=
newPats.Action=onInsertPatByExl
newPats.pic=002.gif