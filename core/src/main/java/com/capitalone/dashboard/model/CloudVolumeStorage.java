package com.capitalone.dashboard.model;


import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "cloud_volume")
public class CloudVolumeStorage extends BaseModel{
    @Indexed
    private String volumeId;

    @Indexed
    private String accountNumber;
    private String status;
    private long creationDate;
    private int size;
    private List<NameValue> tags = new ArrayList<>();
    private boolean encrypted;
    private String type;
    private String zone;
    private List<String> attachInstances = new ArrayList<>();


    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<NameValue> getTags() {
        return tags;
    }


    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public List<String> getAttachInstances() {
        return attachInstances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CloudVolumeStorage that = (CloudVolumeStorage) o;

        return volumeId != null ? volumeId.equals(that.volumeId) : that.volumeId == null;

    }

    @Override
    public int hashCode() {
        return volumeId != null ? volumeId.hashCode() : 0;
    }
}
