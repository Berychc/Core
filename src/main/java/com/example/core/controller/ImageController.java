package com.example.core.controller;

import com.example.core.model.Image;
import com.example.core.repository.ImageRepository;
import com.example.core.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService service;

    @Autowired
    private ImageRepository repository;
}
