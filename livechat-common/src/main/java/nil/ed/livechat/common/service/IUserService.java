package nil.ed.livechat.common.service;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.vo.UserInRoomInfoVO;
import nil.ed.livechat.common.vo.UserBaseVO;
import org.springframework.stereotype.Service;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */

@Service
public interface IUserService {

    /**
     * 根据id获取用户信息
     * @param id id
     * @param getSecretKey 是否获取secretKey
     * @return userVO
     */
    Response<UserBaseVO> getUserVOById(Long id, boolean getSecretKey);

    /**
     * 获取用户在房间的信息
     * @param uid 用户id
     * @param roomId 房间id
     * @return 用户在房间的数据
     */
    Response<UserInRoomInfoVO> getUserInRoomInfo(Long uid, Long roomId);

    /**
     * 注册
     * @param user 用户
     * @return 注册响应
     */
    Response<Void> register(UserEntity user);

    /**
     * 编辑用户信息
     * @param user 用户信息
     * @return 响应
     */
    Response<Void> updateUserInfo(UserEntity user);

    /**
     * 修改密码
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param userId 用户id
     * @return 响应
     */
    Response<Void> changePwd(String oldPwd, String newPwd, Long userId);

    /**
     * 验证真实信息
     * @param name 真实姓名
     * @param identifierId 身份证号码
     * @param userId id
     * @return 响应
     */
    Response<Void> verifyUser(String name, String identifierId, Long userId);

    /**
     * 判断某个用户是否实名
     * @param id 用户id1
     * @return 是否实名
     */
    boolean isVerified(Long id);
}
