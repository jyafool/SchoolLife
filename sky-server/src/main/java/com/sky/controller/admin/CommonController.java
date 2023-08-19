package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口：用於文件上傳
 */
@RestController
@RequestMapping ("/admin/common")
@Api (tags = "通用接口")
@Slf4j
public class CommonController {
    
    @Resource
    private AliOssUtil aliOssUtil;
    
    /**
     * 文件上傳
     *
     * @param file
     * @return
     */
    @ApiOperation ("文件上傳")
    @RequestMapping ("/upload")
    public Result<String> upload (MultipartFile file) {
        log.info ("要上傳的文件：{}", file);
        
        try {
            // 獲取原始文件名 ：adfasfasdf.png
            String originalFilename = file.getOriginalFilename ();
            // 獲取文件名後綴
            String suffix = originalFilename.substring (originalFilename.lastIndexOf ("."));
            // 生成uuid配合文件名後綴，是上傳的圖片文件不會重複導致覆蓋
            String newFileName = UUID.randomUUID () + suffix;
            // 上傳圖片文件到阿里oss并返回文件路徑,該路徑可以直接通過瀏覽器直接訪問到
            String filePath = aliOssUtil.upload (file.getBytes (), newFileName);
            // 將其返回給前端做圖片的回顯
            return Result.success (filePath);
        } catch (IOException e) {
            log.info ("文件上傳失敗：{}", e);
        }
        
        return Result.error (MessageConstant.UPLOAD_FAILED);
    }
    
    
}
