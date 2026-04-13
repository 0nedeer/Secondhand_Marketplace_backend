package com.secondhand.marketplace.backend.modules.trade.service;

import com.secondhand.marketplace.backend.modules.trade.dto.CancelOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateLogisticsTraceRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateOrderRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.CreateShipmentRequest;
import com.secondhand.marketplace.backend.modules.trade.dto.PickupVerifyRequest;
import com.secondhand.marketplace.backend.modules.trade.vo.LogisticsTraceVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderDetailVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderPageVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderShipmentVO;
import com.secondhand.marketplace.backend.modules.trade.vo.OrderStatusLogVO;

import java.util.List;

public interface TradeOrderService {

    Long createOrder(Long currentUserId, CreateOrderRequest request);

    OrderPageVO listOrders(Long currentUserId, String role, String orderStatus, long page, long pageSize);

    OrderDetailVO getOrderDetail(Long currentUserId, Long orderId);

    void cancelOrder(Long currentUserId, Long orderId, CancelOrderRequest request);

    void confirmReceipt(Long currentUserId, Long orderId);

    List<OrderStatusLogVO> getOrderStatusLogs(Long currentUserId, Long orderId);

    Long createShipment(Long currentUserId, Long orderId, CreateShipmentRequest request);

    void signShipment(Long currentUserId, Long orderId, Long shipmentId);

    void verifyPickup(Long currentUserId, Long orderId, Long shipmentId, PickupVerifyRequest request);

    OrderShipmentVO getShipmentDetail(Long currentUserId, Long orderId, Long shipmentId);

    Long createLogisticsTrace(Long currentUserId, Long orderId, Long shipmentId, CreateLogisticsTraceRequest request);

    List<LogisticsTraceVO> getLogisticsTraces(Long currentUserId, Long orderId, Long shipmentId);
}
