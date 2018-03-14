UI.item=File;Edit;Window
UI.button=Save;|;SelectObjectToolButton;|;Undo;Redo;|;Delete;|;&
	ModifyText;ModifyFont;ModifyFontSize;|;AlignmentLeft;AlignmentCenter;AlignmentRight;FontTypeB;FontTypeI;|&;&
	Close;|;Refurbish;|;Exit

File.type=TMenu
File.text=文件
File.M=F
File.item=Save;Close;|;Exit

Save.type=TMenuItem
Save.text=保存
Save.tip=保存
Save.M=S
Save.key=Ctrl+S
Save.Action=onSave
Save.pic=save.gif

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
Edit.item=Undo;Redo;|;Delete;|;Preview

Undo.type=TMenuItem
Undo.text=撤销
Undo.tip=撤销
Undo.M=U
Undo.key=Ctrl+Z
Undo.Action=onUndo
Undo.pic=Undo.gif

Redo.type=TMenuItem
Redo.text=恢复
Redo.tip=恢复
Redo.M=R
Redo.key=Shift+Ctrl+Z
Redo.Action=onRedo
Redo.pic=Redo.gif

Delete.type=TMenuItem
Delete.text=删除
Delete.tip=删除
Delete.M=D
Delete.key=delete
Delete.Action=onDelete
Delete.pic=Delete.gif

ModifyText.type=TTextField
ModifyText.tip=编辑文字
ModifyText.PreferredWidth=80
ModifyText.Enabled=N

ModifyFont.type=TFontCombo
ModifyFont.PreferredWidth=80
ModifyFont.SelectedAction=onModifyFont

ModifyFontSize.type=TFontSizeCombo
ModifyFontSize.PreferredWidth=40
ModifyFontSize.SelectedAction=onModifyFontSize

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

FontTypeB.type=TMenuItem
FontTypeB.text=加粗
FontTypeB.tip=加粗
FontTypeB.SelectedMode=Y
FontTypeB.Action=onFontTypeB
FontTypeB.pic=B.gif

FontTypeI.type=TMenuItem
FontTypeI.text=倾斜
FontTypeI.tip=倾斜
FontTypeI.Action=onFontTypeI
FontTypeI.SelectedMode=Y
FontTypeI.pic=I.gif

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

SelectObjectToolButton.type=TComboButton
SelectObjectToolButton.ColumnCount=2
SelectObjectToolButton.RowCount=3
SelectObjectToolButton.Button=SOTB_ARROW
SelectObjectToolButton.Items=SOTB_ARROW,选择对象,Arrow.gif,onSOTBArrow;&
			     SOTB_TEXT,创建文本,Text.gif,onSOTBText;&
			     SOTB_COLUMN,创建列,Column.gif,onSOTBColumn;&
			     SOTB_LINE,创建线,Line.gif,onSOTBLine;&
			     SOTB_PICTURE,创建图片,Picture.gif,onSOTBPicture;&
			     SOTB_BUTTON,创建按钮,Button.gif,onSOTBButton;&
			     SOTB_GROUP,创建组,Group.gif,onSOTBGroup
