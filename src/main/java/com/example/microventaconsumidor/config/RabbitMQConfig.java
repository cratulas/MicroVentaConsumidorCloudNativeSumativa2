package com.example.microventaconsumidor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String VENTAS_QUEUE     = "cola_ventas";
    public static final String DLX_EXCHANGE     = "dlx-exchange";
    public static final String DLX_QUEUE        = "cola_ventas_dlx";
    public static final String DLX_ROUTING_KEY  = "ventas.dlx";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
        RabbitAdmin admin = new RabbitAdmin(cf);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public Queue ventasQueue() {
        return QueueBuilder
            .durable(VENTAS_QUEUE)
            .withArgument("x-dead-letter-exchange",    DLX_EXCHANGE)
            .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
            .build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder
            .bind(dlxQueue())
            .to(dlxExchange())
            .with(DLX_ROUTING_KEY);
    }

    // Control de mensajes de errores
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false); // 
        return factory;
    }
}
