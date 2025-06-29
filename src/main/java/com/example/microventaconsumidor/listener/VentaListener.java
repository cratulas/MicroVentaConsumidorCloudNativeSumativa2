package com.example.microventaconsumidor.listener;

import com.example.microventaconsumidor.config.RabbitMQConfig;
import com.example.microventaconsumidor.model.Venta;
import com.example.microventaconsumidor.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VentaListener {

    private final VentaRepository ventaRepository;

    @RabbitListener(
        queues = RabbitMQConfig.VENTAS_QUEUE,
        containerFactory = "rabbitListenerContainerFactory" 
    )
    public void recibirVenta(Venta venta) {
        try {
            if (venta.getFecha() == null) {
                venta.setFecha(LocalDateTime.now());
            }

            if (venta.getDetalles() != null) {
                venta.getDetalles().forEach(d -> d.setVenta(venta));
            }

            ventaRepository.save(venta);
            System.out.println("✅ Venta guardada correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error procesando venta, irá a DLX: " + e.getMessage());
            throw new RuntimeException("Error procesando mensaje, reenviar a DLX");
        }
    }
}
