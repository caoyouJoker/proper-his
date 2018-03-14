<Type=TPanel>

UI.ControlClassName=com.tbuilder.work.ModulePanelControl
UI.Item=Design|Center
UI.Layout=BorderLayout
UI.X=10
UI.Y=10
UI.Width=200
UI.Height=200

Design.type=TSplitPane
Design.dividerLocation=<i>100
Design.item=TDB|Left;DesignBack|Right


TDB.type=TTree


DesignBack.type=TSplitPane
DesignBack.dividerLocation=<i>580
DesignBack.item=UIDesign|Left;UIProperty|Right


UIDesign.type=TTextArea
//UIDesign.ControlClassName=com.tbuilder.work.UIDesugbControl
//UIDesign.bkcolor=161,192,245

UIProperty.type=TTable
UIProperty.header=name,100;value,80
UIProperty.autoResizeMode=1
//UIProperty.value=[[x,0],[y,0],[width,0],[height,0]]
UIProperty.lockColumns=0
UIProperty.ColumnHorizontalAlignmentData=0,Left;1,右
//UIProperty.HorizontalAlignmentData=0,1,左;1,1,左
UIProperty.RowHeight=20
UIProperty.focusIndexJump=N


TComponentList=TButton;TLabel;TTextField;TNumberTextField;TDateField;TPasswordField;TCheckBox;TRadioButton;TComboBox;TPanel;TTable;TTree;TTabbedPane;TLayoutAdapter;TMovePane;TDataWindow;TTextArea
TComboList=性别下拉列表;区域下拉列表;付款途径下拉列表;门急住别下拉列表;星期下拉列表;时段下拉列表;MDC项目下拉列表;科室下拉列表;CCMD疾病码下拉列表;报告类别下拉列表;收据费用下拉列表;院内费用下拉列表;统计费用下拉列表;首页费用下拉列表

