package ar.com.facturacion.controller;

import ar.com.facturacion.dominio.*;
import ar.com.facturacion.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/facturas")

public class FacturaController {
    @Autowired
    private EncabezadoRepositorio encabezadoRepositorio;
    private ItemRepositorio itemRepositorio;
    private PieRepositorio pieRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ProductoRepositorio productoRepositorio;

    public FacturaController(EncabezadoRepositorio encabezadoRepositorio, ItemRepositorio itemRepositorio,
                             PieRepositorio pieRepositorio, ClienteRepositorio clienteRepositorio, ProductoRepositorio productoRepositorio) {
        this.encabezadoRepositorio = encabezadoRepositorio;
        this.itemRepositorio = itemRepositorio;
        this.pieRepositorio = pieRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    @GetMapping ("/get-encabezado/{idCliente}")
    public String getEncabezado (@PathVariable Long idCliente, Model model){

        model.addAttribute("encabezado", new Encabezado());
        return "/facturas/regis_factura-encabezado";
    }

    @PostMapping("/post-encabezado/{idCliente}")
    public String postEncabezado (@PathVariable Long idCliente, @Valid Encabezado encabezado){
        Cliente cliente = clienteRepositorio.findById(idCliente).get();
        encabezado.setAnulado(false);
        encabezado.setCliente(cliente);
        encabezadoRepositorio.save(encabezado);
        return "redirect:/facturas/get-item/{idCliente}";
    }

    @GetMapping("/get-item/{idCliente}")
    public String getItem (@PathVariable Long idCliente, Model model){
        List<Producto> productos = productoRepositorio.findAll(); //traemos todos los productos
        model.addAttribute("productos", productos);
        model.addAttribute("item", new Item());
        return "/facturas/regis_factura-item";
    }

    @PostMapping("/post-item/{idCliente}")
    public String postItem (@PathVariable Long idCliente, @Valid Item item){
        Encabezado encabezado = encabezadoRepositorio.getFirstByCliente_IdOrderByIdDesc(idCliente);
        item.setEncabezado(encabezado);
        BigDecimal sum = item.CalcularSubTotal();
        System.out.println(sum);
        item.setSubTotal(sum);
        itemRepositorio.save(item);
        return "redirect:/facturas/get-item/{idCliente}";
    }

    @GetMapping("/get-pie/{idCliente}")
    public String getPie (@PathVariable Long idCliente, Model model){
        Encabezado idEncabezado = encabezadoRepositorio.getFirstByCliente_IdOrderByIdDesc(idCliente);//Agregado
        Long id = idEncabezado.getId();
        List <Item> items = itemRepositorio.findAllByEncabezado_Id(id);//Agregado
        BigDecimal total = BigDecimal.valueOf(0);
        for (Item item: items) {
            total = total.add(item.getSubTotal());
        }
        Pie pie = new Pie();
        pie.setTotal(total);
        System.out.println(total);
        //model.addAttribute("totalxd", total);
        model.addAttribute("pie", pie);

        return "/facturas/regis_factura-pie";
    }

    @PostMapping("/post-pie/{idCliente}")
    public String postPie (@PathVariable Long idCliente, @Valid Pie pie){
        Encabezado encabezado = encabezadoRepositorio.getFirstByCliente_IdOrderByIdDesc(idCliente);
        Long id = encabezado.getId();
        List <Item> items = itemRepositorio.findAllByEncabezado_Id(id);//Agregado
        BigDecimal total = BigDecimal.valueOf(0);
        for (Item item: items) {
            total = total.add(item.getSubTotal());
        }
        pie.setEncabezado(encabezado);
        pie.setTotal(total);
        pieRepositorio.save(pie);
        return "redirect:/clientes/indexcliente";
    }

    @GetMapping("/listado")
    public String listadoFacturas(Model model){
        List <Encabezado> facturas = encabezadoRepositorio.findAll();
        model.addAttribute("facturas",facturas);
        return "facturas/listado_facturas";
    }

    @GetMapping("/ver-factura/{id}")
    public String verFactura(@PathVariable Long id,Model model){
        Encabezado encabezado = encabezadoRepositorio.findById(id).get();
        model.addAttribute("factura",encabezado);
        return "/facturas/factura";
    }
}
