package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



@Service
public class CartServiceImpl  implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);

        if (cartItem != null) {
            throw new APIException("Product" + product.getProductName() + "already exists in cart");
        }

        if (product.getQuantity() == 0) {
            throw new APIException("Product" + product.getProductName() + "is out of stock");
        }

        if (product.getQuantity() < quantity) {
              throw new APIException("Please, make a n order of the " + product.getProductName() + " less than or equal to quantity" +  product.getQuantity());
        }

        CartItem newCartItem = new CartItem();

        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        
        // Explicitly add the new item to the cart's list so it appears in the response
        cart.getCartItems().add(newCartItem);

        cartRepository.save(cart);
        productRepository.save(product);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            map.setDiscount(item.getDiscount());
            return map;
        });

        cartDTO.setProducts(productDTOStream.toList());

        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new APIException("No carts found");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(p -> {
                ProductDTO map = modelMapper.map(p.getProduct(), ProductDTO.class);
                map.setQuantity(p.getQuantity());
                map.setDiscount(p.getDiscount());
                return map;
            }).collect(Collectors.toList());
            cartDTO.setProducts(products);
            return cartDTO;


        }).collect(Collectors.toList());



        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        // Populate products
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> {
                    ProductDTO map = modelMapper.map(p.getProduct(), ProductDTO.class);
                    map.setQuantity(p.getQuantity());
                    map.setDiscount(p.getDiscount());
                    return map;
                })
                .collect(Collectors.toList());
        cartDTO.setProducts(products);
        
        return cartDTO;
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
