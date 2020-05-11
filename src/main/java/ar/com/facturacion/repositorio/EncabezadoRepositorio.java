package ar.com.facturacion.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ar.com.facturacion.dominio.Encabezado;

public interface EncabezadoRepositorio extends JpaRepository<Encabezado, Long>{
    //Encabezado getEncabezadoByCliente_Id(Long idCliente);
    Encabezado getFirstByCliente_IdOrderByIdDesc(Long idCliente);

    //List <Encabezado> getAllByAnuladoIsFalse();
    Page <Encabezado> findAllByAnuladoIsFalse(Pageable page);
}
