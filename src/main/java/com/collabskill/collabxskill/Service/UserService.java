package com.collabskill.collabxskill.Service;

import com.collabskill.collabxskill.Entities.User;
import com.collabskill.collabxskill.io.UserRequestDTO;
import com.collabskill.collabxskill.io.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ValidationException;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    UserResponseDTO getUserById(String id);
    UserResponseDTO getUserByEmail(String email);
    UserResponseDTO saveUser(User user);
    boolean verifyPassword(String email, String password);
    UserResponseDTO resetPassword(Map<String, String> request) throws ValidationException;
    void generateAndSendOtp(String email);
    boolean verifyOtp(UserRequestDTO userRequest) throws IOException;
    void refresh(String rt, HttpServletResponse resp);
    void generateAndSetTokens(HttpServletResponse resp, String email);
    void registerUser(UserRequestDTO userRequestDTO);
}
