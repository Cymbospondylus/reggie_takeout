package site.bzyl.dto;

import lombok.Data;
import site.bzyl.entity.OrderDetail;
import site.bzyl.entity.Orders;

import java.util.List;

@Data
public class OrdersDTO extends Orders {
    private List<OrderDetail> orderDetails;
}
