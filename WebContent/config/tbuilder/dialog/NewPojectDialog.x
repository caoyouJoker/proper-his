<extends=%ROOT%\config\tbuilder\dialog\TDialog.x>
UI.Title=新建工程 Project
UI.ControlClassName=com.tbuilder.project.NewProjectDialogControl
UI.focusList=Name;Path;OK

WorkPanel.Item=LCaption;&
	       LName;Name;&
	       LPath;Path;&
	       SelectPath


//OK.enabled=false
//Cancel.enabled=false
//Path.enabled=false
//Name.enabled=false

LCaption.Type=TLabel
LCaption.Text=为你新建的TBuilder工程选择一个名称和路径
LCaption.x=10
LCaption.y=10
LCaption.width=300
LCaption.height=20

LName.Type=TLabel
LName.Text=名称:
LName.x=10
LName.y=70
LName.width=40
LName.height=20

Name.Type=TTextField
Name.Tip=项目名称
Name.Text=tbuilder.tpx
Name.x=50
Name.y=70
Name.width=200
Name.height=20


LPath.Type=TLabel
LPath.Text=路径:
LPath.x=10
LPath.y=95
LPath.width=40
LPath.height=20

Path.Type=TTextField
Path.Text=%ROOT%\tbuilder
Path.Tip=项目路径
Path.x=50
Path.y=95
Path.width=200
Path.height=20

SelectPath.Type=TButton
SelectPath.Text=选择目录
SelectPath.Tip=弹出选择目录的对话框
SelectPath.x=150
SelectPath.y=120
SelectPath.width=80
SelectPath.height=20
SelectPath.Action=onSelectPath