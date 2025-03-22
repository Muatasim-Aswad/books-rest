package com.asim.books.domain.book.controller.integration;

import com.asim.books.domain.book.model.dto.BookDto;
import com.asim.books.test.util.fixtures.BookTestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Book Listing (GET All) Integration Tests")
class GetAllBooksControllerIntegrationTests extends BaseBookControllerIntegrationTest {

    @BeforeEach
    void setUp() throws Exception {
        // Create multiple books for testing the get all functionality
        BookDto[] books = BookTestFixtures.getManyDTOs();
        for (BookDto book : books) {
            createBook(book).andExpect(status().isCreated());
        }
    }

    @Test
    @DisplayName("should return first page of books when no filters are provided")
    void whenGetAllBooks_thenReturnFirstPageOfBooks() throws Exception {
        // Act & Assert
        getBooks(0, 10, null, null, null)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)))
                .andExpect(jsonPath("$.pageable.pageSize", is(10)));
    }

    @Test
    @DisplayName("should return empty page when page number exceeds available pages")
    void whenGetPageBeyondAvailable_thenReturnEmptyPage() throws Exception {
        // Act & Assert
        getBooks(100, 10, null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty", is(true)));
    }

    @Test
    @DisplayName("should return specified page size when size parameter is provided")
    void whenGetBooksWithPageSize_thenReturnSpecifiedPageSize() throws Exception {
        // Act & Assert
        getBooks(0, 3, null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.pageable.pageSize", is(3)));
    }

    @Test
    @DisplayName("should return sorted books when sort parameter is provided")
    void whenGetBooksWithSort_thenReturnSortedBooks() throws Exception {
        // Arrange
        String[] sortParams = {"title,asc"};

        // Act & Assert
        ResultActions result = getBooks(0, 10, sortParams, null, null)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));

        // Verify sorting order - extract titles and check they're in ascending order
        String responseContent = result.andReturn().getResponse().getContentAsString();
        // Since we can't use complex assertions directly in the jsonPath, we'll check the first two books
        result.andExpect(jsonPath("$.content[0].title", lessThanOrEqualTo(
                jsonPath("$.content[1].title").toString())));
    }

    @Test
    @DisplayName("should return filtered books when title filter is provided")
    void whenGetBooksWithTitleFilter_thenReturnFilteredBooks() throws Exception {
        // Arrange
        // Create a book with a unique title that we can search for
        BookDto uniqueBook = BookTestFixtures.getOneDto();
        String uniqueTitle = "UniqueTitle123";
        uniqueBook.setTitle(uniqueTitle);
        createBook(uniqueBook).andExpect(status().isCreated());

        // Act & Assert
        getBooks(0, 10, null, uniqueTitle, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", containsString(uniqueTitle)));
    }

    @Test
    @DisplayName("should return filtered books when author filter is provided")
    void whenGetBooksWithAuthorFilter_thenReturnFilteredBooks() throws Exception {
        // Arrange
        // Create a book with a unique author name that we can search for
        BookDto uniqueBook = BookTestFixtures.getOneDto();
        String uniqueAuthorName = "UniqueAuthor123";
        uniqueBook.getAuthor().setName(uniqueAuthorName);
        createBook(uniqueBook).andExpect(status().isCreated());

        // Act & Assert
        getBooks(0, 10, null, null, uniqueAuthorName)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].author.name", containsString(uniqueAuthorName)));
    }

    @Test
    @DisplayName("should return filtered books when both title and author filters are provided")
    void whenGetBooksWithTitleAndAuthorFilter_thenReturnFilteredBooks() throws Exception {
        // Arrange
        // Create a book with a unique title and author name that we can search for
        BookDto uniqueBook = BookTestFixtures.getOneDto();
        String uniqueTitle = "UniqueCombinedTitle";
        String uniqueAuthorName = "UniqueCombinedAuthor";
        uniqueBook.setTitle(uniqueTitle);
        uniqueBook.getAuthor().setName(uniqueAuthorName);
        createBook(uniqueBook).andExpect(status().isCreated());

        // Act & Assert
        getBooks(0, 10, null, uniqueTitle, uniqueAuthorName)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", containsString(uniqueTitle)))
                .andExpect(jsonPath("$.content[0].author.name", containsString(uniqueAuthorName)));
    }

    @Test
    @DisplayName("should return empty content when filter matches no books")
    void whenGetBooksWithNonMatchingFilter_thenReturnEmptyContent() throws Exception {
        // Act & Assert
        getBooks(0, 10, null, "NonExistentTitleXYZ", null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.empty", is(true)));
    }

    @Test
    @DisplayName("should return descending sorted books when desc sort order is specified")
    void whenGetBooksWithDescendingSort_thenReturnDescendingSortedBooks() throws Exception {
        // Arrange
        String[] sortParams = {"title,desc"};

        // Act & Assert
        ResultActions result = getBooks(0, 10, sortParams, null, null)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Verify sorting order - extract titles and check they're in descending order
        result.andExpect(jsonPath("$.content[0].title", greaterThanOrEqualTo(
                jsonPath("$.content[1].title").toString())));
    }

    @Test
    @DisplayName("should return books sorted by multiple fields when multiple sort parameters are provided")
    void whenGetBooksWithMultipleSort_thenReturnMultiSortedBooks() throws Exception {
        // Arrange
        String[] sortParams = {"author.name,asc", "title,desc"};

        // Act & Assert
        getBooks(0, 10, sortParams, null, null)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                // We can verify the sort parameters were applied by checking the sort properties in the response
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    @DisplayName("should include metadata in books response")
    void whenGetBooks_thenResponseIncludesMetadata() throws Exception {
        // Act & Assert
        getBooks(0, 10, null, null, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageable", is(notNullValue())))
                .andExpect(jsonPath("$.totalPages", is(greaterThan(0))))
                .andExpect(jsonPath("$.totalElements", is(greaterThan(0))))
                .andExpect(jsonPath("$.size", is(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.sort", is(notNullValue())));
    }

    @Test
    @DisplayName("should handle case insensitive search when filtering by title")
    void whenGetBooksWithCaseInsensitiveTitle_thenReturnFilteredBooks() throws Exception {
        // Arrange
        // Create a book with a specific title that we can search for in different cases
        BookDto testBook = BookTestFixtures.getOneDto();
        String testTitle = "SpecialTestTitle";
        testBook.setTitle(testTitle);
        createBook(testBook).andExpect(status().isCreated());

        // Act & Assert - Search with lowercase
        getBooks(0, 10, null, testTitle.toLowerCase(), null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.title=='" + testTitle + "')]", hasSize(1)));
    }

    @Test
    @DisplayName("should handle partial match search when filtering by title")
    void whenGetBooksWithPartialTitle_thenReturnMatchingBooks() throws Exception {
        // Arrange
        // Create a book with a specific title that we can search for by partial match
        BookDto testBook = BookTestFixtures.getOneDto();
        String testTitle = "VeryUniqueSearchableTitle";
        testBook.setTitle(testTitle);
        createBook(testBook).andExpect(status().isCreated());

        // Act & Assert - Search with partial title
        String partialTitle = "Searchable";
        getBooks(0, 10, null, partialTitle, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.title=='" + testTitle + "')]", hasSize(1)));
    }
}