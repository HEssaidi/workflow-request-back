package ma.neoxia.workflow.bpm;

import ma.neoxia.workflow.service.EmailService;
import ma.neoxia.workflow.service.TemplateService;
import ma.neoxia.workflow.utils.Utils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.delegate.VariableScope;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component(value = "usrListener")
public class TaskUserAssignmentListener implements TaskListener{
    private final static Logger LOGGER = Logger.getLogger(TaskUserAssignmentListener.class.getName());

    @Autowired
    public TemplateService templateService;
    @Autowired
    public EmailService emailService;


    @Override
    public void notify(DelegateTask delegateTask) {

        String assignee = delegateTask.getAssignee();
        VariableScope variableScope = delegateTask.getExecution();
        String taskId = variableScope.getVariable(ProcessVariable.REFERENCE).toString(); //delegateTask.getId();
        String taskName = Utils.taskStatusUI(delegateTask.getName());
        String template ="";
        String filePath ="";

        if (assignee != null) {
            filePath = variableScope.getVariable("file").toString();
            if (variableScope.getVariable(ProcessVariable.DOC_OK).toString().equals("true")) { // confirm to DAF grp by doc validation
                template = Utils.formatText(templateService.findByRef("acknldg").getContent(),delegateTask.getVariables());
                try {
                    emailService.sendEmailWithAttachment(ProcessVariable.DAF_MAIL, template,taskName, taskId, filePath );
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }

            template = Utils.formatText(templateService.findByRef("confirm to agent").getContent(),delegateTask.getVariables());
            LOGGER.log(Level.FINE,"assignee {0} ", assignee);
            LOGGER.log(Level.FINE,"template {0} ", template);

            IdentityService identityService = Context.getProcessEngineConfiguration().getIdentityService();
            User user = identityService.createUserQuery().userId(assignee).singleResult();  // used to get Email

            if (user != null) {
                String recipient = user.getEmail();
                System.out.println("recipient email"+recipient);
                try {
                    emailService.sendEmailWithAttachment(recipient, template,taskName, taskId, filePath );
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            } else {
               LOGGER.log(Level.WARNING,"user {0} has no email address " , assignee);
            }
        } else {
            LOGGER.log(Level.WARNING,"user {0} is not enrolled with identity service. " , assignee);
        }
    }
}
