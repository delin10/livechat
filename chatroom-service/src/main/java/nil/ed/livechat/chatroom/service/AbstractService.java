package nil.ed.livechat.chatroom.service;

import java.time.Instant;

import nil.ed.livechat.chatroom.entity.TimeRecordEntity;

/**
 * @author delin10
 * @since 2019/10/15
 **/
public class AbstractService {
    protected void recordCreateAndUpdateTime(TimeRecordEntity entity){
        Long currentTimestamp = Instant.now().getEpochSecond();
        entity.setCreateTime(currentTimestamp);
        entity.setUpdateTime(currentTimestamp);
    }

    protected void recordUpdateTime(TimeRecordEntity entity){
        Long currentTimestamp = Instant.now().getEpochSecond();
        entity.setUpdateTime(currentTimestamp);
    }
}
