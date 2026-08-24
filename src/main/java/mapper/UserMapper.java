package mapper;

import dto.User.UserRequest;
import entity.User;

public class UserMapper {
    public User toEntity (UserRequest request){
        if(request == null) return;
        return User.builder(
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
        )
    }
}
