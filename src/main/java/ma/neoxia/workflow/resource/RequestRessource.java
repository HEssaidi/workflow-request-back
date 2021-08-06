package ma.neoxia.workflow.resource;


import ma.neoxia.workflow.bpm.ProcessHelper;
import ma.neoxia.workflow.dto.HistoryTaskDto;
import ma.neoxia.workflow.dto.RemarkDto;
import ma.neoxia.workflow.dto.RequestDto;
import ma.neoxia.workflow.service.RequestService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.identity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/request")
@CrossOrigin("http://localhost:3000/")
public class RequestRessource {
    private static final Logger LOGGER = Logger.getLogger( RequestRessource.class.getName() );

    @Autowired
    public ProcessHelper processHelper;
    @Autowired
    public RequestService requestService;


    @PostMapping("/validAG/")
    public void validateRequestAGStep(@RequestParam("taskId") String taskId, @RequestParam("docStatus") Boolean docStatus) {
        requestService.validateRequestAGStep(taskId,docStatus);
    }
    @PostMapping("/validDG/")
    public void validateRequestDGStep(@RequestParam("taskId") String taskId, @RequestParam("docStatus") Boolean docStatus) {
        requestService.validateRequestDGStep(taskId,docStatus);
    }

    @PostMapping("/remarkDG/")
    public void sendRemarkDGStep(@RequestParam("remark") RemarkDto remarkDto, @RequestParam("docStatus") Boolean docStatus,@RequestParam("taskId") String taskId) {
        requestService.sendRemarkDGStep(remarkDto,docStatus, taskId);
    }
    @PostMapping("/remarkAG/")
    public void sendRemarkAGStep(@RequestParam("remark") RemarkDto remarkDto, @RequestParam("docStatus") Boolean docStatus,@RequestParam("taskId") String taskId) {
        requestService.sendRemarkAGStep(remarkDto,docStatus, taskId);
    }

    @PostMapping("/uploadFileCompleted/")
    public void uploadFileCompleted(@RequestParam("taskId") String taskId) {
        requestService.uploadFileCompleted(taskId);
    }
    @PostMapping("/")
    public String startProcessMail(RequestDto requestDto) {
        return requestService.startProcessMail(requestDto);
    }

    @GetMapping("/tasks/")
    public Map<String, Object> getTasks() {
        return requestService.getTasks();
    }
    @PostMapping("/task/docType/")
    public Map<String, Object> getTasksByDocType(@RequestParam("docType") String docType,@RequestParam("grp") String groupUsers) {
        return requestService.getTasksByDocType(docType, groupUsers);
    }

    @PostMapping("/some/path/")
    public Map<String, Object> getTasksByDocTypeAndAssignee(@RequestParam("docType") String docType,@RequestParam("assignee") String assignee) {
        return requestService.getTasksByDocTypeAndAssignee(docType, assignee);
    }
    @GetMapping("/tasks/remarks/taskId/{taskId}")
    public Map<String, Object> getTasksRemarks(@PathVariable String taskId) {
        return requestService.getTasksRemarks(taskId);
    }
    @GetMapping("/currentTasks/")
    public List<HistoryTaskDto> getCurrentTasks() {
        return requestService.getCurrentTasks();
    }
    @GetMapping("/assignee/{assignee}")
    public Map<String, Object> getTasksByAssignee(@PathVariable String assignee) {
        return requestService.getTasksByAssignee(assignee);
    }
    @GetMapping("/agTasks/")
    public Map<String, Object> getAgTasks(String owner) {
        return requestService.getAgTasks(owner);
    }
    @GetMapping("/agTask/{owner}")
    public Map<String, Object> getAGTasks(@PathVariable String owner) {
        return requestService.getAGTasks(owner);
    }
    @GetMapping("/tasks/candidateUsers/{candidateUsers}")
    public Map<String, Object> getTasksByCandidateUser(@PathVariable String candidateUsers) {
        return requestService.getTasksByCandidateUser(candidateUsers);
    }
    @GetMapping("/tasks/groupUsers/{groupUsers}")
    public Map<String, Object> getTasksByGroupUsers(@PathVariable String groupUsers) {
        return requestService.getTasksByGroupUsers(groupUsers);
    }
    @GetMapping("/user/{email}")
    public String getUserByEmail(@PathVariable String email) {
        return requestService.getUserByEmail(email);
    }
    @GetMapping("/userGroups/{email}")
    public Group getGroupsByUserEmail(@PathVariable String email) {
        return requestService.getGroupsByUserEmail(email);
    }

    public Map<String, Object> setTaskVariables(String taskId, Map<String, Object> variables) {
        return requestService.setTaskVariables(taskId, variables);
    }

    public String getTaskVariable(String taskId, String variableName) {
        return requestService.getTaskVariable(taskId, variableName);
    }

    public void removeTaskVariable(String taskId, String variableName) {
        requestService.removeTaskVariable(taskId, variableName);
    }

    public void completeTaskWithVariables(String taskId, Map<String, Object> variables) {
        requestService.completeTaskWithVariables(taskId, variables);
    }
    @PostMapping("/taskCompleted/")
    public void completeTaskWithOutVariables(@RequestParam("taskId") String taskId) {
        requestService.completeTaskWithOutVariables(taskId);
    }
    @PostMapping("/claim/")
    public void claimTask(@RequestParam("taskId") String taskId,@RequestParam("userID") String userID) {
        requestService.claimTask(taskId, userID);
    }
    @PostMapping("/unclaim/")
    public void unClaimTask(@RequestParam("taskId") String taskId) {
        requestService.unClaimTask(taskId);
    }
    @GetMapping("/task/taskId/{taskId}")
    public String assigneeTask(@PathVariable String taskId) {
        return requestService.assigneeTask(taskId);
    }
    @GetMapping("/taskhistory/")
    public List<HistoricTaskInstance> getHsitoryByAssignee() {
        return requestService.getHsitoryByAssignee();
    }
    //new version of historic tasks
    @GetMapping("/groups/{owner}")
    public List<HistoricProcessInstance> getHsitoryByOwner(@PathVariable String owner) {
        return requestService.getHsitoryByOwner(owner);
    }
    @GetMapping("/historyProcess/{owner}")
    public List<HistoryTaskDto> getHsitoryVarsByProcess(@PathVariable String owner) {
        return requestService.getHsitoryVarsByProcess(owner);
    }
    @GetMapping("/currentTasks/{owner}")
    public List<HistoryTaskDto> getCurrentTasksByOwner(@PathVariable String owner) {
        return requestService.getCurrentTasksByOwner(owner);
    }
    @GetMapping("/history/")
    public List<HistoryTaskDto> getHsitoryVariables() {
        return requestService.getHsitoryVariables();
    }
    @GetMapping("/varhistory/")
    public List<HistoricVariableInstance> getHsitoryVars() {
        return requestService.getHsitoryVars();
    }

    public List<HistoricVariableInstance> getHsitoryVarsByProcessInstanceKey(String processInstanceId) {
        return requestService.getHsitoryVarsByProcessInstanceKey(processInstanceId);
    }
}
