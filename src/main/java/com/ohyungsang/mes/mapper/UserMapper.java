package com.ohyungsang.mes.mapper;

import com.ohyungsang.mes.domain.User;

import java.util.List;

public interface UserMapper {
    List<User> findAllActive();
}
