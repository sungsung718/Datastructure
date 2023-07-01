import java.util.Iterator;
import java.util.NoSuchElementException;

public class MovieDB {
	private MyLinkedList<Genre> genreList;
    public MovieDB() {
        // FIXME implement this
		genreList = new MyLinkedList<>();
    }

    public void insert(MovieDBItem item) {
        // FIXME implement this
        // Insert the given item to the MovieDB.
		boolean done = false;

		String itemGenre = item.getGenre();
		String itemTitle = item.getTitle();

		Node<Genre> prev = genreList.head;
		Node<Genre> curr = prev.getNext();

		while (curr != null) {
			Genre currGenre = curr.getItem();
			if (currGenre.toString().equals(itemGenre)) {
				currGenre.getMovieList().add(itemTitle);
				done = true;
				break;
			}
			else if (currGenre.toString().compareTo(itemGenre) > 0) {
				prev.insertNext(new Genre(itemGenre, itemTitle));
				genreList.numItems++;
				done = true;
				break;
			}

			prev = curr;
			curr = curr.getNext();
		}

		if (!done) {
			prev.insertNext(new Genre(itemGenre, itemTitle));
			genreList.numItems++;
		}

    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        // System.err.printf("[trace] MovieDB: INSERT [%s] [%s]\n", item.getGenre(), item.getTitle());
    }

    public void delete(MovieDBItem item) {
        // FIXME implement this
        // Remove the given item from the MovieDB.
		String itemGenre = item.getGenre();
		String itemTitle = item.getTitle();

		Node<Genre> prev = genreList.head;
		Node<Genre> curr = prev.getNext();

		while (curr != null) {
			Genre currGenre = curr.getItem();
			if (currGenre.toString().equals(itemGenre)) {
				currGenre.getMovieList().delete(itemTitle);
				if (currGenre.getMovieList().isEmpty()) {
					prev.removeNext();
					genreList.numItems--;
				}
				break;
			}
			else if (currGenre.toString().compareTo(itemGenre) > 0) {
				break;
			}
			prev = curr;
			curr = curr.getNext();
		}
    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        // System.err.printf("[trace] MovieDB: DELETE [%s] [%s]\n", item.getGenre(), item.getTitle());
    }

    public MyLinkedList<MovieDBItem> search(String term) {
        // FIXME implement this
        // Search the given term from the MovieDB.
        // You should return a linked list of MovieDBItem.
        // The search command is handled at SearchCmd class.
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		for (Genre genre: genreList) {
			MovieList movieList = genre.getMovieList();
			for (String movie: movieList) {
				if (movie.contains(term)) {
					results.add(new MovieDBItem(genre.toString(), movie));
				}
			}
		}
    	
    	// Printing search results is the responsibility of SearchCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.
    	
        // This tracing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
    	// System.err.printf("[trace] MovieDB: SEARCH [%s]\n", term);
    	
    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.   


        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
        // FIXME implement this
        // Search the given term from the MovieDatabase.
        // You should return a linked list of QueryResult.
        // The print command is handled at PrintCmd class.
		MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		for (Genre genre: genreList) {
			MovieList movieList = genre.getMovieList();
			for (String movie: movieList) {
				results.add(new MovieDBItem(genre.toString(), movie));
			}
		}

    	// Printing movie items is the responsibility of PrintCmd class. 
    	// So you must not use System.out in this method to achieve specs of the assignment.

    	// Printing functionality is provided for the sake of debugging.
        // This code should be removed before submitting your work.
        // System.err.printf("[trace] MovieDB: ITEMS\n");

    	// FIXME remove this code and return an appropriate MyLinkedList<MovieDBItem> instance.
    	// This code is supplied for avoiding compilation error.
        
    	return results;
    }
}

class Genre extends Node<String> implements Comparable<Genre> {
	private MovieList movieList;
	public Genre(String name) {
		super(name);
		movieList = new MovieList();
	}

	public Genre(String name, String movie) {
		super(name);
		movieList = new MovieList();
		movieList.add(movie);
	}

	public MovieList getMovieList() {
		return movieList;
	}

	@Override
	public int compareTo(Genre o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
		return this.getItem().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Genre) {
			return this.getItem().equals(((Genre) obj).getItem());
		}
		return false;
	}

	public String toString() { return this.getItem();}
}
class MovieList implements ListInterface<String> {
	MyLinkedList<String> movieList;
	public MovieList() {
		movieList = new MyLinkedList<>();
	}

	@Override
	public Iterator<String> iterator() {
		return movieList.iterator();
	}

	@Override
	public boolean isEmpty() {
		return movieList.isEmpty();
	}

	@Override
	public int size() {
		return movieList.size();
	}

	@Override
	public void add(String item) {
		boolean ifExists = false;
		Node<String> prev = movieList.head;
		Node<String> curr = prev.getNext();

		while (curr != null) {
			if (curr.getItem().equals(item)) {
				ifExists = true;
				break;
			}
			else if (curr.getItem().compareTo(item) > 0) {
				prev.insertNext(item);
				break;
			}
			prev = curr;
			curr = curr.getNext();
		}

		if (!ifExists) {
			if (curr == null) {
				prev.insertNext(item);
			}
			movieList.numItems++;
		}

	}

	public void delete(String item) {
		Node<String> prev = movieList.head;
		Node<String> curr = prev.getNext();

		while (curr != null) {
			if (curr.getItem().equals(item)) {
				prev.removeNext();
				movieList.numItems--;
				break;
			}
			else if (curr.getItem().compareTo(item) > 0) {
				break;
			}
			prev = curr;
			curr = curr.getNext();
		}

	}

	@Override
	public String first() { return movieList.first(); }

	@Override
	public void removeAll() {
		movieList.removeAll();
	}
}