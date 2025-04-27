package org.example.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;
import org.example.entities.UserInfo;
import org.example.service.JwtService;
import org.example.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
@Data
public class JwtAuthFilter extends OncePerRequestFilter {
   @Autowired
    JwtService jwtService;
   @Autowired
    UserDetailsServiceImpl userDetailsServiceImpl;
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response , FilterChain filterChain) throws ServletException, IOException {
        String header= request.getHeader("Authorized");
        String username=null;
        String token=null;
        if(header != null && header.startsWith("Bearer ")){

            token = header.substring(7);
            username= jwtService.extractuserName(token);

        }
        if(username !=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails  userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            if(jwtService.validateToken(token,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request,response);
    }

}
