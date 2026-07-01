package com.he186674.mvc.petshop.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "service_slots")
public class ServiceSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Integer slotId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ShopService service;

    @Column(name = "slot_date", nullable = false)
    private LocalDate slotDate;

    @Column(name = "start_time", nullable = false)
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "capacity")
    private Integer capacity = 1;

    @Column(name = "booked_count")
    private Integer bookedCount = 0;

    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }

    public ShopService getService() { return service; }
    public void setService(ShopService service) { this.service = service; }

    public LocalDate getSlotDate() { return slotDate; }
    public void setSlotDate(LocalDate slotDate) { this.slotDate = slotDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    @Transient
    public boolean isFull() {
        return bookedCount != null && capacity != null && bookedCount >= capacity;
    }
}
