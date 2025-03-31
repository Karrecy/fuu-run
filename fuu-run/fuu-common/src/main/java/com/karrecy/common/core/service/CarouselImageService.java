package com.karrecy.common.core.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 轮播图管理
 */
@Service
public class CarouselImageService {

    // 存放轮播图 Base64 字符串
    private final List<String> carouselImages = new CopyOnWriteArrayList<>();
    /**
     * 添加一张新的轮播图图片（不限制 base64 字符串长度）
     * @param base64Content 不带 data URL 前缀的 Base64 编码字符串
     */
    public void addCarouselImage(String base64Content) {
        // 这里默认图片为 JPEG 格式，如果格式可能变化，可将 MIME 类型作为参数传入
        String image = "data:image/jpeg;base64," + base64Content;
        carouselImages.add(image);
    }
    /**
     * 删除指定索引处的轮播图图片
     * @param index 要删除的图片索引（从 0 开始）
     * @throws IllegalArgumentException 如果索引不合法
     */
    public void deleteCarouselImage(int index) {
        if (index < 0 || index >= carouselImages.size()) {
            throw new IllegalArgumentException("图片索引不合法");
        }
        carouselImages.remove(index);
    }
    /**
     * 修改指定索引处的轮播图图片
     * @param index 要修改的图片索引，从0开始
     * @param base64Content 不带 data URL 前缀的 Base64 编码字符串（例如 "xxxx"）
     */
    public void updateCarouselImage(int index, String base64Content) {
        if (index < 0 || index >= carouselImages.size()) {
            throw new IllegalArgumentException("图片索引不合法");
        }
        // 拼接 data URL 前缀，根据实际情况修改 mime 类型
        String newImage = "data:image/jpeg;base64," + base64Content;
        carouselImages.set(index, newImage);
    }
    public List<String> getCarouselImages() {
        return new ArrayList<>(carouselImages);
    }
}