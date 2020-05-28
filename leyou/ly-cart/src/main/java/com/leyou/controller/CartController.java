package com.leyou.controller;

import com.leyou.item.pojo.Sku;
import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     *
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 批量添加购物车
     *
     * @return
     */
    @PostMapping("list")
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> carts) {
        cartService.addCartList(carts);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 查询购物车列表
     *
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Cart>> queryCartList() {
        List<Cart> carts = cartService.queryCartList();
        if (carts == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(carts);
    }


    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart) {
        cartService.updateNum(cart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}
