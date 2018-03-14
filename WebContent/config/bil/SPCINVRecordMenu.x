<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;commit;|;deleteRow;|;delete;|;query;|;operation;|;clpOrderReSchdCode;|;clearTable;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口                 
Window.M=W
Window.Item=Refresh                   
                                
File.Type=TMenu    
File.Text=文件
File.M=F
File.Item=query;|;Refresh;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

commit.Type=TMenuItem
commit.Text=提交计费
commit.Tip=提交计费
commit.M=S
commit.key=Ctrl+S
commit.Action=onCommit
commit.pic=fee.gif

deleteRow.Type=TMenuItem
deleteRow.Text=删除行
deleteRow.Tip=删除行(Delete Row)
deleteRow.M=N
deleteRow.key=Delete
deleteRow.Action=onDeleteRow
deleteRow.pic=closebill-2.gif

delete.Type=TMenuItem
delete.Text=删除
delete.Tip=删除(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

operation.Type=TMenuItem
operation.Text=手术包计费
operation.Tip=手术包计费
operation.M=P
operation.Action=onOperation
operation.pic=operation.gif  

clearTable.Type=TMenuItem
clearTable.Text=清空表格
clearTable.Tip=清空表格
clearTable.M=C
clearTable.Action=onClearTable
clearTable.pic=clear.gif

clear.Type=TMenuItem
clear.Text=全部清空
clear.Tip=全部清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

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
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif
