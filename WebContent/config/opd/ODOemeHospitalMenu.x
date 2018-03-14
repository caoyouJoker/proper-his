<Type=TMenuBar>
UI.Item=File;PRE;INS;ClinicSPC;Window;Package;Clinical;Report;Clp;Emr;Other
UI.button=save;|;close
 
Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=Refresh;close



//==============================================


//===================================================
save.Type=TMenuItem
save.Text=保存
save.zhText=保存
save.enText=Save
save.Tip=保存
save.zhTip=保存
save.enTip=Save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif




print.Type=TMenuItem
print.Text=住院证打印
print.zhText=住院证打印
print.enText=print
print.Tip=住院证打印
print.zhTip=住院证打印
print.enTip=print
print.M=N
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出
close.zhTip=退出
close.enTip=Quit
close.M=X 
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

