package org.example.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.UserInfo;
import org.example.modal.UserInfoDto;
import org.example.repository.RefreshTokenRepository;
import org.example.repository.UserRepository;
import org.example.utills.ValidationUtils;
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
        return true;
    }

}
