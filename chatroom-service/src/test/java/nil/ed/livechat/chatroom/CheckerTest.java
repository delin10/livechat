package nil.ed.livechat.chatroom;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author delin10
 * @since 2019/10/17
 **/
public class CheckerTest {
    String s1 = "1234354";
    String s2 = "3443433";
    String s3 = "322232a";

    @Test
    public void testPattern(){
        final Pattern pattern = Pattern.compile("[1-9][0-9]*");
        testFramework(i -> {
            boolean match = pattern.matcher(s3).matches();
            if (match){
                Long r = Long.parseLong(s3);
            }
        });
    }

    @Test
    public void testParse(){
        testFramework(i -> {
            Long r = Long.parseLong(s3);
        });
    }

    public void testFramework(IntConsumer consumer){
        try {
            IntStream.range(0, 1000000)
                    .forEach(consumer);
        }catch (NumberFormatException e){ }
    }
}
