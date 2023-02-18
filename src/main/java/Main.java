import java.util.*;
import java.util.List;
import java.util.concurrent.*;
public class Main {
    public static final int capacity = 100;
    public static ArrayBlockingQueue<String> arrayBlockingQueueForA = new ArrayBlockingQueue<>(capacity);
    public static ArrayBlockingQueue<String> arrayBlockingQueueForB = new ArrayBlockingQueue<>(capacity);
    public static ArrayBlockingQueue<String> arrayBlockingQueueForC = new ArrayBlockingQueue<>(capacity);
    public static String generateText (String letters,int length){
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
    public static int maxCount(char symbol, BlockingQueue<String> blockingQueue) throws ExecutionException, InterruptedException {
        int max = 0;
        int count = 0;
        String text;
        for (int i = 0; i < 10000; i++) {
            text = blockingQueue.take();
            for (char c : text.toCharArray()) {
                if (c == symbol) count++;
            }
            if (count > max) max = count;
            count = 0;
        }
        return max;
    };

    public static void main(String[] args) throws InterruptedException {

        Thread textGenerator = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    arrayBlockingQueueForA.put(text);
                    arrayBlockingQueueForB.put(text);
                    arrayBlockingQueueForC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        textGenerator.start();

        Thread aCount = new Thread(() -> {
            try {
                int maxCountA = maxCount('a', arrayBlockingQueueForA);
                System.out.println("Max count of 'A' in all texts -> " + maxCountA);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        aCount.start();

        Thread bCount = new Thread(() -> {
            try {
                int maxCountB = maxCount('b', arrayBlockingQueueForB);
                System.out.println("Max count of 'B' in all texts -> " + maxCountB);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        bCount.start();

        Thread cCount = new Thread(() -> {
            try {
                int maxCountC = maxCount('c', arrayBlockingQueueForC);
                System.out.println("Max count of 'C' in all texts -> " + maxCountC);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
        cCount.start();

        aCount.join();
        bCount.join();
        cCount.join();
    }
}
