-- Users Table
CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(255)
);

-- Insert users
INSERT INTO Users ( user_id, email, full_name, phone_number, address) VALUES
    (1, 'alena27@gmail.com', 'Alena Roy', '15776545365', 'Musterstraße 5 12345 Musterstadt');



-- Categories Table
CREATE TABLE Categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Insert Categories
INSERT INTO Categories (category_id, category_name, description) VALUES 
    (1, 'Sofas', 'A wide range of comfortable and stylish sofas.'),
    (2, 'Tables', 'Dining tables, coffee tables, and more.'),
    (3, 'Chairs', 'From armchairs to recliners, find the perfect seating.'),
    (4, 'Desks', 'Functional and stylish desks for home or office.'),
    (5, 'Storage', 'Wardrobes, bookshelves, and other storage solutions.');


-- Products Table
CREATE TABLE Products (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(10,2) NOT NULL,
    category_id INT,
    weight_kg DECIMAL(6,2),
    dimensions_cm INT,
    material_type VARCHAR(100),
    image_url TEXT, 
    supplier_name VARCHAR(255) NOT NULL,
    supplier_email VARCHAR(255) NOT NULL,
    supplier_phone VARCHAR(20),
    stock_quantity INT DEFAULT 0,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- products list
INSERT INTO Products (product_id, product_name, description, base_price, category_id, weight_kg, dimensions_cm, material_type, image_url, supplier_name, supplier_email, supplier_phone, stock_quantity) VALUES
    (1, 'Modern Sofa', 'A sleek and stylish modern sofa with comfortable seating.', 499.99, 1, 35.00, 200, 'Fabric', 'https://webshopfra.s3.us-east-1.amazonaws.com/morder_sofa.jpg', 'Comfort Furnishings', 'sales@comfortfurnishings.com', '+1234567890', 10),
    (2, 'Wooden Coffee Table', 'Elegant wooden coffee table with a natural finish.', 149.99, 2, 15.00, 100, 'Wood', 'https://webshopfra.s3.us-east-1.amazonaws.com/coffe_table.jpg', 'Nature Home', 'info@naturehome.com', '+1234567891', 20),
    (3, 'Dining Table', 'Contemporary glass dining table for modern homes.', 299.99, 2, 40.00, 150, 'Glass', 'https://webshopfra.s3.us-east-1.amazonaws.com/dinning_table.jpg', 'Urban Living', 'support@urbanliving.com', '+1234567893', 12),
    (4, 'Office Desk', 'Spacious office desk with multiple storage compartments.', 199.99, 4, 20.00, 120, 'Wood', 'https://webshopfra.s3.us-east-1.amazonaws.com/office_desk.jpg', 'Work Smart', 'help@worksmart.com', '+1234567894', 25),
    (5, 'Fabric Armchair', 'Cozy fabric armchair with a classic design.', 179.99, 3, 20.00, 70, 'Fabric', 'https://webshopfra.s3.us-east-1.amazonaws.com/arm_chair.jpg', 'Homely Comforts', 'hello@homelycomforts.com', '+1234567895', 18),
    (6, 'Wardrobe', 'Large wardrobe with plenty of hanging and shelf space.', 349.99, 5, 50.00, 200, 'Wood', 'https://webshopfra.s3.us-east-1.amazonaws.com/wardrobe.jpg', 'Space Saver', 'support@spacesaver.com', '+1234567897', 10),
    (7, 'Recliner chair', 'Luxurious leather recliner chair for ultimate relaxation.', 399.99, 3, 25.00, 80, 'Leather', 'https://webshopfra.s3.us-east-1.amazonaws.com/leather_recliner.jpg', 'Comfort Zone', 'sales@comfortzone.com', '+1234567898', 15),
    (8, 'Glass Cabinet', 'Elegant glass cabinet with ample storage space.', 249.99, 5, 30.00, 180, 'Glass', 'https://webshopfra.s3.us-east-1.amazonaws.com/glass_cabinet.jpg', 'ClearView Furniture', 'info@clearviewfurniture.com', '+1234567899', 8),
    (9, 'Bookshelf', 'Sturdy metal bookshelf with multiple shelves.', 129.99, 5, 18.00, 160, 'Metal', 'https://webshopfra.s3.us-east-1.amazonaws.com/metal_bookshelf.jpg', 'Home Essentials', 'support@homeessentials.com', '+1234567900', 20),
    (10, 'Desk Lamp', 'Modern desk lamp with adjustable brightness and color temperature.', 49.99, 4, 2.00, 40, 'Metal & Plastic', 'https://webshopfra.s3.us-east-1.amazonaws.com/desk_lamp.jpg', 'Lighting Solutions', 'help@lightingsolutions.com', '+1234567901', 30);

-- Orders Table
CREATE TABLE Orders (
    order_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address TEXT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_date DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Order Items Table
CREATE TABLE Order_Items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);


-- Payment Table
CREATE TABLE Payments (
    payment_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    payment_method VARCHAR(255),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2) NOT NULL,
    error_message VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);  
