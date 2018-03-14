UI.item=File;Edit;Window
UI.button=Save;|;LanguageComboTool;|;InputParm;Preview;Close;|;Refurbish;|;Exit

File.type=TMenu
File.text=文件
File.M=F
File.item=NewProject;Save;Close;|;Exit

NewProject.type=TMenuItem
NewProject.text=新建工程...
NewProject.tip=新建工程
NewProject.M=N
NewProject.key=Ctrl+N
NewProject.Action=onNewProject
NewProject.pic=new.gif

Save.type=TMenuItem
Save.text=保存
Save.tip=保存
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

LanguageComboTool.type=TLanguageCombo
LanguageComboTool.selectedAction=onChangeLanguage

Close.type=TMenuItem
Close.text=关闭
Close.tip=关闭
Close.M=C
Close.key=Ctrl+Q
Close.Action=onClosePanel
Close.pic=close1.gif

Exit.type=TMenuItem
Exit.text=退出
Exit.tip=退出
Exit.M=X
Exit.key=Ctrl+F4
Exit.Action=onClose
Exit.pic=close.gif

Edit.type=TMenu
Edit.text=编辑
Edit.M=E
Edit.item=Delete;AlignControls;|;InputParm;Preview

Delete.type=TMenuItem
Delete.text=删除
Delete.tip=删除
Delete.M=D
Delete.key=delete
Delete.Action="onDelete"
Delete.pic=Delete.gif

AlignControls.type=TMenu
AlignControls.text=对齐控制
AlignControls.M=N
AlignControls.item=AlignUP;AlignDown;AlignLeft;AlignRight;|;AlignWCenter;AlignHCenter;|;AlignWidth;AlignHeight;|;AlignWSpace;AlignHSpace

AlignUP.type=TMenuItem
AlignUP.text=顶对齐
AlignUP.tip=顶对齐
AlignUP.M=U
AlignUP.key=Ctrl+1
AlignUP.Action="onAlignUP"
AlignUP.pic=AlignUP.gif

AlignDown.type=TMenuItem
AlignDown.text=底对齐
AlignDown.tip=底对齐
AlignDown.M=D
AlignDown.key=Ctrl+2
AlignDown.Action="onAlignDown"
AlignDown.pic=AlignDown.gif

AlignLeft.type=TMenuItem
AlignLeft.text=左对齐
AlignLeft.tip=左对齐
AlignLeft.M=L
AlignLeft.key=Ctrl+3
AlignLeft.Action="onAlignLeft"
AlignLeft.pic=AlignLeft.gif

AlignRight.type=TMenuItem
AlignRight.text=右对齐
AlignRight.tip=右对齐
AlignRight.M=R
AlignRight.key=Ctrl+4
AlignRight.Action="onAlignRight"
AlignRight.pic=AlignRight.gif

AlignWCenter.type=TMenuItem
AlignWCenter.text=水平对齐
AlignWCenter.tip=水平对齐
AlignWCenter.M=T
AlignWCenter.Action="onAlignWCenter"
AlignWCenter.pic=AlignWCenter.gif

AlignHCenter.type=TMenuItem
AlignHCenter.text=垂直对齐
AlignHCenter.tip=垂直对齐
AlignHCenter.M=I
AlignHCenter.Action="onAlignHCenter"
AlignHCenter.pic=AlignHCenter.gif

AlignWidth.type=TMenuItem
AlignWidth.text=同宽
AlignWidth.tip=同宽
AlignWidth.M=W
AlignWidth.key=Ctrl+5
AlignWidth.Action="onAlignWidth"
AlignWidth.pic=AlignWidth.gif

AlignHeight.type=TMenuItem
AlignHeight.text=同高
AlignHeight.tip=同高
AlignHeight.M=H
AlignHeight.key=Ctrl+6
AlignHeight.Action="onAlignHeight"
AlignHeight.pic=AlignHeight.gif

AlignWSpace.type=TMenuItem
AlignWSpace.text=横向同间隔
AlignWSpace.tip=横向同间隔
AlignWSpace.M=K
AlignWSpace.Action="onAlignWSpace"
AlignWSpace.pic=AlignWSpace.gif

AlignHSpace.type=TMenuItem
AlignHSpace.text=纵向同间隔
AlignHSpace.tip=纵向同间隔
AlignHSpace.M=L
AlignHSpace.Action="onAlignHSpace"
AlignHSpace.pic=AlignHSpace.gif

InputParm.type=TMenuItem
InputParm.text=传入参数设置
InputParm.tip=传入参数设置
InputParm.M=L
InputParm.Action="onInputParm"
InputParm.pic=new.gif


Preview.type=TMenuItem
Preview.text=阅览
Preview.tip=阅览窗口
Preview.M=P
Preview.key=Ctrl+P
Preview.Action="onPreview"
Preview.pic=Preview.gif


Window.type=TMenu
Window.text=窗口
Window.M=W
Window.item=Refurbish;RefurbishAction;MEMAction

Refurbish.type=TMenuItem
Refurbish.text=刷新
Refurbish.tip=刷新
Refurbish.M=R
Refurbish.key=F5
Refurbish.Action="onReset"
Refurbish.pic=Refresh.gif

RefurbishAction.type=TMenuItem
RefurbishAction.text=刷新Action
RefurbishAction.tip=刷新Action
RefurbishAction.M=A
RefurbishAction.key=F6
RefurbishAction.Action="onResetAction"
RefurbishAction.pic=Refresh.gif

MEMAction.type=TMenuItem
MEMAction.text=察看内存
MEMAction.tip=察看内存
MEMAction.M=A
MEMAction.key=
MEMAction.Action="onMEMAction"
//RefurbishAction.pic=Refresh.gif
