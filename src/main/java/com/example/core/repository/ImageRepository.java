package com.example.core.repository;

import com.example.core.model.Image;
import com.example.core.model.Users;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Репозиторий для работы с изображениями.
 * Предоставляет методы для поиска изображений в базе данных.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    /**
     * Поиск изображений по списку ID и пользователю.
     *
     * @param ids  Список ID изображений для поиска.
     * @param user Пользователь, которому принадлежат изображения.
     * @param sort Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByIdInAndUser(List<Integer> ids, Users user, Sort sort);

    /**
     * Поиск изображений по размеру файла и пользователю.
     *
     * @param minSize Минимальный размер файла.
     * @param maxSize Максимальный размер файла.
     * @param user    Пользователь, которому принадлежат изображения.
     * @param sort    Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByFileSizeBetweenAndUser(Long minSize, Long maxSize, Users user, Sort sort);

    /**
     * Поиск изображений по дате загрузки и пользователю.
     *
     * @param startDate Начальная дата для фильтрации изображений.
     * @param endDate   Конечная дата для фильтрации изображений.
     * @param user      Пользователь, которому принадлежат изображения.
     * @param sort      Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByUploadDateBetweenAndUser(Date startDate, Date endDate, Users user, Sort sort);

    /**
     * Поиск изображений по списку ID.
     *
     * @param ids  Список ID изображений для поиска.
     * @param sort Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByIdIn(List<Integer> ids, Sort sort);

    /**
     * Поиск изображений по размеру файла.
     *
     * @param minSize Минимальный размер файла.
     * @param maxSize Максимальный размер файла.
     * @param sort    Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByFileSizeBetween(Long minSize, Long maxSize, Sort sort);

    /**
     * Поиск изображений по дате загрузки.
     *
     * @param startDate Начальная дата для фильтрации изображений.
     * @param endDate   Конечная дата для фильтрации изображений.
     * @param sort      Параметры сортировки.
     * @return Список изображений, соответствующих указанным критериям.
     */
    List<Image> findByUploadDateBetween(Date startDate, Date endDate, Sort sort);

    /**
     * Получение всех изображений.
     *
     * @param sort Параметры сортировки.
     * @return Список всех изображений.
     */
    List<Image> findAll(Sort sort);
}
