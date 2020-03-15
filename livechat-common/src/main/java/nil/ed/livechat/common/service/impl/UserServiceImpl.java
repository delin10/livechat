package nil.ed.livechat.common.service.impl;

import javax.annotation.Resource;

import java.util.List;

import nil.ed.livechat.common.aop.annotation.MethodInvokeLog;
import nil.ed.livechat.common.common.NormalResponseBuilder;
import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.common.ResponseCodeMessage;
import nil.ed.livechat.common.entity.RoomEntity;
import nil.ed.livechat.common.entity.RoomSubscriptionEntity;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.mapper.RoomSubscriptionMapper;
import nil.ed.livechat.common.mapper.UserMapper;
import nil.ed.livechat.common.repository.UserRepository;
import nil.ed.livechat.common.service.IRoomService;
import nil.ed.livechat.common.service.IUserService;
import nil.ed.livechat.common.service.support.impl.SimpleSelectOneHelper;
import nil.ed.livechat.common.vo.OwnedRoomVO;
import nil.ed.livechat.common.vo.UserInRoomInfoVO;
import nil.ed.livechat.common.vo.UserBaseVO;
import nil.ed.livechat.common.vo.VerifiedUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * Created at 2020-01-17
 *
 * @author lidelin
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRepository userRespository;

    @Resource
    private RoomSubscriptionMapper roomSubscriptionMapper;

    @Resource
    private IRoomService roomService;

    @Resource
    private PasswordEncoder customPasswordEncoder;

    @Override
    public Response<UserBaseVO> getUserVOById(Long id, boolean getSecretKey) {
        return new SimpleSelectOneHelper<UserBaseVO>()
                .operate(() -> getUserById(id, getSecretKey));
    }

    @Override
    public Response<UserInRoomInfoVO> getUserInRoomInfo(Long uid, Long roomId) {
        return new SimpleSelectOneHelper<UserInRoomInfoVO>()
                .operate(() -> getUserInRoomInfoVO(uid, roomId));
    }

    private UserInRoomInfoVO getUserInRoomInfoVO(Long id, Long roomId) {
        UserInRoomInfoVO userInRoomInfoVO = new UserInRoomInfoVO();
        userInRoomInfoVO.setRoomId(roomId);
        Example example = new Example(RoomSubscriptionEntity.class);
        example.createCriteria()
                .andEqualTo("uid", id)
                .andEqualTo("roomId", roomId);

        RoomSubscriptionEntity roomSubscriptionEntity = roomSubscriptionMapper.selectOneByExample(example);

        if (roomSubscriptionEntity == null) {
            userInRoomInfoVO.setSubscribed(Boolean.FALSE);
        } else {
            userInRoomInfoVO.setSubscribed(Boolean.TRUE);
            userInRoomInfoVO.setLevel(roomSubscriptionEntity.getLevel());
            userInRoomInfoVO.setSubscribedTime(roomSubscriptionEntity.getCreateTime().getTime());
        }

        return userInRoomInfoVO;
    }

    @Override
    @MethodInvokeLog
    public Response<Void> register(UserEntity user) {
        try {
            user.setPwd(customPasswordEncoder.encode(user.getPwd()));
            userMapper.insert(user);
            return new NormalResponseBuilder<Void>()
                    .setCodeMessage(ResponseCodeMessage.SUCCESS)
                    .build();
        }catch (DuplicateKeyException e) {
            if (userRespository.getByUsername(user.getUsername()) != null) {
                return new NormalResponseBuilder<Void>()
                        .setCodeMessage(ResponseCodeMessage.DUPLICATE_FIELD_MESSAGE.formatMessage("用户名"))
                        .build();
            }
        }
        return new NormalResponseBuilder<Void>()
                .setCodeMessage(ResponseCodeMessage.FAILED)
                .build();
    }

    @Override
    @MethodInvokeLog
    public Response<Void> updateUserInfo(UserEntity entity) {
        if (userRespository.updateUserByIdIgnoredNull(entity)) {
            return new NormalResponseBuilder<Void>()
                    .setCodeMessage(ResponseCodeMessage.SUCCESS)
                    .build();
        } else {
            return new NormalResponseBuilder<Void>()
                    .setCodeMessage(ResponseCodeMessage.FAILED)
                    .build();
        }
    }

    @Override
    public Response<Void> changePwd(String oldPwd, String newPwd, Long userId) {
        UserEntity userEntity = userMapper.selectByPrimaryKey(userId);
        String maskNewPwd = customPasswordEncoder.encode(newPwd);
        String maskOldPwd = customPasswordEncoder.encode(oldPwd);
        if (userEntity != null && userEntity.getPwd().equals(maskOldPwd)) {
            UserEntity updatePwdEntity = new UserEntity();
            updatePwdEntity.setId(userId);
            updatePwdEntity.setPwd(maskNewPwd);
            return updateUserInfo(updatePwdEntity);
        }
        return new NormalResponseBuilder<Void>()
                .setCodeMessage(ResponseCodeMessage.FAILED)
                .build();
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public Response<Void> verifyUser(String name, String identifierId, Long userId) {
        UserEntity curUser = userMapper.selectByPrimaryKey(userId);
        if (StringUtils.isNotBlank(curUser.getIdentifierId()) && StringUtils.isNotBlank(curUser.getActualName())) {
            return new NormalResponseBuilder<Void>()
                    .setCode(ResponseCodeMessage.FAILED.getCode())
                    .setMessage("用户已验证")
                    .build();
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setActualName(name);
        userEntity.setIdentifierId(identifierId);
        updateUserInfo(userEntity);
        return new NormalResponseBuilder<Void>()
                .setCodeMessage(ResponseCodeMessage.SUCCESS)
                .build();
    }

    @MethodInvokeLog
    private UserBaseVO getUserById(Long id, boolean getSecretKey) {
        UserEntity entity = userRespository.getUserById(id);
        if (getSecretKey && StringUtils.isNotBlank(entity.getIdentifierId()) && StringUtils.isNotBlank(entity.getActualName())) {
            Response<List<OwnedRoomVO>> res = roomService.listByUid(id);
            if (res.getCode() == ResponseCodeMessage.SUCCESS.getCode()) {
                return VerifiedUserVO.parse(entity, res.getData());
            } else {
                return null;
            }
        }
        return UserBaseVO.parse(entity);
    }

    @Override
    public boolean isVerified(Long id) {
        UserEntity entity = userRespository.getUserById(id);
        return StringUtils.isNotBlank(entity.getIdentifierId()) && StringUtils.isNotBlank(entity.getActualName());
    }
}
