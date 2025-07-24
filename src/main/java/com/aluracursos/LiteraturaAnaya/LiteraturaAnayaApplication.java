package com.aluracursos.LiteraturaAnaya;

import com.aluracursos.LiteraturaAnaya.Principal.Principal;
import com.aluracursos.LiteraturaAnaya.Repository.AutorRepository;
import com.aluracursos.LiteraturaAnaya.Repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaAnayaApplication implements CommandLineRunner {
	@Autowired
	private LibroRepository libroepo;
	@Autowired
	private AutorRepository autorrepo;

	public static void main(String[] args) {

		SpringApplication.run(LiteraturaAnayaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Principal principal = new Principal(libroepo, autorrepo);
		principal.muestraElMenu();

	}
}
