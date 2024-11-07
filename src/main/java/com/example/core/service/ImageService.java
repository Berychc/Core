package com.example.core.service;

import com.example.core.model.Event;
import com.example.core.model.Image;
import com.example.core.repository.ImageRepository;
import com.example.core.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final RabbitTemplate rabbitTemplate;
    private final ImageRepository imageRepository; // Репозиторий для сохранения изображений
    private final UsersRepository usersRepository; // Для получения email пользователя по id

    public String uploadImage(String userEmail, MultipartFile[] files) {
        long totalSize = 0;

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (!(originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png"))) {
                return String.format("Некорректный формат файла: %s", originalFilename);
            }

            try {
                if (file.getSize() > 10 * 1024 * 1024) {
                    return String.format("Файл слишком большой: %s", originalFilename);
                }

                Image image = new Image();
                image.setMediaType(file.getContentType());
                image.setData(file.getBytes());
                image.setFileSize(file.getSize()); // сохраняем размер файла
                imageRepository.save(image);

                totalSize += file.getSize();
            } catch (IOException e) {
                return String.format("Ошибка при загрузке файла: %s", originalFilename);
            }
        }

        Event event = new Event(userEmail, "Загружены изображения, общий размер: " + totalSize + " байт");
        rabbitTemplate.convertAndSend("mailQueue", event);

        return "Файлы загружены!";
    }


    public byte[] getImage(Integer id) {
        return imageRepository.findById(id).orElseThrow(RuntimeException::new).getData();
    }

}
