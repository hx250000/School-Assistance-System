package org.example.back.service.impl;

import org.example.back.entity.PointsLog;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.example.back.entity.User;
import org.example.back.mapper.PointsLogMapper;
import org.example.back.mapper.ShopMapper;
import org.example.back.mapper.UserMapper;
import org.example.back.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PointsLogMapper pointsLogMapper;

    @Override
    public List<ShopItem> listItems() {
        return shopMapper.listItems();
    }

    @Override
    public void exchange(Long itemId) {

        Long userId = 1L;

        ShopItem item = shopMapper.getById(itemId);
        User user = userMapper.selectById(userId);

        if (user.getPoints() < item.getPrice()) {
            throw new RuntimeException("积分不足");
        }

        // 扣积分
        user.setPoints(user.getPoints() - item.getPrice());
        userMapper.update(user);

        // 写日志
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(-item.getPrice());
        log.setReason("兑换商品");

        pointsLogMapper.insert(log);

        // 订单
        ShopOrder order = new ShopOrder();
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setStatus("PAID");

        shopMapper.insertOrder(order);

        // 库存
        shopMapper.updateStock(itemId);
    }
}