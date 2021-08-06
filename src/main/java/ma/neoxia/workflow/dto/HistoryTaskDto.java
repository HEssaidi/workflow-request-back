package ma.neoxia.workflow.dto;

import java.util.Map;

public class HistoryTaskDto {
    private String processId;
    private Map<String, Object> varList;

    public HistoryTaskDto(String processId, Map<String, Object> varList) {
        this.processId = processId;
        this.varList = varList;
    }
    public HistoryTaskDto(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Map<String, Object> getVarList() {
        return varList;
    }

    public void setVarList(Map<String, Object> varList) {
        this.varList = varList;
    }
}
