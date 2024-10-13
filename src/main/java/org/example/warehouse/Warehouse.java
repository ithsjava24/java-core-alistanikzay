package org.example.warehouse;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    private static final Map<String, Warehouse> warehousesMap = new HashMap<>();
    private final List<ProductRecord> productsList = new ArrayList<>();
    private final List<ProductRecord> changedProductsList = new ArrayList<>();
    private String warehouseName;

    private Warehouse() {}

    private Warehouse(String name) {
        this.warehouseName = name;
    }

    public String getName() {
        return warehouseName;
    }

    public static Warehouse getInstance() {
        return new Warehouse();
    }

    public static Warehouse getInstance(String warehouseName) {
        if (warehouseName == null) {
            throw new IllegalArgumentException("Warehouse name cannot be null");
        }

        warehouseName = capitalizeFirstLetter(warehouseName);
        return warehousesMap.computeIfAbsent(warehouseName, Warehouse::new);
    }

    public ProductRecord addProduct(UUID uuid, String productName, Category category, BigDecimal price) {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name can't be null or empty.");
        }

        if (category == null) {
            throw new IllegalArgumentException("Category can't be null.");
        }

        if (uuid == null) {
            uuid = UUID.randomUUID();
        } else if (getProductById(uuid).isPresent()) {
            throw new IllegalArgumentException("Product with that id already exists, use updateProduct for updates.");
        }

        BigDecimal finalPrice = (price != null) ? price : BigDecimal.ZERO;
        ProductRecord productRecord = new ProductRecord(uuid, productName, category, finalPrice);
        productsList.add(productRecord);
        return productRecord;
    }

    public List<ProductRecord> getProducts() {
        return Collections.unmodifiableList(productsList);
    }

    public Optional<ProductRecord> getProductById(UUID uuid) {
        return productsList.stream().filter(productRecord -> productRecord.uuid().equals(uuid)).findFirst();
    }

    public void updateProductPrice(UUID uuid, BigDecimal newPrice) {
        if (newPrice == null) {
            throw new IllegalArgumentException("Price cannot be null.");
        }

        ProductRecord productRecord = getProductById(uuid).orElseThrow(() ->
                new IllegalArgumentException("Product with that id doesn't exist.")
        );

        // Check if the price is actually changing
        if (!productRecord.price().equals(newPrice)) {
            // Save a copy of the product before changing the price
            changedProductsList.add(new ProductRecord(productRecord.uuid(), productRecord.name(), productRecord.category(), productRecord.price()));

            // Create a new instance to represent the updated product
            ProductRecord updatedProduct = new ProductRecord(productRecord.uuid(), productRecord.name(), productRecord.category(), newPrice);

            // Remove the old product and add the updated one
            productsList.removeIf(p -> p.uuid().equals(uuid));
            productsList.add(updatedProduct);
        }
    }

    public List<ProductRecord> getChangedProducts() {
        return Collections.unmodifiableList(changedProductsList);
    }

    public Map<Category, List<ProductRecord>> getProductsGroupedByCategories() {
        return productsList.stream().collect(Collectors.groupingBy(ProductRecord::category));
    }

    public List<ProductRecord> getProductsBy(Category category) {
        return getProductsGroupedByCategories().getOrDefault(category, List.of());
    }

    private static String capitalizeFirstLetter(String name) {
        if (name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public boolean isEmpty() {
        return productsList.isEmpty();
    }
}
