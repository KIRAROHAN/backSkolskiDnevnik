package com.example.finalProject.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.finalProject.dtos.LoginDTO;
import com.example.finalProject.entities.UserEntity;
import com.example.finalProject.repositories.AdminRepository;
import com.example.finalProject.repositories.ParentRepository;
import com.example.finalProject.repositories.StudentRepository;
import com.example.finalProject.repositories.TeacherRepository;
import com.example.finalProject.repositories.UserRepository;
import com.example.finalProject.util.Encryption;

import io.jsonwebtoken.Jwts;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

	// ovde ce biti logika za login i register

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ParentRepository parentRepository;

	@Autowired
	private SecretKey secretKey;

	@Value("${spring.security.token-duration}")
	private Integer tokenDuration;

	private String getJWTToken(UserEntity userEntity) {
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(userEntity.getRole().getName());
		// sa setId zadajemo Id tokena i to ako koristimo jos negde u aplikaciju u kodu
		// treba da bude isti id, prepoznajemo aplikaciju po tome
		// zaglavlje tokena, tj. subject govori nedvosmisleno kome token pripada
		// ne mozemo samo .claim("authorities",grantedAuthorities) da stavimo, jer je
		// drugi parametar tipa List<GrantedAuthority>
		// a .claim ocekuje String kao drugi parametar. Zato List<GrantedAuthority> u
		// stream, onda nad svakim GrantedAuthority pozovemo getAuthority koji vrati
		// string
		// koliko grantedAutority-ja toliko stringova, pa sve to collect u jednu listu
		// smesti
		// Konvertujemo u String sa
		// grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())

		if (userEntity.getUsername() != null) {

			String token = Jwts.builder().setId("softtekJWT").setSubject(userEntity.getUsername())
					.claim("authorities",
							grantedAuthorities.stream().map(GrantedAuthority::getAuthority)
									.collect(Collectors.toList()))
					.setIssuedAt(new Date(System.currentTimeMillis()))
					.setExpiration(new Date(System.currentTimeMillis() + this.tokenDuration)).signWith(this.secretKey)
					.compact();

			return "Bearer " + token;

		}

		return null;
	}

	@RequestMapping(path = "api/v1/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestParam("user") String email, @RequestParam("password") String pwd) {
		UserEntity userEntity = userRepository.findByUsername(email);

		// System.out.println(userEntity.getUsername());
		// System.out.println(userEntity.getPassword());
		// System.out.println(pwd);
		// System.out.println(Encryption.validatePassword(pwd,
		// userEntity.getPassword()));

		// Prvo proverimo da li postoji user pa onda ovaj drugi uslov. Ako user ne
		// postoji, ne proverava drugi uslov
		// To nas cuva od greske null point exception. Da je obrnut redosled, a user ne
		// postoji, bio bi null point exception kada prokrene userEntity.getPassword()
		// Isto je i kod logickog ili.Kad utvrdi da je prvi uslov tacan, ne proverava
		// dalje uslove jer je dovoljno da jedan bude tacan, pa da ceo izraz bude true.
		if (userEntity != null && Encryption.validatePassword(pwd, userEntity.getPassword())) {

			// 1.Create the token
			String token = getJWTToken(userEntity);

			if (token == null) {
				return new ResponseEntity<>("Wrong credentials", HttpStatus.UNAUTHORIZED);
			}
			// 2.Create response with userDTO
			LoginDTO user = new LoginDTO();
			user.setUser(email);
			user.setToken(token);

			String role = userEntity.getRole().getName();
			user.setRole(role);
			

			if (role.equals("ADMIN")) {
				if (adminRepository.existsByUser(userEntity)) {
					user.setName(adminRepository.findByUser(userEntity).getName());
					//System.out.println(adminRepository.findByUser(userEntity).getName());
				} else
					user.setName("");
			}
			
			if (role.equals("TEACHER")) {
				if (teacherRepository.existsByUser(userEntity)) {
					user.setName(teacherRepository.findByUser(userEntity).getName());
				} else
					user.setName("");
			}
			
			if (role.equals("PARENT")) {
				if (parentRepository.existsByUser(userEntity)) {
					user.setName(parentRepository.findByUser(userEntity).getName());
				} else
					user.setName("");
			}
			
			if (role.equals("STUDENT")) {
				if (studentRepository.existsByUser(userEntity)) {
					user.setName(studentRepository.findByUser(userEntity).getName());
				} else
					user.setName("");
			}

			return new ResponseEntity<>(user, HttpStatus.OK);
		}
		return new ResponseEntity<>("Wrong credentials", HttpStatus.UNAUTHORIZED);
	}
}
