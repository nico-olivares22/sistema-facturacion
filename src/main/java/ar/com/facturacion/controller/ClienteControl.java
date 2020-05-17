package ar.com.facturacion.controller;


import ar.com.facturacion.dominio.Cliente;
import ar.com.facturacion.repositorio.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/clientes")

public class ClienteControl {

    @Autowired
    private ClienteRepositorio repository;
    private static Integer currentPage = 1;
    private static Integer pageSize = 5;

    @GetMapping("/indexcliente")
    public String index_cliente(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        Page<Cliente> dataPage;
        if (!page.isPresent() && !size.isPresent()) {
            dataPage = repository.findAllByBorradoIsFalse(PageRequest.of(currentPage - 1, pageSize));
        } else {
            dataPage = repository.findAllByBorradoIsFalse(PageRequest.of(page.get() - 1, size.get()));
        }
        int totalPages = dataPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("titulo", "Listado de Clientes");
        model.addAttribute("data", dataPage);
        return "clientes/indexcliente";
    }

    @GetMapping(value = "/edit/{id}")
    public String modificarCliente(@PathVariable Long id, Model model) {
        Cliente cliente = repository.findById(id).get();
        model.addAttribute("titulo", "Modificado de Cliente");
        model.addAttribute("cliente", cliente);
        return "clientes/update-cliente";
    }

    @PostMapping(value = "/update/{id}")
    public String updateCliente(@Valid Cliente cliente, RedirectAttributes redirectAttributes) {
        repository.save(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente Modificado con Éxito"); //success
        return "redirect:/clientes/indexcliente";
    }

    @GetMapping("signup")
    public String showClientForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/registro_cliente";
    }

    @PostMapping("add")
    public String addCliente(@Valid Cliente cliente, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "clientes/registro_cliente";
        }
        if (repository.findAllByCuit(cliente.getCuit()).isEmpty()) {
            repository.save(cliente);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente Agregado Exitosamente");
            return "redirect:/clientes/indexcliente";
        } else {
            redirectAttributes.addFlashAttribute("mensajeWarning", "Error, Ese Cuit Existe");
            return "redirect:/clientes/signup";
        }
    }

    @GetMapping(value = "/delete-cliente/{id}")
    public String deleteCliente(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Cliente cliente = repository.findById(id).get();
        cliente.setBorrado(true);
        repository.save(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente Eliminado"); //danger
        return "redirect:/clientes/indexcliente";
    }
}
