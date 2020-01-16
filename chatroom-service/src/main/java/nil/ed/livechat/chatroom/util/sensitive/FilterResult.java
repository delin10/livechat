package nil.ed.livechat.chatroom.util.sensitive;

import lombok.ToString;

/**
 * @author delin10
 * @since 2019/10/23
 **/
@ToString
public class FilterResult {
    private String sourceText;

    private String maskText;

    private int maskCount;

    private double maskCharPercent;

    public FilterResult(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getMaskText() {
        return maskText;
    }

    public void setMaskText(String maskText) {
        this.maskText = maskText;
    }

    public int getMaskCount() {
        return maskCount;
    }

    public void setMaskCount(int maskCount) {
        this.maskCount = maskCount;
    }

    public double getMaskCharPercent() {
        return maskCharPercent;
    }

    public void setMaskCharPercent(double maskCharPercent) {
        this.maskCharPercent = maskCharPercent;
    }
}
