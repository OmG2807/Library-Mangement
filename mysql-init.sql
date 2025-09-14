-- MySQL initialization script for Library Management System
-- This script creates the database schema and initial data

USE library_management;

-- Create authors table
CREATE TABLE IF NOT EXISTS authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nationality VARCHAR(100),
    biography TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author_name (name)
);

-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    published_year INT NOT NULL,
    availability_status ENUM('AVAILABLE', 'BORROWED', 'MAINTENANCE', 'LOST') DEFAULT 'AVAILABLE',
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE,
    INDEX idx_book_title (title),
    INDEX idx_book_isbn (isbn),
    INDEX idx_book_published_year (published_year),
    INDEX idx_book_availability_status (availability_status),
    INDEX idx_book_author_id (author_id),
    FULLTEXT idx_book_title_fulltext (title)
);

-- Insert sample authors
INSERT INTO authors (name, nationality, biography) VALUES
('J.K. Rowling', 'British', 'British author, best known for the Harry Potter fantasy series'),
('George Orwell', 'British', 'English novelist, essayist, journalist and critic'),
('Harper Lee', 'American', 'American novelist best known for To Kill a Mockingbird'),
('F. Scott Fitzgerald', 'American', 'American novelist and short story writer'),
('Jane Austen', 'British', 'English novelist known primarily for her six major novels'),
('Mark Twain', 'American', 'American writer, humorist, entrepreneur, publisher, and lecturer'),
('Charles Dickens', 'British', 'English writer and social critic'),
('Agatha Christie', 'British', 'English writer known for her detective novels');

-- Insert sample books
INSERT INTO books (title, isbn, published_year, availability_status, author_id) VALUES
('Harry Potter and the Philosopher\'s Stone', '978-0747532699', 1997, 'AVAILABLE', 1),
('Harry Potter and the Chamber of Secrets', '978-0747538493', 1998, 'AVAILABLE', 1),
('1984', '978-0451524935', 1949, 'AVAILABLE', 2),
('Animal Farm', '978-0451526342', 1945, 'AVAILABLE', 2),
('To Kill a Mockingbird', '978-0061120084', 1960, 'AVAILABLE', 3),
('The Great Gatsby', '978-0743273565', 1925, 'AVAILABLE', 4),
('Pride and Prejudice', '978-0141439518', 1813, 'AVAILABLE', 5),
('Emma', '978-0141439587', 1815, 'AVAILABLE', 5),
('The Adventures of Tom Sawyer', '978-0486400778', 1876, 'AVAILABLE', 6),
('Adventures of Huckleberry Finn', '978-0486280615', 1884, 'AVAILABLE', 6),
('A Tale of Two Cities', '978-0486406510', 1859, 'AVAILABLE', 7),
('Great Expectations', '978-0486415864', 1861, 'AVAILABLE', 7),
('Murder on the Orient Express', '978-0062073495', 1934, 'AVAILABLE', 8),
('Death on the Nile', '978-0062073501', 1937, 'AVAILABLE', 8);

-- Create indexes for better performance
CREATE INDEX idx_books_created_at ON books(created_at);
CREATE INDEX idx_authors_created_at ON authors(created_at);

-- Print success message
SELECT 'Library Management System database initialized successfully!' as message;