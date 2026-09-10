package com.ivodam.finalpaper.edast.mappers;

import com.ivodam.finalpaper.edast.dto.UserDto;
import com.ivodam.finalpaper.edast.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "confirmPassword", ignore = true)
  UserDto userToUserDto(User user);

  @Mapping(target = "authorities", ignore = true)
  User userDtoToUser(UserDto userDto);
}