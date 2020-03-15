package nil.ed.livechat.common.enums;

import lombok.AllArgsConstructor;

/**
 * Created at 2020-03-02
 *
 * @author lidelin
 */

@AllArgsConstructor
public enum DeleteFlag implements BaseEnum {
    /**
     * 删除状态
     */
    NORMAL(0, "正常"),
    DELETED(1, "删除");

    private int code;

    private String desc;

    static {
        EnumContext.register(DeleteFlag.class, DeleteFlag.values());
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

}
