package org.example.back.service;

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
import org.example.back.service.impl.ShopServiceImpl;
import org.example.back.testutil.AuthTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock
    private ShopItemRepository shopItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointsLogRepository pointsLogRepository;

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopServiceImpl shopService;

    @AfterEach
    void tearDown() {
        AuthTestUtil.clear();
    }

    @Test
    void listItems_shouldReturnAll() {
        when(shopItemRepository.findAll()).thenReturn(List.of(new ShopItem(), new ShopItem()));

        assertThat(shopService.listItems()).hasSize(2);
    }

    @Test
    void exchange_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> shopService.exchange(1L))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void exchange_whenItemNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(1L);
        when(shopItemRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("商品不存在");
    }

    @Test
    void exchange_whenUserNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(1L);
        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(new ShopItem()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void exchange_whenPointsNotEnough_shouldThrowIllegalArgumentException() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(10);

        User user = new User();
        user.setId(1L);
        user.setPoints(99);

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("积分不足");
    }

    @Test
    void exchange_whenOutOfStock_shouldThrowIllegalArgumentException() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(0);

        User user = new User();
        user.setId(1L);
        user.setPoints(200);

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    void exchange_whenSuccess_shouldDeductPoints_writeLog_createOrder_decreaseStock() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(2);

        User user = new User();
        user.setId(1L);
        user.setPoints(150);

        ShopOrder savedOrder = new ShopOrder();
        savedOrder.setId(99L);

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shopRepository.save(any(ShopOrder.class))).thenAnswer(invocation -> {
            ShopOrder order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });

        Long orderId = shopService.exchange(5L);

        assertThat(orderId).isEqualTo(99L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPoints()).isEqualTo(50);

        ArgumentCaptor<PointsLog> logCaptor = ArgumentCaptor.forClass(PointsLog.class);
        verify(pointsLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getChangeAmount()).isEqualTo(-100);

        ArgumentCaptor<ShopOrder> orderCaptor = ArgumentCaptor.forClass(ShopOrder.class);
        verify(shopRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getUserId()).isEqualTo(1L);
        assertThat(orderCaptor.getValue().getItemId()).isEqualTo(5L);
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo("PAID");

        ArgumentCaptor<ShopItem> itemCaptor = ArgumentCaptor.forClass(ShopItem.class);
        verify(shopItemRepository).save(itemCaptor.capture());
        assertThat(itemCaptor.getValue().getStock()).isEqualTo(1);
    }

    @Test
    void addItem_shouldSaveAndReturnId() {
        NewShopItemRequest req = new NewShopItemRequest();
        req.setName("n");
        req.setPrice(10);
        req.setStock(1);
        req.setDescription("d");

        ShopItem saved = new ShopItem();
        saved.setId(7L);
        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(saved);

        assertThat(shopService.addItem(req)).isEqualTo(7L);
    }
}

