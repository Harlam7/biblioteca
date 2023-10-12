package com.ceiba.biblioteca.services;

import com.ceiba.biblioteca.models.PrestamoModel;
import com.ceiba.biblioteca.repositories.PrestamoRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PrestamoService {
    private PrestamoRepository prestamoRepository; //inyectar
    // Constructor que permite inyectar el repositorio
    public PrestamoService(PrestamoRepository prestamoRepository) {
        this.prestamoRepository = prestamoRepository;
    }
    public Map<String, String> crearPrestamo(PrestamoModel prestamo) {
        String idUsuario = prestamo.getIdentificacionUsuario();
        byte tipoUsuario = prestamo.getTipoUsuario();

        Map<String, String> respuesta = new HashMap<>();

        try {
            String fechaDevolucion = calcularFechaDevolucion(tipoUsuario);

            if (tipoUsuario == 3 && consultarPrestamoPorUsuario(idUsuario).isPresent()) {
                respuesta.put("mensaje", "El usuario con identificación " + idUsuario + " ya tiene un libro prestado por lo cual no se le puede realizar otro préstamo");
            } else if (tipoUsuario > 3) {
                respuesta.put("mensaje", "Tipo de usuario no permitido en la biblioteca");
            } else {
                prestamo.setFechaMaximaDevolucion(fechaDevolucion);
                PrestamoModel prestamoModel = prestamoRepository.save(prestamo);
                respuesta.put(String.valueOf(prestamoModel.getId()), fechaDevolucion);
            }
        } catch (IllegalArgumentException e) {
            respuesta.put("mensaje", "Tipo de usuario no permitido en la biblioteca");
        }

        return respuesta;
    }

    public Optional<PrestamoModel> consultarPrestamoPorId(PrestamoModel prestamo) {
        Optional<PrestamoModel> prestamoEncontrado = prestamoRepository.findById(prestamo.getId());

        if (prestamoEncontrado.isPresent()) {
            LocalDate fechaGuardada = LocalDate.parse(prestamoEncontrado.get().getFechaMaximaDevolucion(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            prestamoEncontrado.get().setFechaMaximaDevolucion(formatearFecha(fechaGuardada));
        }

        return prestamoEncontrado;
    }

    private String calcularFechaDevolucion(byte tipoUsuario) {
        LocalDate fechaHoy = LocalDate.now();
        int reglas = obtenerDiasReglamentarios(tipoUsuario);
        int suma = 0;

        while (suma < reglas) {
            fechaHoy = fechaHoy.plusDays(1);

            if (!esFinDeSemana(fechaHoy)) {
                suma++;
            }
        }

        return formatearFecha(fechaHoy);
    }

    private int obtenerDiasReglamentarios(byte tipoUsuario) {
        switch (tipoUsuario) {
            case 1:
                return 10;
            case 2:
                return 8;
            case 3:
                return 7;
            default:
                throw new IllegalArgumentException("Tipo de usuario no válido");
        }
    }

    private boolean esFinDeSemana(LocalDate fecha) {
        return fecha.getDayOfWeek() == DayOfWeek.SATURDAY || fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private String formatearFecha(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LL/yyyy");

        return formatter.format(date);
    }

    // este metodo se implementa para la consulta de prestamos x usuario
    private Optional<PrestamoModel> consultarPrestamoPorUsuario(String idUsuario) {
        return prestamoRepository.findByIdentificacionUsuario(idUsuario);
    }

}
