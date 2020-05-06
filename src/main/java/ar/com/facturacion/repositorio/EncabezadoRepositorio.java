package ar.com.facturacion.repositorio;

import ar.com.facturacion.dominio.Cliente;
import ar.com.facturacion.dominio.Factura;
import ar.com.facturacion.dominio.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import ar.com.facturacion.dominio.Encabezado;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface EncabezadoRepositorio extends JpaRepository<Encabezado, Long>{
    //Encabezado getEncabezadoByCliente_Id(Long idCliente);
    Encabezado getFirstByCliente_IdOrderByIdDesc(Long idCliente);
}
