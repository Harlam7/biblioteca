package com.ceiba.biblioteca.controller;

import com.ceiba.biblioteca.models.PrestamoModel;
import com.ceiba.biblioteca.services.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/prestamo")
public class PrestamoControlador {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<Map<String, String>> crearPrestamo(@RequestBody PrestamoModel prestamo, HttpServletResponse response) {
        Map<String, String> resultadoPrestamo = prestamoService.crearPrestamo(prestamo);

        if (resultadoPrestamo.containsKey("mensaje")) {
            return new ResponseEntity<>(resultadoPrestamo, HttpStatus.BAD_REQUEST);
        } else {
            String id = resultadoPrestamo.keySet().iterator().next();
            String fechaDevolver = resultadoPrestamo.get(id);
            resultadoPrestamo.put("id", id);
            resultadoPrestamo.put("fechaMaximaDevolucion", fechaDevolver);
            resultadoPrestamo.remove(id);
            return new ResponseEntity<>(resultadoPrestamo, HttpStatus.OK);
        }
    }

    @GetMapping("/{id-prestamo}")
    public ResponseEntity<Optional<PrestamoModel>> consultarPrestamoPorId(@PathVariable("id-prestamo") Long id) {
        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setId(id);
        Optional<PrestamoModel> prestamoEncontrado = prestamoService.consultarPrestamoPorId(prestamo);

        if (prestamoEncontrado.isPresent()) {
            return new ResponseEntity<>(prestamoEncontrado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
