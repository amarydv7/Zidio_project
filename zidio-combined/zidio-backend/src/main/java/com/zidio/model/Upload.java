package com.zidio.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "uploads")
public class Upload implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String filepath;

    @Column(columnDefinition = "TEXT")
    private String headersJson;

    private Long userId;

    public Upload(){}

    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}

    public String getFilename(){return filename;}
    public void setFilename(String filename){this.filename = filename;}

    public String getFilepath(){return filepath;}
    public void setFilepath(String filepath){this.filepath = filepath;}

    public String getHeadersJson(){return headersJson;}
    public void setHeadersJson(String headersJson){this.headersJson = headersJson;}

    public Long getUserId(){return userId;}
    public void setUserId(Long userId){this.userId = userId;}
}
