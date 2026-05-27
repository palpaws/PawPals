package com.he186674.mvc.petshop.entities;



import jakarta.persistence.*;

@Entity
@Table(name = "OrderDetails")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderDetailId;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    private Integer quantity;
    private Double unitPrice;

    // ===== Getter & Setter =====

    public Integer getOrderDetailId() { return orderDetailId; }
    public void setOrderDetailId(Integer orderDetailId) { this.orderDetailId = orderDetailId; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
}
