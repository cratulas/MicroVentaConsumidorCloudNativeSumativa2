package com.example.microventaconsumidor.controller;

import com.example.microventaconsumidor.model.Venta;
import com.example.microventaconsumidor.repository.VentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
@CrossOrigin
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @GetMapping
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }
}
