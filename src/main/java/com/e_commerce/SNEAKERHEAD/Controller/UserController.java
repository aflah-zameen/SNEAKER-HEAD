package com.e_commerce.SNEAKERHEAD.Controller;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
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


    @GetMapping("/home")
    public String AdminProduct()
    {
        return "index";
    }
    @GetMapping("/women")
    public String WomenProduct(Model model)
    {
        Category category = categoryRepository.findByName("Women").orElseThrow(()->new NullPointerException());
        List<ProductDto> products = productService.categoryProduct(category);
        model.addAttribute("products",products);
        model.addAttribute("url","women");
        model.addAttribute("category","Women");
        return "productList";
    }

    @GetMapping("/product/filter")
    public ResponseEntity<List<ProductDto>> filterWomenProducts(@RequestParam String filterValue,@RequestParam String category,Model model)
    {
        if(category.equals("Available"))
        {
            List<Product> product = productRepository.findAll();
            List<ProductDto> productDtos = productMapper.toDTOList(product).stream().filter(ProductDto::getStatus).peek(pd -> {
                pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
                pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice()));
            }).collect(Collectors.toList());
            switch (filterValue)
            {
                case "price-high-low": productDtos = productDtos.stream().sorted(Comparator.comparingDouble(pd -> pd.getDefaultVariantDTO().getPrice())).collect(Collectors.toList());
                                        Collections.reverse(productDtos);
                    break;
                case "price-low-high": productDtos = productDtos.stream().sorted(Comparator.comparingDouble(pd -> pd.getDefaultVariantDTO().getPrice())).collect(Collectors.toList());
                    break;
                case "aA-zZ": productDtos = productDtos.stream().sorted(Comparator.comparing(ProductDto::getName)).collect(Collectors.toList());
                    break;
                case "zZ-aA": productDtos = productDtos.stream().sorted(Comparator.comparing(ProductDto::getName).reversed()).collect(Collectors.toList());
                    break;
                default: productDtos= new ArrayList<>();
            }
            return ResponseEntity.ok(productDtos);
        }
        else {
            Category categoryObject = categoryRepository.findByName(category).orElseThrow(() -> new NullPointerException());
            List<ProductDto> productDTOs = productService.filterProduct(categoryObject, filterValue);
            return ResponseEntity.ok(productDTOs);
        }
    }


    @GetMapping("/men")
    public String MenProduct(Model model)
    {
        Category category = categoryRepository.findByName("Men").orElseThrow(()->new NullPointerException());
        List<ProductDto> products = productService.categoryProduct(category);
        model.addAttribute("products",products);
        model.addAttribute("url","men");
        model.addAttribute("category","Men");

        return "productList";
    }

    @GetMapping("/product/detail/{id}")
    public String EditProduct(@PathVariable("id") Long id, Model model,HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String)session.getAttribute("userEmail");
        System.out.println(email);
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        ProductDto details = productService.FindProduct(id);
        System.out.println(details.getDefaultVariantDTO().getQuantity());
        model.addAttribute("userId",user.getId());
        model.addAttribute("details",details);
        model.addAttribute("categoryUrl",details.getCategoryName().toLowerCase());
        return "productdetails";
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
        for(Order order : orders)
        {
            System.out.print(order.getUser().getFull_name());
        }
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
        UserDto userDto = new UserDto();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        userDto.setId(user.getId());
        userDto.setName(user.getFull_name());
        userDto.setPhone(user.getPhone());
        userDto.setGender(user.getGender());
        userDto.setEmail(user.getEmail());
        userDto.setJoinDate(user.getJoin_date());
        model.addAttribute("userObject",userDto);
        return "editprofile";
    }
    @PostMapping("/editprofile")
    public String dataProfile(@Valid @ModelAttribute("userObject") UserDto userDto, BindingResult result,HttpServletRequest request)
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
        user.setFull_name(userDto.getName());
        userRepository.save(user);
        session.setAttribute("userEmail",userDto.getEmail());
        return "redirect:/user/overview";
    }

    @GetMapping("/addresses")
    public String showaddresses(Model model, HttpServletRequest request)
    {
        HttpSession session = request.getSession();
        String email =(String) session.getAttribute("userEmail");
        WebUser user = userRepository.findByEmail(email).orElseThrow(()-> new NullPointerException());
        List<UserAddress> userAddresses = addressRepository.findAllByUser_idAndStatus(user.getId(),"AVAILABLE" );
        AddressDto defaultAddressDto = new AddressDto();
        for(UserAddress y : userAddresses)
        {
            System.out.println(y.getDefaultAddressStatus());
            if(y.getDefaultAddressStatus()==true)
            {
                defaultAddressDto.setId(y.getId());
                defaultAddressDto.setUser(user);
                defaultAddressDto.setName(y.getName());
                defaultAddressDto.setPhone(y.getPhone());
                defaultAddressDto.setBuilding(y.getBuilding());
                defaultAddressDto.setCity(y.getCity());
                defaultAddressDto.setCountry(y.getCountry());
                defaultAddressDto.setState(y.getState());
                defaultAddressDto.setLandmark(y.getLandmark());
                defaultAddressDto.setInstructions(y.getInstruction());
                defaultAddressDto.setZipCode(y.getZipCode());
                defaultAddressDto.setType(y.getType());
                defaultAddressDto.setStreet(y.getStreet());
            }
        }
        System.out.println(defaultAddressDto.getId());
        if(userAddresses.isEmpty())
        {
            userAddresses=null;
        }
        model.addAttribute("defaultAddress",defaultAddressDto);
        model.addAttribute("allAddress",userAddresses);
        model.addAttribute("user",user);
        return "addresses";
    }
    @GetMapping("/addresses/add")
    public String showAddAddresses(Model model)
    {
        model.addAttribute("addressObject",new AddressDto());
        return "editaddresses";
    }
    @PostMapping("/address/data")
    public String addAddress(@Valid @ModelAttribute("addressObject") AddressDto addressDto,BindingResult result,HttpServletRequest request,Model model)
    {
        System.out.println(addressDto.getPhone());
        if(result.hasErrors())
        {
            return "editaddresses";
        }
        HttpSession session = request.getSession();
        WebUser user = userRepository.findByEmail((String)session.getAttribute("userEmail")).orElseThrow(()-> new NullPointerException());
        userService.addAddress(addressDto,user);
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
            System.out.println(y.getDefaultAddressStatus());
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
    public String showWhishlist()
    {
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
        model.addAttribute("defaultAddress",address);
        String FormattedSubTotal = (String) session.getAttribute("subtotalFormatted");
        System.out.println(FormattedSubTotal);
        model.addAttribute("subtotalFormatted",FormattedSubTotal);

        OrderDto orderDto = new OrderDto();
        orderDto.setVariantId((Long)session.getAttribute("productVariantId"));
        orderDto.setQuantity((Integer)session.getAttribute("productQuantity"));
        model.addAttribute("subtotal",FormattedSubTotal);

        return "checkout";
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
    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model)
    {
        List <ProductDto> productDtos=productService.searchProducts(keyword);
        model.addAttribute("products",productDtos);
        model.addAttribute("url","shop");
        model.addAttribute("category","Available");
        model.addAttribute("breadcrumb","Shop");
        return "productList";
    }

    @GetMapping("/shop")
    public String showShop(Model model)
    {
        List<ProductDto>productDtos = productService.ListAllProducts();
        model.addAttribute("products",productDtos);
        model.addAttribute("url","shop");
        model.addAttribute("category","Available");
        model.addAttribute("breadcrumb","Shop");
        return "productList";
    }

}
