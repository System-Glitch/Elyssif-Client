package fr.elyssif.client.repository;

import fr.elyssif.client.gui.repository.Repository;
import fr.elyssif.client.model.TestModel;

public final class TestRepository extends Repository<TestModel> {

	public TestRepository() {
		super(null, null);
		// Repository override to bypass http client and authenticator
	}

}
