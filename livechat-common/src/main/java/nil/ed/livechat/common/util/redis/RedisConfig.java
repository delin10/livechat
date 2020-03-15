package nil.ed.livechat.common.util.redis;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created at 2020-01-12
 *
 * @author lidelin
 */

public class RedisConfig {

    private String key;

    private String desc;

    public RedisConfig(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKeyWithTemplate(Object...args) {
        return String.format(key, args);
    }

    public String getRawKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public byte[] getRawKeyBytesUtf8() {
        return getRawKeyBytes(StandardCharsets.UTF_8);
    }

    public byte[] getRawKeyBytes(Charset charset) {
        return getRawKey().getBytes(charset);
    }

    public byte[] getKeyBytesWithTemplate(Charset charset, Object...args) {
        String key = getKeyWithTemplate(args);
        return key.getBytes(charset);
    }

    public byte[] getKeyBytesUtf8WithTemplate(Object...args) {
        String key = getKeyWithTemplate(args);
        return key.getBytes(StandardCharsets.UTF_8);
    }

}
