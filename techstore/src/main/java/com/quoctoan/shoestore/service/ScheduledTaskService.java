package com.quoctoan.shoestore.service;

import com.quoctoan.shoestore.entity.*;
import com.quoctoan.shoestore.respository.OrderRepository;
import com.quoctoan.shoestore.respository.ProductItemRepository;
import com.quoctoan.shoestore.respository.UpdatePriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ScheduledTaskService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductItemRepository productItemRepository;
    @Autowired
    private UpdatePriceRepository updatePriceRepository;

    @Async
    public void scheduleOrderCheck(Integer orderId) {
        try {
    //         TimeUnit.MINUTES.sleep(1);
             TimeUnit.HOURS.sleep(24);

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

    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void updatePrices() {
        LocalDateTime now = LocalDateTime.now();

        List<UpdatePrice> updatePrices = updatePriceRepository.findByStartDateTimeBefore(now);

        for (UpdatePrice updatePrice : updatePrices) {
            if(updatePrice.getStatus().equals("WAITING")){
                ProductItem productItem = updatePrice.getProductItem();

                productItem.setPrice(updatePrice.getPrice());
                productItemRepository.save(productItem);
                updatePrice.setStatus("DONE");
                updatePriceRepository.save(updatePrice);

                System.out.println("Updated price for ProductItem ID: " + productItem.getId());
            }

        }
    }
}

