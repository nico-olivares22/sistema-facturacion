package ar.com.facturacion.controller;

import ar.com.facturacion.dominio.*;
import ar.com.facturacion.repositorio.*;
import ar.com.facturacion.servicios.FacturacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/facturas")
public class FacturaController {


    @Autowired
    private EncabezadoRepositorio encabezadoRepositorio;
    private ItemRepositorio itemRepositorio;
    private PieRepositorio pieRepositorio;
    private ClienteRepositorio clienteRepositorio;
    private ProductoRepositorio productoRepositorio;
    private static Integer currentPage = 1; //paginacion
    private static Integer pageSize = 5; //paginacion


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

        model.addAttribute("tipoletra",TipoLetra.values());
        model.addAttribute("encabezado", new Encabezado());
        return "/facturas/regis_factura-encabezado";
    }

    @PostMapping("/post-encabezado/{idCliente}")
    public String postEncabezado (@PathVariable Long idCliente, @Valid Encabezado encabezado, BindingResult result){
        if (result.hasErrors()) {
            return "/facturas/regis_factura-encabezado";
        }
        Cliente cliente = clienteRepositorio.findById(idCliente).get();
        encabezado.setAnulado(false);
        encabezado.setCliente(cliente);
        encabezadoRepositorio.save(encabezado);
        return "redirect:/facturas/get-item/{idCliente}";
    }

    @GetMapping("/get-item/{idCliente}")
    public String getItem (@PathVariable Long idCliente, Model model){
        List<Producto> productos = productoRepositorio.findAllByBorradoIsFalse(); //traemos todos los productos
        model.addAttribute("productos", productos);
        model.addAttribute("item", new Item());
        return "/facturas/regis_factura-item";
    }

    @PostMapping("/post-item/{idCliente}")
    public String postItem (@PathVariable Long idCliente, @Valid Item item, BindingResult result){
        if (result.hasErrors()) {
            return "/facturas/regis_factura-item";
        }
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
    public String postPie (@PathVariable Long idCliente, @Valid Pie pie, BindingResult result){
        if (result.hasErrors()) {
            return "/facturas/regis_factura-pie";
        }
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
    public String listadoFacturas(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        Page<Encabezado> dataPage;
        if (!page.isPresent() && !size.isPresent()) {
            dataPage = encabezadoRepositorio.findAllByAnuladoIsFalse(PageRequest.of(currentPage - 1, pageSize));
        }
        else {
            dataPage = encabezadoRepositorio.findAllByAnuladoIsFalse( PageRequest.of(page.get() - 1, size.get()));
        }
        int totalPages = dataPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("data", dataPage);
        model.addAttribute("titulo", "Listado de Facturas");
        return "facturas/listado_facturas";
        //List <Encabezado> facturas = encabezadoRepositorio.getAllByAnuladoIsFalse();
        //model.addAttribute("facturas",facturas);
        //return "facturas/listado_facturas";
    }

    @GetMapping("/ver-factura/{id}")
    public String verFactura(@PathVariable Long id,Model model){
        Encabezado encabezado = encabezadoRepositorio.findById(id).get();
        List <Item> items = itemRepositorio.findAllByEncabezado_Id(id);
        Pie pie = pieRepositorio.getByEncabezado_Id(id);
        model.addAttribute("factura",encabezado);
        model.addAttribute("items",items);
        model.addAttribute("pie",pie);
        return "/facturas/factura";
    }

    @GetMapping("/delete-factura/{id}")
    public String deleteFactura(@PathVariable Long id) {
        Encabezado encabezado = encabezadoRepositorio.findById(id).get();
        encabezado.setAnulado(true);
        encabezadoRepositorio.save(encabezado);
        return "redirect:/facturas/listado";

    }

    @GetMapping("/menu")
        public String menu(){
            return "/menu/pagina_principal";
        }
}
