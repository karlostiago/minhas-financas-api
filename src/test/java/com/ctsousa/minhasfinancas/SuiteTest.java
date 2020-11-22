package com.ctsousa.minhasfinancas;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ctsousa.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.ctsousa.minhasfinancas.model.repository.UsuarioRepositoryTest;
import com.ctsousa.minhasfinancas.resource.UsuarioResourceTest;
import com.ctsousa.minhasfinancas.service.LancamentoServiceTest;
import com.ctsousa.minhasfinancas.service.UsuarioServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
	
	// REPOSITORYS
	UsuarioRepositoryTest.class,
	LancamentoRepositoryTest.class,
	
	// SERVICES
	UsuarioServiceTest.class,
	LancamentoServiceTest.class,
	
	// RESOURCES
	UsuarioResourceTest.class
})
public class SuiteTest {
}
