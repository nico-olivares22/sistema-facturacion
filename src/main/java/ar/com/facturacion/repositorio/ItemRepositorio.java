package ar.com.facturacion.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.com.facturacion.dominio.Item;
import java.util.List;

public interface ItemRepositorio extends JpaRepository<Item, Long>{
    List<Item> findAllByEncabezado_Id(Long encabezado_id);

}
