# Prerequisites

In order to be able to submit your assignement, you should have the following installed:
* JDK >= 17
* Maven
* Postman / any other tool that allows you to hit the application's endpoints
* Any versioning tool
* Any IDE that allows you to run the application


# How to

#### Run the application
The application should be run as a SpringBootApplication. Below is a quick guide on how to do that via IntelliJ:
* Edit Configuration 
   * Add New Configuration (Spring Boot)
     * Change the **Main class** to **ing.assessment.INGAssessment**
       * Run the app.

#### Connect to the H2 database
Access the following url: **http://localhost:8080/h2-console/**
 * **Driver Class**: _**org.h2.Driver**_
 * **JDBC URL**: _**jdbc:h2:mem:testdb**_
 * **User Name**: _**sa**_
 * **Password**: **_leave empty_**



# Order logic

This service provides functionalities to handle orders, including creating, editing, and deleting orders as well as managing their associated products and stock. It also includes validation checks to ensure sufficient stock availability when creating or editing orders.

Functionalities
The OrderServiceImpl class contains the following core functionalities:

1.  **Get All Orders**

       Retrieves a list of all orders in the system.
    
       
    Endpoint: GET /orders


2. **Get Order by ID**

   Fetches a specific order by its ID.
    
    
    Endpoint: GET /orders/{orderId}



3. **Create Order**

   Creates a new order based on the list of products provided. Ensures that there are sufficient stocks for each product, that the product exists and also that the location is among possible values, before the order can be created. Updates the product stock accordingly.

    
    Endpoint: POST /orders

    Parameters:
    A list of OrderProduct objects.
    E.g. {
        "productId": 1,
        "location": 0,
        "quantity": 7
    }
    
    Returns: The created Order object.


**Exceptions**:

Throws an InvalidOrderException if no products are provided or if there are insufficient stock levels.

Throws an ItemNotFound if the products provided are not found in the database.

Throws an InvalidFormatException if the product location provided is not among possible values.

4. **Edit Order Product Quantity**
   
    Updates the quantity of a product in an existing order. Validates that the new quantity is within available stock.Updates the product stock accordingly. If the new quantity is lesser than before, the current product stock will increase and vice versa.


    Endpoint: PATCH /orders/{orderId}/edit-product-quantity


Parameters:

**orderId** - Path variable: The ID of the order.

**productId** - Query param: The ID of the product.

**location** - Query param: The location of the product.

**quantity** - Request body: The new quantity for the product.

Returns: The updated Order object.

    e.g. request /orders/1/edit-product-quantity?productId=1&location=MUNICH
    Body: 5

Exceptions:

Throws ItemNotFound if the order or product cannot be found.

Throws InsufficientStockException if there is not enough stock for the new quantity.

5. **Delete Product from Order**

   Removes a product from an existing order. Updates the product stock accordingly


    Endpoint: DELETE /orders/{orderId}/delete-product

Parameters:

**orderId**: The ID of the order.

**productId**: The ID of the product to be removed.

**location**: The location of the product.

    e.g. request /orders/1/delete-product?productId=2&location=COLOGNE

Returns: The updated Order object after the product is removed.

Exceptions:

Throws ItemNotFound if the order or product cannot be found.

6. **Delete Order**

   Deletes an order by its ID. When deleting, the product stock is increased to reflect the removal of the products from the order.


    Endpoint: DELETE /orders/{orderId}

Parameters:

**orderId**: The ID of the order to delete.

Returns: A void response after deleting the order.

Exceptions:

Throws ItemNotFound if the order cannot be found.


# Product Logic
This service provides an API for managing products, including creating, editing, retrieving, and deleting products, as well as updating their quantities and prices for specific locations. It also provides endpoint functionality for handling product-specific operations such as deleting all products.

Functionalities
The ProductController class exposes the following core endpoints for managing products:

1. **Get All Products**

   Retrieves a list of all products in the system.


    Endpoint: GET /products

Returns: A list of Product objects.

2. **Get Product by ID**

   Fetches a specific product by its ID.

    
    Endpoint: GET /products/{id}

Parameters:

**id**: The ID of the product to retrieve.

Returns: A list of Product objects.

3. **Delete Product**

   Deletes a product by its ID and location.


    Endpoint: DELETE /products/{productId}/delete-product

Parameters:

**productId**: The ID of the product to delete.

**location**: The location of the product.

Returns: An HTTP status of 204 No Content if the product is successfully deleted.

Exceptions:

Throws ItemNotFound if the product cannot be found for the provided ID and location.

4. **Delete All Products**

   Deletes all products from the system.


    Endpoint: DELETE /products

Returns: An HTTP status of 204 No Content indicating that all products have been deleted.

5. **Create Product**

   Creates a new product with the provided details.


    Endpoint: POST /products

Parameters:

A **Product object** in the request body, including product details like name, price, and quantity.

Returns: The created Product object.

6. **Edit Product Quantity**

   Updates the quantity of a specific product in a particular location.

    
    Endpoint: PATCH /products/{productId}/edit-product-quantity/location/{locationId}

Parameters:

**productId**: The ID of the product.

**locationId**: The location where the product is stored.

**quantity**: The new quantity for the product.

Returns: The updated Product object with the new quantity.

Exceptions:

Throws ItemNotFound if the product cannot be found for the provided ID and location.

Throws InsufficientStockException if there is not enough stock available.

7. **Edit Product Price**

   Updates the price of a specific product in a particular location.


    Endpoint: PATCH /products/{productId}/edit-product-price/location/{locationId}

Parameters:

**productId**: The ID of the product.

**locationId**: The location where the product is stored.

**price**: The new price for the product.

Returns: The updated Product object with the new price.

Exceptions:

Throws ItemNotFound if the product cannot be found for the provided ID and location.


        
