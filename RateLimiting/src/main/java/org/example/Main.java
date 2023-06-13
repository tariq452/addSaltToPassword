package org.example;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");
        boolean result;
        boolean result1;
        RateLimiter rateLimiter = new RateLimiter(50,1000);
        for (int i=0;i<15;i++){
            Thread.sleep(10);
            result= rateLimiter.allowRequest("ttami","login");
            Thread.sleep(100);
            result1= rateLimiter.allowRequest("tt","login");
            System.out.println(result);
        }


    }
}