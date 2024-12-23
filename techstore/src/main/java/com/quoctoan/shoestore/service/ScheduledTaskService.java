package com.quoctoan.shoestore.service;

import com.quoctoan.shoestore.entity.Order;
import com.quoctoan.shoestore.entity.OrderItem;
import com.quoctoan.shoestore.entity.OrderStatus;
import com.quoctoan.shoestore.entity.ProductItem;
import com.quoctoan.shoestore.respository.OrderRepository;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledTaskService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductItemRepository productItemRepository;

//    @Autowired
//    private ProductRepository productRepository;
//
//    // Lập lịch chạy mỗi ngày lúc 12:00 (theo format cron)
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void runScheduledTask() {
//        System.out.println("Đang thực thi tác vụ hẹn giờ...");
//
//        // Thực hiện truy vấn MySQL thông qua repository
//        long count = productRepository.count();
//        System.out.println("Số lượng sản phẩm trong cơ sở dữ liệu: " + count);
//
//        // Hoặc các thao tác khác
//    }
@Async
public void scheduleOrderCheck(Integer orderId) {
    try {
         TimeUnit.MINUTES.sleep(1);
//         TimeUnit.HOURS.sleep(24);

        // Kiểm tra trạng thái đơn hàng
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // Nếu chưa thanh toán, hủy đơn hàng
            if (order.getOrderStatus().getId() == 6) {
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setId(8);
                order.setOrderStatus(orderStatus);
                order.setNote("Đơn hàng chưa thanh toán trong thời hạn.");
                orderRepository.save(order);
                List<OrderItem> orderItemList = order.getOrderItems().stream().toList();
                for (OrderItem orderItem : orderItemList) {
                    Optional<ProductItem> productItemOptional = productItemRepository.findById(orderItem.getProductItem().getId());
                    if (productItemOptional.isPresent()){
                        ProductItem productItem = productItemOptional.get();
                        productItem.setQuantityInStock(productItem.getQuantityInStock() + orderItem.getQuantity());
                        productItemRepository.save(productItem);
                    }
                }
                System.out.println("Đơn hàng " + orderId + " đã bị hủy vì chưa thanh toán sau 24 giờ.");
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println("Tác vụ bị gián đoạn: " + e.getMessage());
    }
}
}

