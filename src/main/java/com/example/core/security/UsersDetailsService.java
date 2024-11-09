package com.example.core.security;

import com.example.core.dto.UserDto;
import com.example.core.model.Users;
import com.example.core.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> user = repository.findByEmail(username);
        return user.map(OurUsersDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("%s - не найден", username)));
    }
}
