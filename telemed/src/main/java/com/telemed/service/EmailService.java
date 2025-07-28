
// 2. EmailService.java
package com.telemed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.telemed.model.Appointment;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVideoCallNotification(Appointment appointment, String videoRoomUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            // Set recipient (patient email)
            message.setTo(appointment.getPatient().getEmail());
            
            // Set subject
            message.setSubject("Your Telemedicine Appointment is Ready - Video Call Link");
            
            // Format appointment date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");
            String formattedDate = appointment.getAppointmentDate().format(formatter);
            
            // Create email body
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your telemedicine appointment with Dr. %s is ready to begin!\n\n" +
                "📅 Appointment Details:\n" +
                "Date & Time: %s\n" +
                "Doctor: %s\n" +
                "Specialty: %s\n\n" +
                "🎥 JOIN YOUR VIDEO CALL:\n" +
                "%s\n\n" +
                "📋 Instructions:\n" +
                "1. Click the link above or copy and paste it into your browser\n" +
                "2. Allow camera and microphone access when prompted\n" +
                "3. Wait for your doctor to join the call\n" +
                "4. Have a list of your questions and current medications ready\n\n" +
                "⚠️ Important Notes:\n" +
                "- Please join the call on time\n" +
                "- Ensure you have a stable internet connection\n" +
                "- Find a quiet, private space for your consultation\n" +
                "- Keep your ID ready for verification if needed\n\n" +
                "If you experience any technical issues, please contact our support team.\n\n" +
                "Best regards,\n" +
                "Telemedicine Team",
                
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                formattedDate,
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                videoRoomUrl
            );
            
            message.setText(emailBody);
            
            // Send email
            mailSender.send(message);
            
            System.out.println("Video call notification sent to: " + appointment.getPatient().getEmail());
            
        } catch (Exception e) {
            System.err.println("Failed to send video call notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendAppointmentReminder(Appointment appointment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setTo(appointment.getPatient().getEmail());
            message.setSubject("Reminder: Your Telemedicine Appointment Tomorrow");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");
            String formattedDate = appointment.getAppointmentDate().format(formatter);
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "This is a friendly reminder about your upcoming telemedicine appointment.\n\n" +
                "📅 Appointment Details:\n" +
                "Date & Time: %s\n" +
                "Doctor: %s\n" +
                "Specialty: %s\n\n" +
                "💡 To prepare for your appointment:\n" +
                "- Prepare a list of your current symptoms\n" +
                "- Have your current medications list ready\n" +
                "- Ensure you have a stable internet connection\n" +
                "- Find a quiet, private space\n" +
                "- Test your camera and microphone\n\n" +
                "You will receive the video call link when your doctor starts the session.\n\n" +
                "Looking forward to your consultation!\n\n" +
                "Best regards,\n" +
                "Telemedicine Team",
                
                appointment.getPatient().getName(),
                formattedDate,
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty()
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
            System.out.println("Appointment reminder sent to: " + appointment.getPatient().getEmail());
            
        } catch (Exception e) {
            System.err.println("Failed to send appointment reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void sendUrgentCallReminder(Appointment appointment, String videoRoomUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setTo(appointment.getPatient().getEmail());
            message.setSubject("🚨 Your Appointment Starts in 15 Minutes - Join Now!");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "⏰ Your telemedicine appointment with Dr. %s starts in 15 minutes!\n\n" +
                "🎥 JOIN NOW:\n" +
                "%s\n\n" +
                "Click the link above to join your video call immediately.\n\n" +
                "Best regards,\n" +
                "Telemedicine Team",
                
                appointment.getPatient().getName(),
                appointment.getDoctor().getName(),
                videoRoomUrl
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
            System.out.println("Urgent call reminder sent to: " + appointment.getPatient().getEmail());
            
        } catch (Exception e) {
            System.err.println("Failed to send urgent call reminder: " + e.getMessage());
            e.printStackTrace();
        }
    }
}