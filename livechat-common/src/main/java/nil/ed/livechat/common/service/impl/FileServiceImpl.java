package nil.ed.livechat.common.service.impl;

import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeMessage;
import nil.ed.livechat.common.service.FileService;
import nil.ed.livechat.common.vo.FileInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final String BASE_DIR = System.getenv("HOME") + "/data/livechat/";

    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Override
    @MethodInvokeLog
    public Response<FileInfo> upload(MultipartFile file){
        String name = FilenameUtils.getBaseName(file.getOriginalFilename());
        String ext = FilenameUtils.getExtension(name);
        byte[] buffer = new byte[1024];

        String relativePath = getRandomFilePath(ext);
        if (StringUtils.isBlank(relativePath)) {
            log.warn("生成文件路径错误");
        } else {
            File f = new File(BASE_DIR + relativePath);
            try (InputStream stream = new BufferedInputStream(file.getInputStream());
                    FileOutputStream out = new FileOutputStream(f)) {
                int n;
                while ((n = stream.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }

                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(name);
                fileInfo.setBucket(relativePath);
                return new NormalResponseBuilder<FileInfo>()
                        .setCodeMessage(ResponseCodeMessage.SUCCESS)
                        .setData(fileInfo)
                        .build();
            } catch (IOException e) {
                log.error("上传文件失败", e);
            }
        }
        return new NormalResponseBuilder<FileInfo>()
                .setCodeMessage(ResponseCodeMessage.UPLOAD_FAILED)
                .build();
    }

    private String getRandomFilePath(String ext) {
        byte[] buffer = new byte[16];
        String date = FORMATTER.format(LocalDateTime.now());
        String dir = date + ThreadLocalRandom.current().nextInt(10000);
        String name = DigestUtils.md5Hex(buffer).substring(5, 20);
        String baseDir = BASE_DIR + dir;
        if (new File(baseDir).mkdirs()) {
            return dir + "/" + name + "." + ext;
        } else {
            return "";
        }
    }

    @Override
    public void download(String bucket, String name, HttpServletResponse response) {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(BASE_DIR, bucket)));
                OutputStream output = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = in.read(buffer)) > 0) {
                output.write(buffer, 0, n);
            }
            if (name != null) {
                response.setHeader("Content-Disposition", "attachment;filename=" + name);
            }
        } catch (IOException e) {
            log.error("下载文件失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public static void main(String[] args) {
        System.out.println(FORMATTER.format(LocalDateTime.now()));
        System.out.println(new File("~").getAbsolutePath());
    }
}
