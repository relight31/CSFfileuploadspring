package com.vttp.fileuploaddemo.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.vttp.fileuploaddemo.models.Post;
import com.vttp.fileuploaddemo.repositories.FileRepo;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@RequestMapping(path = "/upload")
@RestController
public class UploadRESTController {
    Logger logger = Logger.getLogger(UploadRESTController.class.getName());
    @Autowired
    private FileRepo fileRepo;
    @Autowired
    private AmazonS3 s3;

    @GetMapping(path = "{id}")
    public ResponseEntity<byte[]> getPicture(@PathVariable int id) {
        Optional<Post> opt = fileRepo.getPost(id);
        Post post = opt.get();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(post.getMediatype()))
                .body(post.getContent());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitForm(
            @RequestPart("file-upload") MultipartFile myFile,
            @RequestPart String title) {
        JsonObject object = Json.createObjectBuilder()
                .add("content-type", myFile.getContentType())
                .add("name", myFile.getName())
                .add("original-name", myFile.getOriginalFilename())
                .add("size", myFile.getSize())
                .build();
        try {
            if (fileRepo.upload(title, myFile.getContentType(), myFile.getInputStream())) {
                logger.info("Successfully uploaded file " + myFile.getOriginalFilename());
                return ResponseEntity.ok(object.toString());
            }
            logger.info("File upload failed");
            throw new Exception();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(object.toString());
        }
    }

    @PostMapping(path = "/spaces", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> postSpacesUpload(
            @RequestPart MultipartFile myFile,
            @RequestPart String title) {
        // custom metadata
        Map<String, String> myData = new HashMap<>();
        myData.put("title", title);
        myData.put("createdOn", (new Date()).toString());

        // set object metadata
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(myFile.getContentType());
        metadata.setContentLength(myFile.getSize());
        metadata.setUserMetadata(myData);

        try {
            PutObjectRequest putReq = new PutObjectRequest(
                    "filebucket",
                    myFile.getOriginalFilename(),
                    myFile.getInputStream(),
                    metadata);
            putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult putResult = s3.putObject(putReq);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject object = Json.createObjectBuilder()
                .add("content-type", myFile.getContentType())
                .add("name", myFile.getOriginalFilename())
                .add("size", myFile.getSize())
                .add("form-title", title)
                .build();
        return ResponseEntity.ok(object.toString());
    }
}
