package ar.com.facturacion.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.com.facturacion.dominio.Producto;
@Repository
public interface ProductoRepositorio extends JpaRepository<Producto, Long>{
	List<Producto> findByCodigoContainingOrNombreContaining(String codigo, String nombre);


}
