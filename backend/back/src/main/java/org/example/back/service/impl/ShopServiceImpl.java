package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.entity.PointsLog;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.PointsLogRepository;
import org.example.back.repository.ShopItemRepository;
import org.example.back.repository.ShopRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Override
    public List<ShopItem> listItems() {
        return shopItemRepository.findAll();
    }

    @Override
    @Transactional
    public Long exchange(Long itemId) {

        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录，无法兑换商品");
        }

        ShopItem item = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("商品不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));

        if (user.getPoints() < item.getPrice()) {
            throw new IllegalArgumentException("积分不足");
        }

        if (item.getStock() == null || item.getStock() <= 0) {
            throw new IllegalArgumentException("库存不足");
        }

        // 扣积分
        user.setPoints(user.getPoints() - item.getPrice());
        userRepository.save(user);

        // 写日志
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(-item.getPrice());
        log.setTitle("兑换商品");
        pointsLogRepository.save(log);

        // 订单
        ShopOrder order = new ShopOrder();
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setStatus("PAID");
        shopRepository.save(order);

        // 库存
        item.setStock(item.getStock() - 1);
        shopItemRepository.save(item);
        return order.getId();
    }

    @Override
    public Long addItem(NewShopItemRequest request) {
        ShopItem newItem = new ShopItem();
        newItem.setName(request.getName());
        newItem.setPrice(request.getPrice());
        newItem.setStock(request.getStock());
        newItem.setDescription(request.getDescription());

        ShopItem save=shopItemRepository.save(newItem);
        return save.getId();
    }


}