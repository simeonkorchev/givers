package com.givers.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.givers.domain.core.PasswordResetTokenService;
import com.givers.domain.core.UserService;
import com.givers.repository.entity.User;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/users")  
public class UserRestController {
    private static final String DEFAULT_AVATAR = "default-avatar.jpg";
	private final MediaType mediaType = MediaType.APPLICATION_JSON_UTF8;
    private final UserService service;
    private final PasswordResetTokenService passwordResetTokenService;
    
    @Value("${images.mount}")
	private String imagesMount;
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private Environment env;
//    
    public UserRestController(UserService service, PasswordResetTokenService prts) {
    	this.service = service;
    	this.passwordResetTokenService = prts;
    }
    
    @GetMapping
	@PreAuthorize("hasRole('ADMIN')")
    public Publisher<User> getAll() {	
    	return this.service.all();
    }
    
    @GetMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Publisher<User> getById(@PathVariable("id") String id) {
    	return this.service.get(id);
    }
    
    @PostMapping()
    public Publisher<ResponseEntity<User>> create(@RequestBody User user) {
    	log.info("Creating user: " + user);
    	return this.service.create(user.getFirstName(), user.getLastName(),
    			user.getEmail(), user.getUsername(),
    			user.getPassword(), user.getCauses(),user.getOwnCauses() , user.getCommentIds(), user.getPhotoPath(), user.getHonor(), user.getAuthorities())
    			.map(u -> ResponseEntity.created(URI.create("users/"+ u.getId()))
    					.contentType(mediaType)
    					.body(u));
    }
    
    @PostMapping("/upload/{username}")
	Mono<ResponseEntity<String>> process(@PathVariable("username") String username, @RequestPart("file") Flux<FilePart> filePartFlux) {
    	log.info("Uploading image for user with id: " + username);
    	log.info("Uploading avatar to: " + imagesMount);
    	log.info("File is: "+ filePartFlux.toString());
		return filePartFlux
				.flatMap(it ->  it.transferTo(Paths.get(this.imagesMount +"/"+  username)))
		        .then(Mono.just(
		        	ResponseEntity
		        		.ok()
		        		.contentType(mediaType)
		        		.build()
		        ));
	}
    
    @DeleteMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
    public Publisher<User> deleteById(@PathVariable("id") String id) {
    	return this.service.delete(id);
    }
    
    @PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
    public Mono<ResponseEntity<User>> updateById(@PathVariable("id") String id, @RequestBody User user) {
    	log.info("Updating user "+ user);
    	return Mono
    			.just(user)
    			.flatMap(u -> this.service.update(id, user.getFirstName(), user.getLastName(),
    					user.getEmail(), user.getUsername(),
    	    			user.getPassword(), user.getCauses(), user.getOwnCauses(), user.getCommentIds(), user.getPhotoPath(), user.getHonor(), user.getAuthorities()))
    			.map(r -> ResponseEntity
    					.ok()
    					.contentType(mediaType)
    					.body(r));
    		
    }
    
    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('USER')")
    public Publisher<User> findByUsername(@PathVariable("username") String username) {
    	return this.service.getByUsername(username);
    }
    
    @PutMapping("/updatePassword")
    @PreAuthorize("hasRole('USER')")
    public Mono<User> updatePassword(@RequestParam("oldPassword") String oldPassword,
    		@RequestParam("newPassword") String newPassword, @RequestBody User user) {
    	log.info("checking whether password ", oldPassword, "matches and set the new password ", newPassword);
    	return this.service.changeUserPassword(user.getUsername(), oldPassword, newPassword);
    }
    
	@GetMapping("/image/{username}")
//	@PreAuthorize("hasRole('USER')")
	Mono<ResponseEntity<InputStreamResource>> getImage(@PathVariable("username") String username) throws FileNotFoundException {
		log.info("Gettiing avatar for: " + username);
		final File imgFile = new File(this.imagesMount + "/" + username);
		InputStream imgStream;
		try {
			imgStream = new DataInputStream(new FileInputStream(imgFile));
			log.info("Avatar found");
		} catch (FileNotFoundException e) {
			log.info("Avatar was not found. Fallback to default image");
			ClassLoader classLoader = getClass().getClassLoader();
		       URL resource = classLoader.getResource(DEFAULT_AVATAR);
			imgStream = new DataInputStream(new FileInputStream(resource.getFile()));
		}
		
		return Mono
				.just(new InputStreamResource(imgStream))
				.map(isr -> ResponseEntity
						.ok()
						.contentType(MediaType.IMAGE_PNG)
						.body(isr)
				);
	}
    
//    @PostMapping("/resetPassword")
//    public Mono<ResponseEntity<GenericResponse>> resetPassword(@RequestParam("email") String email) {
//    	return Mono
//    			.just(new GenericResponse())
//    			.map(gr -> {
//    				User user = this.service.getByEmail(email).block();
//    				if (user == null) {
//    					//TODO decide
//    				}
//    				String token = UUID.randomUUID().toString();
//    				this.passwordResetTokenService
//    					.create(user, token);
//    				String requestURL = "change-me"; //TODO
//    				this.mailSender.send(constructResetTokenEmail(requestURL, token, user));
//    				gr.setError(null);
//    				gr.setMessage("Confirmation mail has been sent.");
//    				return gr;
//    			})
//    			.map(r -> ResponseEntity
//    					.ok()
//    					.contentType(mediaType)
//    					.build()
//    			);
//    }
//    
//    @GetMapping(value = "/changePassword")
//    public String showChangePasswordPage(
//      @RequestParam("id") long id, @RequestParam("token") String token) {
//        String result = validatePasswordResetToken(id, token);
//        if (result != null) {
//            	
//            return "redirect:/login";
//        }
//        return "redirect:/updatePassword";
//    }
    
//    private String validatePasswordResetToken(long userId, String token) {
//    	String userIdStr = String.valueOf(userId);
//    	this.passwordResetTokenService.get(userIdStr)
//    		.map(t -> {
//    			if (t == null || t.getUser().getId() != userIdStr ) {
//    				return "invalidToken";
//    			}
//    			final Calendar cal = Calendar.getInstance();
//    			if(t.getExpiryDate().getTime() - cal.getTime().getTime() <= 0 ) {
//    				return "expired";
//    			}
//    			final User user = t.getUser();
//    	        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
//    	        SecurityContextHolder.getContext().setAuthentication(auth);
//    	        return "";
//    		});
//		return "";
//	}
//
//	private SimpleMailMessage constructResetTokenEmail(
//    		  String contextPath, String token, User user) {
//    		    String url = contextPath + "/user/changePassword?id=" + 
//    		      user.getId() + "&token=" + token;
//    		    String message = "Visit the link from below in order to reset your password";
//    		    return constructEmail("Reset Password", message + " \r\n" + url, user);
//    }
//    		 
//	private SimpleMailMessage constructEmail(String subject, String body, 
//	  User user) {
//	    SimpleMailMessage email = new SimpleMailMessage();
//	    email.setSubject(subject);
//	    email.setText(body);
//	    email.setTo(user.getEmail());
////        email.setFrom(env.getProperty("support.email"));
//	    return email;
//	}
}
