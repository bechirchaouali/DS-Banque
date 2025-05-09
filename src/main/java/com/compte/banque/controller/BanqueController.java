package com.compte.banque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.compte.banque.model.Compte;
import com.compte.banque.repository.CompteRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class BanqueController {

    @Autowired
    private CompteRepository compteRepository;

    @GetMapping("/comptes")
    public String listeComptes(Model model) {
        model.addAttribute("comptes", compteRepository.findAll());
        return "listeComptes";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("compte", new Compte());
        return "ajouterCompte";
    }

    @PostMapping("/ajouter")
    public String ajouterCompte(@ModelAttribute Compte compte) {
        if (compte.getSolde() >= 0) {
            compteRepository.save(compte);
        }
        return "redirect:/comptes";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable int id, Model model) {
        Compte compte = compteRepository.findById(id).orElse(null);
        model.addAttribute("compte", compte);
        return "detailsCompte";
    }

    @PostMapping("/depot/{id}")
    public String depot(@PathVariable int id, @RequestParam double montant) {
        Compte compte = compteRepository.findById(id).orElse(null);
        if (compte != null && montant > 0) {
            compte.setSolde(compte.getSolde() + montant);
            compteRepository.save(compte);
        }
        return "redirect:/details/" + id;
    }

    @PostMapping("/retrait/{id}")
    public String retrait(@PathVariable int id, @RequestParam double montant, Model model) {
        Compte compte = compteRepository.findById(id).orElse(null);
        if (compte != null) {
            if (montant > 0 && compte.getSolde() >= montant) {
                compte.setSolde(compte.getSolde() - montant);
                compteRepository.save(compte);
            } else {
                model.addAttribute("compte", compte);
                model.addAttribute("message", "Montant invalide ou solde insuffisant !");
                return "detailsCompte";
            }
        }
        return "redirect:/details/" + id;
    }
}
