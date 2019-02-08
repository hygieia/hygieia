package com.capitalone.dashboard.api.domain;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * This class will get the details of evidences of a test run
 */
public class Evidence {
    private Long id;
    private String fileName;
    private String fileSize;
    private Date created;
    private String author;
    private URI fileURL;
    //TODO: AS A MACHINE I WANT TO UPLOAD AN IMAGE TO A TESTRUN OR TESTSTEP IF I NEED IT
    private ByteBuffer data;

    public Evidence(Long id,String fileName,String fileSize,Date created,String author,URI fileURL){
        this.id=id;
        this.fileName=fileName;
        this.fileSize=fileSize;
        this.created=created;
        this.author=author;
        this.fileURL=fileURL;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public URI getFileURL() {
        return fileURL;
    }

    public void setFileURL(URI fileURL) {
        this.fileURL = fileURL;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void setData(ByteBuffer data) {
        this.data = data;
    }
}