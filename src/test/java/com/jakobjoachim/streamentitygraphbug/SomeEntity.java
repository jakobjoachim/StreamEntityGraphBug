package com.jakobjoachim.streamentitygraphbug;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class SomeEntity {
	@Id
	private Integer id;

	@OneToMany
	@OrderBy
	private List<AnotherEntity> someOrderedValues;

	public SomeEntity() {
	}

	public SomeEntity(Integer id, List<AnotherEntity> someOrderedValues) {
		this.id = id;
		this.someOrderedValues = someOrderedValues;
	}
}
