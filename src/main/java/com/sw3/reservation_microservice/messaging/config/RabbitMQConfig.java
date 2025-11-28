package com.sw3.reservation_microservice.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de reservas.
 * Define queues, exchanges y bindings para sincronizar barberos y servicios.
 */
@Configuration
public class RabbitMQConfig {

    // ==================== BARBER QUEUES ====================
    public static final String BARBER_QUEUE = "reservation.barber.queue";
    public static final String BARBER_EXCHANGE = "barber.exchange";
    public static final String BARBER_CREATED_ROUTING_KEY = "barber.created";
    public static final String BARBER_UPDATED_ROUTING_KEY = "barber.updated";
    public static final String BARBER_DELETED_ROUTING_KEY = "barber.deleted";

    // ==================== SERVICE QUEUES ====================
    public static final String SERVICE_QUEUE = "reservation.service.queue";
    public static final String SERVICE_EXCHANGE = "service.exchange";
    public static final String SERVICE_CREATED_ROUTING_KEY = "service.created";
    public static final String SERVICE_UPDATED_ROUTING_KEY = "service.updated";
    public static final String SERVICE_DELETED_ROUTING_KEY = "service.deleted";

    // ==================== MESSAGE CONVERTER ====================
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // ==================== BARBER CONFIGURATION ====================
    @Bean
    public Queue barberQueue() {
        return new Queue(BARBER_QUEUE, true); // durable = true
    }

    @Bean
    public TopicExchange barberExchange() {
        return new TopicExchange(BARBER_EXCHANGE);
    }

    @Bean
    public Binding barberCreatedBinding() {
        return BindingBuilder
                .bind(barberQueue())
                .to(barberExchange())
                .with(BARBER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding barberUpdatedBinding() {
        return BindingBuilder
                .bind(barberQueue())
                .to(barberExchange())
                .with(BARBER_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding barberDeletedBinding() {
        return BindingBuilder
                .bind(barberQueue())
                .to(barberExchange())
                .with(BARBER_DELETED_ROUTING_KEY);
    }

    // ==================== SERVICE CONFIGURATION ====================
    @Bean
    public Queue serviceQueue() {
        return new Queue(SERVICE_QUEUE, true); // durable = true
    }

    @Bean
    public TopicExchange serviceExchange() {
        return new TopicExchange(SERVICE_EXCHANGE);
    }

    @Bean
    public Binding serviceCreatedBinding() {
        return BindingBuilder
                .bind(serviceQueue())
                .to(serviceExchange())
                .with(SERVICE_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding serviceUpdatedBinding() {
        return BindingBuilder
                .bind(serviceQueue())
                .to(serviceExchange())
                .with(SERVICE_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding serviceDeletedBinding() {
        return BindingBuilder
                .bind(serviceQueue())
                .to(serviceExchange())
                .with(SERVICE_DELETED_ROUTING_KEY);
    }
}
