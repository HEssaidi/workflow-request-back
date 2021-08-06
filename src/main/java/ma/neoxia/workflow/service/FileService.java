package ma.neoxia.workflow.service;

import ma.neoxia.workflow.bpm.ProcessHelper;
import ma.neoxia.workflow.dto.FileResponseDto;
import ma.neoxia.workflow.helper.MinioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FileService {
    private static final Logger LOGGER = Logger.getLogger( FileService.class.getName() );

    @Autowired
    public ProcessHelper processHelper;
    @Autowired
    private MinioHelper minioHelper;


    public FileResponseDto uploadFile(MultipartFile file,String taskId) throws Exception {
        String bucketName = "request-docs";

        //store to minio server
        minioHelper.uploadFile(bucketName, file.getOriginalFilename()+"-"+taskId, file.getBytes());
        String fileDownloadUri = minioHelper.fileUri(bucketName,file.getOriginalFilename()+"-"+taskId);

        LOGGER.log( Level.FINE, "add variables to this task {0}", taskId);
        //check assignee before completing task
        String assignee = processHelper.assigneeTask(taskId);
        if (assignee.isEmpty()) {
            return null;
        }
        FileResponseDto fileResponseDto = new FileResponseDto(file.getOriginalFilename(), fileDownloadUri, file.getContentType(), file.getSize());
        //add variables
        Map<String, Object> fileVar = new HashMap<>(0);
        fileVar.put("file", fileResponseDto.getFileDownloadUri());
        processHelper.setTaskVariables(taskId, fileVar);
        return fileResponseDto;
    }

}
