<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;query;clear;|;charge;emr;tpr|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

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

charge.Type=TMenuItem
charge.Text=补充计费
charge.Tip=补充计费
charge.M=H
charge.key=Ctrl+H
charge.Action=onCharge
charge.pic=bill-1.gif

emr.Type=TMenuItem
emr.Text=结构化病历
emr.Tip=结构化病历
emr.M=J
emr.key=Ctrl+J
emr.Action=onEmr
emr.pic=emr-2.gif

tpr.Type=TMenuItem
tpr.Text=体温单
tpr.Tip=体温单
tpr.M=J
tpr.key=Ctrl+T
tpr.Action=onVitalSign
tpr.pic=Column.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
close.pic=close.gif