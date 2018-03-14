<Type=TMenuBar>
UI.Item=File;Window
UI.button=Print;|;PRINT_LIST;PrintSetup;|;ShowZoom;|;exit

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=PrintSetup;|;PSetup;|;Print;|;LinkPrint;ShowRowIDSwitch;|;exit

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Save;Load

Save.Type=TMenuItem
Save.Text=保存数据(调试用)
Save.M=S
Save.Action=onSave

Load.Type=TMenuItem
Load.Text=读取数据(调试用)
Load.M=L
Load.Action=onLoad

Print.Type=TMenuItem
Print.Text=打印
Print.Enabled=true
Print.M=P
Print.Action=onPrint
Print.pic=print.gif

PrintSetup.Type=TMenuItem
PrintSetup.Text=打印设置
PrintSetup.Enabled=true
PrintSetup.M=S
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PSetup.Type=TMenuItem
PSetup.Text=打印阅览
PSetup.Enabled=true
PSetup.M=E
PSetup.Action=onPSetup

LinkPrint.Type=TMenuItem
LinkPrint.Text=续印
LinkPrint.Enabled=true
LinkPrint.M=T
LinkPrint.Action=onPrintXDDialog

ShowRowIDSwitch.Type=TMenuItem
ShowRowIDSwitch.Text=显示行号开关
ShowRowIDSwitch.Enabled=true
ShowRowIDSwitch.M=T
ShowRowIDSwitch.Action=onShowRowIDSwitch

ShowZoom.type=TShowZoomCombo
ShowZoom.PreferredWidth=80
ShowZoom.ShowID=false
ShowZoom.SelectedAction=onShowZoom

PRINT_LIST.Type=TPrintListCombo
PRINT_LIST.PreferredWidth=300
PRINT_LIST.ShowID=false
PRINT_LIST.SelectedAction=onPrintList

exit.Type=TMenuItem
exit.Text=退出
exit.Tip=close
exit.M=X
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif

