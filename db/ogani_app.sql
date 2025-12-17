-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 16, 2025 at 10:46 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ogani_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

CREATE TABLE `cart` (
  `cart_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cart_id`, `user_id`, `product_id`, `quantity`) VALUES
(6, 2, 1, 2),
(16, 3, 2, 1);

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`category_id`, `category_name`, `image`) VALUES
(1, 'Fruits', 'https://i.pinimg.com/736x/23/a3/ff/23a3ffac0623ea6d2bd0c8b16619964a.jpg'),
(2, 'Vegetables', 'https://i.pinimg.com/1200x/c8/6d/d7/c86dd70e9f9deb4d725c627a03e74891.jpg'),
(3, 'Beverages', 'https://i.pinimg.com/736x/d4/15/b8/d415b8cf493969d41ab218621c1fbaeb.jpg'),
(4, 'Dried Fruit', 'https://i.pinimg.com/736x/ab/da/b3/abdab3b2128705642e954665e3755748.jpg'),
(5, 'Fast Food', 'https://i.pinimg.com/1200x/23/6b/a5/236ba56962a3ba362a47fcbc634f206e.jpg'),
(6, 'Meat', 'https://i.pinimg.com/1200x/1d/2e/4e/1d2e4e5a92c50a80da8a6c180c742084.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `invoice_code` varchar(50) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total_price` decimal(12,2) DEFAULT NULL,
  `order_status` enum('pending','processing','shipped','completed','cancelled') DEFAULT 'pending',
  `order_time` datetime DEFAULT current_timestamp(),
  `receiver_name` varchar(100) DEFAULT NULL,
  `receiver_phone` varchar(20) DEFAULT NULL,
  `shipping_address` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`order_id`, `invoice_code`, `user_id`, `total_price`, `order_status`, `order_time`, `receiver_name`, `receiver_phone`, `shipping_address`) VALUES
(1, 'INV-68F3FFA231C57', 1, 30000.00, 'shipped', '2025-10-19 03:59:14', 'Jamaludin', '083123123', 'axkdnasd'),
(2, 'INV-68F479720E250', 1, 30000.00, 'pending', '2025-10-19 12:38:58', 'jamaldu', '12093123', 'iosajdasd'),
(3, 'INV-68F4CFC15F1F2', 1, 30000.00, 'processing', '2025-10-19 18:47:13', 'Jamaludin', '083103293225', 'Tangerang'),
(4, 'INV-68F506821A13B', 1, 62000.00, 'pending', '2025-10-19 22:40:50', 'asdkasd', 'asoidjasd', 'asdji0aisd'),
(5, 'INV-68F50D03A5978', 1, 40000.00, 'pending', '2025-10-19 23:08:35', 'lkasd', '123123', 'dsaasd'),
(6, 'INV-68F50D1B1A869', 1, 40000.00, 'pending', '2025-10-19 23:08:59', 'lkasd', '123123', 'dsaasd'),
(7, 'INV-68F50D432ED58', 1, 24000.00, 'pending', '2025-10-19 23:09:39', 'efgg', '971923', 'as;odojasd'),
(8, 'INV-68F5134B31DAC', 1, 30000.00, 'pending', '2025-10-19 23:35:23', 'Hamza Deleon', '083103293225', 'Tangerang');

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `detail_id` int(11) NOT NULL,
  `order_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `price_at_order` decimal(12,2) DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`detail_id`, `order_id`, `product_id`, `quantity`, `price_at_order`, `subtotal`) VALUES
(1, 1, 4, 1, 10000.00, 10000.00),
(2, 1, 2, 1, 20000.00, 20000.00),
(3, 2, 1, 1, 30000.00, 30000.00),
(4, 3, 1, 1, 30000.00, 30000.00),
(5, 4, 7, 1, 12000.00, 12000.00),
(6, 4, 2, 1, 20000.00, 20000.00),
(7, 4, 1, 1, 30000.00, 30000.00),
(8, 5, 2, 2, 20000.00, 40000.00),
(9, 7, 3, 2, 12000.00, 24000.00),
(10, 8, 1, 1, 30000.00, 30000.00);

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(12,2) NOT NULL,
  `stock` int(11) DEFAULT 0,
  `product_image` varchar(255) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `product_name`, `description`, `price`, `stock`, `product_image`, `category_id`) VALUES
(1, 'Apple Fuji', 'Fresh Fuji apples imported from Japan', 30000.00, 93, 'https://i.pinimg.com/1200x/9f/48/90/9f48905995a8a3320f4cac90368fc4a1.jpg', 1),
(2, 'Banana Cavendish', 'Sweet Cavendish bananas per kg', 20000.00, 116, 'https://i.pinimg.com/736x/42/14/73/421473ceb7d77d9f36be61c8b77bc23f.jpg', 1),
(3, 'Broccoli', 'Organic green broccoli per 500g', 12000.00, 78, 'https://i.pinimg.com/1200x/91/c2/d5/91c2d5cb35f8db6bd9ab3cadcb2e65a3.jpg', 2),
(4, 'Carrot', 'Local fresh carrots per kg', 10000.00, 89, 'https://i.pinimg.com/1200x/62/2d/3d/622d3d6a9254162fe71459bdd26b016f.jpg', 2),
(5, 'Apple Juice', '600ML of Apple Juice', 22000.00, 60, 'https://i.pinimg.com/736x/ab/22/98/ab22987c0024f19f2c79b3fbf8fd27f9.jpg', 3),
(6, 'Soft Dried Dragon Fruit', 'Hand Selected Dragon Fruit', 20000.00, 150, 'https://i.pinimg.com/736x/33/19/18/331918d332a80266587e3267fb36bfd3.jpg', 4),
(7, 'Fried Chicken', 'Very Crunchy and Juicy', 12000.00, 90, 'https://i.pinimg.com/1200x/1b/c3/c1/1bc3c1feb503ba69895c5eaedcebbcfd.jpg', 5);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `role` enum('admin','customer') DEFAULT 'customer'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `email`, `full_name`, `password`, `phone_number`, `birth_date`, `age`, `address`, `role`) VALUES
(1, 'deleonwira', 'deleonwira@gmail.com', NULL, '123', '081234567890', NULL, NULL, NULL, 'customer'),
(2, 'fathur', 'fathur@gmail.com', NULL, '123', '082198765432', NULL, NULL, NULL, 'customer'),
(3, 'Admin', 'admin@gmail.com', NULL, '123', '080000000000', NULL, NULL, NULL, 'customer'),
(4, 'jamaludin', 'jamaludin@gmail.com', NULL, '$2y$10$q3NRUUPs/4mKX7Op/6wUPe1/1V3fjZ/bkTt1qtbLg8t/4zr1S2fF6', '0182973123', NULL, NULL, NULL, 'customer'),
(5, 'eren', 'eren@gmail.com', NULL, '$2y$10$462ssbPOW83ustV/m/n2e.hpE71HamBoq8eBeIFZzI2PlvPK3YO7S', '10293123', NULL, NULL, NULL, 'customer'),
(7, 'johndoe2025', 'john.doe@example.com', 'John Doe', '$2a$10$vDvz7bfItBOUqULorVFNguz0crZa6xqUZEDLMje1Q3n0GF52/S.by', '082299887766', '1990-01-15', 35, 'Jl. Sudirman No. 456, Jakarta Selatan', 'customer'),
(8, 'admintest', 'admin@test.com', 'Admin Test User', '$2a$10$H5DnSy6ZHHfbKVyyGtcWoORjA3Hi2iGWQVVHOVxOHtWKgp4P9cGHq', '085512345678', '1985-03-20', 40, 'Jl. Admin Street No. 1, Jakarta', 'customer');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cart_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD UNIQUE KEY `invoice_code` (`invoice_code`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`detail_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `unique_email` (`email`),
  ADD UNIQUE KEY `unique_phone` (`phone_number`),
  ADD KEY `idx_users_full_name` (`full_name`),
  ADD KEY `idx_users_phone` (`phone_number`),
  ADD KEY `idx_users_email` (`email`),
  ADD KEY `idx_users_username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cart`
--
ALTER TABLE `cart`
  MODIFY `cart_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  ADD CONSTRAINT `cart_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
