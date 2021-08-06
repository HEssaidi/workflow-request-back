package ma.neoxia.workflow.helper;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class MailHelper{
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmailWithAttachment(String to, String template, String taskName, String taskId, String fileUri) throws MessagingException, IOException {
        if(!fileUri.isEmpty()) {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(to);
            helper.setSubject(taskId + " : " + taskName);
            helper.setText(template, true);

            //add file attach second
            InputStream input = new URL(fileUri).openStream();
            ByteArrayDataSource ds = new ByteArrayDataSource(input, "application/pdf"); //to read pdf files
            helper.addAttachment(FilenameUtils.getBaseName(fileUri), ds);
            javaMailSender.send(msg);
        }
        else {
            sendSimpleEmail(to, template, taskName, taskId);
        }
    }

    public void sendSimpleEmail(String to, String template, String taskName, String taskId) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo(to);
        helper.setSubject(taskId + " : " + taskName);
        helper.setText(template, true);
        javaMailSender.send(msg);
    }
}
