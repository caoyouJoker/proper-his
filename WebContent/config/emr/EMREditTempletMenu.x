<Type=TMenuBar>
UI.Item=File;Edid;ImageEditMenu;Insert;TableElem;InsertElement;AllExpression;Window
UI.button=save;|;ModifyFontCombo;ModifyFontSizeCombo;AlignmentLeft;AlignmentCenter;AlignmentRight;ShowZoom;|;FontBMenu;FontIMenu;|;clear;|;ClearMenu;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;PrintSetup;PrintPageSet;PrintShow;PrintClear;LinkPrint;ShowRowIDSwitch;Refresh;|;clear;|;close

Edid.Type=TMenu
Edid.Text=编辑
Edid.M=E
Edid.Item=delFixText;FixTextProperty;DIVProperty;|;MicroFieldProperty;addDLText;TableProperty;|;ClearMenu;CutMenu;CopyMenu;PasteMenu;DeleteMenu;DeleteCapMenu;|;FormatSet;FormatUse

Insert.Type=TMenu
Insert.Text=控件
Insert.M=I
Insert.Item=insertFixText;insertMarkText;InsertSingleChoose;InsertRelateChoose;InsertMultiChoose;InsertHasChoose;|;InsertMicroField;|;addInputMessage;|;InsertCaptureObject;CaptureDataProperty;|;InsertCheckBoxChooseObject;CustomScriptDialog;|;InsertPictureObject;InsertNumberChooseObject;|;InsertTextFormat


TableElem.Type=TMenu
TableElem.Text=表格
TableElem.M=T
TableElem.Item=InsertTable;DelTable;MergerCell;InsertTableRow;AddTableRow;DelTableRow


save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

ShowZoom.type=TShowZoomCombo
ShowZoom.PreferredWidth=80
ShowZoom.ShowID=false

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

AlignmentLeft.type=TMenuItem
AlignmentLeft.text=居左
AlignmentLeft.tip=居左
AlignmentLeft.Action=onAlignmentLeft
AlignmentLeft.pic=Left.gif

AlignmentCenter.type=TMenuItem
AlignmentCenter.text=居中
AlignmentCenter.tip=居中
AlignmentCenter.Action=onAlignmentCenter
AlignmentCenter.pic=Center.gif

AlignmentRight.type=TMenuItem
AlignmentRight.text=居右
AlignmentRight.tip=居右
AlignmentRight.Action=onAlignmentRight
AlignmentRight.pic=Right.gif

insertFixText.type=TMenuItem
insertFixText.Text=固定文本
insertFixText.Tip=固定文本
insertFixText.M=I
insertFixText.key=
insertFixText.Action=onInsertFixText
insertFixText.pic=emr-1.gif

insertMarkText.type=TMenuItem
insertMarkText.Text=插入上下标
insertMarkText.Tip=插入上下标
insertMarkText.M=I
insertMarkText.key=
insertMarkText.Action=onInsertMarkText
insertMarkText.pic=emr-1.gif

FixTextProperty.type=TMenuItem
FixTextProperty.Text=元素属性
FixTextProperty.Tip=元素属性
FixTextProperty.M=E
FixTextProperty.key=
FixTextProperty.Action=onFixTextProperty
FixTextProperty.pic=spreadout.gif

InsertSingleChoose.type=TMenuItem
InsertSingleChoose.Text=插入单选
InsertSingleChoose.Tip=插入单选
InsertSingleChoose.M=I
InsertSingleChoose.key=
InsertSingleChoose.Action=onInsertSingleChoose
InsertSingleChoose.pic=check_up_rpt.gif


InsertRelateChoose.type=TMenuItem
InsertRelateChoose.Text=插入关联
InsertRelateChoose.Tip=插入关联
InsertRelateChoose.M=I
InsertRelateChoose.key=
InsertRelateChoose.Action=onInsertRelateChoose
InsertRelateChoose.pic=check_up_rpt.gif


InsertMultiChoose.type=TMenuItem
InsertMultiChoose.Text=插入多选
InsertMultiChoose.Tip=插入多选
InsertMultiChoose.M=I
InsertMultiChoose.key=
InsertMultiChoose.Action=onInsertMultiChoose
InsertMultiChoose.pic=check_up_rpt.gif

InsertHasChoose.type=TMenuItem
InsertHasChoose.Text=有无选择
InsertHasChoose.Tip=有无选择
InsertHasChoose.M=I
InsertHasChoose.key=
InsertHasChoose.Action=onInsertHasChoose
InsertHasChoose.pic=check_up_rpt.gif

InsertMicroField.type=TMenuItem
InsertMicroField.Text=插入宏
InsertMicroField.Tip=插入宏
InsertMicroField.M=I
InsertMicroField.key=
InsertMicroField.Action=onInsertMicroField
InsertMicroField.pic=check_up_rpt.gif

MicroFieldProperty.type=TMenuItem
MicroFieldProperty.Text=宏属性
MicroFieldProperty.Tip=宏属性
MicroFieldProperty.M=I
MicroFieldProperty.key=
MicroFieldProperty.Action=onMicroFieldProperty
MicroFieldProperty.pic=check_up_rpt.gif

InsertTable.type=TMenuItem
InsertTable.Text=插入表格
InsertTable.Tip=插入表格
InsertTable.M=I
InsertTable.key=
InsertTable.Action=onInsertTable
InsertTable.pic=inscon.gif

DelTable.type=TMenuItem
DelTable.Text=删除表格
DelTable.Tip=删除表格
DelTable.M=I
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=cancle.gif

InsertTableRow.type=TMenuItem
InsertTableRow.Text=插入表格行
InsertTableRow.Tip=插入表格行
InsertTableRow.M=I
InsertTableRow.key=
InsertTableRow.Action=onInsertTableRow
InsertTableRow.pic=sys.gif

AddTableRow.type=TMenuItem
AddTableRow.Text=追加表格行
AddTableRow.Tip=追加表格行
AddTableRow.M=I
AddTableRow.key=
AddTableRow.Action=onAddTableRow
AddTableRow.pic=change.gif

DelTableRow.type=TMenuItem
DelTableRow.Text=删除表格行
DelTableRow.Tip=删除表格行
DelTableRow.M=I
DelTableRow.key=
DelTableRow.Action=onDelTableRow
DelTableRow.pic=cancle.gif

PrintSetup.type=TMenuItem
PrintSetup.Text=打印设置
PrintSetup.Tip=打印设置
PrintSetup.M=I
PrintSetup.key=
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PrintClear.Type=TMenuItem
PrintClear.Text=整洁预览
PrintClear.Tip=整洁预览
PrintClear.M=I
PrintClear.key=
PrintClear.Action=onPrintClear
PrintClear.pic=Picture.gif


PrintPageSet.type=TMenuItem
PrintPageSet.Text=页面设置
PrintPageSet.Tip=页面设置
PrintPageSet.M=I
PrintPageSet.key=
PrintPageSet.Action=onPrintPageSet
PrintPageSet.pic=031.gif

PrintShow.type=TMenuItem
PrintShow.Text=打印
PrintShow.Tip=打印
PrintShow.M=I
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

delFixText.type=TMenuItem
delFixText.Text=删除元素
delFixText.Tip=删除元素
delFixText.M=E
delFixText.key=
delFixText.Action=onDelFixText
delFixText.pic=delete.gif

addDLText.type=TMenuItem
addDLText.Text=段落
addDLText.Tip=段落
addDLText.M=E
addDLText.key=
addDLText.Action=onAddDLText
addDLText.pic=Retrieve.gif

addInputMessage.type=TMenuItem
addInputMessage.Text=插入提示组件
addInputMessage.Tip=插入提示组件
addInputMessage.M=I
addInputMessage.key=
addInputMessage.Action=onAddInputMessage
addInputMessage.pic=Retrieve.gif

InsertCaptureObject.Type=TMenuItem
InsertCaptureObject.Text=抓取
InsertCaptureObject.M=R
InsertCaptureObject.Action=onInsertCaptureObject

CaptureDataProperty.Type=TMenuItem
CaptureDataProperty.Text=抓取测试
CaptureDataProperty.M=R
CaptureDataProperty.Action=onCaptureDataProperty

TableProperty.type=TMenuItem
TableProperty.Text=表格属性
TableProperty.Tip=表格属性
TableProperty.M=E
TableProperty.key=
TableProperty.Action=onTableProperty
TableProperty.pic=Retrieve.gif

FontBMenu.type=TMenuItem
FontBMenu.text=粗体
FontBMenu.tip=粗体
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=斜体
FontIMenu.tip=斜体
FontIMenu.pic=I.gif

InsertCheckBoxChooseObject.Type=TMenuItem
InsertCheckBoxChooseObject.Text=选择框
InsertCheckBoxChooseObject.M=R
InsertCheckBoxChooseObject.Action=onInsertCheckBoxChooseObject

CustomScriptDialog.Type=TMenuItem
CustomScriptDialog.Text=自定义脚本对话框
CustomScriptDialog.M=R
CustomScriptDialog.Action=onCustomScriptDialog

InsertPictureObject.Type=TMenuItem
InsertPictureObject.Text=图片
InsertPictureObject.M=R
InsertPictureObject.Action=onInsertPictureObject

InsertNumberChooseObject.Type=TMenuItem
InsertNumberChooseObject.Text=数字选择
InsertNumberChooseObject.M=R
InsertNumberChooseObject.Action=onInsertNumberChooseObject

CutMenu.Type=TMenuItem
CutMenu.Text=剪切
CutMenu.M=v
CutMenu.Action=onCutMenu
cutMenu.Key=Ctrl+X

CopyMenu.Type=TMenuItem
CopyMenu.Text=复制
CopyMenu.M=v
CopyMenu.Action=onCopyMenu
CopyMenu.Key=Ctrl+C

PasteMenu.Type=TMenuItem
PasteMenu.Text=粘贴
PasteMenu.M=v
PasteMenu.Action=onPasteMenu
PasteMenu.Key=Ctrl+V

DeleteMenu.Type=TMenuItem
DeleteMenu.Text=删除
DeleteMenu.M=d
DeleteMenu.Action=onDeleteMenu
DeleteMenu.Key=

DeleteCapMenu.Type=TMenuItem
DeleteCapMenu.Text=删除抓取框
DeleteCapMenu.M=d
DeleteCapMenu.Action=onDeleteCap
DeleteCapMenu.Key=

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

ImageEditMenu.Type=TMenu
ImageEditMenu.Text=图片编辑区
ImageEditMenu.M=A
ImageEditMenu.Item=InsertImageEditMenu;DeleteImageEditMenu;InsertBlockMenu;InsertGLineMenu;|;SizeBlockMenu

InsertImageEditMenu.Type=TMenuItem
InsertImageEditMenu.Text=插入图片编辑区
InsertImageEditMenu.M=R
InsertImageEditMenu.Action=onInsertImageEdit

DeleteImageEditMenu.Type=TMenuItem
DeleteImageEditMenu.Text=删除
DeleteImageEditMenu.M=R
DeleteImageEditMenu.Action=onDeleteImageEdit

InsertBlockMenu.Type=TMenuItem
InsertBlockMenu.Text=插入块
InsertBlockMenu.M=R
InsertBlockMenu.Action=onInsertGBlock

InsertGLineMenu.Type=TMenuItem
InsertGLineMenu.Text=插入线段
InsertGLineMenu.M=R
InsertGLineMenu.Action=onInsertGLine

SizeBlockMenu.Type=TMenu
SizeBlockMenu.Text=调整尺寸
SizeBlockMenu.M=R
SizeBlockMenu.item=SizeBlock1Menu;SizeBlock2Menu;SizeBlock3Menu;SizeBlock4Menu;SizeBlock5Menu;SizeBlock6Menu

SizeBlock1Menu.Type=TMenuItem
SizeBlock1Menu.Text=上对其
SizeBlock1Menu.M=R
SizeBlock1Menu.Action=onSizeBlockMenu|1
SizeBlock1Menu.Key=Ctrl+1

SizeBlock2Menu.Type=TMenuItem
SizeBlock2Menu.Text=下对其
SizeBlock2Menu.M=R
SizeBlock2Menu.Action=onSizeBlockMenu|2
SizeBlock2Menu.Key=Ctrl+2

SizeBlock3Menu.Type=TMenuItem
SizeBlock3Menu.Text=左对其
SizeBlock3Menu.M=R
SizeBlock3Menu.Action=onSizeBlockMenu|3
SizeBlock3Menu.Key=Ctrl+3

SizeBlock4Menu.Type=TMenuItem
SizeBlock4Menu.Text=右对其
SizeBlock4Menu.M=R
SizeBlock4Menu.Action=onSizeBlockMenu|4
SizeBlock4Menu.Key=Ctrl+4

SizeBlock5Menu.Type=TMenuItem
SizeBlock5Menu.Text=同宽
SizeBlock5Menu.M=R
SizeBlock5Menu.Action=onSizeBlockMenu|5
SizeBlock5Menu.Key=Ctrl+5

SizeBlock6Menu.Type=TMenuItem
SizeBlock6Menu.Text=同高
SizeBlock6Menu.M=R
SizeBlock6Menu.Action=onSizeBlockMenu|6
SizeBlock6Menu.Key=Ctrl+6

DIVProperty.Type=TMenuItem
DIVProperty.Text=图层控制
DIVProperty.M=T
DIVProperty.Action=onDIVProperty
DIVProperty.Key=Ctrl+0

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

InsertTextFormat.type=TMenuItem
InsertTextFormat.Text=插入下拉框
InsertTextFormat.Tip=插入下拉框
InsertTextFormat.M=I
InsertTextFormat.key=
InsertTextFormat.Action=onInsertETextFormatObject
InsertTextFormat.pic=check_up_rpt.gif

InsertElement.Type=TMenu
InsertElement.Text=插入元素
InsertElement.M=E
InsertElement.Item=insertElementData;insertHideElement;InsertTemplatePY;

insertElementData.type=TMenuItem
insertElementData.Text=插入数据元素
insertElementData.Tip=插入数据元素
insertElementData.M=I
insertElementData.key=
insertElementData.Action=onInserElement

insertHideElement.type=TMenuItem
insertHideElement.Text=插入隐藏数据元素
insertHideElement.Tip=插入隐藏数据元素
insertHideElement.M=I
insertHideElement.key=
insertHideElement.Action=onInserHideElement

InsertTemplatePY.type=TMenuItem
InsertTemplatePY.Text=插入子模版
InsertTemplatePY.Tip=插入子模版
InsertTemplatePY.M=I
InsertTemplatePY.key=
InsertTemplatePY.Action=onInsertTemplatePY


ClearMenu.Type=TMenuItem
ClearMenu.Text=清空剪贴板
ClearMenu.Tip=清空剪贴板
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

MergerCell.type=TMenuItem
MergerCell.Text=合并单元格
MergerCell.zhText=合并单元格
MergerCell.enText=Merger Cell
MergerCell.Tip=合并单元格
MergerCell.zhTip=合并单元格
MergerCell.enTip=Merger Cell
MergerCell.M=M
MergerCell.key=
MergerCell.Action=onMergerCell
MergerCell.pic=

FormatSet.type=TMenuItem
FormatSet.Text=格式刷选中
FormatSet.Tip=格式刷选中
FormatSet.M=
FormatSet.key=
FormatSet.Action=onFormatSet
FormatSet.pic=

FormatUse.type=TMenuItem
FormatUse.Text=格式刷使用
FormatUse.Tip=格式刷使用
FormatUse.M=
FormatUse.key=
FormatUse.Action=onFormatUse
FormatUse.pic=

AllExpression.Type=TMenu
AllExpression.Text=表达式
AllExpression.M=
AllExpression.Item=InsertExpressionCaptureObject;ExpressionInsert;ExpressionSetting;ExpressionCalculate;

InsertExpressionCaptureObject.Type=TMenuItem
InsertExpressionCaptureObject.Text=插入公式抓取
InsertExpressionCaptureObject.M=
InsertExpressionCaptureObject.Action=onInsertExpressionCapture

ExpressionInsert.type=TMenuItem
ExpressionInsert.Text=插入表达式
ExpressionInsert.Tip=插入表达式
ExpressionInsert.M=
ExpressionInsert.key=
ExpressionInsert.Action=onInsertExpression
ExpressionInsert.pic=

ExpressionSetting.type=TMenuItem
ExpressionSetting.Text=表达式设置
ExpressionSetting.Tip=表达式设置
ExpressionSetting.M=
ExpressionSetting.key=
ExpressionSetting.Action=onSettingExpression
ExpressionSetting.pic=

ExpressionCalculate.type=TMenuItem
ExpressionCalculate.Text=计算表达式
ExpressionCalculate.Tip=计算表达式
ExpressionCalculate.M=
ExpressionCalculate.key=
ExpressionCalculate.Action=onCalculateExpression
ExpressionCalculate.pic=
