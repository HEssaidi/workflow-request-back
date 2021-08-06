package ma.neoxia.workflow.service;

import ma.neoxia.workflow.bpm.ProcessHelper;
import ma.neoxia.workflow.bpm.ProcessVariable;
import ma.neoxia.workflow.dto.HistoryTaskDto;
import ma.neoxia.workflow.dto.RemarkDto;
import ma.neoxia.workflow.dto.RequestDto;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.identity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    @Autowired
    public ProcessHelper processHelper;

    public void validateRequestDGStep(String taskId,Boolean docStatus){
        Map<String, Object> documentStatus = new HashMap<>(0);
        documentStatus.put(ProcessVariable.DOC_OK, docStatus);
        //each actor removes hiw own remark
        if (processHelper.getTaskVariable(taskId,ProcessVariable.DG_REMARK)!=null){
            processHelper.removeTaskVariable(taskId, ProcessVariable.DG_REMARK);
        }
        processHelper.completeTaskWithVariables(taskId,documentStatus);
    }
    public void validateRequestAGStep(String taskId,Boolean docStatus){
        Map<String, Object> documentStatus = new HashMap<>(0);
        documentStatus.put(ProcessVariable.IS_ACCEPTED, docStatus);
        //removing remarks
        if (processHelper.getTaskVariable(taskId,ProcessVariable.AG_REMARK)!=null){
            processHelper.removeTaskVariable(taskId, ProcessVariable.AG_REMARK);
        }
        processHelper.completeTaskWithVariables(taskId,documentStatus);
    }

    public void sendRemarkDGStep(RemarkDto remarkDto,Boolean docStatus,String taskId){
        Map<String, Object> documentStatus = new HashMap<>(0);
        documentStatus.put(ProcessVariable.DOC_OK, docStatus);
        documentStatus.put(ProcessVariable.DG_REMARK, remarkDto.getRemark());
        processHelper.completeTaskWithVariables(taskId,documentStatus);
    }
    public void sendRemarkAGStep(RemarkDto remarkDto,Boolean docStatus,String taskId){
        Map<String, Object> documentStatus = new HashMap<>(0);
        documentStatus.put(ProcessVariable.IS_ACCEPTED, docStatus);
        documentStatus.put(ProcessVariable.AG_REMARK, remarkDto.getRemark());
        processHelper.completeTaskWithVariables(taskId,documentStatus);
    }

    public void uploadFileCompleted(String taskId){
            Map<String, Object> var = new HashMap<>(0);
            processHelper.completeTaskWithVariables(taskId, var);
    }

    public String startProcessMail(RequestDto requestDto) {
        return processHelper.startProcessMail(requestDto);
    }

    public Map<String, Object> getTasks() {
        return processHelper.getTasks();
    }

    public Map<String, Object> getTasksByDocType(String docType, String groupUsers) {
        return processHelper.getTasksByDocType(docType, groupUsers);
    }

    public Map<String, Object> getTasksByDocTypeAndAssignee(String docType, String assignee) {
        return processHelper.getTasksByDocTypeAndAssignee(docType, assignee);
    }

    public Map<String, Object> getTasksRemarks(String taskId) {
        return processHelper.getTasksRemarks(taskId);
    }

    public List<HistoryTaskDto> getCurrentTasks() {
        return processHelper.getCurrentTasks();
    }

    public Map<String, Object> getTasksByAssignee(String assignee) {
        return processHelper.getTasksByAssignee(assignee);
    }

    public Map<String, Object> getAgTasks(String owner) {
        return processHelper.getAgTasks(owner);
    }

    public Map<String, Object> getAGTasks(String owner) {
        return processHelper.getAGTasks(owner);
    }

    public Map<String, Object> getTasksByCandidateUser(String candidateUsers) {
        return processHelper.getTasksByCandidateUser(candidateUsers);
    }

    public Map<String, Object> getTasksByGroupUsers(String groupUsers) {
        return processHelper.getTasksByGroupUsers(groupUsers);
    }

    public String getUserByEmail(String email) {
        return processHelper.getUserByEmail(email);
    }

    public Group getGroupsByUserEmail(String email) {
        return processHelper.getGroupsByUserEmail(email);
    }

    public Map<String, Object> setTaskVariables(String taskId, Map<String, Object> variables) {
        return processHelper.setTaskVariables(taskId, variables);
    }

    public String getTaskVariable(String taskId, String variableName) {
        return processHelper.getTaskVariable(taskId, variableName);
    }

    public void removeTaskVariable(String taskId, String variableName) {
        processHelper.removeTaskVariable(taskId, variableName);
    }

    public void completeTaskWithVariables(String taskId, Map<String, Object> variables) {
        processHelper.completeTaskWithVariables(taskId, variables);
    }

    public void completeTaskWithOutVariables(String taskId) {
        processHelper.completeTaskWithOutVariables(taskId);
    }

    public void claimTask(String taskId, String userID) {
        processHelper.claimTask(taskId, userID);
    }

    public void unClaimTask(String taskId) {
        processHelper.unClaimTask(taskId);
    }

    public String assigneeTask(String taskId) {
        return processHelper.assigneeTask(taskId);
    }

    public List<HistoricTaskInstance> getHsitoryByAssignee() {
        return processHelper.getHsitoryByAssignee();
    }

    public List<HistoricProcessInstance> getHsitoryByOwner(String owner) {
        return processHelper.getHsitoryByOwner(owner);
    }

    public List<HistoryTaskDto> getHsitoryVarsByProcess(String owner) {
        return processHelper.getHsitoryVarsByProcess(owner);
    }

    public List<HistoryTaskDto> getCurrentTasksByOwner(String owner) {
        return processHelper.getCurrentTasksByOwner(owner);
    }

    public List<HistoryTaskDto> getHsitoryVariables() {
        return processHelper.getHsitoryVariables();
    }

    public List<HistoricVariableInstance> getHsitoryVars() {
        return processHelper.getHsitoryVars();
    }

    public List<HistoricVariableInstance> getHsitoryVarsByProcessInstanceKey(String processInstanceId) {
        return processHelper.getHsitoryVarsByProcessInstanceKey(processInstanceId);
    }
}
