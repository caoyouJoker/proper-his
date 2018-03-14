package com.javahis.ui.inv;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTextField;
import com.dongyang.util.FileTool;

public class ImportPictureControl extends TControl {

	private String pack_code = "";//����
	private String fileName = "";//�ϴ����ļ���
	private String file_path = "";//�ϴ��ļ�·��
	private TTextField filePath;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		this.filePath = this.getTextField("FILE_PATH");
		Object obj = this.getParameter();
       if(obj!=null){
           TParm parm = (TParm)obj;
           this.pack_code = parm.getValue("PACK_CODE");
       }
	}
	/**
	 * ѡ��ͼƬ
	 */
	public void onOpen(){
		//��ʼ���ļ�ѡ���
    	JFileChooser fDialog = new JFileChooser();
    	//�����ļ�ѡ���ı��� 
    	fDialog.setDialogTitle("��ѡ��Ҫ�ϴ���ͼƬ");
    	//����ѡ���
    	int returnVal = fDialog.showOpenDialog(null);
    	// �����ѡ�����ļ�
    	if(JFileChooser.APPROVE_OPTION == returnVal){
    		fileName = fDialog.getSelectedFile().getName();
    		String name[] = fileName.split("\\.");
    		if(!name[1].equalsIgnoreCase("jpg")){
    			this.messageBox("ͼƬ����ӦΪjpg��ʽ");
    			return;
    		}
    		file_path = fDialog.getSelectedFile().toString();
    		filePath.setText(file_path);
    	}
	}
	
	/**
	 * �ϴ�ͼƬ
	 */
	public void onUpload(){
		this.sendpic(this.file_path);
	}
	
	/**
	 * ������Ƭ
	 * @param localFileName
	 * 					String �ϴ����ļ�������
	 */
	public void sendpic(String localFileName) {
		String dir = "";
		String photoName = this.pack_code + ".jpg";
		File file = new File(localFileName);
		try {
			dir = TIOM_FileServer.getPath("OpeInfPIC.LocalPath");
			File filepath = new File(dir);
			if (!filepath.exists())
				filepath.mkdirs();
			BufferedImage input = ImageIO.read(file);
			Image scaledImage = input.getScaledInstance(input.getWidth(), input.getHeight(),
					Image.SCALE_DEFAULT);
			BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(),
					BufferedImage.TYPE_INT_BGR);
			output.createGraphics().drawImage(scaledImage, 0, 0, null); // ��ͼ
			ImageIO.write(output, "jpg", new File(dir + photoName));
			sendpic();
		} catch (Exception e) {
		}
	}
	
	/**
	 * ������Ƭ
	 * 
	 */
	public void sendpic() {
		String photoName = this.pack_code + ".jpg";
		String dir = TIOM_FileServer.getPath("OpeInfPIC.LocalPath");
		String localFileName = dir + photoName;
		try {
			File file = new File(localFileName);
			byte[] data = FileTool.getByte(localFileName);
			if (file.exists()) {
				new File(localFileName).delete();
			}
			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
			dir = root + dir;
			TIOM_FileServer.mkdir(TIOM_FileServer.getSocket(), dir);
			TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data);
			this.messageBox("�ϴ��ɹ�");
			this.closeWindow();
		} catch (Exception e) {
			System.out.println("e::::" + e.getMessage());
		}
//		this.viewPhoto(this.pack_code);

	}
	
	/**
	 * ��ʾ��Ƭ
	 * 
	 * @param packCode
	 *            String ����
	 */
//	public void viewPhoto(String packCode) {
//
//		String photoName = packCode + ".jpg";
//		String fileName = photoName;
//		try {
//			TPanel viewPanel = (TPanel) getComponent("VIEW_PANEL");
//			String root = TIOM_FileServer.getRoot();
//			String dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
//			dir = root + dir ;
//			byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
//					dir + fileName);
//			if (data == null) {
//				viewPanel.removeAll();
//				return;
//			}
//			double scale = 0.5;
//			boolean flag = true;
//			Image image = ImageTool.scale(data, scale, flag);
////			 ImageIcon image = TIOM_AppServer.getImage(dir+fileName);
//			Pic pic = new Pic(image);
//			pic.setSize(viewPanel.getWidth(), viewPanel.getHeight());
//			pic.setLocation(0, 0);
//			viewPanel.removeAll();
//			viewPanel.add(pic);
//			pic.repaint();
//		} catch (Exception e) {
//		}
//	}
	
//	class Pic extends JLabel {
//		Image image;
//
//		public Pic(Image image) {
//			this.image = image;
//		}
//
//		public void paint(Graphics g) {
//			g.setColor(new Color(161, 220, 230));
//			g.fillRect(4, 15, 100, 100);
//			if (image != null) {
//				g.drawImage(image, 4, 15, null);
//
//			}
//		}
//	}
	public TTextField getTextField(String tag){
		return (TTextField) this.getComponent(tag);
	}
}
