 #
  # Title: 科室备药生成
  #
  # Description:科室备药生成
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author zhangy 2009.05.07
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;clear;|;printM;|;printRecipe;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;clear;|;printM;|;printRecipe;|;close

save.Type=TMenuItem
save.Text=生成请领单
save.Tip=生成请领单
save.M=C
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

printM.Type=TMenuItem
printM.Text=汇总打印
printM.Tip=汇总打印
printM.M=P
printM.Action=onPrintM
printM.pic=print.gif

printD.Type=TMenuItem
printD.Text=明细打印
printD.Tip=明细打印
printD.M=P
printD.Action=onPrintD
printD.pic=print.gif

printRecipe.Type=TMenuItem
printRecipe.Text=打印处方签
printRecipe.Tip=打印处方签
printRecipe.M=R
printRecipe.Action=onPrintRecipe
printRecipe.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
