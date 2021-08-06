package ma.neoxia.workflow.resource;

import ma.neoxia.workflow.bpm.ProcessHelper;
import ma.neoxia.workflow.dto.FileResponseDto;
import ma.neoxia.workflow.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("api/files")
public class FileRessource {
	@Autowired
	public ProcessHelper processHelper;
    @Autowired
    public FileService fileService;

	//we can use PUT
   @PostMapping("/file")
    public FileResponseDto uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("taskId") String taskId) throws Exception {
        return fileService.uploadFile(file,taskId);
    }


    @PostMapping("/fileCompleted/")
    public void completeTaskWithOutVariables(@RequestParam("taskId") String taskId) {
        processHelper.completeTaskWithOutVariables(taskId);
    }




}
