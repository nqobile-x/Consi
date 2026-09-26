package com.storetemplate.store.web;

import com.storetemplate.store.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService users;

    public AuthController(UserService users) {
        this.users = users;
    }

    /** Backing form for registration. */
    public static class RegisterForm {
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        @NotBlank
        private String fullName;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult binding) {
        if (users.emailTaken(form.getEmail())) {
            binding.rejectValue("email", "taken", "An account with that email already exists");
        }
        if (binding.hasErrors()) {
            return "auth/register";
        }
        users.register(form.getEmail(), form.getPassword(), form.getFullName());
        return "redirect:/auth/login?registered";
    }
}
