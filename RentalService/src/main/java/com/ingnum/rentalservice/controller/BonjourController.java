package com.ingnum.rentalservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class BonjourController {

    // On récupère l'URL du service PHP depuis le fichier de configuration 
    @Value("${customer.service.url}")
    private String customerServiceUrl;

    @GetMapping("/bonjour")
    public String bonjour() {
        return "bonjour";
    }

    // Nouvelle méthode demandée par la consigne pour tester la communication 
    @GetMapping("/customer/{name}")
    public String getCustomer(@PathVariable String name) {
        RestTemplate restTemplate = new RestTemplate();
        
        // Le service Java appelle le service PHP via HTTP [cite: 42, 45]
        String responsePHP = restTemplate.getForObject(customerServiceUrl, String.class);
        
        return "Bonjour " + name + ", la réponse du service PHP est : " + responsePHP;
    }
}