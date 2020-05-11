package cf.infinitus.tinitron.users.controller;

import cf.infinitus.tinitron.users.model.User;
import cf.infinitus.tinitron.users.model.UserDTO;
import cf.infinitus.tinitron.users.model.UserRequest;
import cf.infinitus.tinitron.users.security.SecurityUtils;
import cf.infinitus.tinitron.users.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private SecurityUtils securityUtils;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {

        if (!userService.getUserRole(securityUtils.getPrincipal().getId()).equals("admin")) {
            return new ResponseEntity<>("Operation requires 'admin' status", HttpStatus.UNAUTHORIZED);
        }

        List<UserDTO> list = userService.getAllUsers(pageNo, pageSize, sortBy);
        log.info("Returned page[{}] with pageSize[{}]", pageNo, pageSize);
        return new ResponseEntity(list, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getSingle(@PathVariable String id) {
        if (!securityUtils.getPrincipal().getId().equals(id) &&
            !userService.getUserRole(securityUtils.getPrincipal().getId()).equals("admin")) {
            return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);
        }

        User user = userService.findUserById(id);
        if (user == null) {
            return new ResponseEntity<>("No user found with id: " + id, HttpStatus.NOT_FOUND);
        }
        log.info("Returned user with id[{}]", id);
        return new ResponseEntity(new UserDTO(user), HttpStatus.OK);
    }

    @Transactional
    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody UserRequest request) {
        UserDTO createdUserDTO = userService.addUser(request);
        if (createdUserDTO != null)
            return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(request, HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserRequest request) {
        if (!securityUtils.getPrincipal().getId().equals(id) &&
            !userService.getUserRole(securityUtils.getPrincipal().getId()).equals("admin")) {
            return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);
        }

        return userService.updateUser(id, request);
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        if (!securityUtils.getPrincipal().getId().equals(id) &&
            !userService.getUserRole(securityUtils.getPrincipal().getId()).equals("admin")) {
            return new ResponseEntity<>("Access Denied", HttpStatus.UNAUTHORIZED);
        }

        if (userService.deleteUser(id))
            return new ResponseEntity<>("Deleted user with id: " + id, HttpStatus.OK);
        else
            return new ResponseEntity<>("No user found with id: " + id, HttpStatus.NOT_FOUND);
    }

    @Transactional
    @PostMapping("{id}/authorize")
    public ResponseEntity<?> updateUserRole(@PathVariable String id, @RequestParam String role) {
        if (!userService.getUserRole(securityUtils.getPrincipal().getId()).equals("admin")) {
            return new ResponseEntity<>("Operation requires 'admin' status", HttpStatus.UNAUTHORIZED);
        }

        UserDTO updatedUser = userService.updateUserRole(id, role);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No user found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
