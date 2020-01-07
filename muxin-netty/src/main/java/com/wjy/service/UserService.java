package com.wjy.service;

import com.wjy.constant.MsgSignFlagEnum;
import com.wjy.dao.ChatMsgDAO;
import com.wjy.dao.UserDAO;
import com.wjy.entity.ChatMsgUser;
import com.wjy.entity.DBChatMsg;
import com.wjy.entity.User;
import com.wjy.netty.ChatMsg;
import com.wjy.util.ChineseCharacterUtil;
import com.wjy.util.FileUtils;
import com.wjy.util.QRCodeUtils;
import org.apache.ibatis.annotations.Param;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Sid sid;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChatMsgDAO chatMsgDAO;

    public String  getByMobile(String mobile){
        return "1";
    }

    public void createUser(User user) {
        String nickname = user.getNickname();
        String upperCase = ChineseCharacterUtil.getUpperCase(nickname, false);
        user.setInitial(upperCase.substring(0,1));
        String userId = sid.nextShort();
        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "D://user-" + userId + "-qrcode.png";
        // muxin_qrcode:[username]
        QRCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

//        user.setQrcode(qrCodeUrl);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(userId);
        userDAO.insert(user);
    }

    public User getByUsername(String username){
        return userDAO.getByUsername(username);
    }

    public List<User> getAllRequestBySelfId(String acceptUserId){
        return userDAO.getAllRequestBySelfId(acceptUserId);
    }

    public Map<String, List<User>> getAllMyFriends(String id){
        List<User> users = userDAO.getAllMyFriends(id);
        Map<String, List<User>> groupByInitial = users.stream()
                .collect(Collectors.groupingBy(User::getInitial, TreeMap::new,Collectors.toList()));

        return groupByInitial;
    }

    public String saveMsg(ChatMsg chatMsg) {
        String id = sid.nextShort();
        DBChatMsg dbChatMsg=new DBChatMsg()
                .setMsg(chatMsg.getMsg()).setAcceptUserId(chatMsg.getReceiverId())
                .setSendUserId(chatMsg.getSenderId()).setId(id)
                .setSignFlag(MsgSignFlagEnum.unsign.type).setCreateTime(new Date());
        chatMsgDAO.insert(dbChatMsg);
        return id;
    }

    public void batchUpdateMsgSigned(List<String> msgIdList){
        chatMsgDAO.batchUpdateMsgSigned(msgIdList);
    }
    
    public Map<Integer, Map<String, List<ChatMsgUser>>> getByAcceptUserId(String acceptUserId){
        List<ChatMsgUser> dbChatMsgs= chatMsgDAO.getByAcceptUserId(acceptUserId);
        Map<Integer, Map<String, List<ChatMsgUser>>> groupBySignFlagAndNickname = dbChatMsgs.stream()
                .collect(Collectors.groupingBy(ChatMsgUser::getSignFlag, Collectors.groupingBy(ChatMsgUser::getNickname)));

        return groupBySignFlagAndNickname;
    }
}
