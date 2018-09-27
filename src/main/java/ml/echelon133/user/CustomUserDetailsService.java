package ml.echelon133.service;

import ml.echelon133.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private IUserService userService;

    @Autowired
    public CustomUserDetailsService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String u) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(u);

        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        return user;
    }
}
