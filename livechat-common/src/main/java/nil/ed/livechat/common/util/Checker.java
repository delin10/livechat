package nil.ed.livechat.common.util;

import java.util.function.Function;

/**
 * @author delin10
 * @since 2019/10/17
 **/
public class Checker {
    public static Number checkPureLongNumber(String numberStr){
        return checkPureNumber(numberStr, Long::parseLong);
    }

    public static Number checkPureNumber(String numberStr, Function<String, Number> parser){
        try {
            return parser.apply(numberStr);
        }catch (NumberFormatException e){
            return null;
        }
    }
}
