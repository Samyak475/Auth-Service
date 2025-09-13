package authservice.service;

import authservice.producer.UserInfoEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import authservice.entities.UserInfo;
import authservice.modal.UserInfoDto;
import authservice.producer.UserInfoProducer;
import authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserInfoProducer userInfoProducer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userRepository.findByUsername(username);
        if(userInfo== null){
            throw new UsernameNotFoundException("User not found with given userName");
        }
        return new CustomUserDetails(userInfo);
    }
    public UserInfo checkIfUserExist(UserInfoDto userInfoDto){
        return userRepository.findByUsername(userInfoDto.getUsername());
    }
    public Boolean signUpUser(UserInfoDto userInfoDto){


        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if(Objects.nonNull(checkIfUserExist(userInfoDto))){
            return false;
        }
        String userId= UUID.randomUUID().toString();
        userRepository.save(new UserInfo(userId,userInfoDto.getUsername(),userInfoDto.getPassword(),new HashSet<>()));
        // send event to UserService using kafka
        userInfoProducer.sendMessageToTopic(fetchUserInfoEvent(userInfoDto,userId));
        return true;
    }
    public UserInfoEvent fetchUserInfoEvent(UserInfoDto userInfoDto, String userId){
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .phoneNo(userInfoDto.getPhoneNumber())
                .email(userInfoDto.getEmail())
                .build();
    }

}
