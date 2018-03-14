<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;save;|;disExcel;|;BmsExcel;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;save;|;disExcel;|;BmsExcel;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

disExcel.Type=TMenuItem
disExcel.Text=导入病种数据
disExcel.Tip=导入病种数据(Ctrl+D)
disExcel.M=D
disExcel.key=Ctrl+D
disExcel.Action=onDisExcel
disExcel.pic=045.gif

BmsExcel.Type=TMenuItem
BmsExcel.Text=导入用血数据
BmsExcel.Tip=导入用血数据(Ctrl+B)
BmsExcel.M=B
BmsExcel.key=Ctrl+D
BmsExcel.Action=onBmsExcel
BmsExcel.pic=export.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新(F5)
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
