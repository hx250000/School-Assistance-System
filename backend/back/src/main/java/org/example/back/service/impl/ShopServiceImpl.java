package org.example.back.service.impl;

import org.example.back.config.JwtAuthenticationInterceptor;
import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.response.FileUploadResponse;
import org.example.back.entity.PointsLog;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceConflictException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.*;
import org.example.back.service.FileStorageService;
import org.example.back.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    private static final Logger log = LoggerFactory.getLogger(ShopServiceImpl.class);
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointsLogRepository pointsLogRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ShopOrderRepository shopOrderRepository;

    @Override
    public List<ShopItem> listItems() {
        var res=shopItemRepository.findAll();
        log.info("shopitems= "+res);
        return res;
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

        int stockUpdated = shopItemRepository.decreaseIfNotEmpty(itemId);
        if (stockUpdated == 0) {
            throw new ResourceConflictException("库存不足");
        }

        int updated = userRepository.decreasePoints(userId, item.getPrice());
        if (updated == 0) {
            throw new ResourceConflictException("积分不足");
        }

        // 写日志
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setChangeAmount(-item.getPrice());
        log.setTitle("兑换商品");
        log.setDescription("兑换商品"+item.getName());
        pointsLogRepository.save(log);


        // 订单
        ShopOrder order = new ShopOrder();
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setItemName(item.getName());
        order.setPrice(Long.valueOf(item.getPrice()));
        order.setStatus("PAID");
        var res=shopRepository.save(order);

        return res.getId();

    }

    @Override
    public Long addItem(NewShopItemRequest request) {
        validateShopitem(request);
        ShopItem newItem = new ShopItem();
        newItem.setName(request.getName());
        newItem.setPrice(request.getPrice());
        newItem.setStock(request.getStock());
        newItem.setDescription(request.getDescription());
        newItem.setImageRes(request.getImageRes());
        log.info("add item= "+newItem);
        ShopItem save=shopItemRepository.save(newItem);
        return save.getId();
    }

    @Override
    public Long exchangeCount(){
        Long userId = JwtAuthenticationInterceptor.getCurrentUserId();
        if (userId == null) {
            throw new AuthenticationException("用户未登录");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        String title="兑换商品";
        long count=pointsLogRepository.countByUserIdAndTitle(userId,title);
        log.info("exchangeCount: user={}, count={}", user.getId(), count);
        return count;
    }

    @Override
    public List<ShopOrder> listShopOrders() {
        List<ShopOrder> list=shopOrderRepository.findAllByOrderByCreatedAtDesc();
        log.info("listShopOrders= "+list);
        return list;
    }

    // 复用文件存储服务，把图片存入 uploads/shop/ 目录下
    @Override
    public FileUploadResponse uploadShopitemImage(MultipartFile file) {
        String url=fileStorageService.storeFile(file,"shopitem");
        FileUploadResponse response=new FileUploadResponse();
        response.setType("shopitem");
        response.setFileUrl(url);
        return response;
    }

    public void validateShopitem(NewShopItemRequest request) {
        if (request.getName()==null||request.getName().isBlank()){
            throw new IllegalArgumentException("商品名不能为空！");
        }
        if (request.getDescription()==null){
            request.setDescription("");
        }
        if (request.getPrice()==null||request.getPrice()<=0){
            throw new IllegalArgumentException("商品价格不能为空或小于0！");
        }
        if (request.getStock()==null||request.getStock()<0){
            throw new IllegalArgumentException("商品库存不能为空或小于0！");
        }
        if (request.getImageRes()==null){
            request.setImageRes("");
        }
    }
}