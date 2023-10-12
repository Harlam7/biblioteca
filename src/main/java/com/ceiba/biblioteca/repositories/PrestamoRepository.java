package com.ceiba.biblioteca.repositories;

import com.ceiba.biblioteca.models.PrestamoModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoRepository extends CrudRepository<PrestamoModel, Long> {
    Optional<PrestamoModel> findByIdentificacionUsuario(String idUsuario);
}
