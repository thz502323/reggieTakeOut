package org.reggie.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.UUID;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.reggie.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : 伪中二
 * @Date : 2023/3/1
 * @Description : 用于文件的上传和下载
 */
@RestController
@RequestMapping("/common")
public class CommonController {

  /**
   * 文件上传管理
   *
   * @param file
   * @return
   */
  @Value("${reggie.base-file-path}")
  private String baseFilePath;

  @PostMapping("/upload")
  Result<String> upload(
      MultipartFile file)
      throws IOException {//注意file要和前端name中值一致，也可以用@RequestPart，MultipartFile是SPring用于得到上传文件

    String filename = file.getOriginalFilename();
    assert filename != null;
    filename = UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));

    file.transferTo(new File(baseFilePath + filename));
    return Result.success(filename);
  }


  @GetMapping("/download")
  void download(String name, HttpServletResponse response) throws IOException {
    //得到输出流，读取文件内容
    FileInputStream fileInputStream = new FileInputStream(new File(baseFilePath + name));
    //通过输出流将图片显示给浏览器
    ServletOutputStream outputStream = response.getOutputStream();
    response.setContentType("image/jpeg");
    try (outputStream; fileInputStream) {//outputStream; fileInputStream不能被赋新值，try-with-resource java9风格
      int len = 0;
      byte[] bytes = new byte[1024];
      while ((len = fileInputStream.read(bytes)) != -1) {
          outputStream.write(bytes,0,len);
          outputStream.flush();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
