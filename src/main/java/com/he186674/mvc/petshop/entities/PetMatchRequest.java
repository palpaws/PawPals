package com.he186674.mvc.petshop.entities;




import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "pet_match_requests")
public class PetMatchRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id")
    private Integer requestId;



    @ManyToOne
    @JoinColumn(name="sender_pet_id")
    private Pet senderPet;



    @ManyToOne
    @JoinColumn(name="receiver_pet_id")
    private Pet receiverPet;



    private String message;


    @Column(name="status")
    private String status;


    @Column(name="created_at")
    private LocalDateTime createdAt;



    public Integer getRequestId() {
        return requestId;
    }


    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }


    public Pet getSenderPet() {
        return senderPet;
    }


    public void setSenderPet(Pet senderPet) {
        this.senderPet = senderPet;
    }


    public Pet getReceiverPet() {
        return receiverPet;
    }


    public void setReceiverPet(Pet receiverPet) {
        this.receiverPet = receiverPet;
    }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}