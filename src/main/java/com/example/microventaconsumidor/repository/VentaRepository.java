// VentaRepository.java
package com.example.microventaconsumidor.repository;

import com.example.microventaconsumidor.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> { }
