package ar.com.facturacion.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ar.com.facturacion.dominio.Producto;
import ar.com.facturacion.repositorio.ProductoRepositorio;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;

@Controller
@RequestMapping("/productos")
public class ProductoControl {

    @Autowired
    private ProductoRepositorio repository;
    private static Integer currentPage = 1;
    private static Integer pageSize = 5;

    @GetMapping("/index")
    public String index(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        Page<Producto> dataPage;
        if (!page.isPresent() && !size.isPresent()) {
            //dataPage = repository.findAll(PageRequest.of(currentPage - 1, pageSize));
            dataPage = repository.findAllByBorradoIsFalse(PageRequest.of(currentPage - 1, pageSize));
        } else {
            //dataPage = repository.findAll(PageRequest.of(page.get() - 1, size.get()));
            dataPage = repository.findAllByBorradoIsFalse(PageRequest.of(page.get() - 1, pageSize));
        }
        int totalPages = dataPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("titulo", "Listado de Productos");
        model.addAttribute("data", dataPage);
        return "productos/index";
    }

    @GetMapping("signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("producto", new Producto());
        return "productos/registro_prod";
    }

    @PostMapping("add")
    public String addProducto(@Valid Producto producto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "productos/registro_prod";
        }
        if (repository.findAllByCodigo(producto.getCodigo()).isEmpty()) {
            repository.save(producto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto Agregado Exitosamente");
            return "redirect:/productos/index";
        } else {
            redirectAttributes.addFlashAttribute("msg", "Error, Ese Código Ya Existe!"); //warning
            return "redirect:/productos/signup";
        }

    }

    @GetMapping(value = "/edit/{id}")
    public String modificarProducto(@PathVariable Long id, Model model) {
        Producto producto = repository.findById(id).get();
        model.addAttribute("titulo", "Modificado de Producto");
        model.addAttribute("producto", producto);
        return "productos/update-producto";
    }

    @PostMapping(value = "/update/{id}")
    public String updateProducto(@Valid Producto producto, RedirectAttributes redirectAttributes) {
        repository.save(producto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto Modificado con Éxito"); //success
        return "redirect:/productos/index";
    }

    @GetMapping(value = "/delete-producto/{id}")
    public String deleteProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Producto producto = repository.findById(id).get();
        producto.setBorrado(true);
        repository.save(producto);
        redirectAttributes.addFlashAttribute("mensaje", "Producto Eliminado"); //danger
        return "redirect:/productos/index";
    }

}
