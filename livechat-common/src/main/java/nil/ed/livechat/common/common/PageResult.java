package nil.ed.livechat.common.common;

import java.util.List;

import lombok.Data;

@Data
public class PageResult<T> {
    /*
    page No
     */
    private int pageNo;

    /*
    page Size
     */
    private int pageSize;

    /*
    total data count
     */
    private int total;

    /*
    data list
     */
    private List<T> data;

}
