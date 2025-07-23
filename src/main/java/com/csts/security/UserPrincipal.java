package com.csts.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.csts.model.User;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private User user;
    public UserPrincipal(User user){
        this.user=user;
    }
    public String getId(){
        return user.getId();
    }
    public User getUser(){
        return user;
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }
    @Override
    public boolean isEnabled(){
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}