package jdo.sys;

import com.dongyang.config.TConfig;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.NoSuchProviderException;
import com.dongyang.data.TParm;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Part;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import java.util.Date;
import java.security.Security;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.internet.MimeUtility;

/**
 * <p>Title: 邮件工具类</p>
 *
 * <p>Description: 实现邮件发送功能;</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class MailUtil {
    /**
     * 正文内容类型常量:TEXT
     */
    public static final String CONTENT_TYPE_TEXT = "text/plain";

    /**
     * 正文内容类型常量:HTML
     */
    public static final String CONTENT_TYPE_HTML = "text/html";

    private static MailUtil instanceObject;
    //邮件服务器主机地址；
    private String host;
    //SMTP 端口号
    private String port;
    //是否认证
    private boolean auth;
    //是否安全连接；
    private boolean smtpSSL;
    //发送信件公用邮箱
    private String adminAccount;
    //邮箱密码
    private String adminPassword;
    //日志开关true开|false关闭
    private boolean isDebug = false;

    private String isMailDebug="false";

    private MailUtil() {
        host = TConfig.getSystemValue("mail.smtp.host");
        port = TConfig.getSystemValue("mail.smtp.port");
        auth = TConfig.getSystemValue("mail.smtp.auth").equalsIgnoreCase("Yes") ? true : false;
        smtpSSL = TConfig.getSystemValue("mail.smtp.smtpSSL").equalsIgnoreCase(
            "Yes") ? true : false;
        adminAccount = TConfig.getSystemValue("mail.admin.account");
        adminPassword = TConfig.getSystemValue("mail.admin.password");
//        if(isDebug){
//            System.out.println("=======host=========" + host);
//            System.out.println("=======port=========" + port);
//            System.out.println("=======auth=========" + auth);
//            System.out.println("=======smtpSSL=========" + smtpSSL);
//            System.out.println("=======adminAccount=========" + adminAccount);
//            System.out.println("=======adminPassword=========" + adminPassword);
//        }
    }

    public static synchronized MailUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new MailUtil();
        }
        return instanceObject;
    }

    /**
     * 发送邮件
     * @param mailVO MailVO 邮件实体
     * @return boolean
     */
    public TParm sendMail(MailVO mailVO) {
        TParm result = new TParm();
        //获取SMTP会话
        Session smtpSession = this.getSmtpSession();
        Transport smtpTransport = null;
        try {
            smtpTransport = this.getSmtpTransport(smtpSession);
            //连接SMTP
            this.connectSmtpTransport(smtpTransport);
            //构造邮件部件
            Multipart multipart = new MimeMultipart();
            BodyPart textPart = new MimeBodyPart();
            textPart.setDisposition(Part.INLINE);
            //添加内容
            //html类型：CONTENT_TYPE_HTML 或者 纯文本类型"text/plain";
            textPart.setContent(mailVO.getContent(),
                                mailVO.getContentType() + "; charset=" +
                                mailVO.getCharset());
            multipart.addBodyPart(textPart);
            //添加附件
            //假如有附件，循环加入附件(url附件，和byte[]),现只支持byte[]类型附件；
            if (mailVO.getAttachByteArrays() != null &&
                mailVO.getAttachByteArrays().size() > 0) {
                DataSource dataSource = null;
                //循环获取，对应的附件加入到邮件体；
                for (MailAttachment mailAttachment : mailVO.getAttachByteArrays()) {
                    BodyPart attachmentPart = new MimeBodyPart();
                    //假如附件有数据，加入到邮件体；
                    if (mailAttachment.getData() != null &&
                        mailAttachment.getData().length > 0) {
                        dataSource = new MailByteArrayDataSource(mailAttachment.
                            getData(), "application/octet-stream");
                        attachmentPart.setDataHandler(new DataHandler(
                            dataSource));
                        attachmentPart.setFileName(MimeUtility.encodeText(
                            mailAttachment.getName()));
                        multipart.addBodyPart(attachmentPart);

                    }
                }
            }

            Message message = new MimeMessage(smtpSession);
            //设置发件人;
            if (mailVO.getFrom() != null && !mailVO.getFrom().equals("")) {
                message.setFrom(new InternetAddress(mailVO.getFrom()));

            }
            else {
                //假如发件人为空用邮件管理账号，发送邮件；
                message.setFrom(new InternetAddress(adminAccount));
            }

            //设置收件人；
            if (mailVO.getToAddress() != null &&
                mailVO.getToAddress().size() > 0) {
                InternetAddress[] toMailAddress = new InternetAddress[mailVO.
                    getToAddress().size()];
                for (int i = 0; i < mailVO.getToAddress().size(); i++) {
                    toMailAddress[i] = this.createInternetAddress(mailVO.
                        getToAddress().get(i));
                    //System.out.println("toMailAddress["+i+"]"+toMailAddress[i]);
                }
                message.setRecipients(Message.RecipientType.TO, toMailAddress);

            }
            //设置抄送人；
            if (mailVO.getCcAddress() != null &&
               mailVO.getCcAddress().size() > 0) {
               InternetAddress[] ccMailAddress = new InternetAddress[mailVO.
                   getCcAddress().size()];
               for (int i = 0; i < mailVO.getCcAddress().size(); i++) {
                   ccMailAddress[i] = this.createInternetAddress(mailVO.
                       getCcAddress().get(i));
                   //System.out.println("toMailAddress["+i+"]"+toMailAddress[i]);
               }
               message.setRecipients(Message.RecipientType.CC, ccMailAddress);

           }
            //假如存在接收邮箱，则进行邮件发送;
            if (message.getAllRecipients() != null &&
                message.getAllRecipients().length > 0) {
                //设置主题;
                message.setSubject(mailVO.getSubject());
                message.setContent(multipart);
                message.setSentDate(new Date());
                message.saveChanges();
                smtpTransport.sendMessage(message, message.getAllRecipients());

            }

        }
        catch (NoSuchProviderException ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;
        }
        catch (MessagingException ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;
        }
        catch (Exception ex) {
            if (isDebug) {
                ex.printStackTrace();
            }
            result.setErrCode( -1);
            return result;

        }
        finally {
            //关闭连接；
            try {
                this.closeSmtpTransport(smtpTransport);
            }
            catch (MessagingException ex) {
                if (isDebug) {
                    ex.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * 构造邮件地址
     * @param email String
     * @return InternetAddress
     */
    private InternetAddress createInternetAddress(String email) throws
        AddressException {
        InternetAddress address = new InternetAddress(email);
        // 返回address
        return address;
    }

    /**
     * 获取SMTP属性
     * @return Properties
     */
    private Properties getSmtpProperties() {
        Properties smtpProperties = System.getProperties();
        smtpProperties.put("mail.smtp.host", host);
        smtpProperties.put("mail.smtp.port", port);
        smtpProperties.put("mail.smtp.auth", auth ? "true" : "false");
        //true为调试模式，正式运行时关闭false;
        smtpProperties.put("mail.debug", isMailDebug);
        //是否安全连接；
        if (smtpSSL) {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            smtpProperties.setProperty("mail.smtp.socketFactory.class",
                                       "javax.net.ssl.SSLSocketFactory");
            smtpProperties.setProperty("mail.smtp.socketFactory.fallback",
                                       "false");
            smtpProperties.setProperty("mail.smtp.socketFactory.port", port);
        }

        return smtpProperties;
    }

    /**
     * 获取SMTP会话；
     * @return Session
     */
    private Session getSmtpSession() {
        Session smtpSession = null;
        Properties smtpProperties = this.getSmtpProperties();

        if (auth) {
            smtpSession = Session.getInstance(smtpProperties,
                                              new
                                              MyAuthenticator(adminAccount,
                adminPassword));
        }
        else {
            smtpSession = Session.getInstance(smtpProperties, null);
        }
        return smtpSession;
    }

    /**
     * 获取smtp连接
     * @param smtpSession Session
     * @return Transport
     * @throws NoSuchProviderException
     */
    private Transport getSmtpTransport(Session smtpSession) throws
        NoSuchProviderException {

        Transport smtpTransport = null;
        //通过smtp协议连接
        smtpTransport = smtpSession.getTransport("smtp");
        return smtpTransport;
    }

    /**
     *  连接SMTP
     * @param smtpTransport Transport
     */
    private void connectSmtpTransport(Transport smtpTransport) throws
        MessagingException {
        if (!smtpTransport.isConnected()) {
            if (auth) {
                smtpTransport.connect();
            }
            else {
                smtpTransport.connect(host, Integer.valueOf(port), adminAccount,
                                      adminPassword);
            }
        }
    }

    /**
     * 关闭SMTP连接
     * @param smtpTransport Transport
     * @throws MessagingException
     */
    private void closeSmtpTransport(Transport smtpTransport) throws
        MessagingException {
        if (smtpTransport.isConnected()) {
            smtpTransport.close();
        }
    }


    /**
     * 继承javaMail的认证器,在构造mail的Session时使用
     *
     * 覆写方法getPasswordAuthentication()
     */
    class MyAuthenticator
        extends Authenticator {

        private String userName;

        private String passWord;

        public MyAuthenticator(String userName, String passWord) {
            this.userName = userName;
            this.passWord = passWord;
        }

        /**
         * 覆写父类的getPasswordAuthentication方法
         *
         * @Override
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, passWord);
        }
    }


}
