package nil.ed.livechat.common.service;

import javax.servlet.http.HttpServletResponse;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.vo.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

public interface FileService {

    /**
     * 上传文件
     * @param file 文件
     * @return 文件信息
     */
    Response<FileInfo> upload(MultipartFile file);

    /**
     * 下载文件
     * @param response 响应
     * @param bucket 文件所在路径图
     * @param name 指定文件名称
     */
    void download(String bucket, String name, HttpServletResponse response);
}
