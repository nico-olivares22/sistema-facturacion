package ar.com.facturacion.repositorio;

import ar.com.facturacion.dominio.Factura;
import ar.com.facturacion.dominio.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ar.com.facturacion.dominio.Cliente;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long>{
	Page<Cliente> findAllByBorradoIsFalse(Pageable page);

	List <Cliente> findAllByCuit(String cuit);
}
