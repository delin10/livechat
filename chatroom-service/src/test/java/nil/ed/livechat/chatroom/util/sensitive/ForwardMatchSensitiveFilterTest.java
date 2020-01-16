package nil.ed.livechat.chatroom.util.sensitive;

import nil.ed.livechat.chatroom.util.Arrayx;
import nil.ed.livechat.chatroom.util.sensitive.matcher.AbstractLevelMatcher;
import nil.ed.livechat.chatroom.util.sensitive.matcher.AnotherHashMapMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ForwardMatchSensitiveFilterTest {

    @Test
    public void filter() {
        Function<List<String>, SensitiveFilter> constructor = lib ->
            new ForwardMatchSensitiveFilter(new AnotherHashMapMatcher(AbstractLevelMatcher.MatchLevel.NORMAL, lib));
        testFramework(constructor);
    }

    public void testFramework(Function<List<String>, SensitiveFilter> constructor){
        List<String> words = Arrays.asList("鸡掰","鸡巴","鸡巴鬼", "鸡巴真小","傻逼", "沙雕", "煞笔", "草", "操", "你妈", "尼玛");
        SensitiveFilter filter = constructor.apply(words);
        /*
        bug：重复字符导致状态机循环
         */
        String text = Arrayx.repeat("鸡掰你这个鸡巴鬼，鸡鸡鸡鸡鸡鸡鸡巴真小,真是个傻逼，操你大爷，操尼玛,机掰，及掰，泥马", 1);
        long start = System.currentTimeMillis();
        System.out.println(filter.filter(text));
        System.out.println("匹配实际耗时:" + (System.currentTimeMillis() - start));
    }
}