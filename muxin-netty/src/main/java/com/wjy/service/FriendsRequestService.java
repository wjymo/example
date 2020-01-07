package com.wjy.service;

import com.wjy.dao.FriendsRequestDAO;
import com.wjy.dao.UserDAO;
import com.wjy.entity.FriendsRequest;
import com.wjy.entity.User;
import com.wjy.util.FileUtils;
import com.wjy.util.QRCodeUtils;
import org.apache.ibatis.annotations.Param;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Service
public class FriendsRequestService {

    @Autowired
    private FriendsRequestDAO friendsRequestDAO;
    @Autowired
    private Sid sid;


    public void insert(FriendsRequest friendsRequest){
        String id = sid.nextShort();
        friendsRequest.setId(id);
        friendsRequest.setRequestDateTime(new Date());
        friendsRequestDAO.insert(friendsRequest);
    }

    public void deleteBySnederAndAccepter(String sendUserId,String acceptUserId){
        friendsRequestDAO.deleteBySnederAndAccepter(sendUserId,acceptUserId);
    }

}
