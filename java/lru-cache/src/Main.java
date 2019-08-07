public class Main {

    public static void main(String[] args) {
        LRUCache<String, String> cache = new LRUCache<String, String>(3);
        cache.put("1", "one");
        cache.put("2", "two");
        cache.put("3", "three");
        cache.put("4", "four");
        cache.put("5", "five");
        cache.put("4", "second four");
        cache.put("1", "second one");
        cache.put("2", "second two");

        if (cache.size() != 3) {
            System.out.println("ERROR size: " + cache.size());
        }

        System.out.println(cache.get("2"));
        System.out.println(cache.get("4"));
        System.out.println(cache.values());

        System.out.println("Hello World!");
    }
}
