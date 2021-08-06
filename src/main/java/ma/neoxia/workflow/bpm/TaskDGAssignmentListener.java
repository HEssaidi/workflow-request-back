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
import org.camunda.bpm.engine.task.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component(value = "dgGrpListener")
public class TaskDGAssignmentListener implements TaskListener {
    @Autowired
    public TemplateService templateService;
    @Autowired
    public EmailService emailService;

    private final static Logger LOGGER = Logger.getLogger(TaskDGAssignmentListener.class.getName());


    @Override
    public void notify(DelegateTask delegateTask) {
        String recipient = ProcessVariable.DG_MAIL;
        VariableScope variableScope = delegateTask.getExecution();
        String taskId = variableScope.getVariable(ProcessVariable.REFERENCE).toString(); //delegateTask.getId();
        String taskName = Utils.taskStatusUI(delegateTask.getName());
        String template = "";
        String filePath = "";


        Set<IdentityLink> identityLinks = delegateTask.getCandidates();
        if(identityLinks.isEmpty())
            LOGGER.info("No candidate users or groups exist for this User Task. Id: " + delegateTask.getId());
        else {
            for (IdentityLink identityLink : identityLinks) {
                if (identityLink.getGroupId() != null) {  // DG group
                    template = Utils.formatText(templateService.findByRef("check request").getContent(),delegateTask.getVariables());
                    filePath = variableScope.getVariable("file").toString();
                    LOGGER.log(Level.FINE,"Task {0} ", delegateTask.getId());
                    LOGGER.log(Level.FINE," template is {0}",  template);

                }
            }
        }

        if (delegateTask.getAssignee() != null && !delegateTask.getAssignee().isEmpty()){
            String assignee = delegateTask.getAssignee();
            LOGGER.log(Level.FINE," assignee is {0}",  assignee);
            LOGGER.log(Level.FINE," claimed task  {0}",  taskId);
            // we should inform both users : grp and assignee
            // group template to inform grp by the claiming
            template = Utils.formatText(templateService.findByRef("claiming confirm").getContent(),delegateTask.getVariables());

            // user email process to inform usr by the claiming
            IdentityService identityService = Context.getProcessEngineConfiguration().getIdentityService();
            User user = identityService.createUserQuery().userId(assignee).singleResult();
            String usrClaimedTemplate = templateService.findByRef("user claiming").getContent();
            try {
                emailService.sendSimpleEmail(user.getEmail(),usrClaimedTemplate, taskName, taskId);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            LOGGER.log(Level.FINE," templateService inside assignee  {0}",  templateService);
        }
        try {
            emailService.sendEmailWithAttachment(recipient,template, taskName, taskId,filePath);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
