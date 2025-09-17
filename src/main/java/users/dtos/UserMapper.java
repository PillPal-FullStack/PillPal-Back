package users.dtos;

import users.User;

public interface UserMapper {
    User dtoToEntity(UserRequest dto, List<Medication> medications);
    UserResponse entityToDto(User user);
}
