package com.bld.persistence.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bld.commons.reflection.model.QueryParameter;
import com.bld.commons.utils.data.OrderType;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.core.domain.PostazioneCucina;
import com.bld.persistence.core.domain.Ristorante;
import com.bld.persistence.core.domain.TipoToponimo;
import com.bld.persistence.core.repository.GenereRepository;
import com.bld.persistence.core.repository.PostazioneCucinaRepository;
import com.bld.persistence.core.repository.RistoranteRepository;
import com.bld.persistence.core.repository.TipoToponimoRepository;
import com.bld.persistence.parameter.GenereParameter;

@SpringBootTest
@Transactional
class GenereServiceTest {

	@Autowired
	private GenereService genereService;

	@Autowired
	private GenereRepository genereRepository;

	@Autowired
	private PostazioneCucinaRepository postazioneCucinaRepository;

	@Autowired
	private RistoranteRepository ristoranteRepository;

	@Autowired
	private TipoToponimoRepository tipoToponimoRepository;

	private Ristorante ristorante;
	private PostazioneCucina postazioneImpasto;
	private PostazioneCucina postazionePiatti;
	private Genere pizza;
	private Genere contorno;
	private Genere bevanda;

	@BeforeEach
	void setUp() {
		TipoToponimo via = new TipoToponimo();
		via.setCodTipoToponimo("VIA");
		via.setDesTipoToponimo("Via");
		tipoToponimoRepository.saveAndFlush(via);

		ristorante = new Ristorante();
		ristorante.setCap("20100");
		ristorante.setIndirizzo("Via Roma 1");
		ristorante.setNome("Da Mario");
		ristorante.setNumCivico("1");
		ristorante.setTipoToponimo(via);
		ristoranteRepository.saveAndFlush(ristorante);

		postazioneImpasto = newPostazione("Cucina Impasto");
		postazionePiatti = newPostazione("Cucina Piatti");
		pizza = newGenere("Pizza", postazioneImpasto);
		contorno = newGenere("Contorno", postazionePiatti);
		bevanda = newGenere("Bevanda", postazionePiatti);
	}

	private PostazioneCucina newPostazione(String des) {
		PostazioneCucina postazione = new PostazioneCucina();
		postazione.setDesPostazioneCucina(des);
		postazione.setRistorante(ristorante);
		postazione.setFlagValido(true);
		postazione.setCreateUser("test");
		postazione.setUpdateUser("test");
		postazione.setCreateTimestamp(new Timestamp(System.currentTimeMillis()));
		postazione.setUpdateTimestamp(new Timestamp(System.currentTimeMillis()));
		return postazioneCucinaRepository.saveAndFlush(postazione);
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
		return genereRepository.saveAndFlush(genere);
	}

	@Test
	void findAllReturnsAll() {
		assertEquals(3, genereService.findAll().size());
	}

	@Test
	void findByFilterLikeDesGenereContains() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("PIZZ");
		List<Genere> result = genereService.findByFilter(new QueryParameter<>(parameter));
		assertEquals(1, result.size());
		assertEquals("Pizza", result.get(0).getDesGenere());
	}

	@Test
	void findByFilterLikeDesGenereLowercaseInput() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("pizz");
		List<Genere> result = genereService.findByFilter(new QueryParameter<>(parameter));
		assertEquals(1, result.size());
		assertEquals("Pizza", result.get(0).getDesGenere());
	}

	@Test
	void findByFilterNoMatch() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("inesistente");
		assertTrue(genereService.findByFilter(new QueryParameter<>(parameter)).isEmpty());
	}

	@Test
	void findByFilterLikeDesPosizioneCucina() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesPosizioneCucina("piatti");
		List<Genere> result = genereService.findByFilter(new QueryParameter<>(parameter));
		assertEquals(2, result.size());
	}

	@Test
	void findByFilterIdGenereIn() {
		GenereParameter parameter = new GenereParameter();
		parameter.setIdGenere(List.of(pizza.getIdGenere(), bevanda.getIdGenere()));
		List<Genere> result = genereService.findByFilter(new QueryParameter<>(parameter));
		assertEquals(2, result.size());
	}

	@Test
	void findByFilterIdPostazioneCucinaIn() {
		GenereParameter parameter = new GenereParameter();
		parameter.setIdPostazioneCucina(List.of(postazionePiatti.getIdPostazioneCucina()));
		assertEquals(2, genereService.findByFilter(new QueryParameter<>(parameter)).size());
	}

	@Test
	void findByFilterIdRistoranteIn() {
		GenereParameter parameter = new GenereParameter();
		parameter.setIdRistorante(List.of(ristorante.getIdRistorante()));
		assertEquals(3, genereService.findByFilter(new QueryParameter<>(parameter)).size());
	}

	@Test
	void countByFilterMatchesFind() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("A");
		QueryParameter<Genere, Long> queryParameter = new QueryParameter<>(parameter);
		assertEquals(genereService.findByFilter(queryParameter).size(), genereService.countByFilter(queryParameter));
	}

	@Test
	void singleResultByFilter() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("BEVANDA");
		Genere result = genereService.singleResultByFilter(new QueryParameter<>(parameter));
		assertEquals("Bevanda", result.getDesGenere());
	}

	@Test
	void singleResultByFilterNoMatchReturnsNull() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("INESISTENTE");
		assertNull(genereService.singleResultByFilter(new QueryParameter<>(parameter)));
	}

	@Test
	void mapFindByFilterKeyedById() {
		GenereParameter parameter = new GenereParameter();
		parameter.setDesGenere("PIZZ");
		Map<Long, Genere> map = genereService.mapFindByFilter(new QueryParameter<>(parameter));
		assertEquals(1, map.size());
		assertNotNull(map.get(pizza.getIdGenere()));
	}

	@Test
	void mapKeyFindByFilterKeyedByDesGenere() {
		QueryParameter<Genere, Long> queryParameter = new QueryParameter<>();
		Map<String, Genere> map = genereService.mapKeyFindByFilter(queryParameter, String.class, "desGenere");
		assertEquals(3, map.size());
		assertEquals("Pizza", map.get("Pizza").getDesGenere());
	}

	@Test
	void findByFilterPagedAndOrdered() {
		QueryParameter<Genere, Long> queryParameter = new QueryParameter<>();
		queryParameter.setPageable(0, 2);
		queryParameter.addOrderBy("desGenere", OrderType.ASC);
		List<Genere> result = genereService.findByFilter(queryParameter);
		assertEquals(2, result.size());
		assertEquals("Bevanda", result.get(0).getDesGenere());
		assertEquals("Contorno", result.get(1).getDesGenere());
	}

	@Test
	void deleteByFilter() {
		GenereParameter parameter = new GenereParameter();
		parameter.setIdGenere(List.of(contorno.getIdGenere()));
		genereService.deleteByFilter(new QueryParameter<>(parameter));
		assertEquals(2, genereService.count());
	}

	@Test
	void crudSaveFindUpdateDelete() {
		Genere genere = newGenere("Dolce", postazionePiatti);
		assertNotNull(genereService.findById(genere.getIdGenere()));
		genere.setDesGenere("Dolci");
		genereService.updateAndFlush(genere);
		assertEquals("Dolci", genereService.findById(genere.getIdGenere()).getDesGenere());
		genereService.deleteAndFlush(genere);
		assertNull(genereService.findById(genere.getIdGenere()));
	}

	@Test
	void saveAllPersistsEach() {
		genereService.saveAll(List.of(newGenere("Dolce", postazionePiatti), newGenere("Antipasto", postazioneImpasto)));
		assertEquals(5, genereService.count());
	}

}