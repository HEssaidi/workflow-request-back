package ma.neoxia.workflow.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String filePath(String fileUploadDir, String fileName){
        return fileUploadDir+"/"+fileName;
    }

    public static String mailList(List<String> list){
        return String.join(",", list);
    }
    public static String taskStatusUI(String status){
        String statusUI="";
        if(status.equals("Attach doc to the request")){
            statusUI = "Traitement DAF";
        }
        else if(status.equals("Check Doc by DG")){
            statusUI = "Validation Document DG";
        }
        else if(status.equals("Verify doc by Agent")){
            statusUI = "Vérification collaborateur";
        }
        return statusUI;
    }
    public static String requestRef(String documentType){
        String prefix = "";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        LocalDateTime now = LocalDateTime.now();
        if (documentType.equals("Attestation de travail")) {
            prefix = "DAT";
        }
        else if (documentType.equals("Attestation de salaire")){
            prefix = "DAS";
        }
        return prefix+"-"+dtf.format(now);
    }
    public static String formatText(String text, Map<String, Object> values) {
        if (text != null && values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if(entry.getKey() != null && entry.getValue() != null) {
                    text = text.replace("${" + entry.getKey() + "}", entry.getValue().toString());
                }
            }
        }
        return text;
    }
}
