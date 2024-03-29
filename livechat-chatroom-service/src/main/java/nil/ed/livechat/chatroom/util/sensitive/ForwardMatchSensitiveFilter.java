package nil.ed.livechat.chatroom.util.sensitive;

import nil.ed.livechat.chatroom.util.sensitive.matcher.MatchResult;
import nil.ed.livechat.chatroom.util.sensitive.matcher.MatchText;
import nil.ed.livechat.chatroom.util.sensitive.matcher.StringMatchText;
import nil.ed.livechat.chatroom.util.sensitive.matcher.TextMatcher;
import nil.ed.livechat.chatroom.util.sensitive.util.Arrayx;

/**
 * @author delin10
 * @since 2019/10/28
 **/
public class ForwardMatchSensitiveFilter extends AbstractSensitiveFilter {

    public ForwardMatchSensitiveFilter(TextMatcher matcher) {
        this("*", matcher);
    }

    public ForwardMatchSensitiveFilter(String replaceMask, TextMatcher matcher) {
        super(replaceMask, matcher);
    }

    @Override
    protected int handleMatchResult(String sourceText, MatchText text, int cursor, StringBuilder maskText, MatchResult matchResult) {
        if (matchResult == null){
            maskText.append(sourceText, cursor, sourceText.length());
            return 0;
        }
        String mask = Arrayx.repeat(replaceMask, matchResult.matchLength());
        maskText.append(sourceText, cursor, matchResult.getStart());
        maskText.append(mask);
        return mask.length();
    }

    @Override
    protected MatchText getMatchText(String sourceText) {
        return new StringMatchText(sourceText);
    }
}
