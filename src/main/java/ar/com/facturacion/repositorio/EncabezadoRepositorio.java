package ar.com.facturacion.repositorio;

import ar.com.facturacion.dominio.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ar.com.facturacion.dominio.Encabezado;

import java.util.List;

public interface EncabezadoRepositorio extends JpaRepository<Encabezado, Long>{
    Encabezado getFirstByCliente_IdOrderByIdDesc(Long idCliente);
    Page <Encabezado> findAllByAnuladoIsFalseOrderByFecha(Pageable page);

    List<Encabezado> findAllByNumero(String numero); //consulta validador
    List <Encabezado> findAllByAnuladoIsFalse(); //consulta validador

}
