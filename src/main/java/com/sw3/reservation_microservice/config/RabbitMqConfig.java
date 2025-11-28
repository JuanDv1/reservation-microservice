package com.sw3.reservation_microservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el microservicio de Reservas.
 * Publica eventos propios y escucha cambios de Barberos, Servicios y WorkShifts.
 */
@Configuration
public class RabbitMqConfig {

    // -------------------------------------------------------------------
    // 1. CONSTANTES (Nombres de Exchanges y Colas)
    // -------------------------------------------------------------------

    /** Exchange propio de reservas */
    public static final String RESERVATION_EXCHANGE = "reservation.exchange";

    /** Cola para eventos de barberos */
    public static final String BARBER_LISTENER_QUEUE = "reservation.barber.listener.queue";
    /** Cola para eventos de servicios */
    public static final String SERVICE_LISTENER_QUEUE = "reservation.service.listener.queue";
    /** Cola para eventos de workshifts */
    public static final String WORKSHIFT_LISTENER_QUEUE = "reservation.workshift.listener.queue";

    /** Exchange externo de barberos */
    public static final String BARBER_EXCHANGE = "barber.exchange";
    /** Exchange externo de servicios */
    public static final String SERVICE_EXCHANGE = "service.exchange";
    /** Exchange externo de workshifts */
    public static final String WORKSHIFT_EXCHANGE = "workshift.exchange";

    // -------------------------------------------------------------------
    // 2. CONFIGURACIÓN DE JSON
    // -------------------------------------------------------------------
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // -------------------------------------------------------------------
    // 3. EXCHANGE PROPIO (Producer)
    // -------------------------------------------------------------------
    @Bean
    public TopicExchange reservationExchange() {
        return new TopicExchange(RESERVATION_EXCHANGE);
    }

    // -------------------------------------------------------------------
    // 4. ESCUCHAR BARBEROS (Consumer)
    // -------------------------------------------------------------------
    @Bean
    public Queue barberListenerQueue() {
        return new Queue(BARBER_LISTENER_QUEUE, true);
    }

    @Bean
    public TopicExchange barberExchange() {
        return new TopicExchange(BARBER_EXCHANGE);
    }

    @Bean
    public Binding bindingBarberEvents(Queue barberListenerQueue, TopicExchange barberExchange) {
        return BindingBuilder.bind(barberListenerQueue).to(barberExchange).with("barber.#");
    }

    // -------------------------------------------------------------------
    // 5. ESCUCHAR SERVICIOS (Consumer)
    // -------------------------------------------------------------------
    @Bean
    public Queue serviceListenerQueue() {
        return new Queue(SERVICE_LISTENER_QUEUE, true);
    }

    @Bean
    public TopicExchange serviceExchange() {
        return new TopicExchange(SERVICE_EXCHANGE);
    }

    @Bean
    public Binding bindingServiceEvents(Queue serviceListenerQueue, TopicExchange serviceExchange) {
        return BindingBuilder.bind(serviceListenerQueue).to(serviceExchange).with("service.#");
    }

    // -------------------------------------------------------------------
    // 6. ESCUCHAR WORKSHIFTS (Consumer)
    // -------------------------------------------------------------------
    @Bean
    public Queue workshiftListenerQueue() {
        return new Queue(WORKSHIFT_LISTENER_QUEUE, true);
    }

    @Bean
    public TopicExchange workshiftExchange() {
        return new TopicExchange(WORKSHIFT_EXCHANGE);
    }

    @Bean
    public Binding bindingWorkshiftEvents(Queue workshiftListenerQueue, TopicExchange workshiftExchange) {
        return BindingBuilder.bind(workshiftListenerQueue).to(workshiftExchange).with("workshift.#");
    }
}
