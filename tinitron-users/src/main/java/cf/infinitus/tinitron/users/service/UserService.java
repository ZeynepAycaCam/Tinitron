package cf.infinitus.tinitron.users.service;

import cf.infinitus.tinitron.users.dao.UserRepository;
import cf.infinitus.tinitron.users.model.User;
import cf.infinitus.tinitron.users.model.UserDTO;
import cf.infinitus.tinitron.users.model.UserRequest;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserDTO addUser(UserRequest request) {
        if (StringUtils.isEmpty(request.getEmail()) || StringUtils.isEmpty(request.getPassword()))
            return null;

        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            return null;
        } else {
            User newUser = new User();
            newUser.setId(request.getId() == null ? UUID.randomUUID().toString() : request.getId());
            newUser.setEmail(request.getEmail());
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(newUser);
            return new UserDTO(newUser);
        }
    }

    public ResponseEntity<?> updateUser(String id, UserRequest request) {
        if (StringUtils.isEmpty(request.getUsername()) && StringUtils.isEmpty(request.getPassword()))
            return new ResponseEntity<>("Missing fields updating user with id: " + id, HttpStatus.BAD_REQUEST);

        User existingUser = userRepository.findById(id).get();

        if (existingUser != null) {
            if (request.getUsername() != null)
                existingUser.setUsername(request.getUsername());
            if (request.getPassword() != null)
                existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(existingUser);
            return new ResponseEntity<>(new UserDTO(existingUser), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No user found with id: " + id, HttpStatus.NOT_FOUND);
        }
    }

    public boolean deleteUser(String id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UserDTO updateUserRole(String id, String role) {
        var existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setRole(role);
            userRepository.save(user);
            return new UserDTO(user);
        }
        return null;
    }

    public  String getUserRole(String id) {
        var existingUser = userRepository.findById(id);
        return existingUser.isPresent() ? existingUser.get().getRole() : "";
    }

    public List<UserDTO> getAllUsers(Integer pageNo, Integer pageSize, String sortBy) {
        List<UserDTO> userDTOList = new ArrayList<>();
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

        Page<User> pagedResult = userRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            for (User user : pagedResult.getContent()) {
                userDTOList.add(new UserDTO(user));
            }
        }
        return userDTOList;
    }

    public User findUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

}
