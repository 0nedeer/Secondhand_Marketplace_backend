package com.secondhand.marketplace.backend.modules.user.service.impl;

import com.secondhand.marketplace.backend.common.exception.BusinessException;
import com.secondhand.marketplace.backend.common.util.MinioUtil;
import com.secondhand.marketplace.backend.modules.user.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {
    private final MinioUtil minioUtil;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${minio.use-presigned:false}")
    private boolean usePresigned;

    @Override
    public String uploadAvatar(MultipartFile file)  {
        validateImage(file);
        try{
            String objectName = minioUtil.uploadFile(file,"avatars");
            return buildUrl(objectName);
        }catch (Exception e){
            log.error("头像上传失败",e);
            throw new BusinessException("头像上传失败："+e.getMessage());
        }
    }

    @Override
    public String uploadImage(MultipartFile file,String folder){
        validateImage(file);
        try{
            String objectName = minioUtil.uploadFile(file,folder);
            return buildUrl(objectName);
        }catch(Exception e){
            log.error("图片上传失败", e);
            throw new BusinessException("图片上传失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置构建 URL：public用直链，private用预签名
     */
    private String buildUrl(String objectName) throws Exception{
        if(usePresigned){
            return minioUtil.getPresignedUrl(objectName);
        }
        return endpoint+"/"+bucketName+"/"+objectName;
    }

    /**
     * 校验图片文件
     */
    private void validateImage(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new BusinessException("请选择要上传的图片");
        }

        String contentType = file.getContentType();
        if(!contentType.startsWith("image/")){
            throw new BusinessException("只能上传图片文件（JPG/PNG/GIF/WEBP）");
        }

        if(file.getSize()>5*1024*1024){
            throw new BusinessException("图片大小不能超过5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if(originalFilename != null){
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            if(!ext.matches("jpg|jpeg|png|gif|webp")){
                throw new BusinessException("不支持的图片格式");
            }
        }
    }
}
