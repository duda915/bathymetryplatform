package com.mdud.bathymetryplatform.controller;

import com.mdud.bathymetryplatform.user.ApplicationUser;
import com.mdud.bathymetryplatform.datamodel.dto.AppUserDTO;
import com.mdud.bathymetryplatform.repository.RoleRepository;
import com.mdud.bathymetryplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private ConsumerTokenServices consumerTokenServices;

    public UserController(@Autowired RoleRepository roleRepository,
                          @Autowired UserRepository userRepository,
                          @Autowired ConsumerTokenServices consumerTokenServices) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.consumerTokenServices = consumerTokenServices;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ApplicationUser registerUser(@RequestBody AppUserDTO user) {
//        Authority defaultAuthority = roleRepository.findDistinctByRoleName("ADD");
//        UserAuthority defaultUserAuthority = new UserAuthority(null, defaultAuthority);
//        Set<UserAuthority> roleSet = new HashSet<>();
//        roleSet.add(defaultUserAuthority);
//
//        ApplicationUser newUser = new ApplicationUser(user.getUsername(), user.getPassword(),
//                roleSet);
//        userRepository.save(newUser);
//
//        return newUser;
        return null;
    }

    @GetMapping("/logged")
    public ApplicationUser loggedUser(Principal principal) {
        return userRepository.findDistinctByUsername(principal.getName());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/logout")
    @ResponseBody
    public void revokeToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String refresh = request.getHeader("Refresh");

        if(refresh != null) {
            consumerTokenServices.revokeToken(refresh);
        }

        if (authorization != null && authorization.contains("Bearer")){
            String tokenId = authorization.substring("Bearer".length()+1);
            consumerTokenServices.revokeToken(tokenId);
        }


    }

}
