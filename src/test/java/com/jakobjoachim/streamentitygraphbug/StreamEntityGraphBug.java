package com.jakobjoachim.streamentitygraphbug;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamEntityGraphBug {

	private EntityManagerFactory entityManagerFactory;

	@BeforeEach
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@AfterEach
	public void destroy() {
		entityManagerFactory.close();
	}

	@Test
	void shouldWork() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		final AnotherEntity anotherEntity1 = new AnotherEntity(1);
		final AnotherEntity anotherEntity3 = new AnotherEntity(3);
		entityManager.persist(anotherEntity1);
		entityManager.persist(anotherEntity3);
		final SomeEntity entity5 = new SomeEntity(5, List.of(anotherEntity1, anotherEntity3));
		entityManager.persist(entity5);

		final AnotherEntity anotherEntity2 = new AnotherEntity(2);
		final AnotherEntity anotherEntity4 = new AnotherEntity(4);
		entityManager.persist(anotherEntity2);
		entityManager.persist(anotherEntity4);
		final SomeEntity entity6 = new SomeEntity(6, List.of(anotherEntity2, anotherEntity4));
		entityManager.persist(entity6);

		EntityGraph<?> eGraph = entityManager.createEntityGraph(SomeEntity.class);
		eGraph.addAttributeNodes("someOrderedValues");

		List<SomeEntity> result = entityManager.createQuery("select se from SomeEntity se where id in :id", SomeEntity.class)
				.setParameter("id", List.of(5, 6))
				.setHint("javax.persistence.fetchgraph", eGraph)
				.getResultStream()
				.collect(Collectors.toList());

		assertThat(result)
				.usingRecursiveFieldByFieldElementComparator()
				.containsExactlyInAnyOrder(entity5, entity6);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
