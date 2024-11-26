-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 26, 2024 at 04:18 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `zombiedoomdays`
--

-- --------------------------------------------------------

--
-- Table structure for table `game_scores`
--

CREATE TABLE `game_scores` (
  `id` int(11) NOT NULL,
  `player_name` varchar(100) NOT NULL,
  `score` int(11) NOT NULL,
  `play_time` int(11) NOT NULL,
  `play_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_scores`
--

INSERT INTO `game_scores` (`id`, `player_name`, `score`, `play_time`, `play_date`) VALUES
(22, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(23, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(24, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(25, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(26, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(27, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(28, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(29, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(30, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(31, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(32, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(33, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(34, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(35, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(36, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(37, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(38, 'phuong', 14, 44, '2024-11-26 12:38:16'),
(39, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(40, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(41, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(42, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(43, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(44, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(45, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(46, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(47, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(48, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(49, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(50, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(51, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(52, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(53, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(54, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(55, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(56, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(57, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(58, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(59, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(60, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(61, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(62, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(63, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(64, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(65, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(66, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(67, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(68, 'phuong', 14, 44, '2024-11-26 12:38:17'),
(69, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(70, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(71, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(72, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(73, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(74, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(75, 'phuong', 14, 44, '2024-11-26 12:38:18'),
(76, 'phuong', 0, 44, '2024-11-26 12:38:18'),
(77, 'khó ', 15, 38, '2024-11-26 12:42:36'),
(78, 'vuphuong ', 6, 22, '2024-11-26 12:54:24'),
(79, 'phuong2', 10, 34, '2024-11-26 13:03:05'),
(80, 'tesst', 25, 78, '2024-11-26 14:11:23'),
(81, 'dgfg', 2, 26, '2024-11-26 14:39:12'),
(82, 'khoa cu ngan', 16, 48, '2024-11-26 14:41:18'),
(83, 'dfgf', 6, 23, '2024-11-26 15:11:42');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `game_scores`
--
ALTER TABLE `game_scores`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `game_scores`
--
ALTER TABLE `game_scores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=84;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
