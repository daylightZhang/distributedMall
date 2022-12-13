package com.sample.mall.user.controller;


import com.sample.mall.common.base.BaseResponse;
import com.sample.mall.common.dto.UserDTO;
import com.sample.mall.user.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class UserController {

    @Value("${user:mall}")
    private String user;

    @Resource
    private IUserService userService;

    @GetMapping(value = "/hello")
    public String sayHello() {
        return "hello, " + user;
    }

    @GetMapping(value = "/user/{id}")
    public BaseResponse<UserDTO> getUser(@PathVariable(value = "id") Long userId) {
        UserDTO user = userService.getUser(userId);
        return BaseResponse.success(user);
    }

    @PostMapping(value = "/user")
    public BaseResponse updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return BaseResponse.success();
    }

}
