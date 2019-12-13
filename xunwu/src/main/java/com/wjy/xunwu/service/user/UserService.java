package com.wjy.xunwu.service.user;

import com.wjy.xunwu.dao.UserDAO;
import com.wjy.xunwu.dto.UserDTO;
import com.wjy.xunwu.entity.User;
import com.wjy.xunwu.response.ServiceResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ModelMapper modelMapper;

    public ServiceResult<UserDTO> findById(Long userId) {
        User user = userDAO.findOne(userId);
        if (user == null) {
            return ServiceResult.notFound();
        }
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ServiceResult.of(userDTO);
    }

}
