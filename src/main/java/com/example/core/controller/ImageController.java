//package com.example.core.controller;
//
//import com.example.core.model.Image;
//import com.example.core.service.ImageService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/image")
//@RequiredArgsConstructor
//public class ImageController {
//
//    private final ImageService service;
//
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile[] files,
//                                              @RequestParam String email) {
//        try {
//            String result = service.uploadImage(email, files);
//            return ResponseEntity.ok(result);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Произошла ошибка: " + e.getMessage());
//        }
//    }
//}
