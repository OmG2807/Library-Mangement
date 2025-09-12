package com.library.config;

import com.library.model.Author;
import com.library.model.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Data initializer to populate dynamic sample data on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final Random random = new Random();
    
    @Value("${library.data.initialize-sample-data:true}")
    private boolean initializeSampleData;
    
    // Dynamic data arrays
    private static final List<String> FIRST_NAMES = Arrays.asList(
        "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Jessica", 
        "William", "Ashley", "James", "Amanda", "Christopher", "Jennifer", "Daniel", 
        "Lisa", "Matthew", "Nancy", "Anthony", "Karen", "Mark", "Betty", "Donald", 
        "Helen", "Steven", "Sandra", "Paul", "Donna", "Andrew", "Carol", "Joshua", 
        "Ruth", "Kenneth", "Sharon", "Kevin", "Michelle", "Brian", "Laura", "George", 
        "Sarah", "Edward", "Kimberly", "Ronald", "Deborah", "Timothy", "Dorothy"
    );
    
    private static final List<String> LAST_NAMES = Arrays.asList(
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", 
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", 
        "Thomas", "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", 
        "White", "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", 
        "Young", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores"
    );
    
    private static final List<String> NATIONALITIES = Arrays.asList(
        "American", "British", "Canadian", "Australian", "French", "German", "Italian", 
        "Spanish", "Japanese", "Chinese", "Indian", "Brazilian", "Mexican", "Russian", 
        "Swedish", "Norwegian", "Dutch", "Belgian", "Swiss", "Austrian", "Polish", 
        "Czech", "Hungarian", "Romanian", "Bulgarian", "Greek", "Turkish", "Israeli", 
        "South African", "Egyptian", "Nigerian", "Kenyan", "Moroccan", "Argentine", 
        "Chilean", "Peruvian", "Colombian", "Venezuelan", "Uruguayan", "Ecuadorian"
    );
    
    private static final List<String> BOOK_TITLES = Arrays.asList(
        "The Silent Echo", "Whispers in the Dark", "Beyond the Horizon", "The Last Stand", 
        "Eternal Dreams", "Shadows of Tomorrow", "The Forgotten Path", "Rising Phoenix", 
        "The Hidden Truth", "Winds of Change", "The Final Chapter", "Dawn of Destiny", 
        "The Lost Kingdom", "Echoes of Time", "The Golden Compass", "The Silver Sword", 
        "The Crimson Rose", "The Emerald Forest", "The Midnight Sun", "The Twilight Hour", 
        "The Ancient Wisdom", "The Modern Age", "The Digital Revolution", "The Cosmic Dance", 
        "The Quantum Leap", "The Infinite Loop", "The Virtual Reality", "The Artificial Mind", 
        "The Natural Order", "The Human Condition", "The Social Network", "The Global Village", 
        "The Urban Legend", "The Rural Life", "The Mountain Peak", "The Ocean Deep", 
        "The Desert Storm", "The Arctic Circle", "The Tropical Paradise", "The Winter Solstice"
    );
    
    private static final List<String> BOOK_GENRES = Arrays.asList(
        "Fiction", "Non-Fiction", "Mystery", "Romance", "Science Fiction", "Fantasy", 
        "Thriller", "Horror", "Biography", "History", "Philosophy", "Psychology", 
        "Self-Help", "Business", "Technology", "Art", "Music", "Poetry", "Drama", 
        "Comedy", "Adventure", "Crime", "Western", "Young Adult", "Children's", 
        "Educational", "Reference", "Travel", "Cooking", "Health", "Sports", "Nature"
    );
    
    @Override
    public void run(String... args) throws Exception {
        if (!initializeSampleData) {
            log.info("Sample data initialization is disabled. Use REST APIs to add books and authors dynamically.");
            return;
        }
        
        log.info("Initializing dynamic sample data...");
        
        // Clear existing data
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        
        // Create dynamic authors (5-10 authors)
        int authorCount = random.nextInt(6) + 5; // 5-10 authors
        for (int i = 0; i < authorCount; i++) {
            Author author = createRandomAuthor();
            authorRepository.save(author);
        }
        
        // Create dynamic books (10-20 books)
        int bookCount = random.nextInt(11) + 10; // 10-20 books
        List<Author> authors = authorRepository.findAll();
        
        for (int i = 0; i < bookCount; i++) {
            Author randomAuthor = authors.get(random.nextInt(authors.size()));
            Book book = createRandomBook(randomAuthor);
            bookRepository.save(book);
        }
        
        log.info("Dynamic sample data initialized successfully!");
        log.info("Created {} authors and {} books", authorRepository.count(), bookRepository.count());
        log.info("You can now use REST APIs to add more books and authors dynamically.");
    }
    
    private Author createRandomAuthor() {
        String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
        String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
        String name = firstName + " " + lastName;
        String nationality = NATIONALITIES.get(random.nextInt(NATIONALITIES.size()));
        int birthYear = random.nextInt(80) + 1940; // Born between 1940-2020
        String biography = generateBiography(name, nationality, birthYear);
        
        return new Author(name, biography, birthYear, nationality);
    }
    
    private Book createRandomBook(Author author) {
        String title = BOOK_TITLES.get(random.nextInt(BOOK_TITLES.size()));
        String isbn = generateISBN();
        int publishedYear = random.nextInt(30) + 1990; // Published between 1990-2020
        Book.AvailabilityStatus status = random.nextBoolean() ? 
            Book.AvailabilityStatus.AVAILABLE : Book.AvailabilityStatus.BORROWED;
        
        return new Book(title, isbn, publishedYear, status, author);
    }
    
    private String generateBiography(String name, String nationality, int birthYear) {
        String genre = BOOK_GENRES.get(random.nextInt(BOOK_GENRES.size()));
        return String.format("%s is a %s author born in %d, known for their contributions to %s literature. " +
            "Their works have gained recognition in the literary world and continue to inspire readers globally.", 
            name, nationality, birthYear, genre);
    }
    
    private String generateISBN() {
        // Generate a random 13-digit ISBN
        StringBuilder isbn = new StringBuilder("978");
        for (int i = 0; i < 10; i++) {
            isbn.append(random.nextInt(10));
        }
        return isbn.toString();
    }
}
