<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;sendRe;|;unExecQuery;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;delete;Refresh;query;|;unExecQuery;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=F2
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

sendRe.Type=TMenuItem
sendRe.Text=重送
sendRe.Tip=重送
sendRe.M=S
sendRe.key=Ctrl+S
sendRe.Action=onSendRe
sendRe.pic=008.gif

unExecQuery
unExecQuery.Type=TMenuItem
unExecQuery.Text=未执行医嘱查询
unExecQuery.Tip=未执行医嘱查询
unExecQuery.M=S
unExecQuery.key=Ctrl+S
unExecQuery.Action=onUnExecQuery
unExecQuery.pic=query.gif

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