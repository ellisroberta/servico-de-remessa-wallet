package com.example.servicoderemessawallet.controller;

import com.example.servicoderemessawallet.dto.WalletDTO;
import com.example.servicoderemessawallet.mapper.WalletMapper;
import com.example.servicoderemessawallet.model.Wallet;
import com.example.servicoderemessawallet.repository.WalletRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
@Api(tags = "Carteiras")
public class WalletController {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletController(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @ApiOperation(value = "Obter carteira por ID do usuário", notes = "Retorna a carteira associada a um ID de usuário específico.")
    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWalletByUserId(@PathVariable UUID userId) {
        return walletRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Obter todas as carteiras", notes = "Retorna uma lista de todas as carteiras.")
    @GetMapping
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    @ApiOperation(value = "Criar nova carteira", notes = "Cria uma nova carteira com os dados fornecidos.")
    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody WalletDTO walletDTO) {
        Wallet wallet = walletMapper.toEntity(walletDTO);
        Wallet newWallet = walletRepository.save(wallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
    }

    @ApiOperation(value = "Atualizar carteira", notes = "Atualiza os detalhes de uma carteira existente.")
    @PutMapping("/{id}")
    public ResponseEntity<Wallet> updateWallet(@PathVariable UUID id, @RequestBody WalletDTO updatedWalletDTO) {
        return walletRepository.findById(id)
                .map(existingWallet -> {
                    existingWallet.setBalanceBrl(updatedWalletDTO.getBalanceBrl());
                    existingWallet.setBalanceUsd(updatedWalletDTO.getBalanceUsd());
                    Wallet savedWallet = walletRepository.save(existingWallet);
                    return ResponseEntity.ok().body(savedWallet);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Deletar carteira", notes = "Deleta uma carteira pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable UUID id) {
        // Deleta uma Wallet pelo ID
        walletRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
