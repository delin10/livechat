package nil.ed.livechat.chatroom.util.sensitive;

import java.util.List;

import nil.ed.livechat.chatroom.util.sensitive.matcher.MatchResult;
import nil.ed.livechat.chatroom.util.sensitive.matcher.MatchText;
import nil.ed.livechat.chatroom.util.sensitive.matcher.SegmentMatchText;
import nil.ed.livechat.chatroom.util.sensitive.matcher.TextMatcher;
import nil.ed.livechat.chatroom.util.sensitive.seg.Segmenter;
import nil.ed.livechat.chatroom.util.sensitive.util.Arrayx;

/**
 * @author delin10
 * @since 2019/10/28
 **/
public class SegmentSensitiveFilter extends AbstractSensitiveFilter {
    private Segmenter segmenter;

    public SegmentSensitiveFilter(TextMatcher matcher, Segmenter segmenter) {
        super(matcher);
        this.segmenter = segmenter;
    }

    public SegmentSensitiveFilter(String replaceMask, TextMatcher matcher, Segmenter segmenter) {
        super(replaceMask, matcher);
        this.segmenter = segmenter;
    }

    @Override
    protected MatchText getMatchText(String sourceText) {
        List<String> words = segmenter.seg(sourceText);
        System.out.println(words);
        return new SegmentMatchText(words, true);
    }

    @Override
    protected int handleMatchResult(String sourceText, MatchText text, int cursor, StringBuilder maskText, MatchResult matchResult) {
        int end = matchResult == null ? text.count() : matchResult.getStart();
        for (int i = cursor; i < end; ++i){
            maskText.append(text.wordAt(i));
        }

        if (matchResult == null){
            return 0;
        }

        String maskString = Arrayx.repeat(replaceMask, text.wordAt(matchResult.getStart()).length());
        maskText.append(maskString);
        return maskString.length();
    }
}
