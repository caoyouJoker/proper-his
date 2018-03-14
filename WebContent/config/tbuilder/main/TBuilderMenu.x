UI.item=File;Window
UI.button=Refurbish;|;Exit

File.type=TMenu
File.text=文件
File.M=F
File.item=NewProject;|;Exit

NewProject.type=TMenuItem
NewProject.text=新建工程...
NewProject.tip=新建工程
NewProject.M=N
NewProject.key=Ctrl+N
NewProject.Action=onNewProject
NewProject.pic=new.gif


Exit.type=TMenuItem
Exit.text=退出
Exit.tip=退出
Exit.M=X
Exit.key=Ctrl+F4
Exit.Action=onClose
Exit.pic=close.gif

Window.type=TMenu
Window.text=窗口
Window.M=W
Window.item=Refurbish;RefurbishAction

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
