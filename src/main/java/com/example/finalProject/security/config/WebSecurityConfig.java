package com.example.finalProject.security.config;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	private SecretKey secretKey;
	
	// svaki put kad se server(nasa spring aplicacija) pokrene, pokrene se i WebSecurityConfig klasa
	// napravi se objekat te konfiguracione klase(sto znaci da mora da se pozove konstruktor bez parametara) i generise se novi kljuc
	// jer se poziva konstruktor klase u kojem je inicijalizovan
	// to je sigurnija varijanta od toga da secret key stavimo u aplication properties
	// pa nam tamo probiju i vide kljuc mogu iskoristiti prost java program da izgenerisu json tokene i da dekriptiju
	// nase jwt tokene koje saljemo preko mreze

	public WebSecurityConfig() {
		super();
		// inicijalizujem secretKey na neku vrednost koja se generise
		// u zagradi algoritam za potpisivanje i proveru potpisa
		this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	}

	// jer hocemo kasnije da injektujemo u drugim spring bean-ovima(rest kontroleri, ...), treba nam globalno stavljam @Bean anotaciju
	// @Autowired 
	// public SecretKey secretKey;
	// U filteru to ne mozemo da koristimo jer filter nije bean, to je obicna klasa, nema spring anotaciju za njega
	// zato mu prosledjujemo kljuc kao parametar
	@Bean
	public SecretKey secretKey() {
		return this.secretKey;
	}

	// new JWTAuthorizationFilter(secretKey) --> pravimo novi filter i prosledjujemo mu secretKey, onaj nas filter
	// i dodajemo ga u chain nakon filter UsernamePasswordAuthenticationFilter.class koji postoji u HttpSecurity po default-u
	//*****************
	// znaci prvo prodje kroz filter UsernamePasswordAuthenticationFilter.class, sto znaci da je validno username, password, role
	// onda prolazi kroz nas filter gde smo redefinisali UsernamePasswordAuthenticationFilter.class filter i gde proveravamo postojanje i validnost tokena, kad dobije token korisnik ne mora 
	// vise da salje username i password(zato smo redefinisali prethodni filter)
	// ako prodje ovaj nas filter zakaci ga na context, to moze samo ako je autentifikovan korisnik
	//*****************
	// i dopustili smo svakom requestu da gadja login metodu, kako bi dostavili kredicijale korisnici nasem serveru
	// svi ostali requestovi treba da su autentifikovani
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable()
		        .addFilterAfter(new JWTAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests().antMatchers(HttpMethod.POST, "/api/v1/login").permitAll().anyRequest()
				.authenticated();
		
		http.cors();
		return http.build();
	}

}
