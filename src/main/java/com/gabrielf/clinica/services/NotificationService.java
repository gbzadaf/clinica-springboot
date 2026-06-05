package com.gabrielf.clinica.services;

import com.gabrielf.clinica.model.Appointment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appointment.getPatient().getEmail());
        message.setSubject("Consulta agendada com sucesso!");
        message.setText(String.format("""
            Olá, %s!
            
            Sua consulta foi agendada com sucesso.
            
            Médico: %s
            Especialidade: %s
            Data e hora: %s
            Duração: %d minutos
            
            Em caso de cancelamento, lembre-se de avisar com 24h de antecedência.
            
            Atenciosamente,
            Clínica
            """,
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getScheduledAt(),
                appointment.getDurationMinutes()
        ));

        mailSender.send(message);


    }

    @Async
    public void sendAppointmentCancellation(Appointment appointment) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appointment.getPatient().getEmail());
        message.setSubject("Consulta cancelada com sucesso!");
        message.setText(String.format("""
                        Olá, %s!
                        
                        Sua consulta foi cancelada.
                        
                        Médico: %s
                        Data e hora: %s
                        
                        Para reagendar, acesse nossa plataforma.
                        
                        Atenciosamente,
                        Clínica
                        """,
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getScheduledAt()
        ));

        mailSender.send(message);

    }

    @Async
    public void sendAppointmentReminder(Appointment appointment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(appointment.getPatient().getEmail());
        message.setSubject("Lembrete de consulta amanhã");
        message.setText(String.format("""
                        Olá, %s!
                        
                        Lembramos que você tem uma consulta amanhã.
                        
                        Médico: %s
                        Especialidade: %s
                        Data e hora: %s
                        
                        Atenciosamente,
                        Clínica
                        """,
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getScheduledAt()
        ));

        mailSender.send(message);

    }



}
