package nil.ed.livechat.chatroom.util.sensitive.matcher;

/**
 * @author delin10
 * @since 2019/10/28
 **/
public class MatchResult {
    /**
     * 匹配起始位置
     */
    private int start;

    /**
     * 匹配结束位置(exclusive)
     */
    private int end;

    public MatchResult(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean hasMatch(){
        return matchLength() > 0;
    }

    public int matchLength(){
        return this.end - this.start;
    }
}
