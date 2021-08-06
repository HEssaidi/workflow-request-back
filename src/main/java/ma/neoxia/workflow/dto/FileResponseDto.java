package ma.neoxia.workflow.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileResponseDto {

    private String filename;
    private String fileDownloadUri;
    private String fileType;
    private long size;
    private MultipartFile file;
    private String fileUriOrg;

    public FileResponseDto(String filename, String fileDownloadUri, String fileType, long size) {
        super();
        this.filename = filename;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

    public FileResponseDto(String filename, String fileDownloadUri, String fileType, long size, String fileUriOrg) {
        this.filename = filename;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
        this.fileUriOrg = fileUriOrg;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFileDownloadUri() {
        return fileDownloadUri;
    }
    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public long getSize() {
        return size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
    public String getFileUriOrg() { return fileUriOrg; }
    public void setFileUriOrg(String fileUriOrg) { this.fileUriOrg = fileUriOrg; }
}
