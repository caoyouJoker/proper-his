<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;|;clear;|;selFee;|;nbwPackage;|;deptPackage;|;showpat;|;operation;|;schdCode;|;clpOrderQuote;|;clpOrderReSchdCode;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;clear;selFee;|;nbwPackage;|;deptPackage;showpat;|;operation;|;schdCode;|;Refresh;|;clpOrderQuote;|;clpOrderReSchdCode;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

selFee.Type=TMenuItem
selFee.Text=费用查询
selFee.Tip=费用查询
selFee.M=IS
selFee.Action=onSelFee
selFee.pic=inscon.gif

schdCode.Type=TMenuItem
schdCode.Text=路径时程
schdCode.Tip=路径时程
schdCode.M=IS
schdCode.Action=onChangeSchd
schdCode.pic=convert.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

showpat.Type=TMenuItem
showpat.Text=病患列表
showpat.Tip=病患列表
showpat.M=P
showpat.Action=onShowPat
showpat.pic=patlist.gif

operation.Type=TMenuItem
operation.Text=手术室计费
operation.Tip=手术室计费
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif

nbwPackage.Type=TMenuItem
nbwPackage.Text=护士套餐
nbwPackage.Tip=护士套餐
nbwPackage.M=
nbwPackage.Action=onNbwPackage
nbwPackage.pic=019.gif

deptPackage.Type=TMenuItem
deptPackage.Text=科室套餐
deptPackage.Tip=科室套餐
deptPackage.M=
deptPackage.Action=onDeptPackage
deptPackage.pic=012.gif

clpOrderQuote.Type=TMenuItem
clpOrderQuote.Text=引入路径
clpOrderQuote.Tip=引入路径
clpOrderQuote.M=
clpOrderQuote.Action=onAddCLNCPath
clpOrderQuote.pic=054.gif

clpOrderReSchdCode.Type=TMenuItem
clpOrderReSchdCode.Text=费用时程修改
clpOrderReSchdCode.Tip=费用时程修改
clpOrderReSchdCode.M=
clpOrderReSchdCode.Action=onClpOrderReSchdCode
clpOrderReSchdCode.pic=046.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClosePanel
//close.Action=onClose
close.pic=close.gif

