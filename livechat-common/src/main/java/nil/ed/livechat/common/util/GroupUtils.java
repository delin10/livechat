package nil.ed.livechat.common.util;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created at 2020-03-15
 *
 * @author lidelin
 */

public class GroupUtils {


    public enum GroupType{
        /**
         * 分组类型
         */
        ONLY_OLD,
        ONLY_NEW,
        BOTH
    }

    /**
     * 分组
     * @param oldSet new set，必要时方法会移除其中元素
     * @param newSet old set，不更新
     * @param <V>
     * @return
     */
    public static<V> Map<GroupType, List<V>> groupNoCopy(Set<V> oldSet, Set<V> newSet){
        EnumMap<GroupType, List<V>> groupMap = new EnumMap<>(GroupType.class);
        for (V v : newSet) {
            GroupType type = GroupType.BOTH;
            if (oldSet.contains(v)) {
                oldSet.remove(v);
            } else {
                type = GroupType.ONLY_NEW;
            }
            List<V> vLs = groupMap.computeIfAbsent(type, k -> new LinkedList<>());
            vLs.add(v);
        }
        List<V> vLs = groupMap.computeIfAbsent(GroupType.ONLY_OLD, k -> new LinkedList<>());
        vLs.addAll(oldSet);
        return groupMap;
    }
}
