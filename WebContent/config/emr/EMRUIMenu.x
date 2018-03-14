<Type=TMenuBar>
UI.Item=File;EditMenu;ImageEditMenu;Insert;InsWidget
UI.button=save;printIndex;PrintClear;|;sign;signCancel;|;applyEdit;cancelEdit;|;ModifyFontCombo;ModifyFontSizeCombo;FontBMenu;FontIMenu;|;InsertCurrentTime;PastePictureObject;|;delFixText;ClearMenu;|;insertFixText;InsertPatInfo;ExpressionCalculate;|;exit

EditMenu.Type=TMenu
EditMenu.Text=编辑
EditMenu.zhText=编辑
EditMenu.enTip=Apply
EditMenu.enText=Apply
EditMenu.M=E
EditMenu.Item=ClearMenu;CutMenu;CopyMenu;PasteMenu;DeleteMenu;delFixText;FixTextProperty;addDLText;

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;|;applyEdit;cancelEdit;|;PrintShow;PrintSetup;PrintClear;printIndex;HongR;LinkPrint;ShowRowIDSwitch;|;exit

Insert.Type=TMenu
Insert.Text=数据
Insert.zhText=数据
Insert.enText=Insert
Insert.M=I
Insert.Item=InsertPY;InsertLCSJ;InsertTemplatePY;InsertPatInfo

InsWidget.Type=TMenu
InsWidget.Text=控件
InsWidget.zhText=控件
InsWidget.enText=Insert Widget
InsWidget.M=I
InsWidget.Item=InsertCurrentTime;|;InsertTable;DelTable;InsertTableRow;AddTableRow;DelTableRow;|;InsertPictureObject;PastePictureObject;|;insertFixText

CutMenu.Type=TMenuItem
CutMenu.Text=剪切
CutMenu.zhText=剪切
CutMenu.enText=Cut
CutMenu.M=T
CutMenu.Action=onCutMenu
cutMenu.Key=Ctrl+X

CopyMenu.Type=TMenuItem
CopyMenu.Text=复制
CopyMenu.zhText=复制
CopyMenu.enText=Copy
CopyMenu.M=C
CopyMenu.Action=onCopyMenu
CopyMenu.Key=Ctrl+C

PasteMenu.Type=TMenuItem
PasteMenu.Text=粘贴
PasteMenu.zhText=粘贴
PasteMenu.enText=Paste
PasteMenu.M=P
PasteMenu.Action=onPasteMenu
PasteMenu.Key=Ctrl+V

DeleteMenu.Type=TMenuItem
DeleteMenu.Text=删除
DeleteMenu.zhText=删除
DeleteMenu.enText=Delete
DeleteMenu.M=D
DeleteMenu.Action=onDeleteMenu
DeleteMenu.Key=Delete

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

printIndex.Type=TMenuItem
printIndex.Text=自定义打印
printIndex.zhText=自定义打印
printIndex.enText=Custom Print
printIndex.Tip=自定义打印
printIndex.zhTip=自定义打印
printIndex.enTip=Save
printIndex.M=S
printIndex.key=Ctrl+P
printIndex.Action=onPrintIndex
printIndex.pic=print.gif

sign.Type=TMenuItem
sign.Text=签名
sign.zhText=签名
sign.enText=sign
sign.Tip=签名
sign.zhTip=签名
sign.enTip=sign
sign.M=T
sign.key=Ctrl+T
sign.Action=onSign
sign.pic=017.gif

signCancel.Type=TMenuItem
signCancel.Text=取消签名
signCancel.zhText=取消签名
signCancel.enText=Cancel Sign
signCancel.Tip=取消签名
signCancel.zhTip=取消签名
signCancel.enTip=Cancel Sign
signCancel.M=T
signCancel.key=Ctrl+P
signCancel.Action=onSignCancel
signCancel.pic=030.gif

submit.Type=TMenuItem
submit.Text=提交
submit.zhText=提交
submit.enText=Commit
submit.Tip=提交
submit.zhTip=提交
submit.enTip=Commit
submit.M=T
submit.key=Ctrl+T
submit.Action=onSubmit
submit.pic=017.gif

submitCancel.Type=TMenuItem
submitCancel.Text=取消提交
submitCancel.zhText=取消提交
submitCancel.enText=Cancel Commit
submitCancel.Tip=取消提交
submitCancel.zhTip=取消提交
submitCancel.enTip=Cancel Commit
submitCancel.M=T
submitCancel.key=Ctrl+P
submitCancel.Action=onSubmitCancel
submitCancel.pic=030.gif

applyEdit.Type=TMenuItem
applyEdit.Text=申请编辑
applyEdit.zhText=申请编辑
applyEdit.enText=Apply Edit
applyEdit.Tip=申请编辑
applyEdit.zhTip=申请编辑
applyEdit.enTip=Apply Edit
applyEdit.M=A
applyEdit.key=
applyEdit.Action=onEdit
applyEdit.pic=unlock.gif

cancelEdit.Type=TMenuItem
cancelEdit.Text=取消编辑
cancelEdit.zhText=取消编辑
cancelEdit.enText=Cancel
cancelEdit.Tip=取消编辑
cancelEdit.zhTip=取消编辑
cancelEdit.enTip=Cancel
cancelEdit.M=N
cancelEdit.key=
cancelEdit.Action=onCancelEdit
cancelEdit.pic=lock.gif

InsertTable.type=TMenuItem
InsertTable.Text=插入表格
InsertTable.zhText=插入表格
InsertTable.enText=Insert Table
InsertTable.Tip=插入表格
InsertTable.zhTip=插入表格
InsertTable.enTip=Insert Table
InsertTable.M=N
InsertTable.key=
InsertTable.Action=onInsertTable
InsertTable.pic=inscon.gif

DelTable.type=TMenuItem
DelTable.Text=删除表格
DelTable.zhText=删除表格
DelTable.enText=Delete Table
DelTable.Tip=删除表格
DelTable.zhTip=删除表格
DelTable.enTip=Delete Table
DelTable.M=D
DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=cancle.gif

InsertTableRow.type=TMenuItem
InsertTableRow.Text=插入表格行
InsertTableRow.zhText=插入表格行
InsertTableRow.enText=Insert Row
InsertTableRow.Tip=插入表格行
InsertTableRow.zhTip=插入表格行
InsertTableRow.enTip=Insert Row
InsertTableRow.M=R
InsertTableRow.key=
InsertTableRow.Action=onInsertTableRow
InsertTableRow.pic=sys.gif

AddTableRow.type=TMenuItem
AddTableRow.Text=追加表格行
AddTableRow.zhText=追加表格行
AddTableRow.enText=Add Row
AddTableRow.Tip=追加表格行
AddTableRow.zhTip=追加表格行
AddTableRow.enTip=Add  Row
AddTableRow.M=A
AddTableRow.key=
AddTableRow.Action=onAddTableRow
AddTableRow.pic=change.gif

DelTableRow.type=TMenuItem
DelTableRow.Text=删除表格行
DelTableRow.zhText=删除表格行
DelTableRow.enText=Delete Row
DelTableRow.Tip=删除表格行
DelTableRow.zhTip=删除表格行
DelTableRow.enTip=Delete Row
DelTableRow.M=B
DelTableRow.key=
DelTableRow.Action=onDelTableRow
DelTableRow.pic=cancle.gif

InsertPY.type=TMenuItem
InsertPY.Text=插入片语
InsertPY.zhText=插入片语
InsertPY.enText=Insert Phrase
InsertPY.Tip=插入片语
InsertPY.zhTip=插入片语
InsertPY.enTip=Insert Phrase
InsertPY.M=P
InsertPY.key=
InsertPY.Action=onInsertPY
InsertPY.pic=sys.gif


InsertTemplatePY.type=TMenuItem
InsertTemplatePY.Text=插入子模版
InsertTemplatePY.zhText=插入子模版
InsertTemplatePY.enText=Insert Template Phrase
InsertTemplatePY.Tip=插入子模版
InsertTemplatePY.zhTip=插入子模版
InsertTemplatePY.enTip=Insert Template Phrase
InsertTemplatePY.M=P
InsertTemplatePY.key=
InsertTemplatePY.Action=onInsertTemplatePY
InsertTemplatePY.pic=sys.gif


InsertLCSJ.type=TMenuItem
InsertLCSJ.Text=临床数据
InsertLCSJ.zhText=临床数据
InsertLCSJ.enText=Clinical data
InsertLCSJ.Tip=临床数据
InsertLCSJ.zhTip=临床数据
InsertLCSJ.enTip=Clinical data
InsertLCSJ.M=C
InsertLCSJ.key=
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=sys.gif

PrintSetup.type=TMenuItem
PrintSetup.Text=打印设置
PrintSetup.zhText=打印设置
PrintSetup.enText=Print Setup
PrintSetup.Tip=打印设置
PrintSetup.zhTip=打印设置
PrintSetup.enTip=Print Setup
PrintSetup.M=S
PrintSetup.key=
PrintSetup.Action=onPrintSetup
PrintSetup.pic=print-2.gif

PrintShow.type=TMenuItem
PrintShow.Text=打印
PrintShow.zhText=打印
PrintShow.enText=Print
PrintShow.Tip=打印
PrintShow.zhTip=打印
PrintShow.enTip=Print
PrintShow.M=P
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

PrintClear.Type=TMenuItem
PrintClear.Text=整洁预览
PrintClear.zhText=整洁预览
PrintClear.enText=preview
PrintClear.Tip=整洁预览
PrintClear.zhTip=整洁预览
PrintClear.enTip=preview
PrintClear.M=N
PrintClear.key=
PrintClear.Action=onPrintClear
PrintClear.pic=Retrieve.gif

HongR.Type=TMenuItem
HongR.Text=宏刷新
HongR.zhText=宏刷新
HongR.enText=preview
HongR.Tip=宏刷新
HongR.zhTip=宏刷新
HongR.enTip=preview
HongR.M=N
HongR.key=
HongR.Action=onHongR
HongR.pic=Redo.gif

exit.Type=TMenuItem
exit.Text=关闭
exit.zhText=关闭
exit.enText=Quit
exit.Tip=关闭
exit.zhTip=关闭
exit.enTip=Quit
exit.M=C
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif

LinkPrint.Type=TMenuItem
LinkPrint.Text=续印
LinkPrint.zhText=续印
LinkPrint.enText=Continue Print
LinkPrint.Enabled=true
LinkPrint.M=T
LinkPrint.Action=onPrintXDDialog

ShowRowIDSwitch.Type=TMenuItem
ShowRowIDSwitch.Text=显示行号开关
ShowRowIDSwitch.zhText=显示行号开关
ShowRowIDSwitch.enText=Show Line No
ShowRowIDSwitch.Enabled=true
ShowRowIDSwitch.M=T
ShowRowIDSwitch.Action=onShowRowIDSwitch

addDLText.type=TMenuItem
addDLText.Text=段落
addDLText.zhText=段落
addDLText.enText=Class
addDLText.Tip=段落
addDLText.zhTip=段落
addDLText.enTip=Class
addDLText.M=E
addDLText.key=
addDLText.Action=onAddDLText

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
SizeBlock2Menu.Text=下对齐
SizeBlock2Menu.M=R
SizeBlock2Menu.Action=onSizeBlockMenu|2
SizeBlock2Menu.Key=Ctrl+2

SizeBlock3Menu.Type=TMenuItem
SizeBlock3Menu.Text=左对齐
SizeBlock3Menu.M=R
SizeBlock3Menu.Action=onSizeBlockMenu|3
SizeBlock3Menu.Key=Ctrl+3

SizeBlock4Menu.Type=TMenuItem
SizeBlock4Menu.Text=右对齐
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

delFixText.type=TMenuItem
delFixText.Text=删除元素
delFixText.Tip=删除元素
delFixText.M=E
delFixText.key=
delFixText.Action=onDelFixText
delFixText.pic=delete.gif

InsertPictureObject.Type=TMenuItem
InsertPictureObject.Text=图片
InsertPictureObject.M=R
InsertPictureObject.Action=onInsertPictureObject

insertFixText.type=TMenuItem
insertFixText.Text=上下标
insertFixText.Tip=上下标
insertFixText.M=I
insertFixText.key=
insertFixText.Action=onInsertMarkText
insertFixText.pic=Edit.gif

FixTextProperty.type=TMenuItem
FixTextProperty.Text=上下标属性
FixTextProperty.Tip=上下标属性
FixTextProperty.M=E
FixTextProperty.key=
FixTextProperty.Action=onMarkTextProperty
FixTextProperty.pic=spreadout.gif

InsertCurrentTime.type=TMenuItem
InsertCurrentTime.Text=当前时间
InsertCurrentTime.Tip=当前时间
InsertCurrentTime.M=T
InsertCurrentTime.key=
InsertCurrentTime.Action=onInsertCurrentTime
InsertCurrentTime.pic=date.gif

PastePictureObject.type=TMenuItem
PastePictureObject.Text=帖图片
PastePictureObject.Tip=帖图片
PastePictureObject.M=P
PastePictureObject.key=
PastePictureObject.Action=onPastePicture
PastePictureObject.pic=Picture.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=清剪贴板
ClearMenu.Tip=清剪贴板
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

SavePatInfo.Type=TMenuItem
SavePatInfo.Text=收藏信息
SavePatInfo.Tip=收藏信息
SavePatInfo.M=v
SavePatInfo.Action=onSavePatInfo
SavePatInfo.Key=
SavePatInfo.pic=002.gif


InsertPatInfo.Type=TMenuItem
InsertPatInfo.Text=插入信息
InsertPatInfo.Tip=插入信息
InsertPatInfo.M=v
InsertPatInfo.Action=onInsertPatInfo
InsertPatInfo.Key=
InsertPatInfo.pic=emr-1.gif

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

FontBMenu.type=TMenuItem
FontBMenu.text=粗体
FontBMenu.tip=粗体
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=斜体
FontIMenu.tip=斜体
FontIMenu.pic=I.gif 

ExpressionCalculate.type=TMenuItem
ExpressionCalculate.Text=计算
ExpressionCalculate.Tip=计算
ExpressionCalculate.M=
ExpressionCalculate.key=
ExpressionCalculate.Action=onCalculateExpression
ExpressionCalculate.pic=014.gif
