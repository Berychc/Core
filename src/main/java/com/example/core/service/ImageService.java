package com.example.core.service;

import com.example.core.model.Event;
import com.example.core.model.Image;
import com.example.core.repository.ImageRepository;
import com.example.core.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ImageService {
    @Autowired
    private ImageRepository imageRepository; // Репозиторий для работы с изображениями

    @Autowired
    private RabbitTemplate rabbitTemplate; // Интеграция с RabbitMQ

//    public String uploadImages(MultipartFile[] files, String email) {
//        if (files == null || files.length == 0) {
//            throw new IllegalArgumentException("Не выбраны файлы для загрузки");
//        }
//
//        long totalSize = 0;
//        List<Image> savedImages = new ArrayList<>();
//
//        for (MultipartFile file : files) {
//            String originalFilename = file.getOriginalFilename();
//            if (originalFilename == null || !isValidImageFormat(originalFilename)) {
//                throw new IllegalArgumentException(String.format("Некорректный формат файла: %s", originalFilename));
//            }
//
//            try {
//                long fileSize = file.getSize();
//                if (fileSize > 10 * 1024 * 1024) { // Ограничение на размер 10 MB
//                    throw new IllegalArgumentException(String.format("Файл слишком большой: %s", originalFilename));
//                }
//
//                Image image = new Image();
//                image.setMediaType(file.getContentType());
//                image.setData(file.getBytes());
//                image.setFileSize(fileSize);
//                Image savedImage = imageRepository.save(image);
//                savedImages.add(savedImage);
//                totalSize += fileSize;
//
//            } catch (IOException e) {
//                throw new RuntimeException(String.format("Ошибка при загрузке файла: %s", originalFilename), e);
//            }
//        }
//        String eventMessage = "Загружены изображения, общий размер: " + totalSize + " байт";
//        Event event = new Event(email, eventMessage);
//        rabbitTemplate.convertAndSend("mailQueue", event);
//
//        return "Изображения успешно загружены";
//
////    }
////
////    private boolean isValidImageFormat(String filename) {
////        return filename != null && (filename.endsWith(".jpg") || filename.endsWith(".png"));
////    }
//
//    public byte[] getImage(Integer id) {
//        return imageRepository.findById(id).orElseThrow(() -> new RuntimeException("Image not found")).getData();
//    }
}
