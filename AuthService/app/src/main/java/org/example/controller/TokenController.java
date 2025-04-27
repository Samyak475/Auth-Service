package org.example.controller;

import org.example.entities.RefreshToken;
import org.example.request.AuthRequestDto;
import org.example.request.RequestTokenRequestDto;
import org.example.response.JwtResponseDto;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    JwtService jwtService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/auth/v1/login")
    public ResponseEntity login(@RequestBody AuthRequestDto authRequestDto){
       Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(),authRequestDto.getPassword()));
       if(authentication.isAuthenticated()){
           RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDto.getUsername());
           return new ResponseEntity<>(JwtResponseDto.builder()
                   .accessToken(jwtService.generateToken(authRequestDto.getUsername()))
                   .token(refreshToken.getToken())
                   .build(), HttpStatus.OK
           );
       }
       else{
           return new ResponseEntity<>("Exception in user Login", HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @PostMapping("/auth/v1/refreshToken")

    public JwtResponseDto refreshToken (@RequestBody RequestTokenRequestDto requestTokenRequestDto){
        return refreshTokenService.findByToken(requestTokenRequestDto.getToken()).map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getUsername());
                    return JwtResponseDto.builder().accessToken(accessToken).token(requestTokenRequestDto.getToken()).build();
                }).orElseThrow(()-> new RuntimeException("RefreshToken not in DB")); 

    }

}
