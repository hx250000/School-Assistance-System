package org.example.back.service;

import org.example.back.dto.request.NewShopItemRequest;
import org.example.back.dto.response.FileUploadResponse;
import org.example.back.entity.PointsLog;
import org.example.back.entity.ShopItem;
import org.example.back.entity.ShopOrder;
import org.example.back.entity.User;
import org.example.back.exception.AuthenticationException;
import org.example.back.exception.ResourceConflictException;
import org.example.back.exception.ResourceNotFoundException;
import org.example.back.repository.PointsLogRepository;
import org.example.back.repository.ShopItemRepository;
import org.example.back.repository.ShopRepository;
import org.example.back.repository.UserRepository;
import org.example.back.service.FileStorageService;
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

    @Mock
    private FileStorageService fileStorageService;

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
    void exchange_whenPointsNotEnough_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(10);
        item.setImageRes("img");
        item.setDescription("desc");

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(shopItemRepository.decreaseIfNotEmpty(5L)).thenReturn(1);
        when(userRepository.decreasePoints(1L, 100)).thenReturn(0);

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("积分不足");
    }

    @Test
    void exchange_whenOutOfStock_shouldThrowResourceConflictException() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(0);
        item.setImageRes("img");
        item.setDescription("desc");

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    void exchange_whenSuccess_shouldDeductPoints_writeLog_createOrder_decreaseStock() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(2);
        item.setImageRes("img");
        item.setDescription("desc");

        ShopOrder savedOrder = new ShopOrder();
        savedOrder.setId(99L);

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(userRepository.decreasePoints(1L, 100)).thenReturn(1);
        when(shopRepository.save(any(ShopOrder.class))).thenAnswer(invocation -> {
            ShopOrder order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });
        when(shopItemRepository.decreaseIfNotEmpty(5L)).thenReturn(1);

        Long orderId = shopService.exchange(5L);

        assertThat(orderId).isEqualTo(99L);

        verify(userRepository, times(1)).decreasePoints(1L, 100);

        ArgumentCaptor<PointsLog> logCaptor = ArgumentCaptor.forClass(PointsLog.class);
        verify(pointsLogRepository).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getChangeAmount()).isEqualTo(-100);

        ArgumentCaptor<ShopOrder> orderCaptor = ArgumentCaptor.forClass(ShopOrder.class);
        verify(shopRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getUserId()).isEqualTo(1L);
        assertThat(orderCaptor.getValue().getItemId()).isEqualTo(5L);
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo("PAID");

        verify(shopItemRepository, times(1)).decreaseIfNotEmpty(5L);
    }

    @Test
    void exchange_whenStockDecreaseFails_shouldNotDeductPoints() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(0);
        item.setImageRes("img");
        item.setDescription("desc");

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(shopItemRepository.decreaseIfNotEmpty(5L)).thenReturn(0); // 库存扣减失败

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("库存不足");

        verify(userRepository, never()).decreasePoints(anyLong(), anyInt());
    }

    @Test
    void exchange_whenPointsDeductFails_shouldThrowException() {
        AuthTestUtil.setCurrentUserId(1L);

        ShopItem item = new ShopItem();
        item.setId(5L);
        item.setPrice(100);
        item.setStock(2);
        item.setImageRes("img");
        item.setDescription("desc");

        when(shopItemRepository.findById(5L)).thenReturn(Optional.of(item));
        when(shopItemRepository.decreaseIfNotEmpty(5L)).thenReturn(1);
        when(userRepository.decreasePoints(1L, 100)).thenReturn(0); // 积分扣减失败

        assertThatThrownBy(() -> shopService.exchange(5L))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("积分不足");

        verify(userRepository, times(1)).decreasePoints(1L, 100);
        verify(pointsLogRepository, never()).save(any(PointsLog.class));
        verify(shopRepository, never()).save(any(ShopOrder.class));
    }

    @Test
    void exchangeCount_whenNotLoggedIn_shouldThrowAuthenticationException() {
        AuthTestUtil.clear();

        assertThatThrownBy(() -> shopService.exchangeCount())
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户未登录");
    }

    @Test
    void exchangeCount_whenUserNotFound_shouldThrowResourceNotFoundException() {
        AuthTestUtil.setCurrentUserId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shopService.exchangeCount())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    void exchangeCount_whenSuccess_shouldReturnCount() {
        AuthTestUtil.setCurrentUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pointsLogRepository.countByUserIdAndTitle(1L, "兑换商品")).thenReturn(5);

        Long count = shopService.exchangeCount();

        assertThat(count).isEqualTo(5L);
        verify(pointsLogRepository, times(1)).countByUserIdAndTitle(1L, "兑换商品");
    }

    @Test
    void addItem_shouldSaveAndReturnId() {
        NewShopItemRequest req = new NewShopItemRequest();
        req.setName("n");
        req.setPrice(10);
        req.setStock(1);
        req.setDescription("d");
        req.setImageRes("img");

        ShopItem saved = new ShopItem();
        saved.setId(7L);
        when(shopItemRepository.save(any(ShopItem.class))).thenReturn(saved);

        assertThat(shopService.addItem(req)).isEqualTo(7L);
    }

    @Test
    void uploadShopitemImage_shouldReturnFileUploadResponse() {
        when(fileStorageService.storeFile(any(), eq("shopitem"))).thenReturn("uploads/shopitem/test.jpg");

        FileUploadResponse response = shopService.uploadShopitemImage(null);

        assertThat(response).isNotNull();
        assertThat(response.getType()).isEqualTo("shopitem");
        assertThat(response.getFileUrl()).isEqualTo("uploads/shopitem/test.jpg");
        verify(fileStorageService, times(1)).storeFile(any(), eq("shopitem"));
    }
}

