package com.example.aws.s3example.controller;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.aws.s3example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController
{
    @Autowired
    FileService service;

    @GetMapping("/list-objects")
    public List<S3ObjectSummary> getBucketList()
    {

        return  service.getBucketList();
    }
    @PostMapping("/upload")
    public String uploadFile(@RequestParam (value="file") MultipartFile file)
    {
        return service.uploadFile(file);
    }
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(String fileName)
    {
        byte[] data =service.download(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok().contentLength(data.length)
                .header("Content-type","application/octet-stream")
                .header("Content-disposition","attachment;filename=\""+"download-file"+"\"")
                .body(resource);
    }
    @GetMapping("/objects-STS")
    public List<S3ObjectSummary> getObjects()
    {
        return service.getObjects();
    }

}
