package br.com.fatecads.fatecads;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FatecadsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void usuarioComumNaoDeveAcessarPedidosAdministrativos() throws Exception {
		var autenticacao = new UsernamePasswordAuthenticationToken(
				"usuario-teste",
				null,
				List.of(new SimpleGrantedAuthority("ROLE_USER")));
		var sessao = new MockHttpSession();
		sessao.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new SecurityContextImpl(autenticacao));

		mockMvc.perform(get("/pedidos/listar").session(sessao))
				.andExpect(status().isForbidden());
	}

	@Test
	void usuarioComumDeveAcessarLoja() throws Exception {
		var autenticacao = new UsernamePasswordAuthenticationToken(
				"usuario-teste", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
		var sessao = new MockHttpSession();
		sessao.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new SecurityContextImpl(autenticacao));

		mockMvc.perform(get("/loja").session(sessao))
				.andExpect(status().isOk());
		mockMvc.perform(get("/loja/carrinho").session(sessao))
				.andExpect(status().isOk());
	}

	@Test
	void adminDeveAcessarPainelENaoDeveAcessarLoja() throws Exception {
		var autenticacao = new UsernamePasswordAuthenticationToken(
				"admin-teste", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
		var sessao = new MockHttpSession();
		sessao.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new SecurityContextImpl(autenticacao));

		mockMvc.perform(get("/admin").session(sessao))
				.andExpect(status().isOk());
		mockMvc.perform(get("/loja").session(sessao))
				.andExpect(status().isForbidden());
	}

}
