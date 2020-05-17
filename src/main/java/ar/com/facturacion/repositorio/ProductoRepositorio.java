package ar.com.facturacion.repositorio;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.facturacion.dominio.Producto;
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long>{
	List<Producto> findByCodigoContainingOrNombreContaining(String codigo, String nombre);

	Page<Producto> getAllByBorradoIsFalse( Pageable page);

	Page <Producto> findAllByBorradoIsFalse(Pageable page);

	List <Producto> findAllByBorradoIsFalse();

	List <Producto> findAllByCodigo(String codigo); //consulta validador


}
