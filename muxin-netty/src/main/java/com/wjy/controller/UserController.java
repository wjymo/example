package com.wjy.controller;

import com.wjy.dao.FriendsRequestDAO;
import com.wjy.entity.ChatMsgUser;
import com.wjy.entity.DBChatMsg;
import com.wjy.entity.FriendsRequest;
import com.wjy.entity.User;
import com.wjy.response.CommonResponse;
import com.wjy.response.ResponseUtil;
import com.wjy.service.FriendsRequestService;
import com.wjy.service.MyFriendsService;
import com.wjy.service.UserService;
import com.wjy.util.BeanValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value = "用户模块", tags = {"用户模块接口"})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private FriendsRequestService friendsRequestService;
    @Autowired
    private MyFriendsService myFriendsService;

    @ApiOperation(value="注册用户", notes="注册用户")
    @PostMapping("/regist")
    public CommonResponse regist(@RequestBody User user){
        BeanValidator.check(user);
        userService.createUser(user);
        return ResponseUtil.success();
    }

    @ApiOperation(value="通过用户名搜索用户", notes="通过用户名搜索用户")
    @GetMapping("/username/{username}")
    public CommonResponse getByUsername(@PathVariable("username")String username){
        User user = userService.getByUsername(username);
        return ResponseUtil.success(user);
    }

    @PostMapping("/friend/request")
    public CommonResponse friendRequest(@RequestBody FriendsRequest friendsRequest){
        friendsRequestService.insert(friendsRequest);
        return ResponseUtil.success();
    }

    @GetMapping("/all/request/{acceptUserId}")
    public CommonResponse getAllRequestByselfId(@PathVariable("acceptUserId") String acceptUserId){
        List<User> users = userService.getAllRequestBySelfId(acceptUserId);
        return ResponseUtil.success(users);
    }

    @PostMapping("/canael/request")
    public CommonResponse cancelRerquest(@RequestBody FriendsRequest friendsRequest){
        friendsRequestService.deleteBySnederAndAccepter(friendsRequest.getSendUserId(),friendsRequest.getAcceptUserId());
        return ResponseUtil.success();
    }

    @PostMapping("/pass/request")
    public CommonResponse passRerquest(@RequestBody FriendsRequest friendsRequest){
        myFriendsService.passRequest(friendsRequest.getSendUserId(),friendsRequest.getAcceptUserId());
        friendsRequestService.deleteBySnederAndAccepter(friendsRequest.getSendUserId(),friendsRequest.getAcceptUserId());
        return ResponseUtil.success();
    }

    @GetMapping("/all/friends")
    public CommonResponse getAllMyFriends(@RequestParam("id") String id){
        Map<String, List<User>> myFriends = userService.getAllMyFriends(id);
        return ResponseUtil.success(myFriends);
    }

    @GetMapping("/chatMsg/acceptUser/{acceptUserId}")
    public CommonResponse getByAcceptUserId(@PathVariable("acceptUserId") String acceptUserId){
        Map<Integer, Map<String, List<ChatMsgUser>>> groupBySignFlagAndNickname = userService.getByAcceptUserId(acceptUserId);
        return ResponseUtil.success(groupBySignFlagAndNickname);
    }
}
