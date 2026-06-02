package com.rodrigomv.planetbooksback.service.product;

import com.rodrigomv.planetbooksback.model.dto.ProductDTO;
import com.rodrigomv.planetbooksback.model.dto.ProductItemDTO;
import com.rodrigomv.planetbooksback.model.entity.Product;
import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import com.rodrigomv.planetbooksback.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.debug("Obteniendo productos con paginación");
        return productRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String title, String tag, String level, Pageable pageable) {
        if (title != null && !title.isBlank()) {
            return productRepository.findByTitleContainingIgnoreCase(title, pageable).map(this::convertToDTO);
        }
        if (tag != null && !tag.isBlank()) {
            return productRepository.findByTag(tag, pageable).map(this::convertToDTO);
        }
        if (level != null && !level.isBlank()) {
            return productRepository.findByLevel(level, pageable).map(this::convertToDTO);
        }
        return getAllProducts(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::convertToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = mapDtoToEntity(productDTO);
        return convertToDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        fillEntityFromDto(product, productDTO, false);
        return convertToDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO patchProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        fillEntityFromDto(product, productDTO, true);
        return convertToDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductItemDTO getProductItemById(Long id) {
        return productItemRepository.findById(id)
            .map(this::convertItemToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductItemDTO> getAllProductItems() {
        return productItemRepository.findAll().stream().map(this::convertItemToDTO).collect(Collectors.toList());
    }

    @Override
    public ProductItemDTO createProductItem(ProductItemDTO productItemDTO) {
        Product product = productRepository.findById(productItemDTO.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        ProductItem item = mapItemDtoToEntity(productItemDTO, product);
        return convertItemToDTO(productItemRepository.save(item));
    }

    @Override
    public ProductItemDTO updateProductItem(Long id, ProductItemDTO productItemDTO) {
        ProductItem item = productItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));
        fillItemFromDto(item, productItemDTO, false);
        return convertItemToDTO(productItemRepository.save(item));
    }

    @Override
    public ProductItemDTO patchProductItem(Long id, ProductItemDTO productItemDTO) {
        ProductItem item = productItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));
        fillItemFromDto(item, productItemDTO, true);
        return convertItemToDTO(productItemRepository.save(item));
    }

    @Override
    public void deleteProductItem(Long id) {
        if (!productItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProductItem no encontrado");
        }
        productItemRepository.deleteById(id);
    }

    @Override
    public void decreaseStock(Long productId, String itemKey, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a cero");
        }
        ProductItem item = productItemRepository.findByProductIdAndKey(productId, itemKey)
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));
        if (item.getStock() == null || item.getStock() < quantity) {
            throw new BadRequestException("Stock insuficiente");
        }
        item.setStock(item.getStock() - quantity);
        productItemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTags() {
        return productRepository.findAll().stream()
            .map(Product::getTag)
            .distinct()
            .filter(tag -> tag != null && !tag.isBlank())
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllLevels() {
        return productRepository.findAll().stream()
            .map(Product::getLevel)
            .distinct()
            .filter(level -> level != null && !level.isBlank())
            .collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
            .id(product.getId())
            .title(product.getTitle())
            .description(product.getDescription())
            .tag(product.getTag())
            .categories(product.getCategories())
            .level(product.getLevel())
            .image(product.getImage())
            .gallery(product.getGallery())
            .pages(product.getPages())
            .format(product.getFormat())
            .publisher(product.getPublisher())
            .language(product.getLanguage())
            .items(product.getItems() != null ?
                product.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()) :
                List.of())
            .build();
    }

    private ProductItemDTO convertItemToDTO(ProductItem item) {
        return ProductItemDTO.builder()
            .id(item.getId())
            .productId(item.getProduct().getId())
            .key(item.getKey())
            .title(item.getTitle())
            .price(item.getPrice())
            .image(item.getImage())
            .description(item.getDescription())
            .pages(item.getPages())
            .format(item.getFormat())
            .stock(item.getStock())
            .isDefault(item.getIsDefault())
            .build();
    }

    private Product mapDtoToEntity(ProductDTO dto) {
        Product product = Product.builder()
            .title(dto.getTitle())
            .description(dto.getDescription())
            .tag(dto.getTag())
            .level(dto.getLevel())
            .image(dto.getImage())
            .pages(dto.getPages())
            .format(dto.getFormat())
            .publisher(dto.getPublisher())
            .language(dto.getLanguage())
            .categories(dto.getCategories())
            .gallery(dto.getGallery())
            .build();
        if (dto.getItems() != null) {
            product.setItems(dto.getItems().stream().map(itemDTO -> mapItemDtoToEntity(itemDTO, product)).collect(Collectors.toList()));
        }
        return product;
    }

    private void fillEntityFromDto(Product product, ProductDTO dto, boolean patch) {
        if (!patch || dto.getTitle() != null) product.setTitle(dto.getTitle());
        if (!patch || dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (!patch || dto.getTag() != null) product.setTag(dto.getTag());
        if (!patch || dto.getLevel() != null) product.setLevel(dto.getLevel());
        if (!patch || dto.getImage() != null) product.setImage(dto.getImage());
        if (!patch || dto.getPages() != null) product.setPages(dto.getPages());
        if (!patch || dto.getFormat() != null) product.setFormat(dto.getFormat());
        if (!patch || dto.getPublisher() != null) product.setPublisher(dto.getPublisher());
        if (!patch || dto.getLanguage() != null) product.setLanguage(dto.getLanguage());
        if (!patch || dto.getCategories() != null) product.setCategories(dto.getCategories());
        if (!patch || dto.getGallery() != null) product.setGallery(dto.getGallery());
    }

    private ProductItem mapItemDtoToEntity(ProductItemDTO dto, Product product) {
        return ProductItem.builder()
            .product(product)
            .key(dto.getKey())
            .title(dto.getTitle())
            .price(dto.getPrice())
            .image(dto.getImage())
            .description(dto.getDescription())
            .pages(dto.getPages())
            .format(dto.getFormat())
            .stock(dto.getStock() != null ? dto.getStock() : 0)
            .isDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false)
            .build();
    }

    private void fillItemFromDto(ProductItem item, ProductItemDTO dto, boolean patch) {
        if (!patch || dto.getProductId() != null) {
            if (dto.getProductId() != null && !dto.getProductId().equals(item.getProduct().getId())) {
                Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
                item.setProduct(product);
            }
        }
        if (!patch || dto.getKey() != null) item.setKey(dto.getKey());
        if (!patch || dto.getTitle() != null) item.setTitle(dto.getTitle());
        if (!patch || dto.getPrice() != null) item.setPrice(dto.getPrice());
        if (!patch || dto.getImage() != null) item.setImage(dto.getImage());
        if (!patch || dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (!patch || dto.getPages() != null) item.setPages(dto.getPages());
        if (!patch || dto.getFormat() != null) item.setFormat(dto.getFormat());
        if (!patch || dto.getStock() != null) item.setStock(dto.getStock());
        if (!patch || dto.getIsDefault() != null) item.setIsDefault(dto.getIsDefault());
    }
}
