package nil.ed.livechat.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.service.FileService;
import nil.ed.livechat.common.vo.FileInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

/**
 * Created at 2020-03-10
 *
 * @author lidelin
 */
@RestController
@RequestMapping("/livechat/file")
public class FileController {

    @Resource
    private FileService fileService;

    @PostMapping("/upload")
    public Response<FileInfo> upload(@RequestPart("file") MultipartFile file) {
        return fileService.upload(file);
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response,
            @RequestParam("bucket") String bucket,
            @RequestParam(value = "name", required = false) String name) {
        fileService.download(bucket, name, response);
    }

}
