package ma.neoxia.workflow.resource;

import ma.neoxia.workflow.bean.Template;
import ma.neoxia.workflow.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/template")
@CrossOrigin("http://localhost:3000/")
public class TemplateRessource {
    @Autowired
    public TemplateService templateService;

    @GetMapping("/ref/{ref}")
    public Template getTemplate(@PathVariable String ref) {
        return templateService.findByRef(ref);
    }

    @PostMapping("/temp/")
    public Template save(@RequestBody Template template) {
        return templateService.save(template);
    }


    @PutMapping("/update/")
    public void updateTemplate(@RequestParam("content") String content,@RequestParam("ref") String ref) {
         templateService.updateTemplate(content,ref);
    }

    @GetMapping("/")
    public List<Template> findAll() {
        return templateService.findAll();
    }

}
