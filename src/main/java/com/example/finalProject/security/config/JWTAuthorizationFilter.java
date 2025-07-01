package com.example.finalProject.security.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

	private final String HEADER = "Authorization";
	private final String PREFIX = "Bearer ";
	private SecretKey secretKey;

	// hocemo da secretKey bude globalan na nivou aplikacije, znaci da mi treba u jos metoda van ove klase...
	// posto JWTAthorizationFilter ne mogu proglasiti bean-om jer nije iz springove biblioteke, te ne mogu da ga prosto injektujem sa @Autowired
	// moram secretKey proslediti kao parametar konstruktoru Filtera
	public JWTAuthorizationFilter(SecretKey secretKey) {
		super();
		this.secretKey = secretKey;
	}

	//proverava da li zahtev ima JWTT token u sebi
	private boolean checkIfJWTTokenExists(HttpServletRequest request, HttpServletResponse response) {
		String authorizationHeaderValue = request.getHeader(HEADER);
		if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith(PREFIX)) {
			return false;
		}
		return true;
		
		// moglo je i krace: return if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith(PREFIX))
	}

	private Claims validateToken(HttpServletRequest request) {
		// skidam Bearer iz string-a i menjam ga praznim stringom: Bearer AKADGAGB8983K--> AKADGAGB8983K
		String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
		// parsiranje tokena, translate tokena, gleda sta se nalazi u tokenu
		// za to mora da dekodira token, a to radi pomocu tajnog kljuca poznatog samo nasoj aplikaciji, sa kojim je i kodirao
		// tj. generisao token.
		// ovim smo rekli iz fabrike jwt tokena kreiraj mi parser takav da ce mu signing key za dekripciju/ enkripciju biti secret key koji cu nekako dobiti
		// i izparsiraj mi token da bi dobili njegov sadrzaj
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken).getBody();
	}

	

	private void setUpSpringAuthentication(Claims claims) {
		@SuppressWarnings("unchecked")
		// iz claim-ova koji mogu biti bilo sta izvuce athorities
		List<String> authorities = (List<String>) claims.get("authorities");
		
		// i kazemo za svaki authority pretvori u stream, pa taj string pretvori u SimpleGrantedAuthority(sto je neka klasa koja nastaje na osnovu ovog string-a, poziva se negov konstruktor koji ima jedan string kao parametar)
		// i onda sve to zajedno prebacuje u listu SimpleGrantedAthority-ja i prosledimo kao treci parametar(verovatno lista rola) konstruktoru UsernamePasswordAuthenticationToken
		// drugi parametar je password koje nema, ne saljemo(zato null)
		// prvi parametar je koji je korisnik
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
				authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
		// dobija spring security context i na njega kaci taj token
		SecurityContextHolder.getContext().setAuthentication(auth);
		//System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			if (checkIfJWTTokenExists(request, response)) {
				Claims claims = validateToken(request);
				if (claims.get("authorities") != null) {
					setUpSpringAuthentication(claims);
				} 
				// ukoliko ne moze da se parsira  sa nasim secret key-om token, neko je pokusao da nam podvali
				// (tada je claims null) opet brisemo context
				else {
					SecurityContextHolder.clearContext();
				}
			} 
			// ukoliko token ne postoji sve sto smo stavili na context(sta god da smo zakacili u prethodnim filterisanjima) treba da se obrise
			// jer cim token ne postoji, neko neautentifikovan pokusava da se probije
			// brisemo da mu ne bismo dopustili da prodje dalje, zaustavi ga filter
			else {
				SecurityContextHolder.clearContext();
			}
			// jedan filter zavrsi, drugi preuzme, daisy chaining
			// pozivamo sledeci filter u chain-u
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			return;
		}
	}

}
