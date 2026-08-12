package com.bld.persistence.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.bld.commons.reflection.model.NativeQueryParameter;
import com.bld.persistence.common.GenereMapper;
import com.bld.persistence.core.domain.Genere;
import com.bld.persistence.core.domain.PostazioneCucina;
import com.bld.persistence.core.domain.Ristorante;
import com.bld.persistence.core.domain.TipoToponimo;
import com.bld.persistence.core.repository.GenereRepository;
import com.bld.persistence.core.repository.PostazioneCucinaRepository;
import com.bld.persistence.core.repository.RistoranteRepository;
import com.bld.persistence.core.repository.TipoToponimoRepository;
import com.bld.persistence.parameter.GenereTuple;
import com.bld.persistence.parameter.NativeGenereParameter;
import com.bld.persistence.response.GenereModel;

@SpringBootTest
@Transactional
class GenereNativeQueryTest {

	private static final String NATIVE_QUERY = "select g.id_genere,\n"
			+ "g.des_genere,pc.des_postazione_cucina,r.nome \n"
			+ "from \n"
			+ "genere g \n"
			+ "join postazione_cucina pc on g.id_postazione_cucina=pc.id_postazione_cucina \n"
			+ "join ristorante r on pc.id_ristorante=r.id_ristorante \n"
			+ "${zone1}\n"
			+ "union\n"
			+ "select \n"
			+ "g.id_genere,g.des_genere,pc.des_postazione_cucina,r.nome \n"
			+ "from \n"
			+ "genere g \n"
			+ "join postazione_cucina pc on g.id_postazione_cucina=pc.id_postazione_cucina \n"
			+ "join ristorante r on pc.id_ristorante=r.id_ristorante \n"
			+ "${zone2}\n";

	@Autowired
	private GenereService genereService;

	@Autowired
	private GenereMapper genereMapper;

	@Autowired
	private GenereRepository genereRepository;

	@Autowired
	private PostazioneCucinaRepository postazioneCucinaRepository;

	@Autowired
	private RistoranteRepository ristoranteRepository;

	@Autowired
	private TipoToponimoRepository tipoToponimoRepository;

	private Genere pizza;
	private PostazioneCucina postazioneImpasto;

	@BeforeEach
	void setUp() {
		TipoToponimo via = new TipoToponimo();
		via.setCodTipoToponimo("VIA");
		via.setDesTipoToponimo("Via");
		tipoToponimoRepository.saveAndFlush(via);

		Ristorante ristorante = new Ristorante();
		ristorante.setCap("20100");
		ristorante.setIndirizzo("Via Roma 1");
		ristorante.setNome("Da Mario");
		ristorante.setNumCivico("1");
		ristorante.setTipoToponimo(via);
		ristoranteRepository.saveAndFlush(ristorante);

		postazioneImpasto = newPostazione("Cucina Impasto", ristorante);
		PostazioneCucina postazionePiatti = newPostazione("Cucina Piatti", ristorante);
		pizza = newGenere("Pizza", postazioneImpasto);
		newGenere("Contorno", postazionePiatti);
		newGenere("Bevanda", postazionePiatti);
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
	void nativeQueryEmptyZonesReturnsAll() {
		NativeQueryParameter<GenereModel, Long> parameter = new NativeQueryParameter<>(GenereModel.class, new NativeGenereParameter());
		List<GenereModel> result = genereService.findByFilter(parameter, NATIVE_QUERY);
		assertEquals(3, result.size());
	}

	@Test
	void nativeQueryEmptyZonesRowMapped() {
		NativeQueryParameter<GenereModel, Long> parameter = new NativeQueryParameter<>(GenereModel.class, new NativeGenereParameter());
		List<GenereModel> result = genereService.findByFilter(parameter, NATIVE_QUERY, genereMapper::rowMapper);
		assertEquals(3, result.size());
		assertNotNull(result.get(0).getId());
		assertEquals("Da Mario", result.get(0).getRistorante());
	}

	@Test
	void nativeQueryTupleFilter() {
		NativeGenereParameter nativeParameter = new NativeGenereParameter();
		GenereTuple tuple = new GenereTuple();
		tuple.setIdGenere(pizza.getIdGenere());
		tuple.setIdPostazioneCucina(postazioneImpasto.getIdPostazioneCucina());
		nativeParameter.setGenereTuple(List.of(tuple));

		NativeQueryParameter<GenereModel, Long> parameter = new NativeQueryParameter<>(GenereModel.class, nativeParameter);
		List<GenereModel> result = genereService.findByFilter(parameter, NATIVE_QUERY, genereMapper::rowMapper);
		assertEquals(1, result.size());
		assertEquals("Pizza", result.get(0).getDesGenere());
	}

}