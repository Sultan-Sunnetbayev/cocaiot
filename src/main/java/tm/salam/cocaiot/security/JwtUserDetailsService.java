package tm.salam.cocaiot.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tm.salam.cocaiot.models.User;
import tm.salam.cocaiot.security.jwt.JwtUser;
import tm.salam.cocaiot.services.UserService;

import java.util.LinkedList;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {

        final User user=userService.getUserByEmail(email);

        if(user==null){
            throw new UsernameNotFoundException("user with email "+ email +" not found");
        }
        JwtUser jwtUser= JwtUser.builder()
                .uuid(user.getUuid())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .password(user.getPassword())
                .grantedAuthorities(convertToGrantedAuthority(user.getRole().getName()))
                .enabled(true)
                .build();

        return jwtUser;
    }

    private List<GrantedAuthority> convertToGrantedAuthority(final String... roles){

        List<GrantedAuthority>grantedAuthorities=new LinkedList<>();

        for(String role:roles){
            if(role!=null){
                grantedAuthorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return grantedAuthorities;
    }

}
