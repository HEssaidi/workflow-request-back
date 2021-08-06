package ma.neoxia.workflow.dto;

public class RequestDto {
    private String ref;
    private String nom;
    private String prenom;
    private String owner; //
    private String email;
    private String documentType;
    private String comment;

    public RequestDto(String ref,String nom, String prenom, String owner, String email, String documentType, String comment) {
        this.ref = ref;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.documentType = documentType;
        this.comment = comment;
        this.owner = owner;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}