package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.DTO.*;
import com.e_commerce.SNEAKERHEAD.Entity.Brand;
import com.e_commerce.SNEAKERHEAD.Entity.Category;
import com.e_commerce.SNEAKERHEAD.Entity.Product;
import com.e_commerce.SNEAKERHEAD.Entity.ProductVariant;
import com.e_commerce.SNEAKERHEAD.Mappers.ProductMapper;
import com.e_commerce.SNEAKERHEAD.Repository.BrandRepository;
import com.e_commerce.SNEAKERHEAD.Repository.CategoryRepository;
import com.e_commerce.SNEAKERHEAD.Repository.ProductRepository;
import com.e_commerce.SNEAKERHEAD.Repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public static String formatPrice(double price) {
        DecimalFormat decimalFormat = new DecimalFormat("##,##,##0.00"); // Indian-style formatting
        return "₹" + decimalFormat.format(price);
    }


    public List<ProductDto> categoryProduct(Category category)
    {
        List<Product> products = productRepository.findByCategory(category);

        return  productMapper.toDTOList(products).stream().filter(ProductDto::getStatus).peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
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

    public List<ProductDto> ListProduct() {
        List<Product> products = productRepository.findAll();
        return productMapper.toDTOList(products).stream().peek(pd -> {pd.setDefaultVariantDTO(pd.getProductVariantDTOs().getFirst());
            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
        pd.setQuantity(pd.getProductVariantDTOs().stream().mapToInt(ProductVariantDTO::getQuantity).sum());}).collect(Collectors.toList());
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

    public List<ProductDto> filterProduct(Category category, String filterValue) {
        List<Product> products;
        switch (filterValue)
        {
            case "price-high-low": products = productRepository.findByCategoryAndSortByPriceDesc(category);
                                    break;
            case "price-low-high": products = productRepository.findByCategoryAndSortByPriceAsc(category);
                                    break;
            case "aA-zZ": products = productRepository.findByCategoryAndSortByNameAsc(category);
                break;
            case "zZ-aA": products = productRepository.findByCategoryAndSortByNameDesc(category);
                break;
            default: products= new ArrayList<>();
        }
        List<ProductDto> productDtos= productMapper.toDTOList(products);
        for(ProductDto p : productDtos)
        {
            p.setDefaultVariantDTO(p.getProductVariantDTOs().getFirst());
            for(ProductVariantDTO pv : p.getProductVariantDTOs())
            {
                pv.setFormattedPrice(pv.FormattedPrice());
            }
        }
        return productDtos;
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

    public List<ProductDto> ListAllProducts() {
        List<Product> products = productRepository.findAll();
        products = products.stream().filter(Product::getStatus).collect(Collectors.toList());

        return productMapper.toDTOList(products).stream().peek(pd -> {
            pd.setDefaultVariantDTO(pd.getProductVariantDTOs().stream().findFirst().orElse(null));
            pd.getProductVariantDTOs().forEach(pv -> pv.setFormattedPrice(pv.FormattedPrice()));
        }).collect(Collectors.toList());

    }
}
