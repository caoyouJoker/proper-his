<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;fill;print;reportPrint;disCharge;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;fill;print;|;disCharge;|;close

disCharge.Type=TMenuItem
disCharge.Text=退费
disCharge.Tip=退费
disCharge.M=S
disCharge.key=Ctrl+S
disCharge.Action=onDisCharge
disCharge.pic=030.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

fill.Type=TMenuItem
fill.Text=费用清单
fill.Tip=费用清单
fill.M=F
fill.key=Ctrl+H
fill.Action=onFill
fill.pic=correct.gif

print.Type=TMenuItem
print.Text=补打
print.Tip=票据补打
print.M=F
print.key=Ctrl+P
print.Action=onPrint
print.pic=print-1.gif

reportPrint.Type=TMenuItem
reportPrint.Text=导览单
reportPrint.Tip=导览单
reportPrint.M=F
reportPrint.key=Ctrl+P
reportPrint.Action=onReportPrint
reportPrint.pic=Print.gif
