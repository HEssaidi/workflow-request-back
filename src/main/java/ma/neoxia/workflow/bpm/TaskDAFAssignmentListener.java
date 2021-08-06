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

@Component(value = "dafGrpListener")
public class TaskDAFAssignmentListener implements TaskListener{
    @Autowired
    public TemplateService templateService;
    @Autowired
    public EmailService emailService;


    private final static Logger LOGGER = Logger.getLogger(TaskDAFAssignmentListener.class.getName());


    @Override
    public void notify(DelegateTask delegateTask) {
        String recipient = ProcessVariable.DAF_MAIL;
        VariableScope variableScope = delegateTask.getExecution();
        String taskId = variableScope.getVariable(ProcessVariable.REFERENCE).toString();
        String taskName = Utils.taskStatusUI(delegateTask.getName());
        String template = "";
        String filePath = "";


        Set<IdentityLink> identityLinks = delegateTask.getCandidates();
        if(identityLinks.isEmpty())
            LOGGER.info("No candidate users or groups exist for this User Task. Id: " + delegateTask.getId());
        else {
            for (IdentityLink identityLink : identityLinks) {
                if (identityLink.getGroupId() != null) {
                    //case we have remarks
                    if (variableScope.getVariable(ProcessVariable.IS_ACCEPTED)!= null) {
                        LOGGER.log(Level.FINE, " accepted {0} ", variableScope.getVariable(ProcessVariable.IS_ACCEPTED));
                        if (variableScope.getVariable(ProcessVariable.IS_ACCEPTED).equals(false)) {
                            //send attachment too
                            filePath = variableScope.getVariable("file").toString();
                            //use remark template
                            template = Utils.formatText(templateService.findByRef("remark").getContent(),delegateTask.getVariables());
                        }  //else for acknowldgment
                    }
                    if (variableScope.getVariable(ProcessVariable.DOC_OK)!= null) {
                        LOGGER.log(Level.FINE," docOK {0} ", variableScope.getVariable(ProcessVariable.DOC_OK));
                        if (variableScope.getVariable(ProcessVariable.DOC_OK).equals(false)) {
                            //use remark template
                            template = Utils.formatText(templateService.findByRef("remark").getContent(),delegateTask.getVariables());
                            //send attachment too
                            filePath = variableScope.getVariable("file").toString();
                        }
                    }
                    else {
                        template = Utils.formatText(templateService.findByRef("new request").getContent(),delegateTask.getVariables());
                        LOGGER.log(Level.FINE,"Task {0} ", delegateTask.getId());
                        LOGGER.log(Level.FINE," template is {0}",  template);
                    }

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
            emailService.sendEmailWithAttachment(recipient,template, taskName, taskId,filePath );
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }
}
