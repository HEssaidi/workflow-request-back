package ma.neoxia.workflow.service;

import ma.neoxia.workflow.bean.Template;
import ma.neoxia.workflow.bpm.ProcessHelper;
import ma.neoxia.workflow.dao.TemplateRepository;
import ma.neoxia.workflow.helper.MailHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class TemplateService {
    private static final Logger LOGGER = Logger.getLogger( TemplateService.class.getName() );
    @Autowired
    public TemplateRepository templateRepository;
    @Autowired
    public ProcessHelper processHelper;
    @Autowired
    public MailHelper mailHelper;

    public Template findByRef(String ref) {
        return templateRepository.findByRef(ref);
    }

    public Template save(Template template) {
        return templateRepository.save(template);
    }

    public void updateTemplate(String content,String ref) {
        templateRepository.updateTemplate(content, ref);
    }

    public List<Template> findAll() {
        return templateRepository.findAll();
    }

}
