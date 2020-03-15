package nil.ed.livechat.common.service;

import java.util.List;
import java.util.Set;

import nil.ed.livechat.common.common.PageResult;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.RoomStatusEnum;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.vo.IndexRoomListVO;
import nil.ed.livechat.common.vo.OwnedRoomVO;
import nil.ed.livechat.common.vo.SubscribedRoomVO;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

/**
 * @author delin10
 * @since 2019/10/14
 **/
public interface IRoomService {
    /**
     * 添加房间
     * @param tags 标签列表
     * @param room 标签列表
     */
    Response<Void> addRoom(RoomEntity room, Set<Long> tags);

    /**
     * 删除房间
     *
     * @param id 房间ID
     * @param operator 操作人
     * @return 返回删除结果
     */
    Response<Void> deleteRoom(Long id, Long operator);

    /**
     * 查询房间列表
     *
     * @return 房间列表
     */
    Response<PageResult<RoomEntity>> listRooms();

    /**
     * 获取某房间发言排行榜的用户
     *
     *
     * @param roomId 房间id
     * @return 用户列表
     */
    Response<List<Object>> listRankUsersTop50(Long roomId);

    /**
     * 增加发言次数
     *
     * @param roomId roomId
     * @param userId 用户Id
     * @param delta 自增次数
     */
    boolean incrementChatRecord(Long roomId, Long userId, Long delta);

    /**
     * 根据id获取房间信息
     * @param id id
     * @param userId 用户id
     * @return 房间信息
     */
    Response<SubscribedRoomVO> getRoomById(Long id, Long userId);

    /**
     * 更新房间参数
     * @param entity 房间对象
     * @param tags 标签列表
     * @return 是否成功
     */
    Response<Void> updateRoom(RoomEntity entity, Set<Long> tags);

    /**
     * 根据secret key查询
     * @param secretKey
     * @return
     */
    Response<RoomEntity> getBySecretKey(String secretKey);

    /**
     * 获取房间活跃度
     *
     * @param roomId 房间id
     * @return 当前活跃度
     */
    Response<Double> getRoomActivation(Long roomId);

    /**
     * 检查房间id是否存在
     *
     * @param id 房间id
     * @return id是否存在
     */
    boolean checkExist(Long id);

    /**
     * 检查房间id的合法性
     *
     * @param message 消息
     * @param roomIdStr roomId
     * @return roomId是否合法 true - 不合法
     */
    boolean validateRoomId(Message<?> message, String roomIdStr);

    /**
     * 解析room id
     *
     * @param accessor Stomp 首部访问
     * @return Room Id 字符串
     */
    String resolveRoomId(StompHeaderAccessor accessor);

    /**
     * 列举相关房间信息
     *
     * @param roomId 房间id
     * @return 房间列表
     */
    Response<List<SubscribedRoomVO>> listRelativeRooms(Long roomId);

    /**
     * 查询在线的房间信息，通过热度进行排序
     *
     * @param pageNum 页号
     * @param size 页大小
     * @return 房间列表
     */
    Response<IndexRoomListVO> listOnlineRoomsOrderByActivation(Integer pageNum, Integer size);

    /**
     * 推荐房间
     * @param roomId 房间id
     * @param userId 用户id
     * @param count 推荐数量
     * @return 结果
     */
    Response<List<SubscribedRoomVO>> recommendRooms(Long roomId, Long userId, Integer count);

    /**
     * 查询用户uid下的房间
     * @param uid 用户
     * @return 房间列表
     */
    Response<List<OwnedRoomVO>> listByUid(Long uid);

    /**
     * 更新状态
     *
     * @param id id
     * @param secretKey 密钥
     * @param status 状态
     * @return 是否响应
     */
    Response<Void> updateRoomStatus(Long id, String secretKey, RoomStatusEnum status);

    /**
     * 更新状态
     * @param id 房间id
     * @param status 状态
     * @return 响应
     */
    Response<Void> updateRoomStatus(Long id, RoomStatusEnum status);

    /**
     * 获取订阅数量
     * @param roomId 房间id
     * @return count
     */
    Response<Integer> getSubscriptionCount(Long roomId);

    /**
     * 订阅房间
     * @param uid 用户id
     * @param roomId 房间id
     * @return 结果
     */
    Response<Void> subscribe(Long uid, Long roomId);

    /**
     * 取消订阅房间
     * @param uid 用户id
     * @param roomId 房间id
     * @return 结果
     */
    Response<Void> unsubscribe(Long uid, Long roomId);

    /**
     * 更新房间在线人数
     * @param roomId 房间id
     * @param delta 增量
     */
    void updateRoomChatOnlineCount(Long roomId, long delta);

    /**
     * 获取房间聊天在线人数
     * @param roomId 房间id
     * @return 结果
     */
    Long getRoomChatOnlineCount(Long roomId);

    /**
     * 更新发布流发布时间戳
     * @param roomId 房间id
     */
    void updateLastUpdatePublishTimestamp(Long roomId);

    /**
     * 获取最近一次流发布时间戳
     * @param roomId 房间id
     * @return 时间戳，unit：秒，返回-1表示无该数据：已被清理或者未上线
     */
    Long getLastUpdatePublishTimestamp(Long roomId);

    /**
     * 下线房间后，清理相关数据缓存
     * @param roomId 房间id
     */
    void cleanRoom(Long roomId);
}
