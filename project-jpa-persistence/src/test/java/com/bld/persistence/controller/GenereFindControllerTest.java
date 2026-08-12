package com.bld.persistence.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.core.domain.PostazioneCucina;
import com.bld.persistence.core.domain.Ristorante;
import com.bld.persistence.core.domain.TipoToponimo;
import com.bld.persistence.core.repository.GenereRepository;
import com.bld.persistence.core.repository.PostazioneCucinaRepository;
import com.bld.persistence.core.repository.RistoranteRepository;
import com.bld.persistence.core.repository.TipoToponimoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class GenereFindControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private GenereRepository genereRepository;

	@Autowired
	private PostazioneCucinaRepository postazioneCucinaRepository;

	@Autowired
	private RistoranteRepository ristoranteRepository;

	@Autowired
	private TipoToponimoRepository tipoToponimoRepository;

	@BeforeEach
	void setUp() {
		if (genereRepository.count() == 0) {
			TipoToponimo via = new TipoToponimo();
			via.setCodTipoToponimo("VIA");
			via.setDesTipoToponimo("Via");
			tipoToponimoRepository.save(via);

			Ristorante ristorante = new Ristorante();
			ristorante.setCap("20100");
			ristorante.setIndirizzo("Via Roma 1");
			ristorante.setNome("Da Mario");
			ristorante.setNumCivico("1");
			ristorante.setTipoToponimo(via);
			ristoranteRepository.save(ristorante);

			PostazioneCucina impasto = newPostazione("Cucina Impasto", ristorante);
			PostazioneCucina piatti = newPostazione("Cucina Piatti", ristorante);
			newGenere("Pizza", impasto);
			newGenere("Contorno", piatti);
			newGenere("Bevanda", piatti);
		}
	}

	private PostazioneCucina newPostazione(String des, Ristorante ristorante) {
		PostazioneCucina postazione = new PostazioneCucina();
		postazione.setDesPostazioneCucina(des);
		postazione.setRistorante(ristorante);
		postazione.setFlagValido(true);
		postazione.setCreateUser("test");
		postazione.setUpdateUser("test");
		postazione.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
		postazione.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		return postazioneCucinaRepository.save(postazione);
	}

	private Genere newGenere(String desGenere, PostazioneCucina postazione) {
		Genere genere = new Genere();
		genere.setDesGenere(desGenere);
		genere.setPostazioneCucina(postazione);
		genere.setFlagValido(true);
		genere.setCreateUser("test");
		genere.setUpdateUser("test");
		genere.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
		genere.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		return genereRepository.save(genere);
	}

	@Test
	void findByFilterProxyEndpoint() throws Exception {
		mockMvc.perform(post("/genere/find/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desGenere\":\"PIZZA\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].desGenere").value("Pizza"));
	}

	@Test
	void findByFilterProxyEndpointNoMatch() throws Exception {
		mockMvc.perform(post("/genere/find/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desGenere\":\"inesistente\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void singleResultProxyEndpoint() throws Exception {
		mockMvc.perform(post("/genere/find/single-result")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"desGenere\":\"BEVANDA\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.desGenere").value("Bevanda"));
	}

	@Test
	void collectionResponseProxyEndpoint() throws Exception {
		mockMvc.perform(post("/genere/find/collection-response/filter")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.length()").value(3));
	}

	@Test
	void nativeFindEndpoint() throws Exception {
		mockMvc.perform(post("/genere/find")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].ristorante").value("Da Mario"));
	}

}