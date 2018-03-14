<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;exportRecord;|;authcode;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu   
File.Text=文件
File.M=F
File.Item=query;|;clear;|;export;|;exportRecord;|;authcode;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
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


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

authcode.Type=TMenuItem
authcode.Text=生成授权码
authcode.Tip=生成授权码
authcode.M=P
authcode.Action=onPrint
authcode.pic=print.gif


export.Type=TMenuItem
export.Text=导出就诊记录
export.Tip=导出就诊记录
export.M=E
export.key=F4
export.Action=onExport
export.pic=exportexcel.gif

exportRecord.Type=TMenuItem
exportRecord.Text=导出记录清单
exportRecord.Tip=导出记录清单
exportRecord.M=E
exportRecord.Action=onExportRecord
exportRecord.pic=exportexcel.gif



