package ma.neoxia.workflow.service;

import ma.neoxia.workflow.helper.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public class EmailService {
    @Autowired
    public MailHelper mailHelper;

    public void sendEmailWithAttachment(String to, String template, String taskName, String taskId, String fileUri) throws MessagingException, IOException {
        mailHelper.sendEmailWithAttachment(to, template, taskName, taskId, fileUri);
    }

    public void sendSimpleEmail(String to, String template, String taskName, String taskId) throws MessagingException {
        mailHelper.sendSimpleEmail(to, template, taskName, taskId);
    }
}
