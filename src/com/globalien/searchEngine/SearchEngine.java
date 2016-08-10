package com.globalien.searchEngine;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by ricardodeandrade on 8/9/16.
 */
public class SearchEngine {

    private static String helpMsg = "-s or --search (required): Text to search (Ex: -s Java).\n" +
            "-r or --results (optional): Number of results to show (how many URLs to show). Defaults to 10.\n" +
            "-i or --initial (optional): Initial URL to crawl. Should be a valid Wikipedia URL. Defaults to https://en.wikipedia.org/wiki/Java_(programming_language).\n" +
            "-d or --depth (optional): Number of URLs to crawl from the initial URL. Defaults to 0.\n" +
            "-c or --clear (optional, no arguments needed): Clears the index.\n" +
            "-h or --help (optional): Displays help.\n";

    public static void main(String[] args) throws IOException {
//        args = Stream.of("-s", "java").toArray(size -> new String[size]);
        try {
            new SearchEngine().run(args);
        } catch (Exception e) {
            System.out.println(helpMsg);
            System.out.println("An exception occurred: " + e.toString());
        }
    }

    private void run(String[] args) throws IOException {
        OptionParser parser = createParser();
        OptionSet options = parser.parse(args);
        if (options.has("help")) {
            System.out.println(helpMsg);
            return;
        }
        String url = options.valueOf("initial").toString();
        if (!url.contains("https://en.wikipedia.org/wiki/")) {
            throw new IllegalArgumentException("URL must be a valid Wikipedia link.");
        }

        Jedis jedis = JedisMaker.make();
        JedisIndex index = new JedisIndex(jedis);

        if (options.has("clear")) {
            index.deleteAllKeys();
        }

        WikiCrawler wc = new WikiCrawler(url, index);
        int depth = (int) options.valueOf("depth");
        for (int i = 0; i < depth; i++) {
            wc.crawl();
        }

        String search = options.valueOf("search").toString();
        int results = (int) options.valueOf("results");

        List<Map.Entry<String, Integer>> wikiSearch = WikiSearch.search(search, index).sort();

        if (wikiSearch.isEmpty()) {
            System.out.println("No results found. Try increasing the depth (-d or --depth) to get more results.");
            return;
        }

        System.out.println("\n\nResults:\n");

        int num = 0;
        for (Map.Entry<String, Integer> entry : wikiSearch) {
            System.out.println(++num + ". " + entry.getKey());
            if (num >= results) {
                return;
            }
        }
    }

    private OptionParser createParser() {
        OptionParser parser = new OptionParser();
        parser.accepts("search").withRequiredArg().required();
        parser.accepts("results").withRequiredArg().ofType(Integer.class).defaultsTo(10);
        parser.accepts("initial").withRequiredArg().defaultsTo("https://en.wikipedia.org/wiki/Java_(programming_language)");
        parser.accepts("depth").withRequiredArg().ofType(Integer.class).defaultsTo(0);
        parser.accepts("clear");
        parser.accepts("help").forHelp();
        return parser;
    }


}
