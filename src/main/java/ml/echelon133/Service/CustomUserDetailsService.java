package ml.echelon133.Service;

import ml.echelon133.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String u) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(u);

        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }
        return user;
    }
}
