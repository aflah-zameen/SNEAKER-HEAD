package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Mappers.AddressMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.CartMapper;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import com.e_commerce.SNEAKERHEAD.Service.OrderService;
import com.e_commerce.SNEAKERHEAD.Service.ProductService;
import com.e_commerce.SNEAKERHEAD.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    ProductService productService;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemsRepository orderItemsRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    CartMapper cartMapper;

    @Autowired
    AddressMapper addressMapper;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    CouponRepository couponRepository;


    @GetMapping("/home")
    public String AdminProduct()
    {
        return "index";
    }
    @GetMapping("/women")
    public String WomenProduct(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        Category category = categoryRepository.findByName("Women").orElseThrow(()->new NullPointerException());
        List<ProductDto> products = productService.categoryProduct(category,user.getId());
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        model.addAttribute("products",products);
        model.addAttribute("url","women");
        model.addAttribute("category","Women");
        model.addAttribute("brands",brands);
        model.addAttribute("breadcrumb","Women");
        return "productList";
    }

    @GetMapping("/product/filter")
    public ResponseEntity<List<ProductDto>> filterWomenProducts(@RequestParam String filterType,@RequestParam String filterValue,@RequestParam String category,Model model)
    {
//        if(category.equals("Available") || category.equals("Search"))
//        {
//
//            return ResponseEntity.ok(productDtos);
//        }
//        else {
//            Category categoryObject = categoryRepository.findByName(category).orElseThrow(() -> new NullPointerException());
//            List<ProductDto> productDTOs = productService.filterProduct(categoryObject, filterValue);
//            return ResponseEntity.ok(productDTOs);
//        }
        List<ProductDto> productDtos=productService.filterProduct(filterType,filterValue);
        if(productDtos.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(productDtos);
        else
        return ResponseEntity.ok(productDtos);
    }


    @GetMapping("/men")
    public String MenProduct(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        Category category = categoryRepository.findByName("Men").orElseThrow(()->new NullPointerException());
        List<ProductDto> products = productService.categoryProduct(category,user.getId());
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        model.addAttribute("products",products);
        model.addAttribute("url","men");
        model.addAttribute("category","Men");
        model.addAttribute("brands",brands);
        model.addAttribute("breadcrumb","Men");
        return "productList";
    }

    @GetMapping("/product/detail/{id}")
    public String EditProduct(@PathVariable("id") Long id, Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        ProductDto details = productService.FindProduct(id);
        model.addAttribute("userId",user.getId());
        model.addAttribute("details",details);
        model.addAttribute("categoryUrl",details.getCategoryName().toLowerCase());
        return "productdetails";
    }

    @GetMapping("product/buyNow")
    public String buyProduct(@RequestParam int selectedQuantity,
                             @RequestParam double price,
                             @RequestParam Long variantId,
                             @RequestParam String selectedSize, HttpServletRequest request,Model model)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String) session.getAttribute("userEmail")).orElse(new WebUser());
        UserAddress address = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE").stream().filter(a->a.getDefaultAddressStatus()).findFirst().orElse(new UserAddress());
        Double totalPrice = price*selectedQuantity;
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String FormattedSubTotal = formatter.format(totalPrice);
        model.addAttribute("addressObject",new AddressDTO());
        model.addAttribute("defaultAddress",address);
        model.addAttribute("subtotalFormatted",FormattedSubTotal);
        model.addAttribute("subtotal",totalPrice);
        model.addAttribute("variantId",variantId);
        model.addAttribute("quantity",selectedQuantity);
        model.addAttribute("selectedSize",selectedSize);
        return "direct-checkout";
    }

    @PostMapping("/product/buy")
    public ResponseEntity<?>  buyProduct( @RequestBody OrderDto orderDto,HttpServletRequest request)
    {

        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String) session.getAttribute("userEmail")).orElse(new WebUser());
        Order order = new Order();
        UserAddress address = addressRepository.findById(orderDto.getAddressId()).orElse(new UserAddress());
        ProductVariant productVariant = productVariantRepository.findById(orderDto.getVariantId()).orElse(new ProductVariant());
        order.setUser(user);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setOrderTotal(productVariant.getPrice()*orderDto.getQuantity());
        OrderItems orderItems = new OrderItems();
        orderItems.setPrice(productVariant.getPrice());
        orderItems.setProductVariant(productVariant);
        orderItems.setQuantity(orderDto.getQuantity());
        orderItems.setOrder(orderRepository.save(order));
        orderItemsRepository.save(orderItems);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/overview")
    public String showOverview(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElse(new WebUser());
        model.addAttribute("user",user);
        return "overview";
    }

    @GetMapping("/orders")
    public String showOrders(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        List<ProductVariant> productVariants = new ArrayList<>();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        List<Order> orders = orderRepository.findAllByUser_id(user.getId());
        Collections.reverse(orders);
        orders = orders.stream()
                .sorted(Comparator.comparing(Order::isCancellation)) // Sort false first, true later
                .collect(Collectors.toList());
        model.addAttribute("orders",orders);
        model.addAttribute("user",user);
        return "orders";
    }
    @ResponseBody
    @PostMapping("/orders/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id)
    {
        Order order = orderRepository.findById(id).orElseThrow(()-> new NullPointerException());
        order.setCancellation(true);
        orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body("Confirmed cancellation");
    }
    @GetMapping("/editprofile")
    public String editProfile(Model  model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        UserDTO userDto = new UserDTO();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setPhone(user.getPhone());
        userDto.setGender(user.getGender());
        userDto.setEmail(user.getEmail());
        userDto.setJoinDate(user.getJoinDate());
        model.addAttribute("userObject",userDto);
        return "editprofile";
    }
    @PostMapping("/editprofile")
    public String dataProfile(@Valid @ModelAttribute("userObject") UserDTO userDto, BindingResult result, HttpServletRequest request)
    {
        if(result.hasErrors())
        {
            return "editprofile";
        }
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        userRepository.save(user);
        session.setAttribute("userEmail",userDto.getEmail());
        session.setAttribute("userName",userDto.getFullName());
        return "redirect:/user/overview";
    }

    @GetMapping("/addresses")
    public String showaddresses(Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<UserAddress> userAddresses = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE" );
        AddressDTO defaultAddressDTO = new AddressDTO();
        for(UserAddress y : userAddresses)
        {
            if(y.getDefaultAddressStatus()==true)
            {
                defaultAddressDTO.setId(y.getId());
                defaultAddressDTO.setUser(user);
                defaultAddressDTO.setName(y.getName());
                defaultAddressDTO.setPhone(y.getPhone());
                defaultAddressDTO.setBuilding(y.getBuilding());
                defaultAddressDTO.setCity(y.getCity());
                defaultAddressDTO.setCountry(y.getCountry());
                defaultAddressDTO.setState(y.getState());
                defaultAddressDTO.setLandmark(y.getLandmark());
                defaultAddressDTO.setInstruction(y.getInstruction());
                defaultAddressDTO.setZipCode(y.getZipCode());
                defaultAddressDTO.setType(y.getType());
                defaultAddressDTO.setStreet(y.getStreet());
            }
        }
        if(userAddresses.isEmpty())
        {
            userAddresses=null;
        }
        model.addAttribute("defaultAddress", defaultAddressDTO);
        model.addAttribute("allAddress",userAddresses);
        model.addAttribute("user",user);
        return "addresses";
    }
    @GetMapping("/addresses/add")
    public String showAddAddresses(Model model)
    {
        model.addAttribute("addressObject",new AddressDTO());
        return "editaddresses";
    }
    @GetMapping("/addresses/edit/{id}")
    public String editAddress(@PathVariable Long id,Model model)
    {
        UserAddress address = addressRepository.findById(id).orElse(new UserAddress());
        AddressDTO addressDTO = addressMapper.toDTO(address);
        model.addAttribute("addressObject",addressDTO);
        return "editaddresses";
    }

    @PostMapping("/address/data")
    public String addAddress(@Valid @ModelAttribute("addressObject") AddressDTO addressDto, BindingResult result, HttpServletRequest request, Model model)
    {
        System.out.println(addressDto.getId()+">>>>>>");
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        if(result.hasErrors())
        {
            return "editaddresses";
        }
        if(addressDto.getId()==null)
        {
            System.out.println("heyyy from addAddress");
            userService.addAddress(addressDto,user);
        }
        else
        {
            UserAddress address = addressMapper.toEntity(addressDto);
            List<UserAddress> addresses =addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE").stream().filter(a -> a.getDefaultAddressStatus()).collect(Collectors.toList());
            if(addresses.isEmpty())
            {
                address.setDefaultAddressStatus(true);
            }
            for(UserAddress add : addresses)
            {
                if(add.getId() == address.getId())
                {
                    if(add.getDefaultAddressStatus())
                    {
                        address.setDefaultAddressStatus(true);
                    }
                }
            }
            address.setStatus("AVAILABLE");
            address.setUser(user);
            addressRepository.save(address);
        }
        return "redirect:/user/addresses";
    }

    @GetMapping("/addresses/default/{id}")
    public String setDefault(@PathVariable Long id, HttpServletRequest request)
    {
        UserAddress address = addressRepository.findById(id).orElseThrow(()-> new NullPointerException());
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<UserAddress> userAddresses = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE" );
        for(UserAddress y : userAddresses)
        {
            if(y.getDefaultAddressStatus()==true)
            {
               y.setDefaultAddressStatus(false);
            }
        }
        address.setDefaultAddressStatus(true);
        addressRepository.save(address);
        return "redirect:/user/addresses";
    }
    @GetMapping("/addresses/remove/{id}")
    public String removeAddress(@PathVariable Long id,HttpServletRequest request)
    {
        UserAddress address = addressRepository.findById(id).orElseThrow(()->new NullPointerException());
        address.setStatus("UNAVAILABLE");
        addressRepository.save(address);
        return "redirect:/user/addresses";
    }

    @GetMapping("/wishlist")
    public String showWhishlist(Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(user.getId());
        List<Product> products = productRepository.findAllByStatus(true).stream().filter(pd -> wishlists.stream().anyMatch(wi-> wi.getProduct().getId().equals(pd.getId()))).collect(Collectors.toList());
        List<ProductDto> productDtos = productMapper.toDTOList(products).stream().peek(pd -> { pd.getProductVariantDTOs().stream().peek(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
                                                                                                        pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(new ProductVariantDTO()));
                                                                                                        }).collect(Collectors.toList());
        for(ProductDto p : productDtos)
        {
            System.out.println(p.getName());
        }
        model.addAttribute("products",productDtos);
        return "wishlist";
    }


    @GetMapping("/cart")
    public String showCart(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());
        if(carts.isEmpty())
            carts=null;
        else {
            Double subTotal = 0D;
            for (Cart cart : carts) {
                subTotal = subTotal + cart.getTotalAmount();
            }

            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String FormattedSubTotal = formatter.format(subTotal);
            session.setAttribute("subtotal", subTotal);
            session.setAttribute("subtotalFormatted", FormattedSubTotal);
            model.addAttribute("subtotalFormatted", FormattedSubTotal);
            model.addAttribute("subtotal", subTotal);
        }
        model.addAttribute("carts", carts);
        return "cart";
    }

    @ResponseBody
    @GetMapping("/cart/data/")
    public ResponseEntity<?> submitCheckout(HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        Long userId = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException()).getId();
        List<Cart> carts = cartRepository.findAllByUser_id(userId);
        if(carts.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products in the cart");
        }
        for(Cart cart : carts)
        {
            if(cart.getQuantity() > cart.getProductVariant().getQuantity())
            {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Remove the out of stock product");
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Product ready for the checkout");
    }

    @ResponseBody
    @PostMapping("/addcart")
    public ResponseEntity<?> addCartPost(@RequestBody CartDTO cartDto, BindingResult result, HttpServletRequest request)
    {
        Integer productQuantity = productVariantRepository.findById(cartDto.getProductVariantId()).orElseThrow(()-> new NullPointerException()).getQuantity();
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Product cannot be added");
        }
        else if(cartDto.getQuantity() > productQuantity )
        {
            Map<String, Object> response = new HashMap<>();
            response.put("quantity", productQuantity);
            response.put("message","Lack of products");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        Cart newCart = cartMapper.toEntity(cartDto);
        Cart cart=cartRepository.save(newCart);
        return ResponseEntity.status(HttpStatus.OK).body("Product added to cart");
    }

    @Transactional
    @PostMapping("/cart/remove/{id}")
    public ResponseEntity<?> deleteCartProduct(@PathVariable Long id,HttpServletRequest request)
    {
        cartRepository.deleteById(id);
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        Long userId = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException()).getId();
        List<Cart> carts = cartRepository.findAllByUser_id(userId);
        Double subtotal= 0D;
        for(Cart cart : carts)
        {
            subtotal += cart.getTotalAmount();
        }
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        String FormattedSubTotal =  formatter.format(subtotal);
        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", FormattedSubTotal);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/checkout")
    public String showCheckout(HttpServletRequest request,Model model)
    {
        HttpSession session = request.getSession();
        String email = (String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        UserAddress address = addressRepository.findByUser_idAndDefaultAddressStatusAndStatus(user.getId(), true,"AVAILABLE").orElse(new UserAddress());
        String FormattedSubTotal = (String) session.getAttribute("subtotalFormatted");


        OrderDto orderDto = new OrderDto();
        orderDto.setVariantId((Long)session.getAttribute("productVariantId"));
        orderDto.setQuantity((Integer)session.getAttribute("productQuantity"));
        model.addAttribute("subtotal",(Double) session.getAttribute("subtotal"));
        model.addAttribute("addressObject",new AddressDTO());
        model.addAttribute("defaultAddress",address);
        model.addAttribute("subtotalFormatted",FormattedSubTotal);
        return "checkout";
    }

    @PostMapping("/checkout/address/data")
    public ResponseEntity<?> addCheckoutAddress(@RequestBody @Valid AddressDTO addressDto, BindingResult result, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some thing gone wrong");
        }
        userService.addAddress(addressDto,user);

        return ResponseEntity.status(HttpStatus.OK).body("Created");
    }

    @ResponseBody
    @PostMapping("/checkout")
    public ResponseEntity<?> postCheckout(@RequestBody OrderDto orderDto,BindingResult result,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email=(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()->new NullPointerException());
        List<Cart> carts = cartRepository.findAllByUser_id(user.getId());
        if(carts.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cart is empty");
        }
        if(result.hasErrors())
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order is not accepted");
        }
        Order order = new Order();
        UserAddress userAddress=addressRepository.findById(orderDto.getAddressId()).orElseThrow(()->new NullPointerException());
        order.setUser(user);
        order.setOrderDate(LocalDate.now());
        order.setOrderTotal((Double)session.getAttribute("subtotal"));
        order.setAddress(userAddress);
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setStatus("PENDING");
        Order newOrder = orderRepository.save(order);
        for(Cart cart : carts)
        {
            OrderItems orderItems = new OrderItems();
            orderItems.setOrder(newOrder);
            orderItems.setPrice(cart.getProductVariant().getPrice());
            orderItems.setQuantity(cart.getQuantity());
            orderItems.setProductVariant(cart.getProductVariant());
            orderItemsRepository.save(orderItems);
        }
        orderService.updateCartAndStock(carts,user.getId());
        return ResponseEntity.status(HttpStatus.OK).body("order placed");

    }

    @ResponseBody
    @PostMapping("/checkout/applyCoupon")
    public ResponseEntity<?> addCoupon(@RequestParam("couponCode") String couponCode,@RequestParam("subtotal") Double subtotal)
    {
        Map<String,String> response = new HashMap<>();
        Map<String,Double> opertaion = new HashMap<>();
        opertaion = couponRepository.findAllByIsActive(true).stream().filter(cp -> cp.getCouponCode().equalsIgnoreCase(couponCode)).collect(Collectors.toMap( cp -> cp.getDiscountType(),cp-> cp.getDiscountValue() ));
        if(!opertaion.isEmpty())
        {
            response.put()
            return ResponseEntity.ok()
        }
    }


    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model)
    {
        List <ProductDto> productDtos=productService.searchProducts(keyword);
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        model.addAttribute("products",productDtos);
        model.addAttribute("url","search");
        model.addAttribute("category","Search");
        model.addAttribute("breadcrumb","Search");
        model.addAttribute("keyword",keyword);
        model.addAttribute("brands",brands);
        model.addAttribute("breadcrumb","Men");

        return "productList";
    }

    @GetMapping("/shop")
    public String showShop(Model model,HttpServletRequest request)
    {

        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<Brand> brands = brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        List<ProductDto>productDtos = productService.ListAllProducts(user.getId());
        for(ProductDto p : productDtos )
        {
            System.out.println(p.getWishlisted());
        }
        model.addAttribute("products",productDtos);
        model.addAttribute("url","shop");
        model.addAttribute("category","Available");
        model.addAttribute("breadcrumb","Shop");
        model.addAttribute("brands",brands);

        return "productList";
    }

    //wishlist
    @PostMapping("/wishlist/add")
    public ResponseEntity<?> addToWishList(@RequestParam(name = "productId") Long id,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        Product product = productRepository.findById(id).orElse(new Product());
        Wishlist wishlist  = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        wishlist.setAddedDate(LocalDate.now());
        wishlistRepository.save(wishlist);
        session.setAttribute("wishlistCount",((Integer) session.getAttribute("wishlistCount"))+1);
        return ResponseEntity.ok("completed");
    }

    @Transactional
    @DeleteMapping("/wishlist/remove")
    public ResponseEntity<?> deleteFromWishlist(@RequestParam(name="productId") Long id,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        Long wishlistId = wishlistRepository.findByUser_idAndProduct_id(user.getId(),id).orElse(new Wishlist()).getId();
        wishlistRepository.deleteById(wishlistId);
        session.setAttribute("wishlistCount",((Integer) session.getAttribute("wishlistCount"))-1);
        return ResponseEntity.ok("completed");
    }

}
