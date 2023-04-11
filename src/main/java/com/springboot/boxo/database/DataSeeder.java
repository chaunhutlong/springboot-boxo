//package com.springboot.boxo.database;
//
//import jakarta.persistence.EntityManager;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataSeeder implements CommandLineRunner {
//    private EntityManager entityManager;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // only run this if the database is empty
//        if (entityManager.createQuery("SELECT u FROM User u").getResultList().isEmpty()) {
//            // seed the database
//
//        }
//    }
//}