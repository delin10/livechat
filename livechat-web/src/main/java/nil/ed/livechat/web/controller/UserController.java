package nil.ed.livechat.web.controller;

import javax.annotation.Resource;

import java.security.Principal;

import nil.ed.livechat.common.common.Response;
import nil.ed.livechat.common.entity.UserEntity;
import nil.ed.livechat.common.service.IUserService;
import nil.ed.livechat.common.vo.PwdChangeVO;
import nil.ed.livechat.common.vo.RegisterVO;
import nil.ed.livechat.common.vo.UserBaseVO;
import nil.ed.livechat.common.vo.UserEditVO;
import nil.ed.livechat.common.vo.UserHeadImgUpdateVO;
import nil.ed.livechat.common.vo.UserInRoomInfoVO;
import nil.ed.livechat.common.vo.UserVerifyVO;
import nil.ed.livechat.login.security.AuthenticationConstants;
import nil.ed.livechat.login.user.CustomUserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created at 2020-01-18
 *
 * @author lidelin
 */

@RestController
@RequestMapping("/livechat/user")
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/register")
    public Response<Void> register(@RequestBody RegisterVO registerVO) {
        return userService.register(registerVO.toUserEntity());
    }

    @PostMapping("/info/edit")
    public Response<Void> edit(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody UserEditVO userEditVO) {
        UserEntity userEntity = userEditVO.toUserEntity();
        userEntity.setId(user.getId());
        return userService.updateUserInfo(userEntity);
    }

    @PostMapping("/info/head/update")
    public Response<Void> updateHeadImg(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody UserHeadImgUpdateVO userHeadImgUpdateVO) {
        UserEntity userEntity = userHeadImgUpdateVO.toUserEntity();
        userEntity.setId(user.getId());
        return userService.updateUserInfo(userEntity);
    }

    @PostMapping("/info/pwd/change")
    public Response<Void> changePwd(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody PwdChangeVO pwdChangeVO) {
        return userService.changePwd(pwdChangeVO.getOldPwd(), pwdChangeVO.getNewPwd(), user.getId());
    }

    @PostMapping("/info/actual/verify")
    public Response<Void> verifyActualInfo(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user,
            @RequestBody UserVerifyVO userVerifyVO) {
        return userService.verifyUser(userVerifyVO.getName(), userVerifyVO.getIdentifierId(), user.getId());
    }

    @GetMapping("/info/get")
    public Response<UserBaseVO> getById(@RequestAttribute(AuthenticationConstants.USER_ATTRIBUTE) CustomUserDetailsImpl user)  {
        return userService.getUserVOById(user.getId(), true);
    }

    @GetMapping("/subscription/info")
    public Response<UserInRoomInfoVO> getUserInRoomInfo(@CookieValue("uid") Long uid,
            @RequestParam("room_id") Long roomId){
        return userService.getUserInRoomInfo(uid, roomId);
    }

}
