package com.example.microventaconsumidor.listener;

import com.example.microventaconsumidor.config.RabbitMQConfig;
import com.example.microventaconsumidor.model.DetalleVenta;
import com.example.microventaconsumidor.model.Venta;
import com.example.microventaconsumidor.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VentaListener {

    private final VentaRepository ventaRepository;
    private final RestTemplate restTemplate;

    @RabbitListener(
        queues = RabbitMQConfig.VENTAS_QUEUE,
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void recibirVenta(Venta venta) {
        try {
            if (venta.getFecha() == null) {
                venta.setFecha(LocalDateTime.now());
            }

            if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                throw new RuntimeException("Venta sin detalles");
            }

            for (DetalleVenta detalle : venta.getDetalles()) {
                if (detalle.getProductoId() == null || detalle.getCantidad() <= 0) {
                    throw new RuntimeException("Detalle inválido: productoId o cantidad nula");
                }

                String url = "http://microproducto:8081/productos/" + detalle.getProductoId() + "/descontar/" + detalle.getCantidad();
                restTemplate.put(url, null);

                detalle.setVenta(venta);
            }

            ventaRepository.save(venta);
            System.out.println("✅ Venta guardada correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al procesar venta: " + e.getMessage());
            throw new RuntimeException("Fallo al procesar venta, se enviará a DLX");
        }
    }
}
