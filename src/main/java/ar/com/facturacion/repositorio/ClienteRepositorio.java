package ar.com.facturacion.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.com.facturacion.dominio.Cliente;
import java.util.List;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long>{
	Page<Cliente> findAllByBorradoIsFalse(Pageable page);
	List <Cliente> findAllByCuit(String cuit); //consulta validador
}
