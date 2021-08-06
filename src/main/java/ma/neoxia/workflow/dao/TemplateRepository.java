package ma.neoxia.workflow.dao;

import ma.neoxia.workflow.bean.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    public Template findByRef(String ref);
    public Template save(Template template);

    @Transactional
    @Modifying
    @Query(value="UPDATE template SET content = :content WHERE ref = :ref ",nativeQuery=true)
    public void updateTemplate(@Param("content") String content, @Param("ref") String ref);
}
