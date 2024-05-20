package com.example.servicoderemessawallet.controller;

import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/servico-de-remessa/wallets")
@Api(tags = "Carteiras", description = "Endpoints para operações relacionadas às carteiras dos usuários")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // Endpoint para encontrar uma carteira por ID de usuário
    @ApiOperation(value = "Obter informações de uma carteira",
            notes = "Retorna informações detalhadas de uma carteira baseado no ID do usuário.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Wallet> findWalletByUserId(@PathVariable UUID userId) {
        Wallet wallet = walletService.findWalletByUserId(userId);
        if (wallet != null) {
            return ResponseEntity.ok(wallet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para criar uma nova carteira para um usuário
    @ApiOperation(value = "Criar uma nova carteira",
            notes = "Cria uma nova carteira para um usuário com base no ID do usuário.")
    @PostMapping("/{userId}")
    public ResponseEntity<Wallet> createWallet(@PathVariable UUID userId) {
        try {
            Wallet createdWallet = walletService.createWallet(userId);
            return ResponseEntity.ok(createdWallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // ou qualquer outra resposta apropriada para tratamento de erro
        }
    }

    // Outros endpoints conforme necessário para CRUD de Wallets
}
