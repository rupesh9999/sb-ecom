package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartServiceImpl  implements CartService{
    private final CartRepository cartRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId"));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product" + product.getProductName() + "already exists in cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException("Product" + product.getProductName() + "is out of stock");
        }
        // Create Cart iTEM
        // sAVE CART iTEM
        // rETURN Updated cart
        return null;
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart = cartRepository.save(cart);
        return cartRepository.save(cart);
    }
}
