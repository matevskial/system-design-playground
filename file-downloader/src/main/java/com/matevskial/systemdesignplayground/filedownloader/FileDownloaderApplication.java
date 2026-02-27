package com.matevskial.systemdesignplayground.filedownloader;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.json.*;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class FileDownloaderApplication {

    private static final String DOWNLOAD_DIRECTORY = "downloaded";

    public static void main(String[] args) {
        String jsonFile = "data.json";
        Object json = null;
        try {
            JSONTokener j = new JSONTokener(new FileReader(jsonFile));
            json = j.nextValue();
        } catch (Exception e) {
            log.error("Could not read JSON file: {}. Exiting", jsonFile, e);
            System.exit(1);
        }

        List<UrlEntry> urls = new ArrayList<>();
        try {
            String[] path = new String[21];
            path[0] = "./";
            getUrlsRecursive(json, path, 1, urls, 0);
        } catch (Exception e) {
            log.error("Could not get urls from json data: {}. Exiting", e.getMessage(), e);
            System.exit(1);
        }

        System.out.println("Will download %s files".formatted(urls.size()));
        downloadFilesFromUrls(urls);
    }

    private static void downloadFilesFromUrls(List<UrlEntry> urls) {
        int invalidUrls = 0;
        int downloadedUrls = 0;
        int errors = 0;
        int skipped = 0;

        try {
            Files.createDirectories(Paths.get(DOWNLOAD_DIRECTORY));
        } catch (IOException e) {
            log.error("Could not create directory: {}. Exiting", DOWNLOAD_DIRECTORY, e);
            System.exit(1);
        }

        for (int i = 0; i < urls.size(); i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            String urlStr = urls.get(i).getUrl();
            URL url;
            try {
                url = new URL(urlStr);
            } catch (Exception e) {
                log.error("Found invalid url: {}", urlStr);
                invalidUrls++;
                continue;
            }

            String lastSegment = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            String fileName = DOWNLOAD_DIRECTORY + "/" + i + "-" + lastSegment;
            System.out.println("Downloading file to file name: " + fileName);
            try {
                if (Files.exists(Paths.get(fileName))) {
                    skipped++;
                    continue;
                }
                try(FileOutputStream fos = new FileOutputStream(fileName)) {
                    FileChannel fc = fos.getChannel();
                    fc.transferFrom(Channels.newChannel(url.openStream()), 0, Long.MAX_VALUE);
                }
            } catch (Exception e) {
                log.error("Could not download file to file name: {}", fileName, e);
                errors++;
                continue;
            }
            downloadedUrls++;
        }

        System.out.println("Downloaded urls: " + downloadedUrls);
        System.out.println("Invalid urls: " + invalidUrls);
        System.out.println("Errors: " + errors);
        System.out.println("Skipped: " + skipped);
    }

    private static void getUrlsRecursive(Object json, String[] path, int pathIndex, List<UrlEntry> urls, int level) {
        if (level >= 20) {
            throw new RuntimeException("Max recursive level reached");
        }
        if (json instanceof String str) {
            if ("url".equals(path[pathIndex - 1])) {
                urls.add(UrlEntry.builder().url(str).build());
            }
        } else if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            for (int i = 0; i < jsonArray.length(); i++) {
                path[pathIndex] = path[pathIndex - 1] + "[" + i + "]";
                getUrlsRecursive(jsonArray.get(i), path, pathIndex + 1, urls, level + 1);
                path[pathIndex] = null;
            }
        } else if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            for (String currentKey : jsonObject.keySet()) {
                path[pathIndex] = currentKey;
                getUrlsRecursive(jsonObject.get(currentKey), path, pathIndex + 1, urls, level + 1);
                path[pathIndex] = null;
            }
        }
    }

    @Value
    @Builder
    private static class UrlEntry {
        String url;

        public UrlEntry(String url) {
            if (url == null) {
                throw new IllegalArgumentException("url cannot be null");
            }
            try {
                new URI(url).toURL();
            } catch (Exception e) {
                throw new IllegalArgumentException("invalid url: %s".formatted(url));
            }
            this.url = url;
        }
    }
}
