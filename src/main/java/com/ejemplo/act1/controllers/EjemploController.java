package com.ejemplo.act1.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 
public class EjemploController {

    @GetMapping("/detalles_info")
    public String info(Model model){
        model.addAttribute("titulo", "servidor en linea");
        model.addAttribute("Servidor","Informaticonfig");
        model.addAttribute("IP","10.20.0.2");

        return "detalles_info";
    }
}
