package com.example.beerism.Login;

import com.example.beerism.VO.UsersVO;

public interface MemberRepository {
    void doesUserEmailExist(String email, UsersVO usersVO);

    void addNewRegisteredUser(UsersVO usersVO);

    void getLoginUserByEmail(String email);
}
