package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.*;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    ProductDto productDto;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    WishlistRepository wishlistRepository;

    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("##,##,##0.00"); // Indian-style formatting
        return "₹" + decimalFormat.format(price);
    }


    public List<ProductDto> categoryProduct(Category category,Long id)
    {
        List<Product> products = productRepository.findByCategory(category);
        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(id);
        return  productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
            pd.setWishlisted(wishlists.stream().anyMatch(wi-> wi.getProduct().getId().equals(pd.getId())));
        } ).collect(Collectors.toList());
    }
    public ProductDto FindProduct(Long id)
    {
        Product product = productRepository.findById(id).orElseThrow(()-> new NullPointerException());
        ProductDto productDto = productMapper.toDTO(product);
        for(ProductVariantDTO p : productDto.getProductVariantDTOs())
        {
            p.setFormattedPrice(p.FormattedPrice());
        }
        productDto.setDefaultVariantDTO(productDto.getProductVariantDTOs().getFirst());
        System.out.println(productDto.getDefaultVariantDTO().getFormattedPrice());
        return productDto;
    }

    public Page<Product> ListProduct(Pageable pageable) {
        Page<Product>productPage = productRepository.findAll(pageable);
        return productPage;

//        return productMapper.toDTOList(products).stream().peek(pd -> {pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
//            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
//        pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(ProductVariantDTO::getQuantity).sum());}).collect(Collectors.toList());
    }

    public AddProduct addProduct()
    {
        List<Category> ListCategories = categoryRepository.findAll();
        List<Brand> ListBrands =brandRepository.findAll();
        AddProduct addProduct = new AddProduct();
        List<String> categories = new ArrayList<>() ;
        List<String> brands = new ArrayList<>();
        for(Category x : ListCategories)
        {
            categories.add(x.getName());

        }
        for(Brand y : ListBrands)
        {
            brands.add(y.getName());
        }
        addProduct.setBrands(brands);
        addProduct.setCategories(categories);
        return addProduct;
    }
    public ProductDto productToProductDto(Product product)
    {
        ProductDto productDto = new ProductDto();
        List<ProductVariantDTO> productVariantDTOS = new ArrayList<>();
        List<ProductVariant> productVariants = productVariantRepository.findByProduct_id(product.getId()).orElseThrow(()->new NullPointerException());
        for(ProductVariant x : productVariants)
        {
            ProductVariantDTO productVariantDto = new ProductVariantDTO();
            productVariantDto.setId(x.getId());
            productVariantDto.setImages(x.getImages());
            productVariantDto.setColor(x.getColor());
            productVariantDto.setArticleCode(x.getArticleCode());
            productVariantDto.setSize(x.getSize());
            productVariantDto.setQuantity(x.getQuantity());
            productVariantDto.setPrice(x.getPrice());
//            productVariantDto.setFormatedPrice(formatPrice(x.getPrice()));
            productVariantDTOS.add(productVariantDto);
        }

        productDto.setName(product.getName());
//        productDto.setBrand(product.getBrand());
//        productDto.setProductVariantDTOS(productVariantDTOS);
//        productDto.setCategory(product.getCategory());
        productDto.setImportedBy(product.getImportedBy());
        productDto.setGenericName(product.getGenericName());
        productDto.setManufacturer(product.getManufacturer());
        productDto.setWeight(product.getWeight());
        productDto.setCountryOfOrigin(product.getCountryOfOrigin());
        productDto.setId(product.getId());
        if(!productVariantDTOS.isEmpty())
        {
//            productDto.setDefaultVariant(productVariantDTOS.get(0));
        }
        productDto.setDescription(product.getDescription());
        productDto.setStatus(product.getStatus());
        productDto.setMarketedBy(product.getMarketedBy());
        productDto.setQuantity(productVariantRepository.getTotalQuantityByProductId(product.getId()));

        return productDto;
    }

//    public List<ProductDto> filterProduct(Category category, String filterValue) {
//        List<Product> products;
//        switch (filterValue)
//        {
//            case "price-high-low": products = productRepository.findByCategoryAndSortByPriceDesc(category);
//                                    break;
//            case "price-low-high": products = productRepository.findByCategoryAndSortByPriceAsc(category);
//                                    break;
//            case "aA-zZ": products = productRepository.findByCategoryAndSortByNameAsc(category);
//                break;
//            case "zZ-aA": products = productRepository.findByCategoryAndSortByNameDesc(category);
//                break;
//            default: products= new ArrayList<>();
//        }
//        List<ProductDto> productDtos= productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).collect(Collectors.toList());
//        for(ProductDto p : productDtos)
//        {
//            p.setDefaultVariantDTO(p.getProductVariantDTOs().getFirst());
//            for(ProductVariantDTO pv : p.getProductVariantDTOs())
//            {
//                pv.setFormattedPrice(pv.FormattedPrice());
//            }
//        }
//        return productDtos;
//    }

    public List<ProductDto> filterProduct(String filterType, String filterValue)
    {
        List<Product> product = productRepository.findAll();
        List<ProductDto> productDtos = productMapper.toDTOList(product).stream().filter(ProductDto::getStatus).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice()));
        }).collect(Collectors.toList());
        List<Brand> brands=brandRepository.findAll().stream().filter(Brand::getStatus).collect(Collectors.toList());
        List<Category> categories=categoryRepository.findAll().stream().filter(Category::getStatus).collect(Collectors.toList());
        System.out.println(filterType.equals("Shoe Size (UK)")+">>>>>>");
        if(filterType.equals("Shoe Size (UK)"))
        {
            System.out.println(filterValue);
            switch (filterValue)
            {
                case "6" : productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getSize().stream().anyMatch(size -> size.equals("6")))) // Filter ProductDtos
                        .collect(Collectors.toList());
                    System.out.println("hiii");
                    break;
                case "7" :productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getSize().stream().anyMatch(size -> size.equals("7")))) // Filter ProductDtos
                        .collect(Collectors.toList());
                    break;
                default: productDtos = new ArrayList<>();
            }
        }
        else if(filterType.equals("Price"))
        {
            switch (filterValue)
            {
                case "<5000" : productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getPrice() < 5000)) // Filter ProductDtos
                        .collect(Collectors.toList());
                    break;
                case "5000-10000" :productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getPrice() >=5000 && variant.getPrice() <=10000)) // Filter ProductDtos
                        .collect(Collectors.toList());
                    break;

                case "10000-15000" :productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getPrice() >=10000 && variant.getPrice() <=15000)) // Filter ProductDtos
                        .collect(Collectors.toList());
                    break;

                case ">15000" :productDtos = productDtos.stream()
                        .filter(pd -> pd.getProductVariantDTOs().stream()
                                .anyMatch(variant -> variant.getPrice() >15000 )) // Filter ProductDtos
                        .collect(Collectors.toList());
                    break;

                default: productDtos = new ArrayList<>();
            }
        }
        else if(filterType.equals("Brand"))
        {
           for(Brand brand : brands)
           {
               if(brand.getName().contains(filterValue))
               {
                   productDtos = productDtos.stream()
                           .filter(pd -> pd.getBrandName().equals(filterValue)) // Filter ProductDtos
                           .collect(Collectors.toList());
               }
           }
        }
        else if(filterType.equals("Gender"))
        {
            for(Category category : categories)
            {
                if(category.getName().equals(filterValue))
                {
                    productDtos = productDtos.stream()
                            .filter(pd -> pd.getCategoryName().equals(filterValue)) // Filter ProductDtos
                            .collect(Collectors.toList());
                }
            }
        }
        else if(filterType.equals("Sort by"))
        {
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
        }
        List<Wishlist> wishlists = wishlistRepository.findAll();
        return productDtos.stream().peek(pd -> pd.setWishlisted(wishlists.stream().anyMatch(wi -> wi.getProduct().getId().equals(pd.getId())))).collect(Collectors.toList());

    }

    public List<ProductDto> searchProducts(String keyword)
    {
        List<Product> products = productRepository.searchProducts(keyword);
        List<ProductDto> productDtos = productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach( pv -> pv.setFormattedPrice(pv.FormattedPrice()));
        }).collect(Collectors.toList());

        return productDtos;
    }

    public List<ProductDto> ListAllProducts(Long id) {
        List<Product> products = productRepository.findAll();
        products = products.stream().filter(Product::getStatus).collect(Collectors.toList());
        List<Wishlist> wishlists = wishlistRepository.findAllByUser_id(id);
        return productMapper.toDTOList(products).stream().peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
            pd.setWishlisted(wishlists.stream().anyMatch(wi -> wi.getProduct().getId().equals(pd.getId())));
        }).collect(Collectors.toList());

    }

//    public List<ProductDto> adminProductSorting(SortProductDTO sortProductDTO)
//    {
//        productRepository.
//    }

}
