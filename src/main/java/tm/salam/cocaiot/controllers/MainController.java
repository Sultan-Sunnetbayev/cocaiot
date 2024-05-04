package tm.salam.cocaiot.controllers;

import io.jsonwebtoken.JwtException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tm.salam.cocaiot.helpers.ResponseTransfer;
import tm.salam.cocaiot.models.User;
import tm.salam.cocaiot.security.jwt.JwtTokenProvider;
import tm.salam.cocaiot.services.UserService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/main")
public class MainController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public MainController(UserService userService, JwtTokenProvider jwtTokenProvider,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(path = "/login", produces = "application/json")
    public ResponseEntity login(@RequestBody String requestBody){

        Map<String, Object> response=new LinkedHashMap<>();
        final String email;
        final String password;
        final User user;

        try {
            JSONObject jsonObject=new JSONObject(requestBody);

            email=jsonObject.getString("email");
            password=jsonObject.getString("password");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            user=userService.getUserByEmail(email);
            if(user==null){
                throw new BadCredentialsException("error user login or password incorrect");
            }
        }catch (JSONException jsonException){
            jsonException.printStackTrace();
            response.put("status", false);
            response.put("code", "");
            response.put("message", jsonException.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (BadCredentialsException badCredentialsException){
            badCredentialsException.printStackTrace();
            response.put("status",false);
            response.put("code", "");
            response.put("message", badCredentialsException.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        final String accessToken=jwtTokenProvider.generateToken(user.getUuid(), user.getEmail(), user.getRole().getName());

        response.put("status", true);
        response.put("code", "");
        response.put("message", "accept user successful logined");
        response.put("data", accessToken);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping(path = "/get-profile", produces = "application/json")
    public ResponseEntity getProfile(@RequestHeader("Authorization")String authorization){

        Map<String, Object>response=new LinkedHashMap<>();
        final ResponseTransfer responseTransfer;
        final String userEmail;

        try {
            userEmail = jwtTokenProvider.getUserEmail(authorization.substring(7));
            responseTransfer=userService.getUserDTOByEmail(userEmail);
        }catch (JwtException jwtException){
            jwtException.printStackTrace();

            response.put("status", false);
            response.put("code", "");
            response.put("message", jwtException.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("status", responseTransfer.isStatus());
        response.put("code", responseTransfer.getCode());
        response.put("message", responseTransfer.getMessage());
        response.put("data", responseTransfer.getData());

        return ResponseEntity.status(responseTransfer.getHttpStatus()).body(response);
    }

}
