package ma.neoxia.workflow.bpm;


import ma.neoxia.workflow.dto.HistoryTaskDto;
import ma.neoxia.workflow.dto.RequestDto;
import ma.neoxia.workflow.utils.Utils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.rest.dto.task.TaskDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ProcessHelper {
    private static final Logger LOGGER = Logger.getLogger(ProcessHelper.class.getName());
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IdentityService identityService; //getGroups
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;




    public String startProcessMail(RequestDto requestDto) {
        Map<String, Object> variables = new HashMap<>(0);
        variables.put("nom", requestDto.getNom());
        variables.put("prenom", requestDto.getPrenom());
        variables.put("email", requestDto.getEmail());
        variables.put("documentType", requestDto.getDocumentType());
        variables.put("commentaire", requestDto.getComment());
        variables.put("ref", Utils.requestRef(requestDto.getDocumentType()));
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(ProcessVariable.PROCESS_KEY, variables);  //global variable
        return processInstance.getId();
    }

    public Map<String, Object> getTasks() {
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY) //global variable
                .list();
        LOGGER.log( Level.FINE, "tasks size is {0}", tasks.size() );
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                variables.put(task.getId(), taskService.getVariables(task.getId()));
            }
        }
        return variables;
    }

    //filter needs
    public Map<String, Object> getTasksByDocType (String docType, String groupUsers) {
        Map<String, Object> variablesByDocType = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY) //global variable
                .taskCandidateGroup(groupUsers)
                .list();
        LOGGER.log( Level.FINE, "tasks size is {0}", tasks.size() );
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                if(taskService.getVariable(task.getId(), ProcessVariable.DOCUMENT_TYPE).equals(docType)) {
                    variablesByDocType.put(task.getId(), taskService.getVariables(task.getId()));
                }
            }
        }
        return variablesByDocType;
    }
    public Map<String, Object> getTasksByDocTypeAndAssignee (String docType, String assignee) {
        Map<String, Object> variablesByDocType = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY) //global variable
                .taskAssignee(assignee)
                .list();
        LOGGER.log( Level.FINE, "tasks size is {0}", tasks.size() );
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                if(taskService.getVariable(task.getId(), ProcessVariable.DOCUMENT_TYPE).equals(docType)) {
                    variablesByDocType.put(task.getId(), taskService.getVariables(task.getId()));
                }
            }
        }
        return variablesByDocType;
    }


    public Map<String, Object>  getTasksRemarks (String taskId) {
        Map<String, Object> taskRemarks = new HashMap<>(0);
        List<String>  remarkObject = new ArrayList();
        Map<String, Object> taskVariables = taskService.getVariables(taskId);
        if(taskVariables.get(ProcessVariable.AG_REMARK)!=null && !taskVariables.get(ProcessVariable.AG_REMARK).toString().isEmpty()) {
            remarkObject.add(taskVariables.get(ProcessVariable.AG_REMARK).toString());
        }
        if(taskVariables.get(ProcessVariable.DG_REMARK)!=null && !taskVariables.get(ProcessVariable.DG_REMARK).toString().isEmpty()) {
            remarkObject.add(taskVariables.get(ProcessVariable.DG_REMARK).toString());
        }
        taskRemarks.put(taskId, remarkObject);
        return taskRemarks;
    }

    public List<HistoryTaskDto> getCurrentTasks() {
        List<HistoryTaskDto>  historyTaskDtoList = getHsitoryVariables();
        for (HistoryTaskDto historyTaskDto:historyTaskDtoList) {
            Task currentTask = taskService.createTaskQuery()
                    .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                    .processInstanceId(historyTaskDto.getProcessId())
                    .active()
                    .singleResult();
            if (currentTask!= null) {  //current task is defined : not deleted not finished
                historyTaskDto.getVarList().put("taskStatus", Utils.taskStatusUI(TaskDto.fromEntity(taskService.createTaskQuery()   //use taskStatusUI to change task names
                        .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                        .processInstanceId(historyTaskDto.getProcessId())
                        .active()
                        .singleResult()).getName()));
            }
        }
        return historyTaskDtoList;
    }

    //tasks need
    public Map<String, Object> getTasksByAssignee(String assignee){
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(assignee)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                variables.put(task.getId(), taskService.getVariables(task.getId()));
            }
        }
        return variables;
    }

    //new version of Agent tasks
    public Map<String, Object> getAgTasks(String owner){
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .processVariableValueEquals("email", "essaidihajar50@gmail.com")
                .taskName(ProcessVariable.AG_TASK_NAME) //filter by taskName too
                .list();
        //tasks.get
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                    variables.put(task.getId(), taskService.getVariables(task.getId()));
            }
        }
        return variables;
    }
    //no need for taskAssignee because isn't assigned for the first time
    public Map<String, Object> getAGTasks(String owner){
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .taskName(ProcessVariable.AG_TASK_NAME) //filter by taskName
                .list();
        User user;
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
              if(getTaskVariable(task.getId(),"email").equals(owner)) {
                  user =  identityService.createUserQuery().userEmail(owner).singleResult();
                   taskService.setAssignee(task.getId(),user.getId());
                   variables.put(task.getId(), taskService.getVariables(task.getId()));
              }
            }
        }
        return variables;
    }
    //new version of Agent tasks


    public Map<String, Object> getTasksByCandidateUser(String candidateUsers){
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateUser(candidateUsers)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                variables.put(task.getId(), taskService.getVariables(task.getId()));
            }
        }
        return variables;
    }

    public Map<String, Object> getTasksByGroupUsers(String groupUsers){
        Map<String, Object> variables = new HashMap<>(0);
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateGroup(groupUsers)
                .list();
        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                variables.put(task.getId(), taskService.getVariables(task.getId()));
            }
        }
        return variables;
    }

    public String getUserByEmail(String email){
        return identityService.createUserQuery().userEmail(email).singleResult().getId();
    }
    public Group getGroupsByUserEmail(String email){
        String userId = getUserByEmail(email);  //get user id first
        return identityService.createGroupQuery().groupMember(userId).singleResult();  //then group id for given user id
    }

    public Map<String, Object> setTaskVariables(String taskId, Map<String, Object> variables) {
        taskService.setVariables(taskId, variables);
        LOGGER.log( Level.FINE, "tasks variables are {0}", taskService.getVariables(taskId) );
        return taskService.getVariables(taskId);
    }
    public String getTaskVariable(String taskId, String variableName) {
        return String.valueOf(taskService.getVariable(taskId, variableName));
    }

    public void removeTaskVariable(String taskId, String variableName) {
        taskService.removeVariable(taskId, variableName);
    }


    public void completeTaskWithVariables(String taskId, Map<String, Object> variables) {
            taskService.complete(taskId,variables);
    }

    public void completeTaskWithOutVariables(String taskId) {
        LOGGER.log( Level.FINE, "task id completed {0}",  taskId);
        taskService.complete(taskId);
    }
    public void claimTask(String taskId, String userID) {
        taskService.claim(taskId,userID);
    }
    public void unClaimTask(String taskId) {
        taskService.claim(taskId,null);
    }

    public String assigneeTask(String taskId){
        List<IdentityLink> identities = taskService.getIdentityLinksForTask(taskId);
        String id = "";
        for(IdentityLink link : identities)
        {
            //you can check if it is assigned to a group using IdentityLinkType.GROUP
            if (IdentityLinkType.ASSIGNEE.equals(link.getType())){
                id = link.getUserId();
            }
        }
        return id;
    }


    public List<HistoricTaskInstance> getHsitoryByAssignee(){
        return historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .desc()
                .listPage(0,10);
    }



    //new version of historic tasks
    public List<HistoricProcessInstance> getHsitoryByOwner(String owner){
        User user =  identityService.createUserQuery().userEmail(owner).singleResult();
       /*return historyService.createHistoricTaskInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .taskAssignee(user.getId())
                .finished()
                //.orderBy()
                //.desc()
                .processVariableValueEquals("email" ,owner) //mail
                .listPage(0,10);*/
        return historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .orderByProcessInstanceStartTime()
                .desc()
                .variableValueEquals("email" ,owner)  //mail
                .listPage(0,10);
    }
    ///add
    public List<HistoryTaskDto> getHsitoryVarsByProcess(String owner){
        List<HistoricProcessInstance> list = getHsitoryByOwner(owner);
        List<String> idList =  new ArrayList<>() ;
        for (HistoricProcessInstance historicProcessInstance : list) {
            idList.add(historicProcessInstance.getId());
        }
        List<HistoricVariableInstance> hisVarInstList=  historyService.createHistoricVariableInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .processInstanceIdIn(StringUtils.toStringArray(idList))
                .list();

        Set set = new HashSet();
        for (HistoricVariableInstance variableInstance : hisVarInstList) {
            set.add(variableInstance.getProcessInstanceId());
        }
        List<HistoryTaskDto> historyTaskDtoList= new ArrayList<>();
        HistoryTaskDto historyTaskDto;
        SimpleDateFormat formater = new SimpleDateFormat("EEEE, d MMM yyyy 'à' hh:mm:ss");

        for(Object processInstId : set){
            Map<String, Object> vars = new HashMap<>(0);
            List<HistoricVariableInstance> varsByprocId = getHsitoryVarsByProcessInstanceKey(processInstId.toString());
            vars.put("creationTime",formater.format(varsByprocId.get(0).getCreateTime()));
            for (HistoricVariableInstance historicVariableInstance : varsByprocId) {  //iterate over vars for the same id
                vars.put(historicVariableInstance.getName(), historicVariableInstance.getValue());
            }
            historyTaskDto = new HistoryTaskDto(processInstId.toString(), vars);
            historyTaskDtoList.add(historyTaskDto);
        }
        return historyTaskDtoList;
    }

    public List<HistoryTaskDto> getCurrentTasksByOwner(String owner) {
        List<HistoryTaskDto>  historyTaskDtoList = getHsitoryVarsByProcess(owner);
        for (HistoryTaskDto historyTaskDto:historyTaskDtoList) {
            Task currentTask = taskService.createTaskQuery()
                    .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                    .processInstanceId(historyTaskDto.getProcessId())
                    .active()
                    .singleResult();
            if (currentTask!= null) {  //current task is defined : not deleted not finished
                historyTaskDto.getVarList().put("taskStatus", Utils.taskStatusUI(TaskDto.fromEntity(taskService.createTaskQuery()   //use taskStatusUI to change task names
                        .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                        .processInstanceId(historyTaskDto.getProcessId())
                        .active()
                        .singleResult()).getName()));
            }
        }
        return historyTaskDtoList;
    }
    //new version of historic tasks


    public List<HistoryTaskDto> getHsitoryVariables(){
       List<HistoricTaskInstance> list = getHsitoryByAssignee();
        List<String> idList =  new ArrayList<>() ;
        for (HistoricTaskInstance historicTaskInstance : list) {
            idList.add(historicTaskInstance.getProcessInstanceId());
        }
        List<HistoricVariableInstance> hisVarInstList=  historyService.createHistoricVariableInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)  
                .processInstanceIdIn(StringUtils.toStringArray(idList))
                .list();

        Set set = new HashSet();
        for (HistoricVariableInstance variableInstance : hisVarInstList) {
            set.add(variableInstance.getProcessInstanceId());
        }
        List<HistoryTaskDto> historyTaskDtoList= new ArrayList<>();
        HistoryTaskDto historyTaskDto;
        SimpleDateFormat formater = new SimpleDateFormat("EEEE, d MMM yyyy 'à' hh:mm:ss");

        for(Object processInstId : set){
            Map<String, Object> vars = new HashMap<>(0);
            List<HistoricVariableInstance> varsByprocId = getHsitoryVarsByProcessInstanceKey(processInstId.toString());
            vars.put("creationTime",formater.format(varsByprocId.get(0).getCreateTime()));
            for (HistoricVariableInstance historicVariableInstance : varsByprocId) {  //iterate over vars for the same id
                vars.put(historicVariableInstance.getName(), historicVariableInstance.getValue());
            }
            historyTaskDto = new HistoryTaskDto(processInstId.toString(), vars);
            historyTaskDtoList.add(historyTaskDto);
        }
        return historyTaskDtoList;
    }


    public List<HistoricVariableInstance> getHsitoryVars(){
        return historyService.createHistoricVariableInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .list();
    }
    public List<HistoricVariableInstance> getHsitoryVarsByProcessInstanceKey(String processInstanceId){
        System.out.println("Creation time "+historyService.createHistoricVariableInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .processInstanceId(processInstanceId)
                .list().get(0).getCreateTime());
        return historyService.createHistoricVariableInstanceQuery()
                .processDefinitionKey(ProcessVariable.PROCESS_KEY)
                .processInstanceId(processInstanceId)
                .list();
    }



}