import java.util.*;
// Class to store data related to a book
class BookData {
    public boolean BorrowedStatus;
    public int BorrowedByUserID;
    public int ISBN;
    public String Publisher;
    public String Author;
    public MyDate DateOfReturn;

    public BookData(int isbn, String publisher, String author, MyDate dateOfReturn) {
        this.BorrowedStatus = false;
        this.BorrowedByUserID = -1;
        this.ISBN = isbn;
        this.Publisher = publisher;
        this.Author = author;
        this.DateOfReturn = dateOfReturn;
    }
}

// Class to represent the date in MM/YYYY format
class MyDate {
    public int month;
    public int year;

    public MyDate(int month, int year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("%02d/%d", month, year);
    }
}

// Generic node class for the linked list
class Node<T> {
    public T data;
    public Node<T> previous;
    public Node<T> next;

    public Node(T data) {
        this.data = data;
    }
}

// Class to store request data
class RequestData {
    public int ISBN;
    public int UserID;
    public MyDate RequestDate;

    public RequestData(int isbn, int userID, MyDate requestDate) {
        this.ISBN = isbn;
        this.UserID = userID;
        this.RequestDate = requestDate;
    }

    @Override
    public String toString() {
        return "ISBN: " + ISBN + ", UserID: " + UserID + ", RequestDate: " + RequestDate;
    }
}

// Class to store response data
class Response {
    public int WaitingTime;
    public boolean Available;
    public int PositionInQueue;

    public Response(int waitingTime, boolean available, int positionInQueue) {
        this.WaitingTime = waitingTime;
        this.Available = available;
        this.PositionInQueue = positionInQueue;
    }
}

// Class to store user data
class UserData {
    // public int UserId;
    // public String Name;
    // public String Address;
    // public int Age;
    public int UserId;
    public String Name;
    public String Address;
    public int Age;

    public UserData(int userId, String name, String address, int age) {
        this.UserId = userId;
        this.Name = name;
        this.Address = address;
        this.Age = age;
    }
}

// Class to represent a queue of requests
class RequestQueue {
    private Node<RequestData> front;
    private Node<RequestData> back;
    private int length;

    public RequestQueue() {
        this.front = null;
        this.back = null;
        this.length = 0;
    }
    public RequestData getFront() {
        return front != null ? front.data : null;
    }
    public int getLength() {
        return length;
    }
    public void push(int isbn, int userID, MyDate requestDate) {
        Node<RequestData> newRequest = new Node<>(new RequestData(isbn, userID, requestDate));
        if (back != null) {
            back.next = newRequest;
            newRequest.previous = back;
        }
        back = newRequest;
        if (front == null) {
            front = newRequest;
        }
        length++;
    }
    public void pop() {
        if (front != null) {
            front = front.next;
            if (front != null) {
                front.previous = null;
            } else {
                back = null;
            }
            length--;
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue Length: ").append(length).append("\n");
        Node<RequestData> current = front;
        while (current != null) {
            sb.append(current.data).append("\n");
            current = current.next;
        }
        return sb.toString();
    }
}

// Class to represent a linked list of pending requests
class PendingRequests {
    public int length;
    public Node<RequestData> front;
    public Node<RequestData> back;

    public PendingRequests() {
        this.length = 0;
        this.front = null;
        this.back = null;
    }

    public boolean insert(Node<RequestData> insnode) {
        if (back != null) {
            back.next = insnode;
            insnode.previous = back;
        }
        back = insnode;
        if (front == null) {
            front = insnode;
        }
        length++;
        return true;
    }

    public boolean delete(Node<RequestData> delnode) {
        if (delnode.previous != null) {
            delnode.previous.next = delnode.next;
        }
        if (delnode.next != null) {
            delnode.next.previous = delnode.previous;
        }
        if (delnode == front) {
            front = delnode.next;
        }
        if (delnode == back) {
            back = delnode.previous;
        }
        length--;
        return true;
    }

    public Node<RequestData> find(int isbn) {
        Node<RequestData> current = front;
        while (current != null) {
            if (current.data.ISBN == isbn) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Linked List Length: ").append(length).append("\n");
        Node<RequestData> current = front;
        while (current != null) {
            sb.append(current.data).append("\n");
            current = current.next;
        }
        return sb.toString();
    }
}

// Class to represent the library storage
class LibraryStorage {
    public HashMap<Integer, BookData> storage;
    public RequestQueue rqQueue;
    public PendingRequests prLinkedList;

    public LibraryStorage() {
        this.storage = new HashMap<>();
        this.rqQueue = new RequestQueue();
        this.prLinkedList = new PendingRequests();
    }

    public void push(int isbn, int userID, MyDate requestDate) {
        rqQueue.push(isbn, userID, requestDate);
    }

    public String rqQueueToString() {
        return rqQueue.toString();
    }

    public String prLinkedListToString() {
        return prLinkedList.toString();
    }

    public boolean processQueue() {
        if (rqQueue.getLength() == 0) {
            return false;
        }
        RequestData frontRequest = rqQueue.getFront();
        if (storage.containsKey(frontRequest.ISBN)) {
            BookData book = storage.get(frontRequest.ISBN);
            if (book.BorrowedStatus) {
                prLinkedList.insert(new Node<>(frontRequest));
                return false;
            } else {
                book.BorrowedStatus = true;
                book.BorrowedByUserID = frontRequest.UserID;
                book.DateOfReturn = new MyDate((frontRequest.RequestDate.month % 12) + 1, frontRequest.RequestDate.year + (frontRequest.RequestDate.month / 12));
                storage.put(frontRequest.ISBN, book);
                rqQueue.pop();
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean processReturn(BookData book) {
        Node<RequestData> pendingRequestNode = prLinkedList.find(book.ISBN);
        if (pendingRequestNode != null) {
            RequestData pendingRequest = pendingRequestNode.data;
            book.BorrowedStatus = true;
            book.BorrowedByUserID = pendingRequest.UserID;
            book.DateOfReturn = new MyDate((pendingRequest.RequestDate.month % 12) + 1, pendingRequest.RequestDate.year + (pendingRequest.RequestDate.month / 12));
            prLinkedList.delete(pendingRequestNode);
        } else {
            book.BorrowedStatus = false;
            book.BorrowedByUserID = -1;
        }
        storage.put(book.ISBN, book);
        return true;
    }
}

// Driver code for testing
// public class DriverCode {
//     public static void main(String[] args) {
//         // Initializing library storage
//         LibraryStorage library = new LibraryStorage();

//         library.storage.put(100, new BookData(100, "Publisher1", "George RR Martin", new MyDate(5, 2023)));
//         library.storage.put(101, new BookData(101, "Publisher2", "Holly Jackson", new MyDate(6, 2023)));
//         library.push(100, 1, new MyDate(7, 2023));
//         library.push(401, 2, new MyDate(7, 2023));
//         library.push(100, 3, new MyDate(7, 2023));

//         System.out.println("Processing Queue:");
//         while (library.processQueue()) {
//         }
//         System.out.println("Request Queue:"+library.rqQueueToString());
//         System.out.println("Pending Requests:"+library.prLinkedListToString());
//         System.out.println("Processing Return:");
//         BookData bookToReturn = library.storage.get(123);
//         library.processReturn(bookToReturn);
//         System.out.println("Request Queue:"+library.rqQueueToString());
//         System.out.println("Pending Requests:"+library.prLinkedListToString());
//     }
// }
