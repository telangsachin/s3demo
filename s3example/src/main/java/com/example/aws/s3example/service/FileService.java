package com.example.aws.s3example.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.Credentials;
import software.amazon.awssdk.services.sts.model.StsException;


import java.io.*;

import java.util.List;

import java.util.UUID;

@Component
public class FileService
{
    @Autowired
    AmazonS3 s3Client;
    S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
    String bucketName = "replace with bucket name";

    public List<S3ObjectSummary> getBucketList()
    {
         return s3Client.listObjects("replace with bucket name").getObjectSummaries();
    }

    public String uploadFile(MultipartFile file)
    {
        String fileName = file.getOriginalFilename();
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();
        try
        {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            tm.upload(bucketName, fileName, convFile);
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return "uploaded";
    }

    public byte[] download(String fileName)
    {
        byte[] file = null;
        try
        {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            file = IOUtils.toByteArray(inputStream);
        } catch (Exception e)
        {
            System.out.println("Exception " + e);
        }
        return file;
    }
  public  List<S3ObjectSummary> getObjects()
    {
        List<S3ObjectSummary> objects = null;
        try
        {
            Region region = software.amazon.awssdk.regions.Region.US_EAST_1;
            StsClient stsClient = StsClient.builder()
                    .region(region)
                    .build();
            AssumeRoleRequest roleRequest = AssumeRoleRequest.builder()
                    .roleArn("Replace with Role - ARN")
                    .roleSessionName("Replace with Role Name")
                    .build();
            AssumeRoleResponse roleResponse = stsClient.assumeRole(roleRequest);
            Credentials myCreds = roleResponse.credentials();
            MakingRequestsWithIAMTempCredentials credentials = new MakingRequestsWithIAMTempCredentials();
           objects =  credentials.accessS3WithTempAccess(myCreds.accessKeyId(), myCreds.secretAccessKey(), myCreds.sessionToken());
        } catch (StsException e)
        {
            System.err.println(e.getMessage());
        }
        return objects;
    }

}
