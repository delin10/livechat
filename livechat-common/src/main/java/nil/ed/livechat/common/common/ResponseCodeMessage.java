package nil.ed.livechat.common.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created at 2020-03-08
 *
 * @author lidelin
 */

@Getter
@Setter
@AllArgsConstructor
public class ResponseCodeMessage {
    private int code;
    private String message;

    public static final ResponseCodeMessage SUCCESS = new ResponseCodeMessage(0, "成功");
    public static final ResponseCodeMessage FAILED = new ResponseCodeMessage(-1, "失败");
    public static final ResponseCodeMessage PARAM_VALIDATE_FAILED = new ResponseCodeMessage(1, "参数校验失败");
    public static final ResponseCodeMessage NOT_FOUND = new ResponseCodeMessage(2, "未找到结果");
    public static final ResponseCodeMessage DUPLICATE_FIELD_MESSAGE = new ResponseCodeMessage(3, "%s重复");
    public static final ResponseCodeMessage UPLOAD_FAILED= new ResponseCodeMessage(4, "上传失败");

    public ResponseCodeMessage formatMessage(Object...args) {
        return new ResponseCodeMessage(code, String.format(message, args));
    }

}
