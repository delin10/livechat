package nil.ed.livechat.common.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created at 2020-03-02
 *
 * @author lidelin
 */
public class EnumContext {

    private static Map<Class<? extends BaseEnum>, Map<Integer, BaseEnum>> map = new HashMap<>();

    public static BaseEnum findByCode(Class<? extends BaseEnum> clazz, Integer code) {
        Map<Integer, BaseEnum> enumMap =  map.get(clazz);
        return enumMap.get(code);
    }

    public static void register(Class<? extends BaseEnum> clazz, BaseEnum[] enums) {
        Map<Integer, BaseEnum> enumMap = new HashMap<>(enums.length);
        Arrays.stream(enums)
                .forEach(em -> {
                    enumMap.put(em.getCode(), em);
                    System.out.println(em + "" + em.getCode());
                });
        map.put(clazz, enumMap);
        System.out.println(enumMap.size());
    }

    public static void main(String[] args) {
        System.out.println(DeleteFlag.NORMAL);
        System.out.println(DeleteFlag.DELETED);
        System.out.println(EnumContext.map);
    }

}
