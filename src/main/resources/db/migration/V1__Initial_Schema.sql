CREATE TABLE audiences (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    publisher VARCHAR(255),
    publication_year INT,
    page_count INT,
    language VARCHAR(255),
    format VARCHAR(255),
    description TEXT,
    cover_image_url VARCHAR(255),
    is_age_restricted BOOLEAN NOT NULL DEFAULT FALSE,
    audience_id INT,
    FOREIGN KEY (audience_id) REFERENCES audiences(id)
);

CREATE TABLE book_categories (
    book_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (book_id, category_id),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE book_copies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    `condition` VARCHAR(20) NOT NULL,
    acquisition_date DATE,
    price DECIMAL(10, 2),
    notes TEXT,
    book_id INT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id)
);

CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(255),
    registration_date DATE NOT NULL,
    monthly_borrows INT NOT NULL DEFAULT 0,
    lifetime_borrows INT NOT NULL DEFAULT 0,
    name VARCHAR(255) NOT NULL,
    id_card_number VARCHAR(255),
    phone_number VARCHAR(255),
    status VARCHAR(255) NOT NULL
);

CREATE TABLE system_accounts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE borrowing_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT NOT NULL,
    book_copy_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(255) NOT NULL,
    renewal_count INT NOT NULL DEFAULT 0,
    fine_amount DECIMAL(10, 2),
    notes TEXT,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (book_copy_id) REFERENCES book_copies(id)
);