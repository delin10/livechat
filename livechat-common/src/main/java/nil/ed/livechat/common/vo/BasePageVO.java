package nil.ed.livechat.common.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created at 2020-03-09
 *
 * @author lidelin
 */

@Getter
@Setter
@ToString
public class BasePageVO<T> {
    private int count;
    private List<T> list;
}
