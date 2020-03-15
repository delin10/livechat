package nil.ed.livechat.chatroom;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.junit.Test;

/**
 * @author delin10
 * @since 2019/10/23
 **/
public class PinyinTest {
    @Test
    public void test() throws BadHanyuPinyinOutputFormatCombination {
        String str = "你是谁";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        String pinyinStr = PinyinHelper.toHanYuPinyinString(str, defaultFormat, " ", true);
        System.out.println(pinyinStr);
    }
}
